package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner_events")
data class PlannerEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:mm
    val endTime: String = "", // HH:mm
    val tag: String = "اجتماع", // اجتماع, موعد, دراسة, شخصي, ترفيه
    val location: String = "",
    val notes: String = "",
    val isDone: Boolean = false
)
