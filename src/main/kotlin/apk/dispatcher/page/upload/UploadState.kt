package apk.dispatcher.page.upload

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.channel.MarketState
import apk.dispatcher.style.AppColors
import apk.dispatcher.channel.SubmitState
import apk.dispatcher.widget.ErrorPopup

@Composable
fun ChannelUploadState(name: String, state: SubmitState, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.height(160.dp)
            .clip(RoundedCornerShape(6))
            .background(AppColors.cardBackground)
            .padding(horizontal = 14.dp)
            .then(modifier)
    ) {
        Column {
            Spacer(Modifier.height(10.dp))
            Text(name, color = AppColors.fontBlack, fontSize = 14.sp)
            val stateDesc = when (state) {
                is SubmitState.Waiting -> "等待中"
                is SubmitState.Processing -> "处理中"
                is SubmitState.Uploading -> "上传中"
                is SubmitState.Success -> "上传成功"
                is SubmitState.Error -> "上传失败"
            }
            Spacer(Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(stateDesc, color = AppColors.fontGray, fontSize = 12.sp)
                if (state is SubmitState.Error) {
                    Row {
                        var showError by remember { mutableStateOf(false) }
                        if (showError) {
                            ErrorPopup(state.exception) {
                                showError = false
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Image(
                            painterResource("error_info.png"),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.Red),
                            modifier = Modifier.size(22.dp)
                                .clickable {
                                    showError = true
                                }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.weight(1f))
        UploadState(state, Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
private fun UploadState(state: SubmitState, modifier: Modifier = Modifier) {
    val backgroundColor = Color.White
    val ringWidth = 5.dp
    Box(Modifier.size(80.dp).then(modifier)) {

        when (state) {
            is SubmitState.Waiting -> {
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

            is SubmitState.Processing -> {
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

            is SubmitState.Uploading -> {
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

            is SubmitState.Success -> {
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

            is SubmitState.Error -> {
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
        UploadState(SubmitState.Waiting)
        UploadState(SubmitState.Processing("处理中"))
        UploadState(SubmitState.Uploading(35))
        UploadState(SubmitState.Success)
        UploadState(SubmitState.Error(RuntimeException("参数错误")))
    }
}