package apk.dispatcher.page.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.BuildConfig
import apk.dispatcher.page.Page
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.style.AppStrings
import kotlinx.coroutines.delay

@Composable
fun SplashPage() {
    var visible by remember { mutableStateOf(true) }
    if (visible) {
        Page(Modifier.background(AppColors.auxiliary)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painterResource(BuildConfig.ICON),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                        .clip(RoundedCornerShape(AppShapes.largeCorner))
                )
                Spacer(Modifier.height(40.dp))
                Text(
                    AppStrings.appDesc,
                    color = AppColors.fontBlack,
                    fontSize = 16.sp
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        delay(1000)
        visible = false
    }


}

