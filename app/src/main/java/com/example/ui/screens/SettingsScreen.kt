package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.BuildConfig
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThaiFlagRibbon
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsState()
    val activeLang = settings?.language ?: "en"

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // App Identity Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ThaiGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "Class Companion",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "ผู้ช่วยห้องเรียนภาษาอังกฤษสำหรับครูไทย · v1.0",
                                style = MaterialTheme.typography.bodySmall.copy(color = ThaiGoldLight)
                            )
                        }
                    }

                    ThaiFlagRibbon(
                        modifier = Modifier.padding(vertical = 12.dp),
                        height = 3f
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (settings?.role == "school") "School: ${settings?.schoolName}" else "Teacher: ${settings?.teacherName}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Role: ${if (settings?.role == "school") "School Admin" else "Classroom Teacher"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSubtle)
                            )
                        }

                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ThaiGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Edit Profile", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Language Switcher Section
        item {
            SectionHeader(title = "App Language · ภาษาของแอป")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageOptionRow(
                        code = "en",
                        flag = "🇺🇸",
                        title = "English (US)",
                        subtitle = "Default interface language",
                        isSelected = activeLang == "en",
                        onSelect = { viewModel.setLanguage("en") }
                    )
                    LanguageOptionRow(
                        code = "th",
                        flag = "🇹🇭",
                        title = "ภาษาไทย (Thai)",
                        subtitle = "เมนูและคำอธิบายภาษาไทย",
                        isSelected = activeLang == "th",
                        onSelect = { viewModel.setLanguage("th") }
                    )
                    LanguageOptionRow(
                        code = "zh",
                        flag = "🇨🇳",
                        title = "中文 (Chinese)",
                        subtitle = "中文界面与提示",
                        isSelected = activeLang == "zh",
                        onSelect = { viewModel.setLanguage("zh") }
                    )
                }
            }
        }

        // AI Engine & Connectivity Status
        item {
            SectionHeader(title = "AI Engine & Speech Status")
        }

        item {
            val hasApiKey = !BuildConfig.GEMINI_API_KEY.isNullOrBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" && BuildConfig.GEMINI_API_KEY != "YOUR_API_KEY"

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Gemini 3.5 Flash Model", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary))
                            Text("Direct REST API generation", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                        }
                        StatusBadge(
                            text = if (hasApiKey) "Online & Connected" else "Smart Curriculum Fallback",
                            color = if (hasApiKey) EmeraldGreen else RoyalBlue,
                            bgColor = if (hasApiKey) EmeraldGreenLight else RoyalBlueLight
                        )
                    }

                    Divider(color = BorderLine)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Speech Recognition (STT)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary))
                            Text("Android Native RecognizerIntent", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                        }
                        StatusBadge(text = "Ready", color = EmeraldGreen, bgColor = EmeraldGreenLight)
                    }

                    Divider(color = BorderLine)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Text-to-Speech (TTS)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary))
                            Text("US English Pronunciation engine", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                        }
                        StatusBadge(text = "Active", color = EmeraldGreen, bgColor = EmeraldGreenLight)
                    }
                }
            }
        }

        // Data Management & Reset
        item {
            SectionHeader(title = "Data Management · จัดการข้อมูล")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Reset Database to Demo Sample",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = CoralRed)
                    )
                    Text(
                        text = "Clears all local room records and re-seeds standard Thai Mattayom classes, students, and vocabulary.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralRedLight, contentColor = CoralRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Sample Data")
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        var newRole by remember { mutableStateOf(settings?.role ?: "teacher") }
        var newName by remember {
            mutableStateOf(if (settings?.role == "school") settings?.schoolName ?: "" else settings?.teacherName ?: "")
        }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Role & Name") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = newRole == "teacher",
                            onClick = { newRole = "teacher" },
                            label = { Text("Teacher") }
                        )
                        FilterChip(
                            selected = newRole == "school",
                            onClick = { newRole = "school" },
                            label = { Text("School") }
                        )
                    }

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(if (newRole == "school") "School Name" else "Teacher Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.saveUserRole(newRole, newName.trim())
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Sample Data?") },
            text = { Text("This will restore default classes (M.1/3, M.4/1, M.5/2, M.6/1) and sample students.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun LanguageOptionRow(
    code: String,
    flag: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSelect() },
        color = if (isSelected) RoyalBlueLight else BackgroundLight,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(flag, fontSize = 24.sp)
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NavyPrimary else TextInk
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RoyalBlue)
            }
        }
    }
}
