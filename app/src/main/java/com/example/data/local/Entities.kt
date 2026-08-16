package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int, // 0: Sunday (الأحد), 1: Monday (الإثنين), 2: Tuesday (الثلاثاء), 3: Wednesday (الأربعاء), 4: Thursday (الخميس), 5: Saturday (السبت)
    val subjectName: String,
    val sessionType: String, // محاضرة (Cour), أعمال موجهة (TD), أعمال تطبيقية (TP)
    val roomOrAmphi: String, // قاعة 12, مدرج أ, مخبر 3
    val professorName: String,
    val startTime: String, // 08:00
    val endTime: String, // 09:30
    val colorIndex: Int = 0
)

@Entity(tableName = "exam_items")
data class ExamItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val examDate: String, // YYYY-MM-DD
    val examTime: String, // 09:00
    val roomOrAmphi: String,
    val coeff: Double = 2.0,
    val notes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "task_items")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subjectName: String,
    val category: String, // واجب TD, تقرير TP, مراجعة, بحث, مشروع
    val dueDate: String,
    val priority: String, // عالي, متوسط, منخفض
    val isDone: Boolean = false
)

@Entity(tableName = "semester_tasks")
data class SemesterTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val semester: Int = 1, // 1 to 10
    val title: String,
    val moduleName: String,
    val taskType: String = "واجب TD", // واجب TD, تقرير TP, مراجعة امتحان, مشروع مصغر, مذكرة تخرج, قراءة مرجعية
    val deadlineDate: String, // YYYY-MM-DD
    val deadlineTime: String = "23:59", // HH:mm
    val priority: String = "متوسط", // عالي, متوسط, منخفض
    val isCompleted: Boolean = false,
    val completionTimestamp: Long? = null,
    val estimatedHours: Double = 2.0,
    val notes: String = ""
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val sessionType: String, // TD, TP, Cour
    val maxAllowedAbsences: Int = 3,
    val currentAbsences: Int = 0,
    val excusedAbsences: Int = 0,
    val notes: String = ""
)

@Entity(tableName = "module_grades")
data class ModuleGradeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val academicYear: Int = 1, // 1: L1, 2: L2, 3: L3, 4: M1, 5: M2
    val semester: Int = 1, // 1 to 10 (S1 to S10)
    val moduleName: String,
    val unitType: String = "وحدة أساسية (UEF)", // UEF, UEM, UED, UET
    val coeff: Double = 2.0,
    val credit: Int = 4,
    val examGrade: Double = 10.0, // out of 20
    val tdGrade: Double = 10.0, // out of 20
    val tpGrade: Double = 0.0, // out of 20
    val hasTp: Boolean = false,
    val examWeight: Double = 0.6, // 60% Exam, 40% TD (or 60% Exam + 20% TD + 20% TP)
    val rattrapageGrade: Double = 0.0,
    val isRattrapageUsed: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "saved_references")
data class SavedReference(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val authors: String,
    val year: String,
    val sourceOrPublisher: String,
    val referenceType: String, // كتاب (Book), مقال علمي (Journal Article), مذكرة/أطروحة (Thesis), موقع إلكتروني (Website), مرسوم/قانون (Law)
    val apaCitation: String,
    val ieeeCitation: String,
    val urlOrDoi: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "محمد أمين بن علي",
    val university: String = "جامعة الجزائر 1 - بن يوسف بن خدة",
    val faculty: String = "كلية العلوم والتكنولوجيا",
    val department: String = "قسم الإعلام الآلي",
    val specialty: String = "إعلام آلي - ذكاء اصطناعي",
    val academicLevel: String = "ماستر 2 (Master 2)",
    val studentIdNumber: String = "202131054890",
    val email: String = "etudiant@univ-alger.dz",
    val phone: String = "0661234567"
)

@Entity(tableName = "study_resource_links")
data class StudyResourceLink(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val url: String,
    val category: String = "بوابات جامعية", // بوابات جامعية, منصات ومكتبات, مواد دراسية وملخصات, أدوات ومواقع بحثية
    val description: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "imported_documents")
data class ImportedDocumentItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileType: String, // PDF, WORD, XML, OTHER
    val mimeType: String,
    val localFilePath: String = "",
    val fileSizeBytes: Long = 0L,
    val category: String = "محاضرات ودروس", // محاضرات ودروس, مذكرات وأطروحات, بحوث ومشاريع, تقارير TP, بيانات XML, كشوف ونماذج, عام
    val previewText: String = "",
    val fullContentText: String = "",
    val notes: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

