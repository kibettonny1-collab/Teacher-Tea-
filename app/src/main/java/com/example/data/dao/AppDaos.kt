package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassRoomDao {
    @Query("SELECT * FROM classes ORDER BY createdAt DESC")
    fun getAllClasses(): Flow<List<ClassRoomEntity>>

    @Query("SELECT * FROM classes WHERE id = :id LIMIT 1")
    suspend fun getClassById(id: String): ClassRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classRoom: ClassRoomEntity)

    @Update
    suspend fun updateClass(classRoom: ClassRoomEntity)

    @Query("DELETE FROM classes WHERE id = :id")
    suspend fun deleteClassById(id: String)

    @Query("UPDATE classes SET homeworkText = :text, homeworkDue = :due WHERE id = :classId")
    suspend fun setHomework(classId: String, text: String?, due: String?)

    @Query("DELETE FROM classes")
    suspend fun clearAll()
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY name ASC")
    fun getStudentsByClass(classId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET isSubmitted = :submitted WHERE id = :studentId")
    suspend fun updateSubmission(studentId: String, submitted: Boolean)

    @Query("UPDATE students SET isSubmitted = :submitted WHERE classId = :classId")
    suspend fun updateAllClassSubmissions(classId: String, submitted: Boolean)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudent(id: String)

    @Query("DELETE FROM students")
    suspend fun clearAll()
}

@Dao
interface InboxMessageDao {
    @Query("SELECT * FROM inbox_messages WHERE classId = :classId ORDER BY timestamp DESC")
    fun getMessagesByClass(classId: String): Flow<List<InboxMessageEntity>>

    @Query("SELECT * FROM inbox_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<InboxMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: InboxMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<InboxMessageEntity>)

    @Query("DELETE FROM inbox_messages")
    suspend fun clearAll()
}

@Dao
interface VocabWordDao {
    @Query("SELECT * FROM vocab_words WHERE classId = :classId ORDER BY en ASC")
    fun getVocabByClass(classId: String): Flow<List<VocabWordEntity>>

    @Query("SELECT * FROM vocab_words ORDER BY timestamp DESC")
    fun getAllVocab(): Flow<List<VocabWordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: VocabWordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<VocabWordEntity>)

    @Query("DELETE FROM vocab_words WHERE id = :id")
    suspend fun deleteWord(id: String)

    @Query("DELETE FROM vocab_words")
    suspend fun clearAll()
}

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments WHERE classId = :classId ORDER BY timestamp DESC")
    fun getAssessmentsByClass(classId: String): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments ORDER BY timestamp DESC")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Query("DELETE FROM assessments WHERE id = :id")
    suspend fun deleteAssessment(id: String)

    @Query("DELETE FROM assessments")
    suspend fun clearAll()
}

@Dao
interface StudentScoreDao {
    @Query("SELECT * FROM student_scores WHERE assessmentId = :assessmentId")
    fun getScoresForAssessment(assessmentId: String): Flow<List<StudentScoreEntity>>

    @Query("SELECT * FROM student_scores")
    fun getAllScores(): Flow<List<StudentScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: StudentScoreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: List<StudentScoreEntity>)

    @Query("DELETE FROM student_scores WHERE assessmentId = :assessmentId")
    suspend fun deleteScoresByAssessment(assessmentId: String)

    @Query("DELETE FROM student_scores")
    suspend fun clearAll()
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE classId = :classId ORDER BY timestamp DESC")
    fun getLogsByClass(classId: String): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity)

    @Query("DELETE FROM activity_logs")
    suspend fun clearAll()
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)

    @Query("DELETE FROM user_settings")
    suspend fun clearAll()
}
