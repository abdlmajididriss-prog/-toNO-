package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.NoteEntity
import com.example.data.local.entities.PlannerEventEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

class OrganizerRepository(private val database: AppDatabase) {

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
    suspend fun insertTask(task: TaskEntity): Long = database.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskEntity) = database.taskDao().updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = database.taskDao().deleteTask(task)
    suspend fun deleteTaskById(id: Long) = database.taskDao().deleteTaskById(id)
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean) = database.taskDao().updateCompletionStatus(id, isCompleted)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = database.noteDao().getAllNotes()
    suspend fun insertNote(note: NoteEntity): Long = database.noteDao().insertNote(note)
    suspend fun updateNote(note: NoteEntity) = database.noteDao().updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = database.noteDao().deleteNote(note)
    suspend fun deleteNoteById(id: Long) = database.noteDao().deleteNoteById(id)
    suspend fun updateNotePin(id: Long, isPinned: Boolean) = database.noteDao().updatePinStatus(id, isPinned)

    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = database.transactionDao().getAllTransactions()
    suspend fun insertTransaction(transaction: TransactionEntity): Long = database.transactionDao().insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = database.transactionDao().updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = database.transactionDao().deleteTransaction(transaction)
    suspend fun deleteTransactionById(id: Long) = database.transactionDao().deleteTransactionById(id)

    // Planner Events
    val allEvents: Flow<List<PlannerEventEntity>> = database.plannerEventDao().getAllEvents()
    fun getEventsByDate(date: String): Flow<List<PlannerEventEntity>> = database.plannerEventDao().getEventsByDate(date)
    suspend fun insertEvent(event: PlannerEventEntity): Long = database.plannerEventDao().insertEvent(event)
    suspend fun updateEvent(event: PlannerEventEntity) = database.plannerEventDao().updateEvent(event)
    suspend fun deleteEvent(event: PlannerEventEntity) = database.plannerEventDao().deleteEvent(event)
    suspend fun deleteEventById(id: Long) = database.plannerEventDao().deleteEventById(id)
    suspend fun updateEventStatus(id: Long, isDone: Boolean) = database.plannerEventDao().updateEventStatus(id, isDone)
}
