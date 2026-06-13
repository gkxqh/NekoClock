package com.gkxqh.nekoclock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints

@Composable
fun LandscapeBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isPortrait = maxHeight > maxWidth
        
        if (isPortrait) {
            // 如果是竖屏，旋转 90 度，并交换宽高约束
            Box(
                modifier = Modifier
                    .size(maxHeight, maxWidth) // 交换宽高
                    .rotate(90f)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxHeight
                            )
                        )
                        layout(constraints.maxWidth, constraints.maxHeight) {
                            placeable.place(
                                (constraints.maxWidth - placeable.width) / 2,
                                (constraints.maxHeight - placeable.height) / 2
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        } else {
            // 已经是横屏，直接显示
            content()
        }
    }
}
