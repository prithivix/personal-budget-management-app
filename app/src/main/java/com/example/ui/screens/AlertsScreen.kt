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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MonthSelector
import com.example.ui.theme.TallyBg
import com.example.ui.theme.TallyBorder
import com.example.ui.theme.TallySurface
import com.example.ui.theme.TallyTerracottaDot
import com.example.ui.theme.TallyTerracottaText
import com.example.ui.theme.TallyTextPrimary
import com.example.ui.theme.TallyTextSecondary
import com.example.ui.viewmodel.AlertItem
import com.example.ui.viewmodel.AlertType
import com.example.ui.viewmodel.TallyUiState

@Composable
fun AlertsScreen(
    uiState: TallyUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAlertClick: (AlertItem) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "Alerts",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = TallyTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Overspending, trends and goals for the month you're viewing",
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

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(uiState.alerts, key = { it.id }) { alert ->
                AlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: AlertItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (iconBg, emoji) = when (alert.type) {
        AlertType.OVER_BUDGET -> Pair(Color(0xFFFDECE7), "🚨")
        AlertType.BIGGEST_SPEND -> Pair(Color(0xFFE8F3EE), "🛍️")
        AlertType.SAVINGS_GOAL -> Pair(Color(0xFFE8F2FA), "🎯")
        AlertType.ON_TRACK -> Pair(Color(0xFFE6F4EA), "✨")
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("alert_card_${alert.id}"),
        shape = RoundedCornerShape(18.dp),
        color = TallySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TallyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TallyTextPrimary
                    )

                    if (alert.hasAttentionDot) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TallyTerracottaDot)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = alert.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TallyTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val tagColor = if (alert.type == AlertType.OVER_BUDGET) {
                    TallyTerracottaText
                } else {
                    TallyTextSecondary
                }

                Text(
                    text = alert.tag,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = tagColor
                )
            }
        }
    }
}
