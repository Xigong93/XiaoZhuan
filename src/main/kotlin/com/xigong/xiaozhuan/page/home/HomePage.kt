package com.xigong.xiaozhuan.page.home

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
import com.xigong.xiaozhuan.config.ApkConfigDao
import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.page.Page
import com.xigong.xiaozhuan.page.config.showApkConfigPage
import com.xigong.xiaozhuan.page.upload.showUploadPage
import com.xigong.xiaozhuan.style.AppShapes
import com.xigong.xiaozhuan.widget.ConfirmDialog
import com.xigong.xiaozhuan.widget.Toast

@Composable
fun HomePage(navController: NavController) {
    Page {
        val viewModel = viewModel { HomePageVM() }

        Content(viewModel, navController)


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

            override fun onAboutSoftClick() {
                navController.navigate("about")
            }
        }
    }, onDismiss = onClose)
}


@Composable
private fun Content(
    viewModel: HomePageVM,
    navController: NavController
) {
    val currentApk = viewModel.getCurrentApk().value
    var showMenu by remember { mutableStateOf(false) }

    var showConfirmDialog by remember { mutableStateOf(false) }

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(36.dp)
                        .clip(AppShapes.roundButton)
                        .clickable {
                            showMenu = true
                        }
                ) {
                    Image(
                        painterResource("menu.png"),
                        contentDescription = "菜单",
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (showMenu) {
                    Menu(navController, viewModel, onClose = {
                        showMenu = false
                    }, onDelete = {
                        showConfirmDialog = true
                    })
                }
            }
        }
        Divider()
        val apkVM = viewModel.getApkVM()
        if (apkVM != null) {
            ApkPage(apkVM) {
                navController.showUploadPage(it)
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

