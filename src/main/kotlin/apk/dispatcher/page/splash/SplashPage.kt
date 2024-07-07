package apk.dispatcher.page.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.page.Page
import apk.dispatcher.style.AppColors
import kotlinx.coroutines.delay


@Composable
fun SplashPage(navController: NavController) {
    Page {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painterResource("icon.png"),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(40.dp))
            Text(
                "一键上传Apk到多个应用市场，开源，免费",
                color = AppColors.fontBlack,
                fontSize = 16.sp
            )
        }

        LaunchedEffect(true) {
            delay(800)
            navController.navigate(getStartDestination())
        }
    }

}

private suspend fun getStartDestination(): String {
    val isEmpty = ApkConfigDao().isEmpty()
    return if (isEmpty) "start" else "home"
}
