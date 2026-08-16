package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import com.example.data.powerpoint.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PptTheme(val displayName: String, val bgBrush: Brush, val cardBg: Color, val textMain: Color, val textSub: Color, val accent: Color) {
    DARK_CINEMA(
        "Projector Dark 🎬",
        Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))),
        Color(0xFF1E293B),
        Color.White,
        Color(0xFF94A3B8),
        ThaiGold
    ),
    SMARTBOARD_LIGHT(
        "Smartboard Light 💡",
        Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))),
        Color.White,
        Color(0xFF0F172A),
        Color(0xFF475569),
        RoyalBlue
    ),
    BLACKBOARD_RETRO(
        "Retro Blackboard 🏫",
        Brush.verticalGradient(listOf(Color(0xFF1E392A), Color(0xFF0D2318))),
        Color(0xFF163022),
        Color(0xFFE8F5E9),
        Color(0xFFA5D6A7),
        Color(0xFFFFD54F)
    ),
    ARCADE_NEON(
        "Arcade Neon ⚡",
        Brush.verticalGradient(listOf(Color(0xFF2E0854), Color(0xFF140127))),
        Color(0xFF3B0764),
        Color(0xFFFDF4FF),
        Color(0xFFE879F9),
        Color(0xFFFACC15)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerPointPresenterDialog(
    deck: PowerPointDeckModel,
    onDismiss: () -> Unit,
    onSpeakText: (String) -> Unit,
    onAssignLine: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentSlideIndex by remember { mutableStateOf(0) }
    var currentTheme by remember { mutableStateOf(PptTheme.DARK_CINEMA) }
    var showTeacherNotes by remember { mutableStateOf(false) }
    var showThumbnails by remember { mutableStateOf(false) }

    // Classroom Game State
    var redTeamScore by remember { mutableStateOf(0) }
    var blueTeamScore by remember { mutableStateOf(0) }

    // Per slide revealed answers
    var revealedSlides by remember { mutableStateOf(setOf<Int>()) }

    // Mystery Box Opened Set per deck: SlideIndex to Set of box numbers
    var openedBoxes by remember { mutableStateOf(mapOf<Int, Set<Int>>()) }
    var selectedMysteryBox by remember { mutableStateOf<MysteryBoxItem?>(null) }

    // Would You Rather votes: SlideIndex to (VotesA to VotesB)
    var wyrVotes by remember { mutableStateOf(mapOf<Int, Pair<Int, Int>>()) }

    // Countdown Timer State
    var timerSeconds by remember { mutableStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Handle timer tick
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerSeconds > 0) {
            delay(1000)
            timerSeconds--
            if (timerSeconds == 0) {
                isTimerRunning = false
            }
        }
    }

    val currentSlide = deck.slides.getOrNull(currentSlideIndex) ?: deck.slides.first()
    val isRevealed = revealedSlides.contains(currentSlideIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("powerpoint_presenter_dialog"),
            containerColor = Color.Transparent,
            topBar = {
                // Projector Header
                Surface(
                    color = currentTheme.cardBg.copy(alpha = 0.95f),
                    modifier = Modifier.shadow(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Deck Info & Back
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.testTag("ppt_close_btn")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = currentTheme.textMain)
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = deck.badgeIcon,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = deck.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = currentTheme.textMain
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = "${deck.sourceAttribution} • Slide ${currentSlideIndex + 1} of ${deck.slides.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSub)
                                    )
                                }
                            }

                            // Team Scoreboard & Timer
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Team Red Score
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CoralRed.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, CoralRed)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("🔴 Red:", fontSize = 11.sp, color = currentTheme.textMain, fontWeight = FontWeight.Bold)
                                        Text("$redTeamScore", fontSize = 13.sp, color = CoralRed, fontWeight = FontWeight.ExtraBold)
                                        IconButton(
                                            onClick = { redTeamScore += 50 },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Text("+", fontSize = 13.sp, color = CoralRed, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Team Blue Score
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RoyalBlue.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, RoyalBlue)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("🔵 Blue:", fontSize = 11.sp, color = currentTheme.textMain, fontWeight = FontWeight.Bold)
                                        Text("$blueTeamScore", fontSize = 13.sp, color = RoyalBlue, fontWeight = FontWeight.ExtraBold)
                                        IconButton(
                                            onClick = { blueTeamScore += 50 },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Text("+", fontSize = 13.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Timer
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (timerSeconds <= 5 && isTimerRunning) CoralRed.copy(alpha = 0.3f) else currentTheme.cardBg,
                                    border = BorderStroke(1.dp, if (timerSeconds <= 5) CoralRed else currentTheme.accent)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clickable { isTimerRunning = !isTimerRunning }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            if (isTimerRunning) Icons.Default.Pause else Icons.Default.Timer,
                                            contentDescription = "Timer",
                                            tint = if (timerSeconds <= 5) CoralRed else currentTheme.accent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${timerSeconds}s",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (timerSeconds <= 5) CoralRed else currentTheme.textMain
                                        )
                                    }
                                }

                                // Quick Theme Dropdown Trigger
                                IconButton(
                                    onClick = {
                                        val nextIndex = (currentTheme.ordinal + 1) % PptTheme.values().size
                                        currentTheme = PptTheme.values()[nextIndex]
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Palette, contentDescription = "Theme", tint = currentTheme.accent, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom Presenter Controls
                Surface(
                    color = currentTheme.cardBg.copy(alpha = 0.95f),
                    modifier = Modifier.shadow(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Notes & Audio
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showTeacherNotes = !showTeacherNotes },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (showTeacherNotes) currentTheme.accent.copy(alpha = 0.2f) else Color.Transparent,
                                    contentColor = currentTheme.textMain
                                ),
                                border = BorderStroke(1.dp, currentTheme.accent)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp), tint = currentTheme.accent)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Teacher Script (ครู)", fontSize = 12.sp)
                            }

                            IconButton(
                                onClick = {
                                    val textToRead = "${currentSlide.title}. ${currentSlide.bodyEn}. ${currentSlide.explanationEn ?: ""}"
                                    onSpeakText(textToRead)
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(currentTheme.accent.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "TTS", tint = currentTheme.accent)
                            }

                            IconButton(
                                onClick = { showThumbnails = !showThumbnails },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(currentTheme.accent.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.ViewCarousel, contentDescription = "Slides", tint = currentTheme.accent)
                            }
                        }

                        // Right: Previous / Next Slide Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (currentSlideIndex > 0) {
                                        currentSlideIndex--
                                        timerSeconds = 30
                                        isTimerRunning = false
                                        selectedMysteryBox = null
                                    }
                                },
                                enabled = currentSlideIndex > 0,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentTheme.cardBg,
                                    contentColor = currentTheme.textMain,
                                    disabledContainerColor = currentTheme.cardBg.copy(alpha = 0.4f)
                                ),
                                border = BorderStroke(1.dp, currentTheme.accent.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prev Slide", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (currentSlideIndex < deck.slides.size - 1) {
                                        currentSlideIndex++
                                        timerSeconds = 30
                                        isTimerRunning = false
                                        selectedMysteryBox = null
                                    }
                                },
                                enabled = currentSlideIndex < deck.slides.size - 1,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentTheme.accent,
                                    contentColor = NavyPrimary,
                                    disabledContainerColor = currentTheme.accent.copy(alpha = 0.4f)
                                )
                            ) {
                                Text("Next Slide", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(currentTheme.bgBrush)
            ) {
                // Main Slide Presentation Canvas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Slide Container
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = currentTheme.cardBg),
                        border = BorderStroke(2.dp, currentTheme.accent.copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            when (currentSlide.layoutType) {
                                SlideLayoutType.TITLE_HERO -> TitleHeroSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    onStart = {
                                        if (deck.slides.size > 1) {
                                            currentSlideIndex = 1
                                        }
                                    },
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.GRAMMAR_RULE -> GrammarRuleSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.MYSTERY_BOX -> MysteryBoxSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    openedBoxNumbers = openedBoxes[currentSlideIndex] ?: emptySet(),
                                    selectedBox = selectedMysteryBox,
                                    onBoxClick = { box ->
                                        selectedMysteryBox = box
                                        val currentOpened = openedBoxes[currentSlideIndex] ?: emptySet()
                                        openedBoxes = openedBoxes + (currentSlideIndex to (currentOpened + box.boxNumber))
                                    },
                                    onAwardRed = { pts ->
                                        redTeamScore += pts
                                        selectedMysteryBox = null
                                    },
                                    onAwardBlue = { pts ->
                                        blueTeamScore += pts
                                        selectedMysteryBox = null
                                    },
                                    onCloseModal = { selectedMysteryBox = null },
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.JEOPARDY_MCQ -> JeopardyMcqSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    isRevealed = isRevealed,
                                    onToggleReveal = {
                                        revealedSlides = if (isRevealed) revealedSlides - currentSlideIndex else revealedSlides + currentSlideIndex
                                    },
                                    onAwardRed = { redTeamScore += currentSlide.pointsValue },
                                    onAwardBlue = { blueTeamScore += currentSlide.pointsValue },
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.SPOT_MISTAKE -> SpotMistakeSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    isRevealed = isRevealed,
                                    onToggleReveal = {
                                        revealedSlides = if (isRevealed) revealedSlides - currentSlideIndex else revealedSlides + currentSlideIndex
                                    },
                                    onAwardRed = { redTeamScore += currentSlide.pointsValue },
                                    onAwardBlue = { blueTeamScore += currentSlide.pointsValue },
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.WOULD_YOU_RATHER -> WouldYouRatherSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    votes = wyrVotes[currentSlideIndex] ?: (0 to 0),
                                    onVoteA = {
                                        val cur = wyrVotes[currentSlideIndex] ?: (0 to 0)
                                        wyrVotes = wyrVotes + (currentSlideIndex to (cur.first + 1 to cur.second))
                                    },
                                    onVoteB = {
                                        val cur = wyrVotes[currentSlideIndex] ?: (0 to 0)
                                        wyrVotes = wyrVotes + (currentSlideIndex to (cur.first to cur.second + 1))
                                    },
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.DIALOGUE_ROLEPLAY -> DialogueRoleplaySlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.TABOO_GUESS -> TabooGuessSlideView(
                                    slide = currentSlide,
                                    theme = currentTheme,
                                    timerSeconds = timerSeconds,
                                    isTimerRunning = isTimerRunning,
                                    onStartTimer = { isTimerRunning = true },
                                    onResetTimer = { timerSeconds = 45; isTimerRunning = false },
                                    onAwardRed = { redTeamScore += currentSlide.pointsValue },
                                    onAwardBlue = { blueTeamScore += currentSlide.pointsValue },
                                    onSpeak = onSpeakText
                                )
                                SlideLayoutType.SUMMARY_HOMEWORK -> SummaryHomeworkSlideView(
                                    slide = currentSlide,
                                    deck = deck,
                                    theme = currentTheme,
                                    onAssignLine = onAssignLine,
                                    onSpeak = onSpeakText
                                )
                            }
                        }
                    }

                    // Slide Thumbnails Drawer (if open)
                    AnimatedVisibility(
                        visible = showThumbnails,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = currentTheme.cardBg),
                            border = BorderStroke(1.dp, currentTheme.accent.copy(alpha = 0.5f))
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(deck.slides) { s ->
                                    val isCur = s.slideNumber - 1 == currentSlideIndex
                                    Surface(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                currentSlideIndex = s.slideNumber - 1
                                                showThumbnails = false
                                                selectedMysteryBox = null
                                            },
                                        color = if (isCur) currentTheme.accent else currentTheme.cardBg,
                                        border = BorderStroke(1.dp, if (isCur) ThaiGold else currentTheme.textSub.copy(alpha = 0.3f))
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Slide ${s.slideNumber}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCur) NavyPrimary else currentTheme.textMain
                                                )
                                                Text(s.visualEmoji, fontSize = 11.sp)
                                            }
                                            Text(
                                                text = s.title,
                                                fontSize = 9.sp,
                                                color = if (isCur) NavyPrimary else currentTheme.textSub,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Teacher Script Drawer (if open)
                    AnimatedVisibility(
                        visible = showTeacherNotes,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                            border = BorderStroke(1.dp, ThaiGold)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("👨‍🏫", fontSize = 16.sp)
                                        Text(
                                            text = "TEACHER PRESENTER SCRIPT & ELICITATION GUIDE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ThaiGold
                                        )
                                    }
                                    IconButton(
                                        onClick = { showTeacherNotes = false },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                    }
                                }

                                if (currentSlide.teacherNotesEn.isNotBlank()) {
                                    Text(
                                        text = "🇬🇧 ${currentSlide.teacherNotesEn}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Medium)
                                    )
                                }
                                if (currentSlide.teacherNotesTh.isNotBlank()) {
                                    Text(
                                        text = "🇹🇭 ${currentSlide.teacherNotesTh}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = ThaiGoldLight)
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

// -------------------------------------------------------------------------------------------------
// 1. TITLE HERO SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun TitleHeroSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    onStart: () -> Unit,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(theme.accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(slide.visualEmoji, fontSize = 42.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = slide.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = theme.accent,
                textAlign = TextAlign.Center
            )
        )

        slide.subtitle?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textMain,
                    textAlign = TextAlign.Center
                )
            )
        }

        slide.headline?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = theme.accent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, theme.accent)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge.copy(color = theme.accent, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = slide.bodyEn,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = theme.textMain,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if (slide.bodyTh.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = slide.bodyTh,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = theme.textSub,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStart,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.accent, contentColor = NavyPrimary),
            modifier = Modifier.shadow(6.dp, RoundedCornerShape(14.dp))
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("START PRESENTATION / เกมเริ่ม!", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 2. GRAMMAR RULE SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun GrammarRuleSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = slide.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.accent
                    )
                )
                slide.subtitle?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
                }
            }
            IconButton(onClick = { onSpeak("${slide.title}. ${slide.bodyEn}") }) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "TTS", tint = theme.accent)
            }
        }

        // Rule Formula Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBg),
            border = BorderStroke(1.dp, theme.accent)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = slide.bodyEn,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = theme.textMain,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                )
                if (slide.bodyTh.isNotBlank()) {
                    Text(
                        text = slide.bodyTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub, lineHeight = 20.sp)
                    )
                }
            }
        }

        // Bullet Points
        if (slide.bulletPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slide.bulletPoints.forEach { pt ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = theme.accent.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, theme.accent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("👉", fontSize = 16.sp)
                            Text(
                                text = pt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = theme.textMain,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3. MYSTERY BOX SLIDE VIEW (INTERACTIVE 6 BOXES)
// -------------------------------------------------------------------------------------------------
@Composable
private fun MysteryBoxSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    openedBoxNumbers: Set<Int>,
    selectedBox: MysteryBoxItem?,
    onBoxClick: (MysteryBoxItem) -> Unit,
    onAwardRed: (Int) -> Unit,
    onAwardBlue: (Int) -> Unit,
    onCloseModal: () -> Unit,
    onSpeak: (String) -> Unit
) {
    val boxes = slide.mysteryBoxes ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = theme.accent
                        )
                    )
                    slide.subtitle?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
                    }
                }
                Text("🎁 ${openedBoxNumbers.size}/${boxes.size} Opened", fontSize = 12.sp, color = theme.textSub)
            }

            // 6 Grid Boxes
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(boxes) { box ->
                    val isOpened = openedBoxNumbers.contains(box.boxNumber)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onBoxClick(box) }
                            .testTag("mystery_box_${box.boxNumber}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isOpened) theme.cardBg.copy(alpha = 0.5f) else theme.accent.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (isOpened) Color.Gray.copy(alpha = 0.5f) else theme.accent
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isOpened) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎁", fontSize = 32.sp)
                                    Text(
                                        text = "BOX ${box.boxNumber}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = theme.textMain
                                        )
                                    )
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when (box.rewardType) {
                                            "bomb" -> "💣"
                                            "star" -> "💎"
                                            "double" -> "⚡"
                                            else -> "✅"
                                        },
                                        fontSize = 28.sp
                                    )
                                    Text(
                                        text = "${if (box.points >= 0) "+" else ""}${box.points} PTS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (box.points >= 0) LineGreen else CoralRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mystery Box Modal Popup
        selectedBox?.let { box ->
            var showAnswer by remember { mutableStateOf(false) }

            Dialog(onDismissRequest = onCloseModal) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    border = BorderStroke(2.dp, ThaiGold),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = when (box.rewardType) {
                                "bomb" -> "💣 BOMB DETONATED!"
                                "star" -> "💎 SUPER STAR CHEST!"
                                "double" -> "⚡ DOUBLE POWER-UP!"
                                else -> "🎁 MYSTERY BOX #${box.boxNumber}"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (box.rewardType == "bomb") CoralRed else ThaiGold
                            )
                        )

                        Text(
                            text = box.promptEn,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )

                        if (box.promptTh.isNotBlank()) {
                            Text(
                                text = box.promptTh,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ThaiGoldLight,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        // Reveal Answer Button
                        if (!showAnswer) {
                            Button(
                                onClick = {
                                    showAnswer = true
                                    onSpeak(box.answer)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reveal Answer (เปิดเฉลย)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = LineGreenLight,
                                border = BorderStroke(1.dp, LineGreen)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "ANSWER: ${box.answer}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0F5132)
                                        )
                                    )
                                }
                            }
                        }

                        // Team Award Buttons
                        Text("Award Points to Team:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onAwardRed(box.points) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                            ) {
                                Text("🔴 Team Red (${if (box.points >= 0) "+" else ""}${box.points})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onAwardBlue(box.points) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                            ) {
                                Text("🔵 Team Blue (${if (box.points >= 0) "+" else ""}${box.points})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 4. JEOPARDY MCQ SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun JeopardyMcqSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit,
    onAwardRed: () -> Unit,
    onAwardBlue: () -> Unit,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = slide.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.accent
                    )
                )
                slide.subtitle?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = theme.accent.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, theme.accent)
            ) {
                Text(
                    text = "+${slide.pointsValue} PTS",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.accent
                )
            }
        }

        // Question Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBg),
            border = BorderStroke(1.dp, theme.accent.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = slide.bodyEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = theme.textMain,
                        lineHeight = 24.sp
                    )
                )
                if (slide.bodyTh.isNotBlank()) {
                    Text(
                        text = slide.bodyTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub)
                    )
                }
            }
        }

        // 4 Options Grid
        if (slide.options.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slide.options.forEach { opt ->
                    val isCorrectOption = isRevealed && slide.correctAnswer != null && opt.startsWith(slide.correctAnswer.take(2))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCorrectOption) LineGreenLight else theme.accent.copy(alpha = 0.08f),
                        border = BorderStroke(1.5.dp, if (isCorrectOption) LineGreen else theme.accent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isCorrectOption) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isCorrectOption) Color(0xFF0F5132) else theme.textMain
                                )
                            )
                            if (isCorrectOption) {
                                Text("✅ CORRECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LineGreen)
                            }
                        }
                    }
                }
            }
        }

        // Reveal Button & Explanation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleReveal,
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent, contentColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isRevealed) "Hide Answer" else "Reveal Answer (เปิดเฉลย)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Award points directly
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onAwardRed,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("🔴 +${slide.pointsValue}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAwardBlue,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("🔵 +${slide.pointsValue}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isRevealed && slide.explanationEn != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = LineGreenLight,
                border = BorderStroke(1.dp, LineGreen)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "💡 ${slide.explanationEn}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F5132))
                    )
                    slide.explanationTh?.let {
                        Text(
                            text = "🇹🇭 $it",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0F5132).copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 5. SPOT THE MISTAKE SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun SpotMistakeSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit,
    onAwardRed: () -> Unit,
    onAwardBlue: () -> Unit,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = slide.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.accent
                    )
                )
                slide.subtitle?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CoralRed.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, CoralRed)
            ) {
                Text(
                    text = "🔍 +${slide.pointsValue} PTS",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoralRed
                )
            }
        }

        // Target Sentence with crime tape banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isRevealed) CoralRedLight else theme.cardBg),
            border = BorderStroke(2.dp, if (isRevealed) CoralRed else theme.accent)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = slide.bodyEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isRevealed) CoralRed else theme.textMain,
                        lineHeight = 24.sp
                    )
                )
                if (slide.bodyTh.isNotBlank()) {
                    Text(
                        text = slide.bodyTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub)
                    )
                }
            }
        }

        // Reveal Button & Points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleReveal,
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent, contentColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isRevealed) "Hide Solution" else "Spot the Error (เฉลยจุดผิด)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onAwardRed,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("🔴 +${slide.pointsValue}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAwardBlue,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("🔵 +${slide.pointsValue}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isRevealed && slide.correctAnswer != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LineGreenLight,
                border = BorderStroke(1.5.dp, LineGreen)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "🎯 ${slide.correctAnswer}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F5132)
                        )
                    )
                    slide.explanationEn?.let {
                        Text(
                            text = "💡 $it",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0F5132), fontWeight = FontWeight.Medium)
                        )
                    }
                    slide.explanationTh?.let {
                        Text(
                            text = "🇹🇭 $it",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0F5132).copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 6. WOULD YOU RATHER SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun WouldYouRatherSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    votes: Pair<Int, Int>,
    onVoteA: () -> Unit,
    onVoteB: () -> Unit,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = slide.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.accent
                )
            )
            slide.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
            }
        }

        // Option A vs Option B Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Option A
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onVoteA() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CoralRed.copy(alpha = 0.15f)),
                border = BorderStroke(2.dp, CoralRed)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("OPTION A 🍕", fontWeight = FontWeight.ExtraBold, color = CoralRed, fontSize = 14.sp)
                    Text(
                        text = slide.optionA ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = theme.textMain,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CoralRed
                    ) {
                        Text(
                            text = "🗳️ ${votes.first} Votes",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Option B
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onVoteB() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue.copy(alpha = 0.15f)),
                border = BorderStroke(2.dp, RoyalBlue)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("OPTION B 🌶️", fontWeight = FontWeight.ExtraBold, color = RoyalBlue, fontSize = 14.sp)
                    Text(
                        text = slide.optionB ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = theme.textMain,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = RoyalBlue
                    ) {
                        Text(
                            text = "🗳️ ${votes.second} Votes",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Speaking Sentence Starter Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = theme.accent.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, theme.accent)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("🗣️ Speaking Sentence Frame:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.accent)
                Text(
                    text = slide.bodyEn,
                    style = MaterialTheme.typography.bodyMedium.copy(color = theme.textMain, fontWeight = FontWeight.Bold)
                )
                if (slide.bodyTh.isNotBlank()) {
                    Text(
                        text = slide.bodyTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 7. DIALOGUE ROLEPLAY SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun DialogueRoleplaySlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column {
            Text(
                text = slide.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.accent
                )
            )
            slide.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
            }
        }

        slide.dialogueLines.forEach { (speaker, text) ->
            val isVendor = speaker.contains("Vendor") || speaker.contains("A")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isVendor) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isVendor) RoyalBlue.copy(alpha = 0.2f) else ThaiGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (isVendor) RoyalBlue else ThaiGold)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = speaker,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVendor) RoyalBlue else ThaiGold
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = theme.textMain,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        IconButton(
                            onClick = { onSpeak(text) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "TTS", tint = theme.accent, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 8. TABOO GUESS SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun TabooGuessSlideView(
    slide: PowerPointSlide,
    theme: PptTheme,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    onStartTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAwardRed: () -> Unit,
    onAwardBlue: () -> Unit,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = slide.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = theme.accent
            )
        )
        slide.subtitle?.let {
            Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
        }

        // Big Target Secret Word Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = theme.accent.copy(alpha = 0.2f)),
            border = BorderStroke(2.dp, theme.accent)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = slide.headline ?: "TARGET WORD",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.accent
                    )
                )
            }
        }

        // 3 Taboo Forbidden Words
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CoralRed.copy(alpha = 0.15f)),
            border = BorderStroke(1.5.dp, CoralRed)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🚫 FORBIDDEN TABOO WORDS (ห้ามพูดคำเหล่านี้!):", fontWeight = FontWeight.ExtraBold, color = CoralRed, fontSize = 12.sp)
                slide.tabooForbiddenWords.forEach { taboo ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = CoralRed.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "❌ $taboo",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = CoralRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Clue Helper
        Text(
            text = slide.bodyEn,
            style = MaterialTheme.typography.bodySmall.copy(
                color = theme.textSub,
                textAlign = TextAlign.Center
            )
        )

        // Award Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onAwardRed,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
            ) {
                Text("🔴 Team Red Guessed! (+${slide.pointsValue})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onAwardBlue,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("🔵 Team Blue Guessed! (+${slide.pointsValue})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 9. SUMMARY & HOMEWORK SLIDE VIEW
// -------------------------------------------------------------------------------------------------
@Composable
private fun SummaryHomeworkSlideView(
    slide: PowerPointSlide,
    deck: PowerPointDeckModel,
    theme: PptTheme,
    onAssignLine: (String) -> Unit,
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = slide.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = theme.accent
            )
        )
        slide.subtitle?.let {
            Text(it, style = MaterialTheme.typography.bodySmall.copy(color = theme.textSub))
        }

        Text(
            text = slide.bodyEn,
            style = MaterialTheme.typography.bodyMedium.copy(color = theme.textMain, fontWeight = FontWeight.Bold)
        )

        if (slide.bulletPoints.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = theme.accent.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, theme.accent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📌 Key Takeaways (สรุปเนื้อหาสำคัญ):", fontWeight = FontWeight.Bold, color = theme.accent, fontSize = 12.sp)
                    slide.bulletPoints.forEach { pt ->
                        Text("• $pt", style = MaterialTheme.typography.bodySmall.copy(color = theme.textMain))
                    }
                }
            }
        }

        // Action Buttons: Send to LINE & Copy Notes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val hwText = "📽️ [iSLCollective PPT Lesson Summary: ${deck.title}]\nGrade: ${deck.gradeLevel}\n${slide.bodyEn}\nKey Points:\n" + slide.bulletPoints.joinToString("\n") { "• $it" }
                    onAssignLine(hwText)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ppt_assign_line_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LineGreen, contentColor = Color.White)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Assign to LINE Inbox", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("PPT Summary", "${deck.title}\n" + slide.bulletPoints.joinToString("\n"))
                    clipboard.setPrimaryClip(clip)
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, theme.accent)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = theme.accent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Notes", color = theme.textMain, fontSize = 12.sp)
            }
        }
    }
}
