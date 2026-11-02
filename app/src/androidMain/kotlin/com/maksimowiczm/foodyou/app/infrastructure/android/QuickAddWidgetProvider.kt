package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.maksimowiczm.foodyou.R

class QuickAddWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.quick_add_widget)
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    action = ACTION_QUICK_ADD
                    data = Uri.parse("foodyou://widget/quick-add/$appWidgetId")
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            views.setOnClickPendingIntent(R.id.quick_add_widget, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        const val ACTION_QUICK_ADD = "com.defaultstudent.foodyou.action.QUICK_ADD"
    }
}
