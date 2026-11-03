package com.maksimowiczm.foodyou.app.ui.statistics

import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

internal data class EnergySummary(val total: Double, val average: Double?, val contributingDays: Int)

internal object StatisticsCalculator {
    const val MINIMUM_TRACKED_KCAL = 500.0

    fun window(today: LocalDate, days: Int, totals: Map<LocalDate, Double>): EnergySummary {
        require(days > 0)
        val todayEpochDay = today.toEpochDays().toInt()
        val values = (todayEpochDay - days + 1..todayEpochDay)
            .map { totals[LocalDate.fromEpochDays(it)] ?: 0.0 }
        val contributing = values.filter { it >= MINIMUM_TRACKED_KCAL }
        return EnergySummary(values.sum(), contributing.takeIf { it.isNotEmpty() }?.average(), contributing.size)
    }

    fun calendarWeek(today: LocalDate, previous: Boolean, totals: Map<LocalDate, Double>): EnergySummary {
        val todayEpochDay = today.toEpochDays().toInt()
        val currentMonday = todayEpochDay - today.dayOfWeek.isoDayNumber + 1
        val start = currentMonday - if (previous) 7 else 0
        val end = if (previous) start + 6 else todayEpochDay
        val values = (start..end).map { totals[LocalDate.fromEpochDays(it)] ?: 0.0 }
        val contributing = values.filter { it >= MINIMUM_TRACKED_KCAL }
        return EnergySummary(values.sum(), contributing.takeIf { it.isNotEmpty() }?.average(), contributing.size)
    }
}
