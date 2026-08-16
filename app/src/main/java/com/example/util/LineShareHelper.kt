package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object LineShareHelper {

    const val LINE_PACKAGE_NAME = "jp.naver.line.android"
    const val LINE_URL_SCHEME_PREFIX = "https://line.me/R/msg/text/?"

    /**
     * Checks if the official LINE app is installed on the user's Android device
     */
    fun isLineInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(LINE_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Formats a standard classroom homework assignment message for LINE
     */
    fun formatHomeworkMessage(
        className: String,
        grade: String,
        lessonTopic: String,
        homeworkContent: String,
        dueDate: String?,
        joinCode: String
    ): String {
        return buildString {
            appendLine("📚 [Class Companion - การบ้านภาษาอังกฤษ]")
            appendLine("🏫 ห้องเรียน / Class: $className ($grade)")
            appendLine("📖 หัวข้อบทเรียน / Topic: $lessonTopic")
            if (!dueDate.isNullOrBlank()) {
                appendLine("⏰ กำหนดส่ง / Due Date: $dueDate")
            }
            appendLine("🔑 รหัสเข้าห้องเรียน / Join Code: $joinCode")
            appendLine("----------------------------------")
            appendLine("✍️ คำสั่งและการบ้าน / Assignment:")
            appendLine(homeworkContent)
            appendLine("----------------------------------")
            appendLine("💡 นักเรียนสามารถส่งการบ้านและฝึกออกเสียงได้ที่แอป Class Companion")
        }
    }

    /**
     * Formats an individual student reminder message for LINE
     */
    fun formatStudentReminderMessage(
        studentName: String,
        className: String,
        homeworkTopic: String?,
        dueDate: String?
    ): String {
        return buildString {
            appendLine("⏰ [แจ้งเตือนการบ้าน / Homework Reminder]")
            appendLine("สวัสดีครับ/ค่ะ $studentName 👋")
            appendLine("คุณครูขอเตือนการส่งการบ้านวิชาภาษาอังกฤษ ห้อง $className")
            if (!homeworkTopic.isNullOrBlank()) {
                appendLine("📝 งาน: $homeworkTopic")
            }
            if (!dueDate.isNullOrBlank()) {
                appendLine("📅 กำหนดส่ง: $dueDate")
            }
            appendLine("กรุณาส่งงานให้เรียบร้อยก่อนหมดเวลานะครับ สู้ๆ ครับ! ✨")
        }
    }

    /**
     * Formats a gradebook report / class average summary for LINE
     */
    fun formatGradebookReportMessage(
        className: String,
        assessmentTitle: String,
        avgScore: Float,
        maxScore: Float,
        totalStudents: Int,
        submittedCount: Int
    ): String {
        val percentage = if (maxScore > 0) (avgScore / maxScore * 100).toInt() else 0
        return buildString {
            appendLine("📊 [สรุปผลคะแนนประจำสัปดาห์ / Gradebook Summary]")
            appendLine("🏫 ห้องเรียน / Class: $className")
            appendLine("📝 การประเมิน / Assessment: $assessmentTitle")
            appendLine("👥 จำนวนนักเรียน / Total: $totalStudents คน (ส่งแล้ว $submittedCount คน)")
            appendLine("⭐ คะแนนเฉลี่ย / Class Average: ${String.format("%.1f", avgScore)} / ${String.format("%.0f", maxScore)} ($percentage%)")
            appendLine("----------------------------------")
            appendLine("🎉 ยินดีกับนักเรียนทุกคนที่ตั้งใจทำกิจกรรมในสัปดาห์นี้ครับ! 👏")
        }
    }

    /**
     * Formats a PowerPoint / Smartboard presentation PIN for LINE
     */
    fun formatPowerPointDeckMessage(
        deckTitle: String,
        deckDescription: String,
        gradeLevel: String,
        gamePin: String
    ): String {
        return buildString {
            appendLine("🎮 [Smartboard Presentation & Game PIN]")
            appendLine("🌟 กิจกรรม: $deckTitle ($gradeLevel)")
            appendLine("📝 คำอธิบาย: $deckDescription")
            appendLine("🔢 Game / Room PIN: $gamePin")
            appendLine("----------------------------------")
            appendLine("👉 เข้าร่วมตอบคำถามและแข่งเกมบนหน้าจอใหญ่ในห้องเรียนได้เลยครับ!")
        }
    }

    /**
     * Formats an oral speaking challenge message for LINE
     */
    fun formatSpeakingPracticeMessage(
        className: String,
        phraseEn: String,
        phonetic: String?,
        thaiMeaning: String
    ): String {
        return buildString {
            appendLine("🎙️ [ฝึกออกเสียงภาษาอังกฤษ / Daily Speaking Challenge]")
            appendLine("🏫 ห้องเรียน / Class: $className")
            appendLine("🗣️ ประโยคฝึกพูด / Sentence:")
            appendLine("\"$phraseEn\"")
            if (!phonetic.isNullOrBlank()) {
                appendLine("🔊 คำอ่าน / Phonetic: $phonetic")
            }
            appendLine("🇹🇭 ความหมาย / Thai: $thaiMeaning")
            appendLine("----------------------------------")
            appendLine("💡 อัดเสียงตอบกลับใน Class Companion หรือในแชทนี้เพื่อรับคะแนนโบนัส!")
        }
    }

    /**
     * Formats a custom announcement message for LINE
     */
    fun formatCustomAnnouncementMessage(
        className: String,
        title: String,
        content: String
    ): String {
        return buildString {
            appendLine("📢 [ประกาศห้องเรียน / Classroom Announcement]")
            appendLine("🏫 ห้องเรียน / Class: $className")
            appendLine("📌 เรื่อง: $title")
            appendLine("----------------------------------")
            appendLine(content)
            appendLine("----------------------------------")
            appendLine("✨ Class Companion English Hub")
        }
    }

    /**
     * Computes the encoded LINE URL Scheme for a given message
     */
    fun getLineUrlScheme(message: String): String {
        return try {
            val encoded = URLEncoder.encode(message, "UTF-8")
            "$LINE_URL_SCHEME_PREFIX$encoded"
        } catch (e: Exception) {
            "$LINE_URL_SCHEME_PREFIX$message"
        }
    }

    /**
     * Shares a formatted English Classroom Homework Assignment directly to LINE
     */
    fun shareHomework(
        context: Context,
        className: String,
        grade: String,
        lessonTopic: String,
        homeworkContent: String,
        dueDate: String?,
        joinCode: String
    ) {
        val message = formatHomeworkMessage(className, grade, lessonTopic, homeworkContent, dueDate, joinCode)
        dispatchToLine(context, message, "Share Homework via LINE")
    }

    /**
     * Sends an individual reminder message to a student via LINE
     */
    fun shareStudentReminder(
        context: Context,
        studentName: String,
        className: String,
        homeworkTopic: String?,
        dueDate: String?
    ) {
        val message = formatStudentReminderMessage(studentName, className, homeworkTopic, dueDate)
        dispatchToLine(context, message, "Send LINE Reminder to $studentName")
    }

    /**
     * Shares a classroom grade summary / leaderboard report to LINE Class Group
     */
    fun shareGradebookReport(
        context: Context,
        className: String,
        assessmentTitle: String,
        avgScore: Float,
        maxScore: Float,
        totalStudents: Int,
        submittedCount: Int
    ) {
        val message = formatGradebookReportMessage(className, assessmentTitle, avgScore, maxScore, totalStudents, submittedCount)
        dispatchToLine(context, message, "Share Grade Report to LINE")
    }

    /**
     * Shares an interactive PowerPoint Presentation / Smartboard game PIN to LINE
     */
    fun sharePowerPointDeck(
        context: Context,
        deckTitle: String,
        deckDescription: String,
        gradeLevel: String,
        gamePin: String
    ) {
        val message = formatPowerPointDeckMessage(deckTitle, deckDescription, gradeLevel, gamePin)
        dispatchToLine(context, message, "Share PowerPoint PIN to LINE")
    }

    /**
     * Dispatches text to LINE using the fastest available route (Direct package intent -> LINE web scheme -> Android Chooser)
     */
    fun dispatchToLine(context: Context, message: String, title: String): Boolean {
        val isInstalled = isLineInstalled(context)

        if (isInstalled) {
            try {
                val directIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    `package` = LINE_PACKAGE_NAME
                    putExtra(Intent.EXTRA_TEXT, message)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(directIntent)
                return true
            } catch (e: Exception) {
                // Fallback to URL Scheme if direct package fails
            }
        }

        // Fallback 1: LINE Universal URL scheme
        try {
            val encodedText = URLEncoder.encode(message, "UTF-8")
            val lineUri = Uri.parse("$LINE_URL_SCHEME_PREFIX$encodedText")
            val webIntent = Intent(Intent.ACTION_VIEW, lineUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            return true
        } catch (e: Exception) {
            // Fallback 2: General Android Chooser
            return try {
                val chooserIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(chooserIntent, title).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        }
    }
}
