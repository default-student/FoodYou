package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {

    private var mealEntryWidgetRequest by mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordMealEntryWidgetRequest(intent)

        setContent {
            FoodYouApp(
                mealEntryWidgetRequest = mealEntryWidgetRequest,
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

    private fun recordMealEntryWidgetRequest(intent: Intent?) {
        if (intent?.action == QuickAddWidgetProvider.ACTION_ADD_MEAL) {
            mealEntryWidgetRequest += 1
        }
    }
}
