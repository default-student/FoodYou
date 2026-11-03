package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.Application
import android.content.Intent
import android.os.Build
import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.app.di.initKoin
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.event.EventBus
import com.maksimowiczm.foodyou.settings.domain.event.AppLaunchEvent
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.ObserveDailyEnergyUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class FoodYouApplication : Application() {

    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("FoodYouApplication"))
    }

    private val dateProvider: DateProvider by inject()
    private val observeDailyEnergy: ObserveDailyEnergyUseCase by inject()
    private val mealRepository: MealRepository by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin(coroutineScope) { androidContext(this@FoodYouApplication) }
        publishLaunchEvent()
        observeWidgetEnergy()
        observeMealReminders()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            handleUncaughtException(e)
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun observeWidgetEnergy() {
        coroutineScope.launch {
            dateProvider.observeDate()
                .flatMapLatest(observeDailyEnergy::invoke)
                .collectLatest { updateQuickAddWidgets(this@FoodYouApplication, it) }
        }
    }

    private fun publishLaunchEvent() {
        val dateProvider: DateProvider by inject()
        val eventBus: EventBus by inject()

        val event = AppLaunchEvent(timestamp = dateProvider.nowInstant())
        eventBus.publish(event)
    }

    private fun observeMealReminders() {
        coroutineScope.launch {
            mealRepository.observeMeals().collectLatest { meals ->
                MealReminderScheduler.refresh(this@FoodYouApplication, meals)
            }
        }
    }

    private fun handleUncaughtException(e: Throwable) {
        val intent = Intent(this, CrashReportActivity::class.java)

        val report = buildString {
            appendLine("Version: ${BuildConfig.VERSION_NAME}")
            appendLine("Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine(e.stackTraceToString())
        }

        intent.putExtra("report", report)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
    }
}
