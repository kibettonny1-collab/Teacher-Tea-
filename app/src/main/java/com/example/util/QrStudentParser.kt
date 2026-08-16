package com.example.util

import com.example.data.model.StudentEntity
import org.json.JSONArray
import org.json.JSONObject

enum class QrFormatType {
    SINGLE_STUDENT,
    MULTI_STUDENT_LIST,
    JSON_FORMAT,
    CSV_ROSTER,
    VCARD,
    CLASS_JOIN,
    RAW_TEXT
}

data class ParsedStudentInfo(
    val rawText: String,
    val name: String,
    val studentNumber: String? = null,
    val studentId: String? = null,
    val lineId: String? = null,
    val isExistingInClass: Boolean = false,
    val isSelectedForAddition: Boolean = true
)

data class QrParseResult(
    val rawContent: String,
    val students: List<ParsedStudentInfo>,
    val formatType: QrFormatType,
    val detectedClassName: String? = null
)

object QrStudentParser {

    /**
     * Parses scanned QR code raw text into structured student entries.
     * Checks against existing students in the active class to flag duplicates.
     */
    fun parse(rawText: String, existingStudents: List<StudentEntity> = emptyList()): QrParseResult {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) {
            return QrParseResult(rawText, emptyList(), QrFormatType.RAW_TEXT)
        }

        val existingNames = existingStudents.map { it.name.trim().lowercase() }.toSet()

        // 1. Try JSON parsing
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            val jsonResult = tryParseJson(trimmed, existingNames)
            if (jsonResult != null) return jsonResult
        }

        // 2. Try vCard parsing
        if (trimmed.contains("BEGIN:VCARD", ignoreCase = true)) {
            val vCardResult = tryParseVCard(trimmed, existingNames)
            if (vCardResult != null) return vCardResult
        }

        // 3. Try Multi-line or CSV Roster parsing
        val lines = trimmed.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.size > 1) {
            val multiResult = parseMultipleLines(lines, existingNames, rawText)
            if (multiResult.students.isNotEmpty()) {
                return multiResult
            }
        }

        // 4. Try Key-Value Tagged String (e.g., "NAME:Somchai, NO:1, ID:501")
        if (trimmed.contains("NAME:", ignoreCase = true) || 
            trimmed.contains("STUDENT:", ignoreCase = true) ||
            trimmed.contains("CLASS:", ignoreCase = true)
        ) {
            val kvResult = tryParseKeyValueString(trimmed, existingNames)
            if (kvResult != null) return kvResult
        }

        // 5. Single Name / Line Fallback
        val (cleanName, num, id) = extractNameAndNumber(trimmed)
        val isExist = existingNames.contains(cleanName.lowercase())
        val singleStudent = ParsedStudentInfo(
            rawText = trimmed,
            name = cleanName,
            studentNumber = num,
            studentId = id,
            isExistingInClass = isExist,
            isSelectedForAddition = !isExist
        )

        return QrParseResult(
            rawContent = rawText,
            students = listOf(singleStudent),
            formatType = QrFormatType.SINGLE_STUDENT
        )
    }

    private fun tryParseJson(jsonStr: String, existingNames: Set<String>): QrParseResult? {
        return try {
            if (jsonStr.startsWith("[")) {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<ParsedStudentInfo>()
                for (i in 0 until array.length()) {
                    val item = array.get(i)
                    if (item is JSONObject) {
                        val name = item.optString("name", "").ifEmpty { item.optString("studentName", "") }
                        if (name.isNotBlank()) {
                            val num = item.optString("no", "").ifEmpty { item.optString("studentNo", null) }
                            val id = item.optString("id", "").ifEmpty { item.optString("studentId", null) }
                            val line = item.optString("lineId", "").ifEmpty { item.optString("line", null) }
                            val isExist = existingNames.contains(name.trim().lowercase())
                            list.add(
                                ParsedStudentInfo(
                                    rawText = item.toString(),
                                    name = name.trim(),
                                    studentNumber = num,
                                    studentId = id,
                                    lineId = line,
                                    isExistingInClass = isExist,
                                    isSelectedForAddition = !isExist
                                )
                            )
                        }
                    } else if (item is String && item.isNotBlank()) {
                        val (cleanName, num, id) = extractNameAndNumber(item)
                        val isExist = existingNames.contains(cleanName.lowercase())
                        list.add(
                            ParsedStudentInfo(
                                rawText = item,
                                name = cleanName,
                                studentNumber = num,
                                studentId = id,
                                isExistingInClass = isExist,
                                isSelectedForAddition = !isExist
                            )
                        )
                    }
                }
                if (list.isNotEmpty()) {
                    QrParseResult(jsonStr, list, QrFormatType.JSON_FORMAT)
                } else null
            } else {
                val obj = JSONObject(jsonStr)
                // Check if it has a students array inside
                if (obj.has("students") || obj.has("roster") || obj.has("list")) {
                    val arr = obj.optJSONArray("students") ?: obj.optJSONArray("roster") ?: obj.optJSONArray("list")
                    if (arr != null) {
                        val className = obj.optString("class", "").ifEmpty { obj.optString("className", null) }
                        val list = mutableListOf<ParsedStudentInfo>()
                        for (i in 0 until arr.length()) {
                            val item = arr.get(i)
                            if (item is JSONObject) {
                                val name = item.optString("name", "").ifEmpty { item.optString("studentName", "") }
                                if (name.isNotBlank()) {
                                    val num = item.optString("no", "").ifEmpty { item.optString("studentNo", null) }
                                    val id = item.optString("id", "").ifEmpty { item.optString("studentId", null) }
                                    val line = item.optString("lineId", "").ifEmpty { item.optString("line", null) }
                                    val isExist = existingNames.contains(name.trim().lowercase())
                                    list.add(
                                        ParsedStudentInfo(
                                            rawText = item.toString(),
                                            name = name.trim(),
                                            studentNumber = num,
                                            studentId = id,
                                            lineId = line,
                                            isExistingInClass = isExist,
                                            isSelectedForAddition = !isExist
                                        )
                                    )
                                }
                            } else if (item is String && item.isNotBlank()) {
                                val (cleanName, num, id) = extractNameAndNumber(item)
                                val isExist = existingNames.contains(cleanName.lowercase())
                                list.add(
                                    ParsedStudentInfo(
                                        rawText = item,
                                        name = cleanName,
                                        studentNumber = num,
                                        studentId = id,
                                        isExistingInClass = isExist,
                                        isSelectedForAddition = !isExist
                                    )
                                )
                            }
                        }
                        return QrParseResult(jsonStr, list, QrFormatType.JSON_FORMAT, detectedClassName = className)
                    }
                }

                // Single student JSON object
                val name = obj.optString("name", "").ifEmpty { obj.optString("studentName", "") }
                if (name.isNotBlank()) {
                    val num = obj.optString("no", "").ifEmpty { obj.optString("studentNo", null) }
                    val id = obj.optString("id", "").ifEmpty { obj.optString("studentId", null) }
                    val line = obj.optString("lineId", "").ifEmpty { obj.optString("line", null) }
                    val className = obj.optString("class", "").ifEmpty { obj.optString("className", null) }
                    val isExist = existingNames.contains(name.trim().lowercase())
                    val student = ParsedStudentInfo(
                        rawText = jsonStr,
                        name = name.trim(),
                        studentNumber = num,
                        studentId = id,
                        lineId = line,
                        isExistingInClass = isExist,
                        isSelectedForAddition = !isExist
                    )
                    QrParseResult(jsonStr, listOf(student), QrFormatType.JSON_FORMAT, detectedClassName = className)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun tryParseVCard(vcardStr: String, existingNames: Set<String>): QrParseResult? {
        return try {
            var name = ""
            var id: String? = null
            var note: String? = null

            for (line in vcardStr.lines()) {
                val trimmed = line.trim()
                if (trimmed.startsWith("FN:", ignoreCase = true)) {
                    name = trimmed.substring(3).trim()
                } else if (name.isEmpty() && trimmed.startsWith("N:", ignoreCase = true)) {
                    val parts = trimmed.substring(2).split(";").filter { it.isNotBlank() }
                    name = parts.reversed().joinToString(" ").trim()
                } else if (trimmed.startsWith("NOTE:", ignoreCase = true)) {
                    note = trimmed.substring(5).trim()
                } else if (trimmed.startsWith("TITLE:", ignoreCase = true) || trimmed.startsWith("ORG:", ignoreCase = true)) {
                    id = trimmed.substringAfter(":").trim()
                }
            }

            if (name.isNotBlank()) {
                val isExist = existingNames.contains(name.lowercase())
                val student = ParsedStudentInfo(
                    rawText = vcardStr,
                    name = name,
                    studentId = id ?: note,
                    isExistingInClass = isExist,
                    isSelectedForAddition = !isExist
                )
                QrParseResult(vcardStr, listOf(student), QrFormatType.VCARD)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private fun tryParseKeyValueString(kvStr: String, existingNames: Set<String>): QrParseResult? {
        return try {
            val delimiter = if (kvStr.contains(";")) ";" else if (kvStr.contains(",")) "," else "\n"
            val tokens = kvStr.split(delimiter).map { it.trim() }

            var name = ""
            var number: String? = null
            var id: String? = null
            var lineId: String? = null
            var className: String? = null

            for (token in tokens) {
                val lower = token.lowercase()
                val value = token.substringAfter(":").trim()
                when {
                    lower.startsWith("name:") || lower.startsWith("student:") || lower.startsWith("std:") -> name = value
                    lower.startsWith("no:") || lower.startsWith("number:") || lower.startsWith("num:") -> number = value
                    lower.startsWith("id:") || lower.startsWith("studentid:") || lower.startsWith("code:") -> id = value
                    lower.startsWith("line:") || lower.startsWith("lineid:") -> lineId = value
                    lower.startsWith("class:") || lower.startsWith("room:") || lower.startsWith("grade:") -> className = value
                }
            }

            if (name.isNotBlank()) {
                val isExist = existingNames.contains(name.lowercase())
                val student = ParsedStudentInfo(
                    rawText = kvStr,
                    name = name,
                    studentNumber = number,
                    studentId = id,
                    lineId = lineId,
                    isExistingInClass = isExist,
                    isSelectedForAddition = !isExist
                )
                QrParseResult(kvStr, listOf(student), QrFormatType.SINGLE_STUDENT, detectedClassName = className)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private fun parseMultipleLines(lines: List<String>, existingNames: Set<String>, rawText: String): QrParseResult {
        val studentList = mutableListOf<ParsedStudentInfo>()
        var isCsv = false
        var classNameDetected: String? = null

        // Check if first line is a CSV header (e.g. "No,Name,ID" or "ลำดับ,ชื่อ-สกุล,รหัส")
        var startIndex = 0
        val firstLine = lines.first().lowercase()
        if (firstLine.contains("name") || firstLine.contains("student") || firstLine.contains("ชื่อ") || firstLine.contains("รหัส")) {
            isCsv = true
            startIndex = 1
        }

        for (i in startIndex until lines.size) {
            val line = lines[i].trim()
            if (line.isBlank() || line.startsWith("#") || line.startsWith("//")) continue

            if (line.lowercase().startsWith("class:") || line.lowercase().startsWith("room:")) {
                classNameDetected = line.substringAfter(":").trim()
                continue
            }

            if (isCsv || line.contains(",") || line.contains("\t")) {
                val delimiter = if (line.contains("\t")) "\t" else ","
                val columns = line.split(delimiter).map { it.trim().removeSurrounding("\"") }
                if (columns.isNotEmpty()) {
                    var parsedName = ""
                    var parsedNum: String? = null
                    var parsedId: String? = null

                    if (columns.size == 1) {
                        parsedName = columns[0]
                    } else if (columns[0].toIntOrNull() != null) {
                        parsedNum = columns[0]
                        parsedName = columns.getOrNull(1) ?: ""
                        parsedId = columns.getOrNull(2)
                    } else {
                        parsedName = columns[0]
                        parsedId = columns.getOrNull(1)
                    }

                    val (cleanedName, extraNum, _) = extractNameAndNumber(parsedName)
                    if (cleanedName.isNotBlank()) {
                        val isExist = existingNames.contains(cleanedName.lowercase())
                        studentList.add(
                            ParsedStudentInfo(
                                rawText = line,
                                name = cleanedName,
                                studentNumber = parsedNum ?: extraNum,
                                studentId = parsedId,
                                isExistingInClass = isExist,
                                isSelectedForAddition = !isExist
                            )
                        )
                    }
                }
            } else {
                // Line format (e.g., "1. Somchai Prasert" or "Kittisak Chaiwong")
                val (cleanedName, num, id) = extractNameAndNumber(line)
                if (cleanedName.isNotBlank()) {
                    val isExist = existingNames.contains(cleanedName.lowercase())
                    studentList.add(
                        ParsedStudentInfo(
                            rawText = line,
                            name = cleanedName,
                            studentNumber = num,
                            studentId = id,
                            isExistingInClass = isExist,
                            isSelectedForAddition = !isExist
                        )
                    )
                }
            }
        }

        val format = if (isCsv) QrFormatType.CSV_ROSTER else QrFormatType.MULTI_STUDENT_LIST
        return QrParseResult(rawText, studentList, format, detectedClassName = classNameDetected)
    }

    /**
     * Extracts cleaned student name, removing prefix numbers (e.g., "1. ", "02 - ", "#3 "),
     * parenthesized student IDs, or grade labels.
     */
    fun extractNameAndNumber(input: String): Triple<String, String?, String?> {
        var text = input.trim()
        var extractedNo: String? = null
        var extractedId: String? = null

        // 1. Check for prefix number pattern: "1. Name", "01) Name", "1 - Name", "#01 Name"
        val prefixRegex = Regex("""^(?:#|No\.?|ลำดับที่\s*)?(\d{1,3})[\.\-\)\s:]+\s*(.+)""", RegexOption.IGNORE_CASE)
        val match = prefixRegex.find(text)
        if (match != null) {
            extractedNo = match.groupValues[1]
            text = match.groupValues[2].trim()
        }

        // 2. Check for trailing ID or room in brackets/parentheses: "Somchai (ST-001)" or "Nattaporn [40123]"
        val trailingBracketRegex = Regex("""(.+?)\s*[\(\[\{]([A-Za-z0-9\-\_]+)[\)\]\}]$""")
        val bracketMatch = trailingBracketRegex.find(text)
        if (bracketMatch != null) {
            text = bracketMatch.groupValues[1].trim()
            val bracketContent = bracketMatch.groupValues[2].trim()
            if (bracketContent.toIntOrNull() != null && extractedNo == null) {
                extractedNo = bracketContent
            } else {
                extractedId = bracketContent
            }
        }

        // 3. Remove common Thai prefixes if needed while preserving full name
        // (Keep polite titles like นาย / น.ส. / ด.ช. / ด.ญ. or Mr. / Ms. as part of the student's name if present)
        return Triple(text, extractedNo, extractedId)
    }

    /**
     * Sample physical class lists formatted as QR text for testing and classroom simulations.
     */
    val SAMPLE_PHYSICAL_LIST_QRS = listOf(
        SampleQrCard(
            title = "M.1/3 Official Roster (Multi-Student List)",
            description = "Numbered printed paper class list from Bangkok Secondary School",
            grade = "M.1",
            rawText = """
                Class: M.1/3
                1. Somchai Prasert
                2. Nattaporn Srisuk
                3. Kittisak Chaiwong
                4. Siriporn Thongdee
                5. Ananda Sukjai
                6. Pimchanok Luevisadpaibul
                7. Thanapob Leeratanakachorn
                8. Chanya McClory
            """.trimIndent()
        ),
        SampleQrCard(
            title = "M.4/1 Science Class CSV Sheet (With IDs)",
            description = "Exported MIS spreadsheet format with Student ID codes",
            grade = "M.4",
            rawText = """
                No,Name,ID
                1,Kanya Rattanamongkol,ST-40101
                2,Teerapat Wongsuwan,ST-40102
                3,Wipawan Charoenrat,ST-40103
                4,Chanon Santinatornkul,ST-40104
                5,Saran Sirilak,ST-40105
            """.trimIndent()
        ),
        SampleQrCard(
            title = "Student ID Badge Card (JSON Format)",
            description = "Digital / Physical QR badge with LINE link",
            grade = "M.1",
            rawText = """
                {"name":"Nichaphat Chatchaipholrat","no":"14","id":"ST-10314","class":"M.1/3","lineId":"U98761234"}
            """.trimIndent()
        ),
        SampleQrCard(
            title = "English Club Registration Card (Key-Value)",
            description = "School activity registration QR sticker",
            grade = "M.5",
            rawText = """
                NAME: Pachara Chirathivat; NO: 09; ID: ENG-509; CLASS: M.5/2; LINE: U55443322
            """.trimIndent()
        ),
        SampleQrCard(
            title = "Exchange Student vCard Badge",
            description = "Contact vCard encoded student badge",
            grade = "M.6",
            rawText = """
                BEGIN:VCARD
                VERSION:3.0
                FN:Natthanicha Dangwattanawanich
                NOTE:ST-60128
                ORG:M.6/1 International English
                END:VCARD
            """.trimIndent()
        )
    )
}

data class SampleQrCard(
    val title: String,
    val description: String,
    val grade: String,
    val rawText: String
)
