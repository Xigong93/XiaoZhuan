package apk.dispatcher.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.page.config.ApkConfigPage
import apk.dispatcher.page.home.HomePage

@Composable
fun PageNavHost() {
    val navController = rememberNavController()
    val configDao = remember { ApkConfigDao() }
    val startPage = if (configDao.getConfigList().isEmpty()) {
        AppScreens.Start.name
    } else {
        AppScreens.Home.name
    }
    NavHost(
        navController = navController,
        startDestination = startPage,
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        composable(route = AppScreens.Start.name) {
            StartPage(navController)
        }
        composable(route = AppScreens.Home.name) {
            HomePage(navController)
        }
        composable(route = "${AppScreens.Edit.name}?id={id}") { entry ->
            val id = entry.arguments?.getString("id")
            val apkConfig = id?.let { configDao.getConfig(it) }
            ApkConfigPage(apkConfig, navController)
        }
    }
}
