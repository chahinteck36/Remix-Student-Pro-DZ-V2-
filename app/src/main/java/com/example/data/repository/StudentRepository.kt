package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StudentRepository(private val db: AppDatabase) {

    // DAOs
    val scheduleItems: Flow<List<ScheduleItem>> = db.scheduleDao().getAllScheduleItems()
    val examItems: Flow<List<ExamItem>> = db.examDao().getAllExams()
    val taskItems: Flow<List<TaskItem>> = db.taskDao().getAllTasks()
    val semesterTasks: Flow<List<SemesterTask>> = db.semesterTaskDao().getAllSemesterTasks()
    val attendanceRecords: Flow<List<AttendanceRecord>> = db.attendanceDao().getAllAttendance()
    val gradeItems: Flow<List<ModuleGradeItem>> = db.gradeDao().getAllGrades()
    val savedReferences: Flow<List<SavedReference>> = db.referenceDao().getAllReferences()
    val resourceLinks: Flow<List<StudyResourceLink>> = db.resourceLinkDao().getAllLinks()
    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()

    // Schedule Operations
    suspend fun insertScheduleItem(item: ScheduleItem) = db.scheduleDao().insertItem(item)
    suspend fun updateScheduleItem(item: ScheduleItem) = db.scheduleDao().updateItem(item)
    suspend fun deleteScheduleItem(item: ScheduleItem) = db.scheduleDao().deleteItem(item)

    // Exam Operations
    suspend fun insertExam(exam: ExamItem) = db.examDao().insertExam(exam)
    suspend fun updateExam(exam: ExamItem) = db.examDao().updateExam(exam)
    suspend fun deleteExam(exam: ExamItem) = db.examDao().deleteExam(exam)

    // Task Operations
    suspend fun insertTask(task: TaskItem) = db.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskItem) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: TaskItem) = db.taskDao().deleteTask(task)

    // Semester Study Planner Task Operations
    fun getTasksForSemester(semester: Int): Flow<List<SemesterTask>> = db.semesterTaskDao().getTasksForSemester(semester)
    fun getPendingSemesterTasks(): Flow<List<SemesterTask>> = db.semesterTaskDao().getPendingTasks()
    fun getCompletedSemesterTasks(): Flow<List<SemesterTask>> = db.semesterTaskDao().getCompletedTasks()
    suspend fun insertSemesterTask(task: SemesterTask) = db.semesterTaskDao().insertTask(task)
    suspend fun insertSemesterTasks(tasks: List<SemesterTask>) = db.semesterTaskDao().insertAll(tasks)
    suspend fun updateSemesterTask(task: SemesterTask) = db.semesterTaskDao().updateTask(task)
    suspend fun deleteSemesterTask(task: SemesterTask) = db.semesterTaskDao().deleteTask(task)
    suspend fun deleteSemesterTaskById(id: Int) = db.semesterTaskDao().deleteById(id)
    suspend fun setSemesterTaskCompleted(id: Int, isCompleted: Boolean) = 
        db.semesterTaskDao().setTaskCompletion(id, isCompleted, if (isCompleted) System.currentTimeMillis() else null)

    // Attendance Operations
    suspend fun insertAttendance(record: AttendanceRecord) = db.attendanceDao().insertAttendance(record)
    suspend fun updateAttendance(record: AttendanceRecord) = db.attendanceDao().updateAttendance(record)
    suspend fun deleteAttendance(record: AttendanceRecord) = db.attendanceDao().deleteAttendance(record)

    // Grade Operations
    suspend fun insertGrade(grade: ModuleGradeItem) = db.gradeDao().insertGrade(grade)
    suspend fun insertGrades(grades: List<ModuleGradeItem>) = db.gradeDao().insertAll(grades)
    suspend fun updateGrade(grade: ModuleGradeItem) = db.gradeDao().updateGrade(grade)
    suspend fun deleteGrade(grade: ModuleGradeItem) = db.gradeDao().deleteGrade(grade)
    suspend fun deleteGradesBySemester(semester: Int) = db.gradeDao().deleteBySemester(semester)
    suspend fun clearAllGrades() = db.gradeDao().deleteAllGrades()

    // Reference Operations
    suspend fun insertReference(ref: SavedReference) = db.referenceDao().insertReference(ref)
    suspend fun updateReference(ref: SavedReference) = db.referenceDao().updateReference(ref)
    suspend fun deleteReference(ref: SavedReference) = db.referenceDao().deleteReference(ref)

    // Resource Link Operations
    fun getLinksByCategory(category: String): Flow<List<StudyResourceLink>> = db.resourceLinkDao().getLinksByCategory(category)
    suspend fun insertResourceLink(link: StudyResourceLink) = db.resourceLinkDao().insertLink(link)
    suspend fun insertResourceLinks(links: List<StudyResourceLink>) = db.resourceLinkDao().insertAll(links)
    suspend fun updateResourceLink(link: StudyResourceLink) = db.resourceLinkDao().updateLink(link)
    suspend fun deleteResourceLink(link: StudyResourceLink) = db.resourceLinkDao().deleteLink(link)
    suspend fun deleteResourceLinkById(id: Int) = db.resourceLinkDao().deleteById(id)
    suspend fun setResourceLinkFavorite(id: Int, isFavorite: Boolean) = db.resourceLinkDao().setFavorite(id, isFavorite)

    // User Profile
    suspend fun saveUserProfile(profile: UserProfile) = db.userDao().insertOrUpdateProfile(profile)

    // Seed initial data if empty
    suspend fun checkAndSeedInitialData() {
        val existingUser = db.userDao().getUserProfile().firstOrNull()
        if (existingUser == null) {
            db.userDao().insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    fullName = "محمد أمين بن علي",
                    university = "جامعة الجزائر 1 - بن يوسف بن خدة",
                    faculty = "كلية العلوم والتكنولوجيا",
                    department = "قسم الإعلام الآلي",
                    specialty = "ذكاء اصطناعي ونظم معلوماتية",
                    academicLevel = "ماستر 2 (Master 2)",
                    studentIdNumber = "202131054890",
                    email = "m.benali@univ-alger.dz",
                    phone = "0661234567"
                )
            )
        }

        val currentSchedule = db.scheduleDao().getAllScheduleItems().firstOrNull()
        if (currentSchedule.isNullOrEmpty()) {
            db.scheduleDao().insertAll(
                listOf(
                    ScheduleItem(
                        dayOfWeek = 0, // الأحد
                        subjectName = "الذكاء الاصطناعي المتقدم",
                        sessionType = "محاضرة",
                        roomOrAmphi = "مدرج ابن خلدون",
                        professorName = "د. بلقاسم",
                        startTime = "08:30",
                        endTime = "10:00",
                        colorIndex = 0
                    ),
                    ScheduleItem(
                        dayOfWeek = 0, // الأحد
                        subjectName = "الذكاء الاصطناعي المتقدم",
                        sessionType = "أعمال موجهة (TD)",
                        roomOrAmphi = "قاعة 14",
                        professorName = "أ. خديجة",
                        startTime = "10:15",
                        endTime = "11:45",
                        colorIndex = 0
                    ),
                    ScheduleItem(
                        dayOfWeek = 1, // الإثنين
                        subjectName = "منهجية البحث العلمي والمذكرة",
                        sessionType = "محاضرة",
                        roomOrAmphi = "مدرج 3",
                        professorName = "أ.د. منصوري",
                        startTime = "09:00",
                        endTime = "10:30",
                        colorIndex = 1
                    ),
                    ScheduleItem(
                        dayOfWeek = 1, // الإثنين
                        subjectName = "أمن النظم والمعلومات",
                        sessionType = "أعمال تطبيقية (TP)",
                        roomOrAmphi = "مخبر 5",
                        professorName = "د. كريم",
                        startTime = "13:00",
                        endTime = "15:00",
                        colorIndex = 2
                    ),
                    ScheduleItem(
                        dayOfWeek = 2, // الثلاثاء
                        subjectName = "تحليل البيانات الضخمة (Big Data)",
                        sessionType = "محاضرة",
                        roomOrAmphi = "مدرج أ",
                        professorName = "د. سعيدي",
                        startTime = "10:00",
                        endTime = "11:30",
                        colorIndex = 3
                    ),
                    ScheduleItem(
                        dayOfWeek = 3, // الأربعاء
                        subjectName = "ريادة الأعمال وإدارة المشاريع",
                        sessionType = "محاضرة",
                        roomOrAmphi = "قاعة المحاضرات الكبرى",
                        professorName = "د. زبيري",
                        startTime = "08:30",
                        endTime = "10:00",
                        colorIndex = 4
                    )
                )
            )
        }

        val currentTasks = db.taskDao().getAllTasks().firstOrNull()
        if (currentTasks.isNullOrEmpty()) {
            db.taskDao().insertAll(
                listOf(
                    TaskItem(
                        title = "تسليم سلسلة تمارين TD رقم 2 في الذكاء الاصطناعي",
                        subjectName = "الذكاء الاصطناعي المتقدم",
                        category = "واجب TD",
                        dueDate = "2026-03-25",
                        priority = "عالي",
                        isDone = false
                    ),
                    TaskItem(
                        title = "صياغة إشكالية مذكرة التخرج وعرضها على الأستاذ المشرف",
                        subjectName = "مشروع التخرج (PFE)",
                        category = "مذكرة",
                        dueDate = "2026-03-30",
                        priority = "عالي",
                        isDone = false
                    ),
                    TaskItem(
                        title = "إعداد تقرير TP أمن المعلومات (تطبيق خوارزميات التشفير)",
                        subjectName = "أمن النظم والمعلومات",
                        category = "تقرير TP",
                        dueDate = "2026-04-05",
                        priority = "متوسط",
                        isDone = false
                    ),
                    TaskItem(
                        title = "مراجعة وتلخيص الفصل الأول في منهجية البحث",
                        subjectName = "منهجية البحث العلمي",
                        category = "مراجعة",
                        dueDate = "2026-04-10",
                        priority = "منخفض",
                        isDone = true
                    )
                )
            )
        }

        val currentSemesterTasks = db.semesterTaskDao().getAllSemesterTasks().firstOrNull()
        if (currentSemesterTasks.isNullOrEmpty()) {
            db.semesterTaskDao().insertAll(
                listOf(
                    SemesterTask(
                        semester = 1,
                        title = "تسليم التقرير المخبري الأول (TP 1) - خوارزميات الترتيب والبحث",
                        moduleName = "الخوارزميات وهياكل البيانات 1 (ALGO)",
                        taskType = "تقرير TP",
                        deadlineDate = "2026-03-25",
                        deadlineTime = "23:59",
                        priority = "عالي",
                        isCompleted = false,
                        estimatedHours = 3.5,
                        notes = "تنفيذ خوارزميات QuickSort و MergeSort وتحليل التعقيد الزمني."
                    ),
                    SemesterTask(
                        semester = 1,
                        title = "حل سلسلة تمارين التحليل الرياضي رقم 3 حول المتتاليات والتكاملات",
                        moduleName = "التحليل الرياضي 1 (Analyse)",
                        taskType = "واجب TD",
                        deadlineDate = "2026-03-28",
                        deadlineTime = "18:00",
                        priority = "متوسط",
                        isCompleted = false,
                        estimatedHours = 2.0,
                        notes = "التركيز على مبرهنة القيمة المتوسطة والمتتاليات المتقاربة."
                    ),
                    SemesterTask(
                        semester = 1,
                        title = "مراجعة شاملة للامتحان الجزئي (Interrogation) في الجبر الخطي",
                        moduleName = "الجبر الخطي 1 (Algèbre)",
                        taskType = "مراجعة امتحان",
                        deadlineDate = "2026-04-02",
                        deadlineTime = "09:00",
                        priority = "عالي",
                        isCompleted = false,
                        estimatedHours = 5.0,
                        notes = "الفضاءات الشعاعية، الاستقلال الخطي والمصفوفات المتعامدة."
                    ),
                    SemesterTask(
                        semester = 2,
                        title = "إعداد مشروع موقع الويب المتجاوب بإشراف مخبر الوسائط المتعددة",
                        moduleName = "أدوات وتكنولوجيات الويب (Web Tools)",
                        taskType = "مشروع مصغر",
                        deadlineDate = "2026-04-15",
                        deadlineTime = "23:59",
                        priority = "عالي",
                        isCompleted = false,
                        estimatedHours = 8.0,
                        notes = "تصميم واجهة متجاوبة ومتوافقة مع معايير إمكانية الوصول."
                    ),
                    SemesterTask(
                        semester = 1,
                        title = "تلخيص المحاضرة التمهيدية في تركيب وهندسة الحاسوب",
                        moduleName = "تركيب الحاسوب (Architecture)",
                        taskType = "قراءة مرجعية",
                        deadlineDate = "2026-03-10",
                        deadlineTime = "20:00",
                        priority = "منخفض",
                        isCompleted = true,
                        completionTimestamp = System.currentTimeMillis() - 86400000L * 3,
                        estimatedHours = 1.5,
                        notes = "بنية فون نيومان وجبر بول."
                    )
                )
            )
        }

        val currentExams = db.examDao().getAllExams().firstOrNull()
        if (currentExams.isNullOrEmpty()) {
            db.examDao().insertAll(
                listOf(
                    ExamItem(
                        subjectName = "الذكاء الاصطناعي المتقدم",
                        examDate = "2026-05-18",
                        examTime = "09:00",
                        roomOrAmphi = "مدرج ابن خلدون",
                        coeff = 3.0,
                        notes = "التركيز على خوارزميات التعلم العميق والشبكات العصبية"
                    ),
                    ExamItem(
                        subjectName = "منهجية البحث العلمي والمذكرة",
                        examDate = "2026-05-21",
                        examTime = "11:30",
                        roomOrAmphi = "مدرج 3",
                        coeff = 2.0,
                        notes = "مراجعة قواعد التوثيق APA وصياغة الفرضيات"
                    ),
                    ExamItem(
                        subjectName = "أمن النظم والمعلومات",
                        examDate = "2026-05-25",
                        examTime = "09:00",
                        roomOrAmphi = "قاعة 12",
                        coeff = 2.0,
                        notes = "امتحان نظري وتطبيقي"
                    )
                )
            )
        }

        val currentAttendance = db.attendanceDao().getAllAttendance().firstOrNull()
        if (currentAttendance.isNullOrEmpty()) {
            db.attendanceDao().insertAll(
                listOf(
                    AttendanceRecord(
                        subjectName = "أعمال موجهة (TD) - الذكاء الاصطناعي",
                        sessionType = "TD",
                        maxAllowedAbsences = 3,
                        currentAbsences = 1,
                        excusedAbsences = 0,
                        notes = "غياب واحد بتاريخ 12 فيفري"
                    ),
                    AttendanceRecord(
                        subjectName = "أعمال تطبيقية (TP) - أمن النظم",
                        sessionType = "TP",
                        maxAllowedAbsences = 2,
                        currentAbsences = 0,
                        excusedAbsences = 0,
                        notes = "حضور كامل ومنتظم"
                    ),
                    AttendanceRecord(
                        subjectName = "أعمال موجهة (TD) - تحليل البيانات",
                        sessionType = "TD",
                        maxAllowedAbsences = 3,
                        currentAbsences = 2,
                        excusedAbsences = 1,
                        notes = "تحذير: اقتراب من حد الإقصاء"
                    )
                )
            )
        }

        val currentGrades = db.gradeDao().getAllGrades().firstOrNull()
        if (currentGrades.isNullOrEmpty()) {
            db.gradeDao().insertAll(
                listOf(
                    // Semester 1 (L1)
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "الخوارزميات وهياكل البيانات 1 (ALGO)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 14.5,
                        tdGrade = 15.0,
                        tpGrade = 16.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "التحليل الرياضي 1 (Analyse)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 13.0,
                        tdGrade = 14.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "الجبر الخطي 1 (Algèbre)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 12.5,
                        tdGrade = 13.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "تركيب الحاسوب (Architecture)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 15.0,
                        tdGrade = 15.5,
                        tpGrade = 16.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "فيزياء الكهرباء (Electricité)",
                        unitType = "وحدة استكشافية (UED)",
                        coeff = 2.0,
                        credit = 4,
                        examGrade = 11.5,
                        tdGrade = 12.5,
                        tpGrade = 13.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "اللغة الإنجليزية 1 (Anglais)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 16.0,
                        tdGrade = 16.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 1,
                        moduleName = "الفرنسية والمصطلحات العلمية (Bureautique & TICE)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 15.5,
                        tdGrade = 15.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),

                    // Semester 2 (L1)
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "الخوارزميات وهياكل البيانات 2 (ALGO 2)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 15.0,
                        tdGrade = 16.0,
                        tpGrade = 16.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "التحليل الرياضي 2 (Analyse 2)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 12.5,
                        tdGrade = 13.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "الاحتمالات والإحصاء الوصفي (Probabilités)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 14.0,
                        tdGrade = 14.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "أنظمة التشغيل 1 (Systèmes d'exploitation 1)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 14.5,
                        tdGrade = 15.0,
                        tpGrade = 15.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "أدوات وتكنولوجيات الويب (Web Tools)",
                        unitType = "وحدة استكشافية (UED)",
                        coeff = 2.0,
                        credit = 4,
                        examGrade = 16.5,
                        tdGrade = 17.0,
                        tpGrade = 18.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "اللغة الإنجليزية 2 (Anglais 2)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 16.0,
                        tdGrade = 16.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 1,
                        semester = 2,
                        moduleName = "تاريخ العلوم وتكنولوجيا المعلومات (Histoire des sciences)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 15.0,
                        tdGrade = 15.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),

                    // Semester 3 (L2)
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "هندسة البرمجيات والبرمجة الكائنية (POO / Java)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 15.5,
                        tdGrade = 16.0,
                        tpGrade = 17.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "قواعد البيانات العلائقية (Bases de Données)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 16.0,
                        tdGrade = 16.5,
                        tpGrade = 17.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "أنظمة التشغيل المتقدمة (Systèmes d'exploitation 2)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 13.5,
                        tdGrade = 14.0,
                        tpGrade = 14.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "المنطق الرياضي ونظرية الرسوم (Logique Mathématique & Graphes)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 13.0,
                        tdGrade = 13.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "التحليل العددي (Analyse Numérique)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 2.0,
                        credit = 4,
                        examGrade = 12.0,
                        tdGrade = 13.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "اللغة الإنجليزية التقنية 3 (English 3)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 17.0,
                        tdGrade = 17.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 3,
                        moduleName = "أخلاقيات المهنة والحقوق الرقمية (Ethique & Déontologie)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 16.0,
                        tdGrade = 16.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),

                    // Semester 4 (L2)
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "شبكات الحاسوب والاتصالات (Réseaux)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 14.5,
                        tdGrade = 15.0,
                        tpGrade = 16.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "هندسة البرمجيات والتصميم (Génie Logiciel)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 15.0,
                        tdGrade = 15.5,
                        tpGrade = 16.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "نظرية اللغات والترجمة الآلية (Théorie des Langages & Compilation)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 13.0,
                        tdGrade = 14.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "تطوير تطبيقات الويب والهاتف (Développement Web & Mobile)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 16.5,
                        tdGrade = 17.0,
                        tpGrade = 18.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "الأمن المعلوماتي والتشفير (Sécurité Informatique)",
                        unitType = "وحدة استكشافية (UED)",
                        coeff = 2.0,
                        credit = 4,
                        examGrade = 14.0,
                        tdGrade = 14.5,
                        tpGrade = 15.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "اللغة الإنجليزية 4 (English 4)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 16.5,
                        tdGrade = 16.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 2,
                        semester = 4,
                        moduleName = "إدارة المشاريع المعلوماتية (Gestion de Projets)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 15.5,
                        tdGrade = 15.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),

                    // Semester 5 (L3)
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "الذكاء الاصطناعي (Intelligence Artificielle)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 16.0,
                        tdGrade = 16.5,
                        tpGrade = 17.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "نظم المعلومات الموزعة (Systèmes Distribués)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 4.0,
                        credit = 6,
                        examGrade = 14.0,
                        tdGrade = 14.5,
                        tpGrade = 15.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "منهجية البحث العلمي وإعداد المذكرة (Méthodologie)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 2.0,
                        credit = 4,
                        examGrade = 15.5,
                        tdGrade = 16.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "البرمجة الخطية وبحوث العمليات (Recherche Opérationnelle)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 3.0,
                        credit = 5,
                        examGrade = 13.5,
                        tdGrade = 14.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "تنقيب البيانات والبيانات الضخمة (Data Mining)",
                        unitType = "وحدة استكشافية (UED)",
                        coeff = 2.0,
                        credit = 5,
                        examGrade = 15.0,
                        tdGrade = 15.5,
                        tpGrade = 16.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "ريادة الأعمال وإنشاء المؤسسات الناشئة (Entrepreneuriat)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 16.0,
                        tdGrade = 16.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 5,
                        moduleName = "اللغة الإنجليزية التخصصية (English for Computing)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 17.5,
                        tdGrade = 17.5,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),

                    // Semester 6 (L3 - PFE & Final Stage)
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 6,
                        moduleName = "مشروع نهاية الدراسة ومذكرة التخرج (Projet de Fin d'Etudes PFE)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 6.0,
                        credit = 14,
                        examGrade = 16.5,
                        tdGrade = 17.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.7
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 6,
                        moduleName = "أمن الشبكات وحماية البيانات (Sécurité des Réseaux)",
                        unitType = "وحدة أساسية (UEF)",
                        coeff = 3.0,
                        credit = 6,
                        examGrade = 14.5,
                        tdGrade = 15.0,
                        tpGrade = 16.0,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 6,
                        moduleName = "الحوسبة السحابية (Cloud Computing & IoT)",
                        unitType = "وحدة منهجية (UEM)",
                        coeff = 3.0,
                        credit = 6,
                        examGrade = 15.0,
                        tdGrade = 15.5,
                        tpGrade = 16.5,
                        hasTp = true,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 6,
                        moduleName = "التوثيق العلمي وحقوق الملكية الفكرية (Propriété Intellectuelle)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 16.0,
                        tdGrade = 16.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    ),
                    ModuleGradeItem(
                        academicYear = 3,
                        semester = 6,
                        moduleName = "التواصل الأكاديمي والعرض الشفهي (Communication & Soutenance)",
                        unitType = "وحدة أفقية (UET)",
                        coeff = 1.0,
                        credit = 2,
                        examGrade = 17.0,
                        tdGrade = 17.0,
                        tpGrade = 0.0,
                        hasTp = false,
                        examWeight = 0.6
                    )
                )
            )
        }

        val currentRefs = db.referenceDao().getAllReferences().firstOrNull()
        if (currentRefs.isNullOrEmpty()) {
            db.referenceDao().insertAll(
                listOf(
                    SavedReference(
                        title = "منهجية البحث العلمي وتطبيقاتها في العلوم الإدارية والاقتصادية",
                        authors = "عبيدات، محمد؛ أبو نصار، محمد؛ عقلة، نعيم",
                        year = "2020",
                        sourceOrPublisher = "دار وائل للنشر والتوزيع، عمان، الأردن",
                        referenceType = "كتاب (Book)",
                        apaCitation = "عبيدات، م.، أبو نصار، م.، وعقلة، ن. (2020). منهجية البحث العلمي وتطبيقاتها في العلوم الإدارية والاقتصادية. دار وائل للنشر.",
                        ieeeCitation = "[1] M. Obeidat, M. Abu Nassar, and N. Oqla, Research Methodology and Applications, Amman: Dar Wael, 2020.",
                        notes = "مرجع أساسي في الفصل المنهجي لتبرير اختيار المنهج الوصفي التحليلي."
                    ),
                    SavedReference(
                        title = "Artificial Intelligence in Higher Education: A Comprehensive Review",
                        authors = "Zawacki-Richter, Olaf; Marín, Victoria; Bond, Melissa",
                        year = "2019",
                        sourceOrPublisher = "International Journal of Educational Technology in Higher Education, Vol. 16, No. 1",
                        referenceType = "مقال علمي (Journal Article)",
                        apaCitation = "Zawacki-Richter, O., Marín, V. I., & Bond, M. (2019). Systematic review of research on artificial intelligence applications in higher education. IJETHE, 16(1), 1-27.",
                        ieeeCitation = "[2] O. Zawacki-Richter, V. I. Marín, and M. Bond, \"Systematic review of AI in higher education,\" Int. J. Educ. Technol. High. Educ., vol. 16, no. 1, pp. 1-27, 2019.",
                        urlOrDoi = "https://doi.org/10.1186/s41239-019-0171-0",
                        notes = "دراسة سابقة ممتازة للاستشهاد بها في مقدمة المذكرة."
                    )
                )
            )
        }

        val currentLinks = db.resourceLinkDao().getAllLinks().firstOrNull()
        if (currentLinks.isNullOrEmpty()) {
            db.resourceLinkDao().insertAll(
                listOf(
                    StudyResourceLink(
                        title = "منصة بروغرس PROGRES (وزارة التعليم العالي MESRS)",
                        url = "https://progres.mesrs.dz/webfve/",
                        category = "بوابات جامعية",
                        description = "البوابة المركزية الرسمية للاطلاع على كشوف النقاط، كشوف إعادة التسجيل، والخدمات البيداغوجية والجامعية.",
                        isFavorite = true
                    ),
                    StudyResourceLink(
                        title = "منصة التعليم الإلكتروني Moodle الجامعي",
                        url = "https://moodle.mesrs.dz/",
                        category = "منصات ومكتبات",
                        description = "بوابة المحاضرات والدروس الرقمية، ملفات الأعمال الموجهة TD والتطبيقية TP المرفوعة من أساتذة المقاييس.",
                        isFavorite = true
                    ),
                    StudyResourceLink(
                        title = "النظام الوطني للتوثيق العلمي عبر الإنترنت SNDL (CERIST)",
                        url = "https://www.sndl.cerist.dz/",
                        category = "منصات ومكتبات",
                        description = "المكتبة الرقمية الوطنية الشاملة للوصول إلى المجلات وقواعد البيانات العالمية (ScienceDirect, IEEE, Springer, Scopus).",
                        isFavorite = true
                    ),
                    StudyResourceLink(
                        title = "بوابة المجلات العلمية الجزائرية ASJP",
                        url = "https://www.asjp.cerist.dz/",
                        category = "أدوات ومواقع بحثية",
                        description = "المنصة الرسمية الوطنية لنشر وتصفح وتحميل مقالات المجلات المحكمة للجامعات والمخابر الجزائرية.",
                        isFavorite = true
                    ),
                    StudyResourceLink(
                        title = "المستودع الوطني للأطروحات والمذكرات DSpace",
                        url = "https://weblis.cerist.dz/",
                        category = "أدوات ومواقع بحثية",
                        description = "أرشيف وقاعدة بيانات أطروحات الدكتوراه ومذكرات الماستر المودعة بالجامعات الجزائرية.",
                        isFavorite = false
                    ),
                    StudyResourceLink(
                        title = "الباحث العلمي Google Scholar",
                        url = "https://scholar.google.com/",
                        category = "أدوات ومواقع بحثية",
                        description = "محرك البحث الأكاديمي العالمي للبحث في المقالات والكتب والاقتباسات وحساب معاملات التأثير.",
                        isFavorite = false
                    ),
                    StudyResourceLink(
                        title = "شبكة الباحثين الأكاديمية ResearchGate",
                        url = "https://www.researchgate.net/",
                        category = "أدوات ومواقع بحثية",
                        description = "شبكة التواصل العلمي العالمية لمتابعة الأساتذة والباحثين وتحميل الأوراق والمشاريع البحثية.",
                        isFavorite = false
                    ),
                    StudyResourceLink(
                        title = "دليل المجلات العلمية المفتوحة DOAJ",
                        url = "https://doaj.org/",
                        category = "مواد دراسية وملخصات",
                        description = "دليل شامل ومجاني للمجلات العلمية والأوراق المفهرسة ذات الوصول الحر والمجاني.",
                        isFavorite = false
                    )
                )
            )
        }
    }
}
