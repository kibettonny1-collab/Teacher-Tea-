package com.example.ai

import com.example.BuildConfig
import com.example.data.powerpoint.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

// Data classes for interactive activities
data class StandardTestItem(
    val id: Int,
    val questionEn: String,
    val questionTh: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class StandardTestResult(
    val title: String,
    val items: List<StandardTestItem>,
    val answerKeyText: String
)

data class FlashCardItem(
    val id: Int,
    val en: String,
    val th: String,
    val hint: String = "Tap to flip"
)

data class SpeakingListeningQuestion(
    val id: Int,
    val type: String, // "wh" | "yn"
    val questionEn: String,
    val questionTh: String,
    val suggestedAnswer: String
)

data class SentenceBuilderItem(
    val id: Int,
    val sentenceEn: String,
    val sentenceTh: String,
    val words: List<String>
)

data class WorksheetItem(
    val id: Int,
    val before: String,
    val answer: String,
    val after: String,
    val hintTh: String
)

data class StoryResult(
    val title: String,
    val storyText: String,
    val questions: List<Pair<String, String>> // questionEn -> questionTh
)

data class WordSearchResult(
    val words: List<String>,
    val size: Int,
    val grid: List<List<Char>>
)

data class MemoryMatchCard(
    val id: String,
    val pairId: Int,
    val text: String,
    val isEnglish: Boolean
)

data class WordBankEntry(
    val category: String,
    val en: String,
    val th: String,
    val example: String
)

data class ListeningScriptResult(
    val title: String,
    val script: String,
    val questions: List<String>,
    val answerKey: String
)

data class ConversationMessage(
    val id: String,
    val sender: String, // "ai" | "student"
    val textEn: String,
    val textTh: String? = null,
    val grammarTip: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class MatchUpItem(
    val id: Int,
    val term: String,
    val definition: String,
    val audioHint: String? = null
)

data class MatchUpResult(
    val title: String,
    val pairs: List<MatchUpItem>
)

data class QuizRaceItem(
    val id: Int,
    val question: String,
    val questionTh: String,
    val options: List<String>,
    val correctIndex: Int,
    val points: Int = 100,
    val timeLimitSeconds: Int = 15
)

data class QuizRaceResult(
    val title: String,
    val items: List<QuizRaceItem>
)

data class AnagramItem(
    val id: Int,
    val word: String,
    val scrambled: List<Char>,
    val hintTh: String,
    val definitionEn: String
)

data class AnagramResult(
    val title: String,
    val items: List<AnagramItem>
)

data class TrueFalseItem(
    val id: Int,
    val statementEn: String,
    val statementTh: String,
    val isTrue: Boolean,
    val explanation: String
)

data class TrueFalseResult(
    val title: String,
    val items: List<TrueFalseItem>
)

data class OpenBoxItem(
    val boxNumber: Int,
    val questionEn: String,
    val questionTh: String,
    val answer: String,
    val points: Int,
    val isOpened: Boolean = false
)

data class OpenBoxResult(
    val title: String,
    val boxes: List<OpenBoxItem>
)

object AIActivityGenerator {

    private fun hasValidApiKey(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY" && key != "YOUR_API_KEY"
    }

    private suspend fun queryGemini(prompt: String): String? = withContext(Dispatchers.IO) {
        if (!hasValidApiKey()) return@withContext null
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.4f,
                    maxOutputTokens = 2048
                )
            )
            val response = GeminiClient.apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 1. Standard Test
    suspend fun generateStandardTest(content: String, grade: String): StandardTestResult {
        val prompt = """
            You are an expert English teacher for Thai Secondary (Mattayom $grade) students.
            Create 5 multiple-choice questions based on: "$content".
            Return ONLY valid JSON format:
            {
              "title": "Short Test Title",
              "questions": [
                {
                  "questionEn": "Question in English",
                  "questionTh": "คำแปลภาษาไทยในวงเล็บ",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctIndex": 0,
                  "explanation": "Brief explanation in English and Thai"
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "English Standard Test ($grade)")
                val qArray = obj.getJSONArray("questions")
                val items = mutableListOf<StandardTestItem>()
                for (i in 0 until qArray.length()) {
                    val q = qArray.getJSONObject(i)
                    val opts = mutableListOf<String>()
                    val oArray = q.getJSONArray("options")
                    for (j in 0 until oArray.length()) {
                        opts.add(oArray.getString(j))
                    }
                    items.add(
                        StandardTestItem(
                            id = i + 1,
                            questionEn = q.getString("questionEn"),
                            questionTh = q.getString("questionTh"),
                            options = opts,
                            correctIndex = q.getInt("correctIndex"),
                            explanation = q.optString("explanation", "")
                        )
                    )
                }
                if (items.isNotEmpty()) {
                    val key = items.mapIndexed { idx, it -> "${idx + 1}. ${it.options.getOrNull(it.correctIndex) ?: ""} (${it.questionTh})" }.joinToString("\n")
                    return StandardTestResult(title, items, key)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback
        return getFallbackStandardTest(content, grade)
    }

    // 2. Gamified Quiz
    suspend fun generateGamifiedQuiz(content: String, grade: String): String {
        val prompt = """
            Create a gamified English quiz (points-based, with 100 base points per question, combo streak multiplier x2, and a fun Thai school theme) for Thai Mattayom $grade students based on: "$content".
            Provide 5 engaging questions with bilingual EN/Thai prompts, point rewards, and an answer key. Return plain text formatted nicely.
        """.trimIndent()
        val text = queryGemini(prompt)
        if (!text.isNullOrBlank()) return text.replace("```", "").trim()

        return """
            🎮 CLASS COMPANION QUEST: BATTLE OF WORDS (Grade $grade)
            Theme: English Master Challenger | Base Points: 100 XP per question | Streak Bonus: x2 Combo!

            ⭐ Question 1 (100 XP):
            Which word correctly completes: "Yesterday, Suda ______ to the night market with her family."
            A) go  B) went  C) gone  D) goes
            (คำใดเติมในประโยคได้ถูกต้อง: เมื่อวานนี้สุดาไปตลาดกลางคืนกับครอบครัว)

            ⭐ Question 2 (100 XP + Speed Bonus):
            What is the opposite of "Excited" (ตื่นเต้น)?
            A) Bored (เบื่อ)  B) Happy (มีความสุข)  C) Fast (เร็ว)  D) Hungry (หิว)

            ⭐ Question 3 (100 XP + Combo x2):
            Complete the sentence: "We always borrow interesting books from the school ______."
            A) Canteen  B) Playground  C) Library  D) Swimming pool
            (พวกเรายืมหนังสือที่น่าสนใจจาก...ของโรงเรียนเสมอ)

            ⭐ Question 4 (150 XP Boss Question):
            "Last weekend, we ______ delicious Som Tum at the street stall."
            A) eat  B) eating  C) ate  D) eaten
            (สุดสัปดาห์ที่แล้ว พวกเรากินส้มตำแสนอร่อยที่ร้านริมทาง)

            ⭐ Question 5 (200 XP Super Challenge):
            Translate to English: "เทศกาลสงกรานต์สนุกมาก"
            A) Songkran festival was very fun.
            B) Songkran festival is going fun.
            C) Songkran festival are fun.
            D) Songkran festival will fun.

            🏆 ANSWER KEY (เฉลย):
            1. B (went - อดีตของ go)
            2. A (Bored - ตรงข้ามกับ Excited)
            3. C (Library - ห้องสมุด)
            4. C (ate - อดีตของ eat)
            5. A (Songkran festival was very fun)
        """.trimIndent()
    }

    // 3. Flashcards
    suspend fun generateFlashcards(content: String, grade: String): List<FlashCardItem> {
        val prompt = """
            From this content: "$content" for Thai grade $grade students, extract exactly 8-10 high-value English vocabulary words with accurate Thai translations.
            Return ONLY valid JSON format:
            {"cards": [{"en": "English word", "th": "คำแปลภาษาไทย", "hint": "short hint"}]}
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val cArray = obj.getJSONArray("cards")
                val list = mutableListOf<FlashCardItem>()
                for (i in 0 until cArray.length()) {
                    val c = cArray.getJSONObject(i)
                    list.add(
                        FlashCardItem(
                            id = i + 1,
                            en = c.getString("en"),
                            th = c.getString("th"),
                            hint = c.optString("hint", "Tap to flip")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return getFallbackFlashcards(content)
    }

    // 4. Speaking & Listening Test
    suspend fun generateSpeakingListeningTest(topicOrContent: String, grade: String): List<SpeakingListeningQuestion> {
        val prompt = """
            Create an interactive speaking & listening oral exam for Thai grade $grade English students based on: "$topicOrContent".
            Generate exactly 6 questions: 3 WH-questions and 3 Yes/No questions.
            Return ONLY valid JSON format:
            {
              "questions": [
                {
                  "type": "wh",
                  "questionEn": "What time do you usually wake up in the morning?",
                  "questionTh": "คุณมักจะตื่นนอนกี่โมงในตอนเช้า?",
                  "suggestedAnswer": "I usually wake up at six o'clock."
                },
                {
                  "type": "yn",
                  "questionEn": "Do you like studying English with your friends?",
                  "questionTh": "คุณชอบเรียนภาษาอังกฤษกับเพื่อนๆ ไหม?",
                  "suggestedAnswer": "Yes, I do. / No, I don't."
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val qArray = obj.getJSONArray("questions")
                val list = mutableListOf<SpeakingListeningQuestion>()
                for (i in 0 until qArray.length()) {
                    val q = qArray.getJSONObject(i)
                    list.add(
                        SpeakingListeningQuestion(
                            id = i + 1,
                            type = q.optString("type", if (i % 2 == 0) "wh" else "yn"),
                            questionEn = q.getString("questionEn"),
                            questionTh = q.getString("questionTh"),
                            suggestedAnswer = q.optString("suggestedAnswer", "Sample response")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return getFallbackSpeakingQuestions(topicOrContent, grade)
    }

    // 5. Sentence Builder
    suspend fun generateSentenceBuilder(content: String, grade: String): List<SentenceBuilderItem> {
        val prompt = """
            Based on: "$content" for Thai grade $grade English students, generate exactly 4 natural English sentences.
            Return ONLY valid JSON format:
            {
              "sentences": [
                {
                  "sentenceEn": "She went to school by bus yesterday.",
                  "sentenceTh": "เธอไปโรงเรียนโดยรถบัสเมื่อวานนี้"
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val sArray = obj.getJSONArray("sentences")
                val list = mutableListOf<SentenceBuilderItem>()
                for (i in 0 until sArray.length()) {
                    val s = sArray.getJSONObject(i)
                    val en = s.getString("sentenceEn")
                    val th = s.getString("sentenceTh")
                    val cleanEn = en.replace(".", "").replace("!", "").replace("?", "").trim()
                    val words = cleanEn.split("\\s+".toRegex()).filter { it.isNotBlank() }.shuffled()
                    list.add(SentenceBuilderItem(i + 1, cleanEn, th, words))
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return listOf(
            SentenceBuilderItem(
                1,
                "I ride my bicycle to school every morning",
                "ฉันขี่จักรยานไปโรงเรียนทุกเช้า",
                listOf("to", "bicycle", "every", "morning", "ride", "school", "my", "I").shuffled()
            ),
            SentenceBuilderItem(
                2,
                "Pad Thai is delicious and popular in Thailand",
                "ผัดไทยอร่อยและเป็นที่นิยมในประเทศไทย",
                listOf("and", "in", "Pad", "is", "Thailand", "delicious", "Thai", "popular").shuffled()
            ),
            SentenceBuilderItem(
                3,
                "We read interesting English books in the library",
                "พวกเราอ่านหนังสือภาษาอังกฤษที่น่าสนใจในห้องสมุด",
                listOf("interesting", "in", "books", "library", "We", "the", "read", "English").shuffled()
            ),
            SentenceBuilderItem(
                4,
                "Yesterday my family ate dinner at a night market",
                "เมื่อวานนี้ครอบครัวของฉันกินมื้อเย็นที่ตลาดกลางคืน",
                listOf("family", "at", "dinner", "ate", "Yesterday", "a", "market", "night", "my").shuffled()
            )
        )
    }

    // 6. Live Worksheet
    suspend fun generateWorksheet(content: String, grade: String): List<WorksheetItem> {
        val prompt = """
            Create a fill-in-the-blank live worksheet for Thai grade $grade English students based on: "$content".
            Generate exactly 5 sentences, each with ONE missing keyword.
            Return ONLY valid JSON format:
            {
              "items": [
                {
                  "before": "Yesterday, Somchai",
                  "answer": "went",
                  "after": "to the convenience store.",
                  "hintTh": "ไป (อดีต)"
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val iArray = obj.getJSONArray("items")
                val list = mutableListOf<WorksheetItem>()
                for (i in 0 until iArray.length()) {
                    val it = iArray.getJSONObject(i)
                    list.add(
                        WorksheetItem(
                            id = i + 1,
                            before = it.getString("before"),
                            answer = it.getString("answer").trim(),
                            after = it.getString("after"),
                            hintTh = it.getString("hintTh")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return listOf(
            WorksheetItem(1, "Yesterday, Suda", "went", "to Bangkok with her teacher.", "ไป (อดีต)"),
            WorksheetItem(2, "We always borrow story books from the school", "library", "after lunch.", "ห้องสมุด"),
            WorksheetItem(3, "Tom Yum Goong is spicy and very", "delicious", "for dinner.", "อร่อย"),
            WorksheetItem(4, "Songkran is an important cultural", "festival", "in April.", "เทศกาล"),
            WorksheetItem(5, "I love to ride my new", "bicycle", "around the park.", "จักรยาน")
        )
    }

    // 7. Story
    suspend fun generateStory(content: String, grade: String): StoryResult {
        val prompt = """
            Write an engaging 130-160 word English story for Thai grade $grade students that naturally features: "$content".
            Follow with 3 comprehension questions in English with Thai translations in parentheses.
            Return in clean format:
            Title: [Story Title]
            [Story text]
            Questions:
            1. [Question 1] ([Thai translation 1])
            2. [Question 2] ([Thai translation 2])
            3. [Question 3] ([Thai translation 3])
        """.trimIndent()

        val text = queryGemini(prompt)
        if (!text.isNullOrBlank()) {
            return parseStoryText(text)
        }

        return StoryResult(
            title = "A Special Day at the Festival",
            storyText = "Last Friday, Anan and his classmate Nong Ploy visited the annual temple festival in Rayong. The weather was sunny and warm. They arrived by bicycle in the afternoon and saw colorful lights everywhere. First, they went to the food stalls where they ate delicious Pad Thai and mango sticky rice. Next, they visited the school exhibition booth and saw impressive science projects made by Mattayom students. Anan was very excited when he won a giant teddy bear at the balloon dart game. Before going home, they bought sweet milk tea for their teacher. It was an unforgettable day full of laughter and joy.",
            questions = listOf(
                "How did Anan and Nong Ploy travel to the temple festival?" to "อนันต์และน้องพลอยเดินทางไปงานวัดอย่างไร?",
                "What traditional food did they eat at the food stalls?" to "พวกเขาได้กินอาหารพื้นบ้านอะไรบ้างที่ซุ้มอาหาร?",
                "Why was Anan very excited at the festival?" to "ทำไมอนันต์ถึงตื่นเต้นมากในงานเทศกาล?"
            )
        )
    }

    // 8. Word Search
    suspend fun generateWordSearch(content: String, grade: String): WordSearchResult {
        val prompt = """
            Extract 8 single English vocabulary words (4-8 letters each, no spaces) from: "$content".
            Return ONLY JSON: {"words": ["WORD1", "WORD2", ...]}
        """.trimIndent()

        var words = listOf("BICYCLE", "LIBRARY", "DELICIOUS", "FESTIVAL", "EXCITED", "YESTERDAY", "TEACHER", "STUDENT")
        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val wArray = obj.getJSONArray("words")
                val parsed = mutableListOf<String>()
                for (i in 0 until wArray.length()) {
                    val w = wArray.getString(i).uppercase().replace("[^A-Z]".toRegex(), "")
                    if (w.length in 3..9) parsed.add(w)
                }
                if (parsed.size >= 4) words = parsed.take(8)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return buildWordSearchGrid(words, 10)
    }

    // 9. Memory Match
    suspend fun generateMemoryMatch(content: String, grade: String): List<MemoryMatchCard> {
        val cards = generateFlashcards(content, grade).take(6)
        val result = mutableListOf<MemoryMatchCard>()
        cards.forEachIndexed { idx, it ->
            result.add(MemoryMatchCard("card_${idx}_en", idx, it.en, true))
            result.add(MemoryMatchCard("card_${idx}_th", idx, it.th, false))
        }
        return result.shuffled()
    }

    // 10. Word Bank
    suspend fun generateWordBank(content: String, grade: String): List<WordBankEntry> {
        val prompt = """
            Create a categorized bilingual English/Thai word bank for Thai grade $grade students from: "$content".
            Return ONLY valid JSON format:
            {
              "words": [
                {
                  "category": "Verbs (คำกริยา)",
                  "en": "Travel",
                  "th": "ท่องเที่ยว/เดินทาง",
                  "example": "Many tourists travel to Thailand every winter."
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val wArray = obj.getJSONArray("words")
                val list = mutableListOf<WordBankEntry>()
                for (i in 0 until wArray.length()) {
                    val w = wArray.getJSONObject(i)
                    list.add(
                        WordBankEntry(
                            category = w.optString("category", "General Vocabulary"),
                            en = w.getString("en"),
                            th = w.getString("th"),
                            example = w.getString("example")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return listOf(
            WordBankEntry("Actions & Verbs (กริยา)", "Bicycle", "จักรยาน", "I ride my bicycle to school every morning."),
            WordBankEntry("Actions & Verbs (กริยา)", "Travel", "ท่องเที่ยว", "We travel to Chiang Mai during the holidays."),
            WordBankEntry("Places (สถานที่)", "Library", "ห้องสมุด", "Students read silently in the air-conditioned library."),
            WordBankEntry("Descriptive Adjectives (คำคุณศัพท์)", "Delicious", "อร่อย", "Tom Yum Goong has a rich and delicious flavor."),
            WordBankEntry("Descriptive Adjectives (คำคุณศัพท์)", "Excited", "ตื่นเต้น", "The class is excited for the English speech contest."),
            WordBankEntry("Time & Culture (เวลาและวัฒนธรรม)", "Festival", "เทศกาล", "Loy Krathong is a sparkling water festival in Thailand."),
            WordBankEntry("Time & Culture (เวลาและวัฒนธรรม)", "Yesterday", "เมื่อวานนี้", "Yesterday we practiced irregular past verbs.")
        )
    }

    // 11 & 12. Listening Scripts
    suspend fun generateListeningScript(content: String, grade: String, isVideo: Boolean): ListeningScriptResult {
        val kind = if (isVideo) "video clip script with stage actions" else "audio listening dialogue"
        val prompt = """
            Write a short 90-120 word $kind in English for Thai grade $grade students based on: "$content".
            Include 3 listening comprehension questions with bilingual EN/Thai answer keys.
            Return plain text formatted with Title, Script, Questions, and Answer Key.
        """.trimIndent()

        val text = queryGemini(prompt)
        if (!text.isNullOrBlank()) {
            return ListeningScriptResult(
                title = if (isVideo) "Video Scene: English in Action" else "Audio Dialogue: Daily Conversation",
                script = text,
                questions = listOf(
                    "1. Where does the dialogue take place? (บทสนทนาเกิดขึ้นที่ไหน?)",
                    "2. What activity did they do yesterday? (พวกเขาทำกิจกรรมอะไรเมื่อวานนี้?)",
                    "3. What are their plans for the weekend? (พวกเขามีแผนจะทำอะไรในวันหยุดสุดสัปดาห์?)"
                ),
                answerKey = "Refer to the script above for details."
            )
        }

        val fallbackScript = if (isVideo) """
            [SCENE START: Classroom hallway at Banchang Wittayakhom School]
            Nong Ploy: (Walking with backpack, smiling) Good morning Somchai! Did you finish the English homework?
            Somchai: (Holding notebook) Good morning Ploy! Yes, I wrote five sentences about my weekend trip.
            Nong Ploy: (Points to library) Great! Let's review our vocabulary in the library before teacher arrives.
            Somchai: (Nods happily) Sure! Let's go practice speaking together!
            [SCENE END]
        """.trimIndent() else """
            Teacher Lok: Good morning class! Today we are talking about your favorite hobbies. Anan, what do you like to do on Sundays?
            Anan: Good morning Ajarn! I love riding my bicycle to the park with my brother, and in the evening I help my mother cook dinner.
            Teacher Lok: Wonderful! What did you cook yesterday?
            Anan: We cooked spicy Pad Krapao. It was very delicious!
        """.trimIndent()

        return ListeningScriptResult(
            title = if (isVideo) "Video Script: Meeting Before Class" else "Audio Dialogue: Talking About Weekend Hobbies",
            script = fallbackScript,
            questions = listOf(
                "1. Who are the speakers in this conversation? (ใครเป็นผู้พูดในบทสนทนานี้?)",
                "2. Where did they decide to go or what did they cook? (พวกเขาตัดสินใจไปไหนหรือทำอาหารอะไร?)",
                "3. What adjective was used to describe the food or activity? (คำคุณศัพท์ใดที่ใช้บรรยายอาหารหรือกิจกรรม?)"
            ),
            answerKey = "1. Students/Teacher at school\n2. Library / Cooked Pad Krapao\n3. Delicious / Wonderful"
        )
    }

    // 13. Oral AI Assistant Roleplay
    suspend fun startRoleplay(content: String, grade: String): ConversationMessage {
        val prompt = """
            You are an encouraging, friendly native English teacher roleplaying with a Thai grade $grade student.
            The topic is: "$content".
            Start the roleplay by greeting the student in character and asking a simple friendly question (under 30 words).
            Provide the English line and a Thai translation in parentheses.
        """.trimIndent()

        val text = queryGemini(prompt)
        if (!text.isNullOrBlank()) {
            val parts = text.split("(")
            val en = parts[0].trim()
            val th = if (parts.size > 1) "(" + parts.drop(1).joinToString("(").trim() else "(สวัสดีจ้า มาฝึกภาษาอังกฤษกันนะ!)"
            return ConversationMessage(
                id = "msg_0",
                sender = "ai",
                textEn = en,
                textTh = th
            )
        }

        return ConversationMessage(
            id = "msg_0",
            sender = "ai",
            textEn = "Hello there! Welcome to our English chat. What did you do yesterday after school?",
            textTh = "(สวัสดีจ้า! ยินดีต้อนรับสู่การฝึกสนทนาภาษาอังกฤษ เมื่อวานนี้หลังเลิกเรียนเธอทำอะไรบ้าง?)"
        )
    }

    suspend fun replyRoleplay(
        history: List<ConversationMessage>,
        studentInput: String,
        content: String,
        grade: String
    ): ConversationMessage {
        val historyText = history.takeLast(6).joinToString("\n") {
            "${if (it.sender == "ai") "Teacher" else "Student"}: ${it.textEn}"
        }
        val prompt = """
            You are a friendly English teacher speaking with a Thai student (Grade $grade).
            Topic: "$content"
            Conversation history:
            $historyText
            Student says: "$studentInput"

            Reply in character with:
            1. An encouraging response under 30 words.
            2. A gentle grammar correction woven naturally if they made a mistake.
            3. Thai translation in parentheses.
            Format:
            [English response] ([Thai translation])
            TIP: [Grammar tip if applicable]
        """.trimIndent()

        val text = queryGemini(prompt)
        if (!text.isNullOrBlank()) {
            val lines = text.lines()
            val mainLine = lines.firstOrNull { it.isNotBlank() } ?: text
            val tipLine = lines.firstOrNull { it.startsWith("TIP:", ignoreCase = true) }?.removePrefix("TIP:")?.trim()

            val parts = mainLine.split("(")
            val en = parts[0].trim()
            val th = if (parts.size > 1) "(" + parts.drop(1).joinToString("(").replace(Regex("TIP:.*"), "").trim() else ""

            return ConversationMessage(
                id = "msg_${System.currentTimeMillis()}",
                sender = "ai",
                textEn = en,
                textTh = th.ifBlank { null },
                grammarTip = tipLine
            )
        }

        return ConversationMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = "ai",
            textEn = "That sounds wonderful! Did you go with your friends or your family?",
            textTh = "(ฟังดูยอดเยี่ยมมากเลย! เธอไปกับเพื่อนๆ หรือไปกับครอบครัวเหรอจ๊ะ?)",
            grammarTip = if (studentInput.contains("go to", ignoreCase = true) && !studentInput.contains("went", ignoreCase = true)) "Tip: Use 'went' for past actions (เช่น I went to the market yesterday)" else null
        )
    }

    // --- Helper Functions & Fallbacks ---

    private fun parseStoryText(text: String): StoryResult {
        val lines = text.lines()
        val title = lines.firstOrNull { it.startsWith("Title:", ignoreCase = true) }?.removePrefix("Title:")?.trim() ?: "English Story Time"
        val qIdx = lines.indexOfFirst { it.startsWith("Questions:", ignoreCase = true) }
        val story = if (qIdx != -1) lines.subList(0, qIdx).filter { !it.startsWith("Title:", ignoreCase = true) }.joinToString("\n").trim() else text
        val qList = mutableListOf<Pair<String, String>>()
        if (qIdx != -1) {
            for (i in qIdx + 1 until lines.size) {
                val l = lines[i].trim()
                if (l.isNotBlank()) {
                    val parts = l.split("(")
                    val qEn = parts[0].trim().removePrefix("1.").removePrefix("2.").removePrefix("3.").trim()
                    val qTh = if (parts.size > 1) parts[1].replace(")", "").trim() else ""
                    qList.add(qEn to qTh)
                }
            }
        }
        return StoryResult(
            title = title,
            storyText = story,
            questions = if (qList.isNotEmpty()) qList else listOf("What is the main idea of the story?" to "ใจความสำคัญของเรื่องคืออะไร?")
        )
    }

    private fun buildWordSearchGrid(words: List<String>, size: Int): WordSearchResult {
        val grid = Array(size) { CharArray(size) { ' ' } }
        val dirs = listOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(0, -1))

        words.forEach { word ->
            val upper = word.uppercase()
            if (upper.length <= size) {
                for (attempt in 0..60) {
                    val dir = dirs.random()
                    val dr = dir.first
                    val dc = dir.second
                    val r = Random.nextInt(0, size - if (dr > 0) upper.length - 1 else 0)
                    val c = Random.nextInt(0, size - if (dc > 0) upper.length - 1 else 0)

                    var canPlace = true
                    for (i in upper.indices) {
                        val currR = r + dr * i
                        val currC = c + dc * i
                        if (currR !in 0 until size || currC !in 0 until size) {
                            canPlace = false
                            break
                        }
                        if (grid[currR][currC] != ' ' && grid[currR][currC] != upper[i]) {
                            canPlace = false
                            break
                        }
                    }
                    if (canPlace) {
                        for (i in upper.indices) {
                            grid[r + dr * i][c + dc * i] = upper[i]
                        }
                        break
                    }
                }
            }
        }

        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == ' ') {
                    grid[r][c] = letters.random()
                }
            }
        }

        return WordSearchResult(
            words = words,
            size = size,
            grid = grid.map { it.toList() }
        )
    }

    private fun getFallbackStandardTest(content: String, grade: String): StandardTestResult {
        val items = listOf(
            StandardTestItem(
                1,
                "Yesterday, Nong Ploy ______ her bicycle to the library.",
                "เมื่อวานนี้น้องพลอย...จักรยานไปห้องสมุด",
                listOf("ride", "rode", "riding", "rides"),
                1,
                "Rode is the past tense of ride (ใช้ rode เพราะเป็นเหตุการณ์เมื่อวานนี้)"
            ),
            StandardTestItem(
                2,
                "What is the meaning of the word 'Delicious' in Thai?",
                "คำว่า 'Delicious' มีความหมายภาษาไทยว่าอย่างไร?",
                listOf("อร่อย", "เผ็ด", "แพง", "สะอาด"),
                0,
                "Delicious means very tasty / อร่อย"
            ),
            StandardTestItem(
                3,
                "Songkran is a traditional water ______ celebrated in Thailand.",
                "สงกรานต์เป็น...ทางน้ำตามประเพณีที่จัดขึ้นในประเทศไทย",
                listOf("canteen", "stadium", "festival", "laboratory"),
                2,
                "Festival means เทศกาล"
            ),
            StandardTestItem(
                4,
                "The students were very ______ when they won the quiz competition.",
                "นักเรียนรู้สึก...มากเมื่อพวกเขาชนะการแข่งขันตอบคำถาม",
                listOf("tired", "excited", "sleepy", "angry"),
                1,
                "Excited means ตื่นเต้น/ดีใจ"
            ),
            StandardTestItem(
                5,
                "Which sentence is grammatically correct?",
                "ประโยคใดถูกต้องตามหลักไวยากรณ์?",
                listOf(
                    "She don't like spicy food.",
                    "They went to Chiang Mai last week.",
                    "He eat breakfast at seven am.",
                    "We is studying English now."
                ),
                1,
                "'They went to Chiang Mai last week' uses past tense went correctly."
            )
        )
        val key = items.mapIndexed { idx, it -> "${idx + 1}. ${it.options[it.correctIndex]} (${it.questionTh})" }.joinToString("\n")
        return StandardTestResult("Standard English Quiz ($grade)", items, key)
    }

    private fun getFallbackFlashcards(content: String): List<FlashCardItem> {
        return listOf(
            FlashCardItem(1, "Bicycle", "จักรยาน", "A two-wheeled vehicle"),
            FlashCardItem(2, "Delicious", "อร่อย", "Tasty and flavorful"),
            FlashCardItem(3, "Library", "ห้องสมุด", "Place with books to read"),
            FlashCardItem(4, "Yesterday", "เมื่อวานนี้", "The day before today"),
            FlashCardItem(5, "Festival", "เทศกาล", "A celebration or feast"),
            FlashCardItem(6, "Excited", "ตื่นเต้น", "Feeling enthusiastic"),
            FlashCardItem(7, "Student", "นักเรียน", "Person who studies"),
            FlashCardItem(8, "Teacher", "คุณครู/อาจารย์", "Person who instructs")
        )
    }

    private fun getFallbackSpeakingQuestions(topic: String, grade: String): List<SpeakingListeningQuestion> {
        return listOf(
            SpeakingListeningQuestion(
                1,
                "wh",
                "What is your favorite subject at school and why?",
                "วิชาที่คุณชอบที่สุดในโรงเรียนคือวิชาอะไร และทำไม?",
                "My favorite subject is English because it is fun and useful."
            ),
            SpeakingListeningQuestion(
                2,
                "yn",
                "Do you ride a bicycle or take a bus to school?",
                "คุณขี่จักรยานหรือนั่งรถบัสมาโรงเรียน?",
                "I ride my bicycle to school every morning."
            ),
            SpeakingListeningQuestion(
                3,
                "wh",
                "What did you eat for dinner yesterday evening?",
                "เมื่อวานตอนเย็นคุณกินอะไรเป็นอาหารเย็น?",
                "Yesterday I ate delicious Pad Thai with my family."
            ),
            SpeakingListeningQuestion(
                4,
                "yn",
                "Have you ever visited Songkran festival in Bangkok?",
                "คุณเคยไปเที่ยวเทศกาลสงกรานต์ที่กรุงเทพฯ ไหม?",
                "Yes, I have. It was very exciting and wet!"
            ),
            SpeakingListeningQuestion(
                5,
                "wh",
                "Where do you like to go with your friends on weekends?",
                "ในวันหยุดสุดสัปดาห์ คุณชอบไปเที่ยวที่ไหนกับเพื่อนๆ?",
                "I like to go to the park and the night market."
            ),
            SpeakingListeningQuestion(
                6,
                "yn",
                "Are you ready to practice speaking English every day?",
                "คุณพร้อมที่จะฝึกพูดภาษาอังกฤษทุกวันหรือยัง?",
                "Yes, I am always ready to practice!"
            )
        )
    }

    // 10. Match-up Pairs
    suspend fun generateMatchUp(content: String, grade: String): MatchUpResult {
        val prompt = """
            Create a Match-Up Pairs activity (term to definition or Thai translation) for Thai grade $grade English students based on: "$content".
            Generate 6 pairs. Return ONLY valid JSON format:
            {
              "title": "Vocabulary Match-Up ($grade)",
              "pairs": [
                {
                  "term": "Library",
                  "definition": "A quiet place where students read and borrow books (ห้องสมุด)",
                  "audioHint": "Library"
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "Match-Up Pairs: $content")
                val array = obj.getJSONArray("pairs")
                val list = mutableListOf<MatchUpItem>()
                for (i in 0 until array.length()) {
                    val p = array.getJSONObject(i)
                    list.add(
                        MatchUpItem(
                            id = i + 1,
                            term = p.getString("term"),
                            definition = p.getString("definition"),
                            audioHint = p.optString("audioHint", p.getString("term"))
                        )
                    )
                }
                if (list.isNotEmpty()) return MatchUpResult(title, list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return getFallbackMatchUp(content, grade)
    }

    // 11. Quiz Race / Speed Round
    suspend fun generateQuizRace(content: String, grade: String): QuizRaceResult {
        val prompt = """
            Create a fast-paced Quiz Race (timed speed quiz) for Thai grade $grade English students based on: "$content".
            Generate 5 questions with options and points. Return ONLY valid JSON format:
            {
              "title": "Speed Quiz Race: $content",
              "items": [
                {
                  "question": "What is the past tense of 'Eat'?",
                  "questionTh": "รูปอดีตของ 'Eat' คืออะไร?",
                  "options": ["Eat", "Ate", "Eaten", "Eating"],
                  "correctIndex": 1,
                  "points": 100,
                  "timeLimitSeconds": 15
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "Quiz Race: $content")
                val array = obj.getJSONArray("items")
                val list = mutableListOf<QuizRaceItem>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    val opts = mutableListOf<String>()
                    val oArray = item.getJSONArray("options")
                    for (j in 0 until oArray.length()) opts.add(oArray.getString(j))
                    list.add(
                        QuizRaceItem(
                            id = i + 1,
                            question = item.getString("question"),
                            questionTh = item.optString("questionTh", ""),
                            options = opts,
                            correctIndex = item.getInt("correctIndex"),
                            points = item.optInt("points", 100),
                            timeLimitSeconds = item.optInt("timeLimitSeconds", 15)
                        )
                    )
                }
                if (list.isNotEmpty()) return QuizRaceResult(title, list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return getFallbackQuizRace(content, grade)
    }

    // 12. Anagram (Word Scramble)
    suspend fun generateAnagram(content: String, grade: String): AnagramResult {
        val prompt = """
            Create an Anagram / Word Scramble game for Thai grade $grade English learners based on: "$content".
            Pick 5 meaningful English vocabulary words from the topic.
            Return ONLY valid JSON format:
            {
              "title": "Word Scramble Challenger ($grade)",
              "items": [
                {
                  "word": "FESTIVAL",
                  "hintTh": "เทศกาล / งานฉลอง",
                  "definitionEn": "A day or period of celebration"
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "Anagram Word Scramble ($grade)")
                val array = obj.getJSONArray("items")
                val list = mutableListOf<AnagramItem>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    val rawWord = item.getString("word").trim().uppercase()
                    var scrambledChars = rawWord.toList().shuffled()
                    if (scrambledChars == rawWord.toList() && rawWord.length > 1) {
                        scrambledChars = scrambledChars.reversed()
                    }
                    list.add(
                        AnagramItem(
                            id = i + 1,
                            word = rawWord,
                            scrambled = scrambledChars,
                            hintTh = item.optString("hintTh", ""),
                            definitionEn = item.optString("definitionEn", "")
                        )
                    )
                }
                if (list.isNotEmpty()) return AnagramResult(title, list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return getFallbackAnagram(content, grade)
    }

    // 13. True / False Speed Round
    suspend fun generateTrueFalse(content: String, grade: String): TrueFalseResult {
        val prompt = """
            Create a True/False Speed Round for Thai grade $grade English students based on: "$content".
            Generate 6 statements (mix of True and False) with Thai translations and explanations.
            Return ONLY valid JSON format:
            {
              "title": "True or False Speed Round: $content",
              "items": [
                {
                  "statementEn": "The past tense of 'go' is 'goed'.",
                  "statementTh": "รูปอดีตของ 'go' คือ 'goed'",
                  "isTrue": false,
                  "explanation": "False! The past tense of 'go' is 'went'."
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "True / False Speed: $content")
                val array = obj.getJSONArray("items")
                val list = mutableListOf<TrueFalseItem>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    list.add(
                        TrueFalseItem(
                            id = i + 1,
                            statementEn = item.getString("statementEn"),
                            statementTh = item.optString("statementTh", ""),
                            isTrue = item.getBoolean("isTrue"),
                            explanation = item.optString("explanation", "")
                        )
                    )
                }
                if (list.isNotEmpty()) return TrueFalseResult(title, list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return getFallbackTrueFalse(content, grade)
    }

    // 14. Open The Box (Mystery Question Boxes)
    suspend fun generateOpenTheBox(content: String, grade: String): OpenBoxResult {
        val prompt = """
            Create an "Open the Box" interactive classroom mystery game for Thai grade $grade English students based on: "$content".
            Generate 6 mystery boxes with questions, answers, points (100 to 300 XP).
            Return ONLY valid JSON format:
            {
              "title": "Open The Mystery Box: $content",
              "boxes": [
                {
                  "boxNumber": 1,
                  "questionEn": "What do Thai people splash during Songkran?",
                  "questionTh": "คนไทยสาดอะไรในวันสงกรานต์?",
                  "answer": "Water (น้ำ)",
                  "points": 100
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "Open The Box: $content")
                val array = obj.getJSONArray("boxes")
                val list = mutableListOf<OpenBoxItem>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    list.add(
                        OpenBoxItem(
                            boxNumber = item.optInt("boxNumber", i + 1),
                            questionEn = item.getString("questionEn"),
                            questionTh = item.optString("questionTh", ""),
                            answer = item.getString("answer"),
                            points = item.optInt("points", (i + 1) * 50)
                        )
                    )
                }
                if (list.isNotEmpty()) return OpenBoxResult(title, list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return getFallbackOpenTheBox(content, grade)
    }

    private fun getFallbackMatchUp(content: String, grade: String): MatchUpResult {
        return MatchUpResult(
            title = "Match-Up Pairs: English & Thai ($grade)",
            pairs = listOf(
                MatchUpItem(1, "Delicious", "Very tasty and flavorful (อร่อยมาก)"),
                MatchUpItem(2, "Bicycle", "A vehicle with two wheels you pedal (จักรยาน)"),
                MatchUpItem(3, "Library", "A room or building containing books for reading (ห้องสมุด)"),
                MatchUpItem(4, "Festival", "A special day of celebration or holiday (เทศกาล)"),
                MatchUpItem(5, "Excited", "Feeling very happy and enthusiastic (ตื่นเต้น)"),
                MatchUpItem(6, "Yesterday", "The day before today (เมื่อวานนี้)")
            )
        )
    }

    private fun getFallbackQuizRace(content: String, grade: String): QuizRaceResult {
        return QuizRaceResult(
            title = "Speed Quiz Race ($grade)",
            items = listOf(
                QuizRaceItem(1, "What is the past tense of 'Go'?", "รูปอดีตของ 'Go' คืออะไร?", listOf("Goes", "Went", "Gone", "Going"), 1, 100, 15),
                QuizRaceItem(2, "Where do students eat lunch at school?", "นักเรียนรับประทานอาหารกลางวันที่ไหน?", listOf("Canteen", "Library", "Gym", "Bus"), 0, 100, 15),
                QuizRaceItem(3, "Choose the correct spelling:", "เลือกคำที่สะกดถูกต้อง:", listOf("Delisious", "Delicious", "Delishus", "Delicous"), 1, 150, 15),
                QuizRaceItem(4, "What is the opposite of 'Boring'?", "คำตรงข้ามของ 'Boring' (น่าเบื่อ) คืออะไร?", listOf("Sad", "Exciting", "Tired", "Slow"), 1, 150, 15),
                QuizRaceItem(5, "Which is a traditional Thai holiday?", "ข้อใดคือวันหยุดประเพณีไทย?", listOf("Halloween", "Christmas", "Songkran", "Thanksgiving"), 2, 200, 15)
            )
        )
    }

    private fun getFallbackAnagram(content: String, grade: String): AnagramResult {
        return AnagramResult(
            title = "Anagram Word Scramble ($grade)",
            items = listOf(
                AnagramItem(1, "SCHOOL", listOf('O', 'S', 'H', 'C', 'L', 'O'), "สถานที่เรียนหนังสือ", "Place of learning"),
                AnagramItem(2, "FRIEND", listOf('N', 'E', 'D', 'F', 'R', 'I'), "เพื่อน / มิตร", "A person you like and know well"),
                AnagramItem(3, "LISTEN", listOf('T', 'I', 'L', 'S', 'N', 'E'), "ตั้งใจฟัง", "To pay attention with your ears"),
                AnagramItem(4, "SUMMER", listOf('M', 'R', 'S', 'U', 'E', 'M'), "ฤดูร้อน", "The warmest season of the year"),
                AnagramItem(5, "ANSWER", listOf('W', 'S', 'A', 'N', 'R', 'E'), "คำตอบ / เฉลย", "A reply to a question")
            )
        )
    }

    private fun getFallbackTrueFalse(content: String, grade: String): TrueFalseResult {
        return TrueFalseResult(
            title = "True / False Speed Round ($grade)",
            items = listOf(
                TrueFalseItem(1, "The past tense of 'eat' is 'ate'.", "รูปอดีตของ 'eat' คือ 'ate'", true, "Correct! Eat -> Ate -> Eaten."),
                TrueFalseItem(2, "A 'library' is a place where you buy groceries.", "ห้องสมุดคือสถานที่ซื้อของชำ", false, "False! A library is where you borrow books."),
                TrueFalseItem(3, "Songkran is celebrated in April in Thailand.", "วันสงกรานต์จัดขึ้นในเดือนเมษายนที่ประเทศไทย", true, "Correct! Songkran is April 13-15."),
                TrueFalseItem(4, "'Excited' means feeling sleepy and tired.", "'Excited' แปลว่ารู้สึกง่วงและเหนื่อย", false, "False! 'Excited' means feeling enthusiastic and eager."),
                TrueFalseItem(5, "'Bicycle' has two wheels.", "จักรยานมีสองล้อ", true, "Correct! 'Bi' means two."),
                TrueFalseItem(6, "We use 'went' for present continuous tense.", "เราใช้ 'went' สำหรับ Present Continuous", false, "False! 'Went' is past simple tense.")
            )
        )
    }

    private fun getFallbackOpenTheBox(content: String, grade: String): OpenBoxResult {
        return OpenBoxResult(
            title = "Open The Mystery Box Challenge ($grade)",
            boxes = listOf(
                OpenBoxItem(1, "What is the capital city of Thailand?", "เมืองหลวงของประเทศไทยคืออะไร?", "Bangkok (กรุงเทพฯ)", 100),
                OpenBoxItem(2, "Name 3 colors in English.", "บอกชื่อสีภาษาอังกฤษ 3 สี", "Red, Blue, Green (หรือสีอื่นๆ)", 150),
                OpenBoxItem(3, "Spell the word 'TEACHER' aloud!", "สะกดคำว่า 'TEACHER' ออกเสียงดังๆ", "T - E - A - C - H - E - R", 200),
                OpenBoxItem(4, "Translate: 'ฉันชอบเรียนภาษาอังกฤษ'", "แปลเป็นภาษาอังกฤษ", "I like studying English.", 200),
                OpenBoxItem(5, "What is the past tense of 'SEE'?", "รูปอดีตของคำว่า 'SEE' คืออะไร?", "Saw (เห็นแล้ว)", 250),
                OpenBoxItem(6, "Give a polite greeting in English.", "กล่าวคำทักทายสุภาพเป็นภาษาอังกฤษ", "Good morning! / Hello, how are you?", 300)
            )
        )
    }

    // =========================================================================
    // 17. iSLCollective-Style Interactive PowerPoint Deck Generator
    // =========================================================================
    suspend fun generatePowerPointDeck(content: String, grade: String): PowerPointDeckModel {
        val prompt = """
            Create a 5-slide interactive iSLCollective-style ESL PowerPoint presentation deck based on: "$content" for Thai Mattayom $grade students.
            Return ONLY valid JSON format:
            {
              "title": "Engaging Presentation Title",
              "titleTh": "ชื่อสไลด์ภาษาไทย",
              "description": "Short overview of the lesson deck",
              "slides": [
                {
                  "slideNumber": 1,
                  "layoutType": "TITLE_HERO",
                  "title": "Main Slide Title",
                  "subtitle": "Subtitle with Thai translation",
                  "headline": "Classroom objective",
                  "bodyEn": "Introductory welcome text in English",
                  "bodyTh": "ข้อความต้อนรับภาษาไทย",
                  "teacherNotesEn": "Teacher script in English",
                  "teacherNotesTh": "คำแนะนำครูภาษาไทย"
                },
                {
                  "slideNumber": 2,
                  "layoutType": "GRAMMAR_RULE",
                  "title": "Rule & Grammar Guide",
                  "subtitle": "Formula & Key Concepts",
                  "bodyEn": "Rule explanation in English",
                  "bodyTh": "คำอธิบายไวยากรณ์ภาษาไทย",
                  "bulletPoints": ["Key point 1", "Key point 2"],
                  "teacherNotesEn": "Teacher script in English",
                  "teacherNotesTh": "คำแนะนำครูภาษาไทย"
                },
                {
                  "slideNumber": 3,
                  "layoutType": "JEOPARDY_MCQ",
                  "title": "Interactive Challenge #1",
                  "subtitle": "100 Points Question",
                  "bodyEn": "Multiple choice question prompt in English",
                  "bodyTh": "คำแปลโจทย์ภาษาไทย",
                  "options": ["A) Option 1", "B) Option 2", "C) Option 3", "D) Option 4"],
                  "correctAnswer": "A) Option 1",
                  "explanationEn": "Explanation in English",
                  "explanationTh": "คำอธิบายเฉลยภาษาไทย",
                  "pointsValue": 100,
                  "teacherNotesEn": "Teacher script in English",
                  "teacherNotesTh": "คำแนะนำครูภาษาไทย"
                },
                {
                  "slideNumber": 4,
                  "layoutType": "SPOT_MISTAKE",
                  "title": "Boss Battle: Spot the Error",
                  "subtitle": "Find the grammatical mistake",
                  "bodyEn": "A sentence containing a common grammar error",
                  "bodyTh": "คำแปลภาษาไทย",
                  "correctAnswer": "Error: 'wrong word' -> MUST BE 'correct word'",
                  "explanationEn": "Grammar rule explanation",
                  "explanationTh": "คำอธิบายกฎไวยากรณ์",
                  "pointsValue": 150,
                  "teacherNotesEn": "Teacher script in English",
                  "teacherNotesTh": "คำแนะนำครูภาษาไทย"
                },
                {
                  "slideNumber": 5,
                  "layoutType": "SUMMARY_HOMEWORK",
                  "title": "Lesson Wrap-Up & Homework",
                  "subtitle": "Key Takeaways",
                  "bodyEn": "Summary message and homework instructions",
                  "bodyTh": "สรุปเนื้อหาและการบ้าน",
                  "bulletPoints": ["Takeaway 1", "Takeaway 2", "Takeaway 3"],
                  "teacherNotesEn": "Teacher script in English",
                  "teacherNotesTh": "คำแนะนำครูภาษาไทย"
                }
              ]
            }
        """.trimIndent()

        val jsonStr = queryGemini(prompt)
        if (jsonStr != null) {
            try {
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "Interactive ESL Slide Deck")
                val titleTh = obj.optString("titleTh", "สไลด์การสอนภาษาอังกฤษ")
                val desc = obj.optString("description", "Interactive classroom presentation.")
                val slidesArray = obj.getJSONArray("slides")
                val slidesList = mutableListOf<PowerPointSlide>()

                for (i in 0 until slidesArray.length()) {
                    val s = slidesArray.getJSONObject(i)
                    val num = s.optInt("slideNumber", i + 1)
                    val layoutStr = s.optString("layoutType", "TITLE_HERO")
                    val layout = try { SlideLayoutType.valueOf(layoutStr) } catch (e: Exception) {
                        if (i == 0) SlideLayoutType.TITLE_HERO else if (i == slidesArray.length() - 1) SlideLayoutType.SUMMARY_HOMEWORK else SlideLayoutType.JEOPARDY_MCQ
                    }
                    val sTitle = s.optString("title", "Slide $num")
                    val sSub = if (s.has("subtitle")) s.getString("subtitle") else null
                    val sHead = if (s.has("headline")) s.getString("headline") else null
                    val sBodyEn = s.optString("bodyEn", "")
                    val sBodyTh = s.optString("bodyTh", "")

                    val bullets = mutableListOf<String>()
                    if (s.has("bulletPoints")) {
                        val bArr = s.getJSONArray("bulletPoints")
                        for (b in 0 until bArr.length()) bullets.add(bArr.getString(b))
                    }

                    val opts = mutableListOf<String>()
                    if (s.has("options")) {
                        val oArr = s.getJSONArray("options")
                        for (o in 0 until oArr.length()) opts.add(oArr.getString(o))
                    }

                    val corr = if (s.has("correctAnswer")) s.getString("correctAnswer") else null
                    val expEn = if (s.has("explanationEn")) s.getString("explanationEn") else null
                    val expTh = if (s.has("explanationTh")) s.getString("explanationTh") else null
                    val pts = s.optInt("pointsValue", 100)
                    val tEn = s.optString("teacherNotesEn", "")
                    val tTh = s.optString("teacherNotesTh", "")

                    slidesList.add(
                        PowerPointSlide(
                            slideNumber = num,
                            layoutType = layout,
                            title = sTitle,
                            subtitle = sSub,
                            headline = sHead,
                            bodyEn = sBodyEn,
                            bodyTh = sBodyTh,
                            bulletPoints = bullets,
                            options = opts,
                            correctAnswer = corr,
                            explanationEn = expEn,
                            explanationTh = expTh,
                            pointsValue = pts,
                            teacherNotesEn = tEn,
                            teacherNotesTh = tTh,
                            visualEmoji = when (layout) {
                                SlideLayoutType.TITLE_HERO -> "📽️"
                                SlideLayoutType.MYSTERY_BOX -> "🎁"
                                SlideLayoutType.JEOPARDY_MCQ -> "⚡"
                                SlideLayoutType.SPOT_MISTAKE -> "🕵️"
                                SlideLayoutType.WOULD_YOU_RATHER -> "💬"
                                SlideLayoutType.DIALOGUE_ROLEPLAY -> "🗣️"
                                SlideLayoutType.TABOO_GUESS -> "🤫"
                                SlideLayoutType.SUMMARY_HOMEWORK -> "🏆"
                                else -> "📝"
                            }
                        )
                    )
                }

                if (slidesList.isNotEmpty()) {
                    return PowerPointDeckModel(
                        id = "ppt_ai_${System.currentTimeMillis()}",
                        title = title,
                        titleTh = titleTh,
                        category = PptCategory.GRAMMAR,
                        gradeLevel = grade,
                        difficulty = "Intermediate",
                        totalSlides = slidesList.size,
                        estimatedMinutes = 15,
                        badgeIcon = "✨",
                        sourceAttribution = "AI-Generated iSLCollective Deck",
                        description = desc,
                        tags = listOf("AI Generator", "Interactive PPT", grade),
                        slides = slidesList
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return getFallbackPowerPointDeck(content, grade)
    }

    private fun getFallbackPowerPointDeck(content: String, grade: String): PowerPointDeckModel {
        val topic = if (content.isNotBlank()) content.take(30) else "English Grammar & Conversation"
        return PowerPointDeckModel(
            id = "ppt_ai_fallback_${System.currentTimeMillis()}",
            title = "Interactive Presentation: $topic",
            titleTh = "สไลด์การสอนแบบมีส่วนร่วม: $topic",
            category = PptCategory.GRAMMAR,
            gradeLevel = grade,
            difficulty = "Intermediate",
            totalSlides = 5,
            estimatedMinutes = 15,
            badgeIcon = "📽️",
            sourceAttribution = "iSLCollective ESL Master Deck",
            description = "Interactive 5-slide classroom deck featuring warm-up, core grammar rules, interactive team challenge, and homework wrap-up.",
            tags = listOf(topic, "ESL PowerPoint", grade),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "LESSON CHALLENGE: $topic",
                    subtitle = "Interactive Classroom Deck ($grade) · สื่อการสอนแบบมีปฏิสัมพันธ์",
                    headline = "Objective: Master key concepts and compete for team points!",
                    bodyEn = "Welcome to today's English quest on '$topic'. Work with your team, answer the challenges, and climb the classroom leaderboard!",
                    bodyTh = "ยินดีต้อนรับสู่บทเรียน '$topic' ร่วมมือกับเพื่อนในทีม ตอบคำถามชิงคะแนน และฝึกพูดภาษาอังกฤษไปด้วยกัน!",
                    teacherNotesEn = "Welcome the students, split the room into Team Red and Team Blue, and explain the scoring system.",
                    teacherNotesTh = "ทักทายนักเรียน แบ่งเป็น 2 ทีม (ทีมแดงและทีมน้ำเงิน) เพื่อเริ่มทำกิจกรรม",
                    visualEmoji = "📽️"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.GRAMMAR_RULE,
                    title = "CORE CONCEPT & FORMULA 📊",
                    subtitle = "Key Rules for $topic",
                    bodyEn = "Rule Summary: Pay close attention to sentence structure, subject-verb agreement, and context clues.",
                    bodyTh = "สรุปกฎสำคัญ: สังเกตโครงสร้างประโยค ประธาน-กริยา และคำบอกเวลาในประโยค",
                    bulletPoints = listOf(
                        "Structure: Subject + Auxiliary / Main Verb + Complement",
                        "Key Keywords: Notice time markers and signal words in context",
                        "Tip: Practice reading sentences aloud for natural rhythm and pronunciation"
                    ),
                    teacherNotesEn = "Read through the bullet points and have the class repeat the example formula.",
                    teacherNotesTh = "อ่านทบทวนหัวข้อสำคัญบนสไลด์ ให้นักเรียนออกเสียงตามพร้อมกัน",
                    visualEmoji = "📊"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "TEAM CHALLENGE: QUESTION 1 (100 PTS)",
                    subtitle = "Test your understanding of $topic",
                    bodyEn = "Which of the following sentences correctly applies the rule for '$topic'?",
                    bodyTh = "ประโยคใดต่อไปนี้ถูกต้องตามหลักไวยากรณ์ของบทเรียนนี้มากที่สุด?",
                    options = listOf(
                        "A) The students are practicing their English presentation right now.",
                        "B) The students is practice their English presentation right now.",
                        "C) The students practicing English yesterday morning.",
                        "D) The students does not practices English at all."
                    ),
                    correctAnswer = "A) The students are practicing their English presentation right now.",
                    explanationEn = "'The students' is plural subject, so it takes 'are' + V-ing ('practicing') for Present Continuous.",
                    explanationTh = "ประธาน 'The students' เป็นพหูพจน์ ต้องใช้ 'are' ตามด้วยกริยาเติม -ing ('practicing')",
                    pointsValue = 100,
                    teacherNotesEn = "Give 20 seconds for teams to discuss, then tap Reveal Answer.",
                    teacherNotesTh = "ให้เวลาปรึกษากัน 20 วินาที แล้วกดเฉลยคำตอบพร้อมอธิบาย",
                    visualEmoji = "⚡"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "BOSS BATTLE: SPOT THE MISTAKE (150 PTS)",
                    subtitle = "Find and fix the error",
                    bodyEn = "Yesterday afternoon, Somchai and his friends goes to the market after school.",
                    bodyTh = "เมื่อวานช่วงบ่าย สมชายและเพื่อนๆ ไปตลาดหลังเลิกเรียน",
                    correctAnswer = "Error: 'goes' -> MUST BE 'went'",
                    explanationEn = "'Yesterday afternoon' indicates Past Simple tense, so the verb 'go' must become past form 'went'.",
                    explanationTh = "มีคำบอกเวลาอดีต 'Yesterday afternoon' กริยาต้องเปลี่ยนเป็นช่อง 2 คือ 'went' ไม่ใช่ goes",
                    pointsValue = 150,
                    teacherNotesEn = "Call upon a student to point out which word is incorrect.",
                    teacherNotesTh = "สุ่มเรียกนักเรียนบอกจุดที่ผิด และให้อธิบายเหตุผล",
                    visualEmoji = "🕵️"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "LESSON COMPLETE & HOMEWORK 🏆",
                    subtitle = "Summary and LINE Dispatch",
                    bodyEn = "Outstanding participation today! You demonstrated strong understanding of $topic.",
                    bodyTh = "ยอดเยี่ยมมากทุกคน! วันนี้พวกเราได้เรียนรู้และฝึกฝนเรื่อง $topic ได้อย่างคล่องแคล่ว",
                    bulletPoints = listOf(
                        "Review the key grammar structure from Slide 2",
                        "Complete the 3 follow-up practice sentences in your notebook",
                        "Check your simulated LINE inbox for tonight's homework assignment"
                    ),
                    teacherNotesEn = "Tap 'Assign to LINE' to send this lesson summary directly to active students.",
                    teacherNotesTh = "กดปุ่ม 'ส่งการบ้านไปยัง LINE' เพื่อให้นักเรียนทบทวนบทเรียนที่บ้าน",
                    visualEmoji = "🏆"
                )
            )
        )
    }
}

