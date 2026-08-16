package com.example

import com.example.data.model.StudentEntity
import com.example.util.QrFormatType
import com.example.util.QrStudentParser
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testParseSingleStudent() {
        val raw = "1. Somchai Prasert"
        val result = QrStudentParser.parse(raw)
        assertEquals(QrFormatType.SINGLE_STUDENT, result.formatType)
        assertEquals(1, result.students.size)
        assertEquals("Somchai Prasert", result.students[0].name)
        assertEquals("1", result.students[0].studentNumber)
        assertFalse(result.students[0].isExistingInClass)
    }

    @Test
    fun testParseDuplicateStudentDetection() {
        val existing = listOf(
            StudentEntity(id = "1", classId = "c1", name = "Somchai Prasert", isSubmitted = false)
        )
        val raw = "Somchai Prasert"
        val result = QrStudentParser.parse(raw, existingStudents = existing)
        assertEquals(1, result.students.size)
        assertTrue(result.students[0].isExistingInClass)
        assertFalse(result.students[0].isSelectedForAddition)
    }

    @Test
    fun testParseMultiLineStudentList() {
        val multiLine = """
            1. Ananda Everingham
            2. Pimchanok Luevisadpaibul
            3. Mario Maurer
        """.trimIndent()

        val result = QrStudentParser.parse(multiLine)
        assertEquals(QrFormatType.MULTI_STUDENT_LIST, result.formatType)
        assertEquals(3, result.students.size)
        assertEquals("Ananda Everingham", result.students[0].name)
        assertEquals("1", result.students[0].studentNumber)
        assertEquals("Pimchanok Luevisadpaibul", result.students[1].name)
        assertEquals("2", result.students[1].studentNumber)
        assertEquals("Mario Maurer", result.students[2].name)
        assertEquals("3", result.students[2].studentNumber)
    }

    @Test
    fun testParseCsvRoster() {
        val csv = """
            No,Name,StudentID,LINE
            1,Nattawut Somboon,50101,nattawut_line
            2,Kanya Rattana,50102,kanya_r
        """.trimIndent()

        val result = QrStudentParser.parse(csv)
        assertEquals(QrFormatType.CSV_ROSTER, result.formatType)
        assertEquals(2, result.students.size)
        assertEquals("Nattawut Somboon", result.students[0].name)
        assertEquals("1", result.students[0].studentNumber)
        assertEquals("50101", result.students[0].studentId)
        assertEquals("nattawut_line", result.students[0].lineId)
    }

    @Test
    fun testParseJsonArray() {
        val json = """
            [
                {"name": "Chantavit Dhanasevi", "no": "1", "id": "1001"},
                {"name": "Davika Hoorne", "no": "2", "id": "1002", "line": "davikah"}
            ]
        """.trimIndent()

        val result = QrStudentParser.parse(json)
        assertEquals(QrFormatType.JSON_FORMAT, result.formatType)
        assertEquals(2, result.students.size)
        assertEquals("Chantavit Dhanasevi", result.students[0].name)
        assertEquals("1", result.students[0].studentNumber)
        assertEquals("1001", result.students[0].studentId)
        assertEquals("Davika Hoorne", result.students[1].name)
        assertEquals("davikah", result.students[1].lineId)
    }

    @Test
    fun testParseVCard() {
        val vCard = """
            BEGIN:VCARD
            VERSION:3.0
            FN:Supa Srichan
            NOTE:StudentNo:07; ID:40231
            END:VCARD
        """.trimIndent()

        val result = QrStudentParser.parse(vCard)
        assertEquals(QrFormatType.VCARD, result.formatType)
        assertEquals(1, result.students.size)
        assertEquals("Supa Srichan", result.students[0].name)
    }
}

