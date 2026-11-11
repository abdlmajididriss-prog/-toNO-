package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "أفكار", // أفكار, عمل, شخصي, مهم, دراسة
    val colorHex: String = "#FEF3C7", // pastel accent color
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
