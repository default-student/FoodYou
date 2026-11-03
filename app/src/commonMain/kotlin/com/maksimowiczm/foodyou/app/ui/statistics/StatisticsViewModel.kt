package com.maksimowiczm.foodyou.app.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.ObserveDailyEnergyUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal data class StatisticsState(val today: LocalDate, val totals: Map<LocalDate, Double>)

internal class StatisticsViewModel(
    dateProvider: DateProvider,
    observeDailyEnergy: ObserveDailyEnergyUseCase,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val state = dateProvider.observeDate().flatMapLatest { today ->
        val dates = (today.toEpochDays() - 13..today.toEpochDays()).map(LocalDate::fromEpochDays)
        combine(dates.map { date -> observeDailyEnergy(date).map { date to it } }) { pairs ->
            StatisticsState(today, pairs.toMap())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
