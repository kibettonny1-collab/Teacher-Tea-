package com.example.util

import java.util.Locale
import kotlin.math.max
import kotlin.math.min

enum class PronunciationRating(
    val title: String,
    val titleTh: String,
    val stars: Int,
    val colorHex: Long,
    val badgeBgHex: Long
) {
    PERFECT(
        title = "🌟 Perfect Pronunciation",
        titleTh = "การออกเสียงยอดเยี่ยม ชัดเจนมาก",
        stars = 3,
        colorHex = 0xFF10B981, // Emerald Green
        badgeBgHex = 0xFFD1FAE5
    ),
    GOOD(
        title = "👍 Good & Clear",
        titleTh = "ออกเสียงดี ชัดเจนเข้าใจได้",
        stars = 2,
        colorHex = 0xFF1E3A8A, // Navy / Royal Blue
        badgeBgHex = 0xFFDBEAFE
    ),
    FAIR(
        title = "⚠️ Needs Practice",
        titleTh = "พอเข้าใจได้ ควรปรับเสียงสระ/พยางค์ท้าย",
        stars = 1,
        colorHex = 0xFFD97706, // Amber Gold
        badgeBgHex = 0xFFFEF3C7
    ),
    NEEDS_WORK(
        title = "❌ Mispronounced",
        titleTh = "ออกเสียงไม่ถูกต้อง ลองฟังแล้วพูดใหม่",
        stars = 0,
        colorHex = 0xFFEF4444, // Coral Red
        badgeBgHex = 0xFFFEE2E2
    )
}

data class PronunciationResult(
    val targetWord: String,
    val spokenTranscript: String,
    val scorePercentage: Int, // 0 - 100
    val scorePoints: Float, // 0.0 - 10.0 (for Gradebook)
    val rating: PronunciationRating,
    val feedbackEn: String,
    val feedbackTh: String,
    val syllables: String,
    val ipa: String,
    val isPassed: Boolean,
    val audioTip: String
)

data class CuratedVocabWord(
    val en: String,
    val th: String,
    val syllables: String,
    val ipa: String,
    val example: String,
    val phoneticTip: String,
    val grade: String
)

object PronunciationEvaluator {

    val CURATED_VOCAB_BANK: List<CuratedVocabWord> = listOf(
        // M.1 Foundation Words
        CuratedVocabWord(
            en = "Bicycle",
            th = "จักรยาน",
            syllables = "Bi · cy · cle",
            ipa = "/ˈbaɪ.sɪ.kəl/",
            example = "I ride my bicycle to school every morning.",
            phoneticTip = "Stress the first syllable: 'BY-sih-kuhl'. End softly with the /kəl/ sound.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Delicious",
            th = "อร่อย",
            syllables = "De · li · cious",
            ipa = "/dɪˈlɪʃ.əs/",
            example = "Pad Thai and Tom Yum are very delicious.",
            phoneticTip = "Stress the second syllable: 'duh-LISH-uhs'. Clearly enunciate the ending /ʃəs/ (sh-us) sound.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Library",
            th = "ห้องสมุด",
            syllables = "Li · brar · y",
            ipa = "/ˈlaɪ.brər.i/",
            example = "Students borrow books from the school library.",
            phoneticTip = "Say 'LY-breh-ree'. Do not drop the first 'r' sound.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Yesterday",
            th = "เมื่อวานนี้",
            syllables = "Yes · ter · day",
            ipa = "/ˈjes.tə.deɪ/",
            example = "Yesterday we learned English irregular verbs.",
            phoneticTip = "Clean 'y' sound: 'YES-ter-day'. Keep all 3 syllables distinct.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Festival",
            th = "เทศกาล",
            syllables = "Fes · ti · val",
            ipa = "/ˈfes.tɪ.vəl/",
            example = "Songkran is the biggest water festival in Thailand.",
            phoneticTip = "Bite lower lip for the 'v' sound: 'FES-ti-vuhl'.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Excited",
            th = "ตื่นเต้น",
            syllables = "Ex · cit · ed",
            ipa = "/ɪkˈsaɪ.tɪd/",
            example = "The students are excited about the school trip.",
            phoneticTip = "Clear ending /ɪd/ sound: 'ik-SY-tid'.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Hospital",
            th = "โรงพยาบาล",
            syllables = "Hos · pi · tal",
            ipa = "/ˈhɒs.pɪ.təl/",
            example = "The doctor works at the central hospital.",
            phoneticTip = "Pronounce the middle 's': 'HOS-pi-tuhl'.",
            grade = "M.1"
        ),
        CuratedVocabWord(
            en = "Vegetable",
            th = "ผัก",
            syllables = "Veg · e · ta · ble",
            ipa = "/ˈvedʒ.tə.bəl/",
            example = "Eating green vegetables keeps you healthy.",
            phoneticTip = "Usually 3 syllables in natural English: 'VEJ-tuh-buhl'.",
            grade = "M.1"
        ),

        // M.4 Academic & Culture Words
        CuratedVocabWord(
            en = "Environment",
            th = "สิ่งแวดล้อม",
            syllables = "En · vi · ron · ment",
            ipa = "/ɪnˈvaɪ.rən.mənt/",
            example = "We must reduce plastic waste to protect our environment.",
            phoneticTip = "Pronounce 'in-VY-ron-muhnt'. Lip-to-teeth /v/ and crisp ending /nt/.",
            grade = "M.4"
        ),
        CuratedVocabWord(
            en = "Technology",
            th = "เทคโนโลยี",
            syllables = "Tech · nol · o · gy",
            ipa = "/tekˈnɒl.ə.dʒi/",
            example = "Modern technology helps students learn English faster.",
            phoneticTip = "Stress the second syllable: 'tek-NOL-uh-jee'.",
            grade = "M.4"
        ),
        CuratedVocabWord(
            en = "Opportunity",
            th = "โอกาส",
            syllables = "Op · por · tu · ni · ty",
            ipa = "/ˌɒp.əˈtʃuː.nə.ti/",
            example = "Studying English gives you great career opportunities.",
            phoneticTip = "Stress the 3rd syllable: 'op-er-TOO-nuh-tee'.",
            grade = "M.4"
        ),
        CuratedVocabWord(
            en = "Traditional",
            th = "ตามประเพณี / ดั้งเดิม",
            syllables = "Tra · di · tion · al",
            ipa = "/trəˈdɪʃ.ən.əl/",
            example = "Thai dance is a beautiful traditional art form.",
            phoneticTip = "Clear 'tr' blend and 'shun-uhl' ending: 'truh-DISH-uh-nuhl'.",
            grade = "M.4"
        ),
        CuratedVocabWord(
            en = "Transportation",
            th = "การคมนาคมขนส่ง",
            syllables = "Trans · por · ta · tion",
            ipa = "/ˌtræn.spɔːˈteɪ.ʃən/",
            example = "The BTS Skytrain is convenient public transportation.",
            phoneticTip = "Four syllables: 'tran-spor-TAY-shun'.",
            grade = "M.4"
        ),

        // M.5 STEM & Society
        CuratedVocabWord(
            en = "Sustainable",
            th = "ยั่งยืน",
            syllables = "Sus · tain · a · ble",
            ipa = "/səˈsteɪ.nə.bəl/",
            example = "Solar energy provides sustainable power for cities.",
            phoneticTip = "Stress 'tain': 'suh-STAY-nuh-buhl'.",
            grade = "M.5"
        ),
        CuratedVocabWord(
            en = "Communication",
            th = "การสื่อสาร",
            syllables = "Com · mu · ni · ca · tion",
            ipa = "/kəˌmjuː.nɪˈkeɪ.ʃən/",
            example = "Good communication is essential for global business.",
            phoneticTip = "Five syllables with strong 'CA': 'kuh-myoo-ni-KAY-shun'.",
            grade = "M.5"
        ),
        CuratedVocabWord(
            en = "Biodiversity",
            th = "ความหลากหลายทางชีวภาพ",
            syllables = "Bi · o · di · ver · si · ty",
            ipa = "/ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/",
            example = "Tropical rainforests in Thailand have rich biodiversity.",
            phoneticTip = "Six syllables: 'by-oh-dy-VER-suh-tee'.",
            grade = "M.5"
        ),

        // M.6 Advanced Fluency & University Prep
        CuratedVocabWord(
            en = "Collaboration",
            th = "การร่วมมือกัน",
            syllables = "Col · lab · o · ra · tion",
            ipa = "/kəˌlæb.əˈreɪ.ʃən/",
            example = "International collaboration is key to scientific progress.",
            phoneticTip = "Five syllables: 'kuh-lab-uh-RAY-shun'.",
            grade = "M.6"
        ),
        CuratedVocabWord(
            en = "Infrastructure",
            th = "โครงสร้างพื้นฐาน",
            syllables = "In · fra · struc · ture",
            ipa = "/ˈɪn.frəˌstrʌk.tʃər/",
            example = "High-speed rail improves national infrastructure.",
            phoneticTip = "Four syllables: 'IN-fruh-struhk-cher'.",
            grade = "M.6"
        ),
        CuratedVocabWord(
            en = "Extraordinary",
            th = "ไม่ธรรมดา / ยอดเยี่ยม",
            syllables = "Ex · traor · di · nar · y",
            ipa = "/ɪkˈstrɔː.dɪn.ər.i/",
            example = "The students achieved extraordinary scores on their exams.",
            phoneticTip = "Natural contraction: 'ik-STROR-din-er-ee'.",
            grade = "M.6"
        )
    )

    fun getPhoneticsForWord(word: String): Pair<String, String> {
        val clean = word.trim().lowercase(Locale.ENGLISH)
        val match = CURATED_VOCAB_BANK.find { it.en.equals(clean, ignoreCase = true) }
        if (match != null) {
            return match.syllables to match.ipa
        }

        // Generic syllable breakdown fallback
        val syllables = generateSyllableEstimate(word)
        val ipa = "/${word.lowercase(Locale.ENGLISH)}/"
        return syllables to ipa
    }

    private fun generateSyllableEstimate(word: String): String {
        val clean = word.trim()
        if (clean.length <= 4) return clean

        val vowels = "aeiouyAEIOUY"
        val parts = mutableListOf<String>()
        var current = StringBuilder()

        for (i in clean.indices) {
            current.append(clean[i])
            if (i > 1 && i < clean.length - 2 && vowels.contains(clean[i]) && !vowels.contains(clean[i + 1])) {
                parts.add(current.toString())
                current = StringBuilder()
            }
        }
        if (current.isNotEmpty()) {
            parts.add(current.toString())
        }

        return if (parts.size > 1) parts.joinToString(" · ") else clean
    }

    fun evaluate(
        targetWord: String,
        spokenTranscript: String,
        targetExample: String? = null
    ): PronunciationResult {
        val normalizedTarget = normalizeText(targetWord)
        val normalizedSpoken = normalizeText(spokenTranscript)

        val phoneticInfo = getPhoneticsForWord(targetWord)
        val syllables = phoneticInfo.first
        val ipa = phoneticInfo.second

        if (normalizedSpoken.isBlank()) {
            return PronunciationResult(
                targetWord = targetWord,
                spokenTranscript = "(No speech detected)",
                scorePercentage = 0,
                scorePoints = 0f,
                rating = PronunciationRating.NEEDS_WORK,
                feedbackEn = "No speech was detected. Tap the microphone, speak clearly, and hold the phone close.",
                feedbackTh = "ตรวจไม่พบเสียงพูด กรุณากดไมโครโฟนแล้วออกเสียงใหม่อีกครั้งให้ชัดเจน",
                syllables = syllables,
                ipa = ipa,
                isPassed = false,
                audioTip = "Tap 'Listen to Native Pronunciation' to hear the word before speaking."
            )
        }

        // 1. Check exact word match
        if (normalizedSpoken == normalizedTarget) {
            return PronunciationResult(
                targetWord = targetWord,
                spokenTranscript = spokenTranscript,
                scorePercentage = 100,
                scorePoints = 10.0f,
                rating = PronunciationRating.PERFECT,
                feedbackEn = "🌟 Flawless pronunciation! Every syllable, consonant, and stress was recognized with 100% accuracy.",
                feedbackTh = "ยอดเยี่ยมมาก! สำเนียงและการออกเสียงถูกต้องชัดเจน 100%",
                syllables = syllables,
                ipa = ipa,
                isPassed = true,
                audioTip = "Great mastery! Ready for the next vocabulary word."
            )
        }

        // 2. Check if spoken transcript contains the target word as a standalone word
        val spokenWords = normalizedSpoken.split("\\s+".toRegex())
        val containsTargetWord = spokenWords.any { it == normalizedTarget }
        if (containsTargetWord) {
            return PronunciationResult(
                targetWord = targetWord,
                spokenTranscript = spokenTranscript,
                scorePercentage = 95,
                scorePoints = 9.5f,
                rating = PronunciationRating.PERFECT,
                feedbackEn = "🌟 Excellent! The target word was clearly articulated within your sentence.",
                feedbackTh = "ดีเยี่ยมมาก! ได้ยินคำศัพท์หลักอย่างชัดเจนและถูกต้อง",
                syllables = syllables,
                ipa = ipa,
                isPassed = true,
                audioTip = "Pronunciation was crystal clear to the speech recognizer."
            )
        }

        // 3. Find the closest matching word in multi-word utterances
        var bestSimilarity = 0.0
        var bestWord = normalizedSpoken
        for (w in spokenWords) {
            val sim = calculateSimilarity(normalizedTarget, w)
            if (sim > bestSimilarity) {
                bestSimilarity = sim
                bestWord = w
            }
        }

        // Overall similarity percentage (0..100)
        val scorePercent = (bestSimilarity * 100).toInt().coerceIn(0, 100)
        val scorePoints = (scorePercent / 10f)

        // Specific Phonetic & Thai-English Diagnostic Checks
        val diagnostics = analyzePhoneticNuances(normalizedTarget, bestWord)

        val rating: PronunciationRating
        val feedbackEn: String
        val feedbackTh: String
        val isPassed: Boolean

        when {
            scorePercent >= 90 -> {
                rating = PronunciationRating.PERFECT
                feedbackEn = "🌟 Outstanding accuracy (${scorePercent}%)! Speech recognizer captured your pronunciation accurately."
                feedbackTh = "ยอดเยี่ยมมาก (${scorePercent}%) ออกเสียงได้ใกล้เคียงเจ้าของภาษา"
                isPassed = true
            }
            scorePercent >= 75 -> {
                rating = PronunciationRating.GOOD
                feedbackEn = "👍 Great job (${scorePercent}%)! Clear and understandable. ${diagnostics.first}"
                feedbackTh = "ออกเสียงได้ดี (${scorePercent}%) ฟังเข้าใจได้ง่าย ${diagnostics.second}"
                isPassed = true
            }
            scorePercent >= 50 -> {
                rating = PronunciationRating.FAIR
                feedbackEn = "⚠️ Fair attempt (${scorePercent}%). Recognizer heard '$bestWord'. ${diagnostics.first}"
                feedbackTh = "พอเข้าใจได้ (${scorePercent}%) ระบบจับเสียงได้ว่า '$bestWord' ${diagnostics.second}"
                isPassed = false
            }
            else -> {
                rating = PronunciationRating.NEEDS_WORK
                feedbackEn = "❌ Mispronounced (${scorePercent}%). Heard '$bestWord' instead of '$targetWord'. ${diagnostics.first}"
                feedbackTh = "ออกเสียงไม่ตรง (${scorePercent}%) ได้ยินเป็น '$bestWord' ${diagnostics.second}"
                isPassed = false
            }
        }

        val curatedTip = CURATED_VOCAB_BANK.find { it.en.equals(targetWord, ignoreCase = true) }?.phoneticTip
            ?: "Listen to the audio guide at slow speed, then emphasize the stressed syllable."

        return PronunciationResult(
            targetWord = targetWord,
            spokenTranscript = spokenTranscript,
            scorePercentage = scorePercent,
            scorePoints = scorePoints,
            rating = rating,
            feedbackEn = feedbackEn,
            feedbackTh = feedbackTh,
            syllables = syllables,
            ipa = ipa,
            isPassed = isPassed,
            audioTip = curatedTip
        )
    }

    private fun analyzePhoneticNuances(target: String, spoken: String): Pair<String, String> {
        // Thai EFL Common Nuance 1: Dropped final consonant -s/-t/-d/-k
        if (target.endsWith("s") && !spoken.endsWith("s")) {
            return "Make sure to pronounce the final '-s' sound clearly." to "อย่าลืมออกเสียงลมท้ายพยางค์ '-s' ให้ชัดเจน"
        }
        if (target.endsWith("ed") && !spoken.endsWith("ed") && !spoken.endsWith("t") && !spoken.endsWith("d")) {
            return "Emphasize the '-ed' ending clearly." to "เน้นเสียงลงท้าย '-ed' ให้ชัดเจน"
        }
        if (target.endsWith("tion") && !spoken.endsWith("tion") && !spoken.endsWith("shun")) {
            return "Pronounce the ending '-tion' as 'shun'." to "ออกเสียงพยางค์ท้าย '-tion' เป็นเสียง 'เชิ่น/ชัน'"
        }

        // Thai EFL Common Nuance 2: /v/ vs /w/
        if (target.contains("v") && !spoken.contains("v")) {
            return "Place top teeth gently on your lower lip for the 'V' sound." to "แตะฟันบนที่ริมฝีปากล่างเพื่อออกเสียง 'V'"
        }

        // Thai EFL Common Nuance 3: /th/ sound
        if (target.contains("th") && !spoken.contains("th")) {
            return "Put your tongue between your teeth for the 'TH' sound." to "วางปลายลิ้นแตะระหว่างฟันหน้าเมื่อออกเสียง 'TH'"
        }

        // Thai EFL Common Nuance 4: /r/ vs /l/
        if (target.contains("r") && spoken.contains("l")) {
            return "Curl your tongue back without touching the roof of your mouth for the 'R' sound." to "ห่อลิ้นไม่แตะเพดานปากสำหรับเสียง 'R' ไม่ให้กลายเป็น 'L'"
        }

        return "Focus on syllable cadence and vowel clarity." to "เน้นจังหวะพยางค์และการลงน้ำหนักเสียงให้ชัดเจน"
    }

    private fun normalizeText(text: String): String {
        return text.lowercase(Locale.ENGLISH)
            .replace(Regex("[^a-z0-9\\s]"), "")
            .trim()
    }

    private fun calculateSimilarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0

        val maxLen = max(s1.length, s2.length)
        val distance = levenshteinDistance(s1, s2)
        val similarity = (maxLen - distance).toDouble() / maxLen.toDouble()

        // Give a bonus if one is a substring of another
        if (s1.contains(s2) || s2.contains(s1)) {
            return max(similarity, 0.75)
        }

        return max(0.0, similarity)
    }

    private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength]
    }
}
