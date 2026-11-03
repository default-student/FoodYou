package com.maksimowiczm.foodyou.app.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.common.compose.utility.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun StatisticsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: StatisticsViewModel = koinViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val scroll = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.statistics_title)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scroll,
            )
        },
    ) { padding ->
        if (state == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).nestedScroll(scroll.nestedScrollConnection),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(stringResource(Res.string.statistics_averages), style = MaterialTheme.typography.titleMedium)
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 7, 14).forEach { days ->
                            AverageCard(days, StatisticsCalculator.window(state.today, days, state.totals))
                        }
                    }
                }
                item {
                    Text(stringResource(Res.string.statistics_daily), style = MaterialTheme.typography.titleMedium)
                }
                item { DailyHistory(state.today, state.totals) }
                item {
                    Text(stringResource(Res.string.statistics_weeks), style = MaterialTheme.typography.titleMedium)
                }
                item {
                    WeekCard(
                        stringResource(Res.string.statistics_this_week),
                        StatisticsCalculator.calendarWeek(state.today, false, state.totals),
                    )
                }
                item {
                    WeekCard(
                        stringResource(Res.string.statistics_previous_week),
                        StatisticsCalculator.calendarWeek(state.today, true, state.totals),
                    )
                }
            }
        }
    }
}

@Composable
private fun AverageCard(days: Int, summary: EnergySummary) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(Res.string.statistics_last_days, days), style = MaterialTheme.typography.titleMedium)
            Text(
                summary.average?.let { "${it.roundToInt()} kcal" }
                    ?: stringResource(Res.string.statistics_no_data),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                stringResource(Res.string.statistics_contributing_days, summary.contributingDays),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekCard(title: String, summary: EnergySummary) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text("${summary.total.roundToInt()} kcal", style = MaterialTheme.typography.titleLarge)
            Text(
                summary.average?.let { stringResource(Res.string.statistics_week_average, it.roundToInt()) }
                    ?: stringResource(Res.string.statistics_no_data),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(Res.string.statistics_contributing_days, summary.contributingDays),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DailyHistory(today: LocalDate, totals: Map<LocalDate, Double>) {
    val days = (today.toEpochDays() - 13..today.toEpochDays()).map { LocalDate.fromEpochDays(it) }
    val max = days.maxOfOrNull { totals[it] ?: 0.0 }?.coerceAtLeast(1.0) ?: 1.0
    val dateFormatter = LocalDateFormatter.current

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                stringResource(Res.string.statistics_exclusion_note),
                Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                days.forEach { date ->
                    val value = totals[date] ?: 0.0
                    Column(
                        Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                            Box(
                                Modifier.fillMaxWidth(0.8f)
                                    .fillMaxHeight((value / max).toFloat().coerceIn(0.02f, 1f))
                                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                            )
                        }
                        Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            HorizontalDivider()
            days.asReversed().forEach { date ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        dateFormatter.formatDateShort(date),
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        "${(totals[date] ?: 0.0).roundToInt()} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
