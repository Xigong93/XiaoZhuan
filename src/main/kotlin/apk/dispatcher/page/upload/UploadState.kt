package apk.dispatcher.page.upload

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors
import apk.dispatcher.page.home.ChannelState

@Composable
fun ChannelUploadState(name: String, state: ChannelState, modifier: Modifier = Modifier) {
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