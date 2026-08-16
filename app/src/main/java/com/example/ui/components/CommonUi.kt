package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

object AppStrings {
    fun t(key: String, lang: String): String {
        val map = when (lang) {
            "th" -> mapOf(
                "home" to "หน้าหลัก",
                "companion" to "ผู้ช่วยห้องเรียน",
                "worksheets" to "คลังใบงาน & ควิซ",
                "students" to "นักเรียน",
                "vocab" to "คลังคำศัพท์",
                "assess" to "เครื่องมือประเมิน",
                "speaktest" to "สอบพูดและออกเสียง",
                "oral_exam" to "การสอบพูดคำศัพท์",
                "reports" to "รายงาน",
                "settings" to "ตั้งค่า",
                "classes" to "ห้องเรียน",
                "students_connected" to "นักเรียนที่เชื่อมต่อ",
                "activities_gen" to "กิจกรรมที่สร้างแล้ว",
                "generate" to "สร้างกิจกรรม",
                "send_hw" to "ส่งเป็นการบ้าน",
                "add_student" to "เพิ่มนักเรียน",
                "line_signin" to "เข้าสู่ระบบด้วย LINE",
                "roster" to "รายชื่อนักเรียน",
                "homework" to "การบ้านปัจจุบัน",
                "inbox" to "กล่องข้อความจำลอง LINE",
                "save_score" to "บันทึกคะแนน",
                "export" to "ส่งออกรายงาน",
                "mark_done" to "เสร็จแล้ว",
                "undo" to "ยกเลิก",
                "powerpoints" to "สไลด์การสอน PPT",
                "google_drive" to "Google Drive & คลาวด์",
                "share_line" to "แชร์ผ่าน LINE",
                "export_roster" to "ส่งออกรายชื่อ CSV",
                "export_gradebook" to "ส่งออกสมุดคะแนน CSV",
                "remind" to "เตือนความจำ",
                "clear" to "ล้างข้อมูล"
            )
            "zh" -> mapOf(
                "home" to "首页",
                "companion" to "课堂助手",
                "powerpoints" to "PPT 课件互动",
                "google_drive" to "Google Drive 云备份",
                "share_line" to "通过 LINE 分享",
                "export_roster" to "导出学生花名册",
                "export_gradebook" to "导出成绩册 CSV",
                "worksheets" to "工作表与测试",
                "students" to "学生",
                "vocab" to "词汇库",
                "assess" to "评估工具",
                "speaktest" to "口语与发音测试",
                "oral_exam" to "词汇发音口语考试",
                "reports" to "报告",
                "settings" to "设置",
                "classes" to "班级",
                "students_connected" to "已连接学生",
                "activities_gen" to "已生成活动",
                "generate" to "生成活动",
                "send_hw" to "布置为作业",
                "add_student" to "添加学生",
                "line_signin" to "通过 LINE 登录",
                "roster" to "学生花名册",
                "homework" to "当前作业",
                "inbox" to "LINE 模拟收件箱",
                "save_score" to "保存分数",
                "export" to "导出报告",
                "mark_done" to "已完成",
                "undo" to "撤销",
                "remind" to "提醒",
                "clear" to "清除"
            )
            else -> mapOf(
                "home" to "Home",
                "companion" to "Class Companion",
                "powerpoints" to "PowerPoints / PPT",
                "google_drive" to "Google Drive & Cloud",
                "share_line" to "Share via LINE",
                "export_roster" to "Export Roster CSV",
                "export_gradebook" to "Export Gradebook CSV",
                "worksheets" to "Worksheets & Quizizz",
                "students" to "Students",
                "vocab" to "Vocabulary Bank",
                "assess" to "Assessment Tool",
                "speaktest" to "Oral Exam & Pronunciation",
                "oral_exam" to "Vocab Oral Exam",
                "reports" to "Reports",
                "settings" to "Settings",
                "classes" to "Classes",
                "students_connected" to "Students Connected",
                "activities_gen" to "Activities Generated",
                "generate" to "Generate",
                "send_hw" to "Send as Homework",
                "add_student" to "Add Student",
                "line_signin" to "Sign in with LINE",
                "roster" to "Student Roster",
                "homework" to "Current Homework",
                "inbox" to "Class Inbox (Simulated LINE)",
                "save_score" to "Save Score",
                "export" to "Export Report",
                "mark_done" to "Mark Done",
                "undo" to "Undo",
                "remind" to "Remind",
                "clear" to "Clear"
            )
        }
        return map[key] ?: key
    }
}

@Composable
fun ThaiFlagRibbon(
    modifier: Modifier = Modifier,
    height: Float = 4f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.horizontalGradient(
                    0.0f to ThaiFlagRed,
                    0.18f to ThaiFlagRed,
                    0.181f to Color.White,
                    0.28f to Color.White,
                    0.281f to NavyPrimary,
                    0.719f to NavyPrimary,
                    0.72f to Color.White,
                    0.819f to Color.White,
                    0.82f to ThaiFlagRed,
                    1.0f to ThaiFlagRed
                )
            )
    )
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            ),
            color = RoyalBlue
        )
        action?.invoke()
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}
