package apk.dispatcher.page.splash

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.page.Page
import kotlinx.coroutines.delay


@Composable
fun SplashPage(navController: NavController) {
    Page {
        Text("启动中...", Modifier.align(Alignment.Center))
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
