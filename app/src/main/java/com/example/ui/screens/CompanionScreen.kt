package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ai.*
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class ActivityTypeItem(
    val id: String,
    val icon: String,
    val label: String,
    val thaiLabel: String
)

val ACTIVITY_MODES = listOf(
    ActivityTypeItem("standard", "📝", "Standard Test", "แบบทดสอบมาตรฐาน"),
    ActivityTypeItem("gamified", "🎮", "Gamified Quiz", "ควิซสะสมแต้ม"),
    ActivityTypeItem("flashcards", "🗂️", "Flash Cards", "บัตรคำศัพท์"),
    ActivityTypeItem("sltest", "🎤", "Oral Test (STT/TTS)", "สอบพูดและฟัง"),
    ActivityTypeItem("sentence", "🧩", "Sentence Builder", "สร้างประโยค"),
    ActivityTypeItem("worksheet", "🖊️", "Live Worksheet", "ใบงานเติมคำ"),
    ActivityTypeItem("story", "📖", "Story & Reading", "นิทานและเรื่องสั้น"),
    ActivityTypeItem("wordsearch", "🔍", "Word Search", "ตารางค้นหาคำ"),
    ActivityTypeItem("memory", "🧠", "Memory Match", "เกมจับคู่ความจำ"),
    ActivityTypeItem("matchup", "🔗", "Match-Up Pairs", "จับคู่คำศัพท์และความหมาย"),
    ActivityTypeItem("quizrace", "⚡", "Speed Quiz Race", "วิ่งแข่งตอบคำถามจับเวลา"),
    ActivityTypeItem("anagram", "🔠", "Anagram Scramble", "เรียงตัวอักษรเป็นคำศัพท์"),
    ActivityTypeItem("truefalse", "⚖️", "True / False Speed", "จริงหรือเท็จความเร็วสูง"),
    ActivityTypeItem("openthebox", "🎁", "Open The Box", "เปิดกล่องปริศนาท้าทาย"),
    ActivityTypeItem("wordbank", "📁", "Word Bank", "คลังคำศัพท์ประจำบท"),
    ActivityTypeItem("audio", "🎧", "Audio Listening", "บทสนทนาการฟัง"),
    ActivityTypeItem("video", "📹", "Video Script", "บทวิดีโอสถานการณ์"),
    ActivityTypeItem("conversation", "🗣️", "Oral AI Partner", "แชทบอทฝึกพูด")
)

val PRESET_TOPICS = listOf(
    "Irregular Verbs (go/went, eat/ate, see/saw)",
    "Daily Routine & Morning Activities",
    "Ordering Food & Street Food in Thailand",
    "Songkran & Traditional Thai Festivals",
    "Travel, Directions & Asking for Help",
    "School Life, Subjects & Classroom Rules",
    "Hobbies, Sports & Free Time Activities",
    "Weather, Seasons & Climate in Southeast Asia"
)

@Composable
fun CompanionScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val classes by viewModel.allClasses.collectAsState()
    val activeClass by viewModel.activeClass.collectAsState()
    val lessonInput by viewModel.lessonInputText.collectAsState()
    val selectedType by viewModel.selectedActivityType.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showAddClassDialog by remember { mutableStateOf(false) }
    var dueDateInput by remember { mutableStateOf("2026-08-25") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Step 1: Class & Content Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("companion_step1_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, RoyalBlueLight)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(
                        title = "1. Class & Lesson Content",
                        action = {
                            TextButton(onClick = { showAddClassDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Class", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    )

                    // Class selector chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(classes) { cls ->
                            val isSelected = cls.id == activeClass?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectClass(cls.id) },
                                label = { Text("${cls.name} (${cls.grade})", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Topic or Lesson Material:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextMuted)
                    )

                    // Quick topic chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        items(PRESET_TOPICS) { topic ->
                            SuggestionChip(
                                onClick = { viewModel.lessonInputText.value = topic },
                                label = { Text(topic, fontSize = 11.5.sp) },
                                shape = RoundedCornerShape(8.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = RoyalBlueLight)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = lessonInput,
                        onValueChange = { viewModel.lessonInputText.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp)
                            .testTag("companion_content_input"),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Paste vocabulary words, grammar topic, reading passage, or notes...") },
                        maxLines = 5
                    )
                }
            }
        }

        // Step 2: Activity Mode Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("companion_step2_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, BorderLine)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "2. Choose Activity Format (${ACTIVITY_MODES.size} Modes)")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (chunk in ACTIVITY_MODES.chunked(2)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (item in chunk) {
                                    val isSelected = selectedType == item.id
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("act_btn_${item.id}")
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) RoyalBlue else BorderLine,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.selectedActivityType.value = item.id },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) RoyalBlueLight else SurfaceCard
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(item.icon, fontSize = 20.sp)
                                            Column {
                                                Text(
                                                    text = item.label,
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) NavyPrimary else TextInk
                                                    )
                                                )
                                                Text(
                                                    text = item.thaiLabel,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = TextMuted,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                if (chunk.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateActivity() },
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("companion_generate_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThaiGold,
                            contentColor = NavyPrimary
                        )
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = NavyPrimary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generating with AI...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✨ Generate Activity (${activeClass?.grade ?: "M.1"})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Step 3: Interactive Output Result Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("companion_output_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, ThaiGoldLight)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader(title = "3. Activity Preview & Practice")
                        StatusBadge(
                            text = ACTIVITY_MODES.find { it.id == selectedType }?.label ?: selectedType,
                            color = RoyalBlue,
                            bgColor = RoyalBlueLight
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Render specific output view according to selectedType
                    when (selectedType) {
                        "standard" -> StandardTestView(viewModel)
                        "gamified" -> GamifiedQuizView(viewModel)
                        "flashcards" -> FlashCardsView(viewModel)
                        "sltest" -> SpeakingListeningTestView(viewModel)
                        "sentence" -> SentenceBuilderView(viewModel)
                        "worksheet" -> LiveWorksheetView(viewModel)
                        "story" -> StoryView(viewModel)
                        "wordsearch" -> WordSearchView(viewModel)
                        "memory" -> MemoryMatchView(viewModel)
                        "matchup" -> MatchUpPairsView(viewModel)
                        "quizrace" -> QuizRaceView(viewModel)
                        "anagram" -> AnagramScrambleView(viewModel)
                        "truefalse" -> TrueFalseSpeedView(viewModel)
                        "openthebox" -> OpenTheBoxView(viewModel)
                        "wordbank" -> WordBankView(viewModel)
                        "audio", "video" -> ListeningScriptView(viewModel, isVideo = selectedType == "video")
                        "conversation" -> ConversationRoleplayView(viewModel)
                        else -> StandardTestView(viewModel)
                    }
                }
            }
        }

        // Step 4: Send as Homework Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("companion_homework_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = ThaiGold)
                        Text(
                            text = "Send as Homework · มอบหมายการบ้าน",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "Push this generated activity directly into connected students' LINE inboxes with an assigned due date.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSubtle),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = dueDateInput,
                            onValueChange = { dueDateInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hw_due_date_input"),
                            label = { Text("Due Date", color = ThaiGoldLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ThaiGold,
                                unfocusedBorderColor = TextSubtle
                            )
                        )

                        Button(
                            onClick = { viewModel.sendHomework(dueDateInput) },
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("send_hw_to_class_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThaiGold,
                                contentColor = NavyPrimary
                            )
                        ) {
                            Icon(Icons.Default.MarkEmailRead, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send to Class", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddClassDialog) {
        CreateClassDialog(
            onDismiss = { showAddClassDialog = false },
            onCreate = { name, grade ->
                viewModel.createClass(name, grade)
                showAddClassDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// Activity 1: Standard Test View
// -------------------------------------------------------------
@Composable
private fun StandardTestView(viewModel: ClassCompanionViewModel) {
    val result by viewModel.standardTestResult.collectAsState()
    val context = LocalContext.current
    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    if (result == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tap '✨ Generate Activity' above to build a standard multiple-choice test for this class.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted, textAlign = TextAlign.Center)
            )
        }
        return
    }

    val res = result!!

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = res.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
        )

        res.items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, BorderLine)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${item.id}. ${item.questionEn}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = TextInk)
                    )
                    Text(
                        text = "(${item.questionTh})",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    item.options.forEachIndexed { optIdx, opt ->
                        val isPicked = selectedAnswers[item.id] == optIdx
                        val isCorrect = optIdx == item.correctIndex
                        val showValidation = selectedAnswers.containsKey(item.id)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val map = selectedAnswers.toMutableMap()
                                    map[item.id] = optIdx
                                    selectedAnswers = map
                                },
                            color = when {
                                showValidation && isCorrect -> EmeraldGreenLight
                                showValidation && isPicked && !isCorrect -> CoralRedLight
                                isPicked -> RoyalBlueLight
                                else -> SurfaceCard
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isPicked,
                                    onClick = {
                                        val map = selectedAnswers.toMutableMap()
                                        map[item.id] = optIdx
                                        selectedAnswers = map
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (showValidation && isCorrect) EmeraldGreen else TextInk,
                                        fontWeight = if (isPicked || (showValidation && isCorrect)) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }

                    if (selectedAnswers.containsKey(item.id) && item.explanation.isNotBlank()) {
                        Text(
                            text = "💡 ${item.explanation}",
                            style = MaterialTheme.typography.bodySmall.copy(color = RoyalBlue),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val fullText = res.title + "\n\n" + res.items.joinToString("\n\n") {
                        "${it.id}. ${it.questionEn} (${it.questionTh})\n" + it.options.mapIndexed { idx, o -> "   ${('A' + idx)}) $o" }.joinToString("\n")
                    } + "\n\nAnswer Key:\n" + res.answerKeyText

                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Test", fullText))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Test Text", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 2: Gamified Quiz View
// -------------------------------------------------------------
@Composable
private fun GamifiedQuizView(viewModel: ClassCompanionViewModel) {
    val quizText by viewModel.gamifiedQuizText.collectAsState()

    if (quizText.isNullOrBlank()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to create a gamified point-based quest.", color = TextMuted)
        }
        return
    }

    Surface(
        color = NavyDark,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🎮", fontSize = 20.sp)
                Text("GAMIFIED QUEST", style = MaterialTheme.typography.titleMedium.copy(color = ThaiGold, fontWeight = FontWeight.ExtraBold))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = quizText ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

// -------------------------------------------------------------
// Activity 3: Flashcards View
// -------------------------------------------------------------
@Composable
private fun FlashCardsView(viewModel: ClassCompanionViewModel) {
    val cards by viewModel.flashCardsResult.collectAsState()
    var flippedStates by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }

    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to generate flippable bilingual flashcards.", color = TextMuted)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${cards.size} Flashcards (Auto-saved to Vocab Bank)",
                style = MaterialTheme.typography.labelMedium.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = {
                    val allEn = cards.joinToString(". ") { it.en }
                    viewModel.speechManager.speak(allEn)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlueLight, contentColor = RoyalBlue),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Read All", fontSize = 12.sp)
            }
        }

        // Grid of flashcards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (chunk in cards.chunked(2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (card in chunk) {
                        val isShowingThai = flippedStates[card.id] == true
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    val map = flippedStates.toMutableMap()
                                    map[card.id] = !isShowingThai
                                    flippedStates = map
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isShowingThai) ThaiGold else NavyPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isShowingThai) card.th else card.en,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isShowingThai) NavyDark else Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isShowingThai) "ภาษาไทย (Tap to EN)" else "English (Tap for TH)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isShowingThai) NavyDark.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.speechManager.speak(card.en) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = if (isShowingThai) NavyDark else ThaiGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (chunk.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 4: Speaking & Listening Test View (STT & TTS)
// -------------------------------------------------------------
@Composable
private fun SpeakingListeningTestView(viewModel: ClassCompanionViewModel) {
    val questions by viewModel.speakingListeningQuestions.collectAsState()
    val currentIndex by viewModel.slQuestionIndex.collectAsState()
    val answers by viewModel.slStudentAnswers.collectAsState()
    val isListening by viewModel.speechManager.isListening.collectAsState()
    val spokenText by viewModel.speechManager.spokenText.collectAsState()
    val speechError by viewModel.speechManager.speechError.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.speechManager.startListening {}
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to start an oral speaking and listening test.", color = TextMuted)
        }
        return
    }

    val isFinished = currentIndex >= questions.size || answers.size == questions.size

    if (isFinished) {
        val correctCount = answers.count { it.value.second }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldGreenLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉", fontSize = 36.sp)
                Text(
                    text = "Speaking Exam Completed!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                )
                Text(
                    text = "$correctCount / ${questions.size} Questions Marked Correct",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = NavyPrimary),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveSpeakingTestScoreToAssessment(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save to Assessment Gradebook")
                }
            }
        }
        return
    }

    val q = questions[currentIndex]

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                style = MaterialTheme.typography.labelMedium.copy(color = TextMuted, fontWeight = FontWeight.Bold)
            )
            StatusBadge(
                text = if (q.type == "wh") "WH-Question" else "Yes / No Question",
                color = RoyalBlue,
                bgColor = RoyalBlueLight
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = q.questionEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = q.questionTh,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted, textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.speechManager.speak(q.questionEn) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play Question (TTS)")
                    }

                    FloatingActionButton(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                if (isListening) viewModel.speechManager.stopListening()
                                else viewModel.speechManager.startListening {}
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        containerColor = if (isListening) CoralRed else ThaiGold,
                        contentColor = NavyDark,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Mic"
                        )
                    }
                }

                // Speech recognition transcript box
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isListening) "🎙️ Listening to student... Speak now" else "Student Speech Transcript:",
                            style = MaterialTheme.typography.labelSmall.copy(color = if (isListening) CoralRed else TextMuted, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (spokenText.isNotBlank()) spokenText else "(Awaiting student voice answer...)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (spokenText.isNotBlank()) FontWeight.Bold else FontWeight.Normal,
                                color = if (spokenText.isNotBlank()) NavyPrimary else TextMuted
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if (speechError != null) {
                            Text(
                                text = "⚠️ $speechError",
                                style = MaterialTheme.typography.bodySmall.copy(color = CoralRed),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scoring buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.recordSpeakingTestAnswer(q.id, spokenText, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Incorrect")
                    }

                    Button(
                        onClick = { viewModel.recordSpeakingTestAnswer(q.id, spokenText, true) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Correct")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 5: Sentence Builder View
// -------------------------------------------------------------
@Composable
private fun SentenceBuilderView(viewModel: ClassCompanionViewModel) {
    val items by viewModel.sentenceBuilderItems.collectAsState()
    var selectedWordsMap by remember { mutableStateOf(mutableMapOf<Int, List<String>>()) }
    var resultsMap by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to create interactive sentence scramble puzzles.", color = TextMuted)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { item ->
            val userBuilt = selectedWordsMap[item.id] ?: emptyList()
            val availableWords = item.words.filter { w ->
                val countInOriginal = item.words.count { it == w }
                val countInBuilt = userBuilt.count { it == w }
                countInBuilt < countInOriginal
            }
            val isChecked = resultsMap.containsKey(item.id)
            val isCorrect = resultsMap[item.id] == true

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${item.id}. (${item.sentenceTh})",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted, fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Drop Tray
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    if (isChecked) if (isCorrect) EmeraldGreen else CoralRed else RoyalBlue,
                                    BorderLine
                                )
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (userBuilt.isEmpty()) {
                                Text("Tap words below to build sentence...", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                            } else {
                                userBuilt.forEachIndexed { wordIdx, word ->
                                    Surface(
                                        color = RoyalBlue,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            val list = userBuilt.toMutableList()
                                            list.removeAt(wordIdx)
                                            val map = selectedWordsMap.toMutableMap()
                                            map[item.id] = list
                                            selectedWordsMap = map
                                            resultsMap = resultsMap.toMutableMap().apply { remove(item.id) }
                                        }
                                    ) {
                                        Text(
                                            text = word,
                                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Word Bank Tiles
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.words.forEach { word ->
                            val isUsed = userBuilt.contains(word) && userBuilt.count { it == word } >= item.words.count { it == word }
                            Surface(
                                color = if (isUsed) BorderLine else SurfaceCard,
                                shape = RoundedCornerShape(6.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.clickable(enabled = !isUsed) {
                                    val list = userBuilt + word
                                    val map = selectedWordsMap.toMutableMap()
                                    map[item.id] = list
                                    selectedWordsMap = map
                                }
                            ) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isUsed) TextMuted else NavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    val builtSentence = userBuilt.joinToString(" ").trim()
                                    val correctSentence = item.sentenceEn.replace(".", "").trim()
                                    val ok = builtSentence.equals(correctSentence, ignoreCase = true)
                                    val rMap = resultsMap.toMutableMap()
                                    rMap[item.id] = ok
                                    resultsMap = rMap
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                            ) {
                                Text("Check", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val map = selectedWordsMap.toMutableMap()
                                    map[item.id] = emptyList()
                                    selectedWordsMap = map
                                    resultsMap = resultsMap.toMutableMap().apply { remove(item.id) }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reset", fontSize = 12.sp)
                            }
                        }

                        if (isChecked) {
                            Text(
                                text = if (isCorrect) "✅ Correct!" else "❌ Try again",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = if (isCorrect) EmeraldGreen else CoralRed,
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

// -------------------------------------------------------------
// Activity 6: Live Worksheet View
// -------------------------------------------------------------
@Composable
private fun LiveWorksheetView(viewModel: ClassCompanionViewModel) {
    val items by viewModel.worksheetItems.collectAsState()
    var userInputs by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var checkedStates by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to create a fill-in-the-blank live worksheet.", color = TextMuted)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items.forEach { item ->
            val input = userInputs[item.id] ?: ""
            val isChecked = checkedStates.containsKey(item.id)
            val isCorrect = checkedStates[item.id] == true

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${item.id}. ${item.before}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextInk)
                        )
                        OutlinedTextField(
                            value = input,
                            onValueChange = {
                                val map = userInputs.toMutableMap()
                                map[item.id] = it
                                userInputs = map
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(48.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isChecked) (if (isCorrect) EmeraldGreen else CoralRed) else RoyalBlue,
                                unfocusedBorderColor = if (isChecked) (if (isCorrect) EmeraldGreen else CoralRed) else BorderLine
                            )
                        )
                        Text(
                            text = item.after,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextInk)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Hint: (${item.hintTh})",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )

                        if (isChecked) {
                            Text(
                                text = if (isCorrect) "✅ Correct" else "❌ Answer: ${item.answer}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isCorrect) EmeraldGreen else CoralRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val map = mutableMapOf<Int, Boolean>()
                items.forEach { item ->
                    val userVal = userInputs[item.id]?.trim() ?: ""
                    map[item.id] = userVal.equals(item.answer.trim(), ignoreCase = true)
                }
                checkedStates = map
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Check All Answers")
        }
    }
}

// -------------------------------------------------------------
// Activity 7: Story View
// -------------------------------------------------------------
@Composable
private fun StoryView(viewModel: ClassCompanionViewModel) {
    val story by viewModel.storyResult.collectAsState()

    if (story == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to generate a graded reading story with comprehension questions.", color = TextMuted)
        }
        return
    }

    val res = story!!

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = res.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
            )
            IconButton(onClick = { viewModel.speechManager.speak(res.storyText) }) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Read story", tint = RoyalBlue)
            }
        }

        Surface(
            color = BackgroundLight,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = res.storyText,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, color = TextInk),
                modifier = Modifier.padding(16.dp)
            )
        }

        Text(
            text = "Comprehension Questions (คำถามความเข้าใจ):",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RoyalBlue)
        )

        res.questions.forEachIndexed { idx, q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "${idx + 1}. ${q.first}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )
                    Text(
                        text = "(${q.second})",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 8: Word Search View
// -------------------------------------------------------------
@Composable
private fun WordSearchView(viewModel: ClassCompanionViewModel) {
    val searchResult by viewModel.wordSearchResult.collectAsState()

    if (searchResult == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to construct a vocabulary word search matrix.", color = TextMuted)
        }
        return
    }

    val res = searchResult!!

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Vocabulary Grid (${res.size}x${res.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
        )

        // Matrix
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            res.grid.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { char ->
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(NavySecondary, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ThaiGold
                                )
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Find these words:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextMuted)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(res.words) { word ->
                SuggestionChip(
                    onClick = { viewModel.speechManager.speak(word) },
                    label = { Text(word, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ThaiGoldContainer)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 9: Memory Match View
// -------------------------------------------------------------
@Composable
private fun MemoryMatchView(viewModel: ClassCompanionViewModel) {
    val cards by viewModel.memoryCards.collectAsState()
    val flippedIds by viewModel.memoryFlippedIds.collectAsState()
    val matchedPairs by viewModel.memoryMatchedPairIds.collectAsState()

    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to start an English-Thai memory matching puzzle.", color = TextMuted)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Matched: ${matchedPairs.size} / ${cards.size / 2} Pairs",
                style = MaterialTheme.typography.labelLarge.copy(color = RoyalBlue, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Tap 2 cards to find match",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (chunk in cards.chunked(3)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (card in chunk) {
                        val isFlipped = flippedIds.contains(card.id) || matchedPairs.contains(card.pairId)
                        val isMatched = matchedPairs.contains(card.pairId)

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(76.dp)
                                .clickable { viewModel.onMemoryCardTapped(card) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMatched -> EmeraldGreenLight
                                    isFlipped -> ThaiGold
                                    else -> NavyPrimary
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isFlipped) card.text else "❓",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMatched) EmeraldGreen else if (isFlipped) NavyDark else Color.White,
                                        textAlign = TextAlign.Center
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

// -------------------------------------------------------------
// Activity 10: Word Bank View
// -------------------------------------------------------------
@Composable
private fun WordBankView(viewModel: ClassCompanionViewModel) {
    val entries by viewModel.wordBankEntries.collectAsState()

    if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to compile a categorized word bank reference sheet.", color = TextMuted)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        entries.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.en,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                            )
                            Text(
                                text = "—  ${item.th}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = TextInk)
                            )
                        }
                        Text(
                            text = "\"${item.example}\"",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.speechManager.speak(item.en) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = RoyalBlue)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 11 & 12: Listening Script View
// -------------------------------------------------------------
@Composable
private fun ListeningScriptView(viewModel: ClassCompanionViewModel, isVideo: Boolean) {
    val result by viewModel.listeningResult.collectAsState()

    if (result == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to generate listening scripts with audio playback.", color = TextMuted)
        }
        return
    }

    val res = result!!

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = res.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
            )
            Button(
                onClick = { viewModel.speechManager.speak(res.script) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = Color.White)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Play Script (TTS)")
            }
        }

        Surface(
            color = BackgroundLight,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = res.script,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, color = TextInk),
                modifier = Modifier.padding(14.dp)
            )
        }

        Text("Comprehension Questions:", style = MaterialTheme.typography.labelLarge.copy(color = RoyalBlue, fontWeight = FontWeight.Bold))

        res.questions.forEach { q ->
            Text("• $q", style = MaterialTheme.typography.bodyMedium.copy(color = TextInk))
        }
    }
}

// -------------------------------------------------------------
// Activity 13: Oral AI Partner Roleplay View
// -------------------------------------------------------------
@Composable
private fun ConversationRoleplayView(viewModel: ClassCompanionViewModel) {
    val chat by viewModel.roleplayChat.collectAsState()
    val isListening by viewModel.speechManager.isListening.collectAsState()
    val spokenText by viewModel.speechManager.spokenText.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.speechManager.startListening { text ->
                inputMessage = text
            }
        }
    }

    LaunchedEffect(spokenText) {
        if (spokenText.isNotBlank()) {
            inputMessage = spokenText
        }
    }

    if (chat.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to start a real-time conversational speaking roleplay.", color = TextMuted)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        chat.forEach { msg ->
            val isAi = msg.sender == "ai"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
            ) {
                Surface(
                    color = if (isAi) BackgroundLight else NavyPrimary,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.widthIn(max = 290.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isAi) "🧑‍🏫 Teacher AI" else "Student",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isAi) RoyalBlue else ThaiGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = msg.textEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isAi) TextInk else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (!msg.textTh.isNullOrBlank()) {
                            Text(
                                text = msg.textTh,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isAi) TextMuted else Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        if (!msg.grammarTip.isNullOrBlank()) {
                            Text(
                                text = "💡 ${msg.grammarTip}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ThaiGold),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (isAi) {
                            IconButton(
                                onClick = { viewModel.speechManager.speak(msg.textEn) },
                                modifier = Modifier.size(24.dp).padding(top = 4.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type or speak in English...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            IconButton(
                onClick = {
                    val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    if (hasPerm) {
                        if (isListening) viewModel.speechManager.stopListening()
                        else viewModel.speechManager.startListening { inputMessage = it }
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isListening) CoralRed else ThaiGold)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Mic", tint = NavyDark)
            }

            Button(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.submitRoleplayReply(inputMessage.trim())
                        inputMessage = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 10: Match-up Pairs View (Wordwall-style)
// -------------------------------------------------------------
@Composable
private fun MatchUpPairsView(viewModel: ClassCompanionViewModel) {
    val result by viewModel.matchUpResult.collectAsState()

    if (result == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to create interactive Match-Up pairs.", color = TextMuted)
        }
        return
    }

    val res = result!!
    var selectedTermId by remember(res) { mutableStateOf<Int?>(null) }
    var matchedIds by remember(res) { mutableStateOf<Set<Int>>(emptySet()) }
    var wrongMatchAttempt by remember(res) { mutableStateOf<Int?>(null) }
    val shuffledDefs = remember(res) { res.pairs.map { it.id to it.definition }.shuffled() }

    val allMatched = matchedIds.size == res.pairs.size

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = res.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark)
                )
                Text(
                    text = "Matched: ${matchedIds.size} / ${res.pairs.size} Pairs",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (allMatched) EmeraldGreen else RoyalBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = {
                    matchedIds = emptySet()
                    selectedTermId = null
                    wrongMatchAttempt = null
                }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = RoyalBlue)
            }
        }

        if (allMatched) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreenLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🏆", fontSize = 32.sp)
                    Column {
                        Text(
                            text = "Excellent Work! ครบทุกคู่แล้ว!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        )
                        Text(
                            text = "You successfully matched all ${res.pairs.size} English-Thai vocabulary pairs.",
                            style = MaterialTheme.typography.bodySmall.copy(color = NavyDark)
                        )
                    }
                }
            }
        }

        Text(
            text = "👉 Step 1: Tap a Term on the left. Step 2: Tap matching Definition on the right.",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )

        // 2-Column Matching Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Column: Terms
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Terms (คำศัพท์)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue)
                )
                res.pairs.forEach { pair ->
                    val isMatched = matchedIds.contains(pair.id)
                    val isSelected = selectedTermId == pair.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isMatched) {
                                selectedTermId = pair.id
                                wrongMatchAttempt = null
                                viewModel.speechManager.speak(pair.term)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isMatched -> EmeraldGreenLight
                                isSelected -> RoyalBlue
                                else -> SurfaceCard
                            }
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    if (isSelected) RoyalBlue else BorderLine,
                                    if (isSelected) RoyalBlueLight else BorderLine
                                )
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = pair.term,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (isMatched) EmeraldGreen else NavyDark
                                )
                            )
                            if (isMatched) {
                                Text("✓", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = if (isSelected) Color.White else RoyalBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Right Column: Definitions
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Definitions (ความหมาย)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ThaiGoldDark)
                )
                shuffledDefs.forEach { (pairId, definition) ->
                    val isMatched = matchedIds.contains(pairId)
                    val isWrong = wrongMatchAttempt == pairId

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isMatched && selectedTermId != null) {
                                if (selectedTermId == pairId) {
                                    matchedIds = matchedIds + pairId
                                    selectedTermId = null
                                    wrongMatchAttempt = null
                                } else {
                                    wrongMatchAttempt = pairId
                                }
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isMatched -> EmeraldGreenLight
                                isWrong -> CrimsonRedLight
                                selectedTermId != null -> ThaiGoldContainer
                                else -> SurfaceCard
                            }
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    if (isWrong) CoralRed else BorderLine,
                                    if (isMatched) EmeraldGreen else BorderLine
                                )
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = definition,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isMatched) EmeraldGreen else NavyDark,
                                    fontWeight = if (isMatched) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 11: Speed Quiz Race View
// -------------------------------------------------------------
@Composable
private fun QuizRaceView(viewModel: ClassCompanionViewModel) {
    val result by viewModel.quizRaceResult.collectAsState()

    if (result == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to start a Speed Quiz Race.", color = TextMuted)
        }
        return
    }

    val res = result!!
    var currentIndex by remember(res) { mutableStateOf(0) }
    var score by remember(res) { mutableStateOf(0) }
    var streak by remember(res) { mutableStateOf(0) }
    var selectedOption by remember(res, currentIndex) { mutableStateOf<Int?>(null) }
    var isCompleted by remember(res) { mutableStateOf(false) }

    if (isCompleted) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("🏁", fontSize = 48.sp)
                Text(
                    text = "Quiz Race Completed!",
                    style = MaterialTheme.typography.titleLarge.copy(color = ThaiGold, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Final Score: $score XP · Max Streak: ${streak}x 🔥",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )

                Button(
                    onClick = {
                        currentIndex = 0
                        score = 0
                        streak = 0
                        selectedOption = null
                        isCompleted = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Race Again", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val currentItem = res.items.getOrNull(currentIndex) ?: return
    val isAnswered = selectedOption != null

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentIndex + 1} of ${res.items.size}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = "🏆 $score XP", color = ThaiGoldDark, bgColor = ThaiGoldContainer)
                if (streak > 1) {
                    StatusBadge(text = "🔥 ${streak}x Combo", color = CoralRed, bgColor = CrimsonRedLight)
                }
            }
        }

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyPrimary)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ SPEED QUESTION (${currentItem.points} PTS)",
                        style = MaterialTheme.typography.labelSmall.copy(color = ThaiGold, fontWeight = FontWeight.Bold)
                    )
                    IconButton(
                        onClick = { viewModel.speechManager.speak(currentItem.question) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Read", tint = Color.White)
                    }
                }

                Text(
                    text = currentItem.question,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )

                if (currentItem.questionTh.isNotBlank()) {
                    Text(
                        text = currentItem.questionTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateLight)
                    )
                }
            }
        }

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentItem.options.forEachIndexed { optIndex, optionText ->
                val isCorrectOpt = optIndex == currentItem.correctIndex
                val isSelectedOpt = optIndex == selectedOption

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAnswered) {
                            selectedOption = optIndex
                            if (isCorrectOpt) {
                                val gainedPoints = currentItem.points * (streak + 1)
                                score += gainedPoints
                                streak += 1
                                viewModel.speechManager.speak("Correct! $optionText")
                            } else {
                                streak = 0
                                viewModel.speechManager.speak("Incorrect")
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isAnswered && isCorrectOpt -> EmeraldGreenLight
                            isAnswered && isSelectedOpt -> CrimsonRedLight
                            else -> SurfaceCard
                        }
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(
                                if (isAnswered && isCorrectOpt) EmeraldGreen else if (isAnswered && isSelectedOpt) CoralRed else BorderLine,
                                if (isAnswered && isCorrectOpt) EmeraldGreen else BorderLine
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(
                                        when {
                                            isAnswered && isCorrectOpt -> EmeraldGreen
                                            isAnswered && isSelectedOpt -> CoralRed
                                            else -> RoyalBlueLight
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + optIndex).toString(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isAnswered && (isCorrectOpt || isSelectedOpt)) Color.White else RoyalBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isAnswered && isCorrectOpt) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isAnswered && isCorrectOpt) EmeraldGreen else if (isAnswered && isSelectedOpt) CoralRed else NavyDark
                                )
                            )
                        }

                        if (isAnswered && isCorrectOpt) {
                            Text("✓ +${currentItem.points * streak} XP", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (isAnswered) {
            Button(
                onClick = {
                    if (currentIndex < res.items.size - 1) {
                        currentIndex += 1
                        selectedOption = null
                    } else {
                        isCompleted = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text(
                    text = if (currentIndex < res.items.size - 1) "Next Question ➡️" else "Finish Race 🏁",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 12: Anagram Word Scramble View
// -------------------------------------------------------------
@Composable
private fun AnagramScrambleView(viewModel: ClassCompanionViewModel) {
    val result by viewModel.anagramResult.collectAsState()

    if (result == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to generate an Anagram word scramble.", color = TextMuted)
        }
        return
    }

    val res = result!!
    var wordIndex by remember(res) { mutableStateOf(0) }
    val currentItem = res.items.getOrNull(wordIndex) ?: return

    var placedIndices by remember(res, wordIndex) { mutableStateOf<List<Int>>(emptyList()) }
    var isCorrect by remember(res, wordIndex) { mutableStateOf<Boolean?>(null) }
    var streak by remember(res) { mutableStateOf(0) }

    val currentSpelled = placedIndices.map { currentItem.scrambled[it] }.joinToString("")
    val targetWord = currentItem.word

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Word ${wordIndex + 1} of ${res.items.size}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue)
            )
            StatusBadge(text = "🔥 Streak: ${streak}x", color = ThaiGoldDark, bgColor = ThaiGoldContainer)
        }

        // Hint & Definition Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡 Clue (คำใบ้): ${currentItem.hintTh}",
                        style = MaterialTheme.typography.titleSmall.copy(color = NavyDark, fontWeight = FontWeight.Bold)
                    )
                    IconButton(
                        onClick = { viewModel.speechManager.speak(currentItem.word) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Clue Audio", tint = RoyalBlue)
                    }
                }
                if (currentItem.definitionEn.isNotBlank()) {
                    Text(
                        text = "Meaning: ${currentItem.definitionEn}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }

        // Target Word Letter Slots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark, RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until targetWord.length) {
                val char = if (i < placedIndices.size) currentItem.scrambled[placedIndices[i]] else null
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isCorrect == true -> EmeraldGreen
                                isCorrect == false -> CoralRed
                                char != null -> ThaiGold
                                else -> NavySecondary
                            }
                        )
                        .clickable(enabled = char != null && isCorrect != true) {
                            placedIndices = placedIndices.toMutableList().also { it.removeAt(i) }
                            isCorrect = null
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char?.toString() ?: "_",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (char != null) NavyDark else SlateLight
                        )
                    )
                }
            }
        }

        // Available Scrambled Letter Tiles
        Text(
            text = "Tap letters to build word:",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            currentItem.scrambled.forEachIndexed { sIndex, letter ->
                val isUsed = placedIndices.contains(sIndex)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isUsed) SlateLight else RoyalBlue)
                        .clickable(enabled = !isUsed && placedIndices.size < targetWord.length && isCorrect != true) {
                            placedIndices = placedIndices + sIndex
                            isCorrect = null
                            if (placedIndices.size == targetWord.length) {
                                val fullSpelled = (placedIndices).map { currentItem.scrambled[it] }.joinToString("")
                                if (fullSpelled.equals(targetWord, ignoreCase = true)) {
                                    isCorrect = true
                                    streak += 1
                                    viewModel.speechManager.speak("Correct! $targetWord")
                                } else {
                                    isCorrect = false
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isUsed) TextMuted else Color.White
                        )
                    )
                }
            }
        }

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    placedIndices = emptyList()
                    isCorrect = null
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Clear")
            }

            if (isCorrect == true) {
                Button(
                    onClick = {
                        if (wordIndex < res.items.size - 1) {
                            wordIndex += 1
                            placedIndices = emptyList()
                            isCorrect = null
                        }
                    },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text(
                        text = if (wordIndex < res.items.size - 1) "Next Word ➡️" else "All Solved! 🎉",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 13: True / False Speed View
// -------------------------------------------------------------
@Composable
private fun TrueFalseSpeedView(viewModel: ClassCompanionViewModel) {
    val result by viewModel.trueFalseResult.collectAsState()

    if (result == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to start True/False Speed Practice.", color = TextMuted)
        }
        return
    }

    val res = result!!
    var currentIndex by remember(res) { mutableStateOf(0) }
    var score by remember(res) { mutableStateOf(0) }
    var streak by remember(res) { mutableStateOf(0) }
    var userAnswer by remember(res, currentIndex) { mutableStateOf<Boolean?>(null) }
    var isFinished by remember(res) { mutableStateOf(false) }

    if (isFinished) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("⚖️", fontSize = 42.sp)
                Text(
                    text = "True / False Complete!",
                    style = MaterialTheme.typography.titleMedium.copy(color = ThaiGold, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Final Score: $score / ${res.items.size * 100} XP",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )

                Button(
                    onClick = {
                        currentIndex = 0
                        score = 0
                        streak = 0
                        userAnswer = null
                        isFinished = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Play Again", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val currentItem = res.items.getOrNull(currentIndex) ?: return
    val isAnswered = userAnswer != null
    val isCorrect = isAnswered && (userAnswer == currentItem.isTrue)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Statement ${currentIndex + 1} of ${res.items.size}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = "⭐ $score XP", color = ThaiGoldDark, bgColor = ThaiGoldContainer)
                if (streak > 1) {
                    StatusBadge(text = "🔥 ${streak}x Streak", color = CoralRed, bgColor = CrimsonRedLight)
                }
            }
        }

        // Statement Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("“ STATEMENT ”", style = MaterialTheme.typography.labelSmall.copy(color = RoyalBlue, fontWeight = FontWeight.Bold))
                    IconButton(
                        onClick = { viewModel.speechManager.speak(currentItem.statementEn) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Read", tint = RoyalBlue)
                    }
                }

                Text(
                    text = currentItem.statementEn,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark)
                )

                if (currentItem.statementTh.isNotBlank()) {
                    Text(
                        text = currentItem.statementTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }

        // Big False / True Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (!isAnswered) {
                        userAnswer = false
                        if (!currentItem.isTrue) {
                            score += 100
                            streak += 1
                            viewModel.speechManager.speak("Correct! False.")
                        } else {
                            streak = 0
                            viewModel.speechManager.speak("Incorrect")
                        }
                    }
                },
                enabled = !isAnswered,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralRed,
                    contentColor = Color.White
                )
            ) {
                Text("❌ FALSE (เท็จ)", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (!isAnswered) {
                        userAnswer = true
                        if (currentItem.isTrue) {
                            score += 100
                            streak += 1
                            viewModel.speechManager.speak("Correct! True.")
                        } else {
                            streak = 0
                            viewModel.speechManager.speak("Incorrect")
                        }
                    }
                },
                enabled = !isAnswered,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreen,
                    contentColor = Color.White
                )
            ) {
                Text("✅ TRUE (จริง)", fontWeight = FontWeight.Bold)
            }
        }

        // Feedback Banner
        if (isAnswered) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isCorrect) EmeraldGreenLight else CrimsonRedLight)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isCorrect) "✓ Correct Answer!" else "✕ Incorrect",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) EmeraldGreen else CoralRed
                        )
                    )
                    if (currentItem.explanation.isNotBlank()) {
                        Text(
                            text = currentItem.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(color = NavyDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (currentIndex < res.items.size - 1) {
                                currentIndex += 1
                                userAnswer = null
                            } else {
                                isFinished = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(
                            text = if (currentIndex < res.items.size - 1) "Next Statement ➡️" else "Finish 🏁",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Activity 14: Open The Box View
// -------------------------------------------------------------
@Composable
private fun OpenTheBoxView(viewModel: ClassCompanionViewModel) {
    val result by viewModel.openBoxResult.collectAsState()

    if (result == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tap '✨ Generate Activity' to create an Open The Mystery Box challenge.", color = TextMuted)
        }
        return
    }

    val res = result!!
    var openedBoxes by remember(res) { mutableStateOf<Set<Int>>(emptySet()) }
    var solvedBoxes by remember(res) { mutableStateOf<Set<Int>>(emptySet()) }
    var activeBox by remember(res) { mutableStateOf<OpenBoxItem?>(null) }
    var showAnswer by remember(res, activeBox) { mutableStateOf(false) }
    var totalScore by remember(res) { mutableStateOf(0) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = res.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark)
                )
                Text(
                    text = "Opened: ${openedBoxes.size} / ${res.boxes.size} Boxes",
                    style = MaterialTheme.typography.bodySmall.copy(color = RoyalBlue)
                )
            }
            StatusBadge(text = "⭐ $totalScore XP", color = ThaiGoldDark, bgColor = ThaiGoldContainer)
        }

        // Active Box Reveal Dialog / Card
        if (activeBox != null) {
            val box = activeBox!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎁 MYSTERY BOX #${box.boxNumber} (⭐ ${box.points} XP)",
                            style = MaterialTheme.typography.labelMedium.copy(color = ThaiGold, fontWeight = FontWeight.Bold)
                        )
                        IconButton(
                            onClick = { viewModel.speechManager.speak(box.questionEn) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color.White)
                        }
                    }

                    Text(
                        text = box.questionEn,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )

                    if (box.questionTh.isNotBlank()) {
                        Text(
                            text = box.questionTh,
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateLight)
                        )
                    }

                    if (showAnswer) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = EmeraldGreenLight)
                        ) {
                            Text(
                                text = "Answer: ${box.answer}",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAnswer = !showAnswer },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ThaiGold)
                        ) {
                            Text(if (showAnswer) "Hide Answer" else "Show Answer")
                        }

                        Button(
                            onClick = {
                                solvedBoxes = solvedBoxes + box.boxNumber
                                totalScore += box.points
                                activeBox = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("✓ Solved (+${box.points})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2x3 Grid of Mystery Boxes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (chunk in res.boxes.chunked(3)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (box in chunk) {
                        val isOpened = openedBoxes.contains(box.boxNumber)
                        val isSolved = solvedBoxes.contains(box.boxNumber)

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(88.dp)
                                .clickable {
                                    openedBoxes = openedBoxes + box.boxNumber
                                    activeBox = box
                                    showAnswer = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSolved -> EmeraldGreenLight
                                    isOpened -> SurfaceCard
                                    else -> NavyPrimary
                                }
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(
                                        if (isSolved) EmeraldGreen else if (isOpened) BorderLine else ThaiGold,
                                        if (isSolved) EmeraldGreen else if (isOpened) BorderLine else ThaiGoldLight
                                    )
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isSolved) "✅" else if (isOpened) "📦" else "🎁",
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "Box ${box.boxNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSolved) EmeraldGreen else if (isOpened) NavyDark else Color.White
                                    )
                                )
                                Text(
                                    text = "${box.points} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSolved) EmeraldGreen else if (isOpened) TextMuted else ThaiGold
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

// -------------------------------------------------------------
// Quick Create Class Dialog
// -------------------------------------------------------------
@Composable
fun CreateClassDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("M.1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Class · สร้างห้องเรียนใหม่") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Class Name (e.g. M.1/4)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Grade Level (ระดับชั้น):", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("M.1", "M.4", "M.5", "M.6").forEach { g ->
                        FilterChip(
                            selected = grade == g,
                            onClick = { grade = g },
                            label = { Text(g) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name.trim(), grade) },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
