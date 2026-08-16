package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.LineNotificationAlertState
import com.example.ui.components.LineSimulationScenario
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThaiFlagRibbon
import com.example.ui.theme.*
import com.example.util.LineShareHelper

@Composable
fun HomeScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val classes by viewModel.allClasses.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val activeClass by viewModel.activeClass.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val lang = settings?.language ?: "en"
    val context = LocalContext.current

    val pendingCount = classes.sumOf { cls ->
        if (cls.homeworkText != null) {
            allStudents.count { it.classId == cls.id && !it.isSubmitted }
        } else 0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Hero Card with Image & Greeting
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = "Classroom scene",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, NavyPrimary.copy(alpha = 0.95f)),
                                        startY = 40f
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ThaiGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎓", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = if (settings?.role == "school") settings?.schoolName ?: "My School" else "Sawadee, ${settings?.teacherName ?: "Teacher"}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Ready to elevate your English classroom today",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ThaiGoldLight)
                                )
                            }
                        }
                    }
                    ThaiFlagRibbon(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // 3 Key Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = classes.size.toString(),
                    label = "Classes",
                    icon = Icons.Default.School
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = allStudents.size.toString(),
                    label = "Students",
                    icon = Icons.Default.People
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = allLogs.size.toString(),
                    label = "Generated",
                    icon = Icons.Default.AutoAwesome
                )
            }
        }

        // Active Class Quick Launcher Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, RoyalBlueLight)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE CLASSROOM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = RoyalBlue
                            )
                            Text(
                                text = "${activeClass?.name ?: "M.1/3"} (${activeClass?.grade ?: "M.1"})",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = NavyPrimary
                            )
                        }
                        StatusBadge(
                            text = "Join: ${activeClass?.joinCode ?: "M13TH"}",
                            color = NavyPrimary,
                            bgColor = ThaiGoldContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.currentScreen.value = "companion" },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_create_activity_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Activity", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = { viewModel.currentScreen.value = "worksheets" },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_worksheets_hub_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(ThaiGold, RoyalBlue)))
                        ) {
                            Text("📚", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Worksheets", style = MaterialTheme.typography.labelLarge, color = NavyPrimary, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.currentScreen.value = "powerpoints" },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_powerpoints_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.PresentToAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PPT", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PowerPoint Presentations & Smartboard Games Hub Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.currentScreen.value = "powerpoints" }
                    .testTag("home_ppt_hero_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ThaiGold, RoyalBlue)))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ThaiGoldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🖥️", fontSize = 22.sp)
                            }
                            Column {
                                Text(
                                    text = "iSLCollective PowerPoint Presentations & Games",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NavyPrimary)
                                )
                                Text(
                                    text = "สไลด์การสอน PPT & เกมตอบคำถาม Smartboard จอใหญ่",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Interactive presentation decks designed for Thai secondary classrooms (M.1–M.6), including Game Shows, Grammar Quests, Speed Warm-ups, and Mystery Wordwalls with timer & sounds.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextInk)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.currentScreen.value = "powerpoints" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.PresentToAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open PowerPoint Hub", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.openLineSimulator(LineSimulationScenario.GAME_PIN)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldGreen),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(EmeraldGreen, RoyalBlue)))
                        ) {
                            Text("🟢", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simulate & Share PIN", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Pending Homework Alert if any
        if (pendingCount > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CoralRedLight),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CoralRed, ThaiGold)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = CoralRed)
                                Column {
                                    Text(
                                        text = "Needs Attention · รอดำเนินการ",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = CoralRed
                                    )
                                    Text(
                                        text = "$pendingCount student(s) haven't submitted current homework",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextInk)
                                    )
                                }
                            }
                            TextButton(
                                onClick = { viewModel.currentScreen.value = "students" },
                                colors = ButtonDefaults.textButtonColors(contentColor = CoralRed)
                            ) {
                                Text("View Roster", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        // Direct LINE Broadcast & Simulator Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.openLineSimulator(LineSimulationScenario.REMINDER)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LINE Simulator", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }

                            Button(
                                onClick = {
                                    val currentClass = activeClass ?: classes.firstOrNull()
                                    if (currentClass != null) {
                                        LineShareHelper.shareHomework(
                                            context = context,
                                            className = currentClass.name,
                                            grade = currentClass.grade,
                                            lessonTopic = "English Homework Reminder",
                                            homeworkContent = currentClass.homeworkText ?: "Please review this week's vocabulary and complete the practice worksheet.",
                                            dueDate = currentClass.homeworkDue ?: "Tomorrow 17:00",
                                            joinCode = currentClass.joinCode
                                        )
                                        viewModel.triggerLineHeadsUpAlert(
                                            LineNotificationAlertState(
                                                senderName = "Class Companion OA (${currentClass.name})",
                                                title = "⏰ Homework Reminder Broadcast",
                                                messagePreview = "Reminder sent to all $pendingCount pending students in ${currentClass.name} via LINE!"
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text("🟢", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Broadcast Reminder", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // "Why Teachers Switch" section
        item {
            SectionHeader(title = "Why Teachers Switch · จุดเด่นเพื่อคุณครู")
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WhyFeatureRow(
                    icon = "📄",
                    title = "Curriculum-to-Test Generator",
                    desc = "Turn any lesson text, PDF, or vocab list into a ready classroom test with Thai translations."
                )
                WhyFeatureRow(
                    icon = "☀️",
                    title = "Instant Homework Dispatch",
                    desc = "Generate homework straight from what you just taught and push directly to student devices."
                )
                WhyFeatureRow(
                    icon = "💬",
                    title = "Simulated LINE Inbox",
                    desc = "Students receive assignments & automatic reminders straight into their simulated LINE inbox."
                )
                WhyFeatureRow(
                    icon = "🎙️",
                    title = "Oral Testing with Speech Recognition",
                    desc = "Test speaking & listening with native TTS reading questions and mic capturing student speech live."
                )
                WhyFeatureRow(
                    icon = "📊",
                    title = "Comprehensive Reports & Analytics",
                    desc = "Track who is thriving and who needs a nudge with weekly/monthly analytics and export."
                )
                WhyFeatureRow(
                    icon = "🎮",
                    title = "13 Rich Activity Modes",
                    desc = "Standard tests, gamified quizzes, flashcards, live worksheets, sentence builders, word searches & memory match."
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, BorderLine)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RoyalBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyPrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
        }
    }
}

@Composable
private fun WhyFeatureRow(
    icon: String,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, BorderLine)))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = icon, fontSize = 22.sp)
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
