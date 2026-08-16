package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.ThaiFlagRibbon
import com.example.ui.theme.*

@Composable
fun RoleSetupScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf<String?>("teacher") }
    var nameInput by remember { mutableStateOf("Ajarn Lok") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(NavySecondary, NavyDark),
                    radius = 1200f
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Graduation Cap Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(ThaiGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎓",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Class Companion",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyPrimary
                    )
                )

                Text(
                    text = "ผู้ช่วยห้องเรียนภาษาอังกฤษสำหรับครูไทย\nBuilt for teachers in Thailand",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                ThaiFlagRibbon(
                    modifier = Modifier.padding(vertical = 16.dp),
                    height = 4f
                )

                Text(
                    text = "Choose your role to start",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextInk
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // School Option
                    val isSchool = selectedRole == "school"
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_school_card")
                            .border(
                                width = if (isSchool) 2.dp else 1.dp,
                                color = if (isSchool) ThaiGold else BorderLine,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedRole = "school"
                                if (nameInput == "Ajarn Lok") nameInput = "Banchang Wittayakhom School"
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSchool) ThaiGoldContainer else SurfaceCard
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "School Role",
                                tint = if (isSchool) ThaiGold else NavyPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "I'm a School",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NavyPrimary
                            )
                            Text(
                                text = "โรงเรียน",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    // Teacher Option
                    val isTeacher = selectedRole == "teacher"
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_teacher_card")
                            .border(
                                width = if (isTeacher) 2.dp else 1.dp,
                                color = if (isTeacher) ThaiGold else BorderLine,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedRole = "teacher"
                                if (nameInput == "Banchang Wittayakhom School") nameInput = "Ajarn Lok"
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTeacher) ThaiGoldContainer else SurfaceCard
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Teacher Role",
                                tint = if (isTeacher) ThaiGold else NavyPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "I'm a Teacher",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NavyPrimary
                            )
                            Text(
                                text = "คุณครู / อาจารย์",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = selectedRole != null) {
                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        Text(
                            text = if (selectedRole == "school") "School name (ชื่อโรงเรียน)" else "Teacher name (ชื่ออาจารย์/คุณครู)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("role_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            placeholder = {
                                Text(if (selectedRole == "school") "e.g. Banchang Wittayakhom" else "e.g. Ajarn Lok")
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (nameInput.isNotBlank() && selectedRole != null) {
                                    viewModel.saveUserRole(selectedRole!!, nameInput.trim())
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("role_continue_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThaiGold,
                                contentColor = NavyPrimary
                            )
                        ) {
                            Text(
                                text = "Get Started · เริ่มใช้งาน",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
