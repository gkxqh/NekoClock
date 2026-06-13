package com.gkxqh.nekoclock.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import coil.compose.AsyncImage
import com.gkxqh.nekoclock.data.ClockSettings
import com.gkxqh.nekoclock.data.SettingsManager
import com.gkxqh.nekoclock.ui.components.*
import com.gkxqh.nekoclock.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

import androidx.compose.ui.res.stringResource
import com.gkxqh.nekoclock.R
import com.gkxqh.nekoclock.ui.theme.BackgroundDark
import com.gkxqh.nekoclock.ui.theme.DividerGray
import com.gkxqh.nekoclock.ui.theme.PrimaryGreen
import com.gkxqh.nekoclock.ui.theme.TextDim
import com.gkxqh.nekoclock.ui.theme.TextLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: ClockSettings,
    onSettingChange: (Preferences.Key<*>, Any) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var defaultWallpapers by remember { mutableStateOf<List<String>>(emptyList()) }
    var customWallpapers by remember { mutableStateOf<List<File>>(emptyList()) }

    fun refreshWallpapers() {
        scope.launch(Dispatchers.IO) {
            val defaults = context.assets.list("wallpapers")?.toList() ?: emptyList()
            val customDir = File(context.filesDir, "custom_wallpapers").apply { if (!exists()) mkdirs() }
            val customs = customDir.listFiles()?.toList() ?: emptyList()
            withContext(Dispatchers.Main) { defaultWallpapers = defaults; customWallpapers = customs }
        }
    }
    LaunchedEffect(Unit) { refreshWallpapers() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val customDir = File(context.filesDir, "custom_wallpapers").apply { if (!exists()) mkdirs() }
                val fileName = "wallpaper_${System.currentTimeMillis()}.jpg"
                val destFile = File(customDir, fileName)
                context.contentResolver.openInputStream(it)?.use { input -> FileOutputStream(destFile).use { output -> input.copyTo(output) } }
                withContext(Dispatchers.Main) { 
                    onSettingChange(SettingsManager.Keys.WALLPAPER_TYPE, "custom")
                    onSettingChange(SettingsManager.Keys.WALLPAPER_PATH, fileName)
                    refreshWallpapers() 
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.back_with_arrow), color = TextLight, fontSize = 16.sp, modifier = Modifier.clickable { onBack() })
                Spacer(modifier = Modifier.weight(1f))
                Text(text = stringResource(R.string.settings), color = TextLight, fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SectionHeader(stringResource(R.string.section_wallpaper))
                FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WallpaperThumb(content = { Text(stringResource(R.string.wallpaper_none), color = Color.White, fontSize = 12.sp) }, selected = settings.wallpaperType == "none", onClick = { onSettingChange(SettingsManager.Keys.WALLPAPER_TYPE, "none") })
                    defaultWallpapers.forEach { path -> 
                        WallpaperThumb(
                            content = { AsyncImage(model = "file:///android_asset/wallpapers/$path", contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }, 
                            selected = settings.wallpaperType == "default" && settings.wallpaperPath == path, 
                            onClick = { onSettingChange(SettingsManager.Keys.WALLPAPER_TYPE, "default"); onSettingChange(SettingsManager.Keys.WALLPAPER_PATH, path) }
                        ) 
                    }
                    customWallpapers.forEach { file -> 
                        WallpaperThumb(
                            content = { AsyncImage(model = file, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }, 
                            selected = settings.wallpaperType == "custom" && settings.wallpaperPath == file.name, 
                            onDelete = { file.delete(); if (settings.wallpaperPath == file.name) onSettingChange(SettingsManager.Keys.WALLPAPER_TYPE, "none"); refreshWallpapers() }, 
                            onClick = { onSettingChange(SettingsManager.Keys.WALLPAPER_TYPE, "custom"); onSettingChange(SettingsManager.Keys.WALLPAPER_PATH, file.name) }
                        ) 
                    }
                    WallpaperThumb(content = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }, selected = false, onClick = { launcher.launch(arrayOf("image/*")) })
                }

                SectionHeader(stringResource(R.string.section_personalization))
                ColorSettings(currentColor = settings.textColor, onColorChange = { onSettingChange(SettingsManager.Keys.TEXT_COLOR, it.toArgb().toLong()) })
                
                SettingsSwitch(stringResource(R.string.blur_background_title), settings.useBlur, stringResource(R.string.blur_background_desc)) { onSettingChange(SettingsManager.Keys.USE_BLUR, it) }
                SettingsSwitch(stringResource(R.string.glow_text_title), settings.useGlow, stringResource(R.string.glow_text_desc)) { onSettingChange(SettingsManager.Keys.USE_GLOW, it) }

                SectionHeader(stringResource(R.string.section_display))
                SettingsSwitch(stringResource(R.string.show_seconds), settings.showSeconds) { onSettingChange(SettingsManager.Keys.SHOW_SECONDS, it) }
                SettingsSwitch(stringResource(R.string.show_date), settings.showDate) { onSettingChange(SettingsManager.Keys.SHOW_DATE, it) }
                SettingsSwitch(stringResource(R.string.show_battery), settings.showBattery) { onSettingChange(SettingsManager.Keys.SHOW_BATTERY, it) }
                SettingsSwitch(stringResource(R.string.burn_in_protection_title), settings.burnInProtection, stringResource(R.string.burn_in_protection_desc)) { onSettingChange(SettingsManager.Keys.BURN_IN_PROTECTION, it) }
                SettingsSwitch(stringResource(R.string.hide_settings_icon_title), settings.hideSettingsIcon, stringResource(R.string.hide_settings_icon_desc)) { onSettingChange(SettingsManager.Keys.HIDE_SETTINGS_ICON, it) }
                
                SectionHeader(stringResource(R.string.section_adjust))
                SettingsSlider(stringResource(R.string.font_size), settings.fontSize, 40f..180f) { onSettingChange(SettingsManager.Keys.FONT_SIZE, it) }
                SettingsSlider(stringResource(R.string.brightness), settings.brightness, 0.05f..1.0f) { onSettingChange(SettingsManager.Keys.BRIGHTNESS, it) }
                
                Text(stringResource(R.string.font_style), color = TextDim, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                FlowRow(modifier = Modifier.padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Monospace", "Serif", "SansSerif", "Cursive").forEach { font ->
                        ThemeChip(font, settings.fontStyle == font) { onSettingChange(SettingsManager.Keys.FONT_STYLE, font) }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ColorSettings(currentColor: Color, onColorChange: (Color) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    var hexInput by remember { mutableStateOf(String.format("#%06X", (0xFFFFFF and currentColor.toArgb()))) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(stringResource(R.string.text_color), color = TextDim, fontSize = 12.sp)
        FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AppTheme.PresetColors.forEach { color ->
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color).border(width = if (currentColor.toArgb() == color.toArgb()) 2.dp else 0.dp, color = Color.White, shape = CircleShape).clickable { 
                    onColorChange(color)
                    hexInput = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                })
            }
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(DividerGray).clickable { showPicker = !showPicker }, contentAlignment = Alignment.Center) {
                Text("+", color = Color.White, fontSize = 18.sp)
            }
        }

        if (showPicker) {
            FullColorPicker(currentColor = currentColor, onColorChange = { color ->
                onColorChange(color)
                hexInput = String.format("#%06X", (0xFFFFFF and color.toArgb()))
            })
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = hexInput,
            onValueChange = { input ->
                val upper = input.uppercase().take(7)
                hexInput = upper
                if (upper.matches(Regex("^#[0-9A-F]{6}$"))) {
                    try { onColorChange(Color(android.graphics.Color.parseColor(upper))) } catch (e: Exception) {}
                }
            },
            label = { Text(stringResource(R.string.hex_color_label), fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.width(150.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, unfocusedBorderColor = DividerGray)
        )
    }
}

@Composable
fun FullColorPicker(currentColor: Color, onColorChange: (Color) -> Unit) {
    val hsv = remember { FloatArray(3).also { android.graphics.Color.colorToHSV(currentColor.toArgb(), it) } }
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var sat by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)).pointerInput(Unit) {
            detectDragGestures { change, _ ->
                sat = (change.position.x / size.width).coerceIn(0f, 1f)
                value = (1f - change.position.y / size.height).coerceIn(0f, 1f)
                onColorChange(Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, value))))
            }
        }) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))))
                drawRect(Brush.horizontalGradient(listOf(Color.White, Color.Transparent)))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
                drawCircle(Color.White, radius = 6.dp.toPx(), center = Offset(sat * size.width, (1f - value) * size.height), style = Stroke(2.dp.toPx()))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(CircleShape).pointerInput(Unit) {
            detectDragGestures { change, _ ->
                hue = (change.position.x / size.width).coerceIn(0f, 1f) * 360f
                onColorChange(Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, value))))
            }
        }) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val colors = (0..360 step 60).map { Color(android.graphics.Color.HSVToColor(floatArrayOf(it.toFloat(), 1f, 1f))) }
                drawRect(Brush.horizontalGradient(colors))
                drawRect(Color.White, topLeft = Offset((hue / 360f) * size.width - 2.dp.toPx(), 0f), size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height), style = Stroke(2.dp.toPx()))
            }
        }
    }
}
