package com.chartracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.chartracker.ui.theme.CharTrackerTheme
import com.google.android.gms.ads.MobileAds
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        //If need to get consent from users for legal reasons, need to do before this apparently
        MobileAds.initialize(this) {}

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CharTrackerTheme {
                CharTrackerApp()
            }
        }
    }
}