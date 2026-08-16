package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.ClassRoomEntity
import com.example.data.model.StudentEntity
import com.example.ui.ClassCompanionViewModel
import com.example.ui.theme.*
import com.example.util.CameraQrScannerHelper
import com.example.util.ParsedStudentInfo
import com.example.util.QrFormatType
import com.example.util.QrParseResult
import com.example.util.QrStudentParser
import com.example.util.SampleQrCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraQrScannerDialog(
    viewModel: ClassCompanionViewModel,
    activeClass: ClassRoomEntity?,
    existingStudents: List<StudentEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Scanner state
    var isTorchOn by remember { mutableStateOf(false) }
    var isBackCamera by remember { mutableStateOf(true) }
    var isBatchMode by remember { mutableStateOf(false) }
    var showSampleListDrawer by remember { mutableStateOf(false) }
    var lastScannedRawText by remember { mutableStateOf<String?>(null) }
    var parseResult by remember { mutableStateOf<QrParseResult?>(null) }
    var batchScannedList by remember { mutableStateOf<List<ParsedStudentInfo>>(emptyList()) }
    var scannerHelper by remember { mutableStateOf<CameraQrScannerHelper?>(null) }
    var selectedStudentsInBatch by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Audio beeper for successful scans
    val toneGenerator = remember {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        } catch (_: Exception) {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            toneGenerator?.release()
            scannerHelper?.stopCamera()
        }
    }

    // Function to handle detected QR text
    val handleScannedText: (String) -> Unit = { rawText ->
        if (rawText != lastScannedRawText || isBatchMode) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            lastScannedRawText = rawText
            val result = QrStudentParser.parse(rawText, existingStudents)
            parseResult = result

            if (isBatchMode) {
                // In batch mode, accumulate unique students
                val existingNames = (existingStudents.map { it.name.lowercase() } + batchScannedList.map { it.name.lowercase() }).toSet()
                val newStudents = result.students.filter { !existingNames.contains(it.name.lowercase()) }
                if (newStudents.isNotEmpty()) {
                    batchScannedList = batchScannedList + newStudents
                    viewModel.speechManager.speak("Scanned ${newStudents.first().name}")
                }
            } else {
                // Initialize selection for multi-student or single student
                selectedStudentsInBatch = result.students
                    .filter { !it.isExistingInClass }
                    .map { it.name }
                    .toSet()
            }
        }
    }

    // Animation for scanning laser line
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Dialog(
        onDismissRequest = {
            scannerHelper?.stopCamera()
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("camera_qr_scanner_dialog")
        ) {
            if (hasCameraPermission) {
                // Live CameraX Viewport
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                        val helper = CameraQrScannerHelper(
                            context = ctx,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            onQrCodeDetected = handleScannedText
                        )
                        scannerHelper = helper
                        helper.startCamera()
                        previewView
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("camera_preview_view")
                )
            } else {
                // Permission Fallback Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(RoyalBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Camera Access Required",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = "ต้องการสิทธิ์การใช้งานกล้องเพื่อสแกน QR Code รายชื่อนักเรียน\nCamera is needed to scan student list QR codes from physical paper sheets.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("grant_camera_permission_btn")
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Grant Camera Permission", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showSampleListDrawer = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ThaiGold),
                        border = BorderStroke(1.dp, ThaiGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.School, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Try Sample Class List QRs", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Viewfinder Reticle & Darkened Border Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Control Bar
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                scannerHelper?.stopCamera()
                                onDismiss()
                            },
                            modifier = Modifier.testTag("qr_scanner_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Scanner",
                                tint = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Scan Roster QR Code",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Target: ${activeClass?.name ?: "M.1/3"} (${activeClass?.grade ?: "M.1"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = ThaiGold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Torch Button
                            IconButton(
                                onClick = {
                                    isTorchOn = scannerHelper?.toggleTorch() ?: !isTorchOn
                                },
                                modifier = Modifier.testTag("qr_scanner_torch_btn")
                            ) {
                                Icon(
                                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Torch",
                                    tint = if (isTorchOn) ThaiGold else Color.White
                                )
                            }

                            // Switch Camera
                            IconButton(
                                onClick = {
                                    isBackCamera = scannerHelper?.switchCamera() ?: !isBackCamera
                                },
                                modifier = Modifier.testTag("qr_scanner_switch_cam_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlipCameraAndroid,
                                    contentDescription = "Flip Camera",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // Scanning Target Box / Viewfinder
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            2.dp,
                            Brush.linearGradient(listOf(EmeraldGreen, RoyalBlue, ThaiGold)),
                            RoundedCornerShape(20.dp)
                        )
                        .background(Color.White.copy(alpha = 0.05f))
                        .testTag("qr_viewfinder_box"),
                    contentAlignment = Alignment.Center
                ) {
                    // 4 Corner Markers
                    CornerBracketsOverlay(color = ThaiGold)

                    // Laser Scanning Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (270 * laserPosition).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        EmeraldGreen,
                                        ThaiGold,
                                        EmeraldGreen,
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Center Guide Icon / Watermark
                    if (parseResult == null) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Selector Bar (Single vs Batch Continuous Mode)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, BorderLine.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = !isBatchMode,
                            onClick = {
                                isBatchMode = false
                                batchScannedList = emptyList()
                            },
                            label = { Text("Single / Sheet Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.CropFree, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalBlue,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("qr_mode_single_btn")
                        )

                        FilterChip(
                            selected = isBatchMode,
                            onClick = { isBatchMode = true },
                            label = {
                                Text(
                                    text = if (batchScannedList.isEmpty()) "Rapid Batch Mode" else "Batch (${batchScannedList.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.AutoAwesomeMotion, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("qr_mode_batch_btn")
                        )

                        // Sample Class Lists Trigger
                        IconButton(
                            onClick = { showSampleListDrawer = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("qr_sample_cards_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = "Sample Lists",
                                tint = ThaiGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // Bottom Result / Action Sheet
                AnimatedVisibility(
                    visible = parseResult != null || batchScannedList.isNotEmpty(),
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    if (isBatchMode) {
                        // Batch Scanning Summary Card
                        BatchScannedSummarySheet(
                            scannedStudents = batchScannedList,
                            activeClass = activeClass,
                            onClear = { batchScannedList = emptyList() },
                            onAddAll = {
                                viewModel.addBatchStudentsFromQr(batchScannedList) { added, _ ->
                                    if (added > 0) {
                                        viewModel.speechManager.speak("Added $added students to ${activeClass?.name}")
                                    }
                                    batchScannedList = emptyList()
                                    parseResult = null
                                    lastScannedRawText = null
                                }
                            },
                            onDismiss = {
                                parseResult = null
                                lastScannedRawText = null
                            }
                        )
                    } else {
                        // Single / Multi-Student Parsed Result Sheet
                        parseResult?.let { result ->
                            ParsedResultSheet(
                                result = result,
                                activeClass = activeClass,
                                selectedNames = selectedStudentsInBatch,
                                onToggleStudent = { name ->
                                    selectedStudentsInBatch = if (selectedStudentsInBatch.contains(name)) {
                                        selectedStudentsInBatch - name
                                    } else {
                                        selectedStudentsInBatch + name
                                    }
                                },
                                onSelectAll = {
                                    selectedStudentsInBatch = result.students.map { it.name }.toSet()
                                },
                                onAddSelected = {
                                    val toAdd = result.students.filter { selectedStudentsInBatch.contains(it.name) }
                                    viewModel.addBatchStudentsFromQr(toAdd) { added, _ ->
                                        if (added > 0) {
                                            viewModel.speechManager.speak("Added $added students to ${activeClass?.name}")
                                        }
                                        parseResult = null
                                        lastScannedRawText = null
                                    }
                                },
                                onAddSingle = { student ->
                                    viewModel.addScannedStudent(
                                        name = student.name,
                                        lineId = student.lineId
                                    ) {
                                        viewModel.speechManager.speak("${student.name} added to roster")
                                        parseResult = null
                                        lastScannedRawText = null
                                    }
                                },
                                onScanNext = {
                                    parseResult = null
                                    lastScannedRawText = null
                                }
                            )
                        }
                    }
                }

                // Scanning instruction when no result is displayed
                if (parseResult == null && batchScannedList.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💡", fontSize = 14.sp)
                            Text(
                                text = "Point at printed class list, student ID cards, or test QR codes",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Sample Class List QR Cards Simulator Modal (Bottom Sheet / Overlay)
            if (showSampleListDrawer) {
                SampleQrListPickerSheet(
                    onSelectCard = { card ->
                        handleScannedText(card.rawText)
                        showSampleListDrawer = false
                    },
                    onDismiss = { showSampleListDrawer = false }
                )
            }
        }
    }
}

@Composable
private fun CornerBracketsOverlay(color: Color) {
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Top Left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(24.dp)
                .border(3.dp, color, RoundedCornerShape(topStart = 8.dp))
        )
        // Top Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .border(3.dp, color, RoundedCornerShape(topEnd = 8.dp))
        )
        // Bottom Left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(24.dp)
                .border(3.dp, color, RoundedCornerShape(bottomStart = 8.dp))
        )
        // Bottom Right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .border(3.dp, color, RoundedCornerShape(bottomEnd = 8.dp))
        )
    }
}

@Composable
private fun ParsedResultSheet(
    result: QrParseResult,
    activeClass: ClassRoomEntity?,
    selectedNames: Set<String>,
    onToggleStudent: (String) -> Unit,
    onSelectAll: () -> Unit,
    onAddSelected: () -> Unit,
    onAddSingle: (ParsedStudentInfo) -> Unit,
    onScanNext: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .testTag("qr_parsed_result_sheet"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(EmeraldGreen, RoyalBlue)))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header with format badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✅", fontSize = 14.sp)
                    }
                    Column {
                        Text(
                            text = if (result.students.size > 1) "Class Roster Sheet Scanned" else "Student QR Code Scanned",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NavyPrimary
                        )
                        val badgeText = when (result.formatType) {
                            QrFormatType.MULTI_STUDENT_LIST -> "Multi-Student Paper List (${result.students.size} detected)"
                            QrFormatType.CSV_ROSTER -> "CSV Roster Sheet (${result.students.size} students)"
                            QrFormatType.JSON_FORMAT -> "Structured JSON Badge"
                            QrFormatType.VCARD -> "vCard Digital Badge"
                            QrFormatType.CLASS_JOIN -> "Class Join Token"
                            else -> "Single Student Code"
                        }
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = RoyalBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = onScanNext,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Scan Next", tint = NavySecondary)
                }
            }

            if (result.students.size == 1) {
                // Single student parsed display
                val student = result.students.first()
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = NavyPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (student.studentNumber != null) {
                                    Text(
                                        text = "No. ${student.studentNumber}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen
                                    )
                                }
                                if (student.studentId != null) {
                                    Text(
                                        text = "ID: ${student.studentId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NavySecondary
                                    )
                                }
                                if (student.lineId != null) {
                                    Text(
                                        text = "LINE: ${student.lineId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LineGreen
                                    )
                                }
                            }
                        }

                        if (student.isExistingInClass) {
                            StatusBadge(text = "Already Enrolled", color = LineGreen, bgColor = LineGreenLight)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onScanNext,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Scan Next", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onAddSingle(student) },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("qr_confirm_add_single_btn"),
                        enabled = !student.isExistingInClass,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (student.isExistingInClass) "Already in ${activeClass?.name}" else "Add to ${activeClass?.name ?: "Class"}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Multi-student roster list display
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderLine),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(result.students) { student ->
                            val isSelected = selectedNames.contains(student.name)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleStudent(student.name) }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { onToggleStudent(student.name) },
                                    enabled = !student.isExistingInClass,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (student.isExistingInClass) NavySecondary else NavyPrimary
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (student.studentNumber != null) {
                                            Text("No. ${student.studentNumber}", fontSize = 11.sp, color = EmeraldGreen)
                                        }
                                        if (student.studentId != null) {
                                            Text("ID: ${student.studentId}", fontSize = 11.sp, color = NavySecondary)
                                        }
                                    }
                                }
                                if (student.isExistingInClass) {
                                    Text("Enrolled", fontSize = 10.sp, color = LineGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onSelectAll,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("Select All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onScanNext,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Rescan", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onAddSelected,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("qr_confirm_add_batch_btn"),
                        enabled = selectedNames.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add (${selectedNames.size}) Students",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchScannedSummarySheet(
    scannedStudents: List<ParsedStudentInfo>,
    activeClass: ClassRoomEntity?,
    onClear: () -> Unit,
    onAddAll: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .testTag("qr_batch_summary_sheet"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(EmeraldGreen, ThaiGold)))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${scannedStudents.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text(
                        text = "Continuous Batch Scanner",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NavyPrimary
                    )
                }

                TextButton(onClick = onClear) {
                    Text("Clear All", color = CoralRed, fontSize = 12.sp)
                }
            }

            Text(
                text = "Keep pointing camera at physical QR codes on student desks / badges. Students will be collected automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = NavySecondary
            )

            // Scanned chips
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 120.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(scannedStudents) { student ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${student.name}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NavyPrimary
                            )
                            if (student.studentNumber != null) {
                                Text("No. ${student.studentNumber}", fontSize = 11.sp, color = EmeraldGreen)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Keep Scanning")
                }

                Button(
                    onClick = onAddAll,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("qr_batch_save_all_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add All (${scannedStudents.size}) to ${activeClass?.name ?: "Class"}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SampleQrListPickerSheet(
    onSelectCard: (SampleQrCard) -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("sample_qr_picker_sheet"),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceCard,
        border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(RoyalBlue, EmeraldGreen))),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📋", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "Sample Physical Class List QRs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NavyPrimary
                        )
                        Text(
                            text = "Test QR code scanning with realistic Thai class rosters",
                            style = MaterialTheme.typography.bodySmall,
                            color = NavySecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = NavySecondary)
                }
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QrStudentParser.SAMPLE_PHYSICAL_LIST_QRS) { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCard(card) }
                            .testTag("sample_qr_card_${card.grade}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLine)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QrCodeView(
                                data = card.rawText,
                                size = 52.dp
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    StatusBadge(text = card.grade, color = RoyalBlue, bgColor = RoyalBlueLight)
                                    Text(
                                        text = card.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NavyPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = card.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NavySecondary,
                                    maxLines = 2
                                )
                            }

                            Button(
                                onClick = { onSelectCard(card) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
