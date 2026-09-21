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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.TransactionEntity
import com.example.ui.components.CategoryIcon
import com.example.ui.components.MonthSelector
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallySurfaceVariant
import com.example.ui.theme.TallyTextMuted
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.TallyUiState
import com.example.ui.viewmodel.TallyViewModel

enum class ActivityFilterType { ALL, SPENDING, INCOME }

@Composable
fun ActivityScreen(
    uiState: TallyUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "₹"

    var selectedFilter by remember { mutableStateOf(ActivityFilterType.ALL) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var categoryDropdownOpen by remember { mutableStateOf(false) }

    // Filter transactions
    val filteredTransactions = uiState.transactions.filter { tx ->
        val typeMatch = when (selectedFilter) {
            ActivityFilterType.ALL -> true
            ActivityFilterType.SPENDING -> tx.type == "EXPENSE"
            ActivityFilterType.INCOME -> tx.type == "INCOME"
        }
        val categoryMatch = if (selectedCategoryFilter == "ALL") true else tx.categoryKey == selectedCategoryFilter
        val searchMatch = if (searchQuery.isBlank()) true else {
            tx.note.contains(searchQuery, ignoreCase = true) ||
            tx.categoryName.contains(searchQuery, ignoreCase = true)
        }
        typeMatch && categoryMatch && searchMatch
    }

    // Net calculation
    val netAmount = filteredTransactions.sumOf { tx ->
        if (tx.type == "INCOME") tx.amount else -tx.amount
    }
    val netFormatted = if (netAmount < 0) {
        "-${TallyViewModel.formatAmount(Math.abs(netAmount), currencySymbol)}"
    } else {
        "+${TallyViewModel.formatAmount(netAmount, currencySymbol)}"
    }

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
                    text = "Activity",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = TallyTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Every transaction, newest first",
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

                // Segmented Tabs: [ All | Spending | Income ]
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE5EDE7)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        listOf(
                            Triple(ActivityFilterType.ALL, "All", "activity_filter_all"),
                            Triple(ActivityFilterType.SPENDING, "Spending", "activity_filter_spending"),
                            Triple(ActivityFilterType.INCOME, "Income", "activity_filter_income")
                        ).forEach { (filterType, title, tag) ->
                            val isSelected = selectedFilter == filterType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TallySurface else Color.Transparent)
                                    .clickable { selectedFilter = filterType }
                                    .testTag(tag),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) TallyForestGreen else TallyForestGreen.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Dropdown
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TallyTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                val selectedCategoryName = if (selectedCategoryFilter == "ALL") {
                    "All categories"
                } else {
                    uiState.categories.find { it.categoryKey == selectedCategoryFilter }?.categoryName ?: "All categories"
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, TallyBorder, RoundedCornerShape(12.dp))
                            .clickable { categoryDropdownOpen = true }
                            .testTag("activity_category_dropdown"),
                        color = TallySurface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategoryName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TallyTextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select category filter",
                                tint = TallyTextSecondary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryDropdownOpen,
                        onDismissRequest = { categoryDropdownOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All categories") },
                            onClick = {
                                selectedCategoryFilter = "ALL"
                                categoryDropdownOpen = false
                            }
                        )
                        uiState.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.categoryName) },
                                onClick = {
                                    selectedCategoryFilter = cat.categoryKey
                                    categoryDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Notes
                Text(
                    text = "Search notes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TallyTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("e.g. rent, coffee", color = TallyTextMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TallySurface,
                        unfocusedContainerColor = TallySurface,
                        focusedBorderColor = TallyForestGreen,
                        unfocusedBorderColor = TallyBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_notes_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Transaction Count and Net
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val countText = if (filteredTransactions.size == 1) "1 transaction" else "${filteredTransactions.size} transactions"
                    Text(
                        text = countText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = TallyTextPrimary
                    )

                    Text(
                        text = "Net $netFormatted",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TallyTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Today",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = TallyTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Transaction Cards List
            if (filteredTransactions.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = TallySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TallyTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredTransactions, key = { it.id }) { tx ->
                    val catProgress = uiState.categoryProgressList.find { it.category.categoryKey == tx.categoryKey }
                    val iconKey = catProgress?.category?.iconKey ?: "shopping"
                    val sign = if (tx.type == "EXPENSE") "-" else "+"
                    val amountFmt = "$sign$currencySymbol${tx.amount.toInt()}"

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .testTag("activity_tx_${tx.id}"),
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
                                Spacer(modifier = Modifier.width(14.dp))
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

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = amountFmt,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = TallyTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onDeleteTransaction(tx.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("delete_tx_${tx.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete transaction",
                                        tint = TallyTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
