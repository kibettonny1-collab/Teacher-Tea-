package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ClassCompanionRepository(private val db: AppDatabase) {
    val allClasses: Flow<List<ClassRoomEntity>> = db.classRoomDao().getAllClasses()
    val allLogs: Flow<List<ActivityLogEntity>> = db.activityLogDao().getAllLogs()
    val userSettings: Flow<UserSettingsEntity?> = db.userSettingsDao().getSettings()

    fun getStudentsByClass(classId: String): Flow<List<StudentEntity>> =
        db.studentDao().getStudentsByClass(classId)

    fun getAllStudents(): Flow<List<StudentEntity>> =
        db.studentDao().getAllStudents()

    fun getMessagesByClass(classId: String): Flow<List<InboxMessageEntity>> =
        db.inboxMessageDao().getMessagesByClass(classId)

    fun getAllMessages(): Flow<List<InboxMessageEntity>> =
        db.inboxMessageDao().getAllMessages()

    fun getVocabByClass(classId: String): Flow<List<VocabWordEntity>> =
        db.vocabWordDao().getVocabByClass(classId)

    fun getAllVocab(): Flow<List<VocabWordEntity>> =
        db.vocabWordDao().getAllVocab()

    fun getAssessmentsByClass(classId: String): Flow<List<AssessmentEntity>> =
        db.assessmentDao().getAssessmentsByClass(classId)

    fun getAllAssessments(): Flow<List<AssessmentEntity>> =
        db.assessmentDao().getAllAssessments()

    fun getScoresForAssessment(assessmentId: String): Flow<List<StudentScoreEntity>> =
        db.studentScoreDao().getScoresForAssessment(assessmentId)

    fun getAllScores(): Flow<List<StudentScoreEntity>> =
        db.studentScoreDao().getAllScores()

    // Database Mutations
    suspend fun saveSettings(settings: UserSettingsEntity) = withContext(Dispatchers.IO) {
        db.userSettingsDao().saveSettings(settings)
    }

    suspend fun getSettingsDirect(): UserSettingsEntity? = withContext(Dispatchers.IO) {
        db.userSettingsDao().getSettingsDirect()
    }

    suspend fun createClass(name: String, grade: String): ClassRoomEntity = withContext(Dispatchers.IO) {
        val newClass = ClassRoomEntity(
            name = name,
            grade = grade
        )
        db.classRoomDao().insertClass(newClass)
        newClass
    }

    suspend fun deleteClass(classId: String) = withContext(Dispatchers.IO) {
        db.classRoomDao().deleteClassById(classId)
    }

    suspend fun addStudent(classId: String, name: String, lineLinked: Boolean = false, lineUserId: String? = null): StudentEntity = withContext(Dispatchers.IO) {
        val student = StudentEntity(
            classId = classId,
            name = name,
            lineLinked = lineLinked,
            lineUserId = lineUserId
        )
        db.studentDao().insertStudent(student)
        student
    }

    suspend fun sendMessage(classId: String, studentId: String, studentName: String, text: String, type: String = "info") = withContext(Dispatchers.IO) {
        val msg = InboxMessageEntity(
            studentId = studentId,
            classId = classId,
            studentName = studentName,
            type = type,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        db.inboxMessageDao().insertMessage(msg)
    }

    suspend fun deleteStudent(studentId: String) = withContext(Dispatchers.IO) {
        db.studentDao().deleteStudent(studentId)
    }

    suspend fun toggleStudentSubmission(studentId: String, isSubmitted: Boolean) = withContext(Dispatchers.IO) {
        db.studentDao().updateSubmission(studentId, isSubmitted)
    }

    suspend fun sendHomework(classId: String, text: String, due: String?, studentList: List<StudentEntity>) = withContext(Dispatchers.IO) {
        db.classRoomDao().setHomework(classId, text, due)
        db.studentDao().updateAllClassSubmissions(classId, false)
        val messages = studentList.map { student ->
            val preview = if (text.length > 140) text.substring(0, 140) + "..." else text
            val dueInfo = if (!due.isNullOrBlank()) " (Due: $due)" else ""
            InboxMessageEntity(
                studentId = student.id,
                classId = classId,
                studentName = student.name,
                type = "homework",
                text = "New Homework$dueInfo: $preview",
                timestamp = System.currentTimeMillis()
            )
        }
        db.inboxMessageDao().insertMessages(messages)
    }

    suspend fun clearHomework(classId: String) = withContext(Dispatchers.IO) {
        db.classRoomDao().setHomework(classId, null, null)
    }

    suspend fun sendReminder(studentId: String, classId: String, studentName: String) = withContext(Dispatchers.IO) {
        val msg = InboxMessageEntity(
            studentId = studentId,
            classId = classId,
            studentName = studentName,
            type = "reminder",
            text = "Don't forget to submit your English homework assignment!",
            timestamp = System.currentTimeMillis()
        )
        db.inboxMessageDao().insertMessage(msg)
    }

    suspend fun addVocabWords(words: List<VocabWordEntity>) = withContext(Dispatchers.IO) {
        db.vocabWordDao().insertWords(words)
    }

    suspend fun addVocabWord(word: VocabWordEntity) = withContext(Dispatchers.IO) {
        db.vocabWordDao().insertWord(word)
    }

    suspend fun deleteVocabWord(id: String) = withContext(Dispatchers.IO) {
        db.vocabWordDao().deleteWord(id)
    }

    suspend fun createAssessment(
        classId: String,
        title: String,
        maxScore: Float,
        scores: Map<String, Pair<String, Float>> // studentId -> Pair(studentName, score)
    ): String = withContext(Dispatchers.IO) {
        val assessment = AssessmentEntity(
            classId = classId,
            title = title,
            maxScore = maxScore
        )
        db.assessmentDao().insertAssessment(assessment)

        val scoreEntities = scores.map { (studentId, pair) ->
            StudentScoreEntity(
                assessmentId = assessment.id,
                studentId = studentId,
                studentName = pair.first,
                score = pair.second
            )
        }
        db.studentScoreDao().insertScores(scoreEntities)
        assessment.id
    }

    suspend fun logActivity(classId: String, type: String, grade: String, preview: String, fullContent: String) = withContext(Dispatchers.IO) {
        val log = ActivityLogEntity(
            classId = classId,
            type = type,
            gradeLevel = grade,
            previewText = preview,
            fullContent = fullContent
        )
        db.activityLogDao().insertLog(log)
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        db.classRoomDao().clearAll()
        db.studentDao().clearAll()
        db.inboxMessageDao().clearAll()
        db.vocabWordDao().clearAll()
        db.assessmentDao().clearAll()
        db.studentScoreDao().clearAll()
        db.activityLogDao().clearAll()
        db.userSettingsDao().clearAll()
    }

    suspend fun seedSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        val existing = db.userSettingsDao().getSettingsDirect()
        if (existing == null) {
            // Seed classes
            val class1 = ClassRoomEntity(
                id = "cls_m13",
                name = "M.1/3",
                grade = "M.1",
                joinCode = "M13TH",
                homeworkText = "Complete the 5 past tense verb worksheet sentences and practice pronunciation with speech assistant.",
                homeworkDue = "2026-08-20"
            )
            val class2 = ClassRoomEntity(
                id = "cls_m42",
                name = "M.4/2",
                grade = "M.4",
                joinCode = "M42EN"
            )
            db.classRoomDao().insertClass(class1)
            db.classRoomDao().insertClass(class2)

            // Seed students
            val students = listOf(
                StudentEntity(id = "st_1", classId = "cls_m13", name = "Nong Ploy (น้องพลอย)", isSubmitted = true, lineLinked = true, lineUserId = "U88a91b2"),
                StudentEntity(id = "st_2", classId = "cls_m13", name = "Somchai (สมชาย)", isSubmitted = false, lineLinked = true, lineUserId = "U39f44c1"),
                StudentEntity(id = "st_3", classId = "cls_m13", name = "Tanawat (ธนวัฒน์)", isSubmitted = true, lineLinked = false),
                StudentEntity(id = "st_4", classId = "cls_m13", name = "Natthaporn (ณัฐพร)", isSubmitted = false, lineLinked = true, lineUserId = "U72d50e9"),
                StudentEntity(id = "st_5", classId = "cls_m13", name = "Ananda (อนันดา)", isSubmitted = false, lineLinked = false),
                StudentEntity(id = "st_6", classId = "cls_m42", name = "Kittisak (กิตติศักดิ์)", isSubmitted = false, lineLinked = true, lineUserId = "U19c83a7"),
                StudentEntity(id = "st_7", classId = "cls_m42", name = "Supaporn (สุภาพร)", isSubmitted = false, lineLinked = true, lineUserId = "U65f90d3")
            )
            db.studentDao().insertStudents(students)

            // Seed vocabulary
            val vocab = listOf(
                VocabWordEntity(classId = "cls_m13", en = "Bicycle", th = "จักรยาน", example = "I ride my bicycle to school every morning."),
                VocabWordEntity(classId = "cls_m13", en = "Delicious", th = "อร่อย", example = "Pad Thai is very delicious."),
                VocabWordEntity(classId = "cls_m13", en = "Library", th = "ห้องสมุด", example = "We read books in the school library."),
                VocabWordEntity(classId = "cls_m13", en = "Yesterday", th = "เมื่อวานนี้", example = "Yesterday we studied English past tense."),
                VocabWordEntity(classId = "cls_m13", en = "Festival", th = "เทศกาล", example = "Songkran is a famous Thai water festival."),
                VocabWordEntity(classId = "cls_m13", en = "Excited", th = "ตื่นเต้น", example = "The students are excited for the field trip.")
            )
            db.vocabWordDao().insertWords(vocab)

            // Seed Assessment
            val assess = AssessmentEntity(
                id = "asm_1",
                classId = "cls_m13",
                title = "Unit 1: Daily Routines & Verbs Quiz",
                maxScore = 10f
            )
            db.assessmentDao().insertAssessment(assess)

            val scores = listOf(
                StudentScoreEntity(assessmentId = "asm_1", studentId = "st_1", studentName = "Nong Ploy (น้องพลอย)", score = 9.5f),
                StudentScoreEntity(assessmentId = "asm_1", studentId = "st_2", studentName = "Somchai (สมชาย)", score = 8f),
                StudentScoreEntity(assessmentId = "asm_1", studentId = "st_3", studentName = "Tanawat (ธนวัฒน์)", score = 10f),
                StudentScoreEntity(assessmentId = "asm_1", studentId = "st_4", studentName = "Natthaporn (ณัฐพร)", score = 7.5f),
                StudentScoreEntity(assessmentId = "asm_1", studentId = "st_5", studentName = "Ananda (อนันดา)", score = 8.5f)
            )
            db.studentScoreDao().insertScores(scores)

            // Seed Activity Log
            val logs = listOf(
                ActivityLogEntity(
                    classId = "cls_m13",
                    type = "gamified",
                    gradeLevel = "M.1",
                    previewText = "Irregular Past Verbs Quiz (go/went, eat/ate, see/saw)",
                    fullContent = "Gamified Quiz on Past Tense Verbs with streak bonus for M.1"
                ),
                ActivityLogEntity(
                    classId = "cls_m13",
                    type = "sltest",
                    gradeLevel = "M.1",
                    previewText = "Speaking & Listening: Daily Routine & Morning Activities",
                    fullContent = "Oral testing with WH-questions and yes/no questions"
                )
            )
            logs.forEach { db.activityLogDao().insertLog(it) }

            // Seed settings
            val settings = UserSettingsEntity(
                id = 1,
                role = "teacher",
                teacherName = "Ajarn Lok",
                schoolName = "Banchang Wittayakhom School",
                language = "en",
                activeClassId = "cls_m13"
            )
            db.userSettingsDao().saveSettings(settings)
        }
    }
}
