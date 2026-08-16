package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiApiClient
import com.example.data.content.AcademicCurriculumData
import com.example.data.content.AdministrativeTemplatesData
import com.example.data.content.PromptsLibraryData
import com.example.data.content.SpecialtyPreset
import com.example.data.local.*
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainAppTab(val titleAr: String, val iconName: String) {
    STUDY_PLANNER("تنظيم الدراسة", "CalendarMonth"),
    ACADEMIC_PROGRESS("التقدم الأكاديمي", "AutoGraph"),
    RESEARCH_GUIDES("البحوث الجامعية", "MenuBook"),
    THESIS_PFE("المذكرة والتخرج", "School"),
    CITATIONS("المراجع والتوثيق", "FormatQuote"),
    AI_ASSISTANT("المساعد الذكي", "AutoAwesome"),
    ADMIN_DOCS("النماذج الإدارية", "Article")
}

enum class StudyPlannerSubTab(val titleAr: String) {
    SCHEDULE("الجدول الأسبوعي"),
    TASKS("المهام والواجبات"),
    EXAMS("مخطط الامتحانات"),
    ATTENDANCE("الحضور والغياب"),
    RESOURCES("البوابات والمصادر"),
    LMD_CALCULATOR("حاسبة المعدل LMD")
}

sealed interface AiChatState {
    object Idle : AiChatState
    object Loading : AiChatState
    data class Success(val responseText: String) : AiChatState
    data class Error(val message: String) : AiChatState
}

data class LmdSemesterCalculation(
    val semesterNumber: Int,
    val totalCoeff: Double,
    val totalWeightedScore: Double,
    val semesterAverage: Double,
    val totalAcquiredCredits: Int,
    val isPassed: Boolean
)

data class LmdAnnualCalculation(
    val s1Average: Double,
    val s2Average: Double,
    val annualAverage: Double,
    val totalCredits: Int,
    val statusText: String,
    val isPassed: Boolean
)

class StudentProViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository
    private val prefs = application.getSharedPreferences("student_pro_prefs", Context.MODE_PRIVATE)

    // Dark Mode Theme State
    private val _isDarkMode: MutableStateFlow<Boolean> = run {
        val isSaved = prefs.contains("is_dark_mode")
        val initial = if (isSaved) {
            prefs.getBoolean("is_dark_mode", false)
        } else {
            val currentNightMode = application.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
        MutableStateFlow(initial)
    }
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        prefs.edit().putBoolean("is_dark_mode", newMode).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }

    // App Navigation State
    private val _currentTab = MutableStateFlow(MainAppTab.STUDY_PLANNER)
    val currentTab: StateFlow<MainAppTab> = _currentTab.asStateFlow()

    private val _studySubTab = MutableStateFlow(StudyPlannerSubTab.SCHEDULE)
    val studySubTab: StateFlow<StudyPlannerSubTab> = _studySubTab.asStateFlow()

    // Data Flows from Room
    val scheduleItems: StateFlow<List<ScheduleItem>>
    val examItems: StateFlow<List<ExamItem>>
    val taskItems: StateFlow<List<TaskItem>>
    val semesterTasks: StateFlow<List<SemesterTask>>
    val attendanceRecords: StateFlow<List<AttendanceRecord>>
    val gradeItems: StateFlow<List<ModuleGradeItem>>
    val savedReferences: StateFlow<List<SavedReference>>
    val resourceLinks: StateFlow<List<StudyResourceLink>>
    val userProfile: StateFlow<UserProfile>

    // AI Assistant State
    private val _aiState = MutableStateFlow<AiChatState>(AiChatState.Idle)
    val aiState: StateFlow<AiChatState> = _aiState.asStateFlow()

    private val _aiInputPrompt = MutableStateFlow("")
    val aiInputPrompt: StateFlow<String> = _aiInputPrompt.asStateFlow()

    private val _aiHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val aiHistory: StateFlow<List<Pair<String, String>>> = _aiHistory.asStateFlow()

    // Citation Generator Form State
    var citTitle = MutableStateFlow("")
    var citAuthors = MutableStateFlow("")
    var citYear = MutableStateFlow("")
    var citPublisher = MutableStateFlow("")
    var citType = MutableStateFlow("كتاب (Book)")
    var citGeneratedApa = MutableStateFlow("")
    var citGeneratedIeee = MutableStateFlow("")

    // Admin Docs / Document Center Form State
    private val _selectedAdminTemplateId = MutableStateFlow(AdministrativeTemplatesData.templates.first().id)
    val selectedAdminTemplateId: StateFlow<String> = _selectedAdminTemplateId.asStateFlow()
    val adminDocFormValues = MutableStateFlow<Map<String, String>>(emptyMap())

    private val _selectedDocCategory = MutableStateFlow("الكل")
    val selectedDocCategory: StateFlow<String> = _selectedDocCategory.asStateFlow()

    private val _docSearchQuery = MutableStateFlow("")
    val docSearchQuery: StateFlow<String> = _docSearchQuery.asStateFlow()

    private val _selectedDocLanguage = MutableStateFlow("الكل")
    val selectedDocLanguage: StateFlow<String> = _selectedDocLanguage.asStateFlow()

    // Academic Progress State
    private val _selectedAcademicYear = MutableStateFlow(1)
    val selectedAcademicYear: StateFlow<Int> = _selectedAcademicYear.asStateFlow()

    private val _selectedAcademicSemester = MutableStateFlow(1)
    val selectedAcademicSemester: StateFlow<Int> = _selectedAcademicSemester.asStateFlow()

    private val _targetGpa = MutableStateFlow(15.0)
    val targetGpa: StateFlow<Double> = _targetGpa.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)

        scheduleItems = repository.scheduleItems.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        examItems = repository.examItems.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        taskItems = repository.taskItems.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        semesterTasks = repository.semesterTasks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        attendanceRecords = repository.attendanceRecords.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        gradeItems = repository.gradeItems.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        savedReferences = repository.savedReferences.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        resourceLinks = repository.resourceLinks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        userProfile = repository.userProfile
            .map { it ?: UserProfile() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun setTab(tab: MainAppTab) {
        _currentTab.value = tab
    }

    fun setStudySubTab(subTab: StudyPlannerSubTab) {
        _studySubTab.value = subTab
    }

    fun setSelectedAcademicYear(year: Int) {
        _selectedAcademicYear.value = year
        // Automatically switch selected semester to the first semester of that year
        val firstSemesterOfYear = ((year - 1) * 2) + 1
        _selectedAcademicSemester.value = firstSemesterOfYear
    }

    fun setSelectedAcademicSemester(sem: Int) {
        _selectedAcademicSemester.value = sem
    }

    fun setTargetGpa(target: Double) {
        _targetGpa.value = target
    }

    // Schedule Operations
    fun addScheduleItem(item: ScheduleItem) = viewModelScope.launch {
        repository.insertScheduleItem(item)
    }

    fun deleteScheduleItem(item: ScheduleItem) = viewModelScope.launch {
        repository.deleteScheduleItem(item)
    }

    // Task Operations
    fun addTask(task: TaskItem) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun toggleTask(task: TaskItem) = viewModelScope.launch {
        repository.updateTask(task.copy(isDone = !task.isDone))
    }

    fun deleteTask(task: TaskItem) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    // Semester Tasks Operations
    fun addSemesterTask(task: SemesterTask) = viewModelScope.launch {
        repository.insertSemesterTask(task)
    }

    fun toggleSemesterTask(task: SemesterTask) = viewModelScope.launch {
        repository.setSemesterTaskCompleted(task.id, !task.isCompleted)
    }

    fun completeSemesterTask(task: SemesterTask) = viewModelScope.launch {
        repository.setSemesterTaskCompleted(task.id, true)
    }

    fun updateSemesterTask(task: SemesterTask) = viewModelScope.launch {
        repository.updateSemesterTask(task)
    }

    fun deleteSemesterTask(task: SemesterTask) = viewModelScope.launch {
        repository.deleteSemesterTask(task)
    }

    fun deleteSemesterTaskById(id: Int) = viewModelScope.launch {
        repository.deleteSemesterTaskById(id)
    }

    // Exam Operations
    fun addExam(exam: ExamItem) = viewModelScope.launch {
        repository.insertExam(exam)
    }

    fun toggleExamCompleted(exam: ExamItem) = viewModelScope.launch {
        repository.updateExam(exam.copy(isCompleted = !exam.isCompleted))
    }

    fun deleteExam(exam: ExamItem) = viewModelScope.launch {
        repository.deleteExam(exam)
    }

    // Attendance Operations
    fun addAttendance(record: AttendanceRecord) = viewModelScope.launch {
        repository.insertAttendance(record)
    }

    fun incrementAbsence(record: AttendanceRecord) = viewModelScope.launch {
        repository.updateAttendance(record.copy(currentAbsences = record.currentAbsences + 1))
    }

    fun decrementAbsence(record: AttendanceRecord) = viewModelScope.launch {
        if (record.currentAbsences > 0) {
            repository.updateAttendance(record.copy(currentAbsences = record.currentAbsences - 1))
        }
    }

    fun incrementExcused(record: AttendanceRecord) = viewModelScope.launch {
        repository.updateAttendance(record.copy(excusedAbsences = record.excusedAbsences + 1))
    }

    fun deleteAttendance(record: AttendanceRecord) = viewModelScope.launch {
        repository.deleteAttendance(record)
    }

    // Grade / LMD Operations
    fun addOrUpdateGrade(grade: ModuleGradeItem) = viewModelScope.launch {
        if (grade.id == 0) {
            repository.insertGrade(grade)
        } else {
            repository.updateGrade(grade)
        }
    }

    fun deleteGrade(grade: ModuleGradeItem) = viewModelScope.launch {
        repository.deleteGrade(grade)
    }

    fun deleteSemesterGrades(semester: Int) = viewModelScope.launch {
        repository.deleteGradesBySemester(semester)
    }

    fun clearAllGrades() = viewModelScope.launch {
        repository.clearAllGrades()
    }

    fun applySpecialtyPreset(preset: SpecialtyPreset) = viewModelScope.launch {
        repository.clearAllGrades()
        repository.insertGrades(preset.modules)
    }

    // Calculate module average
    fun calculateModuleAverage(grade: ModuleGradeItem): Double {
        val effectiveExam = if (grade.isRattrapageUsed && grade.rattrapageGrade > 0) {
            maxOf(grade.examGrade, grade.rattrapageGrade)
        } else {
            grade.examGrade
        }

        return if (grade.hasTp) {
            // Standard: ExamWeight% Exam + (1-ExamWeight)% CC where CC is average of TD and TP
            val cc = (grade.tdGrade + grade.tpGrade) / 2.0
            (effectiveExam * grade.examWeight) + (cc * (1.0 - grade.examWeight))
        } else {
            (effectiveExam * grade.examWeight) + (grade.tdGrade * (1.0 - grade.examWeight))
        }
    }

    // Calculate Semester Average & Credits
    fun calculateSemester(semesterNum: Int, grades: List<ModuleGradeItem>): LmdSemesterCalculation {
        val semesterGrades = grades.filter { it.semester == semesterNum }
        if (semesterGrades.isEmpty()) {
            return LmdSemesterCalculation(semesterNum, 0.0, 0.0, 0.0, 0, false)
        }

        var totalCoeff = 0.0
        var totalWeightedScore = 0.0
        var acquiredCredits = 0

        for (grade in semesterGrades) {
            val modAvg = calculateModuleAverage(grade)
            totalCoeff += grade.coeff
            totalWeightedScore += modAvg * grade.coeff
            if (modAvg >= 10.0) {
                acquiredCredits += grade.credit
            }
        }

        val semesterAvg = if (totalCoeff > 0) totalWeightedScore / totalCoeff else 0.0
        val isPassed = semesterAvg >= 10.0
        // In Algerian LMD system: If semester average >= 10.00, student earns all 30 credits by compensation (Par compensation)
        val finalCredits = if (isPassed) 30 else acquiredCredits

        return LmdSemesterCalculation(
            semesterNumber = semesterNum,
            totalCoeff = totalCoeff,
            totalWeightedScore = totalWeightedScore,
            semesterAverage = semesterAvg,
            totalAcquiredCredits = finalCredits,
            isPassed = isPassed
        )
    }

    // Calculate Annual Average
    fun calculateAnnual(s1Calc: LmdSemesterCalculation, s2Calc: LmdSemesterCalculation): LmdAnnualCalculation {
        val annualAvg = if (s1Calc.totalCoeff > 0 && s2Calc.totalCoeff > 0) {
            (s1Calc.semesterAverage + s2Calc.semesterAverage) / 2.0
        } else if (s1Calc.totalCoeff > 0) {
            s1Calc.semesterAverage
        } else {
            s2Calc.semesterAverage
        }
        val isPassed = annualAvg >= 10.0
        val totalCredits = if (isPassed && s1Calc.totalCoeff > 0 && s2Calc.totalCoeff > 0) 60 else (s1Calc.totalAcquiredCredits + s2Calc.totalAcquiredCredits)
        val statusText = when {
            isPassed -> "ناجح ومقبول (Admis) - تم استيفاء السنة الجامعية بنجاح"
            totalCredits >= 45 -> "مقبول مع ديون (Admis avec dettes) - استوفى أكثر من 45 رصيداً"
            annualAvg >= 9.0 -> "مؤهل لدورة الاستدراك / مداولات الإنقاذ (Rattrapage)"
            else -> "دورة الاستدراك (Session de Rattrapage)"
        }
        return LmdAnnualCalculation(
            s1Average = s1Calc.semesterAverage,
            s2Average = s2Calc.semesterAverage,
            annualAverage = annualAvg,
            totalCredits = totalCredits,
            statusText = statusText,
            isPassed = isPassed
        )
    }

    // Cumulative GPA Calculation across all recorded semesters
    fun calculateCumulativeGpa(grades: List<ModuleGradeItem>): Double {
        val distinctSemesters = grades.map { it.semester }.distinct()
        if (distinctSemesters.isEmpty()) return 0.0

        val semesterAverages = distinctSemesters.map { sem ->
            calculateSemester(sem, grades).semesterAverage
        }.filter { it > 0.0 }

        return if (semesterAverages.isNotEmpty()) {
            semesterAverages.sum() / semesterAverages.size
        } else {
            0.0
        }
    }

    // Total acquired credits across all recorded semesters
    fun calculateTotalAcquiredCredits(grades: List<ModuleGradeItem>): Int {
        val distinctSemesters = grades.map { it.semester }.distinct()
        return distinctSemesters.sumOf { sem ->
            calculateSemester(sem, grades).totalAcquiredCredits
        }
    }

    // Target GPA simulation: calculates required future GPA to reach target cumulative GPA
    fun calculateRequiredFutureGpa(
        currentCumulativeGpa: Double,
        completedSemestersCount: Int,
        totalTargetSemesters: Int,
        targetCumulativeGpa: Double
    ): Double {
        val remainingSemesters = totalTargetSemesters - completedSemestersCount
        if (remainingSemesters <= 0) return targetCumulativeGpa

        val currentTotalPoints = currentCumulativeGpa * completedSemestersCount
        val targetTotalPoints = targetCumulativeGpa * totalTargetSemesters
        val requiredPoints = targetTotalPoints - currentTotalPoints
        return requiredPoints / remainingSemesters
    }

    // Generate formatted Academic Transcript
    fun generateTranscriptSummary(profile: UserProfile, grades: List<ModuleGradeItem>): String {
        val sb = StringBuilder()
        sb.append("🎓 كشف المسار الأكاديمي الشامل - Student Pro DZ\n")
        sb.append("═══════════════════════════════════════\n")
        sb.append("👤 الطالب: ${profile.fullName}\n")
        sb.append("🏛️ الجامعة: ${profile.university}\n")
        sb.append("📚 الكلية: ${profile.faculty}\n")
        sb.append("🔬 التخصص: ${profile.specialty}\n")
        sb.append("🆔 رقم التسجيل: ${profile.studentIdNumber}\n")
        sb.append("═══════════════════════════════════════\n\n")

        val distinctSemesters = grades.map { it.semester }.distinct().sorted()
        for (sem in distinctSemesters) {
            val semCalc = calculateSemester(sem, grades)
            val semGrades = grades.filter { it.semester == sem }
            sb.append("📖 [السداسي $sem (S$sem)] - المعدل: ${String.format("%.2f", semCalc.semesterAverage)} / 20.00 | الأرصدة: ${semCalc.totalAcquiredCredits}/30 | الحالة: ${if (semCalc.isPassed) "مستوفى" else "غير مستوفى"}\n")
            sb.append("───────────────────────────────────────\n")
            for (g in semGrades) {
                val avg = calculateModuleAverage(g)
                sb.append("• ${g.moduleName} | معامل: ${g.coeff} | رصيد: ${g.credit} | المعدل: ${String.format("%.2f", avg)}/20 (${if (avg >= 10.0) "مستوفى" else "تحت 10"})\n")
            }
            sb.append("\n")
        }

        val cumulativeGpa = calculateCumulativeGpa(grades)
        val totalCredits = calculateTotalAcquiredCredits(grades)
        val (mention, _) = AcademicCurriculumData.getAcademicMention(cumulativeGpa)
        val (category, _) = AcademicCurriculumData.getLmdCategory(cumulativeGpa)

        sb.append("═══════════════════════════════════════\n")
        sb.append("📊 الحصيلة الأكاديمية الشاملة:\n")
        sb.append("⭐ المعدل التراكمي العام (Cumulative GPA): ${String.format("%.2f", cumulativeGpa)} / 20.00\n")
        sb.append("🎖️ التقدير العام: $mention\n")
        sb.append("🏆 تصنيف الترتيب LMD: $category\n")
        sb.append("💳 مجموع الأرصدة المحصلة (ECTS): $totalCredits رصيداً\n")
        sb.append("═══════════════════════════════════════\n")
        return sb.toString()
    }

    // References & Citations
    fun generateCitation(
        title: String,
        authors: String,
        year: String,
        publisher: String,
        type: String,
        url: String = ""
    ) {
        val cleanYear = if (year.isBlank()) "د.ت" else year
        val cleanAuthors = if (authors.isBlank()) "المؤلف" else authors
        val cleanTitle = if (title.isBlank()) "عنوان المرجع" else title

        val apa = when (type) {
            "كتاب (Book)" -> "$cleanAuthors ($cleanYear). $cleanTitle. $publisher."
            "مقال علمي (Journal Article)" -> "$cleanAuthors ($cleanYear). $cleanTitle. $publisher."
            "مذكرة/أطروحة (Thesis)" -> "$cleanAuthors ($cleanYear). $cleanTitle [مذكرة تخرج / أطروحة دكتوراه غير منشورة]. $publisher."
            "موقع إلكتروني (Website)" -> "$cleanAuthors ($cleanYear). $cleanTitle. متاح على: ${if (url.isNotBlank()) url else publisher}."
            else -> "$cleanAuthors ($cleanYear). $cleanTitle. $publisher."
        }

        val ieee = when (type) {
            "كتاب (Book)" -> "[1] $cleanAuthors, $cleanTitle, $cleanYear: $publisher."
            "مقال علمي (Journal Article)" -> "[1] $cleanAuthors, \"$cleanTitle,\" $publisher, $cleanYear."
            "موقع إلكتروني (Website)" -> "[1] $cleanAuthors, \"$cleanTitle,\" $cleanYear. [عبر الإنترنت]. متاح: $url"
            else -> "[1] $cleanAuthors, \"$cleanTitle,\" $publisher, $cleanYear."
        }

        citGeneratedApa.value = apa
        citGeneratedIeee.value = ieee
    }

    fun saveCurrentCitation() = viewModelScope.launch {
        if (citTitle.value.isNotBlank()) {
            val ref = SavedReference(
                title = citTitle.value,
                authors = citAuthors.value,
                year = citYear.value,
                sourceOrPublisher = citPublisher.value,
                referenceType = citType.value,
                apaCitation = citGeneratedApa.value,
                ieeeCitation = citGeneratedIeee.value
            )
            repository.insertReference(ref)
            citTitle.value = ""
            citAuthors.value = ""
            citYear.value = ""
            citPublisher.value = ""
            citGeneratedApa.value = ""
            citGeneratedIeee.value = ""
        }
    }

    fun deleteReference(ref: SavedReference) = viewModelScope.launch {
        repository.deleteReference(ref)
    }

    // Resource Links Operations
    fun addResourceLink(link: StudyResourceLink) = viewModelScope.launch {
        repository.insertResourceLink(link)
    }

    fun deleteResourceLink(link: StudyResourceLink) = viewModelScope.launch {
        repository.deleteResourceLink(link)
    }

    fun toggleResourceLinkFavorite(link: StudyResourceLink) = viewModelScope.launch {
        repository.setResourceLinkFavorite(link.id, !link.isFavorite)
    }

    // AI Assistant
    fun setAiPrompt(text: String) {
        _aiInputPrompt.value = text
    }

    fun applyPromptTemplate(promptItem: com.example.data.content.AiPromptItem, customInput: String) {
        val filled = promptItem.promptTemplate.replace("[TOPIC]", customInput.ifBlank { promptItem.sampleTopic })
        _aiInputPrompt.value = filled
        _currentTab.value = MainAppTab.AI_ASSISTANT
    }

    fun sendAiPrompt() = viewModelScope.launch {
        val prompt = _aiInputPrompt.value.trim()
        if (prompt.isBlank()) return@launch

        _aiState.value = AiChatState.Loading
        val result = GeminiApiClient.askGemini(prompt)
        result.onSuccess { response ->
            _aiState.value = AiChatState.Success(response)
            _aiHistory.value = _aiHistory.value + (prompt to response)
            _aiInputPrompt.value = ""
        }.onFailure { error ->
            _aiState.value = AiChatState.Error(error.localizedMessage ?: "حدث خطأ غير متوقع أثناء المعالجة")
        }
    }

    // User Profile & Admin Docs
    fun updateUserProfile(profile: UserProfile) = viewModelScope.launch {
        repository.saveUserProfile(profile)
    }

    fun selectAdminTemplate(templateId: String) {
        _selectedAdminTemplateId.value = templateId
    }

    fun setSelectedDocCategory(cat: String) {
        _selectedDocCategory.value = cat
    }

    fun setDocSearchQuery(query: String) {
        _docSearchQuery.value = query
    }

    fun setSelectedDocLanguage(lang: String) {
        _selectedDocLanguage.value = lang
    }

    fun setAdminFormField(key: String, value: String) {
        val current = adminDocFormValues.value.toMutableMap()
        current[key] = value
        adminDocFormValues.value = current
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Student Pro DZ") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "تم النسخ إلى الحافظة بنجاح", Toast.LENGTH_SHORT).show()
    }

    fun shareText(context: Context, text: String, title: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }

    fun downloadDocumentAsFile(context: Context, fileName: String, content: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, content)
                putExtra(Intent.EXTRA_TITLE, fileName)
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                type = "text/plain"
            }
            val chooser = Intent.createChooser(sendIntent, "تحميل / حفظ الوثيقة الرسمية ($fileName)")
            context.startActivity(chooser)
            Toast.makeText(context, "جاهز للحفظ والتصدير: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر إنشاء ملف التصدير", Toast.LENGTH_SHORT).show()
        }
    }
}
