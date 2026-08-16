package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.AIActivityGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Class Companion", appName)
  }

  @Test
  fun `test MatchUp fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateMatchUp("Daily Routine", "M.1")
    assertNotNull(result)
    assertTrue(result.pairs.isNotEmpty())
    assertTrue(result.pairs.any { it.term.isNotBlank() && it.definition.isNotBlank() })
  }

  @Test
  fun `test QuizRace fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateQuizRace("Past Tense", "M.4")
    assertNotNull(result)
    assertTrue(result.items.isNotEmpty())
    val first = result.items[0]
    assertTrue(first.options.size >= 2)
    assertTrue(first.correctIndex in 0 until first.options.size)
  }

  @Test
  fun `test Anagram fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateAnagram("Songkran Festival", "M.1")
    assertNotNull(result)
    assertTrue(result.items.isNotEmpty())
    result.items.forEach { item ->
      assertEquals(item.word.length, item.scrambled.size)
      assertTrue(item.scrambled.all { it.isLetter() })
    }
  }

  @Test
  fun `test TrueFalse fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateTrueFalse("Food & Drinks", "M.1")
    assertNotNull(result)
    assertTrue(result.items.isNotEmpty())
    assertTrue(result.items.any { it.isTrue } && result.items.any { !it.isTrue })
  }

  @Test
  fun `test OpenTheBox fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateOpenTheBox("Classroom English", "M.1")
    assertNotNull(result)
    assertEquals(6, result.boxes.size)
    assertTrue(result.boxes.all { it.points > 0 && it.questionEn.isNotBlank() && it.answer.isNotBlank() })
  }

  @Test
  fun `test Worksheet fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateWorksheet("Daily Routines", "M.1")
    assertNotNull(result)
    assertTrue(result.isNotEmpty())
    assertTrue(result.all { it.answer.isNotBlank() })
  }

  @Test
  fun `test Story fallback generator`() = runBlocking {
    val result = AIActivityGenerator.generateStory("Elephant Sanctuary", "M.1")
    assertNotNull(result)
    assertTrue(result.storyText.isNotBlank())
    assertTrue(result.questions.isNotEmpty())
  }

  @Test
  fun `test WorksheetCatalog retrieval and search`() {
    val all = com.example.data.worksheet.WorksheetCatalog.allWorksheets
    assertTrue(all.size >= 8)
    
    val speedQuiz = com.example.data.worksheet.WorksheetCatalog.getWorksheetById("qz_past_simple_speed")
    assertNotNull(speedQuiz)
    assertEquals("qz_past_simple_speed", speedQuiz?.id)
    assertTrue(speedQuiz!!.questions.isNotEmpty())

    val searchResults = com.example.data.worksheet.WorksheetCatalog.searchWorksheets("Elephant")
    assertTrue(searchResults.isNotEmpty())
    assertTrue(searchResults.any { it.id == "rd_chiang_mai_elephant_rescue" })
  }

  @Test
  fun `test LineShareHelper message formatting for homework`() {
    val message = com.example.util.LineShareHelper.formatHomeworkMessage(
      className = "M.1/3",
      grade = "M.1",
      lessonTopic = "Present Continuous Challenge",
      homeworkContent = "Write 5 sentences describing what your family members are doing.",
      dueDate = "Friday 17:00",
      joinCode = "M13TH"
    )
    assertTrue(message.contains("Class Companion - การบ้านภาษาอังกฤษ"))
    assertTrue(message.contains("M.1/3 (M.1)"))
    assertTrue(message.contains("Present Continuous Challenge"))
    assertTrue(message.contains("Friday 17:00"))
    assertTrue(message.contains("M13TH"))
  }

  @Test
  fun `test LineShareHelper message formatting for student reminder`() {
    val message = com.example.util.LineShareHelper.formatStudentReminderMessage(
      studentName = "Somchai",
      className = "M.1/3",
      homeworkTopic = "Unit 3 Vocab Worksheet",
      dueDate = "Tonight 20:00"
    )
    assertTrue(message.contains("Somchai"))
    assertTrue(message.contains("M.1/3"))
    assertTrue(message.contains("Unit 3 Vocab Worksheet"))
    assertTrue(message.contains("Tonight 20:00"))
  }

  @Test
  fun `test LineShareHelper URL scheme encoding`() {
    val url = com.example.util.LineShareHelper.getLineUrlScheme("Hello World")
    assertTrue(url.startsWith("https://line.me/R/msg/text/?"))
    assertTrue(url.contains("Hello+World") || url.contains("Hello%20World"))
  }
}
