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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallyTextMuted
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary

data class CurrencyOption(val symbol: String, val name: String, val label: String)

@Composable
fun OnboardingScreen(
    onCreateBudget: (name: String, income: Double, symbol: String, currencyName: String) -> Unit,
    onExploreSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("Aditi Sharma") }
    var income by remember { mutableStateOf("60000") }
    val currencies = listOf(
        CurrencyOption("₹", "Indian rupee", "₹ Indian rupee"),
        CurrencyOption("$", "US dollar", "$ US dollar"),
        CurrencyOption("€", "Euro", "€ Euro"),
        CurrencyOption("£", "British pound", "£ British pound"),
        CurrencyOption("¥", "Japanese yen", "¥ Japanese yen")
    )
    var selectedCurrency by remember { mutableStateOf(currencies[0]) }
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TallyBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo with rounded forest green square & white "T"
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(TallyForestGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "T",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // App Name
            Text(
                text = "Tally",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp
                ),
                color = TallyTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Know where it goes. Keep what's left.",
                style = MaterialTheme.typography.bodyMedium,
                color = TallyTextSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Form inputs
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Name
                Column {
                    Text(
                        text = "Your name",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TallyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. Aditi Sharma", color = TallyTextMuted) },
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
                            .testTag("onboarding_name_input")
                    )
                }

                // Monthly income
                Column {
                    Text(
                        text = "Monthly income after tax",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TallyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = income,
                        onValueChange = { income = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("60000", color = TallyTextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TallySurface,
                            unfocusedContainerColor = TallySurface,
                            focusedBorderColor = TallyForestGreen,
                            unfocusedBorderColor = TallyBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_income_input")
                    )
                }

                // Currency Dropdown
                Column {
                    Text(
                        text = "Currency",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TallyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, TallyBorder, RoundedCornerShape(12.dp))
                                .clickable { currencyMenuExpanded = true }
                                .testTag("currency_dropdown"),
                            color = TallySurface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedCurrency.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TallyTextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select currency",
                                    tint = TallyTextSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = currencyMenuExpanded,
                            onDismissRequest = { currencyMenuExpanded = false }
                        ) {
                            currencies.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.label) },
                                    onClick = {
                                        selectedCurrency = item
                                        currencyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Explanatory note
            Text(
                text = "We'll suggest a starting limit for each category from your income. You can change any of them later. Everything stays on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = TallyTextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Button: Create my budget
            Button(
                onClick = {
                    val inc = income.toDoubleOrNull() ?: 60000.0
                    onCreateBudget(name, inc, selectedCurrency.symbol, selectedCurrency.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("create_budget_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TallyForestGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Create my budget",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Link: Explore with sample data instead
            TextButton(
                onClick = onExploreSampleData,
                modifier = Modifier.testTag("explore_sample_data_btn")
            ) {
                Text(
                    text = "Explore with sample data instead",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = TallyForestGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
