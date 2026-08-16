package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.AIActivityGenerator
import com.example.data.powerpoint.*
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.PowerPointPresenterDialog
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerPointsScreen(
    viewModel: ClassCompanionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activeClass by viewModel.activeClass.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<PptCategory?>(null) }
    var selectedGrade by remember { mutableStateOf<String?>(null) }

    // Dynamic decks list initialized with catalog
    var customDecks by remember { mutableStateOf<List<PowerPointDeckModel>>(emptyList()) }
    var presentingDeck by remember { mutableStateOf<PowerPointDeckModel?>(null) }

    // AI Generation Modal State
    var showAiGenDialog by remember { mutableStateOf(false) }
    var aiTopicInput by remember { mutableStateOf("") }
    var aiGradeInput by remember { mutableStateOf("M.3") }
    var isGeneratingAiDeck by remember { mutableStateOf(false) }

    val allDecks = remember(customDecks) {
        customDecks + PowerPointCatalog.allDecks
    }

    val filteredDecks = remember(allDecks, searchQuery, selectedCategory, selectedGrade) {
        allDecks.filter { deck ->
            val matchesQuery = searchQuery.isBlank() ||
                    deck.title.contains(searchQuery, ignoreCase = true) ||
                    deck.titleTh.contains(searchQuery, ignoreCase = true) ||
                    deck.description.contains(searchQuery, ignoreCase = true) ||
                    deck.tags.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesCategory = selectedCategory == null || deck.category == selectedCategory
            val matchesGrade = selectedGrade == null || deck.gradeLevel.contains(selectedGrade!!)

            matchesQuery && matchesCategory && matchesGrade
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("powerpoints_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "iSLCollective PowerPoint Hub",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "Interactive Smartboard Presentations & Games",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("ppt_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAiGenDialog = true },
                        modifier = Modifier.testTag("ppt_ai_create_btn")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Generate Deck", tint = ThaiGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary)
                ) {
                    Box(modifier = Modifier.padding(18.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ThaiGold.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, ThaiGold)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("📽️", fontSize = 12.sp)
                                        Text("iSLCollective Classroom Decks", color = ThaiGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RoyalBlue.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "${allDecks.size} Ready Decks",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Interactive PPT Presentation & Smartboard Hub",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )

                            Text(
                                text = "Engage your Thai students with 16:9 interactive slide decks, team jeopardy competitions, mystery bomb boxes, taboo guessing, and automated LINE homework dispatch.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showAiGenDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ThaiGold, contentColor = NavyPrimary)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Create AI Deck", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        // Quick play first game deck
                                        val gameDeck = allDecks.find { it.category == PptCategory.GAMES } ?: allDecks.first()
                                        presentingDeck = gameDeck
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Quick Play", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ppt_search_input"),
                    placeholder = { Text("Search by topic, grammar rule, O-NET, or grade...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // 3. Category Carousel
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Categories (หมวดหมู่สไลด์)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null },
                                label = { Text("All (${allDecks.size})", fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        items(PptCategory.values()) { cat ->
                            val count = allDecks.count { it.category == cat }
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                                label = { Text("${cat.icon} ${cat.titleEn} ($count)", fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // 4. Grade Level Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val grades = listOf("All Grades", "M.1", "M.2", "M.3", "M.4", "M.5", "M.6")
                    items(grades) { g ->
                        val isAll = g == "All Grades"
                        val isSelected = (isAll && selectedGrade == null) || selectedGrade == g
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedGrade = if (isAll || selectedGrade == g) null else g
                            },
                            label = { Text(g, fontSize = 11.sp) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // 5. Deck Items Section
            item {
                SectionHeader(
                    title = "Available Slide Decks (${filteredDecks.size})",
                    action = {
                        if (filteredDecks.size != allDecks.size) {
                            TextButton(onClick = {
                                searchQuery = ""
                                selectedCategory = null
                                selectedGrade = null
                            }) {
                                Text("Clear Filters", fontSize = 12.sp)
                            }
                        }
                    }
                )
            }

            if (filteredDecks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔍", fontSize = 32.sp)
                            Text("No Slide Decks Found", fontWeight = FontWeight.Bold)
                            Text(
                                "Try searching for a different keyword or create an AI slide deck instantly.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { showAiGenDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Generate with AI")
                            }
                        }
                    }
                }
            } else {
                items(filteredDecks) { deck ->
                    PowerPointDeckCard(
                        deck = deck,
                        onPresent = { presentingDeck = deck },
                        onAssignLine = {
                            val summary = "📽️ [iSLCollective Slide Deck: ${deck.title}]\nGrade: ${deck.gradeLevel}\nOverview: ${deck.description}\nSlides: ${deck.totalSlides} slides\n" +
                                    deck.slides.joinToString("\n") { "• Slide ${it.slideNumber}: ${it.title}" }
                            viewModel.sendDirectHomework(summary)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Fullscreen Smartboard / Presenter Dialog
    presentingDeck?.let { deck ->
        PowerPointPresenterDialog(
            deck = deck,
            onDismiss = { presentingDeck = null },
            onSpeakText = { text -> viewModel.speechManager.speak(text) },
            onAssignLine = { homeworkContent ->
                viewModel.sendDirectHomework(homeworkContent)
            }
        )
    }

    // AI PowerPoint Generator Dialog
    if (showAiGenDialog) {
        AlertDialog(
            onDismissRequest = { if (!isGeneratingAiDeck) showAiGenDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✨", fontSize = 20.sp)
                    Text("AI PowerPoint Studio", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Generate an interactive 5-slide iSLCollective ESL presentation with warm-ups, grammar rules, team challenges, and homework wrap-up.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    OutlinedTextField(
                        value = aiTopicInput,
                        onValueChange = { aiTopicInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ppt_ai_topic_input"),
                        label = { Text("Topic or Grammar Lesson") },
                        placeholder = { Text("e.g., Past Continuous vs Past Simple, Giving Directions") },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Grade selector
                    Text("Target Grade:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("M.1", "M.2", "M.3", "M.4", "M.5", "M.6").forEach { g ->
                            FilterChip(
                                selected = aiGradeInput == g,
                                onClick = { aiGradeInput = g },
                                label = { Text(g, fontSize = 11.sp) },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    // Preset chips
                    Text("Or Choose a Preset Lesson:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val presets = listOf(
                            "Ordering Food at a Cafe",
                            "Conditionals (If-Clause)",
                            "Passive Voice in Daily Life",
                            "TGAT Error Identification",
                            "Describing Personalities"
                        )
                        items(presets) { p ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { aiTopicInput = p }
                            ) {
                                Text(
                                    text = p,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (isGeneratingAiDeck) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generating 5-slide interactive deck...", fontSize = 12.sp, color = RoyalBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val topic = aiTopicInput.ifBlank { "English Conversation & Grammar" }
                        isGeneratingAiDeck = true
                        coroutineScope.launch {
                            try {
                                val generated = AIActivityGenerator.generatePowerPointDeck(topic, aiGradeInput)
                                customDecks = listOf(generated) + customDecks
                                showAiGenDialog = false
                                isGeneratingAiDeck = false
                                presentingDeck = generated
                            } catch (e: Exception) {
                                isGeneratingAiDeck = false
                                showAiGenDialog = false
                            }
                        }
                    },
                    enabled = !isGeneratingAiDeck,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    modifier = Modifier.testTag("ppt_ai_submit_btn")
                ) {
                    Text("Generate & Present")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAiGenDialog = false },
                    enabled = !isGeneratingAiDeck
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// POWERPOINT DECK CARD COMPONENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun PowerPointDeckCard(
    deck: PowerPointDeckModel,
    onPresent: () -> Unit,
    onAssignLine: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPresent() }
            .testTag("ppt_deck_card_${deck.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Emoji, Title, Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(deck.badgeIcon, fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = deck.titleTh,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (deck.category) {
                        PptCategory.GAMES -> CoralRed.copy(alpha = 0.15f)
                        PptCategory.GRAMMAR -> RoyalBlue.copy(alpha = 0.15f)
                        PptCategory.ONET_TGAT -> ThaiGold.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = deck.category.titleEn,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (deck.category) {
                            PptCategory.GAMES -> CoralRed
                            PptCategory.GRAMMAR -> RoyalBlue
                            PptCategory.ONET_TGAT -> Color(0xFFB45309)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Description
            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Meta Info: Grade, Slides, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "🎓 ${deck.gradeLevel}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "🎞️ ${deck.totalSlides} Slides",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "⏱️ ~${deck.estimatedMinutes} min",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = deck.sourceAttribution,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Tags Carousel
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(deck.tags) { tag ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "#$tag",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Action Buttons: Present & Assign to LINE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPresent,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Present Slide Deck", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onAssignLine,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LineGreen),
                    border = BorderStroke(1.dp, LineGreen)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LINE Inbox", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
