package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InboxMessageEntity
import com.example.data.model.StudentEntity
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.LineNotificationAlertState
import com.example.ui.components.LineSimulationScenario
import com.example.ui.components.QrCodeView
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.util.DataExportHelper
import com.example.util.LineShareHelper
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StudentsScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val activeClass by viewModel.activeClass.collectAsState()
    val students by viewModel.studentsInActiveClass.collectAsState()
    val inboxMessages by viewModel.inboxInActiveClass.collectAsState()
    val context = LocalContext.current

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showLineSimulateDialog by remember { mutableStateOf(false) }

    val submittedCount = students.count { it.isSubmitted }
    val totalCount = students.size
    val progress = if (totalCount > 0) submittedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Active Class Summary & QR Join Code Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("students_class_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, RoyalBlueLight)))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STUDENT ROSTER · รายชื่อนักเรียน",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = RoyalBlue
                            )
                            Text(
                                text = "${activeClass?.name ?: "M.1/3"} (${activeClass?.grade ?: "M.1"})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.openCameraQrScanner() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color.White),
                                modifier = Modifier.testTag("scan_roster_qr_btn"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Roster QR", modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Scan QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { showQrDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Join QR", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { showAddStudentDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Add", fontSize = 11.sp)
                            }
                        }
                    }

                    // Roster CSV Export & LINE Actions Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                val uri = DataExportHelper.exportStudentRosterCsv(
                                    context = context,
                                    className = activeClass?.name ?: "M.1/3",
                                    grade = activeClass?.grade ?: "M.1",
                                    joinCode = activeClass?.joinCode ?: "M13TH",
                                    students = students
                                )
                                if (uri != null) {
                                    Toast.makeText(context, "Exported Student Roster CSV to storage!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Export CSV", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.openLineSimulator(LineSimulationScenario.HOMEWORK)
                            },
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LineGreen),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(LineGreen, EmeraldGreen))),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("💬", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("LINE Simulator", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        Button(
                            onClick = {
                                val currentClass = activeClass
                                if (currentClass != null) {
                                    LineShareHelper.shareHomework(
                                        context = context,
                                        className = currentClass.name,
                                        grade = currentClass.grade,
                                        lessonTopic = "Weekly English Assignment",
                                        homeworkContent = currentClass.homeworkText ?: "Please review lesson vocabulary and submit on Class Companion.",
                                        dueDate = currentClass.homeworkDue ?: "Tomorrow 17:00",
                                        joinCode = currentClass.joinCode
                                    )
                                    viewModel.triggerLineHeadsUpAlert(
                                        LineNotificationAlertState(
                                            senderName = "Class Companion OA (${currentClass.name})",
                                            title = "📚 English Homework Broadcast",
                                            messagePreview = currentClass.homeworkText ?: "Please review lesson vocabulary and submit on Class Companion."
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("🟢", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Broadcast", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RoyalBlueLight, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Class Join Code: ${activeClass?.joinCode ?: "M13TH"}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                            )
                        }
                        Text(
                            text = "${students.count { it.lineLinked }} LINE Linked",
                            style = MaterialTheme.typography.labelSmall.copy(color = LineGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Current Homework Tracking Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (activeClass?.homeworkText != null) NavyDark else BackgroundLight
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Assignment,
                                contentDescription = null,
                                tint = if (activeClass?.homeworkText != null) ThaiGold else TextMuted
                            )
                            Text(
                                text = "Current Homework Assignment",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeClass?.homeworkText != null) Color.White else NavyPrimary
                                )
                            )
                        }

                        if (activeClass?.homeworkText != null) {
                            TextButton(
                                onClick = { viewModel.clearHomework() },
                                colors = ButtonDefaults.textButtonColors(contentColor = CoralRedLight)
                            ) {
                                Text("Clear HW", fontSize = 12.sp)
                            }
                        }
                    }

                    if (activeClass?.homeworkText != null) {
                        Text(
                            text = activeClass?.homeworkText ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSubtle,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Submission: $submittedCount / $totalCount Done (${(progress * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (submittedCount == totalCount && totalCount > 0) EmeraldGreenLight else ThaiGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (!activeClass?.homeworkDue.isNullOrBlank()) {
                                Text(
                                    text = "Due: ${activeClass?.homeworkDue}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSubtle)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (progress >= 1f) EmeraldGreen else ThaiGold,
                            trackColor = NavySecondary
                        )
                    } else {
                        Text(
                            text = "No active homework assigned yet. Head to Class Companion to generate and push an activity to this class!",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Student Roster Section Header
        item {
            SectionHeader(
                title = "Enrolled Students (${students.size})",
                action = {
                    TextButton(onClick = { showLineSimulateDialog = true }) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = LineGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LINE Connect Demo", color = LineGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Student List Items
        if (students.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderLine)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = "No Students Enrolled Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NavyPrimary
                        )
                        Text(
                            text = "Quickly populate your class roster by scanning printed QR codes on student desks, badges, or physical roster sheets.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, textAlign = TextAlign.Center)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openCameraQrScanner() },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("empty_state_scan_qr_btn")
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Roster QR", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { showAddStudentDialog = true },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Add Manually")
                            }
                        }
                    }
                }
            }
        } else {
            items(students, key = { it.id }) { student ->
                StudentRowItem(
                    student = student,
                    onToggleSubmit = { viewModel.toggleStudentSubmission(student.id, student.isSubmitted) },
                    onSendReminder = {
                        viewModel.sendReminder(student)
                        viewModel.triggerLineHeadsUpAlert(
                            LineNotificationAlertState(
                                senderName = "Class Companion OA (${activeClass?.name ?: "M.1/3"})",
                                title = "⏰ Homework Due Reminder",
                                messagePreview = "Sent reminder to ${student.name}: Please finish your assignment on time!"
                            )
                        )
                    },
                    onSendLineShare = {
                        viewModel.openLineSimulator(LineSimulationScenario.REMINDER, student)
                    }
                )
            }
        }

        // Simulated LINE Inbox Messages Header
        item {
            SectionHeader(
                title = "Class Inbox · กล่องข้อความ LINE (${inboxMessages.size})"
            )
        }

        // Inbox Stream Feed
        if (inboxMessages.isEmpty()) {
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No simulated messages yet. When you assign homework or send reminders, notifications appear here.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, textAlign = TextAlign.Center),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(inboxMessages.take(6), key = { it.id }) { msg ->
                InboxMessageCard(msg)
            }
        }
    }

    // Dialog: Add Student
    if (showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onAdd = { name, isLineLinked ->
                viewModel.addStudent(name, isLineLinked)
                showAddStudentDialog = false
            }
        )
    }

    // Dialog: QR Code
    if (showQrDialog) {
        val joinUrl = "https://line.me/R/ti/p/@classcompanion_${activeClass?.joinCode ?: "M13TH"}"
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Text(
                    text = "Scan to Join ${activeClass?.name ?: "Class"}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Students scan this QR code with their LINE camera to auto-connect to the class homework channel.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, textAlign = TextAlign.Center)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    QrCodeView(data = joinUrl, size = 180.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CODE: ${activeClass?.joinCode ?: "M13TH"}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalBlue,
                            letterSpacing = 2.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showQrDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Dialog: LINE Connect Simulation
    if (showLineSimulateDialog) {
        LineConnectSimulationDialog(
            className = activeClass?.name ?: "M.1/3",
            onSimulateJoin = { studentName ->
                viewModel.signInWithLine(studentName)
                showLineSimulateDialog = false
            },
            onDismiss = { showLineSimulateDialog = false }
        )
    }
}

@Composable
private fun StudentRowItem(
    student: StudentEntity,
    onToggleSubmit: () -> Unit,
    onSendReminder: () -> Unit,
    onSendLineShare: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_item_${student.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(if (student.isSubmitted) EmeraldGreenLight else BorderLine, BorderLine)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (student.lineLinked) LineGreenLight else RoyalBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (student.lineLinked) LineGreen else RoyalBlue
                        )
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        )
                        if (student.lineLinked) {
                            Surface(
                                color = LineGreenLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "LINE",
                                    style = MaterialTheme.typography.labelSmall.copy(color = LineGreen, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = if (student.isSubmitted) "✅ Submitted Homework" else "⏳ Homework Pending",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (student.isSubmitted) EmeraldGreen else CoralRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!student.isSubmitted) {
                    IconButton(
                        onClick = onSendLineShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("🟢", fontSize = 14.sp)
                    }

                    IconButton(
                        onClick = onSendReminder,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Remind", tint = CoralRed, modifier = Modifier.size(18.dp))
                    }
                }

                IconButton(
                    onClick = onToggleSubmit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (student.isSubmitted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle status",
                        tint = if (student.isSubmitted) EmeraldGreen else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun InboxMessageCard(msg: InboxMessageEntity) {
    val isReminder = msg.type == "reminder"
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = remember(msg.timestamp) { timeFormat.format(Date(msg.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isReminder) CoralRedLight else RoyalBlueLight
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isReminder) "🔔 Reminder to ${msg.studentName}" else "📝 HW to ${msg.studentName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )
                }
                Text(text = timeStr, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodySmall.copy(color = TextInk, lineHeight = 16.sp)
            )
        }
    }
}

@Composable
private fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var linkLine by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Student · เพิ่มนักเรียน") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name (e.g. Nong Ploy / Somchai)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = linkLine, onCheckedChange = { linkLine = it })
                    Text("Auto-link LINE account (จำลองเชื่อม LINE)", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onAdd(name.trim(), linkLine)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Add Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun LineConnectSimulationDialog(
    className: String,
    onSimulateJoin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var simulatedName by remember { mutableStateOf("Nong Fern (นกเฟิร์น)") }
    var selectedAvatar by remember { mutableStateOf("👧") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LineGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LINE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                }
                Column {
                    Text("LINE Login · เข้าสู่ระบบด้วย LINE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Class Companion OA Integration", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Authorize Class Companion to link your student profile with $className for automatic homework delivery and speaking practice.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextInk, lineHeight = 18.sp)
                )

                // Avatar picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("👧", "👦", "🧒", "🎒", "⭐").forEach { av ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (selectedAvatar == av) LineGreen.copy(alpha = 0.2f) else SurfaceCard)
                                .border(
                                    1.5.dp,
                                    if (selectedAvatar == av) LineGreen else BorderLine,
                                    CircleShape
                                )
                                .clickable { selectedAvatar = av },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(av, fontSize = 20.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = simulatedName,
                    onValueChange = { simulatedName = it },
                    label = { Text("LINE Display Name (ชื่อใน LINE)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = LineGreen.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("✓ Profile information & LINE user ID", style = MaterialTheme.typography.bodySmall.copy(color = LineGreen, fontWeight = FontWeight.Bold))
                        Text("✓ Receive English homework in 1-on-1 chat", style = MaterialTheme.typography.bodySmall.copy(color = LineGreen, fontWeight = FontWeight.Bold))
                        Text("✓ Send oral test audio recordings to teacher", style = MaterialTheme.typography.bodySmall.copy(color = LineGreen, fontWeight = FontWeight.Bold))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (simulatedName.isNotBlank()) onSimulateJoin(simulatedName.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agree & Connect LINE Account", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
