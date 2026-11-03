package com.maksimowiczm.foodyou.app.ui.statistics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.datetime.LocalDate

class StatisticsCalculatorTest {
    private val today = LocalDate(2026, 11, 3) // Tuesday

    @Test fun windowEndsTodayAndExcludesValuesBelow500() {
        val totals = mapOf(
            LocalDate(2026, 11, 1) to 400.0,
            LocalDate(2026, 11, 2) to 600.0,
            today to 800.0,
            LocalDate(2026, 10, 31) to 9000.0,
        )
        val summary = StatisticsCalculator.window(today, 3, totals)
        assertEquals(1800.0, summary.total)
        assertEquals(700.0, summary.average)
        assertEquals(2, summary.contributingDays)
        assertEquals(3, StatisticsCalculator.window(today, 7, totals).contributingDays)
        assertEquals(3, StatisticsCalculator.window(today, 14, totals).contributingDays)
    }

    @Test fun missingAndSubthresholdDaysHaveNoAverage() {
        val summary = StatisticsCalculator.window(today, 3, mapOf(today to 499.0))
        assertEquals(499.0, summary.total)
        assertNull(summary.average)
        assertEquals(0, summary.contributingDays)
    }

    @Test fun exactly500KcalContributesToAverage() {
        val summary = StatisticsCalculator.window(today, 3, mapOf(today to 500.0))
        assertEquals(500.0, summary.average)
        assertEquals(1, summary.contributingDays)
    }

    @Test fun calendarWeeksUseMondayBoundaries() {
        val totals = mapOf(
            LocalDate(2026, 10, 26) to 700.0,
            LocalDate(2026, 11, 2) to 1000.0,
            LocalDate(2026, 11, 3) to 500.0,
            LocalDate(2026, 11, 4) to 9000.0,
        )
        val previous = StatisticsCalculator.calendarWeek(today, true, totals)
        val current = StatisticsCalculator.calendarWeek(today, false, totals)
        assertEquals(700.0, previous.total)
        assertEquals(1500.0, current.total)
        assertEquals(2, current.contributingDays)
    }
}
