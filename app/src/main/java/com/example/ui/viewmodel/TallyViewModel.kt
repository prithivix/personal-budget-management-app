package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CategoryBudget
import com.example.data.TallyRepository
import com.example.data.TransactionEntity
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

data class MonthItem(
    val year: Int,
    val month: Int // 1-12
) {
    val monthKey: String get() = String.format(Locale.US, "%04d-%02d", year, month)
    val displayName: String
        get() {
            val names = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            return "${names[month - 1]} $year"
        }
}

data class CategoryProgressItem(
    val category: CategoryBudget,
    val spent: Double,
    val limit: Double,
    val progress: Float, // 0.0 to 1.0 (capped for bar)
    val isOverBudget: Boolean,
    val diffAmount: Double
)

data class AlertItem(
    val id: String,
    val type: AlertType,
    val title: String,
    val body: String,
    val tag: String,
    val hasAttentionDot: Boolean = false,
    val categoryKey: String? = null
)

enum class AlertType {
    OVER_BUDGET,
    BIGGEST_SPEND,
    SAVINGS_GOAL,
    ON_TRACK
}

data class TallyUiState(
    val userProfile: UserProfile? = null,
    val selectedMonth: MonthItem = MonthItem(2026, 9),
    val isCurrentMonth: Boolean = true,
    val categories: List<CategoryBudget> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalIncome: Double = 0.0,
    val leftToSpend: Double = 0.0,
    val daysLeftInMonth: Int = 10,
    val safePerDay: Double = 0.0,
    val unassignedIncome: Double = 0.0,
    val categoryProgressList: List<CategoryProgressItem> = emptyList(),
    val overBudgetCategories: List<CategoryProgressItem> = emptyList(),
    val alerts: List<AlertItem> = emptyList(),
    val isLoading: Boolean = false
)

class TallyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TallyRepository

    private val _selectedMonth = MutableStateFlow(MonthItem(2026, 9))
    val selectedMonth: StateFlow<MonthItem> = _selectedMonth.asStateFlow()

    private val _uiState = MutableStateFlow(TallyUiState())
    val uiState: StateFlow<TallyUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TallyRepository(
            database.userProfileDao(),
            database.categoryBudgetDao(),
            database.transactionDao()
        )

        viewModelScope.launch {
            val existing = database.userProfileDao().getUserProfileOnce()
            if (existing == null) {
                repository.loadSampleData()
            }
        }

        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.userProfile,
                repository.allCategories,
                _selectedMonth
            ) { profile, categories, month ->
                Triple(profile, categories, month)
            }.collect { (profile, categories, month) ->
                if (profile == null || !profile.isOnboarded) {
                    _uiState.value = _uiState.value.copy(
                        userProfile = profile,
                        selectedMonth = month,
                        categories = categories,
                        isLoading = false
                    )
                } else {
                    // Fetch transactions for selected month
                    repository.getTransactionsByMonth(month.monthKey).collect { txList ->
                        val daysLeft = calculateDaysLeftInMonth(month)
                        val totalBudget = categories.sumOf { it.monthlyLimit }
                        val expenseTx = txList.filter { it.type == "EXPENSE" }
                        val incomeTx = txList.filter { it.type == "INCOME" }
                        val totalSpent = expenseTx.sumOf { it.amount }
                        val totalIncome = incomeTx.sumOf { it.amount }
                        val leftToSpend = totalBudget - totalSpent
                        val safePerDay = if (daysLeft > 0) Math.max(0.0, leftToSpend / daysLeft) else 0.0
                        val unassigned = Math.max(0.0, profile.monthlyIncome - totalBudget)

                        // Category progress
                        val progressList = categories.map { cat ->
                            val catSpent = expenseTx.filter { it.categoryKey == cat.categoryKey }.sumOf { it.amount }
                            val limit = cat.monthlyLimit
                            val isOver = catSpent > limit
                            val diff = if (isOver) catSpent - limit else limit - catSpent
                            val progress = if (limit > 0) (catSpent / limit).toFloat().coerceIn(0f, 1f) else 0f
                            CategoryProgressItem(
                                category = cat,
                                spent = catSpent,
                                limit = limit,
                                progress = progress,
                                isOverBudget = isOver,
                                diffAmount = diff
                            )
                        }

                        // Sort for "Closest to their limit": over budget first, then by ratio descending
                        val sortedProgress = progressList.sortedWith(
                            compareByDescending<CategoryProgressItem> { it.isOverBudget }
                                .thenByDescending { if (it.limit > 0) it.spent / it.limit else 0.0 }
                        )

                        val overBudgetItems = progressList.filter { it.isOverBudget }

                        // Generate alerts
                        val alertsList = mutableListOf<AlertItem>()
                        overBudgetItems.forEach { item ->
                            val sym = profile.currencySymbol
                            val spentFmt = formatAmount(item.spent, sym)
                            val limitFmt = formatAmount(item.limit, sym)
                            val overFmt = formatAmount(item.diffAmount, sym)
                            alertsList.add(
                                AlertItem(
                                    id = "over_${item.category.categoryKey}",
                                    type = AlertType.OVER_BUDGET,
                                    title = "Over budget: ${item.category.categoryName}",
                                    body = "You've spent $spentFmt against a $limitFmt limit, which is $overFmt over.",
                                    tag = "Needs attention",
                                    hasAttentionDot = true,
                                    categoryKey = item.category.categoryKey
                                )
                            )
                        }

                        // Biggest spend alert
                        val biggest = progressList.maxByOrNull { it.spent }
                        if (biggest != null && biggest.spent > 0) {
                            val pct = if (totalSpent > 0) Math.round((biggest.spent / totalSpent) * 100).toInt() else 0
                            val sym = profile.currencySymbol
                            alertsList.add(
                                AlertItem(
                                    id = "biggest_spend",
                                    type = AlertType.BIGGEST_SPEND,
                                    title = "${biggest.category.categoryName} is your biggest spend",
                                    body = "$pct% of everything spent in ${month.displayName.split(" ")[0]} (${formatAmount(biggest.spent, sym)}).",
                                    tag = month.displayName.split(" ")[0]
                                )
                            )
                        }

                        // Savings goal / Emergency fund alert
                        val emergencyGoal = profile.emergencyFundGoal
                        val savedSoFar = Math.max(0.0, totalIncome - totalSpent)
                        val fundedPct = if (emergencyGoal > 0) Math.min(100, Math.round((savedSoFar / emergencyGoal) * 100).toInt()) else 0
                        val sym = profile.currencySymbol
                        alertsList.add(
                            AlertItem(
                                id = "emergency_fund",
                                type = AlertType.SAVINGS_GOAL,
                                title = "Emergency fund is $fundedPct% funded",
                                body = "${formatAmount(savedSoFar, sym)} saved of ${formatAmount(emergencyGoal, sym)}.",
                                tag = "Savings goal"
                            )
                        )

                        _uiState.value = TallyUiState(
                            userProfile = profile,
                            selectedMonth = month,
                            isCurrentMonth = (month.year == 2026 && month.month == 9),
                            categories = categories,
                            transactions = txList,
                            recentTransactions = txList.take(5),
                            totalBudget = totalBudget,
                            totalSpent = totalSpent,
                            totalIncome = totalIncome,
                            leftToSpend = leftToSpend,
                            daysLeftInMonth = daysLeft,
                            safePerDay = safePerDay,
                            unassignedIncome = unassigned,
                            categoryProgressList = sortedProgress,
                            overBudgetCategories = overBudgetItems,
                            alerts = alertsList,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun calculateDaysLeftInMonth(month: MonthItem): Int {
        // For September 2026 simulation (as in screenshot 10: "10 days left in the month" on 21/09/2026)
        if (month.year == 2026 && month.month == 9) {
            return 10
        }
        val cal = Calendar.getInstance()
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        return Math.max(1, totalDays - currentDay)
    }

    fun selectPreviousMonth() {
        val current = _selectedMonth.value
        val newMonth = if (current.month == 1) 12 else current.month - 1
        val newYear = if (current.month == 1) current.year - 1 else current.year
        _selectedMonth.value = MonthItem(newYear, newMonth)
    }

    fun selectNextMonth() {
        val current = _selectedMonth.value
        val newMonth = if (current.month == 12) 1 else current.month + 1
        val newYear = if (current.month == 12) current.year + 1 else current.year
        _selectedMonth.value = MonthItem(newYear, newMonth)
    }

    fun createBudget(name: String, income: Double, currencySymbol: String, currencyName: String) {
        viewModelScope.launch {
            repository.createCustomBudget(name, income, currencySymbol, currencyName)
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.loadSampleData()
            _selectedMonth.value = MonthItem(2026, 9)
        }
    }

    fun updateCategoryLimit(categoryKey: String, newLimit: Double) {
        viewModelScope.launch {
            repository.updateCategoryLimit(categoryKey, newLimit)
        }
    }

    fun resetToSuggestedSplit() {
        viewModelScope.launch {
            val income = _uiState.value.userProfile?.monthlyIncome ?: 6999.0
            repository.resetToSuggested(income)
        }
    }

    fun addTransaction(
        type: String,
        amount: Double,
        categoryKey: String,
        categoryName: String,
        note: String,
        dateString: String
    ) {
        viewModelScope.launch {
            val currentMonth = _selectedMonth.value.monthKey
            val tx = TransactionEntity(
                type = type,
                amount = amount,
                categoryKey = categoryKey,
                categoryName = categoryName,
                note = note.ifBlank { categoryName },
                timestamp = System.currentTimeMillis(),
                dateString = dateString,
                monthKey = currentMonth
            )
            repository.addTransaction(tx)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun updateUserProfile(name: String, monthlyIncome: Double, currencySymbol: String) {
        viewModelScope.launch {
            val current = _uiState.value.userProfile ?: UserProfile()
            val updated = current.copy(
                name = name,
                monthlyIncome = monthlyIncome,
                currencySymbol = currencySymbol
            )
            repository.saveUserProfile(updated)
        }
    }

    companion object {
        fun formatAmount(amount: Double, symbol: String = "₹"): String {
            val nf = NumberFormat.getNumberInstance(Locale.US)
            nf.maximumFractionDigits = 0
            val formatted = nf.format(Math.abs(amount).toLong())
            return if (amount < 0) "-$symbol$formatted" else "$symbol$formatted"
        }
    }
}
