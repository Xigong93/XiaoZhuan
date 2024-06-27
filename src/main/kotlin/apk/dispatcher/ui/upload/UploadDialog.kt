package apk.dispatcher.ui.upload

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.ui.ApkViewModel
import apk.dispatcher.ui.ChannelState
import apk.dispatcher.ui.TaskLauncher


@Composable
fun UploadDialog(viewModel: ApkViewModel, onDismiss: () -> Unit) {
    SideEffect {
        viewModel.startDispatch()
    }
    Dialog(
        onDismissRequest = onDismiss,
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
                            .clickable { onDismiss() }
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
    }
}

@Composable
private fun RowScope.ChannelCell(launcher: TaskLauncher?) {
    if (launcher != null) {
        ChannelState(
            launcher.name,
            launcher.getChannelState().value ?: ChannelState.Waiting,
            modifier = Modifier.weight(1f)
        )
    } else {
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ChannelState(name: String, state: ChannelState, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.height(100.dp)
            .clip(RoundedCornerShape(6))
            .background(Color(0xfff4f4f4))
            .padding(horizontal = 14.dp)
            .then(modifier)
    ) {
        Column {
            Spacer(Modifier.height(10.dp))
            Text(name, color = AppColors.fontBlack, fontSize = 14.sp)
            val stateDesc = when (state) {
                is ChannelState.Waiting -> "等待中"
                is ChannelState.Processing -> "处理中"
                is ChannelState.Uploading -> "上传中"
                is ChannelState.Success -> "上传成功"
                is ChannelState.Error -> "上传失败"
            }
            Spacer(Modifier.weight(1f))
            Text(stateDesc, color = AppColors.fontGray, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.weight(1f))
        UploadState(state, Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
private fun UploadState(state: ChannelState, modifier: Modifier = Modifier) {
    val backgroundColor = Color.White
    val ringWidth = 5.dp
    Box(Modifier.size(80.dp).then(modifier)) {

        when (state) {
            is ChannelState.Waiting -> {
                val color = Color(0xff888888).copy(alpha = 0.7f)
                CircularProgressIndicator(
                    progress = 1f,
                    color = color,
                    backgroundColor = backgroundColor,
                    strokeWidth = ringWidth,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Image(
                    painterResource("state_waiting.png"),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier.align(Alignment.Center)
                        .size(50.dp)
                )
            }

            is ChannelState.Processing -> {
                CircularProgressIndicator(
                    color = AppColors.primary,
                    backgroundColor = backgroundColor,
                    strokeWidth = ringWidth,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    "处理中",
                    color = AppColors.fontBlack,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            is ChannelState.Uploading -> {
                CircularProgressIndicator(
                    progress = state.progress.toFloat() / 100f,
                    color = AppColors.primary,
                    backgroundColor = backgroundColor,
                    strokeWidth = ringWidth,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    "${state.progress}%",
                    color = AppColors.fontBlack,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is ChannelState.Success -> {
                CircularProgressIndicator(
                    progress = 1f,
                    color = Color(0xff52c41a),
                    backgroundColor = backgroundColor,
                    strokeWidth = ringWidth,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Image(
                    painterResource("state_success.png"),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center)
                        .size(40.dp)
                )
            }

            is ChannelState.Error -> {
                CircularProgressIndicator(
                    progress = 0.75f,
                    color = Color(0xffff4d4f),
                    backgroundColor = backgroundColor,
                    strokeWidth = ringWidth,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Image(
                    painterResource("state_error.png"),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center)
                        .size(30.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun UploadStatePreview() {
    Column(
        modifier = Modifier
            .background(AppColors.pageBackground)
            .padding(20.dp)
    ) {
        UploadState(ChannelState.Waiting)
        UploadState(ChannelState.Processing("处理中"))
        UploadState(ChannelState.Uploading(35))
        UploadState(ChannelState.Success)
        UploadState(ChannelState.Error("参数错误"))
    }
}