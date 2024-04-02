package com.chartracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.chartracker.ui.theme.CharTrackerTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CharTrackerTheme {
                CharTrackerApp()
            }
        }
    }
}