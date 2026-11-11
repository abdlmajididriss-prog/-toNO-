package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // INCOME or EXPENSE
    val category: String, // راتب, عمل حر, استثمار, طعام, فواتير, مواصلات, تسوق, ترفيه, صحة, أخرى
    val date: String, // YYYY-MM-DD
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
