package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "classes")
data class ClassRoomEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val name: String,
    val grade: String, // "M.1", "M.4", "M.5", "M.6"
    val joinCode: String = UUID.randomUUID().toString().substring(0, 5).uppercase(),
    val homeworkText: String? = null,
    val homeworkDue: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val classId: String,
    val name: String,
    val isSubmitted: Boolean = false,
    val lineLinked: Boolean = false,
    val lineUserId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inbox_messages")
data class InboxMessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val studentId: String,
    val classId: String,
    val studentName: String,
    val type: String, // "homework" | "reminder"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "vocab_words")
data class VocabWordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val classId: String,
    val en: String,
    val th: String,
    val example: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val classId: String,
    val title: String,
    val maxScore: Float = 10f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_scores")
data class StudentScoreEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val assessmentId: String,
    val studentId: String,
    val studentName: String,
    val score: Float
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString().substring(0, 8),
    val classId: String,
    val type: String, // "standard", "gamified", "flashcards", "sltest", "sentence", "worksheet", "story", "wordsearch", "memory", "wordbank", "audio", "video", "conversation"
    val gradeLevel: String,
    val previewText: String,
    val fullContent: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val role: String? = null, // "school" | "teacher" | null
    val schoolName: String = "",
    val teacherName: String = "",
    val language: String = "en", // "en" | "th" | "zh"
    val activeClassId: String? = null
)
