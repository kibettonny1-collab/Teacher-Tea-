package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClassCompanionViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class CuratedTopic(
    val title: String,
    val titleTh: String,
    val grade: String,
    val questionsPreview: String,
    val promptContent: String
)

val CURATED_ORAL_TOPICS = listOf(
    CuratedTopic(
        title = "Self Introduction & Family Life",
        titleTh = "การแนะนำตัวและชีวิตครอบครัว",
        grade = "M.1",
        questionsPreview = "Name, age, siblings, parents' jobs, home location",
        promptContent = "Self introduction, family members, asking names and ages for M.1"
    ),
    CuratedTopic(
        title = "Daily Routine & School Schedule",
        titleTh = "กิจวัตรประจำวันและตารางเรียน",
        grade = "M.1",
        questionsPreview = "Wake up time, breakfast, school subjects, timetable",
        promptContent = "Daily routines, waking up, school subjects, times of day for M.1"
    ),
    CuratedTopic(
        title = "Food, Drinks & Ordering at Market",
        titleTh = "อาหาร เครื่องดื่ม และการสั่งของที่ตลาด",
        grade = "M.1",
        questionsPreview = "Favorite Thai food, ordering Pad Thai, drinks, prices",
        promptContent = "Ordering food at market, favorite dishes, prices and drinks for M.1"
    ),
    CuratedTopic(
        title = "Hobbies, Sports & Free Time",
        titleTh = "งานอดิเรก กีฬา และเวลาว่าง",
        grade = "M.1",
        questionsPreview = "Playing football, drawing, weekend activities, reading",
        promptContent = "Hobbies and free time activities, sports in Thailand for M.1"
    ),
    CuratedTopic(
        title = "Past Weekend & Travel Memories",
        titleTh = "วันหยุดสุดสัปดาห์และความทรงจำการท่องเที่ยว",
        grade = "M.4",
        questionsPreview = "Past simple tense, trips to Pattaya/Chiang Mai, feelings",
        promptContent = "Past weekend trip, using irregular verbs (went, saw, ate) for M.4"
    ),
    CuratedTopic(
        title = "Directions & City Navigation",
        titleTh = "การบอกทิศทางและการเดินทางในเมือง",
        grade = "M.4",
        questionsPreview = "Turn left, straight ahead, BTS train, bus station",
        promptContent = "Giving and asking for directions in a Thai city for M.4"
    ),
    CuratedTopic(
        title = "Traditional Thai Festivals & Culture",
        titleTh = "ประเพณีและวัฒนธรรมไทย (สงกรานต์ ลอยกระทง)",
        grade = "M.4",
        questionsPreview = "Songkran water festival, Loy Krathong, cultural etiquette",
        promptContent = "Thai festivals: Songkran, Loy Krathong, traditional customs for M.4"
    ),
    CuratedTopic(
        title = "Health, Illness & Doctor Visits",
        titleTh = "สุขภาพ อาการเจ็บป่วย และการพบแพทย์",
        grade = "M.4",
        questionsPreview = "Headache, fever, medicine, healthy lifestyle habits",
        promptContent = "Talking about health, symptoms, doctor advice for M.4"
    ),
    CuratedTopic(
        title = "Technology, AI & Social Media",
        titleTh = "เทคโนโลยี ปัญญาประดิษฐ์ และโซเชียลมีเดีย",
        grade = "M.5",
        questionsPreview = "Smartphones, AI tools, online learning, advantages",
        promptContent = "Technology, smartphones, AI in education, social media impact for M.5"
    ),
    CuratedTopic(
        title = "Environmental Protection & Climate",
        titleTh = "การอนุรักษ์สิ่งแวดล้อมและสภาพภูมิอากาศ",
        grade = "M.5",
        questionsPreview = "Plastic waste reduction, recycling, pollution, clean energy",
        promptContent = "Environmental protection, recycling, global warming in Thailand for M.5"
    ),
    CuratedTopic(
        title = "Future Career Goals & University",
        titleTh = "เป้าหมายอาชีพในอนาคตและการเรียนต่อมหาวิทยาลัย",
        grade = "M.6",
        questionsPreview = "Dream job, university majors, future plans, skills",
        promptContent = "Future ambitions, university faculties, career interviews for M.6"
    ),
    CuratedTopic(
        title = "Global Tourism & Hospitality",
        titleTh = "การท่องเที่ยวระดับโลกและงานบริการ",
        grade = "M.6",
        questionsPreview = "Welcoming international tourists, cultural exchange",
        promptContent = "International tourism, hotel service, cultural exchange for M.6"
    )
)

@Composable
fun SpeakTestTopicsScreen(
    viewModel: ClassCompanionViewModel,
    modifier: Modifier = Modifier
) {
    val activeClass by viewModel.activeClass.collectAsState()
    var selectedGradeFilter by remember { mutableStateOf<String?>("All") }

    val filteredTopics = if (selectedGradeFilter == "All" || selectedGradeFilter == null) {
        CURATED_ORAL_TOPICS
    } else {
        CURATED_ORAL_TOPICS.filter { it.grade == selectedGradeFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("speaktest_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLine, RoyalBlueLight)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SPEAKING & LISTENING CURRICULUM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = RoyalBlue
                            )
                            Text(
                                text = "Oral Exam Topics · หัวข้อสอบพูด",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                        }
                        StatusBadge(text = "STT / TTS Enabled", color = EmeraldGreen, bgColor = EmeraldGreenLight)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Select any curated Mattayom topic to immediately launch an oral speaking test with question audio (TTS) and live voice capture.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grade Filter Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
            }
        }

        // Section Header
        item {
            SectionHeader(title = "Curated Oral Exam Topics (${filteredTopics.size})")
        }

        // Topic List Items
        items(filteredTopics) { topic ->
            TopicRowCard(
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

@Composable
private fun TopicRowCard(
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
                    text = "Key elements: ${topic.questionsPreview}",
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
