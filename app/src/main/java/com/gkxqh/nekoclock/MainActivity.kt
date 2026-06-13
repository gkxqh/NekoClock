package com.gkxqh.nekoclock

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gkxqh.nekoclock.data.SettingsManager
import com.gkxqh.nekoclock.ui.ClockApp
import com.gkxqh.nekoclock.ui.theme.NekoClockTheme

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager

    private fun enableImmersiveMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupWakeLock() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        enableImmersiveMode()
        setupWakeLock()
        setContent {
            NekoClockTheme {
                ClockApp(settingsManager)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableImmersiveMode()
    }
}
