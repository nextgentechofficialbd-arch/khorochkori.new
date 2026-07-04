package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.components.getIconByName
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainAppScreen(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val lendBorrows by viewModel.allLendBorrows.collectAsStateWithLifecycle()
    val goals by viewModel.allGoals.collectAsStateWithLifecycle()
    val investments by viewModel.allInvestments.collectAsStateWithLifecycle()
    val expenseCategories by viewModel.expenseCategories.collectAsStateWithLifecycle()
    val incomeCategories by viewModel.incomeCategories.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf("home") } // "home", "transactions", "lending", "investments", "settings"

    // Modal Overlays state
    var txModalVisible by remember { mutableStateOf(false) }
    var txModalType by remember { mutableStateOf("expense") } // "expense" or "income"
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    var investmentModalVisible by remember { mutableStateOf(false) }
    var editingInvestment by remember { mutableStateOf<InvestmentEntity?>(null) }

    var lbModalVisible by remember { mutableStateOf(false) }
    var lbModalType by remember { mutableStateOf("lent") } // "lent" or "borrowed"
    var editingLendBorrow by remember { mutableStateOf<LendBorrowEntity?>(null) }

    var goalModalVisible by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var addSavingsModalVisible by remember { mutableStateOf(false) }
    var selectedGoalForDeposit by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var settleModalVisible by remember { mutableStateOf(false) }
    var selectedSettleId by remember { mutableStateOf("") }

    var warningModalVisible by remember { mutableStateOf(false) }
    var warningTitle by remember { mutableStateOf("") }
    var warningBody by remember { mutableStateOf("") }
    var pendingTxArgs by remember { mutableStateOf<PendingTxArgs?>(null) }

    var showSaveConfirmation by remember { mutableStateOf(false) }
    var saveConfirmationMessage by remember { mutableStateOf("") }

    var backupDialogVisible by remember { mutableStateOf(false) }
    var backupJsonText by remember { mutableStateOf("") }

    var importDialogVisible by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }

    // Onboarding is shown if userProfile in DB is null
    if (userProfile == null) {
        OnboardingScreen { name, userType ->
            viewModel.createProfile(name, userType)
        }
    } else {
        val profile = userProfile!!

        // Check limits if Strict Mode is on and we are adding expense
        fun handleSaveTransaction(
            type: String,
            amount: Double,
            category: String,
            paymentMethod: String,
            date: String,
            note: String
        ) {
            if (editingTransaction != null) {
                viewModel.editTransaction(editingTransaction!!, type, amount, category, paymentMethod, date, note)
                editingTransaction = null
                txModalVisible = false
                saveConfirmationMessage = "লেনদেন এডিট ও সেভ করা হয়েছে"
                showSaveConfirmation = true
                return
            }

            if (type == "expense" && profile.isStrictMode) {
                val newDailySpent = profile.dailySpentToday + amount
                val newMonthlySpent = profile.monthlySpentThisMonth + amount
                val crossDaily = profile.dailyLimit > 0 && newDailySpent > profile.dailyLimit
                val crossMonthly = profile.monthlyLimit > 0 && newMonthlySpent > profile.monthlyLimit

                if (crossDaily || crossMonthly) {
                    val bodyParts = mutableListOf<String>()
                    if (crossDaily) bodyParts.add("আজকের লিমিট")
                    if (crossMonthly) bodyParts.add("এই মাসের লিমিট")

                    warningTitle = "তুমি ${bodyParts.joinToString(" ও ")} ছাড়িয়ে যাচ্ছো"
                    warningBody = "এই খরচ (${formatTaka(amount)}) যোগ করলে তুমি সেট করা লিমিট পার করে ফেলবে। তুমি কী করতে চাও?"
                    pendingTxArgs = PendingTxArgs(type, amount, category, paymentMethod, date, note)
                    warningModalVisible = true
                    return
                }
            }

            viewModel.addTransaction(type, amount, category, paymentMethod, date, note)
            txModalVisible = false
            saveConfirmationMessage = typeToMessage(type)
            showSaveConfirmation = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepVault)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Topbar
                Topbar(
                    isStrictMode = profile.isStrictMode,
                    onModeBadgeClick = { currentScreen = "settings" }
                )

                // Sub-Screen Content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            profile = profile,
                            transactions = transactions,
                            lendBorrows = lendBorrows,
                            goals = goals,
                            expenseCategories = expenseCategories,
                            incomeCategories = incomeCategories,
                            onAddExpense = {
                                editingTransaction = null
                                txModalType = "expense"
                                txModalVisible = true
                            },
                            onAddIncome = {
                                editingTransaction = null
                                txModalType = "income"
                                txModalVisible = true
                            },
                            onAddLendBorrow = {
                                currentScreen = "lending"
                                lbModalType = "lent"
                                lbModalVisible = true
                            },
                            onAddGoal = {
                                goalModalVisible = true
                            },
                            onEditGoal = { goal ->
                                editingGoal = goal
                                goalModalVisible = true
                            },
                            onAddSavings = { goal ->
                                selectedGoalForDeposit = goal
                                addSavingsModalVisible = true
                            },
                            onDeleteTransaction = { id ->
                                viewModel.deleteTransaction(id)
                                Toast.makeText(context, "এন্ট্রি ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onDeleteGoal = { id ->
                                viewModel.deleteSavingsGoal(id)
                                Toast.makeText(context, "গোল ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditTransaction = { tx ->
                                editingTransaction = tx
                                txModalType = tx.type
                                txModalVisible = true
                            }
                        )
                        "transactions" -> TransactionsScreen(
                            transactions = transactions,
                            expenseCategories = expenseCategories,
                            incomeCategories = incomeCategories,
                            onDeleteTransaction = { id ->
                                viewModel.deleteTransaction(id)
                                Toast.makeText(context, "এন্ট্রি ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditTransaction = { tx ->
                                editingTransaction = tx
                                txModalType = tx.type
                                txModalVisible = true
                            }
                        )
                        "lending" -> LendingScreen(
                            lendBorrows = lendBorrows,
                            onAddClick = {
                                editingLendBorrow = null
                                lbModalType = "lent"
                                lbModalVisible = true
                            },
                            onSettleClick = { id ->
                                selectedSettleId = id
                                settleModalVisible = true
                            },
                            onDeleteClick = { id ->
                                viewModel.deleteLendBorrow(id)
                                Toast.makeText(context, "ধার-দেনা এন্ট্রি ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditClick = { lb ->
                                editingLendBorrow = lb
                                lbModalType = lb.type
                                lbModalVisible = true
                            }
                        )
                        "investments" -> InvestmentsScreen(
                            investments = investments,
                            onAddClick = {
                                editingInvestment = null
                                investmentModalVisible = true
                            },
                            onEditClick = { inv ->
                                editingInvestment = inv
                                investmentModalVisible = true
                            },
                            onDeleteClick = { id ->
                                viewModel.deleteInvestment(id)
                                Toast.makeText(context, "ইনভেস্টমেন্ট মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "settings" -> SettingsScreen(
                            profile = profile,
                            expenseCategories = expenseCategories,
                            incomeCategories = incomeCategories,
                            onAddCategory = { type, bn, en, icon ->
                                viewModel.addCategory(type, bn, en, icon)
                                Toast.makeText(context, "ক্যাটাগরি যোগ হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onDeleteCategory = { type, id ->
                                viewModel.deleteCategory(type, id)
                                Toast.makeText(context, "ক্যাটাগরি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditCategory = { type, id, bn, en, icon ->
                                viewModel.editCategory(type, id, bn, en, icon)
                                Toast.makeText(context, "ক্যাটাগরি এডিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditProfileName = { newName ->
                                viewModel.updateProfileName(newName)
                                Toast.makeText(context, "নাম আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onToggleStrictMode = {
                                viewModel.toggleStrictMode()
                            },
                            onSaveLimits = { daily, monthly ->
                                viewModel.saveLimits(daily, monthly)
                                Toast.makeText(context, "লিমিট সেভ হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onToggleNotif = { type ->
                                viewModel.toggleNotifSetting(type)
                            },
                            onExportBackup = {
                                coroutineScope.launch {
                                    val json = viewModel.exportBackupJson()
                                    backupJsonText = json
                                    backupDialogVisible = true
                                }
                            },
                            onImportBackup = {
                                importJsonInput = ""
                                importDialogVisible = true
                            },
                            onImportFileSelected = { json ->
                                viewModel.importBackupJson(json, onSuccess = {
                                    Toast.makeText(context, "ব্যাকআপ ইমপোর্ট সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                                }, onError = { err ->
                                    Toast.makeText(context, "ভুল ফাইল: $err", Toast.LENGTH_LONG).show()
                                })
                            },
                            onClearAll = {
                                viewModel.clearAllData()
                                Toast.makeText(context, "সব ডেটা মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "calendar" -> CalendarScreen(
                            transactions = transactions,
                            lendBorrows = lendBorrows,
                            goals = goals,
                            onDeleteTransaction = { id ->
                                viewModel.deleteTransaction(id)
                                Toast.makeText(context, "এন্ট্রি ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditTransaction = { tx ->
                                editingTransaction = tx
                                txModalType = tx.type
                                txModalVisible = true
                            },
                            onDeleteLendBorrow = { id ->
                                viewModel.deleteLendBorrow(id)
                                Toast.makeText(context, "ধার-দেনা এন্ট্রি ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditLendBorrow = { lb ->
                                editingLendBorrow = lb
                                lbModalType = lb.type
                                lbModalVisible = true
                            },
                            onDeleteGoal = { id ->
                                viewModel.deleteSavingsGoal(id)
                                Toast.makeText(context, "গোল ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onEditGoal = { goal ->
                                editingGoal = goal
                                goalModalVisible = true
                            }
                        )
                    }
                }

                // Bottom Navigation
                BottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it }
                )
            }

            // Quick Floating Action Button at bottom center
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 86.dp, end = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        txModalType = "expense"
                        txModalVisible = true
                    },
                    containerColor = TakaGold,
                    contentColor = DeepVault,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add transaction",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            /* ================= OVERLAYS (Sheets & Dialogs) ================= */

            // 1. Transaction Sheet
            DrawerLikeSheet(
                visible = txModalVisible,
                onDismiss = {
                    txModalVisible = false
                    editingTransaction = null
                },
                title = if (editingTransaction != null) {
                    "লেনদেন এডিট করো"
                } else if (txModalType == "expense") {
                    "খরচ যোগ করো"
                } else {
                    "আয় যোগ করো"
                }
            ) {
                TransactionFormSheet(
                    type = txModalType,
                    onTypeChange = { txModalType = it },
                    expenseCategories = expenseCategories,
                    incomeCategories = incomeCategories,
                    editingTransaction = editingTransaction,
                    onSave = { amount, category, paymentMethod, date, note ->
                        handleSaveTransaction(txModalType, amount, category, paymentMethod, date, note)
                    }
                )
            }

            // 1.5. Investment Modal Sheet
            DrawerLikeSheet(
                visible = investmentModalVisible,
                onDismiss = {
                    investmentModalVisible = false
                    editingInvestment = null
                },
                title = if (editingInvestment != null) "ইনভেস্টমেন্ট এডিট করুন" else "নতুন ইনভেস্টমেন্ট এন্ট্রি"
            ) {
                InvestmentFormSheet(
                    editingInvestment = editingInvestment,
                    onSave = { platformName, amount, date, returnRate, status, note ->
                        val current = editingInvestment
                        if (current != null) {
                            viewModel.editInvestment(
                                id = current.id,
                                platformName = platformName,
                                amount = amount,
                                date = date,
                                returnRate = returnRate,
                                status = status,
                                note = note
                            )
                            Toast.makeText(context, "ইনভেস্টমেন্ট আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addInvestment(
                                platformName = platformName,
                                amount = amount,
                                date = date,
                                returnRate = returnRate,
                                status = status,
                                note = note
                            )
                            Toast.makeText(context, "নতুন ইনভেস্টমেন্ট যোগ হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                        investmentModalVisible = false
                        editingInvestment = null
                    }
                )
            }

            // 2. Lending Modal Sheet
            DrawerLikeSheet(
                visible = lbModalVisible,
                onDismiss = { 
                    lbModalVisible = false
                    editingLendBorrow = null
                },
                title = if (editingLendBorrow != null) "ধার-দেনা এন্ট্রি এডিট করো" else "নতুন ধার-দেনা এন্ট্রি"
            ) {
                LendingFormSheet(
                    initialType = lbModalType,
                    editingLendBorrow = editingLendBorrow,
                    onSave = { contactName, type, amount, date, dueDate, note ->
                        if (editingLendBorrow != null) {
                            viewModel.editLendBorrow(editingLendBorrow!!, contactName, type, amount, date, dueDate, note)
                            saveConfirmationMessage = "ধার-দেনা এন্ট্রি আপডেট করা হয়েছে"
                        } else {
                            viewModel.addLendBorrow(contactName, type, amount, date, dueDate, note)
                            saveConfirmationMessage = "ধার-দেনা এন্ট্রি যোগ করা হয়েছে"
                        }
                        lbModalVisible = false
                        editingLendBorrow = null
                        showSaveConfirmation = true
                    }
                )
            }

            // 3. Savings Goal Sheet
            DrawerLikeSheet(
                visible = goalModalVisible,
                onDismiss = {
                    goalModalVisible = false
                    editingGoal = null
                },
                title = if (editingGoal != null) "সেভিংস গোল এডিট করো" else "নতুন সেভিংস গোল"
            ) {
                SavingsGoalFormSheet(
                    editingGoal = editingGoal,
                    onSave = { title, target, current, deadline ->
                        val currentEditing = editingGoal
                        if (currentEditing != null) {
                            viewModel.editSavingsGoal(currentEditing.id, title, target, current, deadline)
                            Toast.makeText(context, "সেভিংস গোল আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addSavingsGoal(title, target, current, deadline)
                            Toast.makeText(context, "সেভিংস গোল যোগ হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                        goalModalVisible = false
                        editingGoal = null
                    }
                )
            }

            // 3.5 Savings Goal Deposit Sheet
            DrawerLikeSheet(
                visible = addSavingsModalVisible,
                onDismiss = {
                    addSavingsModalVisible = false
                    selectedGoalForDeposit = null
                },
                title = "টাকা জমা করুন"
            ) {
                val goal = selectedGoalForDeposit
                if (goal != null) {
                    var depositInput by remember(goal) { mutableStateOf("") }
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "গোল: ${goal.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TakaGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "বর্তমান ব্যালেন্স: ${formatTaka(goal.currentAmount)} / টার্গেট: ${formatTaka(goal.targetAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "জমার পরিমাণ (৳)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        OutlinedTextField(
                            value = depositInput,
                            onValueChange = { depositInput = it },
                            placeholder = { Text("0", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                val amt = depositInput.toDoubleOrNull() ?: 0.0
                                if (amt <= 0.0) {
                                    Toast.makeText(context, "সঠিক পরিমাণ লেখো", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.addSavingsAmount(goal, amt)
                                    Toast.makeText(context, "৳${amt} সেভিংস গোলের জন্য জমা হয়েছে", Toast.LENGTH_SHORT).show()
                                    addSavingsModalVisible = false
                                    selectedGoalForDeposit = null
                                }
                            }),
                            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 20.sp, color = TextPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TakaGold,
                                unfocusedBorderColor = VaultLine
                            )
                        )
                        Button(
                            onClick = {
                                val amt = depositInput.toDoubleOrNull() ?: 0.0
                                if (amt <= 0.0) {
                                    Toast.makeText(context, "সঠিক পরিমাণ লেখো", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.addSavingsAmount(goal, amt)
                                Toast.makeText(context, "৳${amt} সেভিংস গোলের জন্য জমা হয়েছে", Toast.LENGTH_SHORT).show()
                                addSavingsModalVisible = false
                                selectedGoalForDeposit = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("জমা করো", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // 4. Settle / Repayment Update Sheet
            DrawerLikeSheet(
                visible = settleModalVisible,
                onDismiss = { settleModalVisible = false },
                title = "শোধ/আদায় আপডেট করো"
            ) {
                SettleFormSheet(
                    onSettlePartial = { amt ->
                        viewModel.recordRepayment(selectedSettleId, amt)
                        settleModalVisible = false
                        Toast.makeText(context, "শোধের পরিমাণ সফলভাবে আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    onSettleFull = {
                        viewModel.markFullySettled(selectedSettleId)
                        settleModalVisible = false
                        Toast.makeText(context, "পুরোপুরি শোধ হিসেবে চিহ্নিত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 5. Strict Limit Exceeded Friction Overlay
            if (warningModalVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xDD060A08))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurface),
                        border = BorderStroke(1.dp, WarmRust),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = WarmRust,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = warningTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = warningBody,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    pendingTxArgs?.let {
                                        viewModel.addTransaction(
                                            it.type,
                                            it.amount,
                                            it.category,
                                            it.paymentMethod,
                                            it.date,
                                            it.note
                                        )
                                        saveConfirmationMessage = typeToMessage(it.type)
                                        showSaveConfirmation = true
                                    }
                                    warningModalVisible = false
                                    txModalVisible = false
                                    pendingTxArgs = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, WarmRust),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "এমার্জেন্সি — তাও এগিয়ে যাও", color = WarmRust)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    warningModalVisible = false
                                    pendingTxArgs = null
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = BorderStroke(1.dp, VaultLine),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "বাতিল করো")
                            }
                        }
                    }
                }
            }

            // 6. Backup Export Dialog
            if (backupDialogVisible) {
                AlertDialog(
                    onDismissRequest = { backupDialogVisible = false },
                    title = { Text("ব্যাকআপ ডেটা এক্সপোর্ট", color = TextPrimary) },
                    text = {
                        Column {
                            Text(
                                "নিচের JSON ব্যাকআপ ডেটা কপি করে রাখো অথবা শেয়ার করো।",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(DeepVault, RoundedCornerShape(8.dp))
                                    .border(1.dp, VaultLine, RoundedCornerShape(8.dp))
                                    .verticalScroll(rememberScrollState())
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = backupJsonText,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(backupJsonText))
                                Toast.makeText(context, "ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault)
                        ) {
                            Text("কপি করো")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, backupJsonText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "ব্যাকআপ শেয়ার")
                                context.startActivity(shareIntent)
                            },
                            border = BorderStroke(1.dp, VaultLine),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Text("শেয়ার করো")
                        }
                    },
                    containerColor = VaultSurface,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // 7. Backup Import Paste Dialog
            if (importDialogVisible) {
                AlertDialog(
                    onDismissRequest = { importDialogVisible = false },
                    title = { Text("ব্যাকআপ ইমপোর্ট করুন", color = TextPrimary) },
                    text = {
                        Column {
                            Text(
                                "পাস্ট করুন ব্যাকআপ JSON টেক্সট এখানে:",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = importJsonInput,
                                onValueChange = { importJsonInput = it },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TakaGold,
                                    unfocusedBorderColor = VaultLine,
                                    focusedContainerColor = DeepVault,
                                    unfocusedContainerColor = DeepVault
                                )
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (importJsonInput.trim().isEmpty()) {
                                    Toast.makeText(context, "JSON ডেটা খালি!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.importBackupJson(importJsonInput, onSuccess = {
                                    importDialogVisible = false
                                    Toast.makeText(context, "ব্যাকআপ রিস্টোর সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                                }, onError = { err ->
                                    Toast.makeText(context, "ভুল ডেটা: $err", Toast.LENGTH_LONG).show()
                                })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault)
                        ) {
                            Text("রিস্টোর করুন")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { importDialogVisible = false },
                            border = BorderStroke(1.dp, VaultLine),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Text("বাতিল")
                        }
                    },
                    containerColor = VaultSurface,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (showSaveConfirmation) {
                LaunchedEffect(showSaveConfirmation) {
                    kotlinx.coroutines.delay(1800)
                    showSaveConfirmation = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xBB000000))
                        .clickable { showSaveConfirmation = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurface),
                        border = BorderStroke(1.5.dp, TakaGold),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(280.dp)
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(TakaGold.copy(alpha = 0.15f), CircleShape)
                                    .border(2.dp, TakaGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = TakaGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Added Done / Saved",
                                style = MaterialTheme.typography.titleMedium,
                                color = TakaGold,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = saveConfirmationMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

data class PendingTxArgs(
    val type: String,
    val amount: Double,
    val category: String,
    val paymentMethod: String,
    val date: String,
    val note: String
)

private fun typeToMessage(type: String) = if (type == "income") "আয় যোগ হয়েছে" else "খরচ যোগ হয়েছে"

/* ================= COMPOSABLE SUBCOMPONENTS ================= */

@Composable
fun Topbar(isStrictMode: Boolean, onModeBadgeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "খরচকরি",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .border(1.dp, VaultLine, RoundedCornerShape(999.dp))
                .background(VaultSurface)
                .clickable(onClick = onModeBadgeClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isStrictMode) TakaGold else CalmSage)
            )
            Text(
                text = if (isStrictMode) "Strict Mode" else "Normal Mode",
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun BottomNavBar(currentScreen: String, onNavigate: (String) -> Unit) {
    Column {
        Divider(color = VaultLine, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VaultSurface)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "হোম",
                selected = currentScreen == "home",
                onClick = { onNavigate("home") }
            )
            NavItem(
                icon = Icons.Default.ReceiptLong,
                label = "লেনদেন",
                selected = currentScreen == "transactions",
                onClick = { onNavigate("transactions") }
            )
            NavItem(
                icon = Icons.Default.Handshake,
                label = "ধার-দেনা",
                selected = currentScreen == "lending",
                onClick = { onNavigate("lending") }
            )
            NavItem(
                icon = Icons.Default.TrendingUp,
                label = "বিনিয়োগ",
                selected = currentScreen == "investments",
                onClick = { onNavigate("investments") }
            )
            NavItem(
                icon = Icons.Default.CalendarMonth,
                label = "ক্যালেন্ডার",
                selected = currentScreen == "calendar",
                onClick = { onNavigate("calendar") }
            )
            NavItem(
                icon = Icons.Default.Settings,
                label = "সেটিংস",
                selected = currentScreen == "settings",
                onClick = { onNavigate("settings") }
            )
        }
    }
}

@Composable
fun RowScope.NavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val tint = if (selected) TakaGold else TextMuted
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun DrawerLikeSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3060A08))
                .clickable(
                    onClick = onDismiss,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .clickable(enabled = false) {}
                    .background(
                        color = VaultSurface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = VaultLine,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .navigationBarsPadding() // safety fallback
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    content()
                }
            }
        }
    }
}

/* ================= FORMATTING HELPERS ================= */

fun formatTaka(amount: Double): String {
    val rounded = amount.toLong()
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "৳" + formatter.format(rounded)
}

fun formatDateBn(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = parser.parse(dateStr) ?: return dateStr
        val formatter = SimpleDateFormat("d MMM", Locale("bn", "BD"))
        formatter.format(date)
    } catch (e: Exception) {
        dateStr
    }
}
