package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.UserProfile
import com.example.ui.viewmodel.AiChatState
import com.example.ui.viewmodel.MainAppTab
import com.example.ui.viewmodel.StudentProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "كل ما يحتاجه الطالب الجامعي",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Theme Toggle Button (Light/Dark Mode for Late-Night Study)
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDarkMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                    contentDescription = if (isDarkMode) "تفعيل الوضع النهاري" else "تفعيل الوضع الليلي للمذاكرة",
                                    tint = if (isDarkMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier.testTag("top_bar_profile_btn")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "الملف الجامعي",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                MainAppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val icon = when (tab) {
                        MainAppTab.STUDY_PLANNER -> if (isSelected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
                        MainAppTab.ACADEMIC_PROGRESS -> if (isSelected) Icons.Filled.AutoGraph else Icons.Outlined.AutoGraph
                        MainAppTab.RESEARCH_GUIDES -> if (isSelected) Icons.Filled.MenuBook else Icons.Outlined.MenuBook
                        MainAppTab.THESIS_PFE -> if (isSelected) Icons.Filled.School else Icons.Outlined.School
                        MainAppTab.CITATIONS -> if (isSelected) Icons.Filled.FormatQuote else Icons.Outlined.FormatQuote
                        MainAppTab.AI_ASSISTANT -> if (isSelected) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome
                        MainAppTab.NOTEBOOK_LM -> if (isSelected) Icons.Filled.CollectionsBookmark else Icons.Outlined.CollectionsBookmark
                        MainAppTab.ADMIN_DOCS -> if (isSelected) Icons.Filled.Article else Icons.Outlined.Article
                    }

                    val shortTitle = when (tab) {
                        MainAppTab.STUDY_PLANNER -> "الدراسة"
                        MainAppTab.ACADEMIC_PROGRESS -> "التقدم"
                        MainAppTab.RESEARCH_GUIDES -> "البحوث"
                        MainAppTab.THESIS_PFE -> "المذكرة"
                        MainAppTab.CITATIONS -> "المراجع"
                        MainAppTab.AI_ASSISTANT -> "الذكاء"
                        MainAppTab.NOTEBOOK_LM -> "Notebook"
                        MainAppTab.ADMIN_DOCS -> "الوثائق"
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = { Icon(icon, contentDescription = tab.titleAr) },
                        label = {
                            Text(
                                text = shortTitle,
                                fontSize = 9.sp,
                                maxLines = 1,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        alwaysShowLabel = true,
                        modifier = Modifier.testTag("nav_tab_${tab.name}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainAppTab.STUDY_PLANNER -> StudyPlannerScreen(viewModel = viewModel)
                MainAppTab.ACADEMIC_PROGRESS -> AcademicProgressScreen(viewModel = viewModel)
                MainAppTab.RESEARCH_GUIDES -> ResearchGuidesScreen(viewModel = viewModel)
                MainAppTab.THESIS_PFE -> ThesisPfeScreen(viewModel = viewModel)
                MainAppTab.CITATIONS -> CitationsScreen(viewModel = viewModel)
                MainAppTab.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                MainAppTab.NOTEBOOK_LM -> NotebookLmScreen(viewModel = viewModel)
                MainAppTab.ADMIN_DOCS -> AdminDocsScreen(
                    viewModel = viewModel,
                    onOpenProfileEdit = { showProfileDialog = true }
                )
            }

            // Global Loading Indicator for Gemini API Calls
            androidx.compose.animation.AnimatedVisibility(
                visible = aiState is AiChatState.Loading,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { -it },
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("global_gemini_loading_indicator")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .testTag("global_gemini_circular_progress"),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "جاري المعالجة بواسطة Gemini AI...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfileEditDialog(
            userProfile = userProfile,
            isDarkMode = isDarkMode,
            onToggleDarkMode = { viewModel.setDarkMode(it) },
            onOpenProgresSync = {
                showProfileDialog = false
                viewModel.setTab(MainAppTab.ACADEMIC_PROGRESS)
            },
            onDismiss = { showProfileDialog = false },
            onSave = {
                viewModel.updateUserProfile(it)
                showProfileDialog = false
            }
        )
    }
}

@Composable
fun ProfileEditDialog(
    userProfile: UserProfile,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onOpenProgresSync: () -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember(userProfile) { mutableStateOf(userProfile.fullName) }
    var uni by remember(userProfile) { mutableStateOf(userProfile.university) }
    var faculty by remember(userProfile) { mutableStateOf(userProfile.faculty) }
    var dept by remember(userProfile) { mutableStateOf(userProfile.department) }
    var spec by remember(userProfile) { mutableStateOf(userProfile.specialty) }
    var level by remember(userProfile) { mutableStateOf(userProfile.academicLevel) }
    var matricule by remember(userProfile) { mutableStateOf(userProfile.studentIdNumber) }
    var email by remember(userProfile) { mutableStateOf(userProfile.email) }
    var phone by remember(userProfile) { mutableStateOf(userProfile.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("بيانات الطالب والإعدادات", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Quick PROGRES Auto-fill Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProgresSync() }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "استيراد تلقائي من بروقرس PROGRES",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "جلب الاسم والجامعة والتخصص وكشف النقاط مباشرة",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                item {
                    // Late Night Study Mode Theme Card
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "الوضع الليلي للمذاكرة 🌙",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "تقليل إجهاد العين أثناء المراجعة الليلية",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = onToggleDarkMode,
                                modifier = Modifier.testTag("profile_dark_mode_switch")
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("الاسم واللقب *") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = uni,
                        onValueChange = { uni = it },
                        label = { Text("الجامعة / المركز الجامعي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = faculty,
                        onValueChange = { faculty = it },
                        label = { Text("الكلية / المعهد") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { dept = it },
                        label = { Text("القسم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = spec,
                        onValueChange = { spec = it },
                        label = { Text("التخصص والشعبة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = level,
                        onValueChange = { level = it },
                        label = { Text("المستوى الدراسي (مثال: ماستر 2، ليسانس 3)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = matricule,
                        onValueChange = { matricule = it },
                        label = { Text("رقم التسجيل الجامعي (Matricule)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("البريد الإلكتروني الجامعي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الهاتف") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        userProfile.copy(
                            fullName = name.trim(),
                            university = uni.trim(),
                            faculty = faculty.trim(),
                            department = dept.trim(),
                            specialty = spec.trim(),
                            academicLevel = level.trim(),
                            studentIdNumber = matricule.trim(),
                            email = email.trim(),
                            phone = phone.trim()
                        )
                    )
                },
                modifier = Modifier.testTag("save_profile_btn")
            ) {
                Text("حفظ التغييرات")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
