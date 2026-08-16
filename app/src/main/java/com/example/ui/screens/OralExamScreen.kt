package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.StudentEntity
import com.example.data.model.VocabWordEntity
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.util.CuratedVocabWord
import com.example.util.PronunciationEvaluator
import com.example.util.PronunciationRating
import com.example.util.PronunciationResult

@Composable
fun OralExamScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeClass by viewModel.activeClass.collectAsState()
    val vocabList by viewModel.vocabInActiveClass.collectAsState()
    val students by viewModel.studentsInActiveClass.collectAsState()

    val isExamActive by viewModel.isOralExamActive.collectAsState()
    val isExamCompleted by viewModel.isOralExamCompleted.collectAsState()
    val selectedStudent by viewModel.oralExamSelectedStudent.collectAsState()
    val examWords by viewModel.oralExamWordList.collectAsState()
    val currentIndex by viewModel.oralExamCurrentIndex.collectAsState()
    val examResults by viewModel.oralExamResults.collectAsState()

    val isListening by viewModel.speechManager.isListening.collectAsState()
    val rmsLevel by viewModel.speechManager.rmsLevel.collectAsState()
    val speechError by viewModel.speechManager.speechError.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Vocab Oral Exam, 1: Curated Grade Decks, 2: Conversation Topics
    var selectedGradeFilter by remember { mutableStateOf("All") }
    var selectedWordCount by remember { mutableIntStateOf(5) }
    var shuffleWords by remember { mutableStateOf(false) }

    // Audio Permission Launcher
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    fun startListeningWithPermission(onResult: (String) -> Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            viewModel.speechManager.startListening(onResult)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Mode Header & Tab Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("oral_exam_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(ThaiGold, NavyPrimary))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ORAL EXAM & PRONUNCIATION · สอบพูดและออกเสียง",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = ThaiGoldDark
                            )
                            Text(
                                text = "Speech-to-Text Pronunciation Checker",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                        }
                        StatusBadge(
                            text = if (isExamActive) "Exam Active" else "Ready",
                            color = if (isExamActive) EmeraldGreen else RoyalBlue,
                            bgColor = if (isExamActive) EmeraldGreenLight else RoyalBlueLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Students speak English vocabulary into the microphone. Android Speech-to-Text evaluates pronunciation accuracy, syllable stress, and phonetic clarity against the vocabulary bank in real time.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = OffWhite,
                        contentColor = NavyPrimary,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("🎙️ Vocab Bank Exam", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("📚 Grade Decks", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("💬 Oral Topics", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }
                }
            }
        }

        // Main Tab Content
        when (selectedTab) {
            0 -> {
                // TAB 0: VOCAB BANK ORAL EXAM
                if (isExamCompleted) {
                    // EXAM COMPLETED SUMMARY
                    item {
                        ExamCompletedSummaryCard(
                            student = selectedStudent,
                            className = activeClass?.name ?: "M.1/3",
                            words = examWords,
                            results = examResults,
                            onSaveToAssessments = { viewModel.saveOralExamScoreToAssessments() },
                            onSendLine = { selectedStudent?.let { s -> viewModel.sendOralExamResultToLine(s) } },
                            onRestart = { viewModel.restartOralExam() },
                            onNewStudent = {
                                viewModel.isOralExamCompleted.value = false
                                viewModel.isOralExamActive.value = false
                            }
                        )
                    }
                } else if (isExamActive && examWords.isNotEmpty()) {
                    // ACTIVE EXAM IN PROGRESS
                    item {
                        ActiveExamWordCard(
                            student = selectedStudent,
                            className = activeClass?.name ?: "M.1/3",
                            word = examWords[currentIndex.coerceIn(0, examWords.size - 1)],
                            currentIndex = currentIndex,
                            totalWords = examWords.size,
                            currentResult = examResults[examWords[currentIndex.coerceIn(0, examWords.size - 1)].id.ifBlank { examWords[currentIndex.coerceIn(0, examWords.size - 1)].en }],
                            isListening = isListening,
                            rmsLevel = rmsLevel,
                            speechError = speechError,
                            hasPermission = hasAudioPermission,
                            onRequestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            onListenNormal = { viewModel.speechManager.speak(examWords[currentIndex].en, 0.92f) },
                            onListenSlow = { viewModel.speechManager.speakSlow(examWords[currentIndex].en) },
                            onStartRecording = {
                                startListeningWithPermission { transcript ->
                                    viewModel.evaluateAndRecordPronunciation(transcript)
                                }
                            },
                            onStopRecording = { viewModel.speechManager.stopListening() },
                            onNext = { viewModel.nextOralExamWord() },
                            onPrev = { viewModel.prevOralExamWord() },
                            onFinish = { viewModel.finishOralExam() }
                        )
                    }

                    // Word Navigation Strip
                    item {
                        WordNavigationStrip(
                            words = examWords,
                            currentIndex = currentIndex,
                            results = examResults,
                            onSelectWord = { idx -> viewModel.jumpToOralExamWord(idx) }
                        )
                    }
                } else {
                    // EXAM LAUNCH CONFIGURATION PANEL
                    item {
                        ExamSetupConfigCard(
                            activeClass = activeClass?.name ?: "M.1/3",
                            grade = activeClass?.grade ?: "M.1",
                            vocabCount = vocabList.size,
                            students = students,
                            selectedStudent = selectedStudent,
                            onSelectStudent = { viewModel.oralExamSelectedStudent.value = it },
                            selectedWordCount = selectedWordCount,
                            onSelectWordCount = { selectedWordCount = it },
                            shuffle = shuffleWords,
                            onToggleShuffle = { shuffleWords = it },
                            onStartExam = {
                                viewModel.startOralExamWithVocabBank(
                                    student = selectedStudent,
                                    count = if (selectedWordCount > 0) selectedWordCount else null,
                                    shuffle = shuffleWords
                                )
                            },
                            onImportCurated = { grade ->
                                viewModel.importCuratedDeckToClassVocab(grade)
                            }
                        )
                    }

                    // Vocabulary Bank Preview
                    item {
                        SectionHeader(title = "Vocabulary Words in Class Bank (${vocabList.size})")
                    }

                    if (vocabList.isEmpty()) {
                        item {
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No words currently in ${activeClass?.name ?: "this class"} Vocabulary Bank.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.importCuratedDeckToClassVocab(activeClass?.grade ?: "M.1") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ThaiGoldDark)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Import ${activeClass?.grade ?: "M.1"} Vocab Set")
                                    }
                                }
                            }
                        }
                    } else {
                        items(vocabList) { vocab ->
                            VocabExamPreviewRow(
                                word = vocab,
                                onSpeak = { viewModel.speechManager.speak(vocab.en) },
                                onTestSingle = {
                                    viewModel.startOralExamWithVocabBank(
                                        student = selectedStudent,
                                        customWords = listOf(vocab)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: CURATED GRADE VOCABULARY DECKS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "M.1", "M.4", "M.5", "M.6").forEach { grade ->
                            val isSelected = selectedGradeFilter == grade
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedGradeFilter = grade },
                                label = { Text(grade) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                val curatedList = if (selectedGradeFilter == "All") {
                    PronunciationEvaluator.CURATED_VOCAB_BANK
                } else {
                    PronunciationEvaluator.CURATED_VOCAB_BANK.filter { it.grade == selectedGradeFilter }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader(title = "Curated Decks (${curatedList.size} Words)")
                        if (selectedGradeFilter != "All") {
                            TextButton(onClick = { viewModel.importCuratedDeckToClassVocab(selectedGradeFilter) }) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Import to ${activeClass?.name ?: "Class"}")
                            }
                        }
                    }
                }

                items(curatedList) { word ->
                    CuratedWordCard(
                        word = word,
                        onSpeakNormal = { viewModel.speechManager.speak(word.en) },
                        onSpeakSlow = { viewModel.speechManager.speakSlow(word.en) },
                        onStartExamWithWord = {
                            val entity = VocabWordEntity(
                                classId = activeClass?.id ?: "cls_default",
                                en = word.en,
                                th = word.th,
                                example = word.example
                            )
                            selectedTab = 0
                            viewModel.startOralExamWithVocabBank(
                                student = selectedStudent,
                                customWords = listOf(entity)
                            )
                        }
                    )
                }
            }

            2 -> {
                // TAB 2: CONVERSATION TOPICS (Oral Interview)
                item {
                    SectionHeader(title = "Oral Exam Interview Topics (${CURATED_ORAL_TOPICS.size})")
                }

                items(CURATED_ORAL_TOPICS) { topic ->
                    CuratedOralTopicRowCard(
                        topic = topic,
                        onStart = {
                            viewModel.lessonInputText.value = topic.promptContent
                            viewModel.selectedActivityType.value = "sltest"
                            viewModel.currentScreen.value = "companion"
                            viewModel.generateActivity()
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 1: ACTIVE EXAM WORD CARD (STT & TTS ENGINE)
// -------------------------------------------------------------
@Composable
private fun ActiveExamWordCard(
    student: StudentEntity?,
    className: String,
    word: VocabWordEntity,
    currentIndex: Int,
    totalWords: Int,
    currentResult: PronunciationResult?,
    isListening: Boolean,
    rmsLevel: Float,
    speechError: String?,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onListenNormal: () -> Unit,
    onListenSlow: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onFinish: () -> Unit
) {
    val phoneticInfo = remember(word.en) { PronunciationEvaluator.getPhoneticsForWord(word.en) }
    val syllables = phoneticInfo.first
    val ipa = phoneticInfo.second

    // Animated glow scale for microphone
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_exam_word_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (currentResult?.isPassed == true) listOf(EmeraldGreen, NavyPrimary)
                else if (isListening) listOf(ThaiGold, RoyalBlue)
                else listOf(BorderLine, RoyalBlueLight)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info: Progress & Student
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = "Word ${currentIndex + 1} of $totalWords",
                    color = NavyPrimary,
                    bgColor = ThaiGoldContainer
                )

                Text(
                    text = "Candidate: ${student?.name ?: "Classroom Mode"} ($className)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Target Word Display
            Text(
                text = word.en,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyPrimary,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Syllables and IPA
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OffWhite,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = syllables,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = ipa,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thai Meaning
            Text(
                text = "แปลว่า: ${word.th}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ThaiGoldDark
                )
            )

            if (!word.example.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${word.example}\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Reference Controls (TTS)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onListenNormal,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalBlue)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Listen (1.0x)")
                }

                OutlinedButton(
                    onClick = onListenSlow,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ThaiGoldDark)
                ) {
                    Icon(Icons.Outlined.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Slow (0.7x)")
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Center Interactive Microphone Area
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(110.dp)
            ) {
                // Pulsing ambient background ring when listening
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size((90 * pulseScale + rmsLevel * 20).dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        ThaiGold.copy(alpha = 0.6f),
                                        EmeraldGreen.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                FloatingActionButton(
                    onClick = {
                        if (isListening) {
                            onStopRecording()
                        } else {
                            if (!hasPermission) {
                                onRequestPermission()
                            } else {
                                onStartRecording()
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = if (isListening) EmeraldGreen else NavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(76.dp)
                        .testTag("oral_exam_mic_button")
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                        contentDescription = "Microphone",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // State prompt text
            Text(
                text = if (isListening) "🎙️ Listening... Speak '${word.en}' now!"
                else "Tap microphone and pronounce '${word.en}'",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) EmeraldGreen else RoyalBlue
                ),
                textAlign = TextAlign.Center
            )

            // Audio Level Indicator
            if (isListening) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { rmsLevel },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldGreen,
                    trackColor = OffWhite,
                )
            }

            if (!speechError.isNullOrBlank() && !isListening) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ $speechError",
                    style = MaterialTheme.typography.bodySmall.copy(color = CoralRed),
                    textAlign = TextAlign.Center
                )
            }

            // Real-Time Evaluation Result Card
            if (currentResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                PronunciationFeedbackCard(result = currentResult)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Controls: Prev, Retry, Next, Finish
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    Text("Prev")
                }

                if (currentIndex < totalWords - 1) {
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(if (currentResult != null) "Accept & Next" else "Skip to Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                } else {
                    Button(
                        onClick = onFinish,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish Exam")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 2: PRONUNCIATION FEEDBACK CARD
// -------------------------------------------------------------
@Composable
private fun PronunciationFeedbackCard(result: PronunciationResult) {
    val rating = result.rating
    val badgeColor = Color(rating.colorHex)
    val badgeBg = Color(rating.badgeBgHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pronunciation_feedback_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = badgeBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(badgeColor, badgeBg)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = rating.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = "${result.scorePercentage}% (${result.scorePoints}/10)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Recognized: \"${result.spokenTranscript}\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextInk
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = result.feedbackEn,
                style = MaterialTheme.typography.bodySmall.copy(color = TextInk)
            )

            Text(
                text = result.feedbackTh,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = badgeColor,
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (result.audioTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = ThaiGoldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = result.audioTip,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 3: WORD NAVIGATION STRIP
// -------------------------------------------------------------
@Composable
private fun WordNavigationStrip(
    words: List<VocabWordEntity>,
    currentIndex: Int,
    results: Map<String, PronunciationResult>,
    onSelectWord: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(words) { idx, w ->
            val res = results[w.id.ifBlank { w.en }]
            val isCurrent = idx == currentIndex
            val isPassed = res?.isPassed == true

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectWord(idx) },
                color = if (isCurrent) NavyPrimary else if (isPassed) EmeraldGreenLight else SurfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${idx + 1}. ${w.en}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isCurrent) Color.White else if (isPassed) EmeraldGreen else TextInk
                        )
                    )
                    if (res != null) {
                        Text(
                            text = if (isPassed) "✓" else "!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else if (isPassed) EmeraldGreen else CoralRed
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 4: EXAM SETUP CONFIG CARD
// -------------------------------------------------------------
@Composable
private fun ExamSetupConfigCard(
    activeClass: String,
    grade: String,
    vocabCount: Int,
    students: List<StudentEntity>,
    selectedStudent: StudentEntity?,
    onSelectStudent: (StudentEntity?) -> Unit,
    selectedWordCount: Int,
    onSelectWordCount: (Int) -> Unit,
    shuffle: Boolean,
    onToggleShuffle: (Boolean) -> Unit,
    onStartExam: () -> Unit,
    onImportCurated: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_setup_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ORAL EXAM SETUP · ตั้งค่าการสอบพูด",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = RoyalBlue
            )
            Text(
                text = "Class: $activeClass ($grade) • Bank: $vocabCount Words",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Student Selection
            Text(
                text = "Select Student to Grade (or Free Practice):",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextInk)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = selectedStudent == null,
                        onClick = { onSelectStudent(null) },
                        label = { Text("Whole Class / Practice") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(students) { st ->
                    FilterChip(
                        selected = selectedStudent?.id == st.id,
                        onClick = { onSelectStudent(st) },
                        label = { Text(st.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Word Count Selector
            Text(
                text = "Exam Word Count:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextInk)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(5, 10, 15, 0).forEach { cnt ->
                    val label = if (cnt == 0) "All ($vocabCount)" else "$cnt Words"
                    FilterChip(
                        selected = selectedWordCount == cnt,
                        onClick = { onSelectWordCount(cnt) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ThaiGoldDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Start Exam
            Button(
                onClick = onStartExam,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_oral_exam_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Launch Oral Exam for ${selectedStudent?.name ?: "Class"}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 5: EXAM COMPLETED SUMMARY CARD & GRADEBOOK RECORDING
// -------------------------------------------------------------
@Composable
private fun ExamCompletedSummaryCard(
    student: StudentEntity?,
    className: String,
    words: List<VocabWordEntity>,
    results: Map<String, PronunciationResult>,
    onSaveToAssessments: () -> Unit,
    onSendLine: () -> Unit,
    onRestart: () -> Unit,
    onNewStudent: () -> Unit
) {
    val context = LocalContext.current
    val totalWords = words.size
    val passedCount = results.values.count { it.isPassed }
    val avgScore = if (results.isNotEmpty()) results.values.map { it.scorePoints }.average() else 0.0
    val avgPercent = if (results.isNotEmpty()) results.values.map { it.scorePercentage }.average().toInt() else 0

    val gradeTier = when {
        avgScore >= 8.5 -> "Grade A · Outstanding Fluency 🌟"
        avgScore >= 7.0 -> "Grade B · Good Pronunciation 👍"
        avgScore >= 5.0 -> "Grade C · Fair Attempt ⚠️"
        else -> "Needs Revision · ฝึกซ้ำ 🔄"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_completed_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ThaiGold, EmeraldGreen))),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORAL EXAM REPORT · ผลการทดสอบพูด",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = ThaiGoldDark
                    )
                    Text(
                        text = student?.name ?: "Whole Class Session",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                }
                StatusBadge(text = "$passedCount / $totalWords Passed", color = EmeraldGreen, bgColor = EmeraldGreenLight)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = OffWhite,
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${String.format("%.1f", avgScore)} / 10",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimary
                            )
                        )
                        Text("Average Score", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    }

                    VerticalDivider(modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$avgPercent%",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (avgPercent >= 75) EmeraldGreen else ThaiGoldDark
                            )
                        )
                        Text("Accuracy", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    }

                    VerticalDivider(modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$passedCount/$totalWords",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalBlue
                            )
                        )
                        Text("Words Clear", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = gradeTier,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Word Breakdown List
            Text(
                text = "Detailed Word Breakdown:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextInk)
            )

            Spacer(modifier = Modifier.height(8.dp))

            words.forEachIndexed { idx, w ->
                val res = results[w.id.ifBlank { w.en }]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${idx + 1}. ${w.en} (${w.th})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                        )
                        if (res != null) {
                            Text(
                                text = "Heard: \"${res.spokenTranscript}\"",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                            )
                        }
                    }

                    if (res != null) {
                        StatusBadge(
                            text = "${res.scorePercentage}%",
                            color = if (res.isPassed) EmeraldGreen else CoralRed,
                            bgColor = if (res.isPassed) EmeraldGreenLight else CoralRedLight
                        )
                    } else {
                        StatusBadge(text = "Skipped", color = TextMuted, bgColor = OffWhite)
                    }
                }
                HorizontalDivider(color = BorderLine.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSaveToAssessments,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("💾 Save Score to Assessment Gradebook")
                }

                if (student != null) {
                    Button(
                        onClick = onSendLine,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("💬 Send Oral Scorecard to ${student.name}'s LINE")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val text = "🎙️ Oral Exam Result: ${student?.name ?: "Student"}\n" +
                                    "Class: $className\n" +
                                    "Score: ${String.format("%.1f", avgScore)}/10 ($avgPercent%)\n" +
                                    "Passed: $passedCount/$totalWords words\n" +
                                    words.joinToString("\n") {
                                        val r = results[it.id.ifBlank { it.en }]
                                        "- ${it.en}: ${r?.scorePercentage ?: 0}% (\"${r?.spokenTranscript ?: ""}\")"
                                    }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Oral Exam", text))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Report")
                    }

                    OutlinedButton(
                        onClick = onRestart,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retake")
                    }
                }

                TextButton(
                    onClick = onNewStudent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("👥 Test Another Student or Setup New Exam")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 6: VOCAB EXAM PREVIEW ROW
// -------------------------------------------------------------
@Composable
private fun VocabExamPreviewRow(
    word: VocabWordEntity,
    onSpeak: () -> Unit,
    onTestSingle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = word.en,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = "— ${word.th}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextInk)
                    )
                }

                if (!word.example.isNullOrBlank()) {
                    Text(
                        text = "\"${word.example}\"",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onSpeak) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = RoyalBlue)
                }

                Button(
                    onClick = onTestSingle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test", fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 7: CURATED WORD CARD
// -------------------------------------------------------------
@Composable
private fun CuratedWordCard(
    word: CuratedVocabWord,
    onSpeakNormal: () -> Unit,
    onSpeakSlow: () -> Unit,
    onStartExamWithWord: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(text = word.grade, color = NavyPrimary, bgColor = ThaiGoldContainer)
                    Text(
                        text = word.en,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OffWhite,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = word.ipa,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RoyalBlue,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Syllables: ${word.syllables} • ${word.th}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ThaiGoldDark
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "\"${word.example}\"",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💡 ${word.phoneticTip}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextInk,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onSpeakNormal,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("1.0x", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onSpeakSlow,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Outlined.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("0.7x", fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = onStartExamWithWord,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Pronunciation", fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 8: CURATED ORAL TOPIC ROW CARD
// -------------------------------------------------------------
@Composable
private fun CuratedOralTopicRowCard(
    topic: CuratedTopic,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(text = topic.grade, color = NavyPrimary, bgColor = ThaiGoldContainer)
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                }

                Text(
                    text = topic.titleTh,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = RoyalBlue,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Key questions: ${topic.questionsPreview}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Button(
                onClick = onStart,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Start", fontSize = 12.sp)
            }
        }
    }
}
