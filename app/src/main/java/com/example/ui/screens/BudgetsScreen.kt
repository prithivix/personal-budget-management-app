package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.components.MonthSelector
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.CategoryProgressItem
import com.example.ui.viewmodel.TallyUiState
import com.example.ui.viewmodel.TallyViewModel

@Composable
fun BudgetsScreen(
    uiState: TallyUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToSuggested: () -> Unit,
    onAdjustCategoryLimit: (String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "₹"
    val monthlyIncome = uiState.userProfile?.monthlyIncome ?: 6999.0
    val monthName = uiState.selectedMonth.displayName.split(" ")[0]

    var selectedCategoryForAdjust by remember { mutableStateOf<CategoryProgressItem?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TallyBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Title & Subtitle
                Text(
                    text = "Budgets",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = TallyTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Monthly limits per category. Tap a card to change one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TallyTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Month Selector
                MonthSelector(
                    monthItem = uiState.selectedMonth,
                    isCurrentMonth = uiState.isCurrentMonth,
                    onPrevious = onPreviousMonth,
                    onNext = onNextMonth
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Total Budget Summary Card
                val totalBudgetFmt = TallyViewModel.formatAmount(uiState.totalBudget, currencySymbol)
                val spentFmt = TallyViewModel.formatAmount(uiState.totalSpent, currencySymbol)
                val unassignedFmt = TallyViewModel.formatAmount(uiState.unassignedIncome, currencySymbol)
                val incomeFmt = TallyViewModel.formatAmount(monthlyIncome, currencySymbol)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("total_budget_summary_card"),
                    shape = RoundedCornerShape(20.dp),
                    color = TallySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Total budget",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TallyTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = totalBudgetFmt,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp
                                    ),
                                    color = TallyTextPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Spent in $monthName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TallyTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = spentFmt,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp
                                    ),
                                    color = TallyTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress bar
                        val progress = if (uiState.totalBudget > 0) {
                            (uiState.totalSpent / uiState.totalBudget).toFloat().coerceIn(0f, 1f)
                        } else 0f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFEDE8DF))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(TallyForestGreen)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "$unassignedFmt of your $incomeFmt monthly income isn't assigned to a category. That's your room to save.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TallyTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reset to suggested split button
                OutlinedButton(
                    onClick = onResetToSuggested,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("reset_suggested_split_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TallyTextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCD6CA))
                ) {
                    Text(
                        text = "Reset to a suggested split",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section: Categories
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = TallyTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Category cards list
            items(uiState.categoryProgressList, key = { it.category.categoryKey }) { item ->
                CategoryCard(
                    item = item,
                    currencySymbol = currencySymbol,
                    showAdjustButton = false, // Tap card itself to change limit
                    onClick = { selectedCategoryForAdjust = item }
                )
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
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
