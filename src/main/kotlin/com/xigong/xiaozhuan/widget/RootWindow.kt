package com.xigong.xiaozhuan.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xigong.xiaozhuan.BuildConfig
import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.page.splash.SplashPage
import com.xigong.xiaozhuan.page.version.NewVersionDialog
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window

@Composable
fun RootWindow(
    miniClick: () -> Unit,
    closeClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clip(RoundedCornerShape(AppShapes.largeCorner))
            .background(AppColors.pageBackground)
            .border(1.dp, AppColors.divider, RoundedCornerShape(AppShapes.largeCorner))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(miniClick, closeClick)
            content()
        }
        SplashPage()
        NewVersionDialog()
        Toast.UI()
    }

}

@Composable
private fun TopBar(miniClick: () -> Unit, closeClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(50.dp)
            .background(AppColors.auxiliary)
            .pointerInput(Unit) {
                var last = Point()
                detectDragGestures(onDragStart = {
                    last = getMousePosition()
                }, onDrag = { _, _ ->
                    // 直接使用Window 的Api实现偏移，性能更好，不漂移
                    val point = getMousePosition()
                    val location = getWindow().location
                    getWindow().setLocation(location.x + (point.x - last.x), location.y + (point.y - last.y))
                    last = point

                })
            }
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painterResource(BuildConfig.ICON),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
                .clip(AppShapes.roundButton)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(BuildConfig.appName, fontSize = 16.sp, color = AppColors.fontBlack)
        Spacer(modifier = Modifier.weight(1f))
        ImageButton("window_mini.png", 22.dp, miniClick)
        ImageButton("window_close.png", 16.dp, closeClick)
    }
}

private fun getWindow(): Window {
    return Window.getWindows().first()
}

/**
 * 获取鼠标在屏幕中的位置
 */
private fun getMousePosition(): Point {
    return MouseInfo.getPointerInfo().location
}

@Preview
@Composable
private fun TopBarPreview() {
    TopBar({}, {})
}

@Composable
private fun ImageButton(image: String, size: Dp, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier.size(size)
        )
    }
}