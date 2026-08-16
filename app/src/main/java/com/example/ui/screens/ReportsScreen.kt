package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

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
    val context = LocalContext.current

    var selectedPeriod by remember { mutableStateOf("All Time") }

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
                Column(modifier = Modifier.padding(16.dp)) {
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
                                text = "Engagement & Performance",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                        }

                        Button(
                            onClick = {
                                val csv = buildString {
                                    appendLine("Class Companion Gradebook & Activity Report")
                                    appendLine("Class,Total Students,Submitted Homework,Recorded Assessments,Generated Activities")
                                    classes.forEach { cls ->
                                        val sCount = allStudents.count { it.classId == cls.id }
                                        val subCount = allStudents.count { it.classId == cls.id && it.isSubmitted }
                                        val aCount = allAssessments.count { it.classId == cls.id }
                                        val gCount = allLogs.count { it.classId == cls.id }
                                        appendLine("${cls.name},$sCount,$subCount,$aCount,$gCount")
                                    }
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Gradebook_Report", csv))
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export CSV", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
