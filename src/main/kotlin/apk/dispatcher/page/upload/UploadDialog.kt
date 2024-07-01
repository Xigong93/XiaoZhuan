package apk.dispatcher.page.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import apk.dispatcher.channel.TaskLauncher
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.page.home.ApkViewModel
import apk.dispatcher.page.home.ChannelState


@Composable
fun UploadDialog(viewModel: ApkViewModel, onDismiss: () -> Unit) {
    SideEffect {
        viewModel.startDispatch()
    }
    Dialog(
        onDismissRequest = { },
        properties = remember { DialogProperties(dismissOnClickOutside = false) }
    ) {
        val launchers = viewModel.selectedLaunchers()
        val uploadSuccess = launchers.all { it.getChannelState().value?.success == true }
        val uploadFail = launchers.all { it.getChannelState().value?.finish == true } && !uploadSuccess
        val uploading = !uploadFail && !uploadSuccess
        Column(
            modifier = Modifier
                .width(700.dp)
                .clip(RoundedCornerShape(AppShapes.largeCorner))
                .background(Color.White)
                .padding(20.dp),
        ) {
            val title = when {
                uploadSuccess -> "上传成功"
                uploadFail -> "上传失败"
                else -> "正在上传中"
            }
            Text(title, fontSize = 16.sp, color = AppColors.fontBlack, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            val rows = launchers.chunked(3)
            for (row in rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ChannelCell(row.getOrNull(0))
                    Spacer(Modifier.width(10.dp))
                    ChannelCell(row.getOrNull(1))
                    Spacer(Modifier.width(10.dp))
                    ChannelCell(row.getOrNull(2))
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(20.dp))
            BottomButtons(viewModel, onDismiss)

        }
    }
}

@Composable
private fun BottomButtons(viewModel: ApkViewModel, onDismiss: () -> Unit) {
    val launchers = viewModel.selectedLaunchers()
    val uploadSuccess = launchers.all { it.getChannelState().value?.success == true }
    val uploadFail = launchers.all { it.getChannelState().value?.finish == true } && !uploadSuccess
    val uploading = !uploadFail && !uploadSuccess
    Row {
        Spacer(Modifier.weight(1f))
        val hoverSource = remember { MutableInteractionSource() }
        val hovered = hoverSource.collectIsHoveredAsState().value
        val borderColor = if (hovered) AppColors.primary else AppColors.fontGray
        val textColor = if (hovered) AppColors.primary else AppColors.fontBlack
        if (uploading) {
            Row(
                modifier = Modifier
                    .hoverable(hoverSource)
                    .border(0.5.dp, borderColor, AppShapes.roundButton)
                    .clickable {
                        viewModel.cancelDispatch()
                        onDismiss()
                    }
            ) {
                Text(
                    "取消",
                    color = textColor,
                    letterSpacing = 3.sp,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)

                )
            }
        }

        if (uploadFail) {
            Row(
                modifier = Modifier
                    .hoverable(hoverSource)
                    .border(0.5.dp, borderColor, AppShapes.roundButton)
                    .clickable {
                        onDismiss()
                    }
            ) {
                Text(
                    "取消",
                    color = textColor,
                    letterSpacing = 3.sp,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)

                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                modifier = Modifier
                    .clip(AppShapes.roundButton)
                    .background(AppColors.primary)
                    .clickable { viewModel.retryDispatch() }
            ) {
                Text(
                    "重试",
                    color = Color.White,
                    letterSpacing = 3.sp,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        if (uploadSuccess) {
            Row(
                modifier = Modifier
                    .clip(AppShapes.roundButton)
                    .background(AppColors.primary)
                    .clickable { onDismiss() }
            ) {
                Text(
                    "关闭",
                    color = Color.White,
                    letterSpacing = 3.sp,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.ChannelCell(launcher: TaskLauncher?) {
    if (launcher != null) {
        ChannelUploadState(
            launcher.name,
            launcher.getChannelState().value ?: ChannelState.Waiting,
            modifier = Modifier.weight(1f)
        )
    } else {
        Spacer(modifier = Modifier.weight(1f))
    }
}

