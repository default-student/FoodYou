package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository

class MealReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val meals = GlobalContext.get().get<MealRepository>().observeMeals().first()
                MealReminderScheduler.refresh(context, meals, force = true)
            } finally {
                result.finish()
            }
        }
    }
}
