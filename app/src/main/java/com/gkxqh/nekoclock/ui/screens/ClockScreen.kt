package com.gkxqh.nekoclock.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gkxqh.nekoclock.data.ClockSettings
import com.gkxqh.nekoclock.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ClockScreen(
    settings: ClockSettings,
    onSettingsClick: () -> Unit
) {
    var hour by remember { mutableStateOf("00") }
    var minute by remember { mutableStateOf("00") }
    var second by remember { mutableStateOf("00") }
    var currentDate by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(0) }
    var isCharging by remember { mutableStateOf(false) }
    var iconPeekVisible by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val fontFamily = AppTheme.getFontFamily(settings.fontStyle)

    LaunchedEffect(iconPeekVisible) {
        if (iconPeekVisible) { delay(5000); iconPeekVisible = false }
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    batteryLevel = (level * 100 / scale.toFloat()).toInt()
                    val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { context.unregisterReceiver(receiver) }
    }

    LaunchedEffect(settings.showSeconds, settings.showDate, settings.burnInProtection) {
        var lastShiftTime = System.currentTimeMillis()
        while (true) {
            val now = Calendar.getInstance()
            hour = SimpleDateFormat("HH", Locale.getDefault()).format(now.time)
            minute = SimpleDateFormat("mm", Locale.getDefault()).format(now.time)
            second = SimpleDateFormat("ss", Locale.getDefault()).format(now.time)

            if (settings.showDate) {
                val dateStr = SimpleDateFormat(settings.dateFormat, Locale.getDefault()).format(now.time)
                val weekdayStr = SimpleDateFormat("EEEE", if (settings.useChineseWeekday) Locale.CHINESE else Locale.getDefault()).format(now.time)
                currentDate = "$dateStr $weekdayStr"
            }

            if (settings.burnInProtection && System.currentTimeMillis() - lastShiftTime > 60000) {
                launch { offsetX.animateTo((-20..20).random().toFloat(), tween(2000)) }
                launch { offsetY.animateTo((-10..10).random().toFloat(), tween(2000)) }
                lastShiftTime = System.currentTimeMillis()
            } else if (!settings.burnInProtection) {
                offsetX.snapTo(0f); offsetY.snapTo(0f)
            }
            delay(if (settings.showSeconds) 500 else 1000)
        }
    }

    val wallpaperModel = remember(settings.wallpaperType, settings.wallpaperPath) {
        when (settings.wallpaperType) {
            "default" -> "file:///android_asset/wallpapers/${settings.wallpaperPath}"
            "custom" -> File(context.filesDir, "custom_wallpapers/${settings.wallpaperPath}")
            else -> null
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (wallpaperModel != null) {
            AsyncImage(
                model = wallpaperModel, 
                contentDescription = null, 
                modifier = Modifier.fillMaxSize().then(if (settings.useBlur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Modifier.blur(20.dp) else Modifier),
                contentScale = ContentScale.Crop, 
                alpha = 0.5f
            )
        }
        Box(modifier = Modifier.fillMaxSize().background(if (settings.wallpaperType != "none") Color.Black.copy(alpha = 0.3f) else Color(0xFF0B0B0B)))

        if (settings.showBattery) {
            Text(
                text = if (isCharging) "⚡$batteryLevel%" else "$batteryLevel%",
                color = if (batteryLevel < 20 && !isCharging) Color.Red else Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val clockTextStyle = TextStyle(
                color = settings.textColor,
                fontSize = settings.fontSize.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                letterSpacing = 2.sp,
                shadow = if (settings.useGlow) Shadow(color = settings.textColor, blurRadius = 40f) else null
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                // Hours
                DigitGroup(text = hour, style = clockTextStyle, isScrolling = false)
                
                // Static Colon
                Text(text = ":", style = clockTextStyle.copy(shadow = if (settings.useGlow) Shadow(color = settings.textColor, blurRadius = 30f) else null), modifier = Modifier.padding(horizontal = 2.dp))
                
                // Minutes
                DigitGroup(text = minute, style = clockTextStyle, isScrolling = false)
                
                if (settings.showSeconds) {
                    // Static Colon
                    Text(text = ":", style = clockTextStyle.copy(shadow = if (settings.useGlow) Shadow(color = settings.textColor, blurRadius = 30f) else null), modifier = Modifier.padding(horizontal = 2.dp))
                    
                    // Seconds (Each digit animates independently)
                    DigitGroup(text = second, style = clockTextStyle, isScrolling = true)
                }
            }
            
            if (settings.showDate) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentDate, 
                    style = TextStyle(
                        color = settings.textColor.copy(alpha = 0.8f),
                        fontSize = (settings.fontSize * 0.35f).sp,
                        shadow = if (settings.useGlow) Shadow(color = settings.textColor, blurRadius = 20f) else null
                    )
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.5f).align(Alignment.TopEnd)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                    if (!settings.hideSettingsIcon) onSettingsClick()
                    else { if (iconPeekVisible) { onSettingsClick(); iconPeekVisible = false } else iconPeekVisible = true }
                }
        ) {
            val iconAlpha = if (settings.hideSettingsIcon) (if (iconPeekVisible) 0.4f else 0.0f) else 0.4f
            Text(text = "⚙", color = Color.White.copy(alpha = iconAlpha), fontSize = 28.sp, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))
        }
    }
}

@Composable
fun DigitGroup(text: String, style: TextStyle, isScrolling: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.forEach { char ->
            Digit(digit = char, style = style, isScrolling = isScrolling)
        }
    }
}

@Composable
fun Digit(digit: Char, style: TextStyle, isScrolling: Boolean) {
    Box(modifier = Modifier.width(IntrinsicSize.Min), contentAlignment = Alignment.Center) {
        // Transparent placeholder to maintain width of one digit '0'
        Text(text = "0", style = style.copy(color = Color.Transparent, shadow = null))
        
        AnimatedContent(
            targetState = digit,
            transitionSpec = {
                if (isScrolling) {
                    (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                } else {
                    fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(600))
                }
            },
            label = "IndividualDigitAnim"
        ) { targetDigit ->
            Text(text = targetDigit.toString(), style = style, textAlign = TextAlign.Center)
        }
    }
}
