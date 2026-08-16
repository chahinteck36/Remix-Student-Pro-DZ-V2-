package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ImportedDocumentItem
import com.example.ui.viewmodel.StudentProViewModel

private const val NOTEBOOK_LM_URL = "https://notebooklm.google.com/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookLmScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val importedDocs by viewModel.importedDocuments.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var progressVal by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf(NOTEBOOK_LM_URL) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Web Workspace, 1: Quick Prompts & Helpers, 2: Transfer Docs
    var showSendDocDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }

    // Handle hardware back button for WebView navigation
    BackHandler(enabled = canGoBack && selectedTab == 0) {
        webViewInstance?.goBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header Info Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1A73E8).copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CollectionsBookmark,
                                    contentDescription = null,
                                    tint = Color(0xFF1A73E8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Google NotebookLM",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF1A73E8).copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "Gemini AI",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A73E8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "دفتر الملاحظات الذكي والمساعد البحثي المدعوم بـ Gemini",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Action buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showSendDocDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Outlined.FileUpload,
                                contentDescription = "تصدير محتوى للمفكرة",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(NOTEBOOK_LM_URL))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Outlined.OpenInBrowser,
                                contentDescription = "فتح في المتصفح الخارجي",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Web, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("المفكرة التفاعلية", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("أوامر وتوليد الأفكار", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("دليل وميزات", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }
            }
        }

        // Main Tab Content
        when (selectedTab) {
            0 -> {
                // Embedded NotebookLM WebView
                Column(modifier = Modifier.fillMaxSize()) {
                    // Browser Control Bar
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { webViewInstance?.goBack() },
                                    enabled = canGoBack,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "الرجوع",
                                        tint = if (canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { webViewInstance?.goForward() },
                                    enabled = canGoForward,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "للأمام",
                                        tint = if (canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { webViewInstance?.reload() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "تحديث",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { webViewInstance?.loadUrl(NOTEBOOK_LM_URL) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Home,
                                        contentDescription = "الصفحة الرئيسية",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Quick tip hint
                            Text(
                                text = "سجل دخول بحساب Google لفتح دفاتر مذكراتك",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Progress Bar
                    if (isLoading) {
                        LinearProgressIndicator(
                            progress = { progressVal / 100f },
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Native Android WebView with full error recovery & lifecycle safety
                    Box(modifier = Modifier.fillMaxSize()) {
                        var hasWebError by remember { mutableStateOf(false) }
                        var errorMessage by remember { mutableStateOf("") }

                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

                                    // Cookie & storage configuration
                                    val cookieManager = CookieManager.getInstance()
                                    cookieManager.setAcceptCookie(true)
                                    cookieManager.setAcceptThirdPartyCookies(this, true)

                                    @SuppressLint("SetJavaScriptEnabled")
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        databaseEnabled = true
                                        loadWithOverviewMode = true
                                        useWideViewPort = true
                                        builtInZoomControls = true
                                        displayZoomControls = false
                                        allowFileAccess = true
                                        allowContentAccess = true
                                        setSupportZoom(true)
                                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                        cacheMode = WebSettings.LOAD_DEFAULT
                                    }

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            isLoading = true
                                            hasWebError = false
                                            currentUrl = url ?: NOTEBOOK_LM_URL
                                            canGoBack = view?.canGoBack() == true
                                            canGoForward = view?.canGoForward() == true
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            isLoading = false
                                            canGoBack = view?.canGoBack() == true
                                            canGoForward = view?.canGoForward() == true
                                        }

                                        override fun onReceivedError(
                                            view: WebView?,
                                            request: WebResourceRequest?,
                                            error: WebResourceError?
                                        ) {
                                            super.onReceivedError(view, request, error)
                                            if (request?.isForMainFrame == true) {
                                                isLoading = false
                                                hasWebError = true
                                                errorMessage = error?.description?.toString() ?: "تعذر تحميل الصفحة"
                                            }
                                        }

                                        override fun onRenderProcessGone(
                                            view: WebView?,
                                            detail: RenderProcessGoneDetail?
                                        ): Boolean {
                                            // Recover safely if GPU or renderer crashes in emulator
                                            view?.destroy()
                                            webViewInstance = null
                                            hasWebError = true
                                            errorMessage = "تمت إعادة تعيين محرك العرض"
                                            return true
                                        }
                                    }

                                    webChromeClient = object : WebChromeClient() {
                                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                            progressVal = newProgress
                                            if (newProgress >= 100) isLoading = false
                                        }
                                    }

                                    loadUrl(NOTEBOOK_LM_URL)
                                    webViewInstance = this
                                }
                            },
                            update = { webView ->
                                webViewInstance = webView
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // If offline or web error occurred, display a clean fallback card
                        if (hasWebError) {
                            Surface(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "تعذر الاتصال بخدمة NotebookLM",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "يرجى التحقق من اتصال الإنترنت أو فتح المنصة في المتصفح الخارجي.",
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Button(
                                            onClick = {
                                                hasWebError = false
                                                webViewInstance?.loadUrl(NOTEBOOK_LM_URL)
                                            }
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("إعادة المحاولة")
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(NOTEBOOK_LM_URL))
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("فتح في المتصفح")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Prompts & Academic Workflows tailored for NotebookLM
                NotebookLmPromptsTab(
                    onCopyPrompt = { prompt ->
                        clipboardManager.setText(AnnotatedString(prompt))
                        Toast.makeText(context, "تم نسخ الأمر جاهزاً للصق في NotebookLM!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            2 -> {
                // Features & Guide
                NotebookLmGuideTab(
                    onOpenNotebookLm = { selectedTab = 0 }
                )
            }
        }
    }

    // Dialog: Send / Export Student Data to NotebookLM
    if (showSendDocDialog) {
        AlertDialog(
            onDismissRequest = { showSendDocDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("نسخ وتجهيز بياناتك لـ NotebookLM", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)
                ) {
                    item {
                        Text(
                            text = "اختر أحد محتوياتك أو مستنداتك لنسخ نصه بنقرة واحدة ولصقه مباشرة كمصدر (Source) في NotebookLM:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (userProfile.specialty.isNotBlank()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    val summary = "الطالب: ${userProfile.fullName}\nالتخصص: ${userProfile.specialty} (${userProfile.academicLevel})\nالجامعة: ${userProfile.university}\nالكلية: ${userProfile.faculty}"
                                    clipboardManager.setText(AnnotatedString(summary))
                                    Toast.makeText(context, "تم نسخ ملخص الملف الأكاديمي!", Toast.LENGTH_SHORT).show()
                                    showSendDocDialog = false
                                    selectedTab = 0
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("بيانات الطالب والتخصص الأكاديمي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${userProfile.specialty} • ${userProfile.university}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    if (importedDocs.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "لم تقم باستيراد ملفات PDF أو Word بعد في التطبيق. يمكنك استيرادها من تبويب (تنظيم الدراسة -> المستندات).",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(importedDocs) { doc ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    val contentToCopy = if (doc.fullContentText.isNotBlank()) doc.fullContentText else "${doc.fileName}\n${doc.previewText}\n${doc.notes}"
                                    clipboardManager.setText(AnnotatedString(contentToCopy))
                                    Toast.makeText(context, "تم نسخ محتوى مستند: ${doc.fileName}", Toast.LENGTH_SHORT).show()
                                    showSendDocDialog = false
                                    selectedTab = 0
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        when (doc.fileType.uppercase()) {
                                            "PDF" -> Icons.Default.PictureAsPdf
                                            "WORD" -> Icons.Default.Description
                                            else -> Icons.Default.Code
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(doc.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${doc.category} • ${doc.fileType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSendDocDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
private fun NotebookLmPromptsTab(
    onCopyPrompt: (String) -> Unit
) {
    val promptTemplates = listOf(
        NotebookLmPromptItem(
            title = "إنشاء بودكاست صوتي وملخص تفاعلي (Audio Overview)",
            category = "المراجعة الذكية",
            description = "أمر مخصص لـ NotebookLM لإنشاء نظرة عامة صوتية وتوليد نقاش ثنائي حول محتوى المحاضرة.",
            prompt = "يرجى تحليل جميع المصادر المرفقة لهذه المادة وتقديم ملخص شامل مقسم إلى: 1. الأفكار الجوهرية، 2. المفاهيم والنظريات الأساسية، 3. النقاط المعقدة مع شرح مبسط لها، 4. سيناريو حواري مقترح لمناقشة المحتوى."
        ),
        NotebookLmPromptItem(
            title = "استخراج بنك أسئلة امتحانات وتطبيقات (Exam Generator)",
            category = "الامتحانات والتقييم",
            description = "توليد أسئلة مقالية، متعددة الخيارات (QCM) مع حلولها النموذجية مأخوذة حصراً من مستنداتك.",
            prompt = "بناءً على المستندات والمحاضرات المرفوعة فقط، استخرج بنك أسئلة يشمل: 10 أسئلة اختيار من متعدد (QCM) دقيقة مع الإجابة الصحيحة وتبريرها، و 5 أسئلة مقالية تحليلية مع عناصر الإجابة النموذجية وسلم التنقيط المقترح."
        ),
        NotebookLmPromptItem(
            title = "صياغة إشكالية وخطة مذكرة تخرج (Thesis Outliner)",
            category = "المذكرة والتخرج",
            description = "بناء هيكل أكاديمي متين (مقدمة، إشكالية، فرضيات، فصول نظرية وميدانية) من مراجعك.",
            prompt = "من خلال المراجع والمقالات العلمية المرفقة، اقترح: 1. صياغة دقيقة لإشكالية البحث والأسئلة الفرعية، 2. الفرضيات الأساسية القابلة للاختبار، 3. خطة بحثية متكاملة مقسمة إلى فصول ومباحث مع المنهجية المناسبة."
        ),
        NotebookLmPromptItem(
            title = "مقارنة ونقد الدراسات السابقة (Literature Review)",
            category = "البحث العلمي",
            description = "جدول مقارنة منهجي بين المقالات العلمية والمؤلفين المرفقين في مفكرتك.",
            prompt = "قم بإجراء مراجعة نقدية ومقارنة بين الدراسات السابقة المرفوعة: 1. أوجه التشابه والاختلاف في النتائج، 2. الثغرات البحثية (Research Gaps) التي لم تعالجها، 3. جدول يلخص (الباحث، السنة، المنهج، العينة، أهم النتائج)."
        ),
        NotebookLmPromptItem(
            title = "قاموس المصطلحات والمفاهيم الأساسية (Study Glossary)",
            category = "ملخصات سريعة",
            description = "استخراج المعجم الدلالي والمصطلحات الأكاديمية مع تعريفاتها المعتمدة في مراجعك.",
            prompt = "استخرج قائمة أبجدية شاملة لجميع المصطلحات والمفاهيم العلمية الواردة في هذه الوثائق مع تقديم تعريف أكاديمي موجز ودقيق لكل مصطلح مستنداً فقط إلى النصوص المرفوعة."
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "انسخ أي من هذه الأوامر الأكاديمية الاحترافية والصقها مباشرة في محادثة NotebookLM بعد رفع مذكراتك ومحاضراتك.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(promptTemplates) { item ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = item.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.prompt,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onCopyPrompt(item.prompt) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نسخ الأمر واستخدامه في NotebookLM", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotebookLmGuideTab(
    onOpenNotebookLm: () -> Unit
) {
    val steps = listOf(
        "1. تسجيل الدخول" to "افتح تبويب المفكرة التفاعلية وسجّل الدخول بحساب Google الشخصي أو الجامعي.",
        "2. إنشاء دفتر مذكرات (Notebook)" to "أنشئ دفتراً جديداً وسمّه باسم مادتك أو مذكرة تخرجك.",
        "3. رفع المصادر (Sources)" to "قم برفع ملفات الـ PDF أو شرائح المحاضرات أو نسخ النصوص مباشرة من تطبيقك عبر زر التصدير.",
        "4. توليد البودكاست الصوتي (Audio Overview)" to "انقر على زر 'Generate Audio Overview' لتحويل محاضراتك ونصوصك إلى بودكاست حواري ذكي بالذكاء الاصطناعي.",
        "5. الاستجواب والأسئلة مع الاقتباسات" to "اطرح أي سؤال وسيجيبك Gemini مع الإشارة الدقيقة للصفحة والفقرة في مراجعك دون أي اختلاق."
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8).copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF1A73E8), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("ما هو Google NotebookLM ولماذا هو الأفضل للطلبة؟", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "NotebookLM هو بيئة بحثية وتدوين شخصية مبنية على نموذج Gemini 1.5 Pro. ميزته الكبرى للطلبة أنه (Source-Grounded) يعتمد بنسبة 100% على ملفاتك ومصادرك الخاصة، مما يمنع الأخطاء ويوفر توثيقاً دقيقاً مع ميزة البودكاست الصوتي التفاعلي.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Text(
                text = "دليل الاستخدام السريع خطوة بخطوة:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(steps) { (title, desc) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(desc, fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Button(
                onClick = onOpenNotebookLm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("الانتقال إلى المفكرة التفاعلية الآن")
            }
        }
    }
}

private data class NotebookLmPromptItem(
    val title: String,
    val category: String,
    val description: String,
    val prompt: String
)
