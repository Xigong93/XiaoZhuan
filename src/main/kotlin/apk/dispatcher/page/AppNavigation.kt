package apk.dispatcher.page

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
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
import apk.dispatcher.style.AppColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = getStartDestination(),
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
        modifier = Modifier.fillMaxSize().background(AppColors.pageBackground),
    ) {
        composable(route = "start") {
            StartPage(navController)
        }
        composable(route = "home") {
            HomePage(navController)
        }
        composable(route = "edit?id={id}") {
            val id = it.arguments?.getString("id")
            ApkConfigPage(navController, id)
        }
    }
}

@Composable
private fun getStartDestination(): String {
    val configDao = remember { ApkConfigDao() }
    return if (configDao.getConfigList().isEmpty()) {
        "start"
    } else {
        "home"
    }
}
