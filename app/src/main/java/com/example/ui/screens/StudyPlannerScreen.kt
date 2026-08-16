package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.*
import com.example.ui.viewmodel.StudentProViewModel
import com.example.ui.viewmodel.StudyPlannerSubTab

val ScheduleColors = listOf(
    Color(0xFF6750A4), // Iris / Purple
    Color(0xFF006874), // Deep Teal
    Color(0xFF7D5260), // Mauve
    Color(0xFF825500), // Warm Amber
    Color(0xFFBA1A1A), // Carmine
    Color(0xFF4355B9)  // Indigo
)

val DayNamesAr = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "السبت")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val currentSubTab by viewModel.studySubTab.collectAsStateWithLifecycle()
    val scheduleList by viewModel.scheduleItems.collectAsStateWithLifecycle()
    val tasksList by viewModel.taskItems.collectAsStateWithLifecycle()
    val semesterTasksList by viewModel.semesterTasks.collectAsStateWithLifecycle()
    val examsList by viewModel.examItems.collectAsStateWithLifecycle()
    val attendanceList by viewModel.attendanceRecords.collectAsStateWithLifecycle()
    val gradesList by viewModel.gradeItems.collectAsStateWithLifecycle()
    val resourceLinksList by viewModel.resourceLinks.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sub-tabs Scrollable Row
        ScrollableTabRow(
            selectedTabIndex = currentSubTab.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            StudyPlannerSubTab.values().forEach { tab ->
                Tab(
                    selected = currentSubTab == tab,
                    onClick = { viewModel.setStudySubTab(tab) },
                    text = {
                        Text(
                            text = tab.titleAr,
                            fontWeight = if (currentSubTab == tab) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("study_subtab_${tab.name}")
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (currentSubTab) {
                StudyPlannerSubTab.SCHEDULE -> ScheduleView(
                    scheduleList = scheduleList,
                    onDeleteItem = { viewModel.deleteScheduleItem(it) },
                    onAddClick = { showAddDialog = true }
                )
                StudyPlannerSubTab.TASKS -> TasksView(
                    semesterTasks = semesterTasksList,
                    legacyTasks = tasksList,
                    onToggleSemesterTask = { viewModel.toggleSemesterTask(it) },
                    onDeleteSemesterTask = { viewModel.deleteSemesterTask(it) },
                    onToggleLegacyTask = { viewModel.toggleTask(it) },
                    onDeleteLegacyTask = { viewModel.deleteTask(it) },
                    onAddClick = { showAddDialog = true }
                )
                StudyPlannerSubTab.EXAMS -> ExamsView(
                    exams = examsList,
                    onToggleComplete = { viewModel.toggleExamCompleted(it) },
                    onDeleteExam = { viewModel.deleteExam(it) },
                    onAddClick = { showAddDialog = true }
                )
                StudyPlannerSubTab.ATTENDANCE -> AttendanceView(
                    records = attendanceList,
                    onIncrementAbsence = { viewModel.incrementAbsence(it) },
                    onDecrementAbsence = { viewModel.decrementAbsence(it) },
                    onIncrementExcused = { viewModel.incrementExcused(it) },
                    onDeleteRecord = { viewModel.deleteAttendance(it) },
                    onAddClick = { showAddDialog = true }
                )
                StudyPlannerSubTab.IMPORTED_DOCS -> AcademicDocumentsView(
                    viewModel = viewModel
                )
                StudyPlannerSubTab.RESOURCES -> StudyResourcesView(
                    resourceLinks = resourceLinksList,
                    onToggleFavorite = { viewModel.toggleResourceLinkFavorite(it) },
                    onDeleteLink = { viewModel.deleteResourceLink(it) },
                    onAddClick = { showAddDialog = true }
                )
                StudyPlannerSubTab.LMD_CALCULATOR -> LmdCalculatorView(
                    grades = gradesList,
                    viewModel = viewModel,
                    onDeleteGrade = { viewModel.deleteGrade(it) },
                    onAddClick = { showAddDialog = true }
                )
            }
        }
    }

    if (showAddDialog) {
        when (currentSubTab) {
            StudyPlannerSubTab.SCHEDULE -> AddScheduleDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    viewModel.addScheduleItem(it)
                    showAddDialog = false
                }
            )
            StudyPlannerSubTab.TASKS -> AddSemesterTaskDialog(
                onDismiss = { showAddDialog = false },
                onConfirmSemesterTask = {
                    viewModel.addSemesterTask(it)
                    showAddDialog = false
                }
            )
            StudyPlannerSubTab.EXAMS -> AddExamDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    viewModel.addExam(it)
                    showAddDialog = false
                }
            )
            StudyPlannerSubTab.ATTENDANCE -> AddAttendanceDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    viewModel.addAttendance(it)
                    showAddDialog = false
                }
            )
            StudyPlannerSubTab.IMPORTED_DOCS -> {
                showAddDialog = false
            }
            StudyPlannerSubTab.RESOURCES -> AddResourceLinkDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    viewModel.addResourceLink(it)
                    showAddDialog = false
                }
            )
            StudyPlannerSubTab.LMD_CALCULATOR -> AddGradeDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    viewModel.addOrUpdateGrade(it)
                    showAddDialog = false
                }
            )
        }
    }
}

// -------------------------------------------------------------
// 1. Schedule View
// -------------------------------------------------------------
@Composable
fun ScheduleView(
    scheduleList: List<ScheduleItem>,
    onDeleteItem: (ScheduleItem) -> Unit,
    onAddClick: () -> Unit
) {
    var selectedDay by remember { mutableStateOf(0) }
    val filteredList = scheduleList.filter { it.dayOfWeek == selectedDay }

    Column(modifier = Modifier.fillMaxSize()) {
        // Day selector chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DayNamesAr.indices.toList()) { index ->
                val isSelected = selectedDay == index
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDay = index },
                    label = { Text(DayNamesAr[index]) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.testTag("day_chip_$index")
                )
            }
        }

        if (filteredList.isEmpty()) {
            EmptyStateCard(
                title = "لا توجد حصص مسجلة ليوم ${DayNamesAr[selectedDay]}",
                subtitle = "اضغط على زر الإضافة لإدراج محاضرة، TD أو TP في جدولك الأسبوعي.",
                icon = Icons.Outlined.EventNote,
                onAddClick = onAddClick,
                buttonLabel = "إضافة حصة للجدول"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    val color = ScheduleColors.getOrElse(item.colorIndex) { ScheduleColors.first() }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("schedule_item_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.subjectName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = color.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = item.sessionType,
                                            color = color,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "${item.startTime} - ${item.endTime}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Place,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            item.roomOrAmphi,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (item.professorName.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "الأستاذ: ${item.professorName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onDeleteItem(item) },
                                modifier = Modifier.testTag("delete_schedule_${item.id}")
                            ) {
                                Icon(
                                    Icons.Outlined.DeleteOutline,
                                    contentDescription = "حذف",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Tasks View (Semester Study Planner & Deadlines)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksView(
    semesterTasks: List<SemesterTask>,
    legacyTasks: List<TaskItem>,
    onToggleSemesterTask: (SemesterTask) -> Unit,
    onDeleteSemesterTask: (SemesterTask) -> Unit,
    onToggleLegacyTask: (TaskItem) -> Unit,
    onDeleteLegacyTask: (TaskItem) -> Unit,
    onAddClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedSemester by remember { mutableIntStateOf(1) }
    var selectedStatus by remember { mutableStateOf("الكل") }
    var selectedType by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog States
    var taskToDelete by remember { mutableStateOf<SemesterTask?>(null) }
    var legacyTaskToDelete by remember { mutableStateOf<TaskItem?>(null) }
    var showExportCsvDialog by remember { mutableStateOf(false) }

    val taskTypes = listOf("الكل", "واجب TD", "تقرير TP", "مراجعة امتحان", "مشروع مصغر", "مذكرة تخرج", "قراءة مرجعية")

    // Filter tasks
    val currentSemesterTasks = semesterTasks.filter {
        (selectedSemester == 0 || it.semester == selectedSemester)
    }

    val filteredSemesterTasks = currentSemesterTasks.filter { task ->
        val matchesStatus = when (selectedStatus) {
            "قيد الإنجاز" -> !task.isCompleted
            "المكتملة" -> task.isCompleted
            else -> true
        }
        val matchesType = selectedType == "الكل" || task.taskType == selectedType
        val matchesQuery = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.moduleName.contains(searchQuery, ignoreCase = true) ||
                task.notes.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesType && matchesQuery
    }

    val totalCount = currentSemesterTasks.size
    val completedCount = currentSemesterTasks.count { it.isCompleted }
    val pendingCount = totalCount - completedCount
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    // Confirmation Dialog for Semester Task Deletion
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            icon = {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "تأكيد حذف المهمة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "هل أنت متأكد من رغبتك في حذف هذه المهمة من مخطط السداسي؟ لا يمكن التراجع عن هذا الإجراء.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "📌 ${task.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            if (task.moduleName.isNotBlank()) {
                                Text(
                                    text = "📚 المقياس: ${task.moduleName}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Text(
                                text = "⏰ موعد التسليم: ${task.deadlineDate} (${task.deadlineTime}) - سداسي ${task.semester}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSemesterTask(task)
                        taskToDelete = null
                        Toast.makeText(context, "تم حذف المهمة بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_task_btn")
                ) {
                    Text("نعم، حذف المهمة")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { taskToDelete = null },
                    modifier = Modifier.testTag("cancel_delete_task_btn")
                ) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Confirmation Dialog for Legacy Task Deletion
    legacyTaskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { legacyTaskToDelete = null },
            icon = {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text("تأكيد حذف المهمة العامة", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("هل أنت متأكد من حذف المهمة: \"${task.title}\"؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLegacyTask(task)
                        legacyTaskToDelete = null
                        Toast.makeText(context, "تم حذف المهمة بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { legacyTaskToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Export CSV Dialog
    if (showExportCsvDialog) {
        ExportTasksCsvDialog(
            semesterTasks = if (selectedSemester == 0) semesterTasks else filteredSemesterTasks,
            legacyTasks = if (selectedSemester == 0) legacyTasks else emptyList(),
            selectedSemester = selectedSemester,
            onDismiss = { showExportCsvDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Semester Selector & Actions Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "مخطط المهام والواجبات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { showExportCsvDialog = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("export_csv_btn")
                            ) {
                                Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تصدير CSV", fontSize = 11.sp)
                            }
                            Button(
                                onClick = onAddClick,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("add_task_header_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة مهمة", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSemester == 0,
                                onClick = { selectedSemester = 0 },
                                label = { Text("جميع السداسيات", fontSize = 11.sp) },
                                modifier = Modifier.testTag("semester_chip_all")
                            )
                        }
                        items(6) { idx ->
                            val sNum = idx + 1
                            FilterChip(
                                selected = selectedSemester == sNum,
                                onClick = { selectedSemester = sNum },
                                label = { Text("السداسي $sNum (S$sNum)", fontSize = 11.sp) },
                                modifier = Modifier.testTag("semester_chip_$sNum")
                            )
                        }
                    }
                }
            }
        }

        // Progress & KPI Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedSemester == 0) "نسبة الإنجاز العامة" else "نسبة إنجاز السداسي $selectedSemester",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}% مكتمل",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("إجمالي المهام", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            Text("$totalCount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("قيد الإنجاز", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            Text("$pendingCount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("تم إنجازها", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            Text("$completedCount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF059669))
                        }
                    }
                }
            }
        }

        // Search & Filters Row
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث عن واجب، مقياس، أو موعد تسليم...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Status & Type Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Status Filter
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("الكل", "قيد الإنجاز", "المكتملة").forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status, fontSize = 11.sp) },
                            leadingIcon = {
                                if (selectedStatus == status) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        )
                    }
                }

                // Type Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    items(taskTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Task Items List
        if (filteredSemesterTasks.isEmpty() && legacyTasks.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "لا توجد مهام مطابقة",
                    subtitle = "أضف واجبات الـ TD، تقارير الـ TP، ومواعيد المراجعة والمشاريع لتنظيم سداسياتك بدقة.",
                    icon = Icons.Outlined.TaskAlt,
                    onAddClick = onAddClick,
                    buttonLabel = "إضافة مهمة جديدة"
                )
            }
        } else {
            val pendingSemesterList = filteredSemesterTasks.filter { !it.isCompleted }
            val completedSemesterList = filteredSemesterTasks.filter { it.isCompleted }

            if (pendingSemesterList.isNotEmpty()) {
                item {
                    Text(
                        "المهام القادمة والمواعيد النهائية (${pendingSemesterList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                items(pendingSemesterList, key = { "sem_${it.id}" }) { task ->
                    SemesterTaskCard(
                        task = task,
                        onToggle = { onToggleSemesterTask(task) },
                        onDelete = { taskToDelete = task }
                    )
                }
            }

            if (completedSemesterList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "المهام المنجزة بنجاح (${completedSemesterList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                items(completedSemesterList, key = { "sem_${it.id}" }) { task ->
                    SemesterTaskCard(
                        task = task,
                        onToggle = { onToggleSemesterTask(task) },
                        onDelete = { taskToDelete = task }
                    )
                }
            }

            // Display Legacy tasks if any exist
            if (legacyTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "مهام عامة إضافية (${legacyTasks.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                items(legacyTasks, key = { "leg_${it.id}" }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleLegacyTask(task) },
                        onDelete = { legacyTaskToDelete = task }
                    )
                }
            }
        }
    }
}

@Composable
fun ExportTasksCsvDialog(
    semesterTasks: List<SemesterTask>,
    legacyTasks: List<TaskItem>,
    selectedSemester: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val csvData = remember(semesterTasks, legacyTasks) {
        val builder = StringBuilder()
        // UTF-8 BOM for Excel Arabic compatibility
        builder.append('\uFEFF')
        builder.append("معرف_المهمة,السداسي,عنوان_المهمة,المقياس,نوع_العمل_الأكاديمي,تاريخ_التسليم,الوقت,الأولوية,حالة_الإنجاز,الساعات_المقدرة,ملاحظات\n")
        
        for (task in semesterTasks) {
            val status = if (task.isCompleted) "مكتمل" else "قيد الإنجاز"
            val title = task.title.replace("\"", "\"\"")
            val module = task.moduleName.replace("\"", "\"\"")
            val notes = task.notes.replace("\"", "\"\"")
            builder.append("${task.id},\"S${task.semester}\",\"$title\",\"$module\",\"${task.taskType}\",\"${task.deadlineDate}\",\"${task.deadlineTime}\",\"${task.priority}\",\"$status\",${task.estimatedHours},\"$notes\"\n")
        }

        for (task in legacyTasks) {
            val status = if (task.isDone) "مكتمل" else "قيد الإنجاز"
            val title = task.title.replace("\"", "\"\"")
            val subject = task.subjectName.replace("\"", "\"\"")
            val category = task.category.replace("\"", "\"\"")
            builder.append("${task.id},\"عام\",\"$title\",\"$subject\",\"$category\",\"${task.dueDate}\",\"23:59\",\"${task.priority}\",\"$status\",0.0,\"\"\n")
        }

        builder.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.FileDownload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedSemester == 0) "تصدير جميع المهام (CSV)" else "تصدير مهام السداسي $selectedSemester (CSV)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تم توليد ملف بتنسيق CSV متوافق تماماً مع Microsoft Excel و Google Sheets باللغة العربية. يتضمن ${semesterTasks.size + legacyTasks.size} مهمة.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = csvData,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(csvData))
                        Toast.makeText(context, "تم نسخ محتوى CSV إلى الحافظة", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_csv_btn")
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "مهام_السداسي_StudentProDZ.csv")
                            putExtra(Intent.EXTRA_TEXT, csvData)
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "مشاركة مهام السداسي CSV")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.testTag("share_csv_btn")
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
fun SemesterTaskCard(
    task: SemesterTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "عالي" -> MaterialTheme.colorScheme.error
        "متوسط" -> Color(0xFFD97706) // Warm Amber
        else -> Color(0xFF059669) // Emerald Green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("semester_task_item_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.testTag("semester_task_checkbox_${task.id}")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                    )

                    if (task.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = task.notes,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "سداسي ${task.semester}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = task.taskType,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = priorityColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "أولوية: ${task.priority}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (task.moduleName.isNotBlank()) {
                            Text(
                                text = "📚 ${task.moduleName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "⏰ ${task.deadlineDate} (${task.deadlineTime})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                        )
                        if (task.estimatedHours > 0) {
                            Text(
                                text = "⏱️ ${task.estimatedHours} سا",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isDone) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("task_checkbox_${task.id}")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (task.isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (task.subjectName.isNotBlank()) {
                        Text(
                            text = task.subjectName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (task.dueDate.isNotBlank()) {
                        Text(
                            text = "📅 ${task.dueDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Exams View
// -------------------------------------------------------------
@Composable
fun ExamsView(
    exams: List<ExamItem>,
    onToggleComplete: (ExamItem) -> Unit,
    onDeleteExam: (ExamItem) -> Unit,
    onAddClick: () -> Unit
) {
    if (exams.isEmpty()) {
        EmptyStateCard(
            title = "لا توجد امتحانات مسجلة",
            subtitle = "أضف تواريخ وتوقيت امتحانات السداسي والاستدراك مع المعاملات والمدرجات.",
            icon = Icons.Outlined.Grading,
            onAddClick = onAddClick,
            buttonLabel = "إضافة امتحان"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "مخطط الامتحانات الرسمية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = onAddClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة امتحان", fontSize = 12.sp)
                    }
                }
            }

            items(exams, key = { it.id }) { exam ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_card_${exam.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (exam.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exam.subjectName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = if (exam.isCompleted) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "المعامل: ${exam.coeff}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (exam.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(exam.examDate, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(exam.examTime, fontSize = 13.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Room, contentDescription = null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(exam.roomOrAmphi, fontSize = 13.sp)
                            }
                        }
                        if (exam.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "ملاحظات: ${exam.notes}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { onToggleComplete(exam) }) {
                                Icon(
                                    if (exam.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = if (exam.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (exam.isCompleted) "تم اجتيازه بنجاح" else "تعيين كمجتاز")
                            }
                            IconButton(onClick = { onDeleteExam(exam) }) {
                                Icon(Icons.Outlined.DeleteOutline, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. Attendance View
// -------------------------------------------------------------
@Composable
fun AttendanceView(
    records: List<AttendanceRecord>,
    onIncrementAbsence: (AttendanceRecord) -> Unit,
    onDecrementAbsence: (AttendanceRecord) -> Unit,
    onIncrementExcused: (AttendanceRecord) -> Unit,
    onDeleteRecord: (AttendanceRecord) -> Unit,
    onAddClick: () -> Unit
) {
    if (records.isEmpty()) {
        EmptyStateCard(
            title = "متابع الغيابات والحضور",
            subtitle = "سجل مقاييس الـ TD والـ TP لتتبع عدد الغيابات وتجنب عقوبة الإقصاء (Exclusion).",
            icon = Icons.Outlined.HowToReg,
            onAddClick = onAddClick,
            buttonLabel = "إضافة مقياس للمتابعة"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "تنبيه قانوني جامعي: 3 غيابات غير مبررة أو 5 غيابات مبررة تؤدي للإقصاء من المقياس في نظام LMD.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            items(records, key = { it.id }) { record ->
                val isNearExclusion = record.currentAbsences >= record.maxAllowedAbsences - 1
                val isExcluded = record.currentAbsences >= record.maxAllowedAbsences

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("attendance_item_${record.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isExcluded -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            isNearExclusion -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.subjectName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "نوع الحصة: ${record.sessionType} | أقصى غياب مسموح: ${record.maxAllowedAbsences}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = when {
                                    isExcluded -> MaterialTheme.colorScheme.error
                                    isNearExclusion -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = when {
                                        isExcluded -> "⚠️ مقصى (Exclu)"
                                        isNearExclusion -> "⚠️ تحذير إقصاء"
                                        else -> "وضع سليم"
                                    },
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "الغيابات غير المبررة: ${record.currentAbsences} / ${record.maxAllowedAbsences}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isExcluded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "الغيابات المبررة: ${record.excusedAbsences}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledTonalIconButton(
                                    onClick = { onDecrementAbsence(record) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "إنقاص غياب", modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { onIncrementAbsence(record) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isNearExclusion) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تسجيل غياب", fontSize = 12.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { onIncrementExcused(record) }) {
                                Text("+ إضافة غياب مبرر", fontSize = 12.sp)
                            }
                            IconButton(onClick = { onDeleteRecord(record) }) {
                                Icon(Icons.Outlined.DeleteOutline, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. LMD Grade & GPA Calculator
// -------------------------------------------------------------
@Composable
fun LmdCalculatorView(
    grades: List<ModuleGradeItem>,
    viewModel: StudentProViewModel,
    onDeleteGrade: (ModuleGradeItem) -> Unit,
    onAddClick: () -> Unit
) {
    var selectedSemester by remember { mutableStateOf(1) }
    val s1Calc = viewModel.calculateSemester(1, grades)
    val s2Calc = viewModel.calculateSemester(2, grades)
    val annualCalc = viewModel.calculateAnnual(s1Calc, s2Calc)

    val currentSemesterGrades = grades.filter { it.semester == selectedSemester }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Overall Annual Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lmd_annual_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "المعدل العام السنوي (Moyenne Annuelle)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Surface(
                            color = if (annualCalc.isPassed) Color(0xFF059669) else Color(0xFFDC2626),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (annualCalc.isPassed) "مقبول / ناجح" else "دورة الاستدراك",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = String.format("%.2f / 20", annualCalc.annualAverage),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "مجموع الأرصدة: ${annualCalc.totalCredits} / 60 رصيداً",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("سداسي 1: ${String.format("%.2f", s1Calc.semesterAverage)} (${s1Calc.totalAcquiredCredits} رصيد)", fontSize = 12.sp)
                            Text("سداسي 2: ${String.format("%.2f", s2Calc.semesterAverage)} (${s2Calc.totalAcquiredCredits} رصيد)", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = annualCalc.statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Semester Tab Selector & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedSemester == 1,
                        onClick = { selectedSemester = 1 },
                        label = { Text("السداسي الأول (S1)") },
                        modifier = Modifier.testTag("s1_chip")
                    )
                    FilterChip(
                        selected = selectedSemester == 2,
                        onClick = { selectedSemester = 2 },
                        label = { Text("السداسي الثاني (S2)") },
                        modifier = Modifier.testTag("s2_chip")
                    )
                }
                Button(
                    onClick = onAddClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_grade_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة مقياس", fontSize = 12.sp)
                }
            }
        }

        if (currentSemesterGrades.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "لا توجد مقاييس مسجلة في السداسي $selectedSemester",
                    subtitle = "أدخل أسماء المواد والمعاملات وعلامات الامتحان و TD/TP لحساب معدل السداسي فورياً.",
                    icon = Icons.Outlined.Calculate,
                    onAddClick = onAddClick,
                    buttonLabel = "إضافة مادة للـ S$selectedSemester"
                )
            }
        } else {
            items(currentSemesterGrades, key = { it.id }) { grade ->
                val modAverage = viewModel.calculateModuleAverage(grade)
                val isModPassed = modAverage >= 10.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("module_grade_card_${grade.id}"),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = grade.moduleName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "المعامل: ${grade.coeff} | الأرصدة: ${grade.credit}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format("%.2f / 20", modAverage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isModPassed) Color(0xFF059669) else Color(0xFFDC2626)
                                )
                                Text(
                                    text = if (isModPassed) "مكتسب (${grade.credit} رصيد)" else "غير مستوفى",
                                    fontSize = 11.sp,
                                    color = if (isModPassed) Color(0xFF059669) else Color(0xFFDC2626)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("الامتحان (60%): ${grade.examGrade}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("الـ TD (40%): ${grade.tdGrade}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (grade.hasTp) {
                                Text("الـ TP: ${grade.tpGrade}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { onDeleteGrade(grade) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Outlined.DeleteOutline, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Helper Empty State Card
// -------------------------------------------------------------
@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onAddClick: () -> Unit,
    buttonLabel: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(buttonLabel)
            }
        }
    }
}

// -------------------------------------------------------------
// Add Dialogs
// -------------------------------------------------------------
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (ScheduleItem) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var sessionType by remember { mutableStateOf("محاضرة") }
    var room by remember { mutableStateOf("") }
    var prof by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("08:30") }
    var endTime by remember { mutableStateOf("10:00") }
    var dayOfWeek by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة حصة للجدول الأسبوعي", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("اسم المقياس / المادة *") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_schedule_subject")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("محاضرة", "أعمال موجهة (TD)", "أعمال تطبيقية (TP)").forEach { type ->
                        FilterChip(
                            selected = sessionType == type,
                            onClick = { sessionType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("القاعة / المدرج / المخبر") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = prof,
                    onValueChange = { prof = it },
                    label = { Text("اسم الأستاذ") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("من") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("إلى") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onConfirm(
                            ScheduleItem(
                                dayOfWeek = dayOfWeek,
                                subjectName = subject,
                                sessionType = sessionType,
                                roomOrAmphi = room.ifBlank { "قاعة التدريس" },
                                professorName = prof,
                                startTime = startTime,
                                endTime = endTime
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("dialog_schedule_save")
            ) {
                Text("حفظ الحصة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddSemesterTaskDialog(
    onDismiss: () -> Unit,
    onConfirmSemesterTask: (SemesterTask) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var moduleName by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf("واجب TD") }
    var semester by remember { mutableIntStateOf(1) }
    var deadlineDate by remember { mutableStateOf("2026-04-15") }
    var deadlineTime by remember { mutableStateOf("23:59") }
    var priority by remember { mutableStateOf("متوسط") }
    var estimatedHoursText by remember { mutableStateOf("2.0") }
    var notes by remember { mutableStateOf("") }

    val taskTypes = listOf("واجب TD", "تقرير TP", "مراجعة امتحان", "مشروع مصغر", "مذكرة تخرج", "قراءة مرجعية")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مهمة / موعد تسليم للسداسي", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("السداسي المعني:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(6) { idx ->
                            val sNum = idx + 1
                            FilterChip(
                                selected = semester == sNum,
                                onClick = { semester = sNum },
                                label = { Text("S$sNum", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان المهمة أو المطلوب *") },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_task_title")
                    )
                }

                item {
                    OutlinedTextField(
                        value = moduleName,
                        onValueChange = { moduleName = it },
                        label = { Text("المقياس / المادة الأكاديمية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("نوع العمل الأكاديمي:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(taskTypes) { cat ->
                            FilterChip(
                                selected = taskType == cat,
                                onClick = { taskType = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = deadlineDate,
                            onValueChange = { deadlineDate = it },
                            label = { Text("تاريخ التسليم") },
                            placeholder = { Text("YYYY-MM-DD") },
                            modifier = Modifier.weight(1.3f)
                        )
                        OutlinedTextField(
                            value = deadlineTime,
                            onValueChange = { deadlineTime = it },
                            label = { Text("الوقت") },
                            placeholder = { Text("HH:mm") },
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }

                item {
                    Text("درجة الأولوية:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("عالي", "متوسط", "منخفض").forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = estimatedHoursText,
                        onValueChange = { estimatedHoursText = it },
                        label = { Text("الوقت المقدر للإنجاز (بالساعات)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات إضافية / تعليمات الأستاذ") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val hours = estimatedHoursText.toDoubleOrNull() ?: 2.0
                        onConfirmSemesterTask(
                            SemesterTask(
                                semester = semester,
                                title = title.trim(),
                                moduleName = moduleName.trim(),
                                taskType = taskType,
                                deadlineDate = deadlineDate.trim(),
                                deadlineTime = deadlineTime.trim(),
                                priority = priority,
                                estimatedHours = hours,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("dialog_task_save")
            ) {
                Text("حفظ المهمة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (TaskItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("واجب TD") }
    var dueDate by remember { mutableStateOf("2026-04-15") }
    var priority by remember { mutableStateOf("عالي") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مهمة / واجب أكاديمي", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المهمة / المطلوب *") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_task_title")
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("المقياس / المادة") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("واجب TD", "تقرير TP", "مراجعة", "مذكرة").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("تاريخ التسليم (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            TaskItem(
                                title = title,
                                subjectName = subject,
                                category = category,
                                dueDate = dueDate,
                                priority = priority
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("dialog_task_save")
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddExamDialog(
    onDismiss: () -> Unit,
    onConfirm: (ExamItem) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-05-20") }
    var time by remember { mutableStateOf("09:00") }
    var room by remember { mutableStateOf("مدرج 1") }
    var coeff by remember { mutableStateOf("2.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة امتحان رسمي", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("اسم المقياس *") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_exam_subject")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("التاريخ") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("التوقيت") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("المدرج / القاعة") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = coeff,
                        onValueChange = { coeff = it },
                        label = { Text("المعامل") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات المراجعة") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onConfirm(
                            ExamItem(
                                subjectName = subject,
                                examDate = date,
                                examTime = time,
                                roomOrAmphi = room,
                                coeff = coeff.toDoubleOrNull() ?: 2.0,
                                notes = notes
                            )
                        )
                    }
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddAttendanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (AttendanceRecord) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var sessionType by remember { mutableStateOf("TD") }
    var maxAllowed by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مقياس لمتابعة الغيابات", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("اسم المقياس والحصة *") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_att_subject")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = sessionType == "TD",
                        onClick = { sessionType = "TD" },
                        label = { Text("أعمال موجهة TD") }
                    )
                    FilterChip(
                        selected = sessionType == "TP",
                        onClick = { sessionType = "TP" },
                        label = { Text("أعمال تطبيقية TP") }
                    )
                }
                OutlinedTextField(
                    value = maxAllowed,
                    onValueChange = { maxAllowed = it },
                    label = { Text("أقصى غياب مسموح به (عادة 3)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onConfirm(
                            AttendanceRecord(
                                subjectName = subject,
                                sessionType = sessionType,
                                maxAllowedAbsences = maxAllowed.toIntOrNull() ?: 3
                            )
                        )
                    }
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddGradeDialog(
    onDismiss: () -> Unit,
    onConfirm: (ModuleGradeItem) -> Unit
) {
    var moduleName by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf(1) }
    var coeff by remember { mutableStateOf("2.0") }
    var credit by remember { mutableStateOf("4") }
    var examGrade by remember { mutableStateOf("12.0") }
    var tdGrade by remember { mutableStateOf("13.0") }
    var hasTp by remember { mutableStateOf(false) }
    var tpGrade by remember { mutableStateOf("14.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مادة لحاسبة LMD", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = moduleName,
                    onValueChange = { moduleName = it },
                    label = { Text("اسم المادة / المقياس *") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_grade_module")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = semester == 1,
                        onClick = { semester = 1 },
                        label = { Text("السداسي S1") }
                    )
                    FilterChip(
                        selected = semester == 2,
                        onClick = { semester = 2 },
                        label = { Text("السداسي S2") }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = coeff,
                        onValueChange = { coeff = it },
                        label = { Text("المعامل") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = credit,
                        onValueChange = { credit = it },
                        label = { Text("الأرصدة") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = examGrade,
                        onValueChange = { examGrade = it },
                        label = { Text("الامتحان /20") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tdGrade,
                        onValueChange = { tdGrade = it },
                        label = { Text("الـ TD /20") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasTp, onCheckedChange = { hasTp = it })
                    Text("تحتوي على أعمال تطبيقية (TP)", fontSize = 13.sp)
                }
                if (hasTp) {
                    OutlinedTextField(
                        value = tpGrade,
                        onValueChange = { tpGrade = it },
                        label = { Text("علامة الـ TP /20") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (moduleName.isNotBlank()) {
                        onConfirm(
                            ModuleGradeItem(
                                semester = semester,
                                moduleName = moduleName,
                                coeff = coeff.toDoubleOrNull() ?: 2.0,
                                credit = credit.toIntOrNull() ?: 4,
                                examGrade = examGrade.toDoubleOrNull() ?: 10.0,
                                tdGrade = tdGrade.toDoubleOrNull() ?: 10.0,
                                hasTp = hasTp,
                                tpGrade = if (hasTp) tpGrade.toDoubleOrNull() ?: 10.0 else 0.0
                            )
                        )
                    }
                }
            ) {
                Text("حفظ المادة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

// -------------------------------------------------------------
// 6. Resources & Portals View (البوابات والمصادر الأكاديمية)
// -------------------------------------------------------------
@Composable
fun StudyResourcesView(
    resourceLinks: List<StudyResourceLink>,
    onToggleFavorite: (StudyResourceLink) -> Unit,
    onDeleteLink: (StudyResourceLink) -> Unit,
    onAddClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedCategory by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    var linkToDelete by remember { mutableStateOf<StudyResourceLink?>(null) }

    val categories = listOf(
        "الكل",
        "المفضلة ⭐",
        "بوابات جامعية",
        "منصات ومكتبات",
        "أدوات ومواقع بحثية",
        "مواد دراسية وملخصات"
    )

    val filteredLinks = resourceLinks.filter { link ->
        val matchesCategory = when (selectedCategory) {
            "الكل" -> true
            "المفضلة ⭐" -> link.isFavorite
            else -> link.category == selectedCategory
        }
        val matchesQuery = searchQuery.isBlank() ||
                link.title.contains(searchQuery, ignoreCase = true) ||
                link.description.contains(searchQuery, ignoreCase = true) ||
                link.url.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    // Delete confirmation dialog
    linkToDelete?.let { link ->
        AlertDialog(
            onDismissRequest = { linkToDelete = null },
            icon = {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("تأكيد حذف الرابط", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف الرابط \"${link.title}\"؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLink(link)
                        linkToDelete = null
                        Toast.makeText(context, "تم حذف الرابط بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { linkToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search & Add Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث في البوابات والمصادر...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (searchQuery.isNotBlank()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح")
                        }
                    }
                } else null,
                modifier = Modifier
                    .weight(1f)
                    .testTag("resource_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = onAddClick,
                modifier = Modifier.testTag("add_resource_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    modifier = Modifier.testTag("category_chip_$cat")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredLinks.isEmpty()) {
            EmptyStateCard(
                title = "لا توجد روابط أو بوابات",
                subtitle = if (searchQuery.isNotBlank()) "لا توجد نتائج تطابق بحثك الحالي." else "أضف بواباتك الجامعية المفضلة، منصات التعليم، والمكتبات الرقمية لسهولة الوصول إليها.",
                icon = Icons.Outlined.Public,
                onAddClick = onAddClick,
                buttonLabel = "إضافة بوابة / رابط جديد"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredLinks, key = { it.id }) { link ->
                    ResourceLinkCard(
                        link = link,
                        onToggleFavorite = { onToggleFavorite(link) },
                        onDelete = { linkToDelete = link },
                        onOpen = {
                            try {
                                val url = if (link.url.startsWith("http://") || link.url.startsWith("https://")) {
                                    link.url
                                } else {
                                    "https://${link.url}"
                                }
                                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCopyUrl = {
                            clipboardManager.setText(AnnotatedString(link.url))
                            Toast.makeText(context, "تم نسخ الرابط إلى الحافظة", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ResourceLinkCard(
    link: StudyResourceLink,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
    onCopyUrl: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("resource_card_${link.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = link.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = link.category,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("fav_btn_${link.id}")
                    ) {
                        Icon(
                            imageVector = if (link.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "تفضيل",
                            tint = if (link.isFavorite) Color(0xFFE5A000) else MaterialTheme.colorScheme.outline
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_link_${link.id}")
                    ) {
                        Icon(
                            Icons.Outlined.DeleteOutline,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (link.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = link.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = link.url,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = onCopyUrl,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نسخ", fontSize = 11.sp)
                    }
                    Button(
                        onClick = onOpen,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فتح الرابط", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddResourceLinkDialog(
    onDismiss: () -> Unit,
    onConfirm: (StudyResourceLink) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("بوابات جامعية") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("بوابات جامعية", "منصات ومكتبات", "أدوات ومواقع بحثية", "مواد دراسية وملخصات")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة بوابة أو مصدر أكاديمي", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم البوابة / الموقع *") },
                    placeholder = { Text("مثال: منصة بروغرس، SNDL...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_link_title")
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("رابط الموقع الإلكتروني (URL) *") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_link_url")
                )
                Text("التصنيف:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف مختصر (اختياري)") },
                    placeholder = { Text("ملاحظات حول كيفية الاستخدام والخدمات...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && url.isNotBlank()) {
                        onConfirm(
                            StudyResourceLink(
                                title = title.trim(),
                                url = url.trim(),
                                category = category,
                                description = description.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("dialog_confirm_link_btn")
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

