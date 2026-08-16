package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_items ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllScheduleItems(): Flow<List<ScheduleItem>>

    @Query("SELECT * FROM schedule_items WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getScheduleItemsForDay(day: Int): Flow<List<ScheduleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ScheduleItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ScheduleItem>)

    @Update
    suspend fun updateItem(item: ScheduleItem)

    @Delete
    suspend fun deleteItem(item: ScheduleItem)

    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exam_items ORDER BY examDate ASC, examTime ASC")
    fun getAllExams(): Flow<List<ExamItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exams: List<ExamItem>)

    @Update
    suspend fun updateExam(exam: ExamItem)

    @Delete
    suspend fun deleteExam(exam: ExamItem)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_items ORDER BY isDone ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)
}

@Dao
interface SemesterTaskDao {
    @Query("SELECT * FROM semester_tasks ORDER BY isCompleted ASC, deadlineDate ASC, deadlineTime ASC")
    fun getAllSemesterTasks(): Flow<List<SemesterTask>>

    @Query("SELECT * FROM semester_tasks WHERE semester = :semester ORDER BY isCompleted ASC, deadlineDate ASC, deadlineTime ASC")
    fun getTasksForSemester(semester: Int): Flow<List<SemesterTask>>

    @Query("SELECT * FROM semester_tasks WHERE isCompleted = 0 ORDER BY deadlineDate ASC, deadlineTime ASC")
    fun getPendingTasks(): Flow<List<SemesterTask>>

    @Query("SELECT * FROM semester_tasks WHERE isCompleted = 1 ORDER BY completionTimestamp DESC")
    fun getCompletedTasks(): Flow<List<SemesterTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: SemesterTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<SemesterTask>)

    @Update
    suspend fun updateTask(task: SemesterTask)

    @Delete
    suspend fun deleteTask(task: SemesterTask)

    @Query("UPDATE semester_tasks SET isCompleted = :isCompleted, completionTimestamp = :timestamp WHERE id = :id")
    suspend fun setTaskCompletion(id: Int, isCompleted: Boolean, timestamp: Long?)

    @Query("DELETE FROM semester_tasks WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records ORDER BY subjectName ASC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun updateAttendance(record: AttendanceRecord)

    @Delete
    suspend fun deleteAttendance(record: AttendanceRecord)
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM module_grades ORDER BY academicYear ASC, semester ASC, moduleName ASC")
    fun getAllGrades(): Flow<List<ModuleGradeItem>>

    @Query("SELECT * FROM module_grades WHERE semester = :semester ORDER BY moduleName ASC")
    fun getGradesForSemester(semester: Int): Flow<List<ModuleGradeItem>>

    @Query("SELECT * FROM module_grades WHERE academicYear = :year ORDER BY semester ASC, moduleName ASC")
    fun getGradesForYear(year: Int): Flow<List<ModuleGradeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: ModuleGradeItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(grades: List<ModuleGradeItem>)

    @Update
    suspend fun updateGrade(grade: ModuleGradeItem)

    @Delete
    suspend fun deleteGrade(grade: ModuleGradeItem)

    @Query("DELETE FROM module_grades WHERE semester = :semester")
    suspend fun deleteBySemester(semester: Int)

    @Query("DELETE FROM module_grades")
    suspend fun deleteAllGrades()
}

@Dao
interface ReferenceDao {
    @Query("SELECT * FROM saved_references ORDER BY timestamp DESC")
    fun getAllReferences(): Flow<List<SavedReference>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReference(ref: SavedReference): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(refs: List<SavedReference>)

    @Update
    suspend fun updateReference(ref: SavedReference)

    @Delete
    suspend fun deleteReference(ref: SavedReference)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}

@Dao
interface StudyResourceLinkDao {
    @Query("SELECT * FROM study_resource_links ORDER BY isFavorite DESC, timestamp DESC")
    fun getAllLinks(): Flow<List<StudyResourceLink>>

    @Query("SELECT * FROM study_resource_links WHERE category = :category ORDER BY isFavorite DESC, timestamp DESC")
    fun getLinksByCategory(category: String): Flow<List<StudyResourceLink>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: StudyResourceLink): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(links: List<StudyResourceLink>)

    @Update
    suspend fun updateLink(link: StudyResourceLink)

    @Delete
    suspend fun deleteLink(link: StudyResourceLink)

    @Query("UPDATE study_resource_links SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM study_resource_links WHERE id = :id")
    suspend fun deleteById(id: Int)
}
