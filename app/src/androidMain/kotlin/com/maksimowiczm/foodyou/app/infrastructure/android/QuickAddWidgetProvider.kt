package com.maksimowiczm.foodyou.app.infrastructure.android

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class QuickAddWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateQuickAddWidgetsFromCache(context, appWidgetIds)
    }

    companion object {
        const val ACTION_ADD_MEAL = "com.defaultstudent.foodyou.action.ADD_MEAL"
    }
}
