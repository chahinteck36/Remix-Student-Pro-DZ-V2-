package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SemesterTask
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TaskFilterStatus(val titleAr: String) {
    ALL("الكل"),
    PENDING("قيد الإنجاز"),
    COMPLETED("المكتملة")
}

data class SemesterPlannerUiState(
    val tasks: List<SemesterTask> = emptyList(),
    val filteredTasks: List<SemesterTask> = emptyList(),
    val selectedSemester: Int = 1,
    val selectedTaskType: String? = null,
    val selectedPriority: String? = null,
    val filterStatus: TaskFilterStatus = TaskFilterStatus.ALL,
    val searchQuery: String = "",
    val totalTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val completionPercentage: Float = 0f,
    val upcomingDeadlines: List<SemesterTask> = emptyList(),
    val isLoading: Boolean = false
)

class SemesterPlannerViewModel(
    application: Application,
    private val repository: StudentRepository = StudentRepository(AppDatabase.getDatabase(application))
) : AndroidViewModel(application) {

    private val _selectedSemester = MutableStateFlow(1)
    val selectedSemester: StateFlow<Int> = _selectedSemester.asStateFlow()

    private val _filterStatus = MutableStateFlow(TaskFilterStatus.ALL)
    val filterStatus: StateFlow<TaskFilterStatus> = _filterStatus.asStateFlow()

    private val _selectedTaskType = MutableStateFlow<String?>(null)
    val selectedTaskType: StateFlow<String?> = _selectedTaskType.asStateFlow()

    private val _selectedPriority = MutableStateFlow<String?>(null)
    val selectedPriority: StateFlow<String?> = _selectedPriority.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val rawTasks: StateFlow<List<SemesterTask>> = repository.semesterTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class FilterParams(
        val semester: Int,
        val status: TaskFilterStatus,
        val taskType: String?,
        val priority: String?,
        val searchQuery: String
    )

    private val filterParams = combine(
        _selectedSemester,
        _filterStatus,
        _selectedTaskType,
        _selectedPriority,
        _searchQuery
    ) { semester, status, taskType, priority, query ->
        FilterParams(semester, status, taskType, priority, query)
    }

    val uiState: StateFlow<SemesterPlannerUiState> = combine(
        rawTasks,
        filterParams
    ) { tasks, params ->
        val semesterTasks = tasks.filter { it.semester == params.semester }
        
        val filtered = semesterTasks.filter { task ->
            val matchesStatus = when (params.status) {
                TaskFilterStatus.ALL -> true
                TaskFilterStatus.PENDING -> !task.isCompleted
                TaskFilterStatus.COMPLETED -> task.isCompleted
            }
            val matchesType = params.taskType == null || task.taskType == params.taskType
            val matchesPriority = params.priority == null || task.priority == params.priority
            val matchesQuery = params.searchQuery.isBlank() || 
                task.title.contains(params.searchQuery, ignoreCase = true) ||
                task.moduleName.contains(params.searchQuery, ignoreCase = true) ||
                task.notes.contains(params.searchQuery, ignoreCase = true)

            matchesStatus && matchesType && matchesPriority && matchesQuery
        }

        val total = semesterTasks.size
        val completed = semesterTasks.count { it.isCompleted }
        val pending = total - completed
        val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f

        val upcoming = semesterTasks
            .filter { !it.isCompleted }
            .sortedWith(compareBy({ it.deadlineDate }, { it.deadlineTime }))
            .take(3)

        SemesterPlannerUiState(
            tasks = semesterTasks,
            filteredTasks = filtered,
            selectedSemester = params.semester,
            selectedTaskType = params.taskType,
            selectedPriority = params.priority,
            filterStatus = params.status,
            searchQuery = params.searchQuery,
            totalTasksCount = total,
            completedTasksCount = completed,
            pendingTasksCount = pending,
            completionPercentage = percentage,
            upcomingDeadlines = upcoming,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SemesterPlannerUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun setSelectedSemester(semester: Int) {
        _selectedSemester.value = semester
    }

    fun setFilterStatus(status: TaskFilterStatus) {
        _filterStatus.value = status
    }

    fun setSelectedTaskType(type: String?) {
        _selectedTaskType.value = type
    }

    fun setSelectedPriority(priority: String?) {
        _selectedPriority.value = priority
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addSemesterTask(
        title: String,
        moduleName: String,
        taskType: String = "واجب TD",
        deadlineDate: String,
        deadlineTime: String = "23:59",
        semester: Int = _selectedSemester.value,
        priority: String = "متوسط",
        notes: String = "",
        estimatedHours: Double = 2.0
    ) = viewModelScope.launch {
        val newTask = SemesterTask(
            semester = semester,
            title = title.trim(),
            moduleName = moduleName.trim(),
            taskType = taskType,
            deadlineDate = deadlineDate,
            deadlineTime = deadlineTime,
            priority = priority,
            isCompleted = false,
            estimatedHours = estimatedHours,
            notes = notes.trim()
        )
        repository.insertSemesterTask(newTask)
    }

    fun toggleTaskCompletion(task: SemesterTask) = viewModelScope.launch {
        val newStatus = !task.isCompleted
        repository.setSemesterTaskCompleted(task.id, newStatus)
    }

    fun completeTask(task: SemesterTask) = viewModelScope.launch {
        repository.setSemesterTaskCompleted(task.id, true)
    }

    fun updateTask(task: SemesterTask) = viewModelScope.launch {
        repository.updateSemesterTask(task)
    }

    fun deleteTask(task: SemesterTask) = viewModelScope.launch {
        repository.deleteSemesterTask(task)
    }

    fun deleteTaskById(id: Int) = viewModelScope.launch {
        repository.deleteSemesterTaskById(id)
    }
}
