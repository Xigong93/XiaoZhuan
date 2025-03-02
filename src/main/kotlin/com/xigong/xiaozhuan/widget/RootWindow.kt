package com.xigong.xiaozhuan.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.xigong.xiaozhuan.BuildConfig
import com.xigong.xiaozhuan.page.splash.SplashPage
import com.xigong.xiaozhuan.page.version.NewVersionDialog
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes

@Composable
fun FrameWindowScope.RootWindow(
    state: WindowState,
    closeClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val roundShape = RoundedCornerShape(8.dp)
    Surface(
        shape = roundShape,
        modifier = Modifier
            .clip(roundShape)
            .padding(4.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
                .clip(roundShape)
                .background(AppColors.pageBackground)
                .border(0.5.dp, Color(0xffdcdcdc), roundShape)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar(state, closeClick)
                content()
            }
            SplashPage()
            NewVersionDialog()
            Toast.UI()
        }
    }

}

@Composable
private fun FrameWindowScope.TopBar(state: WindowState, closeClick: () -> Unit) {
    WindowDraggableArea {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
                .background(AppColors.auxiliary)
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Image(
                painterResource(BuildConfig.ICON),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
                    .clip(AppShapes.roundButton)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(BuildConfig.appName, fontSize = 14.sp, color = AppColors.fontBlack)
            Spacer(modifier = Modifier.weight(1f))
            ImageButton("window_mini.png", 20.dp) {
                window.isMinimized = true
            }
            ImageButton("window_max.png", 20.dp) {
                if (state.placement == WindowPlacement.Floating) {
                    state.placement = WindowPlacement.Maximized
                } else {
                    state.placement = WindowPlacement.Floating
                }
            }
            ImageButton("window_close.png", 14.dp, closeClick)
        }
    }
}

@Composable
private fun ImageButton(image: String, size: Dp, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .width(50.dp)
            .clip(RoundedCornerShape(6.dp))
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