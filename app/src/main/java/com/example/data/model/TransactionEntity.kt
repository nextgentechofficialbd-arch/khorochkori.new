package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String,
    val paymentMethod: String,
    val date: String, // "YYYY-MM-DD"
    val note: String
)
