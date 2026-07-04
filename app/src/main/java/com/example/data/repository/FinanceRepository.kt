package com.example.data.repository

import com.example.data.local.UserProfileDao
import com.example.data.local.TransactionDao
import com.example.data.local.LendBorrowDao
import com.example.data.local.SavingsGoalDao
import com.example.data.local.InvestmentDao
import android.content.Context
import com.example.data.model.Category
import com.example.data.model.EXPENSE_CATEGORIES
import com.example.data.model.INCOME_CATEGORIES
import com.example.data.model.UserProfile
import com.example.data.model.TransactionEntity
import com.example.data.model.LendBorrowEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.InvestmentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepository(
    private val userProfileDao: UserProfileDao,
    private val transactionDao: TransactionDao,
    private val lendBorrowDao: LendBorrowDao,
    private val savingsGoalDao: SavingsGoalDao,
    private val investmentDao: InvestmentDao,
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("khorochkori_prefs", Context.MODE_PRIVATE)

    fun saveCategories(type: String, categories: List<Category>) {
        val serialized = categories.joinToString(";") { "${it.id}|${it.nameBn}|${it.nameEn}|${it.iconName}" }
        sharedPrefs.edit().putString("${type}_categories", serialized).apply()
    }

    fun loadCategories(type: String): List<Category> {
        val serialized = sharedPrefs.getString("${type}_categories", null) ?: return if (type == "expense") EXPENSE_CATEGORIES else INCOME_CATEGORIES
        if (serialized.isEmpty()) return emptyList()
        return serialized.split(";").mapNotNull {
            val parts = it.split("|")
            if (parts.size >= 4) {
                Category(parts[0], parts[1], parts[2], parts[3])
            } else null
        }
    }
    // Expose flow of profile with automatic day/month reset reconciliation
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile().map { profile ->
        if (profile != null) {
            val reconciled = reconcileProfile(profile)
            if (reconciled != profile) {
                userProfileDao.insertProfile(reconciled)
            }
            reconciled
        } else null
    }

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allLendBorrows: Flow<List<LendBorrowEntity>> = lendBorrowDao.getAllLendBorrows()
    val allGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()
    val allInvestments: Flow<List<InvestmentEntity>> = investmentDao.getAllInvestments()

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun getCurrentMonthString(): String {
        return SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    }

    private fun reconcileProfile(profile: UserProfile): UserProfile {
        val today = getTodayDateString()
        val thisMonth = getCurrentMonthString()
        var updated = profile
        if (profile.lastDailyResetDate != today) {
            updated = updated.copy(dailySpentToday = 0.0, lastDailyResetDate = today)
        }
        if (profile.lastMonthlyResetDate != thisMonth) {
            updated = updated.copy(monthlySpentThisMonth = 0.0, lastMonthlyResetDate = thisMonth)
        }
        return updated
    }

    suspend fun insertProfile(profile: UserProfile) {
        val reconciled = reconcileProfile(profile)
        userProfileDao.insertProfile(reconciled)
    }

    suspend fun updateProfile(profile: UserProfile) {
        val reconciled = reconcileProfile(profile)
        userProfileDao.updateProfile(reconciled)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        
        // If it's an expense, update the spent amount for today/this month in UserProfile
        if (transaction.type == "expense") {
            val profile = userProfileDao.getUserProfileSync()
            if (profile != null) {
                val reconciled = reconcileProfile(profile)
                val today = getTodayDateString()
                val thisMonth = getCurrentMonthString()

                var newDailySpent = reconciled.dailySpentToday
                var newMonthlySpent = reconciled.monthlySpentThisMonth

                if (transaction.date == today) {
                    newDailySpent += transaction.amount
                }
                if (transaction.date.startsWith(thisMonth)) {
                    newMonthlySpent += transaction.amount
                }

                userProfileDao.insertProfile(
                    reconciled.copy(
                        dailySpentToday = newDailySpent,
                        monthlySpentThisMonth = newMonthlySpent
                    )
                )
            }
        }
    }

    suspend fun deleteTransaction(id: String) {
        val transaction = transactionDao.getTransactionById(id)
        if (transaction != null) {
            transactionDao.deleteTransactionById(id)

            // If it's an expense, reverse the spent amount in UserProfile
            if (transaction.type == "expense") {
                val profile = userProfileDao.getUserProfileSync()
                if (profile != null) {
                    val reconciled = reconcileProfile(profile)
                    val today = getTodayDateString()
                    val thisMonth = getCurrentMonthString()

                    var newDailySpent = reconciled.dailySpentToday
                    var newMonthlySpent = reconciled.monthlySpentThisMonth

                    if (transaction.date == today) {
                        newDailySpent = (newDailySpent - transaction.amount).coerceAtLeast(0.0)
                    }
                    if (transaction.date.startsWith(thisMonth)) {
                        newMonthlySpent = (newMonthlySpent - transaction.amount).coerceAtLeast(0.0)
                    }

                    userProfileDao.insertProfile(
                        reconciled.copy(
                            dailySpentToday = newDailySpent,
                            monthlySpentThisMonth = newMonthlySpent
                        )
                    )
                }
            }
        }
    }

    suspend fun updateTransaction(oldTransaction: TransactionEntity, newTransaction: TransactionEntity) {
        deleteTransaction(oldTransaction.id)
        insertTransaction(newTransaction)
    }

    suspend fun insertLendBorrow(lendBorrow: LendBorrowEntity) {
        lendBorrowDao.insertLendBorrow(lendBorrow)
    }

    suspend fun deleteLendBorrow(id: String) {
        lendBorrowDao.deleteLendBorrowById(id)
    }

    suspend fun insertGoal(goal: SavingsGoalEntity) {
        savingsGoalDao.insertGoal(goal)
    }

    suspend fun deleteGoal(id: String) {
        savingsGoalDao.deleteGoalById(id)
    }

    suspend fun insertInvestment(investment: InvestmentEntity) {
        investmentDao.insertInvestment(investment)
    }

    suspend fun deleteInvestment(id: String) {
        investmentDao.deleteInvestmentById(id)
    }

    // Direct sync methods for backups
    suspend fun getFullBackupData(): BackupPayload {
        return BackupPayload(
            userProfile = userProfileDao.getUserProfileSync(),
            transactions = transactionDao.getAllTransactionsSync(),
            lendBorrows = lendBorrowDao.getAllLendBorrowsSync(),
            savingsGoals = savingsGoalDao.getAllGoalsSync()
        )
    }

    suspend fun restoreBackupData(payload: BackupPayload) {
        payload.userProfile?.let { userProfileDao.insertProfile(it) }
        
        // Clear existing and insert restored
        transactionDao.clearTransactions()
        payload.transactions.forEach { transactionDao.insertTransaction(it) }

        lendBorrowDao.clearLendBorrows()
        payload.lendBorrows.forEach { lendBorrowDao.insertLendBorrow(it) }

        savingsGoalDao.clearGoals()
        payload.savingsGoals.forEach { savingsGoalDao.insertGoal(it) }
    }

    suspend fun clearAllData() {
        userProfileDao.clearProfile()
        transactionDao.clearTransactions()
        lendBorrowDao.clearLendBorrows()
        savingsGoalDao.clearGoals()
        investmentDao.clearInvestments()
    }
}

data class BackupPayload(
    val userProfile: UserProfile?,
    val transactions: List<TransactionEntity>,
    val lendBorrows: List<LendBorrowEntity>,
    val savingsGoals: List<SavingsGoalEntity>
)
