package apk.dispatcher.page.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.log.AppLogger
import apk.dispatcher.page.Page
import apk.dispatcher.page.config.showApkConfigPage
import apk.dispatcher.style.AppColors

/**
 * 启动页
 */
@Composable
fun StartPage(navController: NavController) {
    Page {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    navController.showApkConfigPage(null)
                }
            ) {
                Text(
                    "新建App",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
        LaunchedEffect(Unit) {
            if (!ApkConfigDao().isEmpty()) {
                navController.navigate("home")
            }
        }
        DisposableEffect(Unit) {
            AppLogger.info("启动页", "启动")

            onDispose {
                AppLogger.info("启动页", "销毁")
            }
        }
    }

}