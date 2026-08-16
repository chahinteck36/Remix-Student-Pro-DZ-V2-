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
    val importedDocuments: Flow<List<ImportedDocumentItem>> = db.importedDocumentDao().getAllDocuments()
    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()

    // Imported Documents Operations
    fun getDocumentsByType(fileType: String): Flow<List<ImportedDocumentItem>> = db.importedDocumentDao().getDocumentsByType(fileType)
    fun getDocumentsByCategory(category: String): Flow<List<ImportedDocumentItem>> = db.importedDocumentDao().getDocumentsByCategory(category)
    suspend fun getDocumentById(id: Int) = db.importedDocumentDao().getDocumentById(id)
    suspend fun insertImportedDocument(doc: ImportedDocumentItem) = db.importedDocumentDao().insertDocument(doc)
    suspend fun updateImportedDocument(doc: ImportedDocumentItem) = db.importedDocumentDao().updateDocument(doc)
    suspend fun deleteImportedDocument(doc: ImportedDocumentItem) = db.importedDocumentDao().deleteDocument(doc)
    suspend fun deleteImportedDocumentById(id: Int) = db.importedDocumentDao().deleteById(id)
    suspend fun setDocumentFavorite(id: Int, isFav: Boolean) = db.importedDocumentDao().setFavorite(id, isFav)

    // Schedule Operations
    suspend fun insertScheduleItem(item: ScheduleItem) = db.scheduleDao().insertItem(item)
    suspend fun updateScheduleItem(item: ScheduleItem) = db.scheduleDao().updateItem(item)
    suspend fun deleteScheduleItem(item: ScheduleItem) = db.scheduleDao().deleteItem(item)
    suspend fun deleteAllSchedule() = db.scheduleDao().deleteAllSchedule()

    // Exam Operations
    suspend fun insertExam(exam: ExamItem) = db.examDao().insertExam(exam)
    suspend fun updateExam(exam: ExamItem) = db.examDao().updateExam(exam)
    suspend fun deleteExam(exam: ExamItem) = db.examDao().deleteExam(exam)
    suspend fun deleteAllExams() = db.examDao().deleteAllExams()

    // Task Operations
    suspend fun insertTask(task: TaskItem) = db.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskItem) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: TaskItem) = db.taskDao().deleteTask(task)
    suspend fun deleteAllTasks() = db.taskDao().deleteAllTasks()

    // Semester Study Planner Task Operations
    fun getTasksForSemester(semester: Int): Flow<List<SemesterTask>> = db.semesterTaskDao().getTasksForSemester(semester)
    fun getPendingSemesterTasks(): Flow<List<SemesterTask>> = db.semesterTaskDao().getPendingTasks()
    fun getCompletedSemesterTasks(): Flow<List<SemesterTask>> = db.semesterTaskDao().getCompletedTasks()
    suspend fun insertSemesterTask(task: SemesterTask) = db.semesterTaskDao().insertTask(task)
    suspend fun insertSemesterTasks(tasks: List<SemesterTask>) = db.semesterTaskDao().insertAll(tasks)
    suspend fun updateSemesterTask(task: SemesterTask) = db.semesterTaskDao().updateTask(task)
    suspend fun deleteSemesterTask(task: SemesterTask) = db.semesterTaskDao().deleteTask(task)
    suspend fun deleteSemesterTaskById(id: Int) = db.semesterTaskDao().deleteById(id)
    suspend fun deleteAllSemesterTasks() = db.semesterTaskDao().deleteAllSemesterTasks()
    suspend fun setSemesterTaskCompleted(id: Int, isCompleted: Boolean) = 
        db.semesterTaskDao().setTaskCompletion(id, isCompleted, if (isCompleted) System.currentTimeMillis() else null)

    // Attendance Operations
    suspend fun insertAttendance(record: AttendanceRecord) = db.attendanceDao().insertAttendance(record)
    suspend fun updateAttendance(record: AttendanceRecord) = db.attendanceDao().updateAttendance(record)
    suspend fun deleteAttendance(record: AttendanceRecord) = db.attendanceDao().deleteAttendance(record)
    suspend fun deleteAllAttendance() = db.attendanceDao().deleteAllAttendance()

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
    suspend fun deleteAllReferences() = db.referenceDao().deleteAllReferences()

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

    // Reset entire app data to a clean slate
    suspend fun resetAllUserData() {
        db.userDao().insertOrUpdateProfile(
            UserProfile(
                id = 1,
                fullName = "",
                university = "",
                faculty = "",
                department = "",
                specialty = "",
                academicLevel = "",
                studentIdNumber = "",
                email = "",
                phone = ""
            )
        )
        db.gradeDao().deleteAllGrades()
        db.scheduleDao().deleteAllSchedule()
        db.taskDao().deleteAllTasks()
        db.semesterTaskDao().deleteAllSemesterTasks()
        db.examDao().deleteAllExams()
        db.attendanceDao().deleteAllAttendance()
        db.referenceDao().deleteAllReferences()
    }

    // Initialize clean database without any pre-populated personal mock data
    suspend fun checkAndSeedInitialData() {
        val existingUser = db.userDao().getUserProfile().firstOrNull()
        if (existingUser == null || existingUser.fullName.contains("محمد أمين") || existingUser.fullName.contains("أحمد أمين")) {
            resetAllUserData()
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
