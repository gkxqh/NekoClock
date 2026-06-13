package com.gkxqh.nekoclock

import android.service.dreams.DreamService
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.gkxqh.nekoclock.data.SettingsManager
import com.gkxqh.nekoclock.ui.ClockApp
import com.gkxqh.nekoclock.ui.theme.NekoClockTheme

class NekoDreamService : DreamService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        isInteractive = true
        isFullscreen = true

        settingsManager = SettingsManager(this)

        val composeView = ComposeView(this).apply {
            // Set required owners for Compose to work in a Service
            setViewTreeLifecycleOwner(this@NekoDreamService)
            setViewTreeViewModelStoreOwner(this@NekoDreamService)
            setViewTreeSavedStateRegistryOwner(this@NekoDreamService)
            
            setContent {
                NekoClockTheme {
                    ClockApp(settingsManager)
                }
            }
        }
        setContentView(composeView)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onDetachedFromWindow() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onDetachedFromWindow()
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        super.onDestroy()
    }
}
