package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.maksimowiczm.foodyou.app.domain.mealreminder.MealReminderTargetSelector
import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal object MealReminderScheduler {
    private const val PREFS = "meal_reminders"
    private const val KEY_SCHEDULED_MEALS = "scheduled_meals"
    private const val KEY_FINGERPRINT = "schedule_fingerprint"
    const val ACTION_DELIVER = "com.defaultstudent.foodyou.action.MEAL_REMINDER"
    const val ACTION_OPEN_REMINDER = "com.defaultstudent.foodyou.action.OPEN_MEAL_REMINDER"
    const val EXTRA_TRIGGER_MEAL_ID = "trigger_meal_id"
    const val EXTRA_TRIGGER_DATE = "trigger_date_epoch_day"
    const val EXTRA_PREVIOUS_MEAL_ID = "previous_meal_id"
    const val EXTRA_PREVIOUS_DATE = "previous_date_epoch_day"

    fun refresh(context: Context, meals: List<Meal>, force: Boolean = false) {
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val fingerprint = fingerprint(meals)
        if (!force && preferences.getString(KEY_FINGERPRINT, null) == fingerprint) return

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val oldIds = preferences.getStringSet(KEY_SCHEDULED_MEALS, emptySet()).orEmpty()
        oldIds.forEach { id ->
            pendingIntent(context, id.toLong(), null)?.let(alarmManager::cancel)
        }

        val ordered = MealReminderTargetSelector.orderedMeals(meals)
        if (ordered.isEmpty()) {
            preferences.edit()
                .remove(KEY_SCHEDULED_MEALS)
                .putString(KEY_FINGERPRINT, fingerprint)
                .apply()
            return
        }

        val now = Instant.fromEpochMilliseconds(System.currentTimeMillis())
        val zone = TimeZone.currentSystemDefault()
        val nextIds = mutableSetOf<String>()
        ordered.forEach { triggerMeal ->
            scheduleMeal(
                context,
                alarmManager,
                triggerMeal,
                now,
                zone,
            )
            nextIds += triggerMeal.id.toString()
        }
        preferences.edit()
            .putStringSet(KEY_SCHEDULED_MEALS, nextIds)
            .putString(KEY_FINGERPRINT, fingerprint)
            .apply()
    }

    fun scheduleNext(context: Context, meals: List<Meal>, triggerMealId: Long) {
        val ordered = MealReminderTargetSelector.orderedMeals(meals)
        val index = ordered.indexOfFirst { it.id == triggerMealId }
        if (index < 0) return
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        pendingIntent(context, triggerMealId, null)?.let(alarmManager::cancel)
        scheduleMeal(
            context,
            alarmManager,
            ordered[index],
            Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            TimeZone.currentSystemDefault(),
        )
    }

    private fun scheduleMeal(
        context: Context,
        alarmManager: AlarmManager,
        triggerMeal: Meal,
        now: Instant,
        zone: TimeZone,
    ) {
        val today = now.toLocalDateTime(zone).date
        val candidateDates = listOf(today, today.plus(1, DateTimeUnit.DAY))
        val candidate = candidateDates.map { date -> date to LocalDateTime(date, triggerMeal.from).toInstant(zone) }
            .firstOrNull { (_, instant) -> instant > now }
            ?: return
        val intent = pendingIntent(
            context,
            triggerMeal.id,
            Intent(context, MealReminderReceiver::class.java).apply {
                action = ACTION_DELIVER
                data = Uri.parse("foodyou://reminder/${triggerMeal.id}")
                putExtra(EXTRA_TRIGGER_MEAL_ID, triggerMeal.id)
                putExtra(EXTRA_TRIGGER_DATE, candidate.first.toEpochDays().toLong())
            },
        ) ?: return
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, candidate.second.toEpochMilliseconds(), intent)
    }

    private fun fingerprint(meals: List<Meal>): String = buildString {
        append(TimeZone.currentSystemDefault().id)
        MealReminderTargetSelector.orderedMeals(meals).forEach { meal ->
            append('|').append(meal.id).append(':').append(meal.from).append(':').append(meal.rank)
        }
    }

    private fun pendingIntent(context: Context, mealId: Long, intent: Intent?): PendingIntent? {
        val base = intent ?: Intent(context, MealReminderReceiver::class.java).apply {
            action = ACTION_DELIVER
            data = Uri.parse("foodyou://reminder/$mealId")
        }
        return PendingIntent.getBroadcast(
            context,
            mealId.hashCode(),
            base,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
