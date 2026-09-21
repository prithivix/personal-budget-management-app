package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}

@Dao
interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets ORDER BY displayOrder ASC")
    fun getAllCategoryBudgets(): Flow<List<CategoryBudget>>

    @Query("SELECT * FROM category_budgets ORDER BY displayOrder ASC")
    suspend fun getAllCategoryBudgetsOnce(): List<CategoryBudget>

    @Query("SELECT * FROM category_budgets WHERE categoryKey = :categoryKey LIMIT 1")
    suspend fun getCategoryByKey(categoryKey: String): CategoryBudget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryBudget>)

    @Update
    suspend fun updateCategory(category: CategoryBudget)

    @Query("UPDATE category_budgets SET monthlyLimit = :limit WHERE categoryKey = :categoryKey")
    suspend fun updateLimit(categoryKey: String, limit: Double)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE monthKey = :monthKey ORDER BY timestamp DESC, id DESC")
    fun getTransactionsByMonth(monthKey: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE monthKey = :monthKey ORDER BY timestamp DESC, id DESC LIMIT :limit")
    fun getRecentTransactions(monthKey: String, limit: Int = 5): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()
}
