package com.example.data.powerpoint

enum class PptCategory(val code: String, val titleEn: String, val titleTh: String, val icon: String) {
    ALL("all", "All Slide Decks", "สไลด์ทั้งหมด", "📽️"),
    GAMES("games", "PPT Classroom Games", "สไลด์เกมในห้องเรียน", "🎮"),
    GRAMMAR("grammar", "Grammar & Tenses Decks", "สไลด์ไวยากรณ์ & Tenses", "📝"),
    WARM_UP("warm_up", "Warm-Up & Speaking", "สไลด์ชวนคุย & Warm-Up", "💬"),
    VOCAB("vocab", "Vocab & Phonics", "คำศัพท์และโฟนิกส์", "🔤"),
    ONET_TGAT("exam", "O-NET / TGAT Strategy", "เทคนิคข้อสอบ O-NET/TGAT", "🎯"),
    CULTURE_TRAVEL("culture", "Bangkok & Travel ESL", "ภาษาอังกฤษท่องเที่ยว", "🇹🇭")
}

enum class SlideLayoutType {
    TITLE_HERO,
    GRAMMAR_RULE,
    MYSTERY_BOX,
    JEOPARDY_MCQ,
    SPOT_MISTAKE,
    WOULD_YOU_RATHER,
    DIALOGUE_ROLEPLAY,
    TABOO_GUESS,
    SUMMARY_HOMEWORK
}

data class MysteryBoxItem(
    val boxNumber: Int,
    val promptEn: String,
    val promptTh: String,
    val rewardType: String, // "points" | "bomb" | "double" | "star"
    val points: Int,
    val answer: String
)

data class PowerPointSlide(
    val slideNumber: Int,
    val layoutType: SlideLayoutType,
    val title: String,
    val subtitle: String? = null,
    val headline: String? = null,
    val bodyEn: String = "",
    val bodyTh: String = "",
    val bulletPoints: List<String> = emptyList(),
    val options: List<String> = emptyList(),
    val correctAnswer: String? = null,
    val explanationEn: String? = null,
    val explanationTh: String? = null,
    val pointsValue: Int = 100,
    val mysteryBoxes: List<MysteryBoxItem>? = null,
    val optionA: String? = null,
    val optionB: String? = null,
    val tabooForbiddenWords: List<String> = emptyList(),
    val dialogueLines: List<Pair<String, String>> = emptyList(), // Speaker -> Text
    val teacherNotesEn: String = "",
    val teacherNotesTh: String = "",
    val visualEmoji: String = "✨"
)

data class PowerPointDeckModel(
    val id: String,
    val title: String,
    val titleTh: String,
    val category: PptCategory,
    val gradeLevel: String, // e.g. "P.1-P.3", "P.4-P.6", "M.1-M.3", "M.4-M.6"
    val difficulty: String, // "Beginner" | "Intermediate" | "Challenging"
    val totalSlides: Int,
    val estimatedMinutes: Int = 20,
    val badgeIcon: String,
    val sourceAttribution: String = "iSLCollective Community ESL",
    val description: String,
    val tags: List<String> = emptyList(),
    val slides: List<PowerPointSlide>
)
