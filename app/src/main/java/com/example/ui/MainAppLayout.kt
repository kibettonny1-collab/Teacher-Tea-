package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppStrings
import com.example.ui.components.ThaiFlagRibbon
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

data class NavDestination(
    val id: String,
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val NAV_ITEMS = listOf(
    NavDestination("home", "home", Icons.Filled.Home, Icons.Outlined.Home),
    NavDestination("companion", "companion", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    NavDestination("worksheets", "worksheets", Icons.Filled.CollectionsBookmark, Icons.Outlined.CollectionsBookmark),
    NavDestination("students", "students", Icons.Filled.People, Icons.Outlined.People),
    NavDestination("vocab", "vocab", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    NavDestination("assess", "assess", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    NavDestination("speaktest", "speaktest", Icons.Filled.Mic, Icons.Outlined.Mic),
    NavDestination("reports", "reports", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    NavDestination("settings", "settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val activeClass by viewModel.activeClass.collectAsState()
    val classes by viewModel.allClasses.collectAsState()
    val lang = userSettings?.language ?: "en"

    val snackbarHostState = remember { SnackbarHostState() }
    var showClassMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (userSettings == null || (userSettings?.teacherName.isNullOrBlank() && userSettings?.schoolName.isNullOrBlank())) {
        RoleSetupScreen(viewModel = viewModel)
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 720.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ThaiGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 16.sp)
                        }

                        Column {
                            Text(
                                text = AppStrings.t(NAV_ITEMS.find { it.id == currentScreen }?.titleKey ?: "companion", lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyPrimary
                                )
                            )
                            if (activeClass != null) {
                                Text(
                                    text = "${activeClass?.name} (${activeClass?.grade})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Class selector badge / dropdown
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showClassMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = RoyalBlueLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = activeClass?.name ?: "Classes",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showClassMenu,
                            onDismissRequest = { showClassMenu = false }
                        ) {
                            classes.forEach { cls ->
                                DropdownMenuItem(
                                    text = { Text("${cls.name} (${cls.grade}) - ${cls.joinCode}") },
                                    onClick = {
                                        viewModel.selectClass(cls.id)
                                        showClassMenu = false
                                    },
                                    leadingIcon = if (cls.id == activeClass?.id) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = RoyalBlue) }
                                    } else null
                                )
                            }
                        }
                    }

                    // Language Quick Toggle
                    IconButton(
                        onClick = {
                            val nextLang = when (lang) {
                                "en" -> "th"
                                "th" -> "zh"
                                else -> "en"
                            }
                            viewModel.setLanguage(nextLang)
                        }
                    ) {
                        Text(
                            text = when (lang) {
                                "th" -> "🇹🇭"
                                "zh" -> "🇨🇳"
                                else -> "🇺🇸"
                            },
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceCard,
                    titleContentColor = NavyPrimary
                )
            )
        },
        bottomBar = {
            if (!isExpanded) {
                NavigationBar(
                    containerColor = SurfaceCard,
                    tonalElevation = 8.dp
                ) {
                    val visibleItems = listOf(
                        NAV_ITEMS[0], // Home
                        NAV_ITEMS[1], // Companion
                        NAV_ITEMS[2], // Worksheets
                        NAV_ITEMS[3], // Students
                        NAV_ITEMS[4], // Vocab
                        NAV_ITEMS[5]  // Assess
                    )
                    visibleItems.forEach { item ->
                        val isSelected = currentScreen == item.id
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.currentScreen.value = item.id },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.titleKey,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = AppStrings.t(item.titleKey, lang),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = NavyPrimary,
                                indicatorColor = ThaiGoldContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Navigation Rail for Tablet / Expanded Layout
            if (isExpanded) {
                NavigationRail(
                    containerColor = SurfaceCard,
                    header = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ThaiGold)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 20.sp)
                        }
                    }
                ) {
                    NAV_ITEMS.forEach { item ->
                        val isSelected = currentScreen == item.id
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { viewModel.currentScreen.value = item.id },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.titleKey
                                )
                            },
                            label = { Text(AppStrings.t(item.titleKey, lang), fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = NavyPrimary,
                                indicatorColor = ThaiGoldContainer
                            )
                        )
                    }
                }
            }

            // Main Screen Body with Crossfade Transition
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(BackgroundLight)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                    when (screen) {
                        "home" -> HomeScreen(viewModel = viewModel)
                        "companion" -> CompanionScreen(viewModel = viewModel)
                        "worksheets" -> WorksheetsScreen(viewModel = viewModel)
                        "students" -> StudentsScreen(viewModel = viewModel)
                        "vocab" -> VocabBankScreen(viewModel = viewModel)
                        "assess" -> AssessmentScreen(viewModel = viewModel)
                        "speaktest" -> SpeakTestTopicsScreen(viewModel = viewModel)
                        "reports" -> ReportsScreen(viewModel = viewModel)
                        "settings" -> SettingsScreen(viewModel = viewModel)
                        else -> HomeScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
}
