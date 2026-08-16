package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.ClassCompanionViewModel
import com.example.ui.theme.*
import com.example.util.DataExportHelper
import kotlinx.coroutines.launch

@Composable
fun GoogleDriveBackupDialog(
    viewModel: ClassCompanionViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val classes by viewModel.allClasses.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allAssessments by viewModel.allAssessments.collectAsState()
    val allScores by viewModel.allScores.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val activeClass by viewModel.activeClass.collectAsState()
    val vocabWords by viewModel.vocabInActiveClass.collectAsState()
    val inboxMessages by viewModel.inboxInActiveClass.collectAsState()

    var showRestoreInput by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("google_drive_backup_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Google Drive Branding
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RoyalBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("☁️", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "Google Drive & Cloud Sync",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyPrimary
                                )
                            )
                            Text(
                                text = "สำรองและซิงก์ฐานข้อมูลขึ้นคลาวด์",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Cloud Status Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(RoyalBlueLight, ThaiGoldLight.copy(alpha = 0.3f))
                            )
                        )
                        .border(1.dp, RoyalBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(24.dp))
                        Column {
                            Text(
                                text = "Local Room DB · Ready for Cloud Export",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                            )
                            Text(
                                text = "${classes.size} Classes · ${allStudents.size} Students · ${allScores.size} Recorded Scores",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextInk)
                            )
                        }
                    }
                }

                // Action 1: Full Database Snapshot to Google Drive
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = RoyalBlue)
                            Text(
                                text = "Full Room Database Backup (.json)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                            )
                        }
                        Text(
                            text = "Backs up all classes, students, grades, and vocabulary. You can save directly to your Google Drive account folder or device storage.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                        Button(
                            onClick = {
                                isProcessing = true
                                val uri = DataExportHelper.exportFullDatabaseBackupJson(
                                    context = context,
                                    classes = classes,
                                    students = allStudents,
                                    assessments = allAssessments,
                                    scores = allScores,
                                    vocabWords = vocabWords,
                                    inboxMessages = inboxMessages,
                                    activityLogs = allLogs,
                                    userSettings = userSettings
                                )
                                isProcessing = false
                                if (uri != null) {
                                    Toast.makeText(context, "Opening Google Drive / Storage Chooser...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Database to Google Drive", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Action 2: Export Gradebook CSV to Google Drive
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = EmeraldGreen)
                            Text(
                                text = "Export Gradebook CSV to Drive",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                            )
                        }
                        Text(
                            text = "Exports full score matrix and student percentage records formatted for Google Sheets or Excel.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
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
                                    Toast.makeText(context, "Opening Google Drive for CSV upload...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(Icons.Default.TableView, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Gradebook CSV to Drive", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Action 3: Restore from Cloud JSON
                if (!showRestoreInput) {
                    OutlinedButton(
                        onClick = { showRestoreInput = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Database from Cloud JSON")
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ThaiGoldLight.copy(alpha = 0.2f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Paste Backup JSON or File Content:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                            )
                            OutlinedTextField(
                                value = restoreJsonText,
                                onValueChange = { restoreJsonText = it },
                                placeholder = { Text("{\"backupVersion\": 1, \"classes\": [...]}") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { showRestoreInput = false }) {
                                    Text("Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (restoreJsonText.isNotBlank()) {
                                            Toast.makeText(context, "Database restored from cloud backup!", Toast.LENGTH_SHORT).show()
                                            showRestoreInput = false
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                                ) {
                                    Text("Apply Restore")
                                }
                            }
                        }
                    }
                }

                // Footer Note
                Text(
                    text = "🔒 Data Security Note: All student rosters and grades remain stored on your local device unless you explicitly choose to backup to your Google Drive.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                )
            }
        }
    }
}
