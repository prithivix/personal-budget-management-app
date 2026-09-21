package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_budgets")
data class CategoryBudget(
    @PrimaryKey val categoryKey: String,
    val categoryName: String,
    val iconKey: String,
    val monthlyLimit: Double,
    val defaultPercentage: Double = 0.0,
    val displayOrder: Int = 0
)

enum class StandardCategory(
    val key: String,
    val displayName: String,
    val iconKey: String,
    val defaultPercentage: Double,
    val displayOrder: Int
) {
    HOUSING("housing", "Housing", "house", 0.28, 1),
    GROCERIES("groceries", "Groceries", "groceries", 0.14, 2),
    EATING_OUT("eating_out", "Eating out", "eating_out", 0.06, 3),
    TRANSPORT("transport", "Transport", "transport", 0.08, 4),
    BILLS("bills", "Bills & utilities", "bills", 0.08, 5),
    SHOPPING("shopping", "Shopping", "shopping", 0.06, 6),
    HEALTH("health", "Health", "health", 0.04, 7),
    HOBBIES("hobbies", "Fun & hobbies", "hobbies", 0.05, 8),
    OTHER("other", "Other", "other", 0.03, 9);

    companion object {
        fun defaultCategories(monthlyIncome: Double): List<CategoryBudget> {
            return entries.map { cat ->
                CategoryBudget(
                    categoryKey = cat.key,
                    categoryName = cat.displayName,
                    iconKey = cat.iconKey,
                    monthlyLimit = Math.round(monthlyIncome * cat.defaultPercentage).toDouble(),
                    defaultPercentage = cat.defaultPercentage,
                    displayOrder = cat.displayOrder
                )
            }
        }
    }
}
