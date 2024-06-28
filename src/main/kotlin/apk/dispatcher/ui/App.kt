package apk.dispatcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import apk.dispatcher.ApkConfigDao
import apk.dispatcher.ui.config.ApkConfigPage
import apk.dispatcher.ui.home.HomePage

@Composable
fun App() {
    val navController = rememberNavController()
    val configDao = remember { ApkConfigDao() }
    val startPage = if (configDao.getApkConfigList().isEmpty()) {
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
        composable(route = "${AppScreens.Edit.name}/{id}") { entry ->
            val id = entry.arguments?.getString("id")
            val apkConfig = id?.let { configDao.getApkConfig(it) }
            ApkConfigPage(apkConfig, navController)
        }
    }

}
