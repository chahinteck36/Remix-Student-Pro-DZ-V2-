package com.example.data.local

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object DocumentImportHelper {

    private const val TAG = "DocumentImportHelper"

    /**
     * Copy input stream from picked Uri to internal storage and extract text preview
     */
    fun importDocumentFromUri(
        context: Context,
        uri: Uri,
        chosenCategory: String = "محاضرات ودروس",
        userNotes: String = ""
    ): ImportedDocumentItem {
        val contentResolver = context.contentResolver
        var fileName = "document_${System.currentTimeMillis()}"
        var fileSize = 0L

        // Query metadata
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                    if (sizeIndex != -1) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not read Uri columns: ${e.message}")
        }

        val extension = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        val mimeType = contentResolver.getType(uri) ?: when (extension) {
            "pdf" -> "application/pdf"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "doc" -> "application/msword"
            "xml" -> "text/xml"
            "txt" -> "text/plain"
            else -> "application/octet-stream"
        }

        val fileType = when {
            extension == "pdf" || mimeType.contains("pdf") -> "PDF"
            extension in listOf("docx", "doc") || mimeType.contains("word") || mimeType.contains("officedocument") -> "WORD"
            extension == "xml" || mimeType.contains("xml") -> "XML"
            else -> "OTHER"
        }

        // Create internal directory
        val docsDir = File(context.filesDir, "imported_docs")
        if (!docsDir.exists()) {
            docsDir.mkdirs()
        }

        val safeName = fileName.replace(Regex("[^a-zA-Z0-9._\\-ء-ي]"), "_")
        val destFile = File(docsDir, "${System.currentTimeMillis()}_$safeName")

        var extractedFullText = ""
        var extractedPreview = ""

        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (fileSize == 0L && destFile.exists()) {
                fileSize = destFile.length()
            }

            // Text extraction depending on type
            when (fileType) {
                "XML" -> {
                    extractedFullText = extractXmlText(destFile)
                    extractedPreview = generateSnippet(extractedFullText)
                }
                "WORD" -> {
                    if (extension == "docx") {
                        extractedFullText = extractDocxText(destFile)
                        extractedPreview = generateSnippet(extractedFullText)
                    } else {
                        extractedPreview = "مستند Microsoft Word (.doc) - الحجم: ${formatFileSize(fileSize)}"
                    }
                }
                "PDF" -> {
                    extractedFullText = extractBasicPdfInfo(destFile, fileName, fileSize)
                    extractedPreview = generateSnippet(extractedFullText)
                }
                else -> {
                    if (destFile.length() < 1024 * 500) { // < 500 KB text
                        try {
                            extractedFullText = destFile.readText(Charsets.UTF_8)
                            extractedPreview = generateSnippet(extractedFullText)
                        } catch (e: Exception) {
                            extractedPreview = "ملف نصي / مستند - الحجم: ${formatFileSize(fileSize)}"
                        }
                    } else {
                        extractedPreview = "مستند ${extension.uppercase()} - الحجم: ${formatFileSize(fileSize)}"
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error importing file: ${e.message}", e)
            extractedPreview = "تم حفظ الملف بنجاح (${formatFileSize(fileSize)})"
        }

        val category = if (chosenCategory.isNotBlank() && chosenCategory != "تلقائي") {
            chosenCategory
        } else {
            inferCategory(fileName, fileType)
        }

        return ImportedDocumentItem(
            fileName = fileName,
            fileType = fileType,
            mimeType = mimeType,
            localFilePath = destFile.absolutePath,
            fileSizeBytes = fileSize,
            category = category,
            previewText = extractedPreview,
            fullContentText = extractedFullText,
            notes = userNotes,
            isFavorite = false,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Create a manual document (e.g. from pasted XML, Word content, or study notes)
     */
    fun createManualDocument(
        context: Context,
        title: String,
        fileType: String,
        category: String,
        content: String,
        notes: String
    ): ImportedDocumentItem {
        val docsDir = File(context.filesDir, "imported_docs")
        if (!docsDir.exists()) docsDir.mkdirs()

        val ext = when (fileType) {
            "XML" -> "xml"
            "WORD" -> "docx"
            "PDF" -> "txt"
            else -> "txt"
        }

        val safeTitle = title.replace(Regex("[^a-zA-Z0-9._\\-ء-ي]"), "_")
        val destFile = File(docsDir, "${System.currentTimeMillis()}_${safeTitle}.$ext")
        destFile.writeText(content, Charsets.UTF_8)

        val size = destFile.length()

        return ImportedDocumentItem(
            fileName = if (title.contains('.')) title else "$title.$ext",
            fileType = fileType,
            mimeType = when (fileType) {
                "XML" -> "text/xml"
                "WORD" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                "PDF" -> "application/pdf"
                else -> "text/plain"
            },
            localFilePath = destFile.absolutePath,
            fileSizeBytes = size,
            category = category,
            previewText = generateSnippet(content),
            fullContentText = content,
            notes = notes,
            isFavorite = false,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Extract text from Microsoft Word (.docx) by reading word/document.xml in the ZIP archive
     */
    private fun extractDocxText(file: File): String {
        val textBuilder = StringBuilder()
        try {
            FileInputStream(file).use { fis ->
                ZipInputStream(fis).use { zis ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        if (entry.name == "word/document.xml") {
                            val factory = XmlPullParserFactory.newInstance()
                            factory.isNamespaceAware = true
                            val parser = factory.newPullParser()
                            val contentString = zis.bufferedReader(Charsets.UTF_8).readText()
                            parser.setInput(StringReader(contentString))

                            var eventType = parser.eventType
                            var isInsideParagraph = false

                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                when (eventType) {
                                    XmlPullParser.START_TAG -> {
                                        if (parser.name == "p") {
                                            isInsideParagraph = true
                                        }
                                    }
                                    XmlPullParser.TEXT -> {
                                        val text = parser.text
                                        if (!text.isNullOrBlank()) {
                                            textBuilder.append(text)
                                        }
                                    }
                                    XmlPullParser.END_TAG -> {
                                        if (parser.name == "p") {
                                            textBuilder.append("\n")
                                            isInsideParagraph = false
                                        }
                                    }
                                }
                                eventType = parser.next()
                            }
                            break
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Docx extract fallback: ${e.message}")
        }

        val result = textBuilder.toString().trim()
        return if (result.isNotBlank()) result else "مستند Word (.docx) تم استيراده بنجاح."
    }

    /**
     * Extract and pretty-print XML text
     */
    private fun extractXmlText(file: File): String {
        return try {
            val raw = file.readText(Charsets.UTF_8)
            formatXmlString(raw)
        } catch (e: Exception) {
            "خطأ في قراءة ملف XML: ${e.message}"
        }
    }

    private fun extractBasicPdfInfo(file: File, name: String, sizeBytes: Long): String {
        val sb = StringBuilder()
        sb.append("📄 وثيقة أكاديمية بصيغة PDF: $name\n")
        sb.append("📦 الحجم: ${formatFileSize(sizeBytes)}\n")
        sb.append("📅 تاريخ الاستيراد: ${SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())}\n")
        sb.append("ℹ️ جاهزة للقراءة والمشاركة والتحليل عبر المساعد الذكي Gemini.")
        return sb.toString()
    }

    private fun generateSnippet(fullText: String, maxLen: Int = 180): String {
        if (fullText.isBlank()) return ""
        val clean = fullText.replace("\n", " ").trim()
        return if (clean.length > maxLen) {
            clean.substring(0, maxLen) + "..."
        } else {
            clean
        }
    }

    private fun inferCategory(name: String, fileType: String): String {
        val lower = name.lowercase(Locale.ROOT)
        return when {
            lower.contains("cour") || lower.contains("درس") || lower.contains("محاضرة") || lower.contains("cours") || lower.contains("chapitre") -> "محاضرات ودروس"
            lower.contains("pfe") || lower.contains("memoire") || lower.contains("مذكرة") || lower.contains("تخرج") || lower.contains("these") -> "مذكرات وأطروحات"
            lower.contains("tp") || lower.contains("td") || lower.contains("devoir") || lower.contains("rapport") || lower.contains("تقرير") -> "تقارير وأعمال تطبيقية"
            lower.contains("recherche") || lower.contains("بحث") || lower.contains("projet") || lower.contains("مشروع") -> "بحوث ومشاريع"
            fileType == "XML" || lower.contains("xml") || lower.contains("data") || lower.contains("note") || lower.contains("emploi") -> "بيانات وجداول XML"
            lower.contains("demande") || lower.contains("طلب") || lower.contains("شهادة") || lower.contains("releve") || lower.contains("كشف") -> "كشوف ونماذج إدارية"
            else -> "محاضرات ودروس"
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> String.format(Locale.US, "%.1f ميغابايت", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.1f كيلوبايت", kb)
            else -> "$bytes بايت"
        }
    }

    fun formatXmlString(xml: String): String {
        return try {
            val lines = xml.lines()
            if (lines.size > 1) return xml // Already indented
            // Simple tag-based indent
            var indent = 0
            val sb = StringBuilder()
            var inTag = false
            var tagContent = StringBuilder()

            for (ch in xml) {
                if (ch == '<') {
                    if (tagContent.isNotEmpty()) {
                        val text = tagContent.toString().trim()
                        if (text.isNotEmpty()) {
                            sb.append(text).append("\n")
                        }
                        tagContent.clear()
                    }
                    inTag = true
                    tagContent.append(ch)
                } else if (ch == '>') {
                    tagContent.append(ch)
                    val tag = tagContent.toString()
                    tagContent.clear()
                    inTag = false

                    if (tag.startsWith("</")) {
                        indent = (indent - 1).coerceAtLeast(0)
                        sb.append("  ".repeat(indent)).append(tag).append("\n")
                    } else if (tag.endsWith("/>") || tag.startsWith("<?") || tag.startsWith("<!")) {
                        sb.append("  ".repeat(indent)).append(tag).append("\n")
                    } else {
                        sb.append("  ".repeat(indent)).append(tag).append("\n")
                        indent++
                    }
                } else {
                    tagContent.append(ch)
                }
            }
            sb.toString().trim()
        } catch (e: Exception) {
            xml
        }
    }
}
