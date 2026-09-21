package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallyTextMuted
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.TallyUiState
import com.example.ui.viewmodel.TallyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionScreen(
    uiState: TallyUiState,
    onAddTransaction: (type: String, amount: Double, categoryKey: String, categoryName: String, note: String, dateString: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencySymbol = uiState.userProfile?.currencySymbol ?: "₹"
    val context = LocalContext.current

    var txType by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"
    var amountText by remember { mutableStateOf("0") }
    var selectedCategoryKey by remember {
        mutableStateOf(uiState.categories.firstOrNull()?.categoryKey ?: "housing")
    }
    var categoryDropdownOpen by remember { mutableStateOf(false) }

    // Date formatting matching prototype: "21/09/2026"
    var dateString by remember { mutableStateOf("21/09/2026") }
    var note by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val selectedCategory = uiState.categories.find { it.categoryKey == selectedCategoryKey }
        ?: uiState.categories.firstOrNull()

    // Context hint: e.g. "Housing has ₹1,960 left of its ₹1,960 limit for September."
    val catProgress = uiState.categoryProgressList.find { it.category.categoryKey == selectedCategoryKey }
    val monthName = uiState.selectedMonth.displayName.split(" ")[0]

    val contextHint = if (selectedCategory != null && catProgress != null) {
        val leftFmt = TallyViewModel.formatAmount(catProgress.diffAmount, currencySymbol)
        val limitFmt = TallyViewModel.formatAmount(catProgress.limit, currencySymbol)
        if (catProgress.isOverBudget) {
            "${selectedCategory.categoryName} is currently over budget by $leftFmt for $monthName."
        } else {
            "${selectedCategory.categoryName} has $leftFmt left of its $limitFmt limit for $monthName."
        }
    } else {
        ""
    }

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
            Spacer(modifier = Modifier.height(10.dp))

            // Header & Subtitle
            Text(
                text = "Add transaction",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                color = TallyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Updates your budgets the moment you save",
                style = MaterialTheme.typography.bodyMedium,
                color = TallyTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Segmented Control: [ Expense | Income ]
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
                    val isExpense = txType == "EXPENSE"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isExpense) TallySurface else Color.Transparent)
                            .clickable { txType = "EXPENSE" }
                            .testTag("toggle_expense_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = TallyForestGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isExpense) TallySurface else Color.Transparent)
                            .clickable { txType = "INCOME" }
                            .testTag("toggle_income_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = TallyForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount field
            Text(
                text = "Amount",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TallyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    amountText = if (filtered.isEmpty()) "0" else filtered.trimStart('0').ifEmpty { "0" }
                },
                textStyle = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TallyTextPrimary
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TallySurface,
                    unfocusedContainerColor = TallySurface,
                    focusedBorderColor = TallyForestGreen,
                    unfocusedBorderColor = TallyBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input_field")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Category selector
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TallyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val catEmoji = when (selectedCategory?.iconKey) {
                "house" -> "🏠"
                "groceries" -> "🛒"
                "eating_out" -> "🍜"
                "transport" -> "🚌"
                "bills" -> "💡"
                "shopping" -> "🛍️"
                "health" -> "💊"
                "hobbies" -> "🎬"
                else -> "📦"
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, TallyBorder, RoundedCornerShape(14.dp))
                        .clickable { categoryDropdownOpen = true }
                        .testTag("category_select_field"),
                    color = TallySurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = catEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedCategory?.categoryName ?: "Housing",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TallyTextPrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Choose category",
                            tint = TallyTextSecondary
                        )
                    }
                }

                DropdownMenu(
                    expanded = categoryDropdownOpen,
                    onDismissRequest = { categoryDropdownOpen = false }
                ) {
                    uiState.categories.forEach { cat ->
                        val emoji = when (cat.iconKey) {
                            "house" -> "🏠"
                            "groceries" -> "🛒"
                            "eating_out" -> "🍜"
                            "transport" -> "🚌"
                            "bills" -> "💡"
                            "shopping" -> "🛍️"
                            "health" -> "💊"
                            "hobbies" -> "🎬"
                            else -> "📦"
                        }
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = emoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = cat.categoryName)
                                }
                            },
                            onClick = {
                                selectedCategoryKey = cat.categoryKey
                                categoryDropdownOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Date picker field
            Text(
                text = "Date",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TallyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, TallyBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                dateString = String.format(Locale.US, "%02d/%02d/%04d", d, m + 1, y)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .testTag("date_picker_field"),
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
                        text = dateString,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TallyTextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select date",
                        tint = TallyTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Note (optional)
            Text(
                text = "Note (optional)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TallyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("What was it for?", color = TallyTextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TallySurface,
                    unfocusedContainerColor = TallySurface,
                    focusedBorderColor = TallyForestGreen,
                    unfocusedBorderColor = TallyBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input_field")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Context note
            if (contextHint.isNotBlank()) {
                Text(
                    text = contextHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = TallyTextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button: "Add expense" or "Add income"
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && selectedCategory != null) {
                        onAddTransaction(
                            txType,
                            amt,
                            selectedCategory.categoryKey,
                            selectedCategory.categoryName,
                            note.ifBlank { selectedCategory.categoryName },
                            dateString
                        )
                        amountText = "0"
                        note = ""
                        showSuccessMessage = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_transaction_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TallyForestGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (txType == "EXPENSE") "Add expense" else "Add income",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Section: Recently added
            Text(
                text = "Recently added",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = TallyTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TallyTextSecondary
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.recentTransactions.forEach { tx ->
                        val cat = uiState.categories.find { it.categoryKey == tx.categoryKey }
                        val iconKey = cat?.iconKey ?: "shopping"
                        val sign = if (tx.type == "EXPENSE") "-" else "+"
                        val amountFmt = "$sign$currencySymbol${tx.amount.toInt()}"

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .testTag("recently_added_${tx.id}"),
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

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
