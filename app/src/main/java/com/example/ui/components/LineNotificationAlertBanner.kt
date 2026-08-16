package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class LineNotificationAlertState(
    val id: String = UUID_GEN(),
    val senderName: String = "Class Companion OA (ครูผู้สอน)",
    val studentTarget: String? = null,
    val title: String = "📚 New English Homework Assigned",
    val messagePreview: String = "Please review this week's vocabulary and complete the practice worksheet.",
    val fullContent: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private fun UUID_GEN() = java.util.UUID.randomUUID().toString()
    }
}

@Composable
fun LineNotificationAlertBanner(
    alert: LineNotificationAlertState?,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    onSpeak: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(alert?.id) {
        if (alert != null) {
            delay(5000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = alert != null,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = spring()) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .statusBarsPadding()
    ) {
        if (alert != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick() }
                    .testTag("line_heads_up_notification_banner"),
                color = Color(0xFF1E293B),
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Top App Identity & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // LINE Official Green Icon
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LineGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LINE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 7.sp)
                            }

                            Text(
                                text = "LINE · now",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                            )

                            if (alert.studentTarget != null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = RoyalBlue.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "To: ${alert.studentTarget}",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = Color(0xFF93C5FD),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onSpeak != null) {
                                IconButton(
                                    onClick = { onSpeak(alert.title + ". " + alert.messagePreview) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = ThaiGold, modifier = Modifier.size(16.dp))
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Sender & Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LineGreen.copy(alpha = 0.2f))
                                .border(1.dp, LineGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏫", fontSize = 18.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = alert.senderName,
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = alert.title,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontWeight = FontWeight.Medium),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Message Content Snippet
                    Text(
                        text = alert.messagePreview,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 16.sp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Quick Action Chips in Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LineGreen,
                            modifier = Modifier.clickable { onClick() }
                        ) {
                            Text(
                                text = "💬 Open LINE Chat View",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Tap alert to inspect Intent flow",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}
