package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Aditi Sharma",
    val monthlyIncome: Double = 6999.0,
    val currencySymbol: String = "₹",
    val currencyName: String = "Indian rupee",
    val emergencyFundGoal: Double = 21000.0,
    val isOnboarded: Boolean = false
)
