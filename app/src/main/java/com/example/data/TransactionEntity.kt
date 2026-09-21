package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "EXPENSE", // "EXPENSE" or "INCOME"
    val amount: Double,
    val categoryKey: String,
    val categoryName: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // e.g. "21/09/2026"
    val monthKey: String // e.g. "2026-09"
)
