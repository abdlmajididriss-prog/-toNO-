package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val category: String = "عام",    // عمل, شخصي, دراسة, صحة, تسوق, عام
    val dueDate: String = "",        // YYYY-MM-DD
    val dueTime: String = "",        // HH:mm
    val isCompleted: Boolean = false,
    val isReminderEnabled: Boolean = false,
    val reminderMinutesBefore: Int = 0, // 0 = at due time, 15, 30, 60
    val createdAt: Long = System.currentTimeMillis()
)
