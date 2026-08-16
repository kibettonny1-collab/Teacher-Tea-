package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StudentEntity
import com.example.ui.ClassCompanionViewModel
import com.example.ui.theme.*
import com.example.util.LineShareHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class LineSimulationScenario(val title: String, val icon: String) {
    HOMEWORK("Homework Broadcast", "📚"),
    REMINDER("Student Reminder", "🔔"),
    GRADEBOOK("Gradebook & Quiz", "📊"),
    GAME_PIN("Smartboard Game PIN", "🎮"),
    SPEAKING("Speaking Practice", "🗣️"),
    CUSTOM("Custom Message", "✍️")
}

@Composable
fun LineMessageSimulatorDialog(
    viewModel: ClassCompanionViewModel,
    initialScenario: LineSimulationScenario = LineSimulationScenario.HOMEWORK,
    preselectedStudent: StudentEntity? = null,
    onDismiss: () -> Unit,
    onTriggerHeadsUpAlert: (LineNotificationAlertState) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activeClass by viewModel.activeClass.collectAsState()
    val students by viewModel.studentsInActiveClass.collectAsState()
    val allScores by viewModel.allScores.collectAsState()
    val allAssessments by viewModel.allAssessments.collectAsState()

    var selectedScenario by remember { mutableStateOf(initialScenario) }
    var selectedStudentId by remember {
        mutableStateOf(preselectedStudent?.id ?: students.firstOrNull { !it.isSubmitted }?.id ?: students.firstOrNull()?.id)
    }

    // Editable Scenario Parameters
    var customTopic by remember { mutableStateOf("Unit 3: Past Tense & Daily Routines") }
    var customContent by remember {
        mutableStateOf(
            activeClass?.homeworkText ?: "Please review the 10 irregular verbs in your vocabulary bank and complete the 5 fill-in-the-blank practice sentences."
        )
    }
    var customDueDate by remember { mutableStateOf(activeClass?.homeworkDue ?: "Tomorrow 17:00") }
    var customTitle by remember { mutableStateOf("Class Announcement") }
    var speakingPhrase by remember { mutableStateOf("I went to the market and bought fresh fruit yesterday.") }
    var speakingThai by remember { mutableStateOf("เมื่อวานฉันไปตลาดและซื้อผลไม้สด") }

    // Student Reply Simulation State
    var studentReplyText by remember { mutableStateOf("") }
    var simulatedReplies by remember { mutableStateOf(listOf<Pair<String, Long>>()) }
    var isReadReceiptShown by remember { mutableStateOf(true) }
    var intentTestedSuccess by remember { mutableStateOf(false) }

    val currentStudent = students.find { it.id == selectedStudentId } ?: students.firstOrNull()
    val className = activeClass?.name ?: "M.1/3"
    val grade = activeClass?.grade ?: "M.1"
    val joinCode = activeClass?.joinCode ?: "M13TH"

    // Construct the live formatted LINE message based on current scenario
    val formattedMessage = remember(
        selectedScenario,
        currentStudent,
        customTopic,
        customContent,
        customDueDate,
        customTitle,
        speakingPhrase,
        speakingThai,
        className,
        grade,
        joinCode
    ) {
        when (selectedScenario) {
            LineSimulationScenario.HOMEWORK -> LineShareHelper.formatHomeworkMessage(
                className = className,
                grade = grade,
                lessonTopic = customTopic,
                homeworkContent = customContent,
                dueDate = customDueDate,
                joinCode = joinCode
            )
            LineSimulationScenario.REMINDER -> LineShareHelper.formatStudentReminderMessage(
                studentName = currentStudent?.name ?: "Nong Ploy",
                className = className,
                homeworkTopic = customTopic,
                dueDate = customDueDate
            )
            LineSimulationScenario.GRADEBOOK -> {
                val classScores = allScores.filter { s -> allAssessments.any { a -> a.id == s.assessmentId && a.classId == activeClass?.id } }
                val avg = if (classScores.isNotEmpty()) classScores.map { it.score }.average().toFloat() else 8.5f
                val subCount = students.count { it.isSubmitted }
                LineShareHelper.formatGradebookReportMessage(
                    className = className,
                    assessmentTitle = "Weekly Unit Quiz & Speaking Check",
                    avgScore = avg,
                    maxScore = 10f,
                    totalStudents = students.size.coerceAtLeast(1),
                    submittedCount = subCount
                )
            }
            LineSimulationScenario.GAME_PIN -> LineShareHelper.formatPowerPointDeckMessage(
                deckTitle = "Smartboard Grammar Mystery Box Challenge",
                deckDescription = "Team Quiz Game on Present Continuous & Action Verbs",
                gradeLevel = grade,
                gamePin = joinCode
            )
            LineSimulationScenario.SPEAKING -> LineShareHelper.formatSpeakingPracticeMessage(
                className = className,
                phraseEn = speakingPhrase,
                phonetic = "/aɪ wɛnt tu ðə ˈmɑːrkɪt/",
                thaiMeaning = speakingThai
            )
            LineSimulationScenario.CUSTOM -> LineShareHelper.formatCustomAnnouncementMessage(
                className = className,
                title = customTitle,
                content = customContent
            )
        }
    }

    val isInstalled = remember { LineShareHelper.isLineInstalled(context) }
    val lineUrlScheme = remember(formattedMessage) { LineShareHelper.getLineUrlScheme(formattedMessage) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 8.dp)
                .testTag("line_simulator_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Simulator Top Bar
                Surface(
                    color = LineGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LINE", color = LineGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "LINE Integration Simulator",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0x33FFFFFF)
                                    ) {
                                        Text(
                                            text = "PROD READY",
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "จำลองการส่งข้อความแจ้งเตือนและการตอบกลับของนักเรียน",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE8F8EF), fontSize = 11.sp)
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Scenario Selector Chips
                    Text(
                        text = "1. Select Message Scenario · เลือกรูปแบบข้อความ",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LineSimulationScenario.values().take(3).forEach { sc ->
                            FilterChip(
                                selected = selectedScenario == sc,
                                onClick = { selectedScenario = sc },
                                label = { Text("${sc.icon} ${sc.title}", fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LineGreen,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LineSimulationScenario.values().drop(3).forEach { sc ->
                            FilterChip(
                                selected = selectedScenario == sc,
                                onClick = { selectedScenario = sc },
                                label = { Text("${sc.icon} ${sc.title}", fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LineGreen,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Target Student Selector if Reminder scenario
                    if (selectedScenario == LineSimulationScenario.REMINDER && students.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CoralRedLight),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CoralRed, ThaiGold)))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Target Student for Reminder:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CoralRed)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    students.take(5).forEach { st ->
                                        val isSel = st.id == selectedStudentId
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) CoralRed else Color.White,
                                            border = if (isSel) null else CardDefaults.outlinedCardBorder(),
                                            modifier = Modifier.clickable { selectedStudentId = st.id }
                                        ) {
                                            Text(
                                                text = "${st.name} ${if (st.isSubmitted) "✓" else "⏳"}",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontSize = 11.sp,
                                                color = if (isSel) Color.White else TextInk,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Scenario 2: Authentic LINE Chat Simulation Viewport
                    Text(
                        text = "2. Student LINE App Viewport · หน้าจอแอป LINE ของนักเรียน",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    // Phone Frame Wrapper
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF8CABD9)), // Classic LINE Chat background
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(LineGreen, RoyalBlue)))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // LINE Chat Room Header
                            Surface(
                                color = Color(0xFF263238),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(LineGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🏫", fontSize = 16.sp)
                                        }
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "Class Companion OA",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                                Text("🛡️", fontSize = 10.sp) // Verified badge
                                            }
                                            Text(
                                                text = if (selectedScenario == LineSimulationScenario.REMINDER) "1-on-1 Chat with ${currentStudent?.name ?: "Student"}" else "Group: $className ($grade)",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0BEC5), fontSize = 10.sp)
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            // Chat Content Area
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Date Pill
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0x4D000000)
                                    ) {
                                        Text(
                                            text = "Today ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Incoming Message Bubble from Official Account (Left side)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(LineGreen)
                                            .border(1.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🤖", fontSize = 18.sp)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Class Companion English Bot",
                                            fontSize = 11.sp,
                                            color = Color(0xFF263238),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                                        )

                                        // Rich LINE Flex Message Card
                                        Surface(
                                            shape = RoundedCornerShape(
                                                topStart = 2.dp,
                                                topEnd = 14.dp,
                                                bottomEnd = 14.dp,
                                                bottomStart = 14.dp
                                            ),
                                            color = Color.White,
                                            shadowElevation = 3.dp
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                // Header Banner
                                                Surface(
                                                    color = when (selectedScenario) {
                                                        LineSimulationScenario.REMINDER -> CoralRed
                                                        LineSimulationScenario.GRADEBOOK -> RoyalBlue
                                                        LineSimulationScenario.GAME_PIN -> ThaiGold
                                                        else -> LineGreen
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "${selectedScenario.icon} ${selectedScenario.title}",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        )
                                                        Text(
                                                            text = "$className · $grade",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                color = Color.White.copy(alpha = 0.9f),
                                                                fontSize = 9.sp
                                                            )
                                                        )
                                                    }
                                                }

                                                // Message Text Body
                                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text(
                                                        text = formattedMessage,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = Color(0xFF1E293B),
                                                            lineHeight = 18.sp,
                                                            fontFamily = FontFamily.Default
                                                        )
                                                    )

                                                    Divider(color = BorderLine, thickness = 0.8.dp)

                                                    // Interactive Action Buttons inside LINE Bubble
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                viewModel.speechManager.speak(
                                                                    if (selectedScenario == LineSimulationScenario.SPEAKING) speakingPhrase else customContent
                                                                )
                                                            },
                                                            modifier = Modifier.weight(1f),
                                                            shape = RoundedCornerShape(6.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = LineGreenLight, contentColor = LineGreen),
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                                        ) {
                                                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Listen (TTS)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                        }

                                                        Button(
                                                            onClick = {
                                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                                clipboard.setPrimaryClip(ClipData.newPlainText("LINE_Message", formattedMessage))
                                                                Toast.makeText(context, "Copied formatted text!", Toast.LENGTH_SHORT).show()
                                                            },
                                                            modifier = Modifier.weight(1f),
                                                            shape = RoundedCornerShape(6.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = BackgroundLight, contentColor = TextInk),
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                                        ) {
                                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Copy Text", fontSize = 10.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Time & Read Status
                                    Column(horizontalAlignment = Alignment.Start) {
                                        if (isReadReceiptShown) {
                                            Text(
                                                text = "Read 1\nอ่านแล้ว",
                                                fontSize = 9.sp,
                                                color = Color(0xFF455A64),
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 11.sp
                                            )
                                        }
                                        Text(
                                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                                            fontSize = 9.sp,
                                            color = Color(0xFF546E7A)
                                        )
                                    }
                                }

                                // Simulated Student Replies (Right Side)
                                simulatedReplies.forEach { reply ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(reply.second)),
                                            fontSize = 9.sp,
                                            color = Color(0xFF546E7A),
                                            modifier = Modifier.padding(end = 4.dp)
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(
                                                topStart = 14.dp,
                                                topEnd = 2.dp,
                                                bottomEnd = 14.dp,
                                                bottomStart = 14.dp
                                            ),
                                            color = Color(0xFF85E249), // Authentic student outgoing bubble
                                            shadowElevation = 2.dp
                                        ) {
                                            Text(
                                                text = reply.first,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                fontSize = 12.sp,
                                                color = Color(0xFF1B3800),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                // Interactive Student Reply Simulator Bar
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.95f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Simulate Student Reply (ส่งการบ้านกลับ / ตอบกลับ):",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )

                                        // Quick reply templates
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf(
                                                "✅ หนูส่งการบ้านแล้วค่ะ!",
                                                "🙋‍♂️ ทำข้อสอบเสร็จแล้วครับ",
                                                "🎙️ ฝึกพูดตามคลิปแล้วค่ะ"
                                            ).forEach { quickText ->
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = LineGreenLight,
                                                    border = BorderStroke(0.5.dp, LineGreen),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable {
                                                            val st = currentStudent ?: students.firstOrNull()
                                                            if (st != null) {
                                                                simulatedReplies = simulatedReplies + (quickText to System.currentTimeMillis())
                                                                viewModel.sendMessage(
                                                                    studentId = st.id,
                                                                    studentName = st.name,
                                                                    text = "💬 [LINE Reply from ${st.name}]: $quickText",
                                                                    type = "line_reply"
                                                                )
                                                                if (quickText.contains("ส่งการบ้าน")) {
                                                                    viewModel.toggleStudentSubmission(st.id, false)
                                                                }
                                                                Toast.makeText(context, "Student reply received in Teacher Inbox!", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                ) {
                                                    Text(
                                                        text = quickText,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                                        fontSize = 9.sp,
                                                        color = LineGreen,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }

                                        // Custom student text input
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = studentReplyText,
                                                onValueChange = { studentReplyText = it },
                                                placeholder = { Text("Type simulated student response...", fontSize = 11.sp) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp),
                                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                shape = RoundedCornerShape(8.dp),
                                                singleLine = true
                                            )

                                            Button(
                                                onClick = {
                                                    if (studentReplyText.isNotBlank()) {
                                                        val st = currentStudent ?: students.firstOrNull()
                                                        if (st != null) {
                                                            simulatedReplies = simulatedReplies + (studentReplyText to System.currentTimeMillis())
                                                            viewModel.sendMessage(
                                                                studentId = st.id,
                                                                studentName = st.name,
                                                                text = "💬 [LINE Reply from ${st.name}]: $studentReplyText",
                                                                type = "line_reply"
                                                            )
                                                            studentReplyText = ""
                                                            Toast.makeText(context, "Reply logged to Teacher Inbox!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = LineGreen),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(44.dp)
                                            ) {
                                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section 3: Production Intent & System Flow Inspector
                    Text(
                        text = "3. Production Intent Verification & Live Actions · ตรวจสอบ Intent จริง",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = RoyalBlue)
                                    Column {
                                        Text(
                                            text = "Android Intent Dispatch Pipeline",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        )
                                        Text(
                                            text = if (isInstalled) "Direct Package Intent Active (${LineShareHelper.LINE_PACKAGE_NAME})" else "Fallback URL Scheme Active (line.me/R/msg/text/?)",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isInstalled) LineGreen else Color(0xFFB45309),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isInstalled) EmeraldGreenLight else ThaiGoldLight.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = if (isInstalled) "● LINE INSTALLED" else "● WEB SCHEME FALLBACK",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        fontSize = 9.sp,
                                        color = if (isInstalled) EmeraldGreen else Color(0xFFB45309),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Intent parameters box
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BackgroundLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Intent Action: android.intent.action.SEND",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "MIME Type: text/plain",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "Scheme Target: ${lineUrlScheme.take(50)}...",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = RoyalBlue
                                    )
                                }
                            }

                            // Live Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val success = LineShareHelper.dispatchToLine(context, formattedMessage, "Class Companion LINE Share")
                                        intentTestedSuccess = success
                                        if (success) {
                                            Toast.makeText(context, "Dispatched Android Intent to LINE!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dispatch Real Intent", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        onTriggerHeadsUpAlert(
                                            LineNotificationAlertState(
                                                senderName = "Class Companion OA (${activeClass?.name ?: "M.1/3"})",
                                                studentTarget = currentStudent?.name,
                                                title = "${selectedScenario.icon} ${selectedScenario.title}",
                                                messagePreview = formattedMessage.take(100) + "...",
                                                fullContent = formattedMessage
                                            )
                                        )
                                        Toast.makeText(context, "Triggered Heads-Up LINE Notification Banner!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue, contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Alert Banner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Dismiss Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close Simulator · ปิดหน้าต่างจำลอง")
                    }
                }
            }
        }
    }
}
