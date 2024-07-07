package apk.dispatcher.page.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apk.dispatcher.channel.SubmitState
import apk.dispatcher.channel.TaskLauncher
import apk.dispatcher.page.Page
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.widget.ConfirmDialog


fun NavController.showUploadPage(uploadParam: UploadParam) {
    val param = UploadParam.adapter.toJson(uploadParam)
    navigate("upload/${param}")
}


@Composable
fun UploadPage(uploadParam: UploadParam, onDismiss: () -> Unit) {
    val viewModel = viewModel { UploadVM(uploadParam) }
    LaunchedEffect(Unit) {
        viewModel.startDispatch()
    }

    Page {
        val launchers = viewModel.taskLaunchers
        val uploadSuccess = launchers.all { it.getSubmitState().value?.success == true }
        val uploadFail = launchers.all { it.getSubmitState().value?.finish == true } && !uploadSuccess
        val uploading = !uploadFail && !uploadSuccess
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(40.dp)
        ) {
            val title = when {
                uploadSuccess -> "上传成功"
                uploadFail -> "上传失败"
                else -> "正在上传中"
            }
            Text(title, fontSize = 18.sp, color = AppColors.fontBlack, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(40.dp))
            val rows = launchers.chunked(3)
            val cellSpace = 18.dp
            for (row in rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ChannelCell(row.getOrNull(0))
                    Spacer(Modifier.width(cellSpace))
                    ChannelCell(row.getOrNull(1))
                    Spacer(Modifier.width(cellSpace))
                    ChannelCell(row.getOrNull(2))
                }
                Spacer(Modifier.height(cellSpace))
            }

            Spacer(Modifier.weight(1f))
            BottomButtons(viewModel, onDismiss)
            Spacer(Modifier.height(20.dp))

        }
    }
}

@Composable
private fun ColumnScope.BottomButtons(viewModel: UploadVM, onDismiss: () -> Unit) {
    val launchers = viewModel.taskLaunchers
    val uploadSuccess = launchers.all { it.getSubmitState().value?.success == true }
    val uploadFail = launchers.all { it.getSubmitState().value?.finish == true } && !uploadSuccess
    val uploading = !uploadFail && !uploadSuccess

    var showExitDialog by remember { mutableStateOf(false) }
    Row(Modifier.align(Alignment.CenterHorizontally)) {
        when {
            uploading -> {
                NegativeButton("取消") {
                    showExitDialog = true
                }
            }
            uploadFail -> {
                NegativeButton("取消", onDismiss)
                Spacer(modifier = Modifier.width(20.dp))
                PositiveButton("重试") {
                    viewModel.retryDispatch()
                }
            }

            else -> {
                PositiveButton("关闭", onDismiss)
            }
        }

    }
    if (showExitDialog) {
        ConfirmDialog("确定取消上传吗？", onDismiss = {
            showExitDialog = false
        }, onConfirm = {
            showExitDialog = false
            viewModel.cancelDispatch()
            onDismiss()
        })
    }
}

private val fontSize = 16.sp
private val buttonWidth = 120.dp

@Composable
private fun PositiveButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(buttonWidth)
            .clip(AppShapes.roundButton)
            .background(AppColors.primary)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            color = Color.White,
            letterSpacing = 3.sp,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun NegativeButton(text: String, onClick: () -> Unit) {
    val hoverSource = remember { MutableInteractionSource() }
    val hovered = hoverSource.collectIsHoveredAsState().value
    val borderColor = if (hovered) AppColors.primary else AppColors.fontGray
    val textColor = if (hovered) AppColors.primary else AppColors.fontBlack
    Row(
        modifier = Modifier
            .width(buttonWidth)
            .hoverable(hoverSource)
            .border(0.5.dp, borderColor, AppShapes.roundButton)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            color = textColor,
            letterSpacing = 3.sp,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun RowScope.ChannelCell(launcher: TaskLauncher?) {
    if (launcher != null) {
        ChannelUploadState(
            launcher.name,
            launcher.getSubmitState().value ?: SubmitState.Waiting,
            modifier = Modifier.weight(1f)
        )
    } else {
        Spacer(modifier = Modifier.weight(1f))
    }
}

