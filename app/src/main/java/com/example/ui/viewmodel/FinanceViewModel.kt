package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.UserProfile
import com.example.data.model.TransactionEntity
import com.example.data.model.LendBorrowEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.repository.BackupPayload
import com.example.data.repository.FinanceRepository
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.data.model.InvestmentEntity
import com.example.data.model.Category
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class UserProfileJson(
    val name: String,
    val userType: String,
    val isStrictMode: Boolean,
    val dailyLimit: Double,
    val monthlyLimit: Double,
    val dailySpentToday: Double,
    val monthlySpentThisMonth: Double,
    val lastDailyResetDate: String,
    val lastMonthlyResetDate: String,
    val isLimitAlertEnabled: Boolean,
    val isDueDateAlertEnabled: Boolean,
    val isMonthlySummaryEnabled: Boolean
)

@JsonClass(generateAdapter = true)
data class TransactionJson(
    val id: String,
    val type: String,
    val amount: Double,
    val category: String,
    val paymentMethod: String,
    val date: String,
    val note: String
)

@JsonClass(generateAdapter = true)
data class LendBorrowJson(
    val id: String,
    val contactName: String,
    val type: String,
    val amount: Double,
    val date: String,
    val dueDate: String,
    val note: String,
    val status: String,
    val repaymentHistory: String
)

@JsonClass(generateAdapter = true)
data class SavingsGoalJson(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: String
)

@JsonClass(generateAdapter = true)
data class BackupPayloadJson(
    val userProfile: UserProfileJson?,
    val transactions: List<TransactionJson>,
    val lendBorrows: List<LendBorrowJson>,
    val savingsGoals: List<SavingsGoalJson>,
    val exportedAt: String
)

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _expenseCategories = MutableStateFlow<List<Category>>(emptyList())
    val expenseCategories: StateFlow<List<Category>> = _expenseCategories.asStateFlow()

    private val _incomeCategories = MutableStateFlow<List<Category>>(emptyList())
    val incomeCategories: StateFlow<List<Category>> = _incomeCategories.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        _expenseCategories.value = repository.loadCategories("expense")
        _incomeCategories.value = repository.loadCategories("income")
    }

    fun addCategory(type: String, nameBn: String, nameEn: String, iconName: String) {
        val currentList = if (type == "expense") _expenseCategories.value else _incomeCategories.value
        val newId = "custom_${UUID.randomUUID().toString().take(6)}"
        val newList = currentList + Category(newId, nameBn, nameEn, iconName)
        repository.saveCategories(type, newList)
        loadCategories()
    }

    fun deleteCategory(type: String, id: String) {
        val currentList = if (type == "expense") _expenseCategories.value else _incomeCategories.value
        val newList = currentList.filter { it.id != id }
        repository.saveCategories(type, newList)
        loadCategories()
    }

    fun editCategory(type: String, id: String, nameBn: String, nameEn: String, iconName: String) {
        val currentList = if (type == "expense") _expenseCategories.value else _incomeCategories.value
        val newList = currentList.map { 
            if (it.id == id) Category(id, nameBn, nameEn, iconName) else it
        }
        repository.saveCategories(type, newList)
        loadCategories()
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLendBorrows: StateFlow<List<LendBorrowEntity>> = repository.allLendBorrows
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<SavingsGoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInvestments: StateFlow<List<InvestmentEntity>> = repository.allInvestments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter = moshi.adapter(BackupPayloadJson::class.java)

    fun editTransaction(old: TransactionEntity, type: String, amount: Double, category: String, paymentMethod: String, date: String, note: String) {
        viewModelScope.launch {
            val updated = TransactionEntity(
                id = old.id,
                type = type,
                amount = amount,
                category = category,
                paymentMethod = paymentMethod,
                date = date.ifEmpty { repository.getTodayDateString() },
                note = note
            )
            repository.updateTransaction(old, updated)
        }
    }

    fun createProfile(name: String, userType: String) {
        viewModelScope.launch {
            val profile = UserProfile(
                name = name,
                userType = userType,
                lastDailyResetDate = repository.getTodayDateString(),
                lastMonthlyResetDate = repository.getCurrentMonthString()
            )
            repository.insertProfile(profile)
        }
    }

    fun updateProfileName(newName: String) {
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.updateProfile(current.copy(name = newName))
            }
        }
    }

    fun toggleStrictMode() {
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.updateProfile(current.copy(isStrictMode = !current.isStrictMode))
            }
        }
    }

    fun saveLimits(daily: Double, monthly: Double) {
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.updateProfile(current.copy(dailyLimit = daily, monthlyLimit = monthly))
            }
        }
    }

    fun toggleNotifSetting(type: String) {
        viewModelScope.launch {
            userProfile.value?.let { current ->
                val updated = when (type) {
                    "limit_alerts" -> current.copy(isLimitAlertEnabled = !current.isLimitAlertEnabled)
                    "due_date_alerts" -> current.copy(isDueDateAlertEnabled = !current.isDueDateAlertEnabled)
                    "monthly_summary" -> current.copy(isMonthlySummaryEnabled = !current.isMonthlySummaryEnabled)
                    else -> current
                }
                repository.updateProfile(updated)
            }
        }
    }

    fun addTransaction(type: String, amount: Double, category: String, paymentMethod: String, date: String, note: String) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = UUID.randomUUID().toString(),
                type = type,
                amount = amount,
                category = category,
                paymentMethod = paymentMethod,
                date = date.ifEmpty { repository.getTodayDateString() },
                note = note
            )
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun addLendBorrow(contactName: String, type: String, amount: Double, date: String, dueDate: String, note: String) {
        viewModelScope.launch {
            val entry = LendBorrowEntity(
                id = UUID.randomUUID().toString(),
                contactName = contactName,
                type = type,
                amount = amount,
                date = date.ifEmpty { repository.getTodayDateString() },
                dueDate = dueDate,
                note = note,
                status = "pending"
            )
            repository.insertLendBorrow(entry)
        }
    }

    fun editLendBorrow(entry: LendBorrowEntity, contactName: String, type: String, amount: Double, date: String, dueDate: String, note: String) {
        viewModelScope.launch {
            val updated = entry.copy(
                contactName = contactName,
                type = type,
                amount = amount,
                date = date,
                dueDate = dueDate,
                note = note
            )
            repository.insertLendBorrow(updated)
        }
    }

    fun deleteLendBorrow(id: String) {
        viewModelScope.launch {
            repository.deleteLendBorrow(id)
        }
    }

    fun recordRepayment(id: String, amount: Double) {
        viewModelScope.launch {
            val entry = repository.allLendBorrows.first().find { it.id == id } ?: return@launch
            val today = repository.getTodayDateString()
            val repaymentString = if (entry.repaymentHistory.isEmpty()) {
                "$today:$amount"
            } else {
                "${entry.repaymentHistory},$today:$amount"
            }
            
            val updatedHistory = entry.copy(repaymentHistory = repaymentString)
            val remaining = updatedHistory.getRemainingAmount()
            val newStatus = if (remaining <= 0.0) "settled" else "partial"
            
            repository.insertLendBorrow(updatedHistory.copy(status = newStatus))
        }
    }

    fun markFullySettled(id: String) {
        viewModelScope.launch {
            val entry = repository.allLendBorrows.first().find { it.id == id } ?: return@launch
            val remaining = entry.getRemainingAmount()
            val today = repository.getTodayDateString()
            val repaymentString = if (remaining > 0.0) {
                if (entry.repaymentHistory.isEmpty()) {
                    "$today:$remaining"
                } else {
                    "${entry.repaymentHistory},$today:$remaining"
                }
            } else {
                entry.repaymentHistory
            }
            
            repository.insertLendBorrow(entry.copy(status = "settled", repaymentHistory = repaymentString))
        }
    }

    fun addSavingsGoal(title: String, targetAmount: Double, currentAmount: Double, deadline: String) {
        viewModelScope.launch {
            val goal = SavingsGoalEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = deadline
            )
            repository.insertGoal(goal)
        }
    }

    fun editSavingsGoal(id: String, title: String, targetAmount: Double, currentAmount: Double, deadline: String) {
        viewModelScope.launch {
            val goal = SavingsGoalEntity(
                id = id,
                title = title,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = deadline
            )
            repository.insertGoal(goal)
        }
    }

    fun addSavingsAmount(goal: SavingsGoalEntity, depositAmount: Double) {
        viewModelScope.launch {
            val updated = goal.copy(currentAmount = goal.currentAmount + depositAmount)
            repository.insertGoal(updated)
        }
    }

    fun deleteSavingsGoal(id: String) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    fun addInvestment(platformName: String, amount: Double, date: String, returnRate: Double?, status: String, note: String) {
        viewModelScope.launch {
            val investment = InvestmentEntity(
                id = UUID.randomUUID().toString(),
                platformName = platformName,
                amount = amount,
                date = date.ifEmpty { repository.getTodayDateString() },
                returnRate = returnRate,
                status = status,
                note = note
            )
            repository.insertInvestment(investment)
        }
    }

    fun deleteInvestment(id: String) {
        viewModelScope.launch {
            repository.deleteInvestment(id)
        }
    }

    fun editInvestment(id: String, platformName: String, amount: Double, date: String, returnRate: Double?, status: String, note: String) {
        viewModelScope.launch {
            val updated = InvestmentEntity(
                id = id,
                platformName = platformName,
                amount = amount,
                date = date.ifEmpty { repository.getTodayDateString() },
                returnRate = returnRate,
                status = status,
                note = note
            )
            repository.insertInvestment(updated)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // Export payload to JSON string
    suspend fun exportBackupJson(): String {
        val backup = repository.getFullBackupData()
        val jsonPayload = BackupPayloadJson(
            userProfile = backup.userProfile?.let {
                UserProfileJson(
                    name = it.name,
                    userType = it.userType,
                    isStrictMode = it.isStrictMode,
                    dailyLimit = it.dailyLimit,
                    monthlyLimit = it.monthlyLimit,
                    dailySpentToday = it.dailySpentToday,
                    monthlySpentThisMonth = it.monthlySpentThisMonth,
                    lastDailyResetDate = it.lastDailyResetDate,
                    lastMonthlyResetDate = it.lastMonthlyResetDate,
                    isLimitAlertEnabled = it.isLimitAlertEnabled,
                    isDueDateAlertEnabled = it.isDueDateAlertEnabled,
                    isMonthlySummaryEnabled = it.isMonthlySummaryEnabled
                )
            },
            transactions = backup.transactions.map {
                TransactionJson(it.id, it.type, it.amount, it.category, it.paymentMethod, it.date, it.note)
            },
            lendBorrows = backup.lendBorrows.map {
                LendBorrowJson(it.id, it.contactName, it.type, it.amount, it.date, it.dueDate, it.note, it.status, it.repaymentHistory)
            },
            savingsGoals = backup.savingsGoals.map {
                SavingsGoalJson(it.id, it.title, it.targetAmount, it.currentAmount, it.deadline)
            },
            exportedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())
        )
        return jsonAdapter.toJson(jsonPayload)
    }

    // Import payload from JSON string
    fun importBackupJson(jsonStr: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val payloadJson = jsonAdapter.fromJson(jsonStr)
                if (payloadJson == null) {
                    onError("Invalid JSON format")
                    return@launch
                }
                val domainPayload = BackupPayload(
                    userProfile = payloadJson.userProfile?.let {
                        UserProfile(
                            id = "current",
                            name = it.name,
                            userType = it.userType,
                            isStrictMode = it.isStrictMode,
                            dailyLimit = it.dailyLimit,
                            monthlyLimit = it.monthlyLimit,
                            dailySpentToday = it.dailySpentToday,
                            monthlySpentThisMonth = it.monthlySpentThisMonth,
                            lastDailyResetDate = it.lastDailyResetDate,
                            lastMonthlyResetDate = it.lastMonthlyResetDate,
                            isLimitAlertEnabled = it.isLimitAlertEnabled,
                            isDueDateAlertEnabled = it.isDueDateAlertEnabled,
                            isMonthlySummaryEnabled = it.isMonthlySummaryEnabled
                        )
                    },
                    transactions = payloadJson.transactions.map {
                        TransactionEntity(it.id, it.type, it.amount, it.category, it.paymentMethod, it.date, it.note)
                    },
                    lendBorrows = payloadJson.lendBorrows.map {
                        LendBorrowEntity(it.id, it.contactName, it.type, it.amount, it.date, it.dueDate, it.note, it.status, it.repaymentHistory)
                    },
                    savingsGoals = payloadJson.savingsGoals.map {
                        SavingsGoalEntity(it.id, it.title, it.targetAmount, it.currentAmount, it.deadline)
                    }
                )
                repository.restoreBackupData(domainPayload)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error parsing file")
            }
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
