package com.maksimowiczm.foodyou.app.domain.mealreminder

import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

internal data class MealReminderTarget(
    val meal: Meal,
    val date: LocalDate,
)

internal object MealReminderTargetSelector {
    fun orderedMeals(meals: List<Meal>): List<Meal> =
        meals.sortedWith(compareBy<Meal> { it.from }.thenBy { it.rank }.thenBy { it.id })

    fun previousMeal(
        meals: List<Meal>,
        triggerMealId: Long,
        triggerDate: LocalDate,
    ): MealReminderTarget? {
        val ordered = orderedMeals(meals)
        val triggerIndex = ordered.indexOfFirst { it.id == triggerMealId }
        if (triggerIndex < 0 || ordered.isEmpty()) return null

        return MealReminderTarget(
            meal = ordered[(triggerIndex - 1 + ordered.size) % ordered.size],
            date = if (triggerIndex == 0) triggerDate.plus(-1, DateTimeUnit.DAY) else triggerDate,
        )
    }
}
