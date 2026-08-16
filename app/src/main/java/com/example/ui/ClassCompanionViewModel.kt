package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.*
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ClassCompanionRepository
import com.example.speech.SpeechManager
import com.example.ui.components.LineNotificationAlertState
import com.example.ui.components.LineSimulationScenario
import com.example.util.CuratedVocabWord
import com.example.util.ParsedStudentInfo
import com.example.util.PronunciationEvaluator
import com.example.util.PronunciationRating
import com.example.util.PronunciationResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClassCompanionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClassCompanionRepository
    val speechManager: SpeechManager

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ClassCompanionRepository(db)
        speechManager = SpeechManager(application)

        viewModelScope.launch {
            repository.seedSampleDataIfNeeded()
        }
    }

    val allClasses: StateFlow<List<ClassRoomEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettingsEntity?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLogs: StateFlow<List<ActivityLogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedClassId = MutableStateFlow<String?>(null)
    val selectedClassId: StateFlow<String?> = _selectedClassId.asStateFlow()

    val activeClass: StateFlow<ClassRoomEntity?> = combine(allClasses, _selectedClassId, userSettings) { classes, selectedId, settings ->
        val targetId = selectedId ?: settings?.activeClassId ?: classes.firstOrNull()?.id
        classes.find { it.id == targetId } ?: classes.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val studentsInActiveClass: StateFlow<List<StudentEntity>> = activeClass
        .flatMapLatest { cls ->
            if (cls != null) repository.getStudentsByClass(cls.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<StudentEntity>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val inboxInActiveClass: StateFlow<List<InboxMessageEntity>> = activeClass
        .flatMapLatest { cls ->
            if (cls != null) repository.getMessagesByClass(cls.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val vocabInActiveClass: StateFlow<List<VocabWordEntity>> = activeClass
        .flatMapLatest { cls ->
            if (cls != null) repository.getVocabByClass(cls.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val assessmentsInActiveClass: StateFlow<List<AssessmentEntity>> = activeClass
        .flatMapLatest { cls ->
            if (cls != null) repository.getAssessmentsByClass(cls.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssessments: StateFlow<List<AssessmentEntity>> = repository.getAllAssessments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allScores: StateFlow<List<StudentScoreEntity>> = repository.getAllScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation & UI State
    val currentScreen = MutableStateFlow("home") // "home", "companion", "students", "vocab", "assess", "speaktest", "reports", "settings"
    val lessonInputText = MutableStateFlow("go, went, gone / eat, ate, eaten / see, saw, seen — irregular past tense verbs & daily routines")
    val selectedActivityType = MutableStateFlow("standard")
    val isGenerating = MutableStateFlow(false)
    val toastMessage = MutableSharedFlow<String>()

    // Activity Generation Outputs
    val standardTestResult = MutableStateFlow<StandardTestResult?>(null)
    val gamifiedQuizText = MutableStateFlow<String?>(null)
    val flashCardsResult = MutableStateFlow<List<FlashCardItem>>(emptyList())
    val speakingListeningQuestions = MutableStateFlow<List<SpeakingListeningQuestion>>(emptyList())
    val slQuestionIndex = MutableStateFlow(0)
    val slStudentAnswers = MutableStateFlow<Map<Int, Pair<String, Boolean>>>(emptyMap()) // qId -> (transcript, isCorrect)
    val sentenceBuilderItems = MutableStateFlow<List<SentenceBuilderItem>>(emptyList())
    val worksheetItems = MutableStateFlow<List<WorksheetItem>>(emptyList())
    val storyResult = MutableStateFlow<StoryResult?>(null)
    val wordSearchResult = MutableStateFlow<WordSearchResult?>(null)
    val memoryCards = MutableStateFlow<List<MemoryMatchCard>>(emptyList())
    val memoryFlippedIds = MutableStateFlow<List<String>>(emptyList())
    val memoryMatchedPairIds = MutableStateFlow<Set<Int>>(emptySet())
    val wordBankEntries = MutableStateFlow<List<WordBankEntry>>(emptyList())
    val listeningResult = MutableStateFlow<ListeningScriptResult?>(null)
    val roleplayChat = MutableStateFlow<List<ConversationMessage>>(emptyList())

    // Additional Wordwall-Style Interactive Game State
    val matchUpResult = MutableStateFlow<MatchUpResult?>(null)
    val quizRaceResult = MutableStateFlow<QuizRaceResult?>(null)
    val anagramResult = MutableStateFlow<AnagramResult?>(null)
    val trueFalseResult = MutableStateFlow<TrueFalseResult?>(null)
    val openBoxResult = MutableStateFlow<OpenBoxResult?>(null)

    // LINE Simulation & Heads-Up Alert State
    val lineNotificationAlert = MutableStateFlow<LineNotificationAlertState?>(null)
    val showLineSimulator = MutableStateFlow(false)
    val lineSimulatorScenario = MutableStateFlow(LineSimulationScenario.HOMEWORK)
    val lineSimulatorTargetStudent = MutableStateFlow<StudentEntity?>(null)

    // CameraX QR Code Scanner State
    val showCameraQrScanner = MutableStateFlow(false)

    // Oral Exam & STT Pronunciation State
    val oralExamSelectedStudent = MutableStateFlow<StudentEntity?>(null)
    val oralExamWordList = MutableStateFlow<List<VocabWordEntity>>(emptyList())
    val oralExamCurrentIndex = MutableStateFlow(0)
    val oralExamResults = MutableStateFlow<Map<String, PronunciationResult>>(emptyMap()) // vocabId/word -> PronunciationResult
    val isOralExamActive = MutableStateFlow(false)
    val isOralExamCompleted = MutableStateFlow(false)
    val oralExamSelectedGradeDeck = MutableStateFlow("M.1")

    fun openCameraQrScanner() {
        showCameraQrScanner.value = true
    }

    fun closeCameraQrScanner() {
        showCameraQrScanner.value = false
    }

    fun openLineSimulator(
        scenario: LineSimulationScenario = LineSimulationScenario.HOMEWORK,
        targetStudent: StudentEntity? = null
    ) {
        lineSimulatorScenario.value = scenario
        lineSimulatorTargetStudent.value = targetStudent
        showLineSimulator.value = true
    }

    fun closeLineSimulator() {
        showLineSimulator.value = false
    }

    fun triggerLineHeadsUpAlert(alert: LineNotificationAlertState) {
        lineNotificationAlert.value = alert
    }

    fun clearLineHeadsUpAlert() {
        lineNotificationAlert.value = null
    }

    fun sendMessage(studentId: String, studentName: String, text: String, type: String = "line_reply") {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                classId = cls.id,
                studentId = studentId,
                studentName = studentName,
                text = text,
                type = type
            )
        }
    }

    fun selectClass(classId: String) {
        _selectedClassId.value = classId
        viewModelScope.launch {
            val settings = userSettings.value ?: UserSettingsEntity()
            repository.saveSettings(settings.copy(activeClassId = classId))
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val settings = userSettings.value ?: UserSettingsEntity()
            repository.saveSettings(settings.copy(language = lang))
        }
    }

    fun saveUserRole(role: String, name: String) {
        viewModelScope.launch {
            val settings = userSettings.value ?: UserSettingsEntity()
            val updated = if (role == "school") {
                settings.copy(role = "school", schoolName = name)
            } else {
                settings.copy(role = "teacher", teacherName = name)
            }
            repository.saveSettings(updated)
            currentScreen.value = "home"
        }
    }

    fun createClass(name: String, grade: String) {
        viewModelScope.launch {
            val newClass = repository.createClass(name, grade)
            _selectedClassId.value = newClass.id
            toastMessage.emit("Class $name ($grade) created!")
        }
    }

    fun addStudent(name: String, lineLinked: Boolean = false) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            val lineId = if (lineLinked) "U" + (10000000..99999999).random().toString() else null
            repository.addStudent(cls.id, name, lineLinked, lineId)
            toastMessage.emit(if (lineLinked) "$name connected via LINE!" else "Student $name added!")
        }
    }

    fun addScannedStudent(
        name: String,
        lineId: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        val cls = activeClass.value ?: return
        val cleanName = name.trim()
        if (cleanName.isBlank()) return
        val isDuplicate = studentsInActiveClass.value.any { it.name.trim().equals(cleanName, ignoreCase = true) }
        if (isDuplicate) {
            viewModelScope.launch {
                toastMessage.emit("⚠️ $cleanName is already enrolled in ${cls.name}")
            }
            return
        }

        viewModelScope.launch {
            repository.addStudent(
                classId = cls.id,
                name = cleanName,
                lineLinked = lineId != null,
                lineUserId = lineId
            )
            toastMessage.emit("✅ Added $cleanName to ${cls.name} via QR scan!")
            onSuccess?.invoke()
        }
    }

    fun addBatchStudentsFromQr(
        students: List<ParsedStudentInfo>,
        onComplete: ((addedCount: Int, skippedCount: Int) -> Unit)? = null
    ) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            var added = 0
            var skipped = 0
            val existing = studentsInActiveClass.value.map { it.name.trim().lowercase() }.toSet()

            for (studentInfo in students) {
                val cleanName = studentInfo.name.trim()
                if (cleanName.isBlank()) continue
                if (existing.contains(cleanName.lowercase())) {
                    skipped++
                    continue
                }
                val lineId = studentInfo.lineId
                repository.addStudent(
                    classId = cls.id,
                    name = cleanName,
                    lineLinked = lineId != null,
                    lineUserId = lineId
                )
                added++
            }

            if (added > 0) {
                toastMessage.emit("✅ Successfully added $added student(s) from QR scan to ${cls.name}!")
            } else if (skipped > 0) {
                toastMessage.emit("All $skipped scanned students already exist in ${cls.name}.")
            }
            onComplete?.invoke(added, skipped)
        }
    }

    fun signInWithLine(studentName: String, customLineId: String? = null) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            val lineId = customLineId ?: ("U" + (10000000..99999999).random().toString())
            val student = repository.addStudent(cls.id, studentName, lineLinked = true, lineUserId = lineId)
            repository.sendMessage(
                classId = cls.id,
                studentId = student.id,
                studentName = studentName,
                text = "💬 LINE Bot: Welcome $studentName! Linked to ${cls.name}. Homework alerts and speaking tasks will be delivered here.",
                type = "line_welcome"
            )
            toastMessage.emit("🟢 Connected to LINE as $studentName ($lineId)!")
        }
    }

    fun toggleStudentSubmission(studentId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleStudentSubmission(studentId, !currentStatus)
        }
    }

    fun sendReminder(student: StudentEntity) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            repository.sendReminder(student.id, cls.id, student.name)
            toastMessage.emit("Reminder sent to ${student.name}'s LINE inbox!")
        }
    }

    fun sendHomework(due: String?) {
        val cls = activeClass.value ?: return
        val students = studentsInActiveClass.value
        if (students.isEmpty()) {
            viewModelScope.launch { toastMessage.emit("Add students to ${cls.name} first.") }
            return
        }
        val text = buildHomeworkPreviewText()
        if (text.isBlank()) {
            viewModelScope.launch { toastMessage.emit("Generate an activity first to send as homework.") }
            return
        }

        viewModelScope.launch {
            repository.sendHomework(cls.id, text, due, students)
            toastMessage.emit("Homework sent to ${students.size} student inboxes!")
        }
    }

    fun sendDirectHomework(content: String, due: String? = null) {
        val cls = activeClass.value ?: return
        val students = studentsInActiveClass.value
        if (students.isEmpty()) {
            viewModelScope.launch { toastMessage.emit("Add students to ${cls.name} first.") }
            return
        }
        viewModelScope.launch {
            repository.sendHomework(cls.id, content, due, students)
            toastMessage.emit("Worksheet sent to ${students.size} student inboxes!")
        }
    }

    fun clearHomework() {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            repository.clearHomework(cls.id)
            toastMessage.emit("Homework cleared for ${cls.name}")
        }
    }

    private fun buildHomeworkPreviewText(): String {
        return when (selectedActivityType.value) {
            "standard" -> standardTestResult.value?.let { res ->
                "Standard Test: ${res.title}\n" + res.items.take(3).joinToString("\n") { "${it.id}. ${it.questionEn}" }
            } ?: ""
            "gamified" -> gamifiedQuizText.value ?: ""
            "flashcards" -> "Flashcards Practice (${flashCardsResult.value.size} words): " + flashCardsResult.value.take(4).joinToString(", ") { "${it.en} (${it.th})" }
            "sltest" -> "Speaking Test: " + speakingListeningQuestions.value.take(3).joinToString("; ") { it.questionEn }
            "sentence" -> "Sentence Builder: Practice 4 sentence scrambles"
            "worksheet" -> "Live Worksheet: Complete 5 fill-in-the-blank sentences"
            "story" -> storyResult.value?.let { "${it.title}: ${it.storyText.take(120)}..." } ?: ""
            "wordbank" -> "Word Bank: Review ${wordBankEntries.value.size} vocabulary items"
            "audio" -> listeningResult.value?.let { "Audio Listening: ${it.title}" } ?: ""
            "video" -> listeningResult.value?.let { "Video Listening: ${it.title}" } ?: ""
            "matchup" -> matchUpResult.value?.let { "Match-Up Pairs (${it.pairs.size} pairs): ${it.title}" } ?: ""
            "quizrace" -> quizRaceResult.value?.let { "Quiz Race (${it.items.size} speed questions): ${it.title}" } ?: ""
            "anagram" -> anagramResult.value?.let { "Word Scramble Anagram (${it.items.size} words): ${it.title}" } ?: ""
            "truefalse" -> trueFalseResult.value?.let { "True/False Speed (${it.items.size} statements): ${it.title}" } ?: ""
            "openthebox" -> openBoxResult.value?.let { "Open The Box (${it.boxes.size} mystery questions): ${it.title}" } ?: ""
            else -> lessonInputText.value.take(150)
        }
    }

    fun generateActivity() {
        val content = lessonInputText.value.trim()
        if (content.isBlank()) {
            viewModelScope.launch { toastMessage.emit("Please enter lesson content or a topic.") }
            return
        }
        val cls = activeClass.value
        val grade = cls?.grade ?: "M.1"

        viewModelScope.launch {
            isGenerating.value = true
            try {
                when (selectedActivityType.value) {
                    "standard" -> {
                        val res = AIActivityGenerator.generateStandardTest(content, grade)
                        standardTestResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "standard", grade, res.title, res.items.joinToString("\n") { it.questionEn })
                    }
                    "gamified" -> {
                        val res = AIActivityGenerator.generateGamifiedQuiz(content, grade)
                        gamifiedQuizText.value = res
                        repository.logActivity(cls?.id ?: "gen", "gamified", grade, "Gamified Quest: $content", res)
                    }
                    "flashcards" -> {
                        val res = AIActivityGenerator.generateFlashcards(content, grade)
                        flashCardsResult.value = res
                        if (cls != null) {
                            val vocabEntities = res.map { VocabWordEntity(classId = cls.id, en = it.en, th = it.th) }
                            repository.addVocabWords(vocabEntities)
                        }
                        repository.logActivity(cls?.id ?: "gen", "flashcards", grade, "Flashcards (${res.size} cards)", res.joinToString { it.en })
                    }
                    "sltest" -> {
                        val res = AIActivityGenerator.generateSpeakingListeningTest(content, grade)
                        speakingListeningQuestions.value = res
                        slQuestionIndex.value = 0
                        slStudentAnswers.value = emptyMap()
                        repository.logActivity(cls?.id ?: "gen", "sltest", grade, "Oral Test: $content", res.joinToString { it.questionEn })
                    }
                    "sentence" -> {
                        val res = AIActivityGenerator.generateSentenceBuilder(content, grade)
                        sentenceBuilderItems.value = res
                        repository.logActivity(cls?.id ?: "gen", "sentence", grade, "Sentence Builder (${res.size} items)", res.joinToString { it.sentenceEn })
                    }
                    "worksheet" -> {
                        val res = AIActivityGenerator.generateWorksheet(content, grade)
                        worksheetItems.value = res
                        repository.logActivity(cls?.id ?: "gen", "worksheet", grade, "Live Worksheet (${res.size} blanks)", res.joinToString { it.answer })
                    }
                    "story" -> {
                        val res = AIActivityGenerator.generateStory(content, grade)
                        storyResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "story", grade, res.title, res.storyText)
                    }
                    "wordsearch" -> {
                        val res = AIActivityGenerator.generateWordSearch(content, grade)
                        wordSearchResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "wordsearch", grade, "Word Search (${res.words.size} words)", res.words.joinToString())
                    }
                    "memory" -> {
                        val res = AIActivityGenerator.generateMemoryMatch(content, grade)
                        memoryCards.value = res
                        memoryFlippedIds.value = emptyList()
                        memoryMatchedPairIds.value = emptySet()
                        repository.logActivity(cls?.id ?: "gen", "memory", grade, "Memory Match (${res.size / 2} pairs)", "Memory game with ${res.size} cards")
                    }
                    "wordbank" -> {
                        val res = AIActivityGenerator.generateWordBank(content, grade)
                        wordBankEntries.value = res
                        if (cls != null) {
                            val vocabEntities = res.map { VocabWordEntity(classId = cls.id, en = it.en, th = it.th, example = it.example) }
                            repository.addVocabWords(vocabEntities)
                        }
                        repository.logActivity(cls?.id ?: "gen", "wordbank", grade, "Word Bank (${res.size} words)", res.joinToString { it.en })
                    }
                    "audio" -> {
                        val res = AIActivityGenerator.generateListeningScript(content, grade, isVideo = false)
                        listeningResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "audio", grade, res.title, res.script)
                    }
                    "video" -> {
                        val res = AIActivityGenerator.generateListeningScript(content, grade, isVideo = true)
                        listeningResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "video", grade, res.title, res.script)
                    }
                    "conversation" -> {
                        val opening = AIActivityGenerator.startRoleplay(content, grade)
                        roleplayChat.value = listOf(opening)
                        repository.logActivity(cls?.id ?: "gen", "conversation", grade, "AI Oral Roleplay: $content", opening.textEn)
                    }
                    "matchup" -> {
                        val res = AIActivityGenerator.generateMatchUp(content, grade)
                        matchUpResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "matchup", grade, res.title, "Match-up with ${res.pairs.size} pairs")
                    }
                    "quizrace" -> {
                        val res = AIActivityGenerator.generateQuizRace(content, grade)
                        quizRaceResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "quizrace", grade, res.title, "Quiz Race with ${res.items.size} questions")
                    }
                    "anagram" -> {
                        val res = AIActivityGenerator.generateAnagram(content, grade)
                        anagramResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "anagram", grade, res.title, "Anagram with ${res.items.size} words")
                    }
                    "truefalse" -> {
                        val res = AIActivityGenerator.generateTrueFalse(content, grade)
                        trueFalseResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "truefalse", grade, res.title, "True/False with ${res.items.size} items")
                    }
                    "openthebox" -> {
                        val res = AIActivityGenerator.generateOpenTheBox(content, grade)
                        openBoxResult.value = res
                        repository.logActivity(cls?.id ?: "gen", "openthebox", grade, res.title, "Open the box with ${res.boxes.size} boxes")
                    }
                }
                toastMessage.emit("Activity generated successfully!")
            } catch (e: Exception) {
                toastMessage.emit("Error generating activity: ${e.message}")
            } finally {
                isGenerating.value = false
            }
        }
    }

    fun submitRoleplayReply(studentText: String) {
        if (studentText.isBlank()) return
        val content = lessonInputText.value
        val grade = activeClass.value?.grade ?: "M.1"
        val studentMsg = ConversationMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = "student",
            textEn = studentText
        )
        val updatedHistory = roleplayChat.value + studentMsg
        roleplayChat.value = updatedHistory

        viewModelScope.launch {
            val aiReply = AIActivityGenerator.replyRoleplay(updatedHistory, studentText, content, grade)
            roleplayChat.value = updatedHistory + aiReply
            speechManager.speak(aiReply.textEn)
        }
    }

    fun recordSpeakingTestAnswer(questionId: Int, transcript: String, isCorrect: Boolean) {
        val current = slStudentAnswers.value.toMutableMap()
        current[questionId] = transcript to isCorrect
        slStudentAnswers.value = current

        if (slQuestionIndex.value < speakingListeningQuestions.value.size - 1) {
            slQuestionIndex.value += 1
            val nextQ = speakingListeningQuestions.value[slQuestionIndex.value]
            speechManager.speak(nextQ.questionEn)
        }
    }

    fun saveSpeakingTestScoreToAssessment(studentId: String?, customTitle: String? = null) {
        val cls = activeClass.value ?: return
        val questions = speakingListeningQuestions.value
        val answers = slStudentAnswers.value
        val correctCount = answers.count { it.value.second }.toFloat()
        val title = customTitle ?: "Speaking & Listening Test: ${lessonInputText.value.take(30)}"

        viewModelScope.launch {
            val scoreMap = mutableMapOf<String, Pair<String, Float>>()
            if (studentId != null) {
                val student = studentsInActiveClass.value.find { it.id == studentId }
                if (student != null) {
                    scoreMap[student.id] = student.name to correctCount
                }
            } else {
                studentsInActiveClass.value.forEach { student ->
                    scoreMap[student.id] = student.name to correctCount
                }
            }
            repository.createAssessment(cls.id, title, questions.size.toFloat(), scoreMap)
            toastMessage.emit("Recorded score ($correctCount/${questions.size}) to Assessment Tool!")
        }
    }

    fun onMemoryCardTapped(card: MemoryMatchCard) {
        val currentFlipped = memoryFlippedIds.value
        val currentMatched = memoryMatchedPairIds.value

        if (currentMatched.contains(card.pairId) || currentFlipped.contains(card.id)) return
        if (currentFlipped.size >= 2) return

        val newFlipped = currentFlipped + card.id
        memoryFlippedIds.value = newFlipped

        if (newFlipped.size == 2) {
            val card1 = memoryCards.value.find { it.id == newFlipped[0] }
            val card2 = memoryCards.value.find { it.id == newFlipped[1] }

            if (card1 != null && card2 != null && card1.pairId == card2.pairId && card1.id != card2.id) {
                memoryMatchedPairIds.value = currentMatched + card1.pairId
                memoryFlippedIds.value = emptyList()
                if (memoryMatchedPairIds.value.size == memoryCards.value.size / 2) {
                    viewModelScope.launch { toastMessage.emit("🎉 Congratulations! All pairs matched!") }
                }
            } else {
                viewModelScope.launch {
                    kotlinx.coroutines.delay(800)
                    memoryFlippedIds.value = emptyList()
                }
            }
        }
    }

    fun addCustomVocabWord(en: String, th: String, example: String? = null) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            repository.addVocabWord(VocabWordEntity(classId = cls.id, en = en, th = th, example = example))
            toastMessage.emit("Added '$en' to Vocabulary Bank!")
        }
    }

    fun deleteVocabWord(id: String) {
        viewModelScope.launch {
            repository.deleteVocabWord(id)
            toastMessage.emit("Word removed from bank")
        }
    }

    fun saveAssessment(title: String, maxScore: Float, studentScores: Map<String, Float>) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            val scoreMap = mutableMapOf<String, Pair<String, Float>>()
            studentsInActiveClass.value.forEach { s ->
                val score = studentScores[s.id] ?: 0f
                scoreMap[s.id] = s.name to score
            }
            repository.createAssessment(cls.id, title, maxScore, scoreMap)
            toastMessage.emit("Assessment '$title' saved successfully!")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            repository.seedSampleDataIfNeeded()
            toastMessage.emit("Reset to starter sample data.")
        }
    }

    // Oral Exam & STT Pronunciation Engine Functions
    fun startOralExamWithVocabBank(
        student: StudentEntity? = null,
        count: Int? = null,
        customWords: List<VocabWordEntity>? = null,
        shuffle: Boolean = false
    ) {
        val baseWords = when {
            !customWords.isNullOrEmpty() -> customWords
            vocabInActiveClass.value.isNotEmpty() -> vocabInActiveClass.value
            else -> {
                // Fallback to Curated M.1 / Active Grade Vocab
                val grade = activeClass.value?.grade ?: "M.1"
                val filteredCurated = PronunciationEvaluator.CURATED_VOCAB_BANK.filter { it.grade.equals(grade, ignoreCase = true) }
                    .ifEmpty { PronunciationEvaluator.CURATED_VOCAB_BANK }
                filteredCurated.map {
                    VocabWordEntity(
                        classId = activeClass.value?.id ?: "cls_default",
                        en = it.en,
                        th = it.th,
                        example = it.example
                    )
                }
            }
        }

        val processedWords = if (shuffle) baseWords.shuffled() else baseWords
        val selectedWords = if (count != null && count > 0) processedWords.take(count) else processedWords

        if (selectedWords.isEmpty()) {
            viewModelScope.launch {
                toastMessage.emit("No vocabulary words available for the exam. Add words to the bank first!")
            }
            return
        }

        oralExamSelectedStudent.value = student
        oralExamWordList.value = selectedWords
        oralExamCurrentIndex.value = 0
        oralExamResults.value = emptyMap()
        isOralExamActive.value = true
        isOralExamCompleted.value = false

        // Automatically pronounce the first target word for audio modeling
        selectedWords.firstOrNull()?.let { firstWord ->
            speechManager.speak(firstWord.en)
        }
    }

    fun evaluateAndRecordPronunciation(spokenTranscript: String) {
        val words = oralExamWordList.value
        val currentIndex = oralExamCurrentIndex.value
        if (currentIndex !in words.indices) return

        val currentWord = words[currentIndex]
        val result = PronunciationEvaluator.evaluate(
            targetWord = currentWord.en,
            spokenTranscript = spokenTranscript,
            targetExample = currentWord.example
        )

        val updatedMap = oralExamResults.value.toMutableMap()
        updatedMap[currentWord.id.ifBlank { currentWord.en }] = result
        oralExamResults.value = updatedMap

        viewModelScope.launch {
            if (result.rating == PronunciationRating.PERFECT) {
                toastMessage.emit("🌟 100% Perfect! '${currentWord.en}'")
            } else if (result.rating == PronunciationRating.GOOD) {
                toastMessage.emit("👍 Good job! (${result.scorePercentage}%)")
            } else {
                toastMessage.emit("${result.rating.title}: ${result.scorePercentage}%")
            }
        }
    }

    fun nextOralExamWord() {
        val words = oralExamWordList.value
        val currentIndex = oralExamCurrentIndex.value
        if (currentIndex < words.size - 1) {
            val nextIdx = currentIndex + 1
            oralExamCurrentIndex.value = nextIdx
            val nextWord = words[nextIdx]
            speechManager.speak(nextWord.en)
        } else {
            finishOralExam()
        }
    }

    fun prevOralExamWord() {
        val currentIndex = oralExamCurrentIndex.value
        if (currentIndex > 0) {
            val prevIdx = currentIndex - 1
            oralExamCurrentIndex.value = prevIdx
            val prevWord = oralExamWordList.value[prevIdx]
            speechManager.speak(prevWord.en)
        }
    }

    fun jumpToOralExamWord(index: Int) {
        val words = oralExamWordList.value
        if (index in words.indices) {
            oralExamCurrentIndex.value = index
            speechManager.speak(words[index].en)
        }
    }

    fun finishOralExam() {
        isOralExamActive.value = false
        isOralExamCompleted.value = true
        val results = oralExamResults.value
        val words = oralExamWordList.value
        val avgScore = if (results.isNotEmpty()) {
            results.values.map { it.scorePoints }.average()
        } else 0.0

        viewModelScope.launch {
            toastMessage.emit("🎉 Oral Exam Completed! Average Score: ${String.format("%.1f", avgScore)} / 10")
        }
    }

    fun restartOralExam() {
        val student = oralExamSelectedStudent.value
        val words = oralExamWordList.value
        startOralExamWithVocabBank(student = student, customWords = words)
    }

    fun saveOralExamScoreToAssessments(customTitle: String? = null) {
        val cls = activeClass.value ?: return
        val results = oralExamResults.value
        val words = oralExamWordList.value
        if (words.isEmpty()) return

        val totalPoints = results.values.sumOf { it.scorePoints.toDouble() }.toFloat()
        val maxPoints = (words.size * 10).toFloat()
        // Normalized 10-point scale
        val scaledScore = if (words.isNotEmpty()) (totalPoints / words.size) else 0f

        val student = oralExamSelectedStudent.value
        val title = customTitle ?: "Oral Exam: Vocab Pronunciation (${words.size} words)"

        viewModelScope.launch {
            val scoreMap = mutableMapOf<String, Pair<String, Float>>()
            if (student != null) {
                scoreMap[student.id] = student.name to scaledScore
            } else {
                studentsInActiveClass.value.forEach { s ->
                    scoreMap[s.id] = s.name to scaledScore
                }
            }

            repository.createAssessment(
                classId = cls.id,
                title = title,
                maxScore = 10f,
                scores = scoreMap
            )
            toastMessage.emit("✅ Saved score (${String.format("%.1f", scaledScore)}/10) to Gradebook for ${student?.name ?: "all students"}!")
        }
    }

    fun sendOralExamResultToLine(student: StudentEntity) {
        val cls = activeClass.value ?: return
        val results = oralExamResults.value
        val words = oralExamWordList.value
        val count = words.size
        val passedCount = results.values.count { it.isPassed }
        val avgScore = if (results.isNotEmpty()) results.values.map { it.scorePoints }.average() else 0.0

        val topWords = words.take(4).joinToString(", ") { it.en }
        val messageText = "🎙️ Oral Pronunciation Exam Result for ${student.name}\n" +
                "📚 Class: ${cls.name} (${cls.grade})\n" +
                "⭐ Final Score: ${String.format("%.1f", avgScore)} / 10 (Passed $passedCount/$count words)\n" +
                "🔤 Words tested: $topWords\n" +
                "💡 Practice tip: Use the speech assistant to practice syllable endings and stress."

        viewModelScope.launch {
            repository.sendMessage(
                classId = cls.id,
                studentId = student.id,
                studentName = student.name,
                text = messageText,
                type = "oral_exam_report"
            )

            // Also trigger simulation heads-up alert
            triggerLineHeadsUpAlert(
                LineNotificationAlertState(
                    title = "LINE Notification: Oral Exam Score",
                    senderName = "Teacher (${userSettings.value?.teacherName ?: "Ajarn"})",
                    message = "Sent ${student.name}'s pronunciation scorecard (${String.format("%.1f", avgScore)}/10) via LINE!",
                    badge = "Oral Exam"
                )
            )
            toastMessage.emit("💬 Oral Exam result sent to ${student.name}'s LINE!")
        }
    }

    fun importCuratedDeckToClassVocab(grade: String) {
        val cls = activeClass.value ?: return
        val curated = PronunciationEvaluator.CURATED_VOCAB_BANK.filter { it.grade.equals(grade, ignoreCase = true) }
        if (curated.isEmpty()) return

        val vocabEntities = curated.map {
            VocabWordEntity(
                classId = cls.id,
                en = it.en,
                th = it.th,
                example = it.example
            )
        }

        viewModelScope.launch {
            repository.addVocabWords(vocabEntities)
            toastMessage.emit("📥 Imported ${curated.size} $grade curated vocabulary words to ${cls.name}!")
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
}
