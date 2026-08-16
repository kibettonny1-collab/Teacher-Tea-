package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.worksheet.*
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThaiFlagRibbon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksheetsScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val activeClass by viewModel.activeClass.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(WorksheetCategory.ALL) }
    var selectedGradeFilter by remember { mutableStateOf("All Grades") }

    // Dialog States
    var playingWorksheet by remember { mutableStateOf<WorksheetItemModel?>(null) }
    var viewingPrintableSheet by remember { mutableStateOf<WorksheetItemModel?>(null) }
    var showAiCreatorDialog by remember { mutableStateOf(false) }

    val gradeFilters = listOf("All Grades", "P.1-P.3", "P.4-P.6", "M.1-M.3", "M.4-M.6")

    val displayedWorksheets = remember(searchQuery, selectedCategory, selectedGradeFilter) {
        WorksheetCatalog.searchWorksheets(
            query = searchQuery,
            category = selectedCategory,
            grade = if (selectedGradeFilter == "All Grades") null else selectedGradeFilter
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("worksheets_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ThaiGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📚", fontSize = 22.sp)
                                }
                                Column {
                                    Text(
                                        text = "WORKSHEETS & QUIZIZZ HUB",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = ThaiGoldLight
                                    )
                                    Text(
                                        text = "คลังใบงานและควิซ",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { showAiCreatorDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ThaiGold,
                                    contentColor = NavyPrimary
                                )
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Generator", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Explore 30+ interactive Quizizz speed challenges, Pinterest infographic worksheets, O-NET test prep, and printable homework sheets tailored for Thai ESL classrooms.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.88f), lineHeight = 18.sp)
                        )
                    }

                    ThaiFlagRibbon(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Search Bar & Grade Filter
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("worksheet_search_input"),
                        placeholder = { Text("Search by topic, grammar rule, or tag...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalBlue) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Grade Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gradeFilters.forEach { gr ->
                            val isSelected = selectedGradeFilter == gr
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedGradeFilter = gr },
                                label = { Text(gr, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBlueLight,
                                    selectedLabelColor = NavyPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Category Horizontal Carousel
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(WorksheetCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = cat },
                        color = if (isSelected) NavyPrimary else SurfaceCard,
                        border = if (isSelected) null else BorderStroke(1.dp, BorderLine),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(cat.icon, fontSize = 14.sp)
                            Column {
                                Text(
                                    text = cat.titleEn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else NavyPrimary
                                    )
                                )
                                Text(
                                    text = cat.titleTh,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isSelected) ThaiGoldLight else TextMuted
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title & Counter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "${selectedCategory.titleEn} (${displayedWorksheets.size})")
                Text(
                    text = "Active: ${activeClass?.name ?: "M.1/3"}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }
        }

        // Worksheets List
        if (displayedWorksheets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🔍", fontSize = 36.sp)
                        Text(
                            text = "No worksheets found matching your search.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                        )
                        Text(
                            text = "Try clearing filters or tap 'AI Generator' to build a custom worksheet on this topic.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, textAlign = TextAlign.Center)
                        )
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = WorksheetCategory.ALL
                                selectedGradeFilter = "All Grades"
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        } else {
            items(displayedWorksheets) { ws ->
                WorksheetCardItem(
                    worksheet = ws,
                    onPlayQuizizz = { playingWorksheet = ws },
                    onViewPrintable = { viewingPrintableSheet = ws },
                    onAssignHw = {
                        val hwContent = "${ws.title} (${ws.sourceStyle})\nInstructions: ${ws.instructionsEn}\n(${ws.instructionsTh})\nTotal Points: ${ws.totalPoints} pts"
                        viewModel.sendDirectHomework(hwContent)
                    },
                    onRemixAi = {
                        viewModel.lessonInputText.value = ws.title
                        viewModel.currentScreen.value = "companion"
                    }
                )
            }
        }
    }

    // -------------------------------------------------------------
    // DIALOG 1: INTERACTIVE QUIZIZZ GAME PLAYER
    // -------------------------------------------------------------
    playingWorksheet?.let { ws ->
        QuizizzGamePlayerDialog(
            worksheet = ws,
            viewModel = viewModel,
            onDismiss = { playingWorksheet = null },
            onAssignScore = { score ->
                // Automatically log score to active assessment
                viewModel.toastMessage.tryEmit("🎉 Score saved: $score / ${ws.totalPoints} pts")
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG 2: PINTEREST PRINTABLE / PAPER WORKSHEET VIEW
    // -------------------------------------------------------------
    viewingPrintableSheet?.let { ws ->
        PinterestPrintableSheetDialog(
            worksheet = ws,
            schoolName = viewModel.userSettings.value?.schoolName ?: "Banchang Wittayakhom School",
            teacherName = viewModel.userSettings.value?.teacherName ?: "Teacher Lok",
            className = activeClass?.name ?: "M.1/3",
            onDismiss = { viewingPrintableSheet = null },
            onAssignLine = {
                val hwText = "📝 [Pinterest Worksheet: ${ws.title}]\nGrade: ${ws.gradeLevel} | Points: ${ws.totalPoints}\n${ws.instructionsEn}\n${ws.instructionsTh}"
                viewModel.sendDirectHomework(hwText)
                viewingPrintableSheet = null
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG 3: AI CUSTOM WORKSHEET CREATOR
    // -------------------------------------------------------------
    if (showAiCreatorDialog) {
        AiWorksheetCreatorDialog(
            viewModel = viewModel,
            onDismiss = { showAiCreatorDialog = false },
            onCreatedAndOpenCompanion = { topic ->
                viewModel.lessonInputText.value = topic
                viewModel.currentScreen.value = "companion"
                showAiCreatorDialog = false
            }
        )
    }
}

// -----------------------------------------------------------------------------
// WORKSHEET CARD ITEM
// -----------------------------------------------------------------------------
@Composable
private fun WorksheetCardItem(
    worksheet: WorksheetItemModel,
    onPlayQuizizz: () -> Unit,
    onViewPrintable: () -> Unit,
    onAssignHw: () -> Unit,
    onRemixAi: () -> Unit
) {
    val isQuizizz = worksheet.sourceStyle.contains("Quizizz", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("worksheet_card_${worksheet.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(
            1.dp,
            if (isQuizizz) PurplePrimary.copy(alpha = 0.3f) else ThaiGold.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = if (isQuizizz) "⚡ Quizizz Mode" else "📌 Pinterest Sheet",
                        color = if (isQuizizz) PurplePrimary else ThaiGoldDark,
                        bgColor = if (isQuizizz) PurplePrimaryLight else ThaiGoldContainer
                    )
                    StatusBadge(
                        text = worksheet.gradeLevel,
                        color = NavyPrimary,
                        bgColor = RoyalBlueLight
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextMuted)
                    Text("${worksheet.estimatedMinutes}m", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("⭐ ${worksheet.totalPoints} pts", style = MaterialTheme.typography.labelSmall.copy(color = ThaiGoldDark, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Thai Subtitle
            Text(
                text = worksheet.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            )
            Text(
                text = worksheet.titleTh,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = worksheet.description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextInk, lineHeight = 18.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                worksheet.tags.forEach { tag ->
                    Surface(
                        color = BackgroundLight,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, BorderLine)
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderLine)
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play Quizizz Game
                Button(
                    onClick = onPlayQuizizz,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isQuizizz) PurplePrimary else RoyalBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("🎮 Play Quizizz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Printable / Paper Mode
                FilledTonalButton(
                    onClick = onViewPrintable,
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("📄 Printable", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Homework / More
                IconButton(
                    onClick = onAssignHw,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LineGreen.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Assign HW", tint = LineGreen, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onRemixAi,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ThaiGold.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Remix AI", tint = ThaiGoldDark, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. QUIZIZZ LIVE GAME PLAYER COMPONENT
// -----------------------------------------------------------------------------
@Composable
private fun QuizizzGamePlayerDialog(
    worksheet: WorksheetItemModel,
    viewModel: ClassCompanionViewModel,
    onDismiss: () -> Unit,
    onAssignScore: (Int) -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var textAnswerInput by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var highestStreak by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    val questions = worksheet.questions
    val currentQ = questions.getOrNull(currentIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyPrimary),
            color = NavyPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar: Quit, Points, Streak
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Quit", tint = Color.White)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Streak badge
                        if (streak > 1) {
                            Surface(
                                color = CoralRed,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "🔥 ${streak}X STREAK",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Points
                        Surface(
                            color = ThaiGold,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "⭐ $score PTS",
                                color = NavyPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ThaiGold,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isGameOver || currentQ == null) {
                    // GAME OVER SUMMARY PODIUM
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🏆", fontSize = 64.sp)
                        Text(
                            text = "QUIZIZZ COMPLETED!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ThaiGold
                            )
                        )
                        Text(
                            text = worksheet.title,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Final Score", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                                Text(
                                    text = "$score / ${questions.sumOf { it.points }}",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyPrimary
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Max Streak", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                                        Text("🔥 ${highestStreak}x", fontWeight = FontWeight.Bold, color = CoralRed)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Accuracy", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                                        val totalPossible = questions.sumOf { it.points }
                                        val pct = if (totalPossible > 0) (score.toFloat() / totalPossible * 100).toInt() else 100
                                        Text("🎯 $pct%", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    currentIndex = 0
                                    selectedOptionIndex = null
                                    textAnswerInput = ""
                                    isSubmitted = false
                                    score = 0
                                    streak = 0
                                    highestStreak = 0
                                    isGameOver = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Text("Play Again")
                            }

                            Button(
                                onClick = {
                                    onAssignScore(score)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyPrimary)
                            ) {
                                Text("Done / Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // ACTIVE QUESTION VIEW
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Question Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Question ${currentIndex + 1} of ${questions.size}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = RoyalBlue
                                            )
                                        )
                                        IconButton(
                                            onClick = { viewModel.speechManager.speak(currentQ.promptEn) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = RoyalBlue)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = currentQ.promptEn,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary,
                                            lineHeight = 28.sp
                                        )
                                    )

                                    if (currentQ.promptTh.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "(${currentQ.promptTh})",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Options / Input Modes
                            if (currentQ.options.isNotEmpty()) {
                                // Multiple Choice Option Cards (Quizizz Colorful style)
                                val optionColors = listOf(
                                    Color(0xFFE21B3C), // Red
                                    Color(0xFF1368CE), // Blue
                                    Color(0xFFD89E00), // Yellow/Gold
                                    Color(0xFF26890C)  // Green
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    currentQ.options.forEachIndexed { optIdx, opt ->
                                        val isSelected = selectedOptionIndex == optIdx
                                        val isCorrectOption = optIdx == currentQ.correctIndex
                                        val cardColor = if (isSubmitted) {
                                            if (isCorrectOption) EmeraldGreen else if (isSelected) CoralRed else Color.White.copy(alpha = 0.2f)
                                        } else {
                                            optionColors.getOrElse(optIdx % optionColors.size) { RoyalBlue }
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable(enabled = !isSubmitted) {
                                                    selectedOptionIndex = optIdx
                                                },
                                            color = cardColor,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = listOf("A", "B", "C", "D").getOrElse(optIdx) { "${optIdx + 1}" },
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                    )
                                                    Text(
                                                        text = opt,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }

                                                if (isSubmitted) {
                                                    if (isCorrectOption) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color.White)
                                                    } else if (isSelected) {
                                                        Icon(Icons.Default.Cancel, contentDescription = "Wrong", tint = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Fill in the blank mode
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Type your answer:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                                        OutlinedTextField(
                                            value = textAnswerInput,
                                            onValueChange = { textAnswerInput = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("Your answer here...") },
                                            enabled = !isSubmitted,
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        if (currentQ.hintTh.isNotBlank()) {
                                            Text("💡 Hint: ${currentQ.hintTh}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                                        }
                                    }
                                }
                            }

                            // Explanation Card if submitted
                            if (isSubmitted) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedOptionIndex == currentQ.correctIndex || textAnswerInput.trim().equals(currentQ.correctAnswer.trim(), ignoreCase = true)) {
                                            EmeraldGreenLight
                                        } else CoralRedLight
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = if (selectedOptionIndex == currentQ.correctIndex || textAnswerInput.trim().equals(currentQ.correctAnswer.trim(), ignoreCase = true)) {
                                                "🎉 Correct! (+${currentQ.points} pts)"
                                            } else "❌ Incorrect. Correct Answer: ${currentQ.correctAnswer}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (selectedOptionIndex == currentQ.correctIndex || textAnswerInput.trim().equals(currentQ.correctAnswer.trim(), ignoreCase = true)) EmeraldGreen else CoralRed
                                            )
                                        )
                                        if (currentQ.explanation.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = currentQ.explanation,
                                                style = MaterialTheme.typography.bodySmall.copy(color = TextInk)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Actions (Submit / Next)
                        Column(modifier = Modifier.padding(top = 20.dp)) {
                            if (!isSubmitted) {
                                Button(
                                    onClick = {
                                        val isCorrect = if (currentQ.options.isNotEmpty()) {
                                            selectedOptionIndex == currentQ.correctIndex
                                        } else {
                                            textAnswerInput.trim().equals(currentQ.correctAnswer.trim(), ignoreCase = true)
                                        }

                                        if (isCorrect) {
                                            score += currentQ.points
                                            streak += 1
                                            if (streak > highestStreak) highestStreak = streak
                                        } else {
                                            streak = 0
                                        }
                                        isSubmitted = true
                                    },
                                    enabled = if (currentQ.options.isNotEmpty()) selectedOptionIndex != null else textAnswerInput.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyPrimary)
                                ) {
                                    Text("Submit Answer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (currentIndex + 1 < questions.size) {
                                            currentIndex += 1
                                            selectedOptionIndex = null
                                            textAnswerInput = ""
                                            isSubmitted = false
                                        } else {
                                            isGameOver = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyPrimary)
                                ) {
                                    Text(
                                        if (currentIndex + 1 < questions.size) "Next Question ➡️" else "View Results 🏆",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. PINTEREST PRINTABLE / PAPER WORKSHEET VIEW
// -----------------------------------------------------------------------------
@Composable
private fun PinterestPrintableSheetDialog(
    worksheet: WorksheetItemModel,
    schoolName: String,
    teacherName: String,
    className: String,
    onDismiss: () -> Unit,
    onAssignLine: () -> Unit
) {
    val context = LocalContext.current
    var showAnswerKey by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight),
            color = BackgroundLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NavyPrimary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { showAnswerKey = !showAnswerKey },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (showAnswerKey) "Hide Answers" else "🔑 Answer Key", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val fullText = buildString {
                                    appendLine("==============================================")
                                    appendLine("$schoolName - English Department")
                                    appendLine("${worksheet.title} (${worksheet.gradeLevel})")
                                    appendLine("Name: ____________________ Class: $className Date: ________")
                                    appendLine("==============================================")
                                    appendLine("Instructions: ${worksheet.instructionsEn}")
                                    appendLine("(${worksheet.instructionsTh})")
                                    appendLine()
                                    if (worksheet.passageText != null) {
                                        appendLine("--- Reading Passage: ${worksheet.passageTitle ?: ""} ---")
                                        appendLine(worksheet.passageText)
                                        appendLine()
                                    }
                                    worksheet.questions.forEachIndexed { idx, q ->
                                        appendLine("${idx + 1}. ${q.promptEn} (${q.points} pts)")
                                        if (q.options.isNotEmpty()) {
                                            q.options.forEachIndexed { oIdx, o ->
                                                appendLine("   ${listOf("A", "B", "C", "D").getOrElse(oIdx) { "$oIdx" }}. $o")
                                            }
                                        }
                                        appendLine()
                                    }
                                    if (showAnswerKey) {
                                        appendLine("--- ANSWER KEY ---")
                                        worksheet.questions.forEachIndexed { idx, q ->
                                            appendLine("${idx + 1}. ${q.correctAnswer} - ${q.explanation}")
                                        }
                                    }
                                }
                                clipboard.setPrimaryClip(ClipData.newPlainText("worksheet_text", fullText))
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Sheet", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Printable A4 Sheet Paper Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, BorderLine),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // School & Worksheet Formal Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = schoolName.uppercase(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "ENGLISH LANGUAGE DEPARTMENT · WORKSHEET PACKET",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = worksheet.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalBlue
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = worksheet.titleTh,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Student Name Line Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = BackgroundLight,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderLine)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Name: ____________________", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                Text("Class: $className", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                Text("Score: _____ / ${worksheet.totalPoints}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = ThaiGoldDark))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Instructions
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = RoyalBlueLight.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📌 Instructions: ${worksheet.instructionsEn}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                Text(
                                    text = "คำชี้แจง: ${worksheet.instructionsTh}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                )
                            }
                        }

                        // Reading Passage Box if applicable
                        if (worksheet.passageText != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = BackgroundLight,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, BorderLine)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (worksheet.passageTitle != null) {
                                        Text(
                                            text = "📖 ${worksheet.passageTitle}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    Text(
                                        text = worksheet.passageText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextInk,
                                            lineHeight = 20.sp,
                                            fontFamily = FontFamily.Default
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Worksheet Questions
                        worksheet.questions.forEachIndexed { idx, q ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${idx + 1}. ${q.promptEn}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextInk)
                                    )
                                    Text(
                                        text = "(${q.points} pts)",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }

                                if (q.promptTh.isNotBlank()) {
                                    Text(
                                        text = "   (${q.promptTh})",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                if (q.options.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        q.options.forEachIndexed { oIdx, opt ->
                                            val letter = listOf("A", "B", "C", "D").getOrElse(oIdx) { "$oIdx" }
                                            Text(
                                                text = "($letter) $opt",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = if (showAnswerKey && oIdx == q.correctIndex) EmeraldGreen else TextInk,
                                                    fontWeight = if (showAnswerKey && oIdx == q.correctIndex) FontWeight.Bold else FontWeight.Normal
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "   Answer: ___________________________________",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                    )
                                }

                                if (showAnswerKey) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = EmeraldGreenLight,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(start = 12.dp)
                                    ) {
                                        Text(
                                            text = "🔑 Correct: ${q.correctAnswer} (${q.explanation})",
                                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = BorderLine.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Dispatch to LINE Action
                Button(
                    onClick = onAssignLine,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dispatch Sheet to Class LINE Inbox", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. AI CUSTOM WORKSHEET CREATOR DIALOG
// -----------------------------------------------------------------------------
@Composable
private fun AiWorksheetCreatorDialog(
    viewModel: ClassCompanionViewModel,
    onDismiss: () -> Unit,
    onCreatedAndOpenCompanion: (String) -> Unit
) {
    var topicInput by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("M.1") }
    var selectedStyle by remember { mutableStateOf("Quizizz Speed Quiz") }

    val presetTopics = listOf(
        "Solar System & Space Exploration",
        "At the Bangkok Airport & Flying",
        "Doctor Consultation & Health",
        "Shopping at Chatuchak Weekend Market",
        "Past Continuous vs Past Simple",
        "Songkran Splash & Cultural Values"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("✨", fontSize = 22.sp)
                Text("AI Worksheet & Quizizz Generator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Generate a tailor-made Pinterest-style worksheet or Quizizz speed quiz for any lesson topic.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )

                OutlinedTextField(
                    value = topicInput,
                    onValueChange = { topicInput = it },
                    label = { Text("Lesson Topic or Grammar Focus") },
                    placeholder = { Text("e.g. Irregular verbs, Food & drinks...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Quick Preset Ideas:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextMuted))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetTopics.forEach { top ->
                        Surface(
                            color = BackgroundLight,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderLine),
                            modifier = Modifier.clickable { topicInput = top }
                        ) {
                            Text(top, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTopic = if (topicInput.isNotBlank()) topicInput.trim() else "Daily English Expressions"
                    onCreatedAndOpenCompanion(finalTopic)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Generate in Companion")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
