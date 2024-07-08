package apk.dispatcher.page.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors
import apk.dispatcher.AppPath
import apk.dispatcher.widget.Toast
import java.awt.Desktop
import java.io.IOException


@Composable
fun MenuDialog(listener: MenuDialogListener, onDismiss: () -> Unit) {
    DropdownMenu(true, onDismissRequest = onDismiss, modifier = Modifier.padding(0.dp)) {
        Column(modifier = Modifier.width(200.dp)) {
            item("新增") {
                onDismiss()
                listener.onAddClick()
            }
            Divider()
            item("编辑") {
                onDismiss()
                listener.onEditClick()
            }
            Divider()
            item("删除") {
                onDismiss()
                listener.onDeleteClick()
            }
            Divider()
            item("配置文件夹") {
                onDismiss()
                openApkDispatchDir()
            }
            Divider()
            item("关于软件") {
                onDismiss()
                listener.onAboutSoftClick()
            }
        }
    }
}

private fun openApkDispatchDir() {
    try {
        // 替换为你要打开的目录路径
        val directory = AppPath.getRootDir()
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
    fun onAboutSoftClick()
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