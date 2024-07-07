package apk.dispatcher.widget

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
import apk.dispatcher.BuildConfig
import apk.dispatcher.log.AppLogger
import apk.dispatcher.page.splash.SplashPage
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes

@Composable
fun RootWindow(
    onDrag: (offset: Offset) -> Unit,
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
            TopBar(onDrag, miniClick, closeClick)
            content()
        }
        SplashPage()
        Toast.UI()
    }

}

@Composable
private fun TopBar(onDrag: (offset: Offset) -> Unit, miniClick: () -> Unit, closeClick: () -> Unit) {

    val dragHolder = rememberUpdatedState(onDrag)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(50.dp)
            .background(AppColors.auxiliary)
            .pointerInput(Unit) {
                detectDragGestures { change, offset ->
                    change.consume()
                    dragHolder.value(offset)
                    AppLogger.info("拖动", "$offset")
                }
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

@Preview
@Composable
private fun TopBarPreview() {
    TopBar({ }, {}, {})
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