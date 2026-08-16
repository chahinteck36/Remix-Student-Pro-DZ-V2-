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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.content.AcademicCurriculumData
import com.example.data.content.SpecialtyPreset
import com.example.data.local.ModuleGradeItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicProgressScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val grades by viewModel.gradeItems.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val selectedYear by viewModel.selectedAcademicYear.collectAsState()
    val selectedSemester by viewModel.selectedAcademicSemester.collectAsState()
    val targetGpa by viewModel.targetGpa.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingGradeItem by remember { mutableStateOf<ModuleGradeItem?>(null) }
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showTranscriptDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<ModuleGradeItem?>(null) }
    var showClearSemesterDialog by remember { mutableStateOf(false) }

    val cumulativeGpa = remember(grades) { viewModel.calculateCumulativeGpa(grades) }
    val totalAcquiredCredits = remember(grades) { viewModel.calculateTotalAcquiredCredits(grades) }
    val (academicMention, mentionDesc) = remember(cumulativeGpa) {
        AcademicCurriculumData.getAcademicMention(cumulativeGpa)
    }
    val (lmdCategory, categoryDesc) = remember(cumulativeGpa) {
        AcademicCurriculumData.getLmdCategory(cumulativeGpa)
    }

    // Calculation for selected semester
    val currentSemesterCalc = remember(selectedSemester, grades) {
        viewModel.calculateSemester(selectedSemester, grades)
    }

    // Paired semester for annual calculation (e.g. S1 & S2 for Year 1, S3 & S4 for Year 2, etc.)
    val baseSemester = ((selectedYear - 1) * 2) + 1
    val siblingSemester = baseSemester + 1
    val sA = remember(baseSemester, grades) { viewModel.calculateSemester(baseSemester, grades) }
    val sB = remember(siblingSemester, grades) { viewModel.calculateSemester(siblingSemester, grades) }
    val annualCalc = remember(sA, sB) { viewModel.calculateAnnual(sA, sB) }

    val filteredGrades = remember(selectedSemester, grades) {
        grades.filter { it.semester == selectedSemester }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingGradeItem = null
                    showAddEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة مقياس جديد")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إضافة مقياس (S$selectedSemester)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Bar
            item {
                AcademicHeader(
                    onOpenPresets = { showPresetsDialog = true },
                    onExportTranscript = { showTranscriptDialog = true }
                )
            }

            // Cumulative GPA & Honors Hero Card
            item {
                AcademicHeroCard(
                    cumulativeGpa = cumulativeGpa,
                    academicMention = academicMention,
                    mentionDesc = mentionDesc,
                    lmdCategory = lmdCategory,
                    totalAcquiredCredits = totalAcquiredCredits,
                    totalSemestersTracked = grades.map { it.semester }.distinct().size
                )
            }

            // Tenure Progression Chart & Timeline
            item {
                TenureProgressionVisualizer(
                    allGrades = grades,
                    selectedSemester = selectedSemester,
                    onSelectSemester = { sem ->
                        val year = ((sem - 1) / 2) + 1
                        viewModel.setSelectedAcademicYear(year)
                        viewModel.setSelectedAcademicSemester(sem)
                    }
                )
            }

            // Academic Year & Semester Selector
            item {
                AcademicYearAndSemesterSelector(
                    selectedYear = selectedYear,
                    selectedSemester = selectedSemester,
                    onYearSelected = { year -> viewModel.setSelectedAcademicYear(year) },
                    onSemesterSelected = { sem -> viewModel.setSelectedAcademicSemester(sem) },
                    annualCalc = annualCalc,
                    currentSemesterCalc = currentSemesterCalc
                )
            }

            // Semester Header and Action bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مقاييس السداسي $selectedSemester (S$selectedSemester)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${filteredGrades.size} مقاييس مسجلة • المجموع: ${String.format("%.1f", currentSemesterCalc.totalCoeff)} معاملات",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (filteredGrades.isNotEmpty()) {
                        TextButton(
                            onClick = { showClearSemesterDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Outlined.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مسح السداسي", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Subject Cards List
            if (filteredGrades.isEmpty()) {
                item {
                    EmptySemesterSubjectsCard(
                        semesterNum = selectedSemester,
                        onAddSubject = {
                            editingGradeItem = null
                            showAddEditDialog = true
                        },
                        onOpenPresets = { showPresetsDialog = true }
                    )
                }
            } else {
                items(filteredGrades, key = { it.id }) { gradeItem ->
                    SubjectGradeCard(
                        gradeItem = gradeItem,
                        calculatedAverage = viewModel.calculateModuleAverage(gradeItem),
                        onEdit = {
                            editingGradeItem = gradeItem
                            showAddEditDialog = true
                        },
                        onDelete = {
                            showDeleteConfirmDialog = gradeItem
                        },
                        onToggleRattrapage = { isUsed, rattrapageScore ->
                            viewModel.addOrUpdateGrade(
                                gradeItem.copy(
                                    isRattrapageUsed = isUsed,
                                    rattrapageGrade = rattrapageScore
                                )
                            )
                        }
                    )
                }
            }

            // Target GPA & Goal Simulator Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TargetGpaSimulatorCard(
                    cumulativeGpa = cumulativeGpa,
                    completedSemesters = grades.map { it.semester }.distinct().size,
                    targetGpa = targetGpa,
                    onTargetGpaChange = { viewModel.setTargetGpa(it) }
                )
            }

            // Algerian University LMD Regulation Tips Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                LmdRegulationInfoCard()
            }
        }
    }

    // Add / Edit Subject Dialog
    if (showAddEditDialog) {
        AddEditSubjectDialog(
            initialItem = editingGradeItem,
            defaultAcademicYear = selectedYear,
            defaultSemester = selectedSemester,
            onDismiss = { showAddEditDialog = false },
            onSave = { updatedItem ->
                viewModel.addOrUpdateGrade(updatedItem)
                showAddEditDialog = false
            }
        )
    }

    // Specialty Curriculum Presets Dialog
    if (showPresetsDialog) {
        SpecialtyPresetsDialog(
            presets = AcademicCurriculumData.presets,
            onDismiss = { showPresetsDialog = false },
            onSelectPreset = { preset ->
                viewModel.applySpecialtyPreset(preset)
                showPresetsDialog = false
            }
        )
    }

    // Academic Transcript Dialog
    if (showTranscriptDialog) {
        val transcriptText = remember(userProfile, grades) {
            viewModel.generateTranscriptSummary(userProfile, grades)
        }
        AcademicTranscriptDialog(
            transcriptText = transcriptText,
            onDismiss = { showTranscriptDialog = false },
            onCopy = { viewModel.copyToClipboard(context, transcriptText, "كشف المسار الأكاديمي") },
            onShare = { viewModel.shareText(context, transcriptText, "كشف المسار الأكاديمي الشامل") }
        )
    }

    // Delete Single Subject Confirm Dialog
    showDeleteConfirmDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("حذف المقياس") },
            text = { Text("هل أنت متأكد من حذف مقياس \"${item.moduleName}\" من السداسي ${item.semester}؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGrade(item)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Clear Semester Dialog
    if (showClearSemesterDialog) {
        AlertDialog(
            onDismissRequest = { showClearSemesterDialog = false },
            title = { Text("مسح مقاييس السداسي $selectedSemester") },
            text = { Text("سيتم حذف جميع علامات ومقاييس السداسي $selectedSemester نهائياً.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSemesterGrades(selectedSemester)
                        showClearSemesterDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("مسح الكل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearSemesterDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// ---------------------- Sub-composables ----------------------

@Composable
private fun AcademicHeader(
    onOpenPresets: () -> Unit,
    onExportTranscript: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "التقدم الأكاديمي والمسار الجامعي",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "متابعة السداسيات، الأرصدة، والمعدل التراكمي LMD",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onOpenPresets,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        Icons.Outlined.AutoStories,
                        contentDescription = "نماذج التخصصات",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onExportTranscript,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = "تصدير كشف المسار",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun AcademicHeroCard(
    cumulativeGpa: Double,
    academicMention: String,
    mentionDesc: String,
    lmdCategory: String,
    totalAcquiredCredits: Int,
    totalSemestersTracked: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المعدل التراكمي العام (Cumulative GPA)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (cumulativeGpa > 0) String.format("%.2f", cumulativeGpa) else "--.--",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (cumulativeGpa >= 10.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = " / 20.00",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                        )
                    }
                }

                // Honors Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (cumulativeGpa >= 14.0) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (cumulativeGpa >= 14.0) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onPrimaryContainer,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (cumulativeGpa >= 14.0) Icons.Default.EmojiEvents else Icons.Default.Stars,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = academicMention.split(" ").firstOrNull() ?: "مستوفى",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "التقدير الجامعي",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = academicMention,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "تصنيف دفعة LMD",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = lmdCategory.split(" - ").firstOrNull() ?: lmdCategory,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Credits Progress Bar
            val targetLicenceCredits = 180f
            val creditsProgress = (totalAcquiredCredits / targetLicenceCredits).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأرصدة المحصلة (ECTS)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$totalAcquiredCredits / 180 رصيداً (${(creditsProgress * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { creditsProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "السداسيات المسجلة: $totalSemestersTracked من 6",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (totalAcquiredCredits >= 180) "✅ استيفاء شهادة الليسانس" else "قيد التحصيل",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (totalAcquiredCredits >= 180) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TenureProgressionVisualizer(
    allGrades: List<ModuleGradeItem>,
    selectedSemester: Int,
    onSelectSemester: (Int) -> Unit
) {
    val semesters = (1..6).toList() // L1, L2, L3 standard 6 semesters

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مخطط التطور عبر السداسيات (S1 - S6)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "عتبة النجاح: 10.00",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                semesters.forEach { sem ->
                    val semGrades = allGrades.filter { it.semester == sem }
                    val hasData = semGrades.isNotEmpty()
                    val totalCoeff = semGrades.sumOf { it.coeff }
                    val weightedSum = semGrades.sumOf {
                        val cc = if (it.hasTp) (it.tdGrade + it.tpGrade) / 2.0 else it.tdGrade
                        val effectiveExam = if (it.isRattrapageUsed && it.rattrapageGrade > 0) maxOf(it.examGrade, it.rattrapageGrade) else it.examGrade
                        ((effectiveExam * it.examWeight) + (cc * (1.0 - it.examWeight))) * it.coeff
                    }
                    val gpa = if (totalCoeff > 0) weightedSum / totalCoeff else 0.0
                    val isPassed = gpa >= 10.0
                    val isSelected = sem == selectedSemester

                    val barHeightFraction = (gpa / 20.0).toFloat().coerceIn(0.1f, 1f)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelectSemester(sem) }
                            .padding(horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (hasData) {
                            Text(
                                text = String.format("%.1f", gpa),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(if (hasData) barHeightFraction * 0.75f else 0.08f)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    when {
                                        !hasData -> MaterialTheme.colorScheme.surfaceVariant
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isPassed -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.errorContainer
                                    }
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                    ) else Modifier
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "S$sem",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AcademicYearAndSemesterSelector(
    selectedYear: Int,
    selectedSemester: Int,
    onYearSelected: (Int) -> Unit,
    onSemesterSelected: (Int) -> Unit,
    annualCalc: com.example.ui.viewmodel.LmdAnnualCalculation,
    currentSemesterCalc: com.example.ui.viewmodel.LmdSemesterCalculation
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Academic Years (L1, L2, L3, M1, M2)
        Text(
            text = "السنة الجامعية والمستوى",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        val years = listOf(
            1 to "السنة 1 (L1)",
            2 to "السنة 2 (L2)",
            3 to "السنة 3 (L3)",
            4 to "ماستر 1 (M1)",
            5 to "ماستر 2 (M2)"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(years) { (yearNum, yearLabel) ->
                val isSelected = selectedYear == yearNum
                FilterChip(
                    selected = isSelected,
                    onClick = { onYearSelected(yearNum) },
                    label = { Text(yearLabel, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Semesters within that year
        val firstSem = ((selectedYear - 1) * 2) + 1
        val secondSem = firstSem + 1

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SemesterSelectTab(
                title = "السداسي $firstSem (S$firstSem)",
                isSelected = selectedSemester == firstSem,
                onClick = { onSemesterSelected(firstSem) },
                modifier = Modifier.weight(1f)
            )
            SemesterSelectTab(
                title = "السداسي $secondSem (S$secondSem)",
                isSelected = selectedSemester == secondSem,
                onClick = { onSemesterSelected(secondSem) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Selected Semester & Annual Calculation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "معدل السداسي S$selectedSemester",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (currentSemesterCalc.totalCoeff > 0) String.format("%.2f", currentSemesterCalc.semesterAverage) else "--.--",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (currentSemesterCalc.isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = " / 20.00",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                        )
                    }
                    Text(
                        text = "${currentSemesterCalc.totalAcquiredCredits}/30 رصيداً • ${if (currentSemesterCalc.isPassed) "مستوفى بالتعويض" else "غير مستوفى"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentSemesterCalc.isPassed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "المعدل السنوي (L$selectedYear)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (annualCalc.annualAverage > 0) "${String.format("%.2f", annualCalc.annualAverage)} / 20" else "--.--",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (annualCalc.isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (annualCalc.annualAverage > 0) (if (annualCalc.isPassed) "مقبول وناجح (Admis)" else "دورة استدراك") else "في انتظار اكتمال العلامات",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (annualCalc.isPassed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun SemesterSelectTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SubjectGradeCard(
    gradeItem: ModuleGradeItem,
    calculatedAverage: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleRattrapage: (Boolean, Double) -> Unit
) {
    val isPassed = calculatedAverage >= 10.0
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = gradeItem.unitType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = gradeItem.moduleName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "المعامل: ${gradeItem.coeff}  •  الأرصدة: ${gradeItem.credit} ECTS  •  وزن الامتحان: ${(gradeItem.examWeight * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Average Badge
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPassed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        contentColor = if (isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = String.format("%.2f", calculatedAverage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isPassed) "مستوفى" else "تحت 10",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grades Breakdown Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradePill(label = "الامتحان", value = gradeItem.examGrade, modifier = Modifier.weight(1f))
                GradePill(label = "الأعمال الموجهة (TD)", value = gradeItem.tdGrade, modifier = Modifier.weight(1f))
                if (gradeItem.hasTp) {
                    GradePill(label = "الأعمال التطبيقية (TP)", value = gradeItem.tpGrade, modifier = Modifier.weight(1f))
                }
                if (gradeItem.isRattrapageUsed && gradeItem.rattrapageGrade > 0) {
                    GradePill(label = "الاستدراك", value = gradeItem.rattrapageGrade, isHighlight = true, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rattrapage Quick Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onToggleRattrapage(!gradeItem.isRattrapageUsed, gradeItem.rattrapageGrade)
                    }
                ) {
                    Checkbox(
                        checked = gradeItem.isRattrapageUsed,
                        onCheckedChange = { onToggleRattrapage(it, gradeItem.rattrapageGrade) },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تطبيق الاستدراك",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "تعديل المقياس",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "حذف المقياس",
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
private fun GradePill(
    label: String,
    value: Double,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isHighlight) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${String.format("%.2f", value)} / 20",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptySemesterSubjectsCard(
    semesterNum: Int,
    onAddSubject: () -> Unit,
    onOpenPresets: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
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
                Icons.Outlined.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "لا توجد مقاييس مضافة في السداسي $semesterNum حتى الآن",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "أضف مقاييسك يدوياً أو قم بتحميل نموذج تخصصك الجامعي بضغطة زر",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenPresets) {
                    Icon(Icons.Outlined.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نماذج التخصصات")
                }
                Button(onClick = onAddSubject) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة مقياس")
                }
            }
        }
    }
}

@Composable
private fun TargetGpaSimulatorCard(
    cumulativeGpa: Double,
    completedSemesters: Int,
    targetGpa: Double,
    onTargetGpaChange: (Double) -> Unit
) {
    val totalSemesters = 6 // Licence
    val remainingSemesters = (totalSemesters - completedSemesters).coerceAtLeast(1)

    // Formula: Required = (Target * Total - Current * Completed) / Remaining
    val currentPoints = (cumulativeGpa.takeIf { it > 0 } ?: 10.0) * completedSemesters
    val requiredFutureGpa = ((targetGpa * totalSemesters) - currentPoints) / remainingSemesters

    val isAchievable = requiredFutureGpa in 0.0..20.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Calculate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حاسبة الهدف ومحاكاة التخرج",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "اختر المعدل التراكمي المستهدف لشهادة الليسانس / الماستر:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Target GPA selection chips
            val presetTargets = listOf(12.0, 13.0, 14.0, 15.0, 16.0)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presetTargets) { target ->
                    val isSelected = targetGpa == target
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTargetGpaChange(target) },
                        label = { Text("${target.toInt()} (${if (target >= 14.0) "جيد جداً" else "جيد"})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isAchievable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAchievable) "المعدل المطلوب في السداسيات المتبقية ($remainingSemesters سداسي):" else "تنبيه الحساب:",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isAchievable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAchievable) {
                            "تحتاج لتحقيق معدل ${String.format("%.2f", requiredFutureGpa)} / 20.00 في كل سداسي قادم للوصول إلى هدف ${String.format("%.1f", targetGpa)}."
                        } else {
                            "يتجاوز المعدل المطلوب سقف 20/20. يمكنك استهداف معدل واقعي أقرب مثل 13.00 أو 14.00."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isAchievable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun LmdRegulationInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "قواعد التعويض والانتقال في النظام الجامعي الجزائري (LMD)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• التعويض بين المقاييس: إذا كان معدل السداسي ≥ 10.00، يكتسب الطالب كافة أرصدة السداسي (30 رصيداً) بالتعويض (Compensation).\n" +
                        "• الانتقال بالديون (Passage avec dettes): يحق للطالب الانتقال من L1 إلى L2 باستيفاء 30 رصيداً كحد أدنى، ومن L2 إلى L3 باستيفاء 90 رصيداً تراكمياً على الأقل مع استيفاء جميع وحدات L1 الأساسية.\n" +
                        "• تصنيف الترتيب (القرار 712): تُحسب فئات التصنيف (A/B/C/D) لاجتياز مسابقات الدكتوراه وفق المعدل التراكمي وتاريخ التخرج بدون استدراك.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

// ---------------------- Dialogs ----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditSubjectDialog(
    initialItem: ModuleGradeItem?,
    defaultAcademicYear: Int,
    defaultSemester: Int,
    onDismiss: () -> Unit,
    onSave: (ModuleGradeItem) -> Unit
) {
    var moduleName by remember { mutableStateOf(initialItem?.moduleName ?: "") }
    var selectedUnitType by remember { mutableStateOf(initialItem?.unitType ?: AcademicCurriculumData.unitTypes.first()) }
    var coeffStr by remember { mutableStateOf(initialItem?.coeff?.toString() ?: "3.0") }
    var creditStr by remember { mutableStateOf(initialItem?.credit?.toString() ?: "5") }
    var examGradeStr by remember { mutableStateOf(initialItem?.examGrade?.toString() ?: "12.0") }
    var tdGradeStr by remember { mutableStateOf(initialItem?.tdGrade?.toString() ?: "13.0") }
    var tpGradeStr by remember { mutableStateOf(initialItem?.tpGrade?.toString() ?: "14.0") }
    var hasTp by remember { mutableStateOf(initialItem?.hasTp ?: false) }
    var examWeightStr by remember { mutableStateOf((if (initialItem != null) (initialItem.examWeight * 100).toInt() else 60).toString()) }
    var rattrapageGradeStr by remember { mutableStateOf(initialItem?.rattrapageGrade?.toString() ?: "0.0") }
    var isRattrapageUsed by remember { mutableStateOf(initialItem?.isRattrapageUsed ?: false) }

    // Live preview of the module average
    val parsedExam = examGradeStr.toDoubleOrNull() ?: 0.0
    val parsedTd = tdGradeStr.toDoubleOrNull() ?: 0.0
    val parsedTp = tpGradeStr.toDoubleOrNull() ?: 0.0
    val parsedWeight = (examWeightStr.toDoubleOrNull() ?: 60.0) / 100.0
    val parsedRattrapage = rattrapageGradeStr.toDoubleOrNull() ?: 0.0

    val effectiveExam = if (isRattrapageUsed && parsedRattrapage > 0) maxOf(parsedExam, parsedRattrapage) else parsedExam
    val previewAvg = if (hasTp) {
        val cc = (parsedTd + parsedTp) / 2.0
        (effectiveExam * parsedWeight) + (cc * (1.0 - parsedWeight))
    } else {
        (effectiveExam * parsedWeight) + (parsedTd * (1.0 - parsedWeight))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialItem == null) "إضافة مقياس جديد للسداسي $defaultSemester" else "تعديل المقياس",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live preview banner
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (previewAvg >= 10.0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("المعدل المحسوب للمقياس:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${String.format("%.2f", previewAvg)} / 20",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (previewAvg >= 10.0) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }

                // Module name
                item {
                    OutlinedTextField(
                        value = moduleName,
                        onValueChange = { moduleName = it },
                        label = { Text("اسم المقياس (مثال: الخوارزميات، التحليل)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Unit Type
                item {
                    Text("نوع وحدة التعليم:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(AcademicCurriculumData.unitTypes) { uType ->
                            val isSelected = selectedUnitType == uType
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedUnitType = uType },
                                label = { Text(uType.split(" ").firstOrNull() ?: uType) }
                            )
                        }
                    }
                }

                // Coeff and Credit
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = coeffStr,
                            onValueChange = { coeffStr = it },
                            label = { Text("المعامل (Coeff)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = creditStr,
                            onValueChange = { creditStr = it },
                            label = { Text("الأرصدة (ECTS)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Exam Grade & Exam Weight %
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = examGradeStr,
                            onValueChange = { examGradeStr = it },
                            label = { Text("علامة الامتحان /20") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = examWeightStr,
                            onValueChange = { examWeightStr = it },
                            label = { Text("وزن الامتحان %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // TD Grade & TP Grade
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = tdGradeStr,
                            onValueChange = { tdGradeStr = it },
                            label = { Text("علامة TD /20") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        if (hasTp) {
                            OutlinedTextField(
                                value = tpGradeStr,
                                onValueChange = { tpGradeStr = it },
                                label = { Text("علامة TP /20") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Has TP Toggle
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasTp = !hasTp }
                    ) {
                        Checkbox(checked = hasTp, onCheckedChange = { hasTp = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("يحتوي على أعمال تطبيقية (TP)", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Rattrapage
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = rattrapageGradeStr,
                            onValueChange = { rattrapageGradeStr = it },
                            label = { Text("علامة الاستدراك /20") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isRattrapageUsed = !isRattrapageUsed }
                        ) {
                            Checkbox(checked = isRattrapageUsed, onCheckedChange = { isRattrapageUsed = it })
                            Text("تطبيق", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (moduleName.isNotBlank()) {
                        val newItem = ModuleGradeItem(
                            id = initialItem?.id ?: 0,
                            academicYear = initialItem?.academicYear ?: defaultAcademicYear,
                            semester = initialItem?.semester ?: defaultSemester,
                            moduleName = moduleName.trim(),
                            unitType = selectedUnitType,
                            coeff = coeffStr.toDoubleOrNull() ?: 2.0,
                            credit = creditStr.toIntOrNull() ?: 4,
                            examGrade = examGradeStr.toDoubleOrNull() ?: 10.0,
                            tdGrade = tdGradeStr.toDoubleOrNull() ?: 10.0,
                            tpGrade = tpGradeStr.toDoubleOrNull() ?: 0.0,
                            hasTp = hasTp,
                            examWeight = (examWeightStr.toDoubleOrNull() ?: 60.0) / 100.0,
                            rattrapageGrade = rattrapageGradeStr.toDoubleOrNull() ?: 0.0,
                            isRattrapageUsed = isRattrapageUsed
                        )
                        onSave(newItem)
                    }
                },
                enabled = moduleName.isNotBlank()
            ) {
                Text(if (initialItem == null) "إضافة" else "حفظ التعديلات")
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
private fun SpecialtyPresetsDialog(
    presets: List<SpecialtyPreset>,
    onDismiss: () -> Unit,
    onSelectPreset: (SpecialtyPreset) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("نماذج التخصصات الجامعية الجاهزة", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "اختر تخصصك لتحميل مقاييسه، معاملاته وأرصدته الرسمية دفعة واحدة:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(presets) { preset ->
                    Card(
                        onClick = { onSelectPreset(preset) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = preset.nameAr,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = preset.facultyAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${preset.modules.size} مقياس موزعة على السداسيات",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
private fun AcademicTranscriptDialog(
    transcriptText: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("كشف المسار الأكاديمي الشامل", fontWeight = FontWeight.Bold)
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
                            text = transcriptText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onShare) {
                Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("مشاركة")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCopy) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("نسخ")
            }
        }
    )
}
