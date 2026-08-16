package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.content.AiPromptItem
import com.example.data.content.PromptsLibraryData
import com.example.data.local.DocumentImportHelper
import com.example.ui.viewmodel.AiChatState
import com.example.ui.viewmodel.StudentProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val inputPrompt by viewModel.aiInputPrompt.collectAsStateWithLifecycle()
    val history by viewModel.aiHistory.collectAsStateWithLifecycle()

    val allPrompts = remember { PromptsLibraryData.prompts }
    var selectedPromptCategory by remember { mutableStateOf("الكل") }
    var promptDialogItem by remember { mutableStateOf<AiPromptItem?>(null) }

    val categories = listOf("الكل", "فهم", "تلخيص", "أسئلة", "تحسين", "ترجمة", "خطة بحث")

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importDocumentFromUri(context, uri, chosenCategory = "محاضرات ودروس")
            // Also load into AI prompt
            val docItem = DocumentImportHelper.importDocumentFromUri(context, uri)
            val promptText = if (docItem.fullContentText.isNotBlank()) {
                "يرجى تلخيص وشرح أهم النقاط في هذا المستند (${docItem.fileName}):\n\n${docItem.fullContentText.take(3000)}"
            } else {
                "أريد المساعدة في مراجعة وتلخيص هذا الملف الأكاديمي: ${docItem.fileName} (${docItem.previewText})"
            }
            viewModel.setAiPrompt(promptText)
        }
    }
    val filteredPrompts = if (selectedPromptCategory == "الكل") allPrompts else allPrompts.filter { it.category == selectedPromptCategory }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_hero_banner"),
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
                                    "المساعد الأكاديمي الذكي (AI Study Partner)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "مساعد تعليمي ذكي مخصص للطلبة للمساعدة على الفهم، التلخيص، صياغة الإشكاليات والتحضير للامتحانات.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Quick Prompts Section
            item {
                Text(
                    "💡 نماذج الأوامر الأكاديمية الجاهزة (Prompts Library):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedPromptCategory == cat,
                            onClick = { selectedPromptCategory = cat },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("ai_cat_$cat")
                        )
                    }
                }
            }

            // Prompts Horizontal/Grid Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredPrompts.forEach { pItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { promptDialogItem = pItem }
                                .testTag("prompt_item_${pItem.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pItem.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = pItem.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = { promptDialogItem = pItem },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("use_prompt_${pItem.id}")
                                ) {
                                    Text("استخدام", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Input Prompt Box
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_input_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "اكتب سؤالك أو الأمر الأكاديمي:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputPrompt,
                            onValueChange = { viewModel.setAiPrompt(it) },
                            placeholder = { Text("مثال: اشرح لي الفرق بين المنهج الوصفي والمنهج التجريبي مع أمثلة...") },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp).testTag("ai_text_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        filePickerLauncher.launch(
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
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إرفاق PDF / Word / XML", fontSize = 11.sp)
                                }

                                if (inputPrompt.isNotBlank()) {
                                    TextButton(onClick = { viewModel.setAiPrompt("") }) {
                                        Text("مسح", fontSize = 11.sp)
                                    }
                                }
                            }
                            Button(
                                onClick = { viewModel.sendAiPrompt() },
                                enabled = inputPrompt.isNotBlank() && aiState !is AiChatState.Loading,
                                modifier = Modifier.testTag("ai_submit_btn")
                            ) {
                                if (aiState is AiChatState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("جاري المعالجة...")
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إرسال للذكاء الاصطناعي")
                                }
                            }
                        }
                    }
                }
            }

            // AI State Results
            when (val state = aiState) {
                is AiChatState.Loading -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("المساعد الأكاديمي يقوم بالتحليل والصياغة الأكاديمية...", fontSize = 13.sp)
                            }
                        }
                    }
                }
                is AiChatState.Error -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("تنبيه / خطأ في الاتصال", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(state.message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
                is AiChatState.Success -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_result_card"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("إجابة المساعد الأكاديمي:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Row {
                                        IconButton(
                                            onClick = { viewModel.copyToClipboard(context, state.responseText, "إجابة المساعد الأكاديمي") },
                                            modifier = Modifier.testTag("ai_copy_result_btn")
                                        ) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ الإجابة")
                                        }
                                        IconButton(
                                            onClick = { viewModel.shareText(context, state.responseText, "Student Pro DZ - استشارة أكاديمية") }
                                        ) {
                                            Icon(Icons.Outlined.Share, contentDescription = "مشاركة")
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = state.responseText,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                is AiChatState.Idle -> {
                    // Nothing
                }
            }

            // History Items if any
            if (history.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("سجل الاستشارات الأكاديمية السابقة:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                }
                items(history.asReversed()) { (question, answer) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("❓ $question", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(answer, fontSize = 12.sp, maxLines = 4)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { viewModel.copyToClipboard(context, answer, "استشارة سابقة") }) {
                                    Text("نسخ الإجابة كاملة", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for Prompt with custom input
    promptDialogItem?.let { item ->
        var customTopic by remember { mutableStateOf(item.sampleTopic) }

        AlertDialog(
            onDismissRequest = { promptDialogItem = null },
            title = { Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customTopic,
                        onValueChange = { customTopic = it },
                        label = { Text("الموضوع أو المفهوم") },
                        placeholder = { Text(item.placeholderInput) },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_prompt_topic_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.applyPromptTemplate(item, customTopic)
                        promptDialogItem = null
                    },
                    modifier = Modifier.testTag("dialog_prompt_apply_btn")
                ) {
                    Text("تطبيق وإرسال")
                }
            },
            dismissButton = {
                TextButton(onClick = { promptDialogItem = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
