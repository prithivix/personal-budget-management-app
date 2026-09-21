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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallyForestGreen
import com.example.ui.theme.TallyForestGreenLight
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallyTerracottaDot
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.TallyUiState
import com.example.ui.viewmodel.TallyViewModel

enum class AppNavTab(val title: String, val testTag: String) {
    HOME("Home", "nav_tab_home"),
    ACTIVITY("Activity", "nav_tab_activity"),
    ADD("Add", "nav_tab_add"),
    BUDGETS("Budgets", "nav_tab_budgets"),
    ALERTS("Alerts", "nav_tab_alerts")
}

@Composable
fun MainAppScreen(
    uiState: TallyUiState,
    viewModel: TallyViewModel,
    modifier: Modifier = Modifier
) {
    // If not onboarded, show OnboardingScreen
    val profile = uiState.userProfile
    if (profile == null || !profile.isOnboarded) {
        OnboardingScreen(
            onCreateBudget = { name, income, symbol, currencyName ->
                viewModel.createBudget(name, income, symbol, currencyName)
            },
            onExploreSampleData = {
                viewModel.loadSampleData()
            }
        )
        return
    }

    var currentTab by remember { mutableStateOf(AppNavTab.HOME) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TallyBg,
        bottomBar = {
            TallyBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
                hasAlerts = uiState.overBudgetCategories.isNotEmpty()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.selectPreviousMonth() },
                        onNextMonth = { viewModel.selectNextMonth() },
                        onNavigateToAdd = { currentTab = AppNavTab.ADD },
                        onNavigateToBudgets = { currentTab = AppNavTab.BUDGETS },
                        onNavigateToActivity = { currentTab = AppNavTab.ACTIVITY },
                        onAdjustCategoryLimit = { key, limit ->
                            viewModel.updateCategoryLimit(key, limit)
                        },
                        onAvatarClick = { showProfileDialog = true }
                    )
                }
                AppNavTab.ACTIVITY -> {
                    ActivityScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.selectPreviousMonth() },
                        onNextMonth = { viewModel.selectNextMonth() },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
                    )
                }
                AppNavTab.ADD -> {
                    AddTransactionScreen(
                        uiState = uiState,
                        onAddTransaction = { type, amount, catKey, catName, note, dateStr ->
                            viewModel.addTransaction(type, amount, catKey, catName, note, dateStr)
                            // navigate to home or activity after adding
                            currentTab = AppNavTab.HOME
                        }
                    )
                }
                AppNavTab.BUDGETS -> {
                    BudgetsScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.selectPreviousMonth() },
                        onNextMonth = { viewModel.selectNextMonth() },
                        onResetToSuggested = { viewModel.resetToSuggestedSplit() },
                        onAdjustCategoryLimit = { key, limit ->
                            viewModel.updateCategoryLimit(key, limit)
                        }
                    )
                }
                AppNavTab.ALERTS -> {
                    AlertsScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.selectPreviousMonth() },
                        onNextMonth = { viewModel.selectNextMonth() },
                        onAlertClick = { alert ->
                            if (alert.categoryKey != null) {
                                currentTab = AppNavTab.BUDGETS
                            }
                        }
                    )
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfileEditDialog(
            currentName = profile.name,
            currentIncome = profile.monthlyIncome,
            currencySymbol = profile.currencySymbol,
            onDismiss = { showProfileDialog = false },
            onSave = { name, income ->
                viewModel.updateUserProfile(name, income, profile.currencySymbol)
                showProfileDialog = false
            },
            onReloadSampleData = {
                viewModel.loadSampleData()
                showProfileDialog = false
            }
        )
    }
}

@Composable
fun TallyBottomNavigationBar(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    hasAlerts: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar"),
        color = TallySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppNavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val itemColor = if (isSelected) TallyForestGreen else TallyTextSecondary

                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag(tab.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        // Icon Pill Container
                        val iconBg = if (isSelected) TallyForestGreenLight else Color.Transparent
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(iconBg)
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (tab) {
                                AppNavTab.HOME -> {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Home",
                                        tint = itemColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                AppNavTab.ACTIVITY -> {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.List,
                                        contentDescription = "Activity",
                                        tint = itemColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                AppNavTab.ADD -> {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = itemColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                AppNavTab.BUDGETS -> {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Budgets",
                                        tint = itemColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                AppNavTab.ALERTS -> {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Alerts",
                                        tint = itemColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Badge Dot on Alerts tab if over budget
                        if (tab == AppNavTab.ALERTS && hasAlerts) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(TallyTerracottaDot)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = itemColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileEditDialog(
    currentName: String,
    currentIncome: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit,
    onReloadSampleData: () -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var incomeInput by remember { mutableStateOf(currentIncome.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Budget profile",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Your name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TallyForestGreen,
                        unfocusedBorderColor = TallyBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = incomeInput,
                    onValueChange = { incomeInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monthly income ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TallyForestGreen,
                        unfocusedBorderColor = TallyBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = onReloadSampleData,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reload prototype sample data", color = TallyForestGreen)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inc = incomeInput.toDoubleOrNull() ?: currentIncome
                    onSave(nameInput, inc)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TallyForestGreen),
                shape = RoundedCornerShape(10.dp)
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
