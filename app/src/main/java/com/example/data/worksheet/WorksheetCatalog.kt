package com.example.data.worksheet

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class WorksheetCategory(val code: String, val titleEn: String, val titleTh: String, val icon: String) {
    ALL("all", "All Worksheets", "ใบงานทั้งหมด", "📚"),
    QUIZIZZ_SPEED("quizizz", "Quizizz Speed Quizzes", "ควิซจับเวลา Quizizz", "⚡"),
    VOCABULARY("vocab", "Pinterest Visual Vocab", "คำศัพท์ภาพสไตล์ Pinterest", "📌"),
    GRAMMAR("grammar", "Grammar & Tenses", "ไวยากรณ์และ Tenses", "📝"),
    READING("reading", "Reading & Stories", "บทความและการอ่าน", "📖"),
    CONVERSATION("conversation", "Daily Roleplay Cards", "การสนทนาในชีวิตจริง", "💬"),
    PUZZLES("puzzles", "Puzzles & Crosswords", "เกมปริศนาและอักษรไขว้", "🧩"),
    EXAM_PREP("exam", "O-NET / TGAT Prep", "ข้อสอบ O-NET และ TGAT", "🎯"),
    CULTURE("culture", "Thai Culture & Travel", "การท่องเที่ยวและวัฒนธรรมไทย", "🇹🇭")
}

enum class QuestionType {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK,
    MATCHING_PAIR,
    TRUE_FALSE,
    SENTENCE_SCRAMBLE,
    READING_PASSAGE_MCQ
}

data class WorksheetQuestion(
    val id: Int,
    val type: QuestionType,
    val promptEn: String,
    val promptTh: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val correctIndex: Int = 0,
    val explanation: String = "",
    val hintTh: String = "",
    val points: Int = 10
)

data class WorksheetItemModel(
    val id: String,
    val title: String,
    val titleTh: String,
    val category: WorksheetCategory,
    val gradeLevel: String, // e.g. "P.1-P.3", "P.4-P.6", "M.1-M.3", "M.4-M.6"
    val sourceStyle: String, // "Pinterest Printable" | "Quizizz Game" | "Infographic Worksheet"
    val difficulty: String, // "Beginner" | "Intermediate" | "Challenging"
    val description: String,
    val instructionsEn: String,
    val instructionsTh: String,
    val badgeIcon: String,
    val estimatedMinutes: Int = 15,
    val totalPoints: Int = 100,
    val passageText: String? = null,
    val passageTitle: String? = null,
    val questions: List<WorksheetQuestion>,
    val tags: List<String> = emptyList()
)

object WorksheetCatalog {

    val allWorksheets: List<WorksheetItemModel> = listOf(
        // =========================================================================
        // 1. QUIZIZZ SPEED QUIZZES
        // =========================================================================
        WorksheetItemModel(
            id = "qz_past_simple_speed",
            title = "Quizizz Blitz: Past Simple Irregular Verbs Sprint",
            titleTh = "ควิซจับเวลา: กริยา 3 ช่อง Past Simple ความเร็วสูง",
            category = WorksheetCategory.QUIZIZZ_SPEED,
            gradeLevel = "M.1-M.3",
            sourceStyle = "Quizizz Game",
            difficulty = "Intermediate",
            description = "High-octane Quizizz-style challenge testing past tense forms of 10 essential irregular verbs.",
            instructionsEn = "Choose the correct past tense form within 15 seconds to build your combo streak!",
            instructionsTh = "เลือกรูปกริยาช่อง 2 ในอดีตที่ถูกต้อง สะสมแต้มคอมโบความเร็วสูง",
            badgeIcon = "⚡",
            estimatedMinutes = 8,
            totalPoints = 100,
            tags = listOf("Past Simple", "Irregular Verbs", "Grammar", "Quizizz"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Yesterday, Somchai _____ his homework before dinner.", "เมื่อวานนี้ สมชายทำ...การบ้านก่อนมื้อเย็น", listOf("do", "did", "done", "doing"), "did", 1, "'did' is the past simple of 'do'.", "อดีตของ do คือ did"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "We _____ delicious Pad Thai at the night market.", "พวกเรา...ผัดไทยแสนอร่อยที่ตลาดโต้รุ่ง", listOf("eat", "eated", "ate", "eaten"), "ate", 2, "Past form of 'eat' is 'ate'.", "eat -> ate"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "Nong Ploy _____ a lovely postcard from Chiang Mai.", "น้องพลอย...โปสการ์ดน่ารักจากเชียงใหม่", listOf("write", "wrote", "written", "writing"), "wrote", 1, "Past simple of 'write' is 'wrote'.", "write -> wrote"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "The students _____ a famous movie in the theater last Sunday.", "นักเรียน...ภาพยนตร์ดังในโรงหนังเมื่อวันอาทิตย์ที่แล้ว", listOf("see", "saw", "seen", "sawed"), "saw", 1, "Past simple of 'see' is 'saw'.", "see -> saw"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "Ajarn Lok _____ a new English dictionary yesterday.", "อาจารย์โลก...พจนานุกรมเล่มใหม่เมื่อวานนี้", listOf("buy", "bought", "bring", "buys"), "bought", 1, "Past simple of 'buy' is 'bought'.", "buy -> bought"),
                WorksheetQuestion(6, QuestionType.MULTIPLE_CHOICE, "My brother _____ his bicycle to school this morning.", "น้องชายของฉัน...จักรยานไปโรงเรียนเมื่อเช้านี้", listOf("ride", "rode", "ridden", "road"), "rode", 1, "Past simple of 'ride' is 'rode'.", "ride -> rode"),
                WorksheetQuestion(7, QuestionType.MULTIPLE_CHOICE, "They _____ very tired after the sports day tournament.", "พวกเขา...เหนื่อยมากหลังการแข่งขันกีฬาสี", listOf("is", "was", "were", "are"), "were", 2, "They + were in the past.", "ประธานพหูพจน์ใช้ were"),
                WorksheetQuestion(8, QuestionType.MULTIPLE_CHOICE, "I _____ my school keys in my backpack this morning.", "ฉัน...กุญแจโรงเรียนในกระเป๋าเป้เมื่อเช้านี้", listOf("find", "found", "founded", "finds"), "found", 1, "Past simple of 'find' is 'found'.", "find -> found")
            )
        ),

        WorksheetItemModel(
            id = "qz_prepositions_speed",
            title = "Quizizz Marathon: Prepositions of Place & Time (In / On / At)",
            titleTh = "ควิซมาราธอน: การใช้ In / On / At บอกสถานที่และเวลา",
            category = WorksheetCategory.QUIZIZZ_SPEED,
            gradeLevel = "P.4-M.2",
            sourceStyle = "Quizizz Game",
            difficulty = "Beginner",
            description = "Rapid multiple-choice quiz designed to eliminate confusion between in, on, and at for Thai students.",
            instructionsEn = "Pick the exact preposition needed for each time or place context.",
            instructionsTh = "เลือกคำบุพบท In, On, At ให้ถูกต้องตามหลักเวลาและสถานที่",
            badgeIcon = "🎯",
            estimatedMinutes = 10,
            totalPoints = 80,
            tags = listOf("Prepositions", "In On At", "Grammar", "Quizizz"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Our English class starts _____ 8:30 AM every Monday.", "วิชาภาษาอังกฤษเริ่มเวลา 8:30 น. ทุกวันจันทร์", listOf("in", "on", "at", "by"), "at", 2, "We use 'at' for specific clock times.", "ใช้ at กับเวลาตามเข็มนาฬิกา"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "Songkran festival is celebrated _____ April every year.", "เทศกาลสงกรานต์จัดขึ้นในเดือนเมษายน", listOf("in", "on", "at", "for"), "in", 0, "We use 'in' for months.", "ใช้ in กับเดือนและปี"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "My birthday is _____ July 15th.", "วันเกิดของฉันตรงกับวันที่ 15 กรกฎาคม", listOf("in", "on", "at", "to"), "on", 1, "We use 'on' for specific dates and days.", "ใช้ on กับวันและวันที่ระบุชัดเจน"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "There are fresh fruits _____ the wooden dining table.", "มีผลไม้สดวางอยู่บนโต๊ะกินข้าวไม้", listOf("in", "on", "at", "under"), "on", 1, "Fruits are on the surface of the table.", "วางอยู่บนพื้นผิวใช้ on"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "Anan lives _____ Bangkok, Thailand.", "อนันต์อาศัยอยู่ในกรุงเทพมหานคร", listOf("in", "on", "at", "with"), "in", 0, "We use 'in' for cities and countries.", "ใช้ in กับเมืองและประเทศ"),
                WorksheetQuestion(6, QuestionType.MULTIPLE_CHOICE, "Meet me _____ the bus stop near the school gate.", "เจอกันที่ป้ายรถเมล์ใกล้ประตูโรงเรียน", listOf("in", "on", "at", "of"), "at", 2, "We use 'at' for specific spot locations.", "ใช้ at กับจุดนัดพบที่ระบุเฉพาะเจาะจง")
            )
        ),

        WorksheetItemModel(
            id = "qz_wh_questions_rush",
            title = "Quizizz Flash: Wh-Question Words Master Challenge",
            titleTh = "ควิซประลองความเร็ว: คำถาม Wh-Questions (Who, What, Where, When, Why, How)",
            category = WorksheetCategory.QUIZIZZ_SPEED,
            gradeLevel = "P.4-P.6",
            sourceStyle = "Quizizz Game",
            difficulty = "Beginner",
            description = "Interactive quiz ensuring students master questions words for conversation and listening tests.",
            instructionsEn = "Identify the correct question word to complete each sentence.",
            instructionsTh = "เลือกคำขึ้นต้นคำถาม Wh- ที่สอดคล้องกับคำตอบในบริบท",
            badgeIcon = "❓",
            estimatedMinutes = 10,
            totalPoints = 80,
            tags = listOf("Wh-Questions", "Conversation", "Grammar"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "_____ is your English teacher? - Ajarn Somsri.", "ใครคือครูสอนภาษาอังกฤษของคุณ? - อาจารย์สมศรี", listOf("What", "Who", "Where", "When"), "Who", 1, "'Who' asks about a person.", "Who ใช้ถามถึงบุคคล"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "_____ is the nearest 7-Eleven store? - Across the road.", "เซเว่นที่ใกล้ที่สุดอยู่ที่ไหน? - ฝั่งตรงข้ามถนน", listOf("Where", "Why", "Who", "Which"), "Where", 0, "'Where' asks about location.", "Where ใช้ถามสถานที่"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "_____ do you ride your bicycle to school? - Because it's healthy.", "ทำไมคุณถึงปั่นจักรยานมาเรียน? - เพราะว่าดีต่อสุขภาพ", listOf("When", "Where", "Why", "What"), "Why", 2, "'Why' asks for reasons ('Because...').", "Why ถามหาเหตุผล"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "_____ does the morning assembly begin? - At 7:50 AM.", "การเข้าแถวเคารพธงชาติตอนเช้าเริ่มกี่โมง? - 7:50 น.", listOf("When", "Who", "Where", "Whose"), "When", 0, "'When' asks about time.", "When ใช้ถามเวลา"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "_____ do you travel from Rayong to Bangkok? - By minivan.", "คุณเดินทางจากระยองไปกรุงเทพฯ อย่างไร? - โดยรถตู้", listOf("How", "What", "Who", "Why"), "How", 0, "'How' asks about manner or transportation.", "How ใช้ถามวิธีการและยานพาหนะ"),
                WorksheetQuestion(6, QuestionType.MULTIPLE_CHOICE, "_____ is your favorite Thai food? - Pad Thai.", "อาหารไทยที่คุณชอบที่สุดคืออะไร? - ผัดไทย", listOf("What", "Where", "Who", "When"), "What", 0, "'What' asks for things or names.", "What ใช้ถามสิ่งของหรือชื่อ")
            )
        ),

        // =========================================================================
        // 2. PINTEREST VISUAL VOCABULARY & INFOGRAPHIC WORKSHEETS
        // =========================================================================
        WorksheetItemModel(
            id = "pin_daily_routine_infographic",
            title = "Pinterest Infographic: Daily Routine & Morning Habits",
            titleTh = "ใบงานอินโฟกราฟิก Pinterest: กิจวัตรประจำวันและนิสัยยามเช้า",
            category = WorksheetCategory.VOCABULARY,
            gradeLevel = "P.4-M.1",
            sourceStyle = "Pinterest Printable",
            difficulty = "Beginner",
            description = "Aesthetic printable worksheet with structured visual timeline boxes and vocabulary matching.",
            instructionsEn = "Fill in the blank boxes with the correct daily routine phrase from the word bank.",
            instructionsTh = "เติมสำนวนกิจวัตรประจำวันลงในช่องว่างให้ตรงกับลำดับเวลาในชีวิตประจำวัน",
            badgeIcon = "⏰",
            estimatedMinutes = 15,
            totalPoints = 80,
            tags = listOf("Daily Routine", "Time", "Pinterest", "Vocabulary"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.FILL_IN_BLANK, "Every morning at 6:00 AM, I [wake up] when the alarm rings.", "ทุกเช้าเวลา 6:00 น. ฉัน (ตื่นนอน) เมื่อนาฬิกาปลุกดัง", correctAnswer = "wake up", hintTh = "ตื่นนอน"),
                WorksheetQuestion(2, QuestionType.FILL_IN_BLANK, "Before eating breakfast, I always [brush my teeth] with mint toothpaste.", "ก่อนทานอาหารเช้า ฉัน (แปรงฟัน) เสมอ", correctAnswer = "brush my teeth", hintTh = "แปรงฟัน"),
                WorksheetQuestion(3, QuestionType.FILL_IN_BLANK, "At 7:15 AM, I put on my student uniform and [pack my backpack].", "เวลา 7:15 น. ฉันใส่ชุดนักเรียนและ (จัดกระเป๋าเป้)", correctAnswer = "pack my backpack", hintTh = "จัดกระเป๋า"),
                WorksheetQuestion(4, QuestionType.FILL_IN_BLANK, "We sing the Thai national anthem and [attend the assembly] at 8:00 AM.", "พวกเราร้องเพลงชาติและ (เข้าร่วมแถวตอนเช้า) เวลา 8:00 น.", correctAnswer = "attend the assembly", hintTh = "เข้าแถวเคารพธงชาติ"),
                WorksheetQuestion(5, QuestionType.FILL_IN_BLANK, "At midday, students gather in the canteen to [eat lunch].", "ตอนเที่ยง นักเรียนมารวมตัวกันที่โรงอาหารเพื่อ (ทานอาหารกลางวัน)", correctAnswer = "eat lunch", hintTh = "ทานอาหารกลางวัน"),
                WorksheetQuestion(6, QuestionType.FILL_IN_BLANK, "After class, I [do my homework] before watching cartoons.", "หลังเลิกเรียน ฉัน (ทำการบ้าน) ก่อนดูการ์ตูน", correctAnswer = "do my homework", hintTh = "ทำการบ้าน"),
                WorksheetQuestion(7, QuestionType.FILL_IN_BLANK, "At 9:30 PM, I turn off the lamp and [go to bed].", "เวลา 21:30 น. ฉันปิดโคมไฟและ (เข้านอน)", correctAnswer = "go to bed", hintTh = "เข้านอน")
            )
        ),

        WorksheetItemModel(
            id = "pin_thai_street_food_vocab",
            title = "Pinterest Visual Board: Thai Street Food & Flavor Descriptors",
            titleTh = "ใบงานคำศัพท์อาหารสตรีทฟู้ดไทยและรสชาติต่างๆ สไตล์ Pinterest",
            category = WorksheetCategory.VOCABULARY,
            gradeLevel = "P.5-M.3",
            sourceStyle = "Pinterest Printable",
            difficulty = "Beginner",
            description = "Rich visual vocabulary sheet matching iconic Thai dishes with sensory tasting adjectives (spicy, sweet, crispy, aromatic).",
            instructionsEn = "Match each Thai food dish with its authentic English flavor descriptor and serving style.",
            instructionsTh = "จับคู่อาหารไทยกับคำคุณศัพท์อธิบายรสชาติและวิธีเสิร์ฟภาษาอังกฤษ",
            badgeIcon = "🍜",
            estimatedMinutes = 12,
            totalPoints = 80,
            tags = listOf("Food", "Adjectives", "Culture", "Pinterest"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Tom Yum Goong is famous for its _____ and sour lemongrass broth.", "ต้มยำกุ้งมีชื่อเสียงเรื่องน้ำซุปตะไคร้รส...และเปรี้ยว", listOf("spicy", "bland", "bitter", "sweet"), "spicy", 0, "'Spicy' describes chili heat.", "spicy = เผ็ด"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "Mango sticky rice is topped with warm coconut milk and has a _____ flavor.", "ข้าวเหนียวมะม่วงราดกะทิอุ่น มีรสชาติ...", listOf("sour", "salty", "sweet", "spicy"), "sweet", 2, "Mango sticky rice is sweet.", "sweet = หวาน"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "Fresh green papaya salad (Som Tum) is famous for its _____ texture.", "ส้มตำไทยมีชื่อเสียงในเรื่องเนื้อสัมผัสที่...", listOf("crispy & crunchy", "soft & soggy", "dry", "burnt"), "crispy & crunchy", 0, "'Crunchy' describes crisp green papaya.", "crispy & crunchy = กรอบ"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "Moo Ping (grilled pork skewers) are marinated to be sweet and _____.", "หมูปิ้งหมักรสหวานและ...", listOf("savory & juicy", "bitter", "raw", "tasteless"), "savory & juicy", 0, "'Savory' describes umami flavor.", "savory & juicy = กลมกล่อมนุ่มชุ่มฉ่ำ"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "Thai iced milk tea (Cha Yen) has a creamy and _____ aroma.", "ชาเย็นใส่นมมีความหอมมันและ...", listOf("fragrant", "sour", "smelly", "salty"), "fragrant", 0, "'Fragrant' describes pleasant scent.", "fragrant = หอมละมุน")
            )
        ),

        WorksheetItemModel(
            id = "pin_emotions_adjectives_wheel",
            title = "Pinterest Emotion Wheel: Feelings, Moods & Personality",
            titleTh = "ใบงานวงล้ออารมณ์และความรู้สึก (Feelings & Emotions Wheel)",
            category = WorksheetCategory.VOCABULARY,
            gradeLevel = "M.1-M.4",
            sourceStyle = "Pinterest Printable",
            difficulty = "Intermediate",
            description = "Emotional literacy worksheet expanding beyond 'happy/sad' to advanced adjectives like confident, exhausted, and thrilled.",
            instructionsEn = "Complete each situational scenario with the most accurate emotion adjective.",
            instructionsTh = "เลือกคำคุณศัพท์แสดงอารมณ์และความรู้สึกที่ตรงกับแต่ละสถานการณ์",
            badgeIcon = "🎭",
            estimatedMinutes = 15,
            totalPoints = 80,
            tags = listOf("Emotions", "Adjectives", "Psychology", "Pinterest"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Somchai studied for 5 hours non-stop for the midterm. He feels completely _____.", "สมชายอ่านหนังสือสอบ 5 ชั่วโมงติด เขารู้สึก...อย่างสิ้นเชิง", listOf("exhausted", "thrilled", "jealous", "careless"), "exhausted", 0, "'Exhausted' means extremely tired.", "exhausted = เหนื่อยล้าหมดแรง"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "Nong Ploy won the national English speech contest! She was _____!", "น้องพลอยชนะการแข่งสปีชภาษาอังกฤษระดับชาติ! เธอรู้สึก...มาก!", listOf("overjoyed & thrilled", "nervous", "disappointed", "guilty"), "overjoyed & thrilled", 0, "'Thrilled' means extremely happy.", "overjoyed & thrilled = ดีใจตื่นเต้นสุดขีด"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "Before walking onto the stage to sing, Anan's hands shook. He was very _____.", "ก่อนขึ้นเวทีร้องเพลง มือของอนันต์สั่น เขารู้สึก...มาก", listOf("anxious & nervous", "proud", "calm", "confident"), "anxious & nervous", 0, "'Anxious' means worried or nervous.", "anxious & nervous = กังวลประหม่า"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "When his classmate lost his pet dog, Somkiat felt _____ for his friend.", "เมื่อเพื่อนทำสุนัขหาย สมเกียรติรู้สึก...เห็นอกเห็นใจเพื่อน", listOf("sympathetic", "greedy", "arrogant", "angry"), "sympathetic", 0, "'Sympathetic' means showing compassion.", "sympathetic = เห็นอกเห็นใจ"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "After practicing her English pronunciation daily, Dao felt _____ during the interview.", "หลังจากฝึกออกเสียงทุกวัน ดาวรู้สึก...มั่นใจในตนเองตอนสัมภาษณ์", listOf("confident", "clumsy", "bored", "scared"), "confident", 0, "'Confident' means believing in oneself.", "confident = มั่นใจในตัวเอง")
            )
        ),

        // =========================================================================
        // 3. GRAMMAR & TENSES PRINTABLE & INTERACTIVE SHEETS
        // =========================================================================
        WorksheetItemModel(
            id = "gr_present_perfect_vs_past_simple",
            title = "Pinterest Grammar Chart: Present Perfect vs Past Simple",
            titleTh = "ใบงานแผนภาพไวยากรณ์: Present Perfect vs Past Simple",
            category = WorksheetCategory.GRAMMAR,
            gradeLevel = "M.2-M.5",
            sourceStyle = "Pinterest Printable",
            difficulty = "Intermediate",
            description = "High-yield grammar comparison worksheet with timeline markers (since, for, already, yesterday, last week).",
            instructionsEn = "Choose whether each sentence requires Present Perfect (have/has + V3) or Past Simple (V2).",
            instructionsTh = "วิเคราะห์ประโยคและเลือกใช้ Present Perfect หรือ Past Simple ให้ถูกต้องตามตัวบอกเวลา",
            badgeIcon = "⏳",
            estimatedMinutes = 15,
            totalPoints = 80,
            tags = listOf("Present Perfect", "Past Simple", "Grammar", "Tenses"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "I _____ to Chiang Mai three times in my life.", "ฉันเคยไปเชียงใหม่มาแล้ว 3 ครั้งในชีวิต", listOf("have been", "went", "am going", "had been"), "have been", 0, "Life experiences with no fixed past time take Present Perfect.", "ประสบการณ์ในชีวิตใช้ have been"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "We _____ Wat Phra Kaew last December during the holidays.", "พวกเราไปวัดพระแก้วเมื่อเดือนธันวาคมปีที่แล้ว", listOf("visited", "have visited", "are visiting", "visits"), "visited", 0, "'Last December' is a specific past time -> Past Simple.", "มีระบุเวลาในอดีตชัดเจนใช้ V2"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "Dao _____ her English assignment yet.", "ดาวยังทำการบ้านภาษาอังกฤษไม่เสร็จเลย", listOf("has not finished", "did not finish", "had not finish", "is not finishing"), "has not finished", 0, "'Yet' signals Present Perfect negative.", "yet ใช้กับ Present Perfect"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "Somchai _____ English for six years since primary school.", "สมชายเรียนภาษาอังกฤษมา 6 ปีแล้วตั้งแต่ชั้นประถม", listOf("has studied", "studied", "studies", "was studying"), "has studied", 0, "'Since' and 'for' indicating duration up to now take Present Perfect.", "การกระทำที่เริ่มในอดีตและทำต่อเนื่องถึงปัจจุบันใช้ have/has + V3"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "Anan _____ his smartphone on the bus yesterday morning.", "อนันต์ทำสมาร์ทโฟนหายบนรถบัสเมื่อเช้าวานนี้", listOf("lost", "has lost", "had lost", "losing"), "lost", 0, "'Yesterday morning' refers to a completed past moment -> Past Simple.", "yesterday morning เป็นอดีตที่สิ้นสุดแล้วใช้ V2")
            )
        ),

        WorksheetItemModel(
            id = "gr_comparatives_superlatives",
            title = "Pinterest Animal Kingdom: Comparatives & Superlatives Sheet",
            titleTh = "ใบงานการเปรียบเทียบขั้นกว่าและขั้นสุด (Animal Kingdom Edition)",
            category = WorksheetCategory.GRAMMAR,
            gradeLevel = "P.5-M.2",
            sourceStyle = "Pinterest Printable",
            difficulty = "Beginner",
            description = "Fun comparative/superlative worksheet with animal speed, size, and intelligence metrics.",
            instructionsEn = "Fill in the correct comparative (-er/more) or superlative (-est/most) form.",
            instructionsTh = "เติมรูปเปรียบเทียบขั้นกว่า (-er/more) หรือขั้นสุด (-est/most) ให้ถูกต้อง",
            badgeIcon = "🐘",
            estimatedMinutes = 12,
            totalPoints = 80,
            tags = listOf("Comparatives", "Superlatives", "Adjectives", "Grammar"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.FILL_IN_BLANK, "An Asian elephant is [heavier] than a water buffalo.", "ช้างเอเชียมีน้ำหนัก (หนักกว่า) ควาย", correctAnswer = "heavier", hintTh = "หนักกว่า (heavy -> heavier)"),
                WorksheetQuestion(2, QuestionType.FILL_IN_BLANK, "The cheetah is the [fastest] land animal on Earth.", "เสือชีตาห์เป็นสัตว์บกที่ (วิ่งเร็วที่สุด) ในโลก", correctAnswer = "fastest", hintTh = "เร็วที่สุด (fast -> fastest)"),
                WorksheetQuestion(3, QuestionType.FILL_IN_BLANK, "Dolphins are considered [more intelligent] than sharks.", "โลมาได้รับการพิจารณาว่า (ฉลาดกว่า) ฉลาม", correctAnswer = "more intelligent", hintTh = "ฉลาดกว่า (intelligent -> more intelligent)"),
                WorksheetQuestion(4, QuestionType.FILL_IN_BLANK, "Mount Everest is the [highest] mountain in the world.", "ยอดเขาเอเวอร์เรสต์เป็นภูเขาที่ (สูงที่สุด) ในโลก", correctAnswer = "highest", hintTh = "สูงที่สุด (high -> highest)"),
                WorksheetQuestion(5, QuestionType.FILL_IN_BLANK, "Bangkok is [more crowded] than Hua Hin.", "กรุงเทพฯ มีความ (แออัด/คนเยอะกว่า) หัวหิน", correctAnswer = "more crowded", hintTh = "แออัดกว่า (crowded -> more crowded)")
            )
        ),

        WorksheetItemModel(
            id = "gr_conditional_if_sentences",
            title = "Quizizz Master: First & Second Conditionals (If-Clauses)",
            titleTh = "ควิซประลองสมอง: ประโยคเงื่อนไข If-Clauses แบบที่ 1 และ 2",
            category = WorksheetCategory.GRAMMAR,
            gradeLevel = "M.2-M.6",
            sourceStyle = "Quizizz Game",
            difficulty = "Intermediate",
            description = "Practice real possibilities (Type 1) and hypothetical dreams (Type 2) with clear formula breakdowns.",
            instructionsEn = "Select the grammatically accurate clause pairing for each conditional sentence.",
            instructionsTh = "เลือกคู่กริยาที่สอดคล้องกับกฎ If-Clause Type 1 และ Type 2",
            badgeIcon = "💡",
            estimatedMinutes = 14,
            totalPoints = 80,
            tags = listOf("If-Clause", "Conditionals", "Grammar", "Quizizz"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "If it _____ tomorrow, we will hold the sports day indoors.", "ถ้าพรุ่งนี้ฝนตก พวกเราจะจัดกีฬาสีในร่ม", listOf("rains", "will rain", "rained", "raining"), "rains", 0, "Type 1: If + Present Simple, will + V1.", "Type 1 ใช้ If + V1"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "If I won 1 million Baht in the lottery, I _____ my family a new house.", "ถ้าฉันถูกหวย 1 ล้านบาท ฉันจะซื้อบ้านใหม่ให้ครอบครัว", listOf("would buy", "will buy", "bought", "buy"), "would buy", 0, "Type 2 hypothetical: If + Past Simple, would + V1.", "Type 2 ใช้ If + V2, would + V1"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "You will pass the English exam if you _____ consistently every day.", "คุณจะสอบผ่านภาษาอังกฤษถ้าคุณทบทวนอย่างสม่ำเสมอทุกวัน", listOf("review", "will review", "reviewed", "reviews"), "review", 0, "Result clause with 'will pass' pairs with Present Simple 'review'.", "will pass คู่กับ V1 (review)"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "If Somchai _____ taller, he would join the school basketball team.", "ถ้าสมชายสูงกว่านี้ เขาคงจะเข้าร่วมทีมบาสเกตบอลของโรงเรียน", listOf("were", "was", "is", "be"), "were", 0, "In formal subjunctive Type 2, 'were' is used for all subjects.", "Type 2 ใช้ were กับทุกประธาน"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "If students do not finish the project on time, teacher _____ points.", "ถ้านักเรียนส่งงานไม่ตรงเวลา ครูจะหักคะแนน", listOf("will deduct", "deducted", "would deduct", "deducts"), "will deduct", 0, "Type 1 real future consequence: will + V1.", "Type 1 ใช้ will + V1")
            )
        ),

        // =========================================================================
        // 4. READING COMPREHENSION & STORY PASSAGES (PINTEREST STYLE)
        // =========================================================================
        WorksheetItemModel(
            id = "rd_chiang_mai_elephant_rescue",
            title = "Pinterest Reading Passage: The Elephant Sanctuary in Chiang Mai",
            titleTh = "ใบงานการอ่านจับใจความ: ศูนย์อนุรักษ์ช้างแห่งเชียงใหม่",
            category = WorksheetCategory.READING,
            gradeLevel = "M.1-M.4",
            sourceStyle = "Pinterest Printable",
            difficulty = "Intermediate",
            description = "Graded reading passage with 5 comprehension questions, vocabulary in context, and moral reflections.",
            instructionsEn = "Read the passage carefully and answer the comprehension questions below.",
            instructionsTh = "อ่านบทความอย่างละเอียดและตอบคำถามเพื่อวัดความเข้าใจใจความสำคัญ",
            badgeIcon = "📖",
            estimatedMinutes = 20,
            totalPoints = 100,
            passageTitle = "A Life-Changing Visit to Elephant Nature Haven",
            passageText = """
                Last weekend, 14-year-old Nong Ploy and her family traveled from Bangkok to Chiang Mai to volunteer at an ethical elephant sanctuary in Mae Rim. Unlike old tourist camps where elephants were forced to carry heavy wooden chairs, this sanctuary rescues injured elephants and lets them roam freely in the lush green mountains.

                In the morning, the volunteers prepared special vitamin nutrition balls using cooked sticky rice, crushed bananas, tamarind, and dietary supplements for older elephants whose teeth were worn down. Nong Ploy was assigned to care for 'Kham Mee', a gentle 52-year-old female elephant who was rescued from an illegal logging site five years ago.

                After lunch, the most exciting activity began: the mud spa! Elephants splash wet river mud all over their thick skin because the mud acts as a natural sunscreen and insect repellent against biting flies. Nong Ploy and her brother helped splash cooling water from the river onto Kham Mee's back. 

                "Elephants are highly intelligent and emotional creatures," the head veterinarian explained. "They remember kindness forever." As the sun set behind Doi Suthep, Nong Ploy realized that real conservation means protecting animals with respect, not using them for entertainment.
            """.trimIndent(),
            tags = listOf("Reading", "Comprehension", "Environment", "Animals", "Pinterest"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.READING_PASSAGE_MCQ, "What makes this sanctuary different from traditional tourist camps?", "อะไรทำให้ศูนย์อนุรักษ์แห่งนี้แตกต่างจากปางช้างแบบดั้งเดิม?", listOf("Elephants roam freely without carrying heavy chairs", "Elephants perform circus acrobatics", "Elephants are kept in small cages", "Visitors can ride elephants up the mountain"), "Elephants roam freely without carrying heavy chairs", 0, "The passage explicitly states elephants roam freely and do not carry heavy chairs.", "ในบทความระบุว่าช้างเดินได้อย่างอิสระและไม่ต้องแบกเก้าอี้หนัก"),
                WorksheetQuestion(2, QuestionType.READING_PASSAGE_MCQ, "Why did the volunteers prepare soft vitamin balls for older elephants?", "ทำไมอาสาสมัครจึงต้องทำอาหารบดวิตามินให้ช้างสูงวัย?", listOf("Because their teeth are worn down", "Because elephants cannot eat bananas", "Because it makes them run faster", "Because river water is too cold"), "Because their teeth are worn down", 0, "The passage notes their teeth were worn down.", "ฟันของช้างสูงวัยสึกกร่อนจึงต้องทานอาหารบด"),
                WorksheetQuestion(3, QuestionType.READING_PASSAGE_MCQ, "What is the biological purpose of the mud spa for elephants?", "โคลนสปามีประโยชน์ทางชีววิทยาอย่างไรต่อช้าง?", listOf("It acts as a natural sunscreen and protects against insects", "It cleans their clothes", "It makes them sleep immediately", "It changes their skin color permanently"), "It acts as a natural sunscreen and protects against insects", 0, "The mud acts as a natural sunscreen and insect repellent.", "โคลนเป็นกันแดดธรรมชาติและป้องกันแมลงสัตว์กัดต่อย"),
                WorksheetQuestion(4, QuestionType.READING_PASSAGE_MCQ, "What memorable quote did the veterinarian share with the volunteers?", "สัตวแพทย์ได้กล่าวประโยคที่น่าประทับใจว่าอย่างไร?", listOf("Elephants remember kindness forever", "Elephants can fly in dreams", "Elephants prefer living in cities", "Elephants do not like river water"), "Elephants remember kindness forever", 0, "The veterinarian explained: 'They remember kindness forever.'", "ช้างจดจำความเมตตาได้ตลอดไป"),
                WorksheetQuestion(5, QuestionType.READING_PASSAGE_MCQ, "What important lesson did Nong Ploy learn by the end of the trip?", "น้องพลอยได้เรียนรู้บทเรียนสำคัญอะไรจากการไปทัศนศึกษาครั้งนี้?", listOf("Real conservation means respecting animals, not exploiting them for entertainment", "Elephant riding is the best tourist activity", "Bananas are harmful to elephants", "Bangkok has more mountains than Chiang Mai"), "Real conservation means respecting animals, not exploiting them for entertainment", 0, "Real conservation means protecting animals with respect.", "การอนุรักษ์ที่แท้จริงคือการเคารพและปกป้องสัตว์ ไม่ใช่แสวงหาผลประโยชน์")
            )
        ),

        WorksheetItemModel(
            id = "rd_songkran_festival_reading",
            title = "Pinterest Reading Sheet: The Culture of Songkran Water Festival",
            titleTh = "ใบงานการอ่านและวัฒนธรรม: ประเพณีสงกรานต์และวันขึ้นปีใหม่ไทย",
            category = WorksheetCategory.READING,
            gradeLevel = "P.5-M.3",
            sourceStyle = "Pinterest Printable",
            difficulty = "Beginner",
            description = "Bilingual cultural reading passage exploring the spiritual meaning of Songkran, Rod Nam Dum Hua, and community unity.",
            instructionsEn = "Read the cultural text and answer the true/false and multiple choice questions.",
            instructionsTh = "อ่านบทความเกี่ยวกับประเพณีสงกรานต์และตอบคำถามท้ายบทความ",
            badgeIcon = "💦",
            estimatedMinutes = 15,
            totalPoints = 80,
            passageTitle = "Songkran: Splashes of Joy and Traditional Blessings",
            passageText = """
                Songkran is the official Thai Traditional New Year celebrated nationwide from April 13th to 15th every year. The word 'Songkran' originates from the ancient Sanskrit word 'Sankranti', which symbolizes astrological movement and transformation into a fresh calendar year.

                While international tourists know Songkran for its thrilling water fights with water guns on Silom Road and Khao San Road, the holiday holds deep family and religious roots. On the morning of April 13th, Thai families wake up early to make merit at local Buddhist temples by offering fresh food to monks and releasing caged birds and fish back into nature.

                A sacred tradition is 'Song Nam Phra', gently pouring fragrant water infused with jasmine blossoms over sacred Buddha images. Later at home, younger generations perform 'Rod Nam Dum Hua', pouring scented water over the palms of parents and grandparents to show gratitude, ask for forgiveness, and receive auspicious blessings for prosperity and good health.
            """.trimIndent(),
            tags = listOf("Songkran", "Culture", "Reading", "Festivals"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "When is Songkran celebrated throughout Thailand?", "เทศกาลสงกรานต์จัดขึ้นช่วงวันใดของทุกปี?", listOf("April 13th - 15th", "December 31st - January 1st", "November during full moon", "February 14th"), "April 13th - 15th", 0, "Songkran is celebrated from April 13th to 15th.", "จัดขึ้น 13-15 เมษายน"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "What does the Sanskrit root word 'Sankranti' symbolize?", "รากศัพท์สันสกฤตคำว่า 'สังกรานติ' มีความหมายเชิงสัญลักษณ์ว่าอย่างไร?", listOf("Astrological movement and fresh transformation", "Swimming in the ocean", "Cooking spicy food", "Singing loudly in school"), "Astrological movement and fresh transformation", 0, "It represents astrological movement into a fresh year.", "การก้าวผ่านและเปลี่ยนผ่านสู่ปีใหม่"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "What is the purpose of the 'Rod Nam Dum Hua' ceremony?", "พิธีรดน้ำดำหัวมีวัตถุประสงค์เพื่ออะไร?", listOf("To express gratitude, ask forgiveness, and receive elders' blessings", "To win a water pistol race", "To sell jasmine flowers at the market", "To clean the house floor"), "To express gratitude, ask forgiveness, and receive elders' blessings", 0, "To show gratitude and receive auspicious blessings.", "แสดงความกตัญญู ขอขมา และรับพรจากผู้ใหญ่"),
                WorksheetQuestion(4, QuestionType.TRUE_FALSE, "Making merit on Songkran morning often includes offering food to monks.", "การทำบุญในวันสงกรานต์มักรวมถึงการตักบาตรถวายภัตตาหารแด่พระสงฆ์", correctAnswer = "True", correctIndex = 1, explanation = "Yes, families wake up early to offer food to monks at temples.", hintTh = "จริง (True)")
            )
        ),

        // =========================================================================
        // 5. DAILY CONVERSATION ROLEPLAY CARDS (QUIZIZZ & PINTEREST)
        // =========================================================================
        WorksheetItemModel(
            id = "conv_convenience_store_7eleven",
            title = "Pinterest Roleplay Cards: Ordering at 7-Eleven & Supermarkets",
            titleTh = "การ์ดบทสนทนาสถานการณ์: การซื้อของที่ร้านสะดวกซื้อและซูเปอร์มาร์เก็ต",
            category = WorksheetCategory.CONVERSATION,
            gradeLevel = "P.5-M.3",
            sourceStyle = "Pinterest Printable",
            difficulty = "Beginner",
            description = "Practical dialogue cards with essential cash register, heating food, and plastic bag phrases.",
            instructionsEn = "Choose the most polite and natural conversational response for each retail scenario.",
            instructionsTh = "เลือกประโยคสนทนาภาษาอังกฤษที่สุภาพและเป็นธรรมชาติที่สุดในร้านค้า",
            badgeIcon = "🏪",
            estimatedMinutes = 12,
            totalPoints = 80,
            tags = listOf("Shopping", "Conversation", "Roleplay", "Practical English"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Cashier: 'Would you like your toasted sandwich heated up?'\nYou: '_____.'", "พนักงาน: ต้องการให้อุ่นแซนวิชอบร้อนไหมครับ?\nคุณ: ...", listOf("Yes, please. / No, thank you.", "I don't know you.", "Where is my money?", "My name is Somchai."), "Yes, please. / No, thank you.", 0, "Polite acceptance or decline.", "ตอบ Yes, please. หรือ No, thank you. อย่างสุภาพ"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "Cashier: 'Do you have an All-Member phone number for points?'\nYou: '_____.'", "พนักงาน: มีเบอร์สมาชิกสะสมแต้มไหมคะ?\nคุณ: ...", listOf("Yes, it's 081-234-5678.", "I love eating bananas.", "The weather is sunny.", "Turn right at the bank."), "Yes, it's 081-234-5678.", 0, "Providing a phone number or 'No, I don't have one.'", "บอกหมายเลขโทรศัพท์สมาชิก"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "You want to know the price of a green tea bottle.\nYou ask: '_____?'", "คุณต้องการถามราคาชาเขียวขวดนี้ คุณจะถามว่าอย่างไร?", listOf("Excuse me, how much is this green tea?", "Where were you born?", "Do you like playing games?", "What time is it now?"), "Excuse me, how much is this green tea?", 0, "'How much is this...?' is the standard price inquiry.", "How much is this... = อันนี้ราคาเท่าไหร่"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "Cashier: 'That comes to 85 Baht in total.'\nYou hand over 100 Baht and say: '_____.'", "พนักงาน: ทั้งหมด 85 บาทครับ\nคุณยื่นธนบัตร 100 บาทให้พร้อมพูดว่า: ...", listOf("Here you are.", "Give me free food.", "I am very angry.", "See you tomorrow."), "Here you are.", 0, "'Here you are' or 'Here you go' is polite when handing over money.", "Here you are = นี่ครับ/ค่ะ (เวลายื่นของหรือเงินให้)")
            )
        ),

        WorksheetItemModel(
            id = "conv_asking_directions_bts",
            title = "Quizizz Dialogue Sprint: Asking Directions on the BTS Skytrain & MRT",
            titleTh = "ควิซบทสนทนา: การถามทางและเดินทางด้วยรถไฟฟ้า BTS / MRT",
            category = WorksheetCategory.CONVERSATION,
            gradeLevel = "M.1-M.4",
            sourceStyle = "Quizizz Game",
            difficulty = "Intermediate",
            description = "Interactive dialogue quiz covering ticket machines, platform interchange, and exit gates in Bangkok.",
            instructionsEn = "Select the appropriate phrase to successfully navigate public transit.",
            instructionsTh = "เลือกสำนวนภาษาอังกฤษสำหรับการเดินทางด้วยระบบขนส่งสาธารณะ",
            badgeIcon = "🚆",
            estimatedMinutes = 12,
            totalPoints = 80,
            tags = listOf("Transport", "Directions", "Conversation", "Bangkok"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Tourist: 'Excuse me, which platform goes to Siam Station?'\nYou: '_____.'", "นักท่องเที่ยว: ขอโทษนะครับ ชานชาลาไหนไปสถานีสยามครับ?\nคุณ: ...", listOf("Take Platform 1 on the upper level.", "I don't like trains.", "Siam is a big store.", "Yesterday was Sunday."), "Take Platform 1 on the upper level.", 0, "Giving clear platform instructions.", "บอกชานชาลาที่ถูกต้อง"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "You want to buy a single journey ticket to Asok.\nYou say to the staff: '_____.'", "คุณต้องการซื้อตั๋วเที่ยวเดียวไปอโศก คุณจะบอกเจ้าหน้าที่ว่า: ...", listOf("One single ticket to Asok, please.", "I want to sleep on the train.", "How old are you?", "Where is Chiang Mai?"), "One single ticket to Asok, please.", 0, "Polite ticket purchase.", "ขอซื้อตั๋วไปอโศก 1 ใบ"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "A passenger asks: 'Do I need to interchange trains at Phaya Thai to take the Airport Rail Link?'\nYou reply: '_____.'", "ผู้โดยสารถามว่า: ต้องเปลี่ยนขบวนที่พญาไทเพื่อต่อแอร์พอร์ตลิงก์ไหม?\nคุณตอบว่า: ...", listOf("Yes, exit the gates and follow the walkway to the Airport Link.", "No, trains cannot fly.", "Phaya Thai is delicious.", "I have no tickets."), "Yes, exit the gates and follow the walkway to the Airport Link.", 0, "Clear interchange guidance.", "ใช่แล้ว ให้ออกจากทางออกแล้วเดินตามทางเชื่อม")
            )
        ),

        // =========================================================================
        // 6. PUZZLES, CROSSWORDS & WORD SEARCHES (PINTEREST STYLE)
        // =========================================================================
        WorksheetItemModel(
            id = "puz_school_stationery_crossword",
            title = "Pinterest Puzzle Sheet: School Life & Stationery Crossword",
            titleTh = "ใบงานเกมอักษรไขว้ Pinterest: อุปกรณ์การเรียนและชีวิตในโรงเรียน",
            category = WorksheetCategory.PUZZLES,
            gradeLevel = "P.3-P.6",
            sourceStyle = "Pinterest Printable",
            difficulty = "Beginner",
            description = "Charming illustrated crossword clues with letter count hints for primary school students.",
            instructionsEn = "Solve the crossword clues to find the correct stationery and classroom words.",
            instructionsTh = "ไขปริศนาอักษรไขว้ตามคำใบ้เกี่ยวกับอุปกรณ์เครื่องเขียนและห้องเรียน",
            badgeIcon = "✏️",
            estimatedMinutes = 15,
            totalPoints = 80,
            tags = listOf("Crossword", "Stationery", "School", "Puzzles"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.FILL_IN_BLANK, "1. A tool with two sharp blades used to cut paper and craft boards: [SCISSORS]", "กรรไกรตัดกระดาษ (8 ตัวอักษร)", correctAnswer = "SCISSORS", hintTh = "กรรไกร (SCISSORS)"),
                WorksheetQuestion(2, QuestionType.FILL_IN_BLANK, "2. A small rubber block used to remove pencil mistakes: [ERASER]", "ยางลบดินสอ (6 ตัวอักษร)", correctAnswer = "ERASER", hintTh = "ยางลบ (ERASER)"),
                WorksheetQuestion(3, QuestionType.FILL_IN_BLANK, "3. A straight plastic or wooden tool used to measure centimeters and draw straight lines: [RULER]", "ไม้บรรทัด (5 ตัวอักษร)", correctAnswer = "RULER", hintTh = "ไม้บรรทัด (RULER)"),
                WorksheetQuestion(4, QuestionType.FILL_IN_BLANK, "4. A quiet air-conditioned room full of bookshelves where students borrow books: [LIBRARY]", "ห้องสมุด (7 ตัวอักษร)", correctAnswer = "LIBRARY", hintTh = "ห้องสมุด (LIBRARY)"),
                WorksheetQuestion(5, QuestionType.FILL_IN_BLANK, "5. A zippered fabric pouch where you store pens, pencils, and highlighters: [PENCIL CASE]", "กล่องดินสอหรือกระเป๋าดินสอ (10 ตัวอักษร)", correctAnswer = "PENCIL CASE", hintTh = "กล่องดินสอ (PENCIL CASE)")
            )
        ),

        WorksheetItemModel(
            id = "puz_travel_anagram_scramble",
            title = "Quizizz Anagram Blitz: Southeast Asia Travel & Transport Scramble",
            titleTh = "เกมถอดรหัสตัวอักษร: คำศัพท์การเดินทางและคมนาคมในอาเซียน",
            category = WorksheetCategory.PUZZLES,
            gradeLevel = "P.5-M.3",
            sourceStyle = "Quizizz Game",
            difficulty = "Beginner",
            description = "Unscramble mixed letters to reveal famous transport vehicles and travel accessories.",
            instructionsEn = "Rearrange the scrambled letters to spell each travel vocabulary word.",
            instructionsTh = "เรียงตัวอักษรที่สลับตำแหน่งให้เป็นคำศัพท์การเดินทางที่ถูกต้อง",
            badgeIcon = "🔠",
            estimatedMinutes = 10,
            totalPoints = 80,
            tags = listOf("Anagram", "Transport", "Travel", "Spelling"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.FILL_IN_BLANK, "Unscramble: P - A - S - S - P - O - R - T -> [PASSPORT]", "หนังสือเดินทาง (PASSPORT)", correctAnswer = "PASSPORT", hintTh = "หนังสือเดินทาง"),
                WorksheetQuestion(2, QuestionType.FILL_IN_BLANK, "Unscramble: A - I - R - P - O - R - T -> [AIRPORT]", "สนามบิน (AIRPORT)", correctAnswer = "AIRPORT", hintTh = "สนามบิน"),
                WorksheetQuestion(3, QuestionType.FILL_IN_BLANK, "Unscramble: B - I - C - Y - C - L - E -> [BICYCLE]", "จักรยาน (BICYCLE)", correctAnswer = "BICYCLE", hintTh = "จักรยาน"),
                WorksheetQuestion(4, QuestionType.FILL_IN_BLANK, "Unscramble: S - U - I - T - C - A - S - E -> [SUITCASE]", "กระเป๋าเดินทางล้อลาก (SUITCASE)", correctAnswer = "SUITCASE", hintTh = "กระเป๋าเดินทาง"),
                WorksheetQuestion(5, QuestionType.FILL_IN_BLANK, "Unscramble: T - U - K - T - U - K -> [TUKTUK]", "รถสามล้อตุ๊กตุ๊กไทย (TUKTUK)", correctAnswer = "TUKTUK", hintTh = "รถตุ๊กตุ๊ก")
            )
        ),

        // =========================================================================
        // 7. O-NET & TGAT STANDARDIZED EXAM PREPARATION SHEETS
        // =========================================================================
        WorksheetItemModel(
            id = "exam_onet_signboards_notices",
            title = "O-NET & TGAT Exam Sheet: Reading Signboards & Public Notices",
            titleTh = "ใบงานแนวข้อสอบ O-NET/TGAT: ป้ายสัญลักษณ์และประกาศสาธารณะ",
            category = WorksheetCategory.EXAM_PREP,
            gradeLevel = "M.3-M.6",
            sourceStyle = "Pinterest Printable",
            difficulty = "Challenging",
            description = "High-frequency national exam questions analyzing airport signs, safety notices, and hospital warnings.",
            instructionsEn = "Examine each public notice sign and deduce the correct meaning intended by authorities.",
            instructionsTh = "วิเคราะห์ป้ายประกาศและสัญลักษณ์สาธารณะตามแนวข้อสอบ O-NET และ TGAT",
            badgeIcon = "🛑",
            estimatedMinutes = 18,
            totalPoints = 100,
            tags = listOf("O-NET", "TGAT", "Exam Prep", "Signboards"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Sign: 'CAUTION: WET FLOOR. PLEASE WATCH YOUR STEP.'\nWhat should you do?", "ป้ายเตือน: พื้นเปียก โปรดระวังการก้าวเดิน\nคุณควรปฏิบัติตนอย่างไร?", listOf("Walk carefully to avoid slipping and falling", "Run across the floor quickly", "Take off your shoes and jump", "Wash your hands with soap"), "Walk carefully to avoid slipping and falling", 0, "'Caution: Wet Floor' warns pedestrians of slipping hazards.", "เดินด้วยความระมัดระวังเพื่อไม่ให้ลื่นล้ม"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "Library Notice: 'STRICTLY NO FOOD OR DRINKS ALLOWED. CELL PHONES ON SILENT.'\nWhich behavior is permitted?", "ประกาศห้องสมุด: ห้ามนำอาหารเครื่องดื่มเข้า และปิดเสียงโทรศัพท์\nพฤติกรรมใดต่อไปนี้ได้รับอนุญาต?", listOf("Reading a textbook while drinking nothing", "Eating fried chicken at a desk", "Calling a friend loudly on speaker", "Spilling milk tea on books"), "Reading a textbook while drinking nothing", 0, "No food/drinks and quiet reading are required.", "อ่านหนังสือเงียบๆ โดยไม่นำของกินเข้ามา"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "National Park Sign: 'DO NOT FEED THE MONKEYS. KEEP VALUABLES INSIDE BAGS.'\nWhy is this rule enforced?", "ป้ายอุทยาน: ห้ามให้อาหารลิง และเก็บของมีค่าในกระเป๋า\nทำไมจึงต้องมีกฎนี้?", listOf("To prevent wildlife aggression and avoid theft of personal items", "Because monkeys eat plastic money", "To make monkeys starve", "Because tourists must feed tigers instead"), "To prevent wildlife aggression and avoid theft of personal items", 0, "Feeding wild animals changes behavior and causes attacks.", "ป้องกันลิงแย่งชิงของและรักษาสวัสดิภาพสัตว์ป่า"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "Airport Gate Screen: 'FINAL BOARDING CALL: FLIGHT TG102 TO CHIANG MAI. GATE CLOSING IN 5 MINUTES.'\nWhat does this mean?", "หน้าจอเกตสนามบิน: เรียกขึ้นเครื่องครั้งสุดท้าย เที่ยวบิน TG102 ประตูจะปิดใน 5 นาที\nหมายความว่าอย่างไร?", listOf("Passengers must proceed to the gate immediately to avoid missing the flight", "The flight has been canceled until tomorrow", "The plane has already landed in Chiang Mai", "Tickets are half price at the gate"), "Passengers must proceed to the gate immediately to avoid missing the flight", 0, "Final call means immediate boarding is required.", "ผู้โดยสารต้องรีบไปที่ประตูขึ้นเครื่องทันที"),
                WorksheetQuestion(5, QuestionType.MULTIPLE_CHOICE, "Hospital Corridor: 'QUIET PLEASE: INTENSIVE CARE UNIT (ICU).'\nWhat is required of visitors?", "ทางเดินโรงพยาบาล: โปรดงดใช้เสียง หอผู้ป่วยวิกฤต (ICU)\nผู้มาเยือนต้องทำอย่างไร?", listOf("Keep conversations quiet and respect resting patients", "Play loud music on headphones", "Shout to doctors across the hall", "Run down the hallway"), "Keep conversations quiet and respect resting patients", 0, "Silence is essential in critical medical areas.", "งดส่งเสียงดังเพื่อไม่รบกวนผู้ป่วยวิกฤต")
            )
        ),

        WorksheetItemModel(
            id = "exam_tgat_conversational_cloze",
            title = "TGAT Exam Master: Conversational Cloze & Polite Idioms",
            titleTh = "แนวข้อสอบ TGAT English: บทสนทนาอุดช่องว่างและสำนวนภาษาอังกฤษ",
            category = WorksheetCategory.EXAM_PREP,
            gradeLevel = "M.4-M.6",
            sourceStyle = "Quizizz Game",
            difficulty = "Challenging",
            description = "High school university admissions prep focusing on natural colloquial expressions, offers, and polite disagreements.",
            instructionsEn = "Select the most appropriate communicative utterance for each collegiate dialogue.",
            instructionsTh = "เลือกบทสนทนาภาษาอังกฤษที่สละสลวย ถูกกาลเทศะ และตรงตามบริบทข้อสอบเข้ามหาวิทยาลัย",
            badgeIcon = "🎓",
            estimatedMinutes = 15,
            totalPoints = 80,
            tags = listOf("TGAT", "Exam Prep", "University Entrance", "Idioms"),
            questions = listOf(
                WorksheetQuestion(1, QuestionType.MULTIPLE_CHOICE, "Student A: 'I'm feeling really stressed about the upcoming university interview.'\nStudent B: '_____! You've practiced every day and you'll do great!'", "นักเรียน A: ฉันรู้สึกเครียดมากกับการสัมภาษณ์เข้ามหาวิทยาลัย\nนักเรียน B: ...! เธอฝึกซ้อมมาทุกวัน เธอต้องทำได้ดีแน่นอน!", listOf("Keep your chin up and believe in yourself", "Give up now", "I don't care at all", "Why are you crying"), "Keep your chin up and believe in yourself", 0, "'Keep your chin up' is an idiom of encouragement.", "Keep your chin up = สู้ๆ นะ เชิดหน้าเข้าไว้"),
                WorksheetQuestion(2, QuestionType.MULTIPLE_CHOICE, "Teacher: 'Would you mind helping me carry these graded exam papers to the staff room?'\nStudent: '_____.'", "ครู: รบกวนช่วยครูถือปึกข้อสอบไปห้องพักครูหน่อยได้ไหมจ๊ะ?\nนักเรียน: ...", listOf("Not at all, Ajarn! I'd be happy to help.", "Yes, I hate carrying papers.", "Go carry them alone.", "Where is my dinner?"), "Not at all, Ajarn! I'd be happy to help.", 0, "Answering 'Would you mind...?' with 'Not at all' agrees politely to help.", "ตอบ Not at all (ไม่รังเกียจเลยครับ ยินดีช่วยครับ)"),
                WorksheetQuestion(3, QuestionType.MULTIPLE_CHOICE, "Friend A: 'Shall we split the restaurant bill evenly?'\nFriend B: 'No need! It's my birthday today, so _____.'", "เพื่อน A: พวกเราหารค่าอาหารกันเท่าๆ กันไหม?\nเพื่อน B: ไม่ต้องหรอก! วันนี้วันเกิดฉันเอง ดังนั้น...", listOf("it's on me / my treat", "you pay double", "give me your wallet", "let's run away"), "it's on me / my treat", 0, "'It's on me' or 'My treat' means I will pay for everyone.", "it's on me / my treat = ฉันเลี้ยงเอง"),
                WorksheetQuestion(4, QuestionType.MULTIPLE_CHOICE, "Colleague: 'I sincerely apologize for the unexpected delay in submitting the lesson plan.'\nSupervisor: '_____.'", "เพื่อนร่วมงาน: ขออภัยเป็นอย่างยิ่งที่ส่งแผนการสอนล่าช้าครับ\nหัวหน้าหมวด: ...", listOf("Don't worry about it. Just ensure it's completed by tomorrow noon.", "You are fired immediately.", "I will never forgive you.", "Who are you?"), "Don't worry about it. Just ensure it's completed by tomorrow noon.", 0, "Polite professional acceptance of apology.", "ตอบรับคำขอโทษอย่างมืออาชีพ")
            )
        )
    )

    fun getWorksheetById(id: String): WorksheetItemModel? {
        return allWorksheets.find { it.id == id }
    }

    fun getWorksheetsByCategory(category: WorksheetCategory): List<WorksheetItemModel> {
        if (category == WorksheetCategory.ALL) return allWorksheets
        return allWorksheets.filter { it.category == category }
    }

    fun searchWorksheets(query: String, category: WorksheetCategory = WorksheetCategory.ALL, grade: String? = null): List<WorksheetItemModel> {
        val base = if (category == WorksheetCategory.ALL) allWorksheets else allWorksheets.filter { it.category == category }
        return base.filter { ws ->
            val matchQuery = query.isBlank() || ws.title.contains(query, ignoreCase = true) ||
                    ws.titleTh.contains(query, ignoreCase = true) ||
                    ws.tags.any { it.contains(query, ignoreCase = true) } ||
                    ws.description.contains(query, ignoreCase = true)
            val matchGrade = grade == null || grade.isBlank() || grade == "All Grades" || ws.gradeLevel.contains(grade, ignoreCase = true)
            matchQuery && matchGrade
        }
    }
}
