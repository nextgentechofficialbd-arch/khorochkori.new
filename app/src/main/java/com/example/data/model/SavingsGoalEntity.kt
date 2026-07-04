package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: String // "YYYY-MM-DD" or empty
)
