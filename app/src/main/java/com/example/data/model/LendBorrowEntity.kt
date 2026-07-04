package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lend_borrow")
data class LendBorrowEntity(
    @PrimaryKey val id: String,
    val contactName: String,
    val type: String, // "lent" or "borrowed"
    val amount: Double,
    val date: String, // "YYYY-MM-DD"
    val dueDate: String, // "YYYY-MM-DD" or empty
    val note: String,
    val status: String, // "pending", "partial", "settled"
    val repaymentHistory: String = "" // formatted as "date1:amount1,date2:amount2"
) {
    data class Repayment(val date: String, val amount: Double)

    fun getRepayments(): List<Repayment> {
        if (repaymentHistory.isEmpty()) return emptyList()
        return repaymentHistory.split(",").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                Repayment(parts[0], parts[1].toDoubleOrNull() ?: 0.0)
            } else null
        }
    }

    fun getRemainingAmount(): Double {
        val repaid = getRepayments().sumOf { it.amount }
        return (amount - repaid).coerceAtLeast(0.0)
    }
}
