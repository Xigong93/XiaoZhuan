package apk.dispatcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors
import apk.dispatcher.util.PathUtil
import apk.dispatcher.widget.Toast
import java.awt.Desktop
import java.io.IOException


class EditMenu() {
    var eventListener: EventListener? = null


    @Composable
    fun render() {

        Column(
            modifier = Modifier.width(180.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(
                    1.dp, AppColors.pageBackground,
                    shape = RoundedCornerShape(6.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item("新增") {
                eventListener?.onAddClick()
            }
            Divider()
            item("编辑") {
                eventListener?.onEditClick()
            }
            Divider()
            item("打开配置") {
                openApkDispatchDir()
            }
            Divider()
            item("删除", color = Color.Red) {
                eventListener?.onDeleteClick()
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

    @Composable
    fun item(title: String, color: Color = AppColors.fontBlack, onClick: () -> Unit) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 20.dp)) {
            Text(
                text = title,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    interface EventListener {
        fun onAddClick();
        fun onEditClick()
        fun onDeleteClick()
    }
}

@Preview
@Composable
fun editMenuPreview() {
    EditMenu().render()
}