package com.gkxqh.nekoclock.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.gkxqh.nekoclock.data.SettingsManager
import com.gkxqh.nekoclock.ui.components.LandscapeBox
import com.gkxqh.nekoclock.ui.screens.ClockScreen
import com.gkxqh.nekoclock.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun ClockApp(settingsManager: SettingsManager) {
    val scope = rememberCoroutineScope()
    val settings by settingsManager.settingsFlow.collectAsState(initial = null)
    var showSettings by remember { mutableStateOf(false) }
    var predictiveBackProgress by remember { mutableFloatStateOf(0f) }

    val activity = LocalContext.current as? ComponentActivity

    if (settings == null) return
    val currentSettings = settings!!

    if (showSettings) {
        PredictiveBackHandler { backEvent ->
            try {
                backEvent.collect { event ->
                    predictiveBackProgress = event.progress
                }
                showSettings = false
            } catch (e: Exception) {
                Log.e("ClockApp", "Error during predictive back", e)
            } finally {
                predictiveBackProgress = 0f
            }
        }
    }

    LaunchedEffect(currentSettings.brightness) {
        activity?.window?.attributes?.let { params ->
            params.screenBrightness = currentSettings.brightness
            activity.window.attributes = params
        }
    }

    AnimatedContent(
        targetState = showSettings,
        transitionSpec = {
            if (targetState) {
                (slideInHorizontally { it } + fadeIn()).togetherWith(
                    slideOutHorizontally { -it / 3 } + fadeOut()
                )
            } else {
                (slideInHorizontally { -it / 3 } + fadeIn()).togetherWith(
                    slideOutHorizontally { it } + fadeOut()
                )
            }
        },
        label = "SettingsTransition",
        modifier = Modifier.fillMaxSize()
            .graphicsLayer {
                if (showSettings && predictiveBackProgress > 0) {
                    val scale = 1f - (predictiveBackProgress * 0.05f)
                    scaleX = scale
                    scaleY = scale
                    translationX = predictiveBackProgress * 50f
                }
            }
    ) { targetShowSettings ->
        LandscapeBox {
            if (targetShowSettings) {
                SettingsScreen(
                    settings = currentSettings,
                    onSettingChange = { key, value ->
                        scope.launch {
                            @Suppress("UNCHECKED_CAST")
                            settingsManager.updateSetting(key as androidx.datastore.preferences.core.Preferences.Key<Any>, value)
                        }
                    },
                    onBack = { showSettings = false }
                )
            } else {
                ClockScreen(
                    settings = currentSettings,
                    onSettingsClick = { showSettings = true }
                )
            }
        }
    }
}
