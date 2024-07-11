package com.xigong.xiaozhuan.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes


@Composable
fun PositiveButton(text: String, fontSize: TextUnit = 14.sp, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(AppShapes.roundButton)
            .background(AppColors.primary)
            .then(modifier)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            color = Color.White,
            letterSpacing = 3.sp,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun NegativeButton(text: String, fontSize: TextUnit = 14.sp, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val hoverSource = remember { MutableInteractionSource() }
    val hovered = hoverSource.collectIsHoveredAsState().value
    val borderColor = if (hovered) AppColors.primary else AppColors.fontGray
    val textColor = if (hovered) AppColors.primary else AppColors.fontBlack
    Row(
        modifier = Modifier
            .hoverable(hoverSource)
            .border(0.5.dp, borderColor, AppShapes.roundButton)
            .then(modifier)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            color = textColor,
            letterSpacing = 3.sp,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}