package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.getIconByName
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/* ================= ONBOARDING SCREEN ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onStart: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVault)
            .padding(26.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "খরচকরি",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TakaGold
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "একটা শান্ত ভল্ট, তোমার টাকার হিসাবের জন্য",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "সম্পূর্ণ অফলাইন — কোনো ইন্টারনেট লাগে না। তোমার সব তথ্য শুধু এই ডিভাইসেই থাকবে, কোনো সার্ভারে পাঠানো হয় না।",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Name Field
            Text(
                text = "তোমার নাম",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("যেমন: রাহিম", color = TextMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onb_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TakaGold,
                    unfocusedBorderColor = VaultLine,
                    focusedContainerColor = VaultSurface,
                    unfocusedContainerColor = VaultSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Type Selection
            Text(
                text = "তুমি কোন ধরনের ব্যবহারকারী?",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Triple("student", "school", "স্টুডেন্ট"),
                    Triple("job", "work", "চাকরিজীবী"),
                    Triple("freelance", "laptop_mac", "ফ্রিল্যান্সার/ব্যবসায়ী"),
                    Triple("family", "family_restroom", "পরিবার পরিচালক")
                ).forEach { (typeId, iconStr, label) ->
                    val isSelected = selectedType == typeId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TakaGold.copy(alpha = 0.1f) else VaultSurface)
                            .border(
                                1.dp,
                                if (isSelected) TakaGold else VaultLine,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedType = typeId }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = getIconByName(iconStr),
                            contentDescription = label,
                            tint = if (isSelected) TakaGold else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) TakaGold else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onStart(name.trim(), selectedType) },
                enabled = name.trim().isNotEmpty() && selectedType.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TakaGold,
                    disabledContainerColor = VaultLine
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("onb_start_button")
            ) {
                Text(
                    text = "শুরু করি",
                    color = if (name.trim().isNotEmpty() && selectedType.isNotEmpty()) DeepVault else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


/* ================= HOME VIEW SCREEN ================= */

@Composable
fun HomeScreen(
    profile: UserProfile,
    transactions: List<TransactionEntity>,
    lendBorrows: List<LendBorrowEntity>,
    goals: List<SavingsGoalEntity>,
    expenseCategories: List<Category> = emptyList(),
    incomeCategories: List<Category> = emptyList(),
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onAddLendBorrow: () -> Unit,
    onAddGoal: () -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit = {},
    onEditGoal: (SavingsGoalEntity) -> Unit = {},
    onAddSavings: (SavingsGoalEntity) -> Unit = {}
) {
    var selectedTxForOptions by remember { mutableStateOf<TransactionEntity?>(null) }

    if (selectedTxForOptions != null) {
        val tx = selectedTxForOptions!!
        val category = if (tx.type == "income") {
            incomeCategories.find { it.id == tx.category } ?: INCOME_CATEGORIES.find { it.id == tx.category } ?: Category(tx.category, tx.category, tx.category, "payments")
        } else {
            expenseCategories.find { it.id == tx.category } ?: EXPENSE_CATEGORIES.find { it.id == tx.category } ?: Category(tx.category, tx.category, tx.category, "payments")
        }

        AlertDialog(
            onDismissRequest = { selectedTxForOptions = null },
            containerColor = VaultSurface,
            title = {
                Text(
                    text = "লেনদেন অপশন",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "বিবরণ: ${tx.note.ifEmpty { category.nameBn }}",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "পরিমাণ: ${formatTaka(tx.amount)}",
                        color = if (tx.type == "income") CalmSage else TakaGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "ক্যাটাগরি: ${category.nameBn}",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "মাধ্যম: ${tx.paymentMethod}",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onEditTransaction(tx)
                            selectedTxForOptions = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এডিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onDeleteTransaction(tx.id)
                            selectedTxForOptions = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmRust, contentColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ডিলিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTxForOptions = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }

    // Current month filter
    val currentMonthStr = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    var totalIncome = 0.0
    var totalExpense = 0.0

    transactions.forEach {
        if (it.date.startsWith(currentMonthStr)) {
            if (it.type == "income") totalIncome += it.amount else totalExpense += it.amount
        }
    }

    val totalBalance = totalIncome - totalExpense

    // Debt totals
    var totalToReceive = 0.0
    var totalToPay = 0.0
    lendBorrows.forEach { entry ->
        if (entry.status != "settled") {
            val remaining = entry.getRemainingAmount()
            if (entry.type == "lent") {
                totalToReceive += remaining
            } else {
                totalToPay += remaining
            }
        }
    }

    // Savings goal total approximation (sum of current goal amounts)
    val totalSavedApprox = goals.sumOf { it.currentAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Balance Vault Card
        item {
            BalanceCard(
                balance = totalBalance,
                income = totalIncome,
                expense = totalExpense,
                saved = totalSavedApprox,
                profile = profile
            )
        }

        // Debt Strip
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LendChip(
                    title = "মোট বাকি পাব",
                    amount = totalToReceive,
                    amountColor = CalmSage,
                    modifier = Modifier.weight(1f)
                )
                LendChip(
                    title = "মোট বাকি দেব",
                    amount = totalToPay,
                    amountColor = WarmRust,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillAction(
                        icon = Icons.Default.RemoveCircle,
                        label = "খরচ যোগ করো",
                        onClick = onAddExpense,
                        modifier = Modifier.weight(1f)
                    )
                    PillAction(
                        icon = Icons.Default.AddCircle,
                        label = "আয় যোগ করো",
                        onClick = onAddIncome,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillAction(
                        icon = Icons.Default.Handshake,
                        label = "ধার-দেনা এন্ট্রি",
                        onClick = onAddLendBorrow,
                        modifier = Modifier.weight(1f)
                    )
                    PillAction(
                        icon = Icons.Default.Savings,
                        label = "সেভিংস এ যোগ করো",
                        onClick = onAddGoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Transactions title
        item {
            Text(
                text = "সাম্প্রতিক লেনদেন",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        // Recent Transactions list (Up to 8)
        val recentTransactions = transactions.take(8)
        if (recentTransactions.isEmpty()) {
            item {
                EmptyState(message = "এখনো কোনো লেনদেন নেই — প্রথম এন্ট্রি যোগ করো")
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                TransactionRow(
                    tx = tx,
                    expenseCategories = expenseCategories,
                    incomeCategories = incomeCategories,
                    onClick = { selectedTxForOptions = tx }
                )
            }
        }

        // Savings Goals title
        item {
            Text(
                text = "সেভিংস গোল",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        // Savings Goals list
        if (goals.isEmpty()) {
            item {
                EmptyState(message = "এখনো কোনো সেভিংস গোল নেই — একটা যোগ করো")
            }
        } else {
            items(goals.take(4), key = { it.id }) { goal ->
                GoalCard(
                    goal = goal,
                    onDelete = { onDeleteGoal(goal.id) },
                    onEdit = { onEditGoal(goal) },
                    onAddMoney = { onAddSavings(goal) }
                )
            }
        }
    }
}

@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    saved: Double,
    profile: UserProfile
) {
    // Breathing Ring glow implementation
    val transition = rememberInfiniteTransition(label = "breathing")
    val breathingValue by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val dailyProgress = if (profile.dailyLimit > 0) profile.dailySpentToday / profile.dailyLimit else 0.0
    val monthlyProgress = if (profile.monthlyLimit > 0) profile.monthlySpentThisMonth / profile.monthlyLimit else 0.0

    val glowColor = if (profile.isStrictMode) {
        if (dailyProgress >= 1.0 || monthlyProgress >= 1.0) WarmRust else CalmSage
    } else {
        TakaGold
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VaultSurface)
            .border(1.dp, VaultLine, RoundedCornerShape(12.dp))
            .drawBehind {
                // Subtle radial glow behind the balance number
                val brush = Brush.radialGradient(
                    colors = listOf(glowColor.copy(alpha = 0.12f * breathingValue), Color.Transparent),
                    radius = size.minDimension * 0.8f
                )
                drawRect(brush = brush)
            }
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "বর্তমান ব্যালেন্স",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTaka(balance),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = MonospaceFontFamily,
                    color = TakaGold,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = VaultLine)
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "এই মাসের আয়", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = formatTaka(income), style = MaterialTheme.typography.bodyLarge, color = CalmSage, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "এই মাসের খরচ", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = formatTaka(expense), style = MaterialTheme.typography.bodyLarge, color = WarmRust, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "এই মাসের জমা", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = formatTaka(saved), style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            // Progress rows for limits (Only visible in strict mode)
            if (profile.isStrictMode) {
                Spacer(modifier = Modifier.height(18.dp))
                Divider(color = VaultLine)
                Spacer(modifier = Modifier.height(14.dp))

                LimitProgressRow(
                    label = "আজকের খরচ",
                    spent = profile.dailySpentToday,
                    limit = profile.dailyLimit,
                    progress = dailyProgress
                )
                Spacer(modifier = Modifier.height(12.dp))
                LimitProgressRow(
                    label = "এই মাসের খরচ",
                    spent = profile.monthlySpentThisMonth,
                    limit = profile.monthlyLimit,
                    progress = monthlyProgress
                )
            }
        }
    }
}

@Composable
fun LimitProgressRow(label: String, spent: Double, limit: Double, progress: Double) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(
                text = "${formatTaka(spent)} / ${if (limit > 0) formatTaka(limit) else "—"}",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFontFamily),
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(VaultLine)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0.0, 1.0).toFloat())
                    .clip(CircleShape)
                    .background(if (progress >= 1.0) WarmRust else CalmSage)
            )
        }
    }
}

@Composable
fun LendChip(title: String, amount: Double, amountColor: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurface),
        border = BorderStroke(1.dp, VaultLine),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTaka(amount),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = MonospaceFontFamily),
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PillAction(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, VaultLine),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        modifier = modifier.height(48.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = TakaGold, modifier = Modifier.size(17.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 13.sp)
    }
}

@Composable
fun TransactionRow(
    tx: TransactionEntity,
    expenseCategories: List<Category> = emptyList(),
    incomeCategories: List<Category> = emptyList(),
    onClick: () -> Unit
) {
    val category = if (tx.type == "income") {
        incomeCategories.find { it.id == tx.category } ?: INCOME_CATEGORIES.find { it.id == tx.category } ?: Category(tx.category, tx.category, tx.category, "payments")
    } else {
        expenseCategories.find { it.id == tx.category } ?: EXPENSE_CATEGORIES.find { it.id == tx.category } ?: Category(tx.category, tx.category, tx.category, "payments")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(VaultSurface2)
                .border(1.dp, VaultLine, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconByName(category.iconName),
                contentDescription = category.nameBn,
                tint = TextSecondary,
                modifier = Modifier.size(17.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.note.ifEmpty { category.nameBn },
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${category.nameBn} · ${tx.paymentMethod} · ${formatDateBn(tx.date)}",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Text(
            text = "${if (tx.type == "income") "+" else ""}${formatTaka(tx.amount)}",
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = MonospaceFontFamily),
            color = if (tx.type == "income") CalmSage else TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun GoalCard(
    goal: SavingsGoalEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onAddMoney: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) goal.currentAmount / goal.targetAmount else 0.0

    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurface),
        border = BorderStroke(1.dp, VaultLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Goal",
                        tint = TakaGold,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = goal.title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(VaultLine)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0.0, 1.0).toFloat())
                        .clip(CircleShape)
                        .background(TakaGold)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${formatTaka(goal.currentAmount)} / ${formatTaka(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFontFamily),
                    color = TextSecondary
                )
                if (goal.deadline.isNotEmpty()) {
                    Text(
                        text = "ডেডলাইন: ${formatDateBn(goal.deadline)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddMoney,
                    colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("টাকা জমান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TakaGold),
                    border = BorderStroke(1.dp, TakaGold),
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("এডিট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = TextMuted,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center
        )
    }
}


/* ================= TRANSACTIONS VIEW SCREEN ================= */

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    expenseCategories: List<Category> = emptyList(),
    incomeCategories: List<Category> = emptyList(),
    onDeleteTransaction: (String) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("all") } // "all", "income", "expense"
    var selectedTxForOptions by remember { mutableStateOf<TransactionEntity?>(null) }

    val filteredTransactions = when (selectedFilter) {
        "income" -> transactions.filter { it.type == "income" }
        "expense" -> transactions.filter { it.type == "expense" }
        else -> transactions
    }

    if (selectedTxForOptions != null) {
        val tx = selectedTxForOptions!!
        val category = if (tx.type == "income") {
            incomeCategories.find { it.id == tx.category } ?: INCOME_CATEGORIES.find { it.id == tx.category } ?: Category(tx.category, tx.category, tx.category, "payments")
        } else {
            expenseCategories.find { it.id == tx.category } ?: EXPENSE_CATEGORIES.find { it.id == tx.category } ?: Category(tx.category, tx.category, tx.category, "payments")
        }

        AlertDialog(
            onDismissRequest = { selectedTxForOptions = null },
            containerColor = VaultSurface,
            title = {
                Text(
                    text = "লেনদেন অপশন",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "বিবরণ: ${tx.note.ifEmpty { category.nameBn }}",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "পরিমাণ: ${formatTaka(tx.amount)}",
                        color = if (tx.type == "income") CalmSage else TakaGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonospaceFontFamily,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "ক্যাটাগরি: ${category.nameBn}",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "মাধ্যম: ${tx.paymentMethod}",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onEditTransaction(tx)
                            selectedTxForOptions = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এডিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onDeleteTransaction(tx.id)
                            selectedTxForOptions = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmRust, contentColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ডিলিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTxForOptions = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "সব লেনদেন",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(top = 14.dp, bottom = 12.dp)
        )

        // Filter chips row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            listOf(
                "all" to "সব",
                "income" to "আয়",
                "expense" to "খরচ"
            ).forEach { (filterId, label) ->
                val isSelected = selectedFilter == filterId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, if (isSelected) TakaGold else VaultLine, RoundedCornerShape(999.dp))
                        .background(if (isSelected) TakaGold.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { selectedFilter = filterId }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TakaGold else TextSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }

        Divider(color = VaultLine, modifier = Modifier.padding(bottom = 6.dp))

        // Transaction list
        if (filteredTransactions.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("কোনো লেনদেন পাওয়া যায়নি", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    TransactionRow(
                        tx = tx,
                        expenseCategories = expenseCategories,
                        incomeCategories = incomeCategories,
                        onClick = { selectedTxForOptions = tx }
                    )
                }
            }
        }
    }
}


/* ================= LENDING VIEW SCREEN ================= */

@Composable
fun LendingScreen(
    lendBorrows: List<LendBorrowEntity>,
    onAddClick: () -> Unit,
    onSettleClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (LendBorrowEntity) -> Unit
) {
    var totalToReceive = 0.0
    var totalToPay = 0.0

    lendBorrows.forEach { entry ->
        if (entry.status != "settled") {
            val remaining = entry.getRemainingAmount()
            if (entry.type == "lent") {
                totalToReceive += remaining
            } else {
                totalToPay += remaining
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "ধার-দেনা খাতা",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(top = 14.dp)
        )

        // Summary columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurface),
                border = BorderStroke(1.dp, VaultLine),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "বাকি পাব", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = formatTaka(totalToReceive),
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = MonospaceFontFamily),
                        color = CalmSage,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurface),
                border = BorderStroke(1.dp, VaultLine),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "বাকি দেব", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = formatTaka(totalToPay),
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = MonospaceFontFamily),
                        color = WarmRust,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Add action button
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextPrimary),
            border = BorderStroke(1.dp, TakaGold),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Handshake, contentDescription = null, tint = TakaGold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "নতুন এন্ট্রি যোগ করো", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }

        Text(
            text = "সব এন্ট্রি",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)
        )

        Divider(color = VaultLine, modifier = Modifier.padding(bottom = 6.dp))

        // Debt entries list
        if (lendBorrows.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("কোনো ধার-দেনা এন্ট্রি নেই", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lendBorrows, key = { it.id }) { item ->
                    LendBorrowItemCard(
                        item = item,
                        onSettle = { onSettleClick(item.id) },
                        onDelete = { onDeleteClick(item.id) },
                        onEdit = { onEditClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun LendBorrowItemCard(
    item: LendBorrowEntity,
    onSettle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val remaining = item.getRemainingAmount()
    val statusLabel = when (item.status) {
        "settled" -> "পুরোপুরি শোধ"
        "partial" -> "আংশিক শোধ"
        else -> "বকেয়া"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurface),
        border = BorderStroke(1.dp, VaultLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = item.contactName, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${if (item.type == "lent") "দিয়েছি" else "নিয়েছি"} · ${formatDateBn(item.date)}" +
                                if (item.dueDate.isNotEmpty()) " · ফেরত: ${formatDateBn(item.dueDate)}" else "",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .border(
                                1.dp,
                                when (item.status) {
                                    "settled" -> CalmSage
                                    "partial" -> TakaGold
                                    else -> VaultLine
                                },
                                RoundedCornerShape(999.dp)
                              )
                            .padding(horizontal = 9.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 10.5.sp,
                            color = when (item.status) {
                                "settled" -> CalmSage
                                "partial" -> TakaGold
                                else -> TextSecondary
                            }
                        )
                    }
                }

                Text(
                    text = formatTaka(item.amount),
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = MonospaceFontFamily),
                    color = if (item.type == "lent") CalmSage else WarmRust,
                    fontWeight = FontWeight.Bold
                )
            }

            if (item.status != "settled") {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "বাকি আছে: ${formatTaka(remaining)}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.status != "settled") {
                    OutlinedButton(
                        onClick = onSettle,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, VaultLine),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("শোধ আপডেট", fontSize = 12.sp)
                    }
                }
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TakaGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TakaGold),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("এডিট", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, WarmRust),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmRust),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("ডিলিট", fontSize = 12.sp)
                }
            }
        }
    }
}


/* ================= SETTINGS VIEW SCREEN ================= */

@Composable
fun SettingsScreen(
    profile: UserProfile,
    expenseCategories: List<Category> = emptyList(),
    incomeCategories: List<Category> = emptyList(),
    onAddCategory: (String, String, String, String) -> Unit,
    onDeleteCategory: (String, String) -> Unit,
    onEditCategory: (String, String, String, String, String) -> Unit,
    onEditProfileName: (String) -> Unit,
    onToggleStrictMode: () -> Unit,
    onSaveLimits: (Double, Double) -> Unit,
    onToggleNotif: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onImportFileSelected: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var dailyInput by remember { mutableStateOf(if (profile.dailyLimit > 0) profile.dailyLimit.toLong().toString() else "") }
    var monthlyInput by remember { mutableStateOf(if (profile.monthlyLimit > 0) profile.monthlyLimit.toLong().toString() else "") }

    // Request notification permissions dynamically on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "নোটিফিকেশন পারমিশন না দিলে নোটিফিকেশন এলার্ট পাওয়া যাবে না!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val jsonStr = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { r -> r.readText() }
                if (jsonStr != null) {
                    onImportFileSelected(jsonStr)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "ফাইল পড়তে ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "প্রোফাইল",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(top = 14.dp)
        )

        // Profile details
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = profile.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Text(
                        text = USER_TYPE_LABELS[profile.userType] ?: profile.userType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                TextButton(onClick = {
                    // Quick simple prompt rename
                    onEditProfileName("") // Screen logic handles re-prompting or custom rename inside MainActivity later
                }) {
                    Text("এডিট", color = TakaGold)
                }
            }
        }

        // Strict Mode limits card
        Text(
            text = "স্ট্রিক্ট/সেভিংস মোড",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "স্ট্রিক্ট মোড चालू করো", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        Text(text = "দৈনিক/মাসিক খরচ লিমিট সেট করে সতর্ক থাকো", fontSize = 12.sp, color = TextMuted)
                    }
                    Switch(
                        checked = profile.isStrictMode,
                        onCheckedChange = { onToggleStrictMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TakaGold,
                            checkedTrackColor = TakaGold.copy(alpha = 0.2f),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = VaultLine
                        )
                    )
                }

                if (profile.isStrictMode) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = VaultLine)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = dailyInput,
                            onValueChange = { dailyInput = it },
                            label = { Text("দৈনিক লিমিট (৳)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TakaGold,
                                unfocusedBorderColor = VaultLine,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = monthlyInput,
                            onValueChange = { monthlyInput = it },
                            label = { Text("মাসিক লিমিট (৳)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TakaGold,
                                unfocusedBorderColor = VaultLine,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val d = dailyInput.toDoubleOrNull() ?: 0.0
                            val m = monthlyInput.toDoubleOrNull() ?: 0.0
                            onSaveLimits(d, m)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("লিমিট সেভ করো", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Category Customization section
        var showCategoryDialog by remember { mutableStateOf(false) }
        var editingCat by remember { mutableStateOf<Category?>(null) }
        var activeCategoryType by remember { mutableStateOf("expense") } // "expense" or "income"

        var catNameEn by remember { mutableStateOf("") }
        var catNameBn by remember { mutableStateOf("") }
        var catIcon by remember { mutableStateOf("category") }

        if (showCategoryDialog) {
            AlertDialog(
                onDismissRequest = { showCategoryDialog = false },
                containerColor = VaultSurface,
                title = {
                    Text(
                        text = if (editingCat == null) "নতুন ক্যাটাগরি যোগ করুন" else "ক্যাটাগরি এডিট করুন",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "বাংলা নাম", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        OutlinedTextField(
                            value = catNameBn,
                            onValueChange = { catNameBn = it },
                            placeholder = { Text("যেমন: খাবার, যাতায়াত", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TakaGold, unfocusedBorderColor = VaultLine, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        Text(text = "ইংরেজি নাম (ID হিসেবে)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        OutlinedTextField(
                            value = catNameEn,
                            onValueChange = { catNameEn = it },
                            placeholder = { Text("যেমন: food, transport", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TakaGold, unfocusedBorderColor = VaultLine, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        Text(text = "আইকন সিলেক্ট করুন", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        
                        // Icon Picker Grid
                        val AVAILABLE_ICONS = listOf(
                            "restaurant", "home", "directions_bus", "receipt_long", "school", 
                            "health_and_safety", "family_restroom", "shopping_bag", "celebration", 
                            "credit_card", "mosque", "business_center", "category", "work", 
                            "account_balance_wallet", "menu_book", "laptop_mac", "storefront", 
                            "redeem", "schedule", "card_giftcard", "flight", "trending_up", 
                            "replay", "apartment", "add_circle"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(DeepVault, RoundedCornerShape(8.dp))
                                .border(1.dp, VaultLine, RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(AVAILABLE_ICONS) { iconName ->
                                    val isSelected = catIcon == iconName
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) TakaGold.copy(alpha = 0.2f) else Color.Transparent)
                                            .border(1.dp, if (isSelected) TakaGold else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { catIcon = iconName }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = getIconByName(iconName),
                                            contentDescription = null,
                                            tint = if (isSelected) TakaGold else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (catNameBn.trim().isEmpty() || catNameEn.trim().isEmpty()) {
                                Toast.makeText(context, "সব তথ্য পূরণ করুন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val currentEditing = editingCat
                            if (currentEditing == null) {
                                onAddCategory(activeCategoryType, catNameBn.trim(), catNameEn.trim().lowercase(), catIcon)
                            } else {
                                onEditCategory(activeCategoryType, currentEditing.id, catNameBn.trim(), catNameEn.trim().lowercase(), catIcon)
                            }
                            showCategoryDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault)
                    ) {
                        Text("সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCategoryDialog = false }) {
                        Text("বাতিল", color = TextSecondary)
                    }
                }
            )
        }

        Text(
            text = "ক্যাটাগরি কাস্টমাইজ",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Category Type Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { activeCategoryType = "expense" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeCategoryType == "expense") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (activeCategoryType == "expense") TakaGold else VaultLine),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("খরচ ক্যাটাগরি", fontSize = 12.sp, color = if (activeCategoryType == "expense") TakaGold else TextSecondary)
                    }
                    Button(
                        onClick = { activeCategoryType = "income" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeCategoryType == "income") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (activeCategoryType == "income") TakaGold else VaultLine),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("আয় ক্যাটাগরি", fontSize = 12.sp, color = if (activeCategoryType == "income") TakaGold else TextSecondary)
                    }
                }

                val currentCats = if (activeCategoryType == "expense") {
                    expenseCategories.ifEmpty { EXPENSE_CATEGORIES }
                } else {
                    incomeCategories.ifEmpty { INCOME_CATEGORIES }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentCats.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DeepVault, RoundedCornerShape(8.dp))
                                .border(1.dp, VaultLine, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(VaultSurface2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getIconByName(cat.iconName),
                                        contentDescription = null,
                                        tint = TakaGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Column {
                                    Text(text = cat.nameBn, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = cat.nameEn, color = TextMuted, fontSize = 10.sp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        editingCat = cat
                                        catNameBn = cat.nameBn
                                        catNameEn = cat.nameEn
                                        catIcon = cat.iconName
                                        showCategoryDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TakaGold, modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = {
                                        onDeleteCategory(activeCategoryType, cat.id)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = WarmRust, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        editingCat = null
                        catNameBn = ""
                        catNameEn = ""
                        catIcon = "category"
                        showCategoryDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন ক্যাটাগরি যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Notifications card
        Text(
            text = "নোটিফিকেশন",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NotificationSettingRow(
                    label = "লিমিট এলার্ট",
                    checked = profile.isLimitAlertEnabled,
                    onCheckedChange = { onToggleNotif("limit_alerts") }
                )
                NotificationSettingRow(
                    label = "ধার-দেনা ডিউ ডেট",
                    checked = profile.isDueDateAlertEnabled,
                    onCheckedChange = { onToggleNotif("due_date_alerts") }
                )
                NotificationSettingRow(
                    label = "মাসিক সামারি",
                    checked = profile.isMonthlySummaryEnabled,
                    onCheckedChange = { onToggleNotif("monthly_summary") }
                )
            }
        }

        // Data backup card
        Text(
            text = "ডেটা ব্যাকআপ ও প্রাইভেসি",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.FileDownload,
                    label = "JSON ব্যাকআপ এক্সপোর্ট করো",
                    onClick = onExportBackup
                )
                SettingsActionRow(
                    icon = Icons.Default.FileUpload,
                    label = "JSON ব্যাকআপ ইমপোর্ট করো",
                    onClick = onImportBackup
                )
                SettingsActionRow(
                    icon = Icons.Default.Folder,
                    label = "ব্যাকআপ ফাইল বেছে নিন (.json)",
                    onClick = { filePickerLauncher.launch("application/json") }
                )
                SettingsActionRow(
                    icon = Icons.Default.DeleteForever,
                    label = "সব ডেটা মুছে ফেলো",
                    onClick = onClearAll,
                    labelColor = WarmRust
                )
            }
        }

        Text(
            text = "এই অ্যাপের ডেটা শুধু এই ডিভাইস/ব্রাউজারেই সংরক্ষিত থাকে — অন্য ডিভাইস বা ব্রাউজারে আলাদাভাবে থাকবে। ডেটা সরাতে JSON এক্সপোর্ট/ইমপোর্ট ব্যবহার করো।",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(bottom = 120.dp)
        )
    }
}

@Composable
fun NotificationSettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TakaGold,
                checkedTrackColor = TakaGold.copy(alpha = 0.2f),
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = VaultLine
            )
        )
    }
}

@Composable
fun SettingsActionRow(icon: ImageVector, label: String, onClick: () -> Unit, labelColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = if (labelColor == WarmRust) WarmRust else TakaGold, modifier = Modifier.size(20.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = labelColor)
    }
}


/* ================= FORM SHEET COMPOSABLES ================= */

@Composable
fun TransactionFormSheet(
    type: String,
    onTypeChange: (String) -> Unit,
    expenseCategories: List<Category> = emptyList(),
    incomeCategories: List<Category> = emptyList(),
    editingTransaction: TransactionEntity? = null,
    onSave: (Double, String, String, String, String) -> Unit
) {
    var amountInput by remember(editingTransaction) { mutableStateOf(editingTransaction?.amount?.toLong()?.toString() ?: "") }
    var selectedCat by remember(editingTransaction) { mutableStateOf(editingTransaction?.category ?: "") }
    var selectedPay by remember(editingTransaction) { mutableStateOf(editingTransaction?.paymentMethod ?: "") }
    var note by remember(editingTransaction) { mutableStateOf(editingTransaction?.note ?: "") }
    var date by remember(editingTransaction) { mutableStateOf(editingTransaction?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }

    val context = LocalContext.current

    val performSave = {
        val amt = amountInput.toDoubleOrNull() ?: 0.0
        if (amt <= 0.0) {
            Toast.makeText(context, "সঠিক পরিমাণ লেখো", Toast.LENGTH_SHORT).show()
        } else if (selectedCat.isEmpty()) {
            Toast.makeText(context, "একটা ক্যাটাগরি বেছে নাও", Toast.LENGTH_SHORT).show()
        } else {
            onSave(amt, selectedCat, selectedPay, date, note)
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onTypeChange("expense") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "expense") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = BorderStroke(1.dp, if (type == "expense") TakaGold else VaultLine),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "খরচ", color = if (type == "expense") TakaGold else TextSecondary)
            }
            Button(
                onClick = { onTypeChange("income") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "income") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = BorderStroke(1.dp, if (type == "income") TakaGold else VaultLine),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "আয়", color = if (type == "income") TakaGold else TextSecondary)
            }
        }

        // Amount Input
        Text(text = "পরিমাণ (৳)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            placeholder = { Text("0", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performSave() }),
            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 20.sp, color = TextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Category Selection
        Text(text = "ক্যাটাগরি", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        val categories = if (type == "expense") {
            expenseCategories.ifEmpty { EXPENSE_CATEGORIES }
        } else {
            incomeCategories.ifEmpty { INCOME_CATEGORIES }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(top = 4.dp, bottom = 14.dp)
                .background(DeepVault, RoundedCornerShape(8.dp))
                .border(1.dp, VaultLine, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCat == cat.id
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TakaGold.copy(alpha = 0.1f) else Color.Transparent)
                            .border(1.dp, if (isSelected) TakaGold else VaultLine, RoundedCornerShape(8.dp))
                            .clickable { selectedCat = cat.id }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = getIconByName(cat.iconName),
                            contentDescription = cat.nameBn,
                            tint = if (isSelected) TakaGold else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.nameBn,
                            fontSize = 11.sp,
                            color = if (isSelected) TakaGold else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Payment Method Select
        Text(text = "পেমেন্ট মেথড", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
        ) {
            items(PAYMENT_METHODS) { method ->
                val isSelected = selectedPay == method
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, if (isSelected) TakaGold else VaultLine, RoundedCornerShape(999.dp))
                        .clickable { selectedPay = method }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = method, fontSize = 12.sp, color = if (isSelected) TakaGold else TextSecondary)
                }
            }
        }

        // Date Picker Field
        Text(text = "তারিখ", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val c = Calendar.getInstance().apply { set(year, month, day) }
                            date = fmt.format(c.time)
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = TakaGold)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Note Input
        Text(text = "নোট (ঐচ্ছিক)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text("যেমন: দুপুরের খাবার, বন্ধুর সাথে", color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performSave() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        Button(
            onClick = { performSave() },
            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("সেভ করো", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun LendingFormSheet(
    initialType: String,
    editingLendBorrow: LendBorrowEntity? = null,
    onSave: (String, String, Double, String, String, String) -> Unit
) {
    var type by remember(editingLendBorrow, initialType) { mutableStateOf(editingLendBorrow?.type ?: initialType) } // "lent" or "borrowed"
    var name by remember(editingLendBorrow) { mutableStateOf(editingLendBorrow?.contactName ?: "") }
    var amountInput by remember(editingLendBorrow) { mutableStateOf(editingLendBorrow?.amount?.toLong()?.toString() ?: "") }
    var note by remember(editingLendBorrow) { mutableStateOf(editingLendBorrow?.note ?: "") }
    var date by remember(editingLendBorrow) { mutableStateOf(editingLendBorrow?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var dueDate by remember(editingLendBorrow) { mutableStateOf(editingLendBorrow?.dueDate ?: "") }

    val context = LocalContext.current

    val performSave = {
        val amt = amountInput.toDoubleOrNull() ?: 0.0
        if (name.trim().isEmpty()) {
            Toast.makeText(context, "নাম লেখো", Toast.LENGTH_SHORT).show()
        } else if (amt <= 0.0) {
            Toast.makeText(context, "সঠিক পরিমাণ লেখো", Toast.LENGTH_SHORT).show()
        } else {
            onSave(name.trim(), type, amt, date, dueDate, note)
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { type = "lent" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "lent") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = BorderStroke(1.dp, if (type == "lent") TakaGold else VaultLine),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "কাকে দিলাম", color = if (type == "lent") TakaGold else TextSecondary)
            }
            Button(
                onClick = { type = "borrowed" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "borrowed") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = BorderStroke(1.dp, if (type == "borrowed") TakaGold else VaultLine),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "কার কাছে ধার নিলাম", color = if (type == "borrowed") TakaGold else TextSecondary)
            }
        }

        // Contact Name
        Text(text = "নাম", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("যেমন: করিম ভাই", color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Amount Input
        Text(text = "পরিমাণ (৳)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            placeholder = { Text("0", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performSave() }),
            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 20.sp, color = TextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Date Picker
        Text(text = "তারিখ", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val c = Calendar.getInstance().apply { set(year, month, day) }
                            date = fmt.format(c.time)
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = TakaGold)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Due Date Picker
        Text(text = "ফেরত দেওয়ার তারিখ (ঐচ্ছিক)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            readOnly = true,
            placeholder = { Text("তারিখ বেছে নাও", color = TextMuted) },
            trailingIcon = {
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val c = Calendar.getInstance().apply { set(year, month, day) }
                            dueDate = fmt.format(c.time)
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = TakaGold)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Note Input
        Text(text = "নোট (ঐচ্ছিক)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text("যেমন: বই কেনার জন্য", color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performSave() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        Button(
            onClick = { performSave() },
            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("সেভ করো", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun SavingsGoalFormSheet(
    editingGoal: SavingsGoalEntity? = null,
    onSave: (String, Double, Double, String) -> Unit
) {
    var title by remember(editingGoal) { mutableStateOf(editingGoal?.title ?: "") }
    var targetInput by remember(editingGoal) { mutableStateOf(editingGoal?.targetAmount?.toLong()?.toString() ?: "") }
    var currentInput by remember(editingGoal) { mutableStateOf(editingGoal?.currentAmount?.toLong()?.toString() ?: "") }
    var deadline by remember(editingGoal) { mutableStateOf(editingGoal?.deadline ?: "") }

    val context = LocalContext.current

    val performSave = {
        val target = targetInput.toDoubleOrNull() ?: 0.0
        val current = currentInput.toDoubleOrNull() ?: 0.0
        if (title.trim().isEmpty()) {
            Toast.makeText(context, "গোলের নাম লেখো", Toast.LENGTH_SHORT).show()
        } else if (target <= 0.0) {
            Toast.makeText(context, "সঠিক টার্গেট লেখো", Toast.LENGTH_SHORT).show()
        } else {
            onSave(title.trim(), target, current, deadline)
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Goal Title
        Text(text = "গোলের নাম", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("যেমন: নতুন ফোন", color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Target amount
        Text(text = "টার্গেট পরিমাণ (৳)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = targetInput,
            onValueChange = { targetInput = it },
            placeholder = { Text("0", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 18.sp, color = TextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Current amount
        Text(text = "এখন পর্যন্ত জমানো (৳)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = currentInput,
            onValueChange = { currentInput = it },
            placeholder = { Text("0", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performSave() }),
            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 18.sp, color = TextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Deadline Date Picker
        Text(text = "ডেডলাইন (ঐচ্ছিক)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = deadline,
            onValueChange = { deadline = it },
            readOnly = true,
            placeholder = { Text("তারিখ বেছে নাও", color = TextMuted) },
            trailingIcon = {
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val c = Calendar.getInstance().apply { set(year, month, day) }
                            deadline = fmt.format(c.time)
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = TakaGold)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        Button(
            onClick = { performSave() },
            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("সেভ করো", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun SettleFormSheet(
    onSettlePartial: (Double) -> Unit,
    onSettleFull: () -> Unit
) {
    var settleInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column {
        Text(text = "কত পরিমাণ শোধ হলো (৳)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = settleInput,
            onValueChange = { settleInput = it },
            placeholder = { Text("0", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 20.sp, color = TextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        Button(
            onClick = {
                val amt = settleInput.toDoubleOrNull() ?: 0.0
                if (amt <= 0.0) {
                    Toast.makeText(context, "সঠিক পরিমাণ লেখো", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onSettlePartial(amt)
            },
            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("আপডেট করো", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onSettleFull,
            border = BorderStroke(1.dp, VaultLine),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TakaGold),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TakaGold)
            Spacer(modifier = Modifier.width(6.dp))
            Text("পুরোপুরি শোধ হয়ে গেছে", fontWeight = FontWeight.SemiBold)
        }
    }
}

/* ================= INVESTMENTS VIEW SCREEN ================= */

@Composable
fun InvestmentsScreen(
    investments: List<InvestmentEntity>,
    onAddClick: () -> Unit,
    onEditClick: (InvestmentEntity) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val totalInvested = investments.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "বিনিয়োগ খাতা",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "মোট বিনিয়োগ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = formatTaka(totalInvested),
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = MonospaceFontFamily),
                        color = TakaGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = TakaGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Divider(color = VaultLine, modifier = Modifier.padding(bottom = 12.dp))

        if (investments.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "এখনো কোনো বিনিয়োগ যুক্ত করা হয়নি",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(investments, key = { it.id }) { investment ->
                    InvestmentCard(
                        investment = investment,
                        onEdit = { onEditClick(investment) },
                        onDelete = { onDeleteClick(investment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun InvestmentCard(
    investment: InvestmentEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurface),
        border = BorderStroke(1.dp, VaultLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = investment.platformName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Status badge
                val isActive = investment.status == "active"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isActive) CalmSage.copy(alpha = 0.15f) else TextMuted.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isActive) "চলতি" else "উত্তোলনকৃত",
                        color = if (isActive) CalmSage else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTaka(investment.amount),
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = MonospaceFontFamily),
                    color = TakaGold,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = formatDateBn(investment.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            if (investment.returnRate != null && investment.returnRate > 0.0) {
                Text(
                    text = "লভ্যাংশ হার: ${investment.returnRate}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = CalmSage,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (investment.note.isNotEmpty()) {
                Text(
                    text = "নোট: ${investment.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = VaultLine)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TakaGold, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = WarmRust, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun InvestmentFormSheet(
    editingInvestment: InvestmentEntity? = null,
    onSave: (String, Double, String, Double?, String, String) -> Unit
) {
    var platformInput by remember(editingInvestment) { mutableStateOf(editingInvestment?.platformName ?: "") }
    var amountInput by remember(editingInvestment) { mutableStateOf(editingInvestment?.amount?.toLong()?.toString() ?: "") }
    var returnRateInput by remember(editingInvestment) { mutableStateOf(editingInvestment?.returnRate?.toString() ?: "") }
    var noteInput by remember(editingInvestment) { mutableStateOf(editingInvestment?.note ?: "") }
    var statusInput by remember(editingInvestment) { mutableStateOf(editingInvestment?.status ?: "active") }
    var dateInput by remember(editingInvestment) { mutableStateOf(editingInvestment?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }

    val context = LocalContext.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // Platform Name
        Text(text = "বিনিয়োগের মাধ্যম/জায়গা", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = platformInput,
            onValueChange = { platformInput = it },
            placeholder = { Text("যেমন: সঞ্চয়পত্র, স্বর্ণ, মিউচুয়াল ফান্ড", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Amount Input
        Text(text = "বিনিয়োগের পরিমাণ (৳)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            placeholder = { Text("0", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(fontFamily = MonospaceFontFamily, fontSize = 20.sp, color = TextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Return Rate Input
        Text(text = "লভ্যাংশ/মুনাফার হার (%) - ঐচ্ছিক", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = returnRateInput,
            onValueChange = { returnRateInput = it },
            placeholder = { Text("যেমন: ৮.৫", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Date Picker
        Text(text = "তারিখ", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = dateInput,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp)
                .clickable {
                    val calendar = Calendar.getInstance()
                    val dpd = DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val cal = Calendar.getInstance().apply { set(y, m, d) }
                            dateInput = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.show()
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Status Selection
        Text(text = "অবস্থা (Status)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { statusInput = "active" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (statusInput == "active") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = BorderStroke(1.dp, if (statusInput == "active") TakaGold else VaultLine),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "চলতি (Active)", color = if (statusInput == "active") TakaGold else TextSecondary)
            }
            Button(
                onClick = { statusInput = "withdrawn" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (statusInput == "withdrawn") TakaGold.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = BorderStroke(1.dp, if (statusInput == "withdrawn") TakaGold else VaultLine),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "উত্তোলনকৃত (Withdrawn)", color = if (statusInput == "withdrawn") TakaGold else TextSecondary)
            }
        }

        // Note Input
        Text(text = "নোট", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            placeholder = { Text("যেকোনো মন্তব্য...", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TakaGold,
                unfocusedBorderColor = VaultLine
            )
        )

        // Save Button
        Button(
            onClick = {
                val amt = amountInput.toDoubleOrNull() ?: 0.0
                if (platformInput.trim().isEmpty() || amt <= 0.0) {
                    Toast.makeText(context, "অনুগ্রহ করে সব তথ্য সঠিক দিন", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onSave(platformInput.trim(), amt, dateInput, returnRateInput.toDoubleOrNull(), statusInput, noteInput.trim())
            },
            colors = ButtonDefaults.buttonColors(containerColor = TakaGold, contentColor = DeepVault),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(if (editingInvestment != null) "বিনিয়োগ এডিট করো" else "বিনিয়োগ সেভ করো", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CalendarScreen(
    transactions: List<TransactionEntity>,
    lendBorrows: List<LendBorrowEntity>,
    goals: List<SavingsGoalEntity> = emptyList(),
    onDeleteTransaction: (String) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteLendBorrow: (String) -> Unit,
    onEditLendBorrow: (LendBorrowEntity) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onEditGoal: (SavingsGoalEntity) -> Unit
) {
    var calendarState by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }

    val currentYear = calendarState.get(Calendar.YEAR)
    val currentMonth = calendarState.get(Calendar.MONTH) // 0-indexed

    val monthsBn = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    // Calculate days of the current month
    val daysList = remember(currentYear, currentMonth) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday ... 7 = Saturday
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val list = mutableListOf<Int?>()
        // Standard Calendar Grid: standard 7 column calendar.
        val emptySlots = firstDayOfWeek - 1
        for (i in 0 until emptySlots) {
            list.add(null)
        }
        for (day in 1..maxDays) {
            list.add(day)
        }
        list
    }

    // Map of dates to show indicators
    val eventDates = remember(transactions, lendBorrows, goals) {
        val dates = mutableSetOf<String>()
        transactions.forEach { dates.add(it.date) }
        lendBorrows.forEach { dates.add(it.date) }
        goals.forEach { dates.add(it.deadline) }
        dates
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "ক্যালেন্ডার খাতা",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(top = 14.dp, bottom = 12.dp)
        )

        // Month Selection Header
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurface),
            border = BorderStroke(1.dp, VaultLine),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendarState.time
                        add(Calendar.MONTH, -1)
                    }
                    calendarState = newCal
                }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TakaGold)
                }

                Text(
                    text = "${monthsBn[currentMonth]} $currentYear",
                    style = MaterialTheme.typography.titleMedium,
                    color = TakaGold,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendarState.time
                        add(Calendar.MONTH, 1)
                    }
                    calendarState = newCal
                }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month", tint = TakaGold)
                }
            }
        }

        // Calendar Weekday Headers
        val weekdays = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি")
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekdays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Days Grid
        val rowsCount = (daysList.size + 6) / 7
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VaultSurface, RoundedCornerShape(12.dp))
                .border(1.dp, VaultLine, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            for (r in 0 until rowsCount) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (c in 0..6) {
                        val index = r * 7 + c
                        val dayNum = if (index < daysList.size) daysList[index] else null

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNum != null) {
                                val dayStr = String.format(Locale.US, "%02d", dayNum)
                                val monthStr = String.format(Locale.US, "%02d", currentMonth + 1)
                                val dateKey = "$currentYear-$monthStr-$dayStr"
                                val isSelected = dateKey == selectedDate
                                val hasEvents = eventDates.contains(dateKey)

                                val dayBg = when {
                                    isSelected -> TakaGold.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                                val dayBorder = when {
                                    isSelected -> BorderStroke(1.5.dp, TakaGold)
                                    else -> null
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(0.9f)
                                        .clip(CircleShape)
                                        .background(dayBg)
                                        .then(if (dayBorder != null) Modifier.border(dayBorder, CircleShape) else Modifier)
                                        .clickable { selectedDate = dateKey },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TakaGold else TextPrimary
                                        )
                                        if (hasEvents) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(TakaGold, CircleShape)
                                                    .padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Header
        val selectedDateFormatted = remember(selectedDate) {
            try {
                val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDate)
                parsed?.let {
                    val cal = Calendar.getInstance().apply { time = it }
                    val d = cal.get(Calendar.DAY_OF_MONTH)
                    val m = cal.get(Calendar.MONTH)
                    val y = cal.get(Calendar.YEAR)
                    "$d ${monthsBn[m]} $y"
                } ?: selectedDate
            } catch (e: Exception) {
                selectedDate
            }
        }

        Text(
            text = "$selectedDateFormatted - এর লেনদেন সমূহ",
            style = MaterialTheme.typography.titleMedium,
            color = TakaGold,
            modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )

        // Selected date items list
        val selectedTx = remember(selectedDate, transactions) {
            transactions.filter { it.date == selectedDate }
        }
        val selectedLends = remember(selectedDate, lendBorrows) {
            lendBorrows.filter { it.date == selectedDate }
        }
        val selectedGoals = remember(selectedDate, goals) {
            goals.filter { it.deadline == selectedDate }
        }

        if (selectedTx.isEmpty() && selectedLends.isEmpty() && selectedGoals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "এই তারিখে কোনো লেনদেন বা রেকর্ড নেই",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Transactions
                selectedTx.forEach { tx ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurface),
                        border = BorderStroke(1.dp, if (tx.type == "income") CalmSage.copy(alpha = 0.5f) else WarmRust.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (tx.type == "income") CalmSage.copy(alpha = 0.15f) else WarmRust.copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.type == "income") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = tx.type,
                                    tint = if (tx.type == "income") CalmSage else WarmRust,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.category.ifEmpty { if (tx.type == "income") "আয়" else "খরচ" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (tx.note.isNotEmpty()) {
                                    Text(
                                        text = tx.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "পেমেন্ট: ${tx.paymentMethod}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (tx.type == "income") "+৳${tx.amount.toInt()}" else "-৳${tx.amount.toInt()}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == "income") CalmSage else WarmRust
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onEditTransaction(tx) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TakaGold, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteTransaction(tx.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = WarmRust, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Lends / Borrows
                selectedLends.forEach { lb ->
                    val isLent = lb.type == "lent"
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurface),
                        border = BorderStroke(1.dp, TakaGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(TakaGold.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = lb.type,
                                    tint = TakaGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isLent) "৳${lb.amount.toInt()} ধার দেয়া হয়েছে" else "৳${lb.amount.toInt()} ধার নেয়া হয়েছে",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "ব্যক্তি: ${lb.contactName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                if (lb.note.isNotEmpty()) {
                                    Text(
                                        text = lb.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                if (lb.dueDate.isNotEmpty()) {
                                    Text(
                                        text = "ফেরতের তারিখ: ${lb.dueDate}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TakaGold
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "৳${lb.getRemainingAmount().toInt()} বাকি",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TakaGold
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onEditLendBorrow(lb) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TakaGold, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteLendBorrow(lb.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = WarmRust, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Goals
                selectedGoals.forEach { goal ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurface),
                        border = BorderStroke(1.dp, TakaGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(TakaGold.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Goal",
                                    tint = TakaGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "সঞ্চয় লক্ষ্য: ${goal.title}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "লক্ষ্যমাত্রা: ৳${goal.targetAmount.toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "৳${goal.currentAmount.toInt()} জমানো হয়েছে",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TakaGold
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onEditGoal(goal) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TakaGold, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteGoal(goal.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = WarmRust, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
