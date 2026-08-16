package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object DataExportHelper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault())
    private val readableDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    /**
     * Generates a clean CSV file for Student Roster and launches Android Share/Save sheet
     */
    fun exportStudentRosterCsv(
        context: Context,
        className: String,
        grade: String,
        joinCode: String,
        students: List<StudentEntity>
    ): Uri? {
        val timestamp = dateFormat.format(Date())
        val fileName = "Student_Roster_${sanitize(className)}_$timestamp.csv"
        val file = File(context.cacheDir, fileName)

        val csvContent = buildString {
            appendLine("Student ID,Full Name,Class,Grade,Join Code,Homework Submitted,LINE Linked,LINE User ID,Date Added")
            students.forEach { s ->
                val dateStr = readableDateFormat.format(Date(s.createdAt))
                appendLine(
                    "\"${s.id}\",\"${escapeCsv(s.name)}\",\"${escapeCsv(className)}\",\"${escapeCsv(grade)}\",\"$joinCode\",\"${if (s.isSubmitted) "Yes" else "No"}\",\"${if (s.lineLinked) "Connected" else "Pending"}\",\"${s.lineUserId ?: "N/A"}\",\"$dateStr\""
                )
            }
        }

        return try {
            file.writeText(csvContent, Charsets.UTF_8)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            shareFile(context, uri, "text/csv", "Export Student Roster ($className)")
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates a comprehensive Gradebook CSV matrix
     */
    fun exportGradebookCsv(
        context: Context,
        className: String,
        assessments: List<AssessmentEntity>,
        scores: List<StudentScoreEntity>,
        students: List<StudentEntity>
    ): Uri? {
        val timestamp = dateFormat.format(Date())
        val fileName = "Gradebook_${sanitize(className)}_$timestamp.csv"
        val file = File(context.cacheDir, fileName)

        val csvContent = buildString {
            // Header information
            appendLine("Class Companion Gradebook Report")
            appendLine("Class: $className, Export Date: ${readableDateFormat.format(Date())}, Total Students: ${students.size}")
            appendLine()

            // Detailed score records table
            appendLine("Student ID,Student Name,Assessment Title,Score,Max Score,Percentage (%),Grade Category,Recorded Date")
            
            if (scores.isEmpty()) {
                students.forEach { student ->
                    appendLine("\"${student.id}\",\"${escapeCsv(student.name)}\",\"No Assessments Recorded\",\"0\",\"10\",\"0%\",\"Pending\",\"${readableDateFormat.format(Date())}\"")
                }
            } else {
                scores.forEach { score ->
                    val assessment = assessments.find { it.id == score.assessmentId }
                    val maxScore = assessment?.maxScore ?: 10f
                    val percentage = if (maxScore > 0) (score.score / maxScore * 100).toInt() else 0
                    val gradeCat = when {
                        percentage >= 80 -> "Excellent (4.0)"
                        percentage >= 70 -> "Good (3.0)"
                        percentage >= 60 -> "Fair (2.0)"
                        percentage >= 50 -> "Pass (1.0)"
                        else -> "Needs Improvement (0.0)"
                    }
                    val dateStr = assessment?.let { readableDateFormat.format(Date(it.timestamp)) } ?: readableDateFormat.format(Date())
                    appendLine(
                        "\"${score.studentId}\",\"${escapeCsv(score.studentName)}\",\"${escapeCsv(assessment?.title ?: "Assessment")}\",\"${score.score}\",\"$maxScore\",\"$percentage%\",\"$gradeCat\",\"$dateStr\""
                    )
                }
            }

            // Summary Matrix
            appendLine()
            appendLine("--- Summary Matrix by Student ---")
            val assessmentHeaders = assessments.joinToString(",") { "\"${escapeCsv(it.title)} (Max ${it.maxScore})\"" }
            appendLine("Student Name,$assessmentHeaders,Average Score")
            
            students.forEach { student ->
                val studentScores = assessments.map { assess ->
                    val s = scores.find { it.assessmentId == assess.id && it.studentId == student.id }
                    s?.score ?: 0f
                }
                val avg = if (studentScores.isNotEmpty()) studentScores.average() else 0.0
                val scoresStr = studentScores.joinToString(",") { String.format(Locale.US, "%.1f", it) }
                appendLine("\"${escapeCsv(student.name)}\",$scoresStr,\"${String.format(Locale.US, "%.1f", avg)}\"")
            }
        }

        return try {
            file.writeText(csvContent, Charsets.UTF_8)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            shareFile(context, uri, "text/csv", "Export Gradebook ($className)")
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Creates a full JSON snapshot backup of all Room Database tables
     */
    fun exportFullDatabaseBackupJson(
        context: Context,
        classes: List<ClassRoomEntity>,
        students: List<StudentEntity>,
        assessments: List<AssessmentEntity>,
        scores: List<StudentScoreEntity>,
        vocabWords: List<VocabWordEntity>,
        inboxMessages: List<InboxMessageEntity>,
        activityLogs: List<ActivityLogEntity>,
        userSettings: UserSettingsEntity?
    ): Uri? {
        val timestamp = dateFormat.format(Date())
        val fileName = "Class_Companion_Backup_$timestamp.json"
        val file = File(context.cacheDir, fileName)

        val rootJson = JSONObject().apply {
            put("backupVersion", 1)
            put("createdAt", System.currentTimeMillis())
            put("formattedDate", readableDateFormat.format(Date()))
            put("appName", "Class Companion")
            
            // Settings
            put("settings", JSONObject().apply {
                put("role", userSettings?.role ?: "teacher")
                put("schoolName", userSettings?.schoolName ?: "")
                put("teacherName", userSettings?.teacherName ?: "")
                put("language", userSettings?.language ?: "en")
            })

            // Classes
            val classesArr = JSONArray()
            classes.forEach { c ->
                classesArr.put(JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("grade", c.grade)
                    put("joinCode", c.joinCode)
                    put("homeworkText", c.homeworkText ?: "")
                    put("homeworkDue", c.homeworkDue ?: "")
                    put("createdAt", c.createdAt)
                })
            }
            put("classes", classesArr)

            // Students
            val studentsArr = JSONArray()
            students.forEach { s ->
                studentsArr.put(JSONObject().apply {
                    put("id", s.id)
                    put("classId", s.classId)
                    put("name", s.name)
                    put("isSubmitted", s.isSubmitted)
                    put("lineLinked", s.lineLinked)
                    put("lineUserId", s.lineUserId ?: "")
                    put("createdAt", s.createdAt)
                })
            }
            put("students", studentsArr)

            // Assessments
            val assessArr = JSONArray()
            assessments.forEach { a ->
                assessArr.put(JSONObject().apply {
                    put("id", a.id)
                    put("classId", a.classId)
                    put("title", a.title)
                    put("maxScore", a.maxScore.toDouble())
                    put("timestamp", a.timestamp)
                })
            }
            put("assessments", assessArr)

            // Scores
            val scoresArr = JSONArray()
            scores.forEach { sc ->
                scoresArr.put(JSONObject().apply {
                    put("id", sc.id)
                    put("assessmentId", sc.assessmentId)
                    put("studentId", sc.studentId)
                    put("studentName", sc.studentName)
                    put("score", sc.score.toDouble())
                })
            }
            put("scores", scoresArr)

            // Vocab
            val vocabArr = JSONArray()
            vocabWords.forEach { v ->
                vocabArr.put(JSONObject().apply {
                    put("id", v.id)
                    put("classId", v.classId)
                    put("en", v.en)
                    put("th", v.th)
                    put("example", v.example ?: "")
                    put("timestamp", v.timestamp)
                })
            }
            put("vocabWords", vocabArr)

            // Inbox
            val inboxArr = JSONArray()
            inboxMessages.forEach { m ->
                inboxArr.put(JSONObject().apply {
                    put("id", m.id)
                    put("studentId", m.studentId)
                    put("classId", m.classId)
                    put("studentName", m.studentName)
                    put("type", m.type)
                    put("text", m.text)
                    put("timestamp", m.timestamp)
                })
            }
            put("inboxMessages", inboxArr)

            // Logs
            val logsArr = JSONArray()
            activityLogs.forEach { l ->
                logsArr.put(JSONObject().apply {
                    put("id", l.id)
                    put("classId", l.classId)
                    put("type", l.type)
                    put("gradeLevel", l.gradeLevel)
                    put("previewText", l.previewText)
                    put("fullContent", l.fullContent)
                    put("timestamp", l.timestamp)
                })
            }
            put("activityLogs", logsArr)
        }

        return try {
            file.writeText(rootJson.toString(2), Charsets.UTF_8)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            shareFile(context, uri, "application/json", "Backup Database to Google Drive / Files")
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Launches Android chooser to share or upload to Google Drive, LINE, Gmail, or Files
     */
    fun shareFile(context: Context, uri: Uri, mimeType: String, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }

    private fun sanitize(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9_.-]"), "_")
    }
}
