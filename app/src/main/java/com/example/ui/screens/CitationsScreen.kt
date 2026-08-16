package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SavedReference
import com.example.ui.viewmodel.StudentProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitationsScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedRefs by viewModel.savedReferences.collectAsStateWithLifecycle()
    val citTitle by viewModel.citTitle.collectAsStateWithLifecycle()
    val citAuthors by viewModel.citAuthors.collectAsStateWithLifecycle()
    val citYear by viewModel.citYear.collectAsStateWithLifecycle()
    val citPublisher by viewModel.citPublisher.collectAsStateWithLifecycle()
    val citType by viewModel.citType.collectAsStateWithLifecycle()
    val citApa by viewModel.citGeneratedApa.collectAsStateWithLifecycle()
    val citIeee by viewModel.citGeneratedIeee.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showGuideDialog by remember { mutableStateOf(false) }

    val refTypes = listOf("كتاب (Book)", "مقال علمي (Journal Article)", "مذكرة/أطروحة (Thesis)", "موقع إلكتروني (Website)")
    val filteredRefs = savedRefs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.authors.contains(searchQuery, ignoreCase = true) ||
        it.referenceType.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Hero Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citations_banner"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "مولد وتوثيق المراجع (APA & IEEE)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "أدخل بيانات الكتاب أو المقال أو المذكرة لتحصل فورياً على التوثيق الأكاديمي القياسي مع حفظه في قاعدة بيانات بحثك.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                Icons.Outlined.FormatQuote,
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = { showGuideDialog = true },
                            modifier = Modifier.fillMaxWidth().testTag("open_citation_rules_btn")
                        ) {
                            Icon(Icons.Outlined.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("دليل قواعد التوثيق والتهميش الأكاديمي", fontSize = 13.sp)
                        }
                    }
                }
            }

            // Generator Form Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citation_generator_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "صانع التوثيق الفوري",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Type selector chips
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(refTypes) { type ->
                                FilterChip(
                                    selected = citType == type,
                                    onClick = {
                                        viewModel.citType.value = type
                                        viewModel.generateCitation(citTitle, citAuthors, citYear, citPublisher, type)
                                    },
                                    label = { Text(type, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = citTitle,
                            onValueChange = {
                                viewModel.citTitle.value = it
                                viewModel.generateCitation(it, citAuthors, citYear, citPublisher, citType)
                            },
                            label = { Text("عنوان المرجع أو المقال *") },
                            modifier = Modifier.fillMaxWidth().testTag("cit_input_title")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = citAuthors,
                            onValueChange = {
                                viewModel.citAuthors.value = it
                                viewModel.generateCitation(citTitle, it, citYear, citPublisher, citType)
                            },
                            label = { Text("المؤلفون (مثال: اللقب، الاسم؛ أو الاسم الكامل)") },
                            modifier = Modifier.fillMaxWidth().testTag("cit_input_authors")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = citYear,
                                onValueChange = {
                                    viewModel.citYear.value = it
                                    viewModel.generateCitation(citTitle, citAuthors, it, citPublisher, citType)
                                },
                                label = { Text("سنة النشر") },
                                modifier = Modifier.weight(1f).testTag("cit_input_year")
                            )

                            OutlinedTextField(
                                value = citPublisher,
                                onValueChange = {
                                    viewModel.citPublisher.value = it
                                    viewModel.generateCitation(citTitle, citAuthors, citYear, it, citType)
                                },
                                label = { Text("دار النشر / المجلة") },
                                modifier = Modifier.weight(2f).testTag("cit_input_publisher")
                            )
                        }

                        // Generated APA Snippet
                        if (citApa.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("📌 توثيق نظام APA 7th:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(citApa, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { viewModel.copyToClipboard(context, citApa, "توثيق APA") }) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("نسخ APA", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Generated IEEE Snippet
                        if (citIeee.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("⚡ توثيق نظام IEEE:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(citIeee, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { viewModel.copyToClipboard(context, citIeee, "توثيق IEEE") }) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("نسخ IEEE", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.saveCurrentCitation() },
                            enabled = citTitle.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("save_citation_btn")
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حفظ المرجع في مكتبتي")
                        }
                    }
                }
            }

            // Saved References Header & Search
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "مكتبة مراجع البحث (${filteredRefs.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث في المراجع المحفوظة بالاسم أو المؤلف...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = if (searchQuery.isNotBlank()) {
                        { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = null) } }
                    } else null,
                    modifier = Modifier.fillMaxWidth().testTag("ref_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Saved References List
            if (filteredRefs.isEmpty()) {
                item {
                    Text(
                        "لا توجد مراجع محفوظة تطابق البحث.",
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(filteredRefs, key = { it.id }) { ref ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("saved_ref_${ref.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = ref.referenceType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteReference(ref) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ref.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "المؤلف: ${ref.authors} (${ref.year})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (ref.sourceOrPublisher.isNotBlank()) {
                                Text(
                                    text = "المصدر: ${ref.sourceOrPublisher}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            if (ref.apaCitation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            ref.apaCitation,
                                            fontSize = 11.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { viewModel.copyToClipboard(context, ref.apaCitation, ref.title) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = { Text("قواعد التوثيق والتهميش الأكاديمي", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            """
📚 1. نظام التوثيق في المتن APA (7th Edition):
• مؤلف واحد: (اللقب، السنة، ص. الصفحة). مثال: (بلقاسم، 2022، ص. 45).
• مؤلفان: (اللقب واللقب، السنة، ص. الصفحة).
• ثلاثة مؤلفين فأكثر: (اللقب وآخرون، السنة، ص. الصفحة).

📚 2. التهميش الكلاسيكي في أسفل الصفحة (Notes de bas de page):
• عند ورود المرجع لأول مرة:
  الاسم الكامل للمؤلف، عنوان الكتاب، دار النشر، مكان النشر، الطبعة، سنة النشر، الصفحة.
• عند تكرار نفس المرجع مباشرة:
  المرجع نفسه، ص. 50. (أو: Ibid., p. 50).
• عند تكرار المرجع ولكن بعد مراجع أخرى:
  اسم المؤلف، مرجع سابق، ص. 60. (أو: Op.cit., p. 60).

📚 3. ترتيب قائمة المراجع النهائية:
• ترتب المراجع هجائياً (أ، ب، ت...) بحسب اللقب العائلي للمؤلف دون ترقيم.
• يتم الفصل بين: المراجع باللغة العربية أولاً، ثم المراجع باللغات الأجنبية، ثم الرسائل والمذكرات، ثم المقالات، ثم المواقع الإلكترونية والوثائق الرسمية.
                            """.trimIndent(),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showGuideDialog = false }) {
                    Text("فهمت")
                }
            }
        )
    }
}
