package com.maksimowiczm.foodyou.app.infrastructure.android

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.app.domain.mealreminder.MealReminderTargetSelector
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.ObserveDailyEnergyUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt
import org.koin.core.context.GlobalContext

class MealReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != MealReminderScheduler.ACTION_DELIVER) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val meals = GlobalContext.get().get<MealRepository>()
                    .observeMeals()
                    .first()
                val triggerMealId = intent.getLongExtra(MealReminderScheduler.EXTRA_TRIGGER_MEAL_ID, -1L)
                val triggerDateEpochDay = intent.getLongExtra(MealReminderScheduler.EXTRA_TRIGGER_DATE, Long.MIN_VALUE)
                MealReminderScheduler.scheduleNext(context, meals, triggerMealId)
                if (triggerDateEpochDay != Long.MIN_VALUE) {
                    val triggerDate = LocalDate.fromEpochDays(triggerDateEpochDay.toInt())
                    val target = MealReminderTargetSelector.previousMeal(meals, triggerMealId, triggerDate)
                    if (target != null) {
                        val today = GlobalContext.get().get<DateProvider>().now().date
                        val energy = GlobalContext.get().get<ObserveDailyEnergyUseCase>().invoke(today).first()
                        showNotification(
                            context,
                            target.meal.id,
                            target.meal.name,
                            target.date.toEpochDays().toLong(),
                            energy.roundToInt(),
                        )
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, mealId: Long, mealName: String, dateEpochDay: Long, energyKcal: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val channelId = context.getString(R.string.meal_reminder_channel_id)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    context.getString(R.string.meal_reminder_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            action = MealReminderScheduler.ACTION_OPEN_REMINDER
            data = android.net.Uri.parse("foodyou://reminder/open/$mealId/$dateEpochDay")
            putExtra(MealReminderScheduler.EXTRA_PREVIOUS_MEAL_ID, mealId)
            putExtra(MealReminderScheduler.EXTRA_PREVIOUS_DATE, dateEpochDay)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val tap = PendingIntent.getActivity(
            context,
            "${mealId}:$dateEpochDay".hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_meal_reminder)
            .setContentTitle(context.getString(R.string.meal_reminder_title, mealName))
            .setContentText(context.getString(R.string.meal_reminder_body, energyKcal))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.meal_reminder_body, energyKcal)))
            .setContentIntent(tap)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify("${mealId}:$dateEpochDay".hashCode(), notification)
    }
}
