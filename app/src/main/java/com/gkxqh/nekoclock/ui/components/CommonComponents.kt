package com.gkxqh.nekoclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gkxqh.nekoclock.R
import com.gkxqh.nekoclock.ui.theme.PrimaryGreen
import com.gkxqh.nekoclock.ui.theme.SurfaceDark
import com.gkxqh.nekoclock.ui.theme.TextDim
import com.gkxqh.nekoclock.ui.theme.TextLight

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = PrimaryGreen,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun SettingsSwitch(
    title: String,
    checked: Boolean,
    description: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextLight, fontSize = 16.sp)
            if (description != null) {
                Text(text = description, color = TextDim, fontSize = 11.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGreen
            )
        )
    }
}

@Composable
fun SettingsSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = title, color = TextLight, fontSize = 14.sp)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = PrimaryGreen
            )
        )
    }
}

@Composable
fun WallpaperThumb(
    content: @Composable () -> Unit,
    selected: Boolean,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(2.dp, if (selected) PrimaryGreen else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
        if (onDelete != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(18.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.delete), tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun ThemeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) PrimaryGreen else SurfaceDark, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = if (selected) Color.White else TextDim, fontSize = 12.sp)
    }
}
