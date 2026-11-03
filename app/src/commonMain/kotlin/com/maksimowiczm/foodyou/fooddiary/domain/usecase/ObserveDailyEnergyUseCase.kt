package com.maksimowiczm.foodyou.fooddiary.domain.usecase

import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate

/** Observes the sum of recorded energy for a date, in the diary's canonical kcal unit. */
class ObserveDailyEnergyUseCase(
    private val mealRepository: MealRepository,
    private val foodEntryRepository: FoodDiaryEntryRepository,
    private val manualEntryRepository: ManualDiaryEntryRepository,
) {
    operator fun invoke(date: LocalDate): Flow<Double> =
        mealRepository.observeMeals().flatMapLatest { meals ->
            if (meals.isEmpty()) return@flatMapLatest flowOf(0.0)

            val mealTotals = meals.map { meal ->
                combine(
                    foodEntryRepository.observeAll(meal.id, date),
                    manualEntryRepository.observeAll(meal.id, date),
                ) { foodEntries, manualEntries ->
                    foodEntries.sumOf { it.nutritionFacts.energy.value ?: 0.0 } +
                        manualEntries.sumOf { it.nutritionFacts.energy.value ?: 0.0 }
                }
            }

            combine(mealTotals) { totals -> totals.sum() }
        }
}
