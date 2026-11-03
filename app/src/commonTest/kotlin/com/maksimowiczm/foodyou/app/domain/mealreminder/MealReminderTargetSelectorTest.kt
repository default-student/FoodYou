package com.maksimowiczm.foodyou.app.domain.mealreminder

import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class MealReminderTargetSelectorTest {
    private val day = LocalDate(2026, 11, 3)

    @Test fun firstMealTargetsLastMealOnPreviousDate() {
        val meals = listOf(meal(2, "Lunch", "12:00", 2), meal(1, "Breakfast", "08:00", 1))

        val target = MealReminderTargetSelector.previousMeal(meals, triggerMealId = 1, triggerDate = day)

        assertEquals(meals.first(), target?.meal)
        assertEquals(LocalDate(2026, 11, 2), target?.date)
    }

    @Test fun sameStartTimesHaveStableRankAndIdOrder() {
        val lowerId = meal(2, "Lower ID", "08:00", 1)
        val higherId = meal(3, "Higher ID", "08:00", 1)
        val later = meal(4, "Lunch", "12:00", 2)
        val meals = listOf(later, higherId, lowerId)

        val target = MealReminderTargetSelector.previousMeal(meals, triggerMealId = 4, triggerDate = day)

        assertEquals(higherId, target?.meal)
        assertEquals(day, target?.date)
    }

    @Test fun missingTriggerMealHasNoTarget() {
        assertNull(MealReminderTargetSelector.previousMeal(emptyList(), 99, day))
    }

    private fun meal(id: Long, name: String, from: String, rank: Int) =
        Meal(id, name, LocalTime.parse(from), LocalTime(23, 59), rank)
}
