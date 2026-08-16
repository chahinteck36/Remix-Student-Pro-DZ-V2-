package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.content.AdminDocumentTemplate
import com.example.data.content.AdministrativeTemplatesData
import com.example.data.local.UserProfile
import com.example.ui.viewmodel.StudentProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDocsScreen(
    viewModel: StudentProViewModel,
    onOpenProfileEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedTemplateId by viewModel.selectedAdminTemplateId.collectAsStateWithLifecycle()
    val formValues by viewModel.adminDocFormValues.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedDocCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.docSearchQuery.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedDocLanguage.collectAsStateWithLifecycle()

    var isCustomizerExpanded by remember { mutableStateOf(true) }
    var showQuickPreviewDialog by remember { mutableStateOf<AdminDocumentTemplate?>(null) }

    val allTemplates = remember { AdministrativeTemplatesData.templates }
    val categories = remember { AdministrativeTemplatesData.categories }

    // Filter templates based on category, search query, and language
    val filteredTemplates = remember(selectedCategory, searchQuery, selectedLanguage, allTemplates) {
        allTemplates.filter { tpl ->
            val matchCategory = selectedCategory == "الكل" || tpl.category == selectedCategory
            val matchLang = selectedLanguage == "الكل" || tpl.language == selectedLanguage
            val matchQuery = searchQuery.isBlank() ||
                    tpl.title.contains(searchQuery, ignoreCase = true) ||
                    tpl.description.contains(searchQuery, ignoreCase = true) ||
                    tpl.category.contains(searchQuery, ignoreCase = true) ||
                    tpl.tag.contains(searchQuery, ignoreCase = true)
            matchCategory && matchLang && matchQuery
        }
    }

    val currentTemplate = allTemplates.find { it.id == selectedTemplateId } ?: allTemplates.first()

    // Live generated text for the currently selected template
    val generatedDocument = remember(userProfile, formValues, currentTemplate) {
        currentTemplate.generateContent(userProfile, formValues)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Hero Card
            item {
                DocumentCenterHeader(
                    userProfile = userProfile,
                    onOpenProfileEdit = onOpenProfileEdit
                )
            }

            // Search Bar & Language Filter
            item {
                DocumentSearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setDocSearchQuery(it) },
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { viewModel.setSelectedDocLanguage(it) }
                )
            }

            // Category Filter Tabs
            item {
                DocumentCategoryTabs(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.setSelectedDocCategory(it) }
                )
            }

            // Template Browser Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "النماذج المتاحة (${filteredTemplates.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "اختر نموذجاً للتخصيص والتحميل",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Templates Horizontal/Grid Carousel
            item {
                if (filteredTemplates.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لم يتم العثور على نماذج تطابق بحثك",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredTemplates, key = { it.id }) { tpl ->
                            DocumentTemplateCard(
                                template = tpl,
                                isSelected = tpl.id == selectedTemplateId,
                                onSelect = { viewModel.selectAdminTemplate(tpl.id) },
                                onQuickPreview = { showQuickPreviewDialog = tpl },
                                onQuickDownload = {
                                    val content = tpl.generateContent(userProfile, formValues)
                                    val fileName = "${tpl.title.replace(" ", "_")}.txt"
                                    viewModel.downloadDocumentAsFile(context, fileName, content)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Active Template Customizer & Live Letterhead Form
            item {
                ActiveTemplateEditorSection(
                    template = currentTemplate,
                    userProfile = userProfile,
                    formValues = formValues,
                    generatedContent = generatedDocument,
                    isExpanded = isCustomizerExpanded,
                    onToggleExpand = { isCustomizerExpanded = !isCustomizerExpanded },
                    onFormFieldChange = { key, value -> viewModel.setAdminFormField(key, value) },
                    onCopy = { viewModel.copyToClipboard(context, generatedDocument, currentTemplate.title) },
                    onShare = { viewModel.shareText(context, generatedDocument, currentTemplate.title) },
                    onDownload = {
                        val fileName = "${currentTemplate.title.replace(" ", "_")}.txt"
                        viewModel.downloadDocumentAsFile(context, fileName, generatedDocument)
                    }
                )
            }
        }
    }

    // Quick Preview Dialog
    showQuickPreviewDialog?.let { tpl ->
        val previewText = remember(userProfile, formValues, tpl) {
            tpl.generateContent(userProfile, formValues)
        }
        AlertDialog(
            onDismissRequest = { showQuickPreviewDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tpl.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = previewText,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.selectAdminTemplate(tpl.id)
                        showQuickPreviewDialog = null
                    }
                ) {
                    Text("تخصيص وتعديل")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val fileName = "${tpl.title.replace(" ", "_")}.txt"
                        viewModel.downloadDocumentAsFile(context, fileName, previewText)
                    }
                ) {
                    Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحميل")
                }
            }
        )
    }
}

// ---------------------- Sub-components ----------------------

@Composable
private fun DocumentCenterHeader(
    userProfile: UserProfile,
    onOpenProfileEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("admin_docs_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = "مركز الوثائق والنماذج الجامعية",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مكتبة النماذج والطلبات الإدارية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "توليد وتحميل طلبات التربص الميداني، استمارات التسجيل، الطعون، وتبريرات الأساتذة مدمجة ببياناتك الجامعية الرسمية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.FolderSpecial,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${userProfile.fullName} • ${userProfile.university}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    TextButton(
                        onClick = onOpenProfileEdit,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعديل البيانات", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("ابحث عن نموذج (تربص، تسجيل، طعن، إشراف، سكن...)", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "بحث", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("doc_search_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Language toggle row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "اللغة:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val languages = listOf("الكل", "عربي", "Français")
            languages.forEach { lang ->
                val isSelected = selectedLanguage == lang
                FilterChip(
                    selected = isSelected,
                    onClick = { onLanguageSelected(lang) },
                    label = { Text(lang, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun DocumentCategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
                modifier = Modifier.height(38.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentTemplateCard(
    template: AdminDocumentTemplate,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onQuickPreview: () -> Unit,
    onQuickDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(210.dp)
            .clickable { onSelect() }
            .testTag("admin_tpl_${template.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary), width = 2.dp) else CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = template.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (template.language == "عربي") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = template.language,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (template.language == "عربي") MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = template.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onQuickPreview, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.Visibility,
                        contentDescription = "معاينة سريعة",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onQuickDownload, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.FileDownload,
                        contentDescription = "تحميل مباشر",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = onSelect,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = if (isSelected) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text(if (isSelected) "محدد" else "تخصيص", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun ActiveTemplateEditorSection(
    template: AdminDocumentTemplate,
    userProfile: UserProfile,
    formValues: Map<String, String>,
    generatedContent: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onFormFieldChange: (String, String) -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("admin_form_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تخصيص النموذج: ${template.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "املأ الخانات لتحديث نص الوثيقة الرسمية تلقائياً",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "طي" else "توسيع"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    template.requiredFields.forEach { field ->
                        val currentValue = formValues[field.key] ?: field.defaultValue
                        OutlinedTextField(
                            value = currentValue,
                            onValueChange = { onFormFieldChange(field.key, it) },
                            label = { Text(field.label) },
                            placeholder = { Text(field.placeholder) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("field_${field.key}"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Official Letterhead Paper View
            OfficialDocumentPaperView(
                title = template.title,
                content = generatedContent,
                onCopy = onCopy,
                onShare = onShare,
                onDownload = onDownload
            )
        }
    }
}

@Composable
private fun OfficialDocumentPaperView(
    title: String,
    content: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "معاينة الوثيقة الرسمية الصادرة:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onCopy, modifier = Modifier.size(36.dp).testTag("copy_admin_doc_btn")) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onShare, modifier = Modifier.size(36.dp).testTag("share_admin_doc_btn")) {
                    Icon(Icons.Outlined.Share, contentDescription = "مشاركة", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDownload, modifier = Modifier.size(36.dp).testTag("download_admin_doc_btn")) {
                    Icon(Icons.Outlined.FileDownload, contentDescription = "تحميل وتصدير", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Styled Paper Canvas
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Export Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onDownload,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تحميل الوثيقة (.txt / Word)")
            }

            OutlinedButton(
                onClick = onCopy,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("نسخ النص")
            }
        }
    }
}
