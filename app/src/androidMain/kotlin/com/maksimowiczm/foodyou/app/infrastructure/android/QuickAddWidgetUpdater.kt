package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.maksimowiczm.foodyou.R
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.math.roundToInt

internal fun updateQuickAddWidgets(context: Context, energyKcal: Double) {
    val manager = AppWidgetManager.getInstance(context)
    val provider = ComponentName(context, QuickAddWidgetProvider::class.java)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays().toInt()
    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        .edit()
        .putInt(KEY_ENERGY_KCAL, energyKcal.roundToInt())
        .putInt(KEY_ENERGY_DATE, today)
        .apply()
    val widgetIds = manager.getAppWidgetIds(provider)
    widgetIds.forEach { widgetId ->
        manager.updateAppWidget(widgetId, renderWidget(context, widgetId, energyKcal.roundToInt()))
    }
}

internal fun updateQuickAddWidgetsFromCache(context: Context, widgetIds: IntArray) {
    val manager = AppWidgetManager.getInstance(context)
    val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays().toInt()
    val kcal = if (preferences.getInt(KEY_ENERGY_DATE, Int.MIN_VALUE) == today) {
        preferences.getInt(KEY_ENERGY_KCAL, 0)
    } else {
        0
    }
    widgetIds.forEach { widgetId ->
        manager.updateAppWidget(widgetId, renderWidget(context, widgetId, kcal))
    }
}

private fun renderWidget(context: Context, widgetId: Int, energyKcal: Int): RemoteViews {
    val clickIntent = Intent(context, MainActivity::class.java).apply {
        action = QuickAddWidgetProvider.ACTION_ADD_MEAL
        data = Uri.parse("foodyou://widget/add-meal/$widgetId")
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        widgetId,
        clickIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    return RemoteViews(context.packageName, R.layout.quick_add_widget).apply {
        setTextViewText(R.id.quick_add_widget_energy, context.getString(R.string.quick_add_widget_energy, energyKcal))
        setOnClickPendingIntent(R.id.quick_add_widget, pendingIntent)
    }
}

private const val PREFERENCES = "quick_add_widget"
private const val KEY_ENERGY_KCAL = "energy_kcal"
private const val KEY_ENERGY_DATE = "energy_date"
