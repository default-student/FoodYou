package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {

    private var quickAddWidgetRequest by mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordQuickAddWidgetRequest(intent)

        setContent {
            FoodYouApp(
                quickAddWidgetRequest = quickAddWidgetRequest,
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
        recordQuickAddWidgetRequest(intent)
    }

    private fun recordQuickAddWidgetRequest(intent: Intent?) {
        if (intent?.action == QuickAddWidgetProvider.ACTION_QUICK_ADD) {
            quickAddWidgetRequest += 1
        }
    }
}
