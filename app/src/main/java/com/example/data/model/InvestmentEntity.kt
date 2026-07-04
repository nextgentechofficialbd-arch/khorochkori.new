package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey val id: String,
    val platformName: String,
    val amount: Double,
    val date: String, // YYYY-MM-DD
    val returnRate: Double?, // Return percentage
    val status: String, // "active" or "withdrawn"
    val note: String
)
