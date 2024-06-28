package apk.dispatcher.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.navArgument
import apk.dispatcher.style.AppColors
import apk.dispatcher.ui.AppScreens
import apk.dispatcher.util.PathUtil
import apk.dispatcher.widget.Toast
import java.awt.Desktop
import java.io.IOException


@Composable
fun MenuDialog(listener: MenuDialogListener, onDismiss: () -> Unit) {
    CursorDropdownMenu(true, onDismissRequest = onDismiss, modifier = Modifier.padding(0.dp)) {
        Column(modifier = Modifier.width(200.dp)) {
            item("新增") {
                listener.onAddClick()
            }
            Divider()
            item("编辑") {
                listener.onEditClick()
            }
            Divider()
            item("打开配置") {
                openApkDispatchDir()
            }
            Divider()
            item("删除", color = Color.Red) {
                listener.onDeleteClick()
            }
        }
    }
}

private fun openApkDispatchDir() {
    try {
        // 替换为你要打开的目录路径
        val directory = PathUtil.getApkDispatcherDir()
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            desktop.open(directory)
        } else {
            Toast.show("请手动打开:${directory.absolutePath}")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

interface MenuDialogListener {
    fun onAddClick();
    fun onEditClick()
    fun onDeleteClick()
}

@Composable
private fun item(title: String, color: Color = AppColors.fontBlack, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(vertical = 20.dp)) {
        Text(
            text = title,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
        )
    }
}