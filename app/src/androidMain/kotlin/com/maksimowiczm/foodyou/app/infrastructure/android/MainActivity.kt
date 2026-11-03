package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Intent
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {
    private val reminderPreferences by lazy {
        getSharedPreferences("meal_reminders", MODE_PRIVATE)
    }

    private var mealEntryWidgetRequest by mutableLongStateOf(0L)
    private var mealEntryDateEpochDay: Long? by mutableStateOf(null)
    private var mealEntryMealId: Long? by mutableStateOf(null)
    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordMealEntryWidgetRequest(intent)

        setContent {
            FoodYouApp(
                mealEntryWidgetRequest = mealEntryWidgetRequest,
                mealEntryDateEpochDay = mealEntryDateEpochDay,
                mealEntryMealId = mealEntryMealId,
                onRequestNotificationPermission = ::requestNotificationPermission,
                onDatabaseBackup = {
                    val intent =
                        Intent(this, DeveloperActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                    startActivity(intent)
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recordMealEntryWidgetRequest(intent)
    }

    override fun onPostResume() {
        super.onPostResume()
        if (Build.VERSION.SDK_INT >= 33 &&
            intent?.action == Intent.ACTION_MAIN &&
            intent?.categories?.contains(Intent.CATEGORY_LAUNCHER) == true &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
            !reminderPreferences.getBoolean(KEY_PERMISSION_ASKED, false)
        ) {
            reminderPreferences.edit().putBoolean(KEY_PERMISSION_ASKED, true).apply()
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun recordMealEntryWidgetRequest(intent: Intent?) {
        when (intent?.action) {
            QuickAddWidgetProvider.ACTION_ADD_MEAL -> {
                mealEntryDateEpochDay = null
                mealEntryMealId = null
                mealEntryWidgetRequest += 1
            }
            MealReminderScheduler.ACTION_OPEN_REMINDER -> {
                val date = intent.getLongExtra(MealReminderScheduler.EXTRA_PREVIOUS_DATE, Long.MIN_VALUE)
                val meal = intent.getLongExtra(MealReminderScheduler.EXTRA_PREVIOUS_MEAL_ID, -1L)
                if (date != Long.MIN_VALUE && meal >= 0) {
                    mealEntryDateEpochDay = date
                    mealEntryMealId = meal
                    mealEntryWidgetRequest += 1
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        when {
            Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ->
                if (!reminderPreferences.getBoolean(KEY_PERMISSION_ASKED, false)) {
                    reminderPreferences.edit().putBoolean(KEY_PERMISSION_ASKED, true).apply()
                    notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    openNotificationSettings()
                }
            else -> openNotificationSettings()
        }
    }

    private fun openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= 26) {
            startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            )
        } else {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(android.net.Uri.parse("package:$packageName")))
        }
    }

    private companion object {
        const val KEY_PERMISSION_ASKED = "notification_permission_asked"
    }
}
