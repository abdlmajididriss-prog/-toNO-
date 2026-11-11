package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.NoteEntity
import com.example.data.local.entities.PlannerEventEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.repository.OrganizerRepository
import com.example.ui.util.ArabicDateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    DASHBOARD("الرئيسة"),
    TASKS("المهام"),
    PLANNER("المخطط"),
    FINANCE("الميزانية"),
    NOTES("الملاحظات")
}

enum class ThemeMode(val title: String) {
    SYSTEM("تلقائي"),
    LIGHT("فاتح"),
    DARK("داكن")
}

sealed class DeleteTarget {
    data class Task(val task: TaskEntity) : DeleteTarget()
    data class Note(val note: NoteEntity) : DeleteTarget()
    data class Transaction(val transaction: TransactionEntity) : DeleteTarget()
    data class Event(val event: PlannerEventEntity) : DeleteTarget()
}

class OrganizerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrganizerRepository
    private val prefs = application.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    init {
        val db = AppDatabase.getInstance(application)
        repository = OrganizerRepository(db)
    }

    // Theme Mode
    private val _themeMode = MutableStateFlow(
        try {
            ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    // Selected Navigation Tab
    private val _selectedTab = MutableStateFlow(AppTab.DASHBOARD)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    // Reactive Data from Repository
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<PlannerEventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Date State
    private val _selectedCalendarDate = MutableStateFlow(ArabicDateUtils.getTodayDateString())
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    fun setSelectedCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    // Tasks Filtering
    val taskSearchQuery = MutableStateFlow("")
    val taskFilterStatus = MutableStateFlow("ALL") // ALL, PENDING, COMPLETED
    val taskFilterCategory = MutableStateFlow("الكل") // الكل, عمل, شخصي, دراسة, صحة, تسوق, عام

    // Notes Filtering
    val noteSearchQuery = MutableStateFlow("")
    val noteFilterCategory = MutableStateFlow("الكل")

    // Finance Filtering
    val financeFilterType = MutableStateFlow("ALL") // ALL, INCOME, EXPENSE
    val financeFilterMonth = MutableStateFlow(ArabicDateUtils.getCurrentMonthPrefix())

    // Global Search State
    val isGlobalSearchOpen = MutableStateFlow(false)
    val globalSearchQuery = MutableStateFlow("")
    val globalSearchFilter = MutableStateFlow("ALL") // ALL, TASKS, NOTES

    fun openGlobalSearch(initialQuery: String = "") {
        globalSearchQuery.value = initialQuery
        isGlobalSearchOpen.value = true
    }

    fun closeGlobalSearch() {
        isGlobalSearchOpen.value = false
        globalSearchQuery.value = ""
    }

    // Dialogs & Editing State
    val showTaskDialog = MutableStateFlow(false)
    val taskToEdit = MutableStateFlow<TaskEntity?>(null)

    val showNoteDialog = MutableStateFlow(false)
    val noteToEdit = MutableStateFlow<NoteEntity?>(null)

    val showTransactionDialog = MutableStateFlow(false)
    val transactionToEdit = MutableStateFlow<TransactionEntity?>(null)

    val showEventDialog = MutableStateFlow(false)
    val eventToEdit = MutableStateFlow<PlannerEventEntity?>(null)

    val deleteTarget = MutableStateFlow<DeleteTarget?>(null)

    // --- Task Actions ---
    fun openNewTaskDialog(defaultDueDate: String = ArabicDateUtils.getTodayDateString()) {
        taskToEdit.value = TaskEntity(title = "", dueDate = defaultDueDate)
        showTaskDialog.value = true
    }

    fun openEditTaskDialog(task: TaskEntity) {
        taskToEdit.value = task
        showTaskDialog.value = true
    }

    fun dismissTaskDialog() {
        showTaskDialog.value = false
        taskToEdit.value = null
    }

    fun saveTask(task: TaskEntity) {
        viewModelScope.launch {
            val savedTask = if (task.id == 0L) {
                val newId = repository.insertTask(task)
                task.copy(id = newId)
            } else {
                repository.updateTask(task)
                task
            }
            com.example.service.TaskReminderScheduler.scheduleReminder(getApplication(), savedTask)
            dismissTaskDialog()
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            repository.updateTaskStatus(task.id, newCompleted)
            val updatedTask = task.copy(isCompleted = newCompleted)
            com.example.service.TaskReminderScheduler.scheduleReminder(getApplication(), updatedTask)
        }
    }

    fun triggerTestReminderNotification() {
        com.example.service.TaskReminderScheduler.sendImmediateTestNotification(getApplication())
    }

    fun confirmDelete(target: DeleteTarget) {
        deleteTarget.value = target
    }

    fun dismissDeleteConfirm() {
        deleteTarget.value = null
    }

    fun executeDelete() {
        val target = deleteTarget.value ?: return
        viewModelScope.launch {
            when (target) {
                is DeleteTarget.Task -> {
                    com.example.service.TaskReminderScheduler.cancelReminder(getApplication(), target.task.id)
                    repository.deleteTask(target.task)
                }
                is DeleteTarget.Note -> repository.deleteNote(target.note)
                is DeleteTarget.Transaction -> repository.deleteTransaction(target.transaction)
                is DeleteTarget.Event -> repository.deleteEvent(target.event)
            }
            deleteTarget.value = null
        }
    }

    // --- Note Actions ---
    fun openNewNoteDialog() {
        noteToEdit.value = NoteEntity(title = "", content = "")
        showNoteDialog.value = true
    }

    fun openEditNoteDialog(note: NoteEntity) {
        noteToEdit.value = note
        showNoteDialog.value = true
    }

    fun dismissNoteDialog() {
        showNoteDialog.value = false
        noteToEdit.value = null
    }

    fun saveNote(note: NoteEntity) {
        viewModelScope.launch {
            val updated = note.copy(updatedAt = System.currentTimeMillis())
            if (updated.id == 0L) {
                repository.insertNote(updated)
            } else {
                repository.updateNote(updated)
            }
            dismissNoteDialog()
        }
    }

    fun toggleNotePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNotePin(note.id, !note.isPinned)
        }
    }

    // --- Transaction Actions ---
    fun openNewTransactionDialog(defaultType: String = "EXPENSE") {
        transactionToEdit.value = TransactionEntity(
            title = "",
            amount = 0.0,
            type = defaultType,
            category = if (defaultType == "INCOME") "راتب" else "طعام",
            date = ArabicDateUtils.getTodayDateString()
        )
        showTransactionDialog.value = true
    }

    fun openEditTransactionDialog(transaction: TransactionEntity) {
        transactionToEdit.value = transaction
        showTransactionDialog.value = true
    }

    fun dismissTransactionDialog() {
        showTransactionDialog.value = false
        transactionToEdit.value = null
    }

    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            if (transaction.id == 0L) {
                repository.insertTransaction(transaction)
            } else {
                repository.updateTransaction(transaction)
            }
            dismissTransactionDialog()
        }
    }

    // --- Planner Actions ---
    fun openNewEventDialog(defaultDate: String = _selectedCalendarDate.value) {
        eventToEdit.value = PlannerEventEntity(
            title = "",
            date = defaultDate,
            startTime = "10:00",
            endTime = "11:00",
            tag = "اجتماع"
        )
        showEventDialog.value = true
    }

    fun openEditEventDialog(event: PlannerEventEntity) {
        eventToEdit.value = event
        showEventDialog.value = true
    }

    fun dismissEventDialog() {
        showEventDialog.value = false
        eventToEdit.value = null
    }

    fun saveEvent(event: PlannerEventEntity) {
        viewModelScope.launch {
            if (event.id == 0L) {
                repository.insertEvent(event)
            } else {
                repository.updateEvent(event)
            }
            dismissEventDialog()
        }
    }

    fun toggleEventDone(event: PlannerEventEntity) {
        viewModelScope.launch {
            repository.updateEventStatus(event.id, !event.isDone)
        }
    }
}
