package apk.dispatcher.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import apk.dispatcher.page.Page
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import apk.dispatcher.widget.ConfirmDialog
import apk.dispatcher.widget.Toast

@Composable
fun HomePage(navController: NavController) {
    Page {
        val viewModel = remember { HomePageVM() }
        var showConfirmDialog by remember { mutableStateOf(false) }
        var showMenu by remember { mutableStateOf(false) }

        Content(viewModel, { showMenu = true })
        if (showMenu) {
            Menu(navController, viewModel, onClose = {
                showMenu = false
            }, onDelete = {
                showConfirmDialog = true
            })
        }

        if (showConfirmDialog) {
            val name = viewModel.currentApk?.name
            ConfirmDialog("确定删除${name}吗？", onConfirm = {
                viewModel.deleteCurrent()
                if (viewModel.apkList.isEmpty()) {
                    navController.popBackStack()
                }
                Toast.show("${name}已删除")
                showConfirmDialog = false
            }, onDismiss = {
                showConfirmDialog = false
            })
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
                val id = viewModel.currentApk?.applicationId ?: ""
                navController.navigate("edit?id=$id")
            }

            override fun onDeleteClick() {
                onDelete()
            }
        }
    }, onDismiss = onClose)
}


@Composable
private fun Content(viewModel: HomePageVM, showMenuClick: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Title()
            Spacer(Modifier.width(80.dp))
            ApkSelector(viewModel)
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
        val currentApk = viewModel.currentApk
        if (currentApk != null) {
            ApkPage(currentApk)
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

