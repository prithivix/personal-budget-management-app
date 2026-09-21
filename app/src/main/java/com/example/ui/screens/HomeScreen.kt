package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdjustBudgetDialog
import com.example.ui.components.CategoryCard
import com.example.ui.components.CategoryIcon
import com.example.ui.components.MonthSelector
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallyForestGreenLight
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallyTerracottaBanner
import com.example.ui.theme.TallyTextMuted
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.CategoryProgressItem
import com.example.ui.viewmodel.TallyUiState
import com.example.ui.viewmodel.TallyViewModel

@Composable
fun HomeScreen(
    uiState: TallyUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onAdjustCategoryLimit: (String, Double) -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "₹"
    val userName = uiState.userProfile?.name ?: "User"
    val firstInitial = userName.trim().firstOrNull()?.uppercase() ?: "U"

    var selectedCategoryForAdjust by remember { mutableStateOf<CategoryProgressItem?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TallyBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Header: Greeting, Name, Avatar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good afternoon,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TallyTextSecondary
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = TallyTextPrimary
                    )
                }

                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(TallyForestGreenLight)
                        .clickable(onClick = onAvatarClick)
                        .testTag("avatar_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstInitial,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TallyForestGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Month Selector
            MonthSelector(
                monthItem = uiState.selectedMonth,
                isCurrentMonth = uiState.isCurrentMonth,
                onPrevious = onPreviousMonth,
                onNext = onNextMonth
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Left to spend Hero Card
            val leftFormatted = TallyViewModel.formatAmount(uiState.leftToSpend, currencySymbol)
            val spentFormatted = TallyViewModel.formatAmount(uiState.totalSpent, currencySymbol)
            val budgetFormatted = TallyViewModel.formatAmount(uiState.totalBudget, currencySymbol)
            val monthTitle = uiState.selectedMonth.displayName.split(" ")[0]

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("left_to_spend_card"),
                shape = RoundedCornerShape(22.dp),
                color = TallyForestGreen
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "Left to spend in $monthTitle",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = leftFormatted,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 42.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    val budgetProgress = if (uiState.totalBudget > 0) {
                        (uiState.totalSpent / uiState.totalBudget).toFloat().coerceIn(0f, 1f)
                    } else 0f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(budgetProgress)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "$spentFormatted spent of $budgetFormatted budgeted. ${uiState.daysLeftInMonth} days left in the month.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
            }

            // Over Budget Alert Banner (if any category is over budget)
            if (uiState.overBudgetCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                val overCategory = uiState.overBudgetCategories.first()
                val countText = if (uiState.overBudgetCategories.size == 1) {
                    "1 category is over budget"
                } else {
                    "${uiState.overBudgetCategories.size} categories are over budget"
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("over_budget_banner"),
                    shape = RoundedCornerShape(20.dp),
                    color = TallyTerracottaBanner
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🚨", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = countText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${overCategory.category.categoryName}. Review your limits or move some spending.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNavigateToBudgets,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("review_budgets_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = TallyTerracottaBanner
                            )
                        ) {
                            Text(
                                text = "Review budgets",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Stat Metric Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val safePerDayFmt = TallyViewModel.formatAmount(uiState.safePerDay, currencySymbol)
                val incomeFmt = TallyViewModel.formatAmount(uiState.totalIncome, currencySymbol)
                val spentFmt = TallyViewModel.formatAmount(uiState.totalSpent, currencySymbol)

                MetricStatCard(
                    amount = incomeFmt,
                    label = "Income",
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    amount = spentFmt,
                    label = "Spent",
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    amount = safePerDayFmt,
                    label = "Safe per day",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Closest to their limit
            Text(
                text = "Closest to their limit",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = TallyTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show top 3 categories closest to limit
            val topCategories = uiState.categoryProgressList.take(3)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                topCategories.forEach { item ->
                    CategoryCard(
                        item = item,
                        currencySymbol = currencySymbol,
                        showAdjustButton = true,
                        onAdjustClick = { selectedCategoryForAdjust = item },
                        onClick = { selectedCategoryForAdjust = item }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Recent transactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent transactions",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = TallyTextPrimary
                )

                TextButton(
                    onClick = onNavigateToActivity,
                    modifier = Modifier.testTag("see_all_transactions_btn")
                ) {
                    Text(
                        text = "See all",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TallyForestGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (uiState.recentTransactions.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = TallySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions yet this month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TallyTextSecondary
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.recentTransactions.take(3).forEach { tx ->
                        val catProgress = uiState.categoryProgressList.find { it.category.categoryKey == tx.categoryKey }
                        val iconKey = catProgress?.category?.iconKey ?: "shopping"
                        val sign = if (tx.type == "EXPENSE") "-" else "+"
                        val amountFmt = "$sign$currencySymbol${tx.amount.toInt()}"

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .testTag("recent_tx_${tx.id}"),
                            shape = RoundedCornerShape(18.dp),
                            color = TallySurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CategoryIcon(iconKey = iconKey)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = tx.categoryName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TallyTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tx.note.ifBlank { tx.categoryName },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TallyTextSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = amountFmt,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = TallyTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Quick actions
            Text(
                text = "Quick actions",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = TallyTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_add_expense_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TallyForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Add expense",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToBudgets,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_edit_budgets_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TallyTextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD7D2C7))
                ) {
                    Text(
                        text = "Edit budgets",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Adjust Budget Dialog
        selectedCategoryForAdjust?.let { item ->
            AdjustBudgetDialog(
                categoryName = item.category.categoryName,
                currentLimit = item.limit,
                currencySymbol = currencySymbol,
                onDismiss = { selectedCategoryForAdjust = null },
                onSave = { newLimit ->
                    onAdjustCategoryLimit(item.category.categoryKey, newLimit)
                    selectedCategoryForAdjust = null
                }
            )
        }
    }
}

@Composable
private fun MetricStatCard(
    amount: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .testTag("stat_card_${label.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        color = TallySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = TallyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TallyTextSecondary
            )
        }
    }
}
