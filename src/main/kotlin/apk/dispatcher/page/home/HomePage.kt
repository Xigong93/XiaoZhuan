package apk.dispatcher.page.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.log.AppLogger
import apk.dispatcher.page.Page
import apk.dispatcher.page.config.showApkConfigPage
import apk.dispatcher.page.upload.showUploadPage
import apk.dispatcher.widget.ConfirmDialog
import apk.dispatcher.widget.Toast

@Composable
fun HomePage(navController: NavController) {
    Page {
        val viewModel = viewModel { HomePageVM() }
        var showConfirmDialog by remember { mutableStateOf(false) }
        var showMenu by remember { mutableStateOf(false) }

        Content(viewModel, navController) { showMenu = true }
        if (showMenu) {
            Menu(navController, viewModel, onClose = {
                showMenu = false
            }, onDelete = {
                showConfirmDialog = true
            })
        }


        if (showConfirmDialog) {
            val name = viewModel.getCurrentApk().value?.name
            ConfirmDialog("确定删除\"${name}\"吗？", onConfirm = {
                viewModel.deleteCurrent {
                    if (ApkConfigDao().isEmpty()) {
                        navController.navigate("start") {
                            popUpTo("splash")
                        }
                    }
                }
                Toast.show("${name}已删除")
                showConfirmDialog = false
            }, onDismiss = {
                showConfirmDialog = false
            })
        }


        // 这个方法相当于onResume
        LaunchedEffect(Unit) {
            AppLogger.info("首页", "首页可见")
            viewModel.loadData()
        }

    }

}

@Composable
private fun Menu(navController: NavController, viewModel: HomePageVM, onClose: () -> Unit, onDelete: () -> Unit) {
    MenuDialog(listener = remember {
        object : MenuDialogListener {
            override fun onAddClick() {
                navController.navigate("edit")
            }

            override fun onEditClick() {
                val id = viewModel.getCurrentApk().value?.applicationId ?: ""
                navController.showApkConfigPage(id)
            }

            override fun onDeleteClick() {
                onDelete()
            }
        }
    }, onDismiss = onClose)
}


@Composable
private fun Content(
    viewModel: HomePageVM,
    navController: NavController,
    showMenuClick: () -> Unit
) {
    val currentApk = viewModel.getCurrentApk().value

    Column(Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Title()
            Spacer(Modifier.width(80.dp))
            if (currentApk != null) {
                ApkSelector(viewModel.getApkList().value, currentApk) {
                    viewModel.updateCurrent(it)
                }
            }
            Spacer(Modifier.weight(1f))
            Image(
                painterResource("menu.png"),
                contentDescription = "菜单",
                modifier = Modifier.size(32.dp)
                    .clip(CircleShape)
                    .clickable {
                        showMenuClick()
                    }
            )
        }
        Divider()

        val subNavController = rememberNavController()
        NavHost(subNavController, startDestination = "default") {
            composable("default") {

            }
            composable("apk?id={id}") {
                val appId = it.arguments?.getString("id") ?: ""
                ApkPage(appId) {
                    navController.showUploadPage(it)
                }
            }
        }

        if (currentApk != null) {
            DisposableEffect(currentApk) {
                AppLogger.info("首页", "切换到App子页面:${currentApk}")
                subNavController.navigate("apk?id=${currentApk.applicationId}") {
                    popUpTo("apk") { inclusive = true }
                }
                onDispose { }
            }
        }
    }
}


@Composable
private fun Title() {
    Text(
        "软件版本更新",
        color = Color.Black,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

