package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TallyRepository(
    private val userProfileDao: UserProfileDao,
    private val categoryBudgetDao: CategoryBudgetDao,
    private val transactionDao: TransactionDao
) {
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allCategories: Flow<List<CategoryBudget>> = categoryBudgetDao.getAllCategoryBudgets()

    fun getTransactionsByMonth(monthKey: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByMonth(monthKey)
    }

    fun getRecentTransactions(monthKey: String, limit: Int = 5): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(monthKey, limit)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateCategoryLimit(categoryKey: String, limit: Double) {
        categoryBudgetDao.updateLimit(categoryKey, limit)
    }

    suspend fun resetToSuggested(monthlyIncome: Double) {
        val suggested = StandardCategory.defaultCategories(monthlyIncome)
        categoryBudgetDao.insertCategories(suggested)
    }

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun loadSampleData() {
        // Screenshot exact state:
        // Income = 6999, Total budget = 5740, Unassigned = 1259
        val profile = UserProfile(
            id = 1,
            name = "Aditi Sharma",
            monthlyIncome = 6999.0,
            currencySymbol = "₹",
            currencyName = "Indian rupee",
            emergencyFundGoal = 21000.0,
            isOnboarded = true
        )
        userProfileDao.insertOrUpdateProfile(profile)

        val categories = listOf(
            CategoryBudget("housing", "Housing", "house", 1960.0, 0.28, 1),
            CategoryBudget("groceries", "Groceries", "groceries", 980.0, 0.14, 2),
            CategoryBudget("eating_out", "Eating out", "eating_out", 420.0, 0.06, 3),
            CategoryBudget("transport", "Transport", "transport", 560.0, 0.08, 4),
            CategoryBudget("bills", "Bills & utilities", "bills", 560.0, 0.08, 5),
            CategoryBudget("shopping", "Shopping", "shopping", 420.0, 0.06, 6),
            CategoryBudget("health", "Health", "health", 280.0, 0.04, 7),
            CategoryBudget("hobbies", "Fun & hobbies", "hobbies", 350.0, 0.05, 8),
            CategoryBudget("other", "Other", "other", 210.0, 0.03, 9)
        )
        categoryBudgetDao.insertCategories(categories)

        transactionDao.clearAllTransactions()
        // Add sample transaction: Shopping ₹1,000 on 21/09/2026
        val sampleTx = TransactionEntity(
            type = "EXPENSE",
            amount = 1000.0,
            categoryKey = "shopping",
            categoryName = "Shopping",
            note = "Shopping",
            timestamp = System.currentTimeMillis(),
            dateString = "21/09/2026",
            monthKey = "2026-09"
        )
        transactionDao.insertTransaction(sampleTx)
    }

    suspend fun createCustomBudget(name: String, income: Double, currencySymbol: String, currencyName: String) {
        val profile = UserProfile(
            id = 1,
            name = name.ifBlank { "User" },
            monthlyIncome = income,
            currencySymbol = currencySymbol,
            currencyName = currencyName,
            emergencyFundGoal = income * 3.0,
            isOnboarded = true
        )
        userProfileDao.insertOrUpdateProfile(profile)
        val categories = StandardCategory.defaultCategories(income)
        categoryBudgetDao.insertCategories(categories)
    }
}
