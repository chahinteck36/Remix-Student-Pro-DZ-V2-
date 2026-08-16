package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ScheduleItem::class,
        ExamItem::class,
        TaskItem::class,
        SemesterTask::class,
        AttendanceRecord::class,
        ModuleGradeItem::class,
        SavedReference::class,
        UserProfile::class,
        StudyResourceLink::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun examDao(): ExamDao
    abstract fun taskDao(): TaskDao
    abstract fun semesterTaskDao(): SemesterTaskDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun gradeDao(): GradeDao
    abstract fun referenceDao(): ReferenceDao
    abstract fun userDao(): UserDao
    abstract fun resourceLinkDao(): StudyResourceLinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_pro_dz_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
