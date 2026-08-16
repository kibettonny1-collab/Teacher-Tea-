package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.GoogleDriveBackupDialog
import com.example.ui.components.LineNotificationAlertState
import com.example.ui.components.LineSimulationScenario
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.util.DataExportHelper
import com.example.util.LineShareHelper

@Composable
fun ReportsScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val classes by viewModel.allClasses.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val allAssessments by viewModel.allAssessments.collectAsState()
    val allScores by viewModel.allScores.collectAsState()
    val activeClass by viewModel.activeClass.collectAsState()
    val context = LocalContext.current

    var selectedPeriod by remember { mutableStateOf("All Time") }
    var showDriveDialog by remember { mutableStateOf(false) }

    if (showDriveDialog) {
        GoogleDriveBackupDialog(viewModel = viewModel, onDismiss = { showDriveDialog = false })
    }

    val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
    val oneMonthAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)

    val filteredLogs = when (selectedPeriod) {
        "This Week" -> allLogs.filter { it.timestamp >= oneWeekAgo }
        "This Month" -> allLogs.filter { it.timestamp >= oneMonthAgo }
        else -> allLogs
    }

    // Activity distribution by type
    val activityTypeCounts = filteredLogs.groupBy { it.type }.mapValues { it.value.size }
    val maxTypeCount = (activityTypeCounts.values.maxOrNull() ?: 1).coerceAtLeast(1)

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
                    .testTag("reports_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, RoyalBlueLight)))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CLASSROOM ANALYTICS · รายงานและสถิติ",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = RoyalBlue
                            )
                            Text(
                                text = "Engagement & Gradebook",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                        }

                        IconButton(
                            onClick = { showDriveDialog = true },
                            modifier = Modifier.testTag("reports_cloud_drive_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Google Drive", tint = RoyalBlue)
                        }
                    }

                    // Export Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                val currentClass = activeClass ?: classes.firstOrNull()
                                val className = currentClass?.name ?: "All_Classes"
                                val classStudents = allStudents.filter { currentClass == null || it.classId == currentClass.id }
                                val classAssessments = allAssessments.filter { currentClass == null || it.classId == currentClass.id }
                                val classScores = allScores.filter { s -> classAssessments.any { it.id == s.assessmentId } }

                                val uri = DataExportHelper.exportGradebookCsv(
                                    context = context,
                                    className = className,
                                    assessments = classAssessments,
                                    scores = classScores,
                                    students = classStudents
                                )
                                if (uri != null) {
                                    Toast.makeText(context, "Exported Gradebook CSV to storage!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.openLineSimulator(LineSimulationScenario.GRADEBOOK)
                            },
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LineGreen),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(LineGreen, EmeraldGreen))),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("💬", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("LINE Sim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val currentClass = activeClass ?: classes.firstOrNull()
                                val className = currentClass?.name ?: "M.1/3"
                                val classScores = allScores.filter { s -> (activeClass == null || allAssessments.any { a -> a.id == s.assessmentId && a.classId == currentClass?.id }) }
                                val avg = if (classScores.isNotEmpty()) classScores.map { it.score }.average().toFloat() else 8.5f
                                val totalStudents = if (activeClass != null) allStudents.count { it.classId == currentClass?.id } else allStudents.size
                                val subCount = if (activeClass != null) allStudents.count { it.classId == currentClass?.id && it.isSubmitted } else allStudents.count { it.isSubmitted }

                                LineShareHelper.shareGradebookReport(
                                    context = context,
                                    className = className,
                                    assessmentTitle = "Weekly Gradebook & Unit Quiz",
                                    avgScore = avg,
                                    maxScore = 10f,
                                    totalStudents = totalStudents,
                                    submittedCount = subCount
                                )
                                viewModel.triggerLineHeadsUpAlert(
                                    LineNotificationAlertState(
                                        senderName = "Class Companion OA ($className)",
                                        title = "📊 Gradebook Summary Broadcast",
                                        messagePreview = "Assessment average: ${String.format("%.1f", avg)}/10.0 | Submitted: $subCount/$totalStudents"
                                    )
                                )
                            },
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("🟢", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showDriveDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalBlue),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Period Filter Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("This Week", "This Month", "All Time").forEach { period ->
                            val isSelected = selectedPeriod == period
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPeriod = period },
                                label = { Text(period) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Summary Metric Tiles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportMetricTile(
                    modifier = Modifier.weight(1f),
                    title = "Activities",
                    value = filteredLogs.size.toString(),
                    subtitle = "$selectedPeriod created"
                )
                ReportMetricTile(
                    modifier = Modifier.weight(1f),
                    title = "Assessments",
                    value = allAssessments.size.toString(),
                    subtitle = "Tests recorded"
                )
                ReportMetricTile(
                    modifier = Modifier.weight(1f),
                    title = "Scores",
                    value = allScores.size.toString(),
                    subtitle = "Student grades"
                )
            }
        }

        // Homework Completion Rates per Class
        item {
            SectionHeader(title = "Class Homework Completion Rates")
        }

        items(classes) { cls ->
            val clsStudents = allStudents.filter { it.classId == cls.id }
            val subCount = clsStudents.count { it.isSubmitted }
            val total = clsStudents.size
            val pct = if (total > 0) (subCount.toFloat() / total * 100).toInt() else 0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${cls.name} (${cls.grade})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                        )
                        StatusBadge(
                            text = "$subCount / $total Submitted ($pct%)",
                            color = if (pct >= 80) EmeraldGreen else if (pct >= 50) RoyalBlue else CoralRed,
                            bgColor = if (pct >= 80) EmeraldGreenLight else if (pct >= 50) RoyalBlueLight else CoralRedLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { if (total > 0) subCount.toFloat() / total else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (pct >= 80) EmeraldGreen else RoyalBlue,
                        trackColor = BorderLine
                    )
                }
            }
        }

        // Activity Type Distribution Bars
        item {
            SectionHeader(title = "Activity Generator Mix ($selectedPeriod)")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (activityTypeCounts.isEmpty()) {
                        Text("No activities recorded for this period yet.", color = TextMuted, fontSize = 13.sp)
                    } else {
                        activityTypeCounts.forEach { (type, count) ->
                            val ratio = count.toFloat() / maxTypeCount
                            val label = ACTIVITY_MODES.find { it.id == type }?.label ?: type

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary))
                                    Text("$count activities", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = RoyalBlue,
                                    trackColor = BorderLine
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
private fun ReportMetricTile(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelSmall.copy(color = RoyalBlue, fontWeight = FontWeight.Bold))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = NavyPrimary))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
        }
    }
}
