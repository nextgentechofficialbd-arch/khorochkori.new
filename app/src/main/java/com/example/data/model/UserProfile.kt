package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "current",
    val name: String,
    val userType: String, // "student", "job", "freelance", "family"
    val isStrictMode: Boolean = false,
    val dailyLimit: Double = 0.0,
    val monthlyLimit: Double = 0.0,
    val dailySpentToday: Double = 0.0,
    val monthlySpentThisMonth: Double = 0.0,
    val lastDailyResetDate: String = "", // "YYYY-MM-DD"
    val lastMonthlyResetDate: String = "", // "YYYY-MM"
    val isLimitAlertEnabled: Boolean = true,
    val isDueDateAlertEnabled: Boolean = true,
    val isMonthlySummaryEnabled: Boolean = true
)
