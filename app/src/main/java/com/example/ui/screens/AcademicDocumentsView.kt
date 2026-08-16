package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.DocumentImportHelper
import com.example.data.local.ImportedDocumentItem
import com.example.ui.viewmodel.StudentProViewModel
import java.text.SimpleDateFormat
import java.util.*

val DocumentCategories = listOf(
    "الكل",
    "محاضرات ودروس",
    "مذكرات وأطروحات",
    "بحوث ومشاريع",
    "تقارير وأعمال تطبيقية",
    "بيانات وجداول XML",
    "كشوف ونماذج إدارية",
    "عام"
)

val DocumentTypeFilters = listOf("الكل", "PDF", "WORD", "XML")

@Composable
fun AcademicDocumentsView(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDocs by viewModel.importedDocuments.collectAsStateWithLifecycle()
    val typeFilter by viewModel.docTypeFilter.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.docCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.importedDocSearchQuery.collectAsStateWithLifecycle()

    var showManualAddDialog by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf<ImportedDocumentItem?>(null) }
    var showImportDetailsDialog by remember { mutableStateOf<Uri?>(null) }
    var docToDelete by remember { mutableStateOf<ImportedDocumentItem?>(null) }

    // System Document Picker for PDF, Word (.docx, .doc), and XML files
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            showImportDetailsDialog = uri
        }
    }

    // Filter documents
    val filteredDocs = remember(allDocs, typeFilter, categoryFilter, searchQuery) {
        allDocs.filter { doc ->
            val matchType = typeFilter == "الكل" || doc.fileType.equals(typeFilter, ignoreCase = true)
            val matchCat = categoryFilter == "الكل" || doc.category == categoryFilter
            val matchQuery = searchQuery.isBlank() ||
                    doc.fileName.contains(searchQuery, ignoreCase = true) ||
                    doc.category.contains(searchQuery, ignoreCase = true) ||
                    doc.notes.contains(searchQuery, ignoreCase = true) ||
                    doc.previewText.contains(searchQuery, ignoreCase = true)
            matchType && matchCat && matchQuery
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    documentPickerLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "text/xml",
                            "application/xml",
                            "text/plain",
                            "*/*"
                        )
                    )
                },
                icon = { Icon(Icons.Default.UploadFile, contentDescription = null) },
                text = { Text("استيراد ملف جديد", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_import_document")
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Import Hub Banner
            item {
                DocumentHubHeroCard(
                    totalDocs = allDocs.size,
                    pdfCount = allDocs.count { it.fileType == "PDF" },
                    wordCount = allDocs.count { it.fileType == "WORD" },
                    xmlCount = allDocs.count { it.fileType == "XML" },
                    onPickFile = {
                        documentPickerLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "text/xml",
                                "application/xml",
                                "text/plain",
                                "*/*"
                            )
                        )
                    },
                    onOpenManualInput = { showManualAddDialog = true }
                )
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setImportedDocSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doc_search_input"),
                    placeholder = { Text("بحث في الملفات، المذكرات، والمستندات...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setImportedDocSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // File Type Filters (PDF, Word, XML)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "تصفية حسب نوع الملف:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DocumentTypeFilters.forEach { type ->
                            val isSelected = typeFilter == type
                            val typeLabel = when (type) {
                                "الكل" -> "جميع الصيغ (${allDocs.size})"
                                "PDF" -> "📕 مستندات PDF (${allDocs.count { it.fileType == "PDF" }})"
                                "WORD" -> "📘 ملفات Word DOCX (${allDocs.count { it.fileType == "WORD" }})"
                                "XML" -> "📊 ملفات وبيانات XML (${allDocs.count { it.fileType == "XML" }})"
                                else -> type
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setDocTypeFilter(type) },
                                label = { Text(typeLabel, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Category Filters
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DocumentCategories.forEach { cat ->
                        val isSelected = categoryFilter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setDocCategoryFilter(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Documents List or Empty State
            if (filteredDocs.isEmpty()) {
                item {
                    EmptyDocumentsState(
                        hasAnyDocs = allDocs.isNotEmpty(),
                        onPickFile = {
                            documentPickerLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "application/msword",
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                    "text/xml",
                                    "application/xml",
                                    "text/plain",
                                    "*/*"
                                )
                            )
                        },
                        onOpenManual = { showManualAddDialog = true }
                    )
                }
            } else {
                items(filteredDocs, key = { it.id }) { doc ->
                    ImportedDocumentCard(
                        doc = doc,
                        onOpenExternal = { viewModel.openDocumentExternal(context, doc) },
                        onShare = { viewModel.shareDocumentFile(context, doc) },
                        onPreview = { showPreviewDialog = doc },
                        onAiAnalyze = { viewModel.sendDocumentToAiAssistant(doc) },
                        onToggleFavorite = { viewModel.toggleDocumentFavorite(doc.id, !doc.isFavorite) },
                        onDelete = { docToDelete = doc }
                    )
                }
            }
        }
    }

    // Dialog: Tag & Confirm File Import from Picker
    showImportDetailsDialog?.let { uri ->
        ImportDetailsDialog(
            uri = uri,
            onDismiss = { showImportDetailsDialog = null },
            onConfirm = { category, notes ->
                viewModel.importDocumentFromUri(context, uri, category, notes)
                showImportDetailsDialog = null
            }
        )
    }

    // Dialog: Manual XML / Word / Text input
    if (showManualAddDialog) {
        ManualDocumentInputDialog(
            onDismiss = { showManualAddDialog = false },
            onSave = { title, type, cat, content, notes ->
                viewModel.addManualDocument(context, title, type, cat, content, notes)
                showManualAddDialog = false
            }
        )
    }

    // Dialog: Full Preview & Reader
    showPreviewDialog?.let { doc ->
        DocumentPreviewDialog(
            doc = doc,
            onDismiss = { showPreviewDialog = null },
            onCopy = { viewModel.copyToClipboard(context, doc.fullContentText.ifBlank { doc.previewText }, doc.fileName) },
            onOpenExternal = { viewModel.openDocumentExternal(context, doc) },
            onAiAnalyze = {
                showPreviewDialog = null
                viewModel.sendDocumentToAiAssistant(doc)
            }
        )
    }

    // Dialog: Delete Confirmation
    docToDelete?.let { doc ->
        AlertDialog(
            onDismissRequest = { docToDelete = null },
            title = { Text("حذف المستند") },
            text = { Text("هل أنت متأكد من رغبتك في حذف ملف \"${doc.fileName}\" من التطبيق؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteImportedDocument(doc)
                        docToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { docToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun DocumentHubHeroCard(
    totalDocs: Int,
    pdfCount: Int,
    wordCount: Int,
    xmlCount: Int,
    onPickFile: () -> Unit,
    onOpenManualInput: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "مستودع المستندات والملفات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "دعم كامل لملفات PDF، Word (DOCX)، وهياكل XML",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(title = "PDF", count = pdfCount, color = Color(0xFFBA1A1A), modifier = Modifier.weight(1f))
                StatBadge(title = "Word", count = wordCount, color = Color(0xFF1B64BA), modifier = Modifier.weight(1f))
                StatBadge(title = "XML", count = xmlCount, color = Color(0xFF006B54), modifier = Modifier.weight(1f))
                StatBadge(title = "الإجمالي", count = totalDocs, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            }

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPickFile,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استيراد من الهاتف", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenManualInput,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إدخال كود XML / نص", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatBadge(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ImportedDocumentCard(
    doc: ImportedDocumentItem,
    onOpenExternal: () -> Unit,
    onShare: () -> Unit,
    onPreview: () -> Unit,
    onAiAnalyze: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val typeColor = when (doc.fileType) {
        "PDF" -> Color(0xFFBA1A1A)
        "WORD" -> Color(0xFF1B64BA)
        "XML" -> Color(0xFF006B54)
        else -> MaterialTheme.colorScheme.secondary
    }

    val typeIcon = when (doc.fileType) {
        "PDF" -> Icons.Default.PictureAsPdf
        "WORD" -> Icons.Default.Description
        "XML" -> Icons.Default.DataObject
        else -> Icons.Default.InsertDriveFile
    }

    val dateFormatted = remember(doc.timestamp) {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(doc.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPreview() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Type Badge + File Name + Favorite & Delete
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = typeColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            typeIcon,
                            contentDescription = doc.fileType,
                            tint = typeColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.fileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = typeColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = doc.fileType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = typeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = doc.category,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = DocumentImportHelper.formatFileSize(doc.fileSizeBytes),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (doc.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "تفضيل",
                        tint = if (doc.isFavorite) Color(0xFFC78200) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Preview snippet
            if (doc.previewText.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = doc.previewText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(10.dp),
                        fontFamily = if (doc.fileType == "XML") FontFamily.Monospace else FontFamily.Default
                    )
                }
            }

            if (doc.notes.isNotBlank()) {
                Text(
                    text = "📝 ملاحظة: ${doc.notes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatted,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // AI Analyze
                    FilledTonalIconButton(
                        onClick = onAiAnalyze,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "تحليل بالذكاء الاصطناعي",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Open in default external viewer (Adobe / Word / Browser)
                    FilledTonalIconButton(
                        onClick = onOpenExternal,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Launch,
                            contentDescription = "فتح الملف",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "مشاركة",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyDocumentsState(
    hasAnyDocs: Boolean,
    onPickFile: () -> Unit,
    onOpenManual: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = if (hasAnyDocs) "لا توجد ملفات مطابقة للبحث أو التصفية" else "لا توجد ملفات مستوردة حتى الآن",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "يمكنك استيراد ملفات المحاضرات (PDF)، مذكرات ومسودات التخرج (Word DOCX)، أو جداول وبيانات المناهج الأكاديمية (XML) لتصفحها وتحليلها بالذكاء الاصطناعي.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPickFile,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استيراد ملف الآن")
                }

                OutlinedButton(
                    onClick = onOpenManual,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("لصق XML / نص")
                }
            }
        }
    }
}

@Composable
fun ImportDetailsDialog(
    uri: Uri,
    onDismiss: () -> Unit,
    onConfirm: (category: String, notes: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("محاضرات ودروس") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تأكيد استيراد الملف", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "حدد تصنيف المستند لتسهيل تنظيمه ومراجعته:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Category Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("التصنيف الأكاديمي:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    DocumentCategories.filter { it != "الكل" }.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                        ) {
                            RadioButton(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat, fontSize = 13.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية حول الملف (اختياري)") },
                    placeholder = { Text("مثال: خاص بالسداسي الثاني، الأستاذ فلان...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedCategory, notes) },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ واستيراد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun ManualDocumentInputDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, fileType: String, category: String, content: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("XML") }
    var category by remember { mutableStateOf("بيانات وجداول XML") }
    var content by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Predefined XML Templates
    val sampleScheduleXml = """<?xml version="1.0" encoding="UTF-8"?>
<timetable semester="S2">
    <session day="Sunday" time="08:00-09:30" type="Cour">
        <module>خوارزميات وهياكل بيانات</module>
        <room>مدرج أ</room>
        <professor>د. بوعلام</professor>
    </session>
    <session day="Monday" time="10:00-11:30" type="TD">
        <module>قواعد البيانات المتقدمة</module>
        <room>قاعة 14</room>
        <professor>أ. قادري</professor>
    </session>
</timetable>"""

    val sampleThesisDocxTemplate = """خطة مذكرة التخرج الجامعية:
1. الإطار المنهجي: الإشكالية، الفرضيات، وأهداف البحث.
2. الإطار النظري: المفاهيم والدراسات السابقة.
3. الإطار التطبيقي: عينة الدراسة، أدوات القياس، وتحليل النتائج.
4. الخاتمة والتوصيات وقائمة المراجع."""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PostAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("إدخال ملف / كود نصي", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الملف أو المستند *") },
                    placeholder = { Text("مثال: timetable_s2 أو مسودة_المذكرة") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("XML", "WORD", "PDF").forEach { type ->
                        FilterChip(
                            selected = fileType == type,
                            onClick = {
                                fileType = type
                                if (type == "XML" && content.isBlank()) content = sampleScheduleXml
                                if (type == "WORD" && content.isBlank()) content = sampleThesisDocxTemplate
                            },
                            label = { Text(type) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick Template Insert buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            fileType = "XML"
                            title = "جدول_الحصص_XML"
                            category = "بيانات وجداول XML"
                            content = sampleScheduleXml
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("نموذج XML", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            fileType = "WORD"
                            title = "هيكل_المذكرة_Word"
                            category = "مذكرات وأطروحات"
                            content = sampleThesisDocxTemplate
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("نموذج Word", fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("محتوى الملف النصي / كود XML *") },
                    placeholder = { Text("الصق كود XML أو نص المستند هنا...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = if (fileType == "XML") FontFamily.Monospace else FontFamily.Default
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, fileType, category, content, notes)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ المستند")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun DocumentPreviewDialog(
    doc: ImportedDocumentItem,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onOpenExternal: () -> Unit,
    onAiAnalyze: () -> Unit
) {
    val displayContent = doc.fullContentText.ifBlank { doc.previewText }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        when (doc.fileType) {
                            "PDF" -> Icons.Default.PictureAsPdf
                            "WORD" -> Icons.Default.Description
                            "XML" -> Icons.Default.DataObject
                            else -> Icons.Default.InsertDriveFile
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = doc.fileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "الصيغة: ${doc.fileType}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "الحجم: ${DocumentImportHelper.formatFileSize(doc.fileSizeBytes)}",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = doc.category,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (doc.notes.isNotBlank()) {
                    Text(
                        text = "📝 ملاحظة: ${doc.notes}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                Text(
                    text = "المحتوى المستخرج والمعاينة:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SelectionContainer {
                        Text(
                            text = displayContent,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp,
                            fontFamily = if (doc.fileType == "XML") FontFamily.Monospace else FontFamily.Default
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAiAnalyze,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحليل ذكي")
                }

                OutlinedButton(
                    onClick = onOpenExternal,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("فتح")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("نسخ النص")
            }
        }
    )
}

@Composable
private fun SelectionContainer(content: @Composable () -> Unit) {
    androidx.compose.foundation.text.selection.SelectionContainer {
        content()
    }
}
