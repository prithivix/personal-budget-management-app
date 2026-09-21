package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallyGreenDot
import com.example.ui.theme.TallyGreenText
import com.example.ui.theme.TallyIconBgBills
import com.example.ui.theme.TallyIconBgEatingOut
import com.example.ui.theme.TallyIconBgGroceries
import com.example.ui.theme.TallyIconBgHealth
import com.example.ui.theme.TallyIconBgHobbies
import com.example.ui.theme.TallyIconBgHouse
import com.example.ui.theme.TallyIconBgOther
import com.example.ui.theme.TallyIconBgShopping
import com.example.ui.theme.TallyIconBgTransport
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallySurfaceVariant
import com.example.ui.theme.TallyTerracotta
import com.example.ui.theme.TallyTerracottaDot
import com.example.ui.theme.TallyTerracottaLight
import com.example.ui.theme.TallyTerracottaText
import com.example.ui.theme.TallyTextMuted
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.CategoryProgressItem
import com.example.ui.viewmodel.MonthItem
import com.example.ui.viewmodel.TallyViewModel

@Composable
fun MonthSelector(
    monthItem: MonthItem,
    isCurrentMonth: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("month_selector"),
        shape = RoundedCornerShape(16.dp),
        color = TallySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPrevious,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("month_prev_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = TallyForestGreen
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = monthItem.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TallyTextPrimary
                )
                Text(
                    text = if (isCurrentMonth) "Current month" else "Viewing month",
                    style = MaterialTheme.typography.bodySmall,
                    color = TallyTextSecondary
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("month_next_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = TallyForestGreen
                )
            }
        }
    }
}

@Composable
fun CategoryIcon(
    iconKey: String,
    modifier: Modifier = Modifier
) {
    val (bg, iconVector, emoji) = when (iconKey) {
        "house" -> Triple(TallyIconBgHouse, Icons.Default.Home, "🏠")
        "groceries" -> Triple(TallyIconBgGroceries, Icons.Default.ShoppingCart, "🛒")
        "eating_out" -> Triple(TallyIconBgEatingOut, Icons.Default.Restaurant, "🍜")
        "transport" -> Triple(TallyIconBgTransport, Icons.Default.DirectionsBus, "🚌")
        "bills" -> Triple(TallyIconBgBills, Icons.Default.Lightbulb, "💡")
        "shopping" -> Triple(TallyIconBgShopping, Icons.Default.LocalMall, "🛍️")
        "health" -> Triple(TallyIconBgHealth, Icons.Default.MedicalServices, "💊")
        "hobbies" -> Triple(TallyIconBgHobbies, Icons.Default.Movie, "🎬")
        else -> Triple(TallyIconBgOther, Icons.Default.Inventory2, "📦")
    }

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 22.sp
        )
    }
}

@Composable
fun CategoryCard(
    item: CategoryProgressItem,
    currencySymbol: String,
    showAdjustButton: Boolean = true,
    onAdjustClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spentFormatted = TallyViewModel.formatAmount(item.spent, currencySymbol)
    val limitFormatted = TallyViewModel.formatAmount(item.limit, currencySymbol)
    val diffFormatted = TallyViewModel.formatAmount(item.diffAmount, currencySymbol)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("category_card_${item.category.categoryKey}"),
        shape = RoundedCornerShape(18.dp),
        color = TallySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CategoryIcon(iconKey = item.category.iconKey)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.category.categoryName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TallyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$spentFormatted of $limitFormatted",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TallyTextSecondary
                        )
                    }
                }

                if (showAdjustButton) {
                    Button(
                        onClick = onAdjustClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TallyForestGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("adjust_btn_${item.category.categoryKey}")
                    ) {
                        Text(
                            text = "Adjust",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val animatedProgress by animateFloatAsState(
                targetValue = if (item.isOverBudget) 1f else item.progress,
                label = "progress"
            )
            val barColor = if (item.isOverBudget) TallyTerracotta else TallyForestGreen

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFEDE8DF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status Badge Pill
            if (item.isOverBudget) {
                StatusPill(
                    text = "Over by $diffFormatted",
                    dotColor = TallyTerracottaDot,
                    textColor = TallyTerracottaText,
                    bgColor = TallyTerracottaLight
                )
            } else {
                StatusPill(
                    text = "On track, $diffFormatted left",
                    dotColor = TallyGreenDot,
                    textColor = TallyGreenText,
                    bgColor = Color(0xFFE6F3EB)
                )
            }
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    dotColor: Color,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = textColor
            )
        }
    }
}

@Composable
fun AdjustBudgetDialog(
    categoryName: String,
    currentLimit: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var limitInput by remember { mutableStateOf(currentLimit.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Adjust $categoryName budget",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column {
                Text(
                    text = "Set monthly spending limit for $categoryName:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TallyTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = limitInput,
                    onValueChange = { limitInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monthly Limit ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TallyForestGreen,
                        unfocusedBorderColor = TallyBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_limit_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val curr = limitInput.toDoubleOrNull() ?: currentLimit
                    listOf(-500, -100, 100, 500).forEach { delta ->
                        OutlinedButton(
                            onClick = {
                                val newVal = Math.max(0.0, curr + delta)
                                limitInput = newVal.toInt().toString()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (delta > 0) "+$delta" else "$delta",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = limitInput.toDoubleOrNull() ?: currentLimit
                    onSave(amount)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TallyForestGreen
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_budget_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TallyTextSecondary)
            }
        },
        containerColor = TallySurface,
        shape = RoundedCornerShape(20.dp)
    )
}
