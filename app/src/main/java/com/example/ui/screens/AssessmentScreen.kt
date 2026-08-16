package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentEntity
import com.example.data.model.StudentEntity
import com.example.data.model.StudentScoreEntity
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AssessmentScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val activeClass by viewModel.activeClass.collectAsState()
    val students by viewModel.studentsInActiveClass.collectAsState()
    val assessments by viewModel.assessmentsInActiveClass.collectAsState()
    val allScores by viewModel.allScores.collectAsState()

    var showNewAssessmentDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("assessment_header_card"),
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
                                text = "ASSESSMENT GRADEBOOK · บันทึกคะแนน",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = RoyalBlue
                            )
                            Text(
                                text = "${activeClass?.name ?: "M.1/3"} (${assessments.size} Tests Recorded)",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                        }

                        Button(
                            onClick = { showNewAssessmentDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Test", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Track oral speaking scores, standard quizzes, and midterm/final evaluations for all enrolled students.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }

        // Section header
        item {
            SectionHeader(title = "Recorded Assessments (${assessments.size})")
        }

        // Assessments List
        if (assessments.isEmpty()) {
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No assessments recorded yet. Tap 'New Test' or run a Speaking & Listening Test to record scores.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(assessments, key = { it.id }) { assessment ->
                val scoresForThis = allScores.filter { it.assessmentId == assessment.id }
                AssessmentItemCard(
                    assessment = assessment,
                    scores = scoresForThis
                )
            }
        }
    }

    if (showNewAssessmentDialog) {
        NewAssessmentDialog(
            students = students,
            onDismiss = { showNewAssessmentDialog = false },
            onSave = { title, maxScore, scoresMap ->
                viewModel.saveAssessment(title, maxScore, scoresMap)
                showNewAssessmentDialog = false
            }
        )
    }
}

@Composable
private fun AssessmentItemCard(
    assessment: AssessmentEntity,
    scores: List<StudentScoreEntity>
) {
    var isExpanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val dateStr = remember(assessment.timestamp) { dateFormat.format(Date(assessment.timestamp)) }

    val avgScore = if (scores.isNotEmpty()) scores.map { it.score }.average().toFloat() else 0f
    val avgPct = if (assessment.maxScore > 0) (avgScore / assessment.maxScore * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assessment.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = "Date: $dateStr · Max: ${assessment.maxScore.toInt()} pts",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                StatusBadge(
                    text = "Avg: ${String.format("%.1f", avgScore)} (${avgPct}%)",
                    color = if (avgPct >= 70) EmeraldGreen else RoyalBlue,
                    bgColor = if (avgPct >= 70) EmeraldGreenLight else RoyalBlueLight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expand breakdown toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide Student Scores" else "View ${scores.size} Student Scores",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = RoyalBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = RoyalBlue
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    scores.forEach { score ->
                        val pct = if (assessment.maxScore > 0) (score.score / assessment.maxScore * 100).toInt() else 0
                        Surface(
                            color = BackgroundLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = score.studentName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = TextInk)
                                )
                                Text(
                                    text = "${score.score.toInt()} / ${assessment.maxScore.toInt()} ($pct%)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (pct >= 80) EmeraldGreen else if (pct >= 60) RoyalBlue else CoralRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewAssessmentDialog(
    students: List<StudentEntity>,
    onDismiss: () -> Unit,
    onSave: (String, Float, Map<String, Float>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var maxScoreStr by remember { mutableStateOf("10") }
    var scoresMap by remember {
        mutableStateOf(students.associate { it.id to 8f }.toMutableMap())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Assessment · บันทึกการประเมิน") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Assessment Title (e.g. Unit 3 Speaking Test)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = maxScoreStr,
                        onValueChange = { maxScoreStr = it },
                        label = { Text("Max Score (e.g. 10 or 20)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Enter Student Scores:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                items(students) { student ->
                    val maxVal = maxScoreStr.toFloatOrNull() ?: 10f
                    val currentScore = scoresMap[student.id] ?: (maxVal * 0.8f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(student.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Slider(
                            value = currentScore,
                            onValueChange = {
                                val m = scoresMap.toMutableMap()
                                m[student.id] = it
                                scoresMap = m
                            },
                            valueRange = 0f..maxVal,
                            steps = (maxVal.toInt() - 1).coerceAtLeast(0),
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = "${currentScore.toInt()} pts",
                            modifier = Modifier.width(48.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val maxScore = maxScoreStr.toFloatOrNull() ?: 10f
                        onSave(title.trim(), maxScore, scoresMap)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Assessment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
