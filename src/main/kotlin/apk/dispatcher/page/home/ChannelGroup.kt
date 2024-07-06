package apk.dispatcher.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.page.upload.UploadParam
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.widget.ConfirmDialog
import apk.dispatcher.widget.Toast
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * 渠道页面
 */
@Composable
fun ChannelGroup(viewModel: ApkPageState, startUpload:(UploadParam)->Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "渠道",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))

                Box(modifier = Modifier.size(40.dp)) {
                    if (viewModel.loadingMarkState) {
                        CircularProgressIndicator(
                            color = AppColors.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Image(
                            painterResource("refresh.png"),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppColors.primary),
                            modifier = Modifier.size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    tryReloadMarketState(viewModel)
                                }
                                .padding(2.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
            }
            Spacer(Modifier.height(12.dp))

            val scrollState = rememberScrollState()
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                        .weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(15.dp))
                    viewModel.channels.withIndex().forEach { (index, chan) ->
                        val selected = viewModel.selectedChannels.contains(chan.channelName)
                        val name = chan.channelName
                        val taskLauncher = viewModel.taskLaunchers.first { it.name == name }
                        val apkFileState = taskLauncher.getApkFileState()
                        val desc = apkFileState.value?.name
                        val marketState = taskLauncher.getMarketState().value
                        val state = when {
                            viewModel.loadingMarkState || marketState == null -> "加载中"
                            marketState.isSuccess -> {
                                val state = marketState.getOrThrow()
                                "v${state.lastVersionName} ${state.reviewState.desc}"
                            }

                            else -> "获取状态失败"
                        }
                        ChannelView(selected, name, desc, state) { checked ->
                            viewModel.selectChannel(name, checked)
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                VerticalScrollbar(
                    rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 14.dp)
        ) {
            val allSelected = viewModel.allChannelSelected()
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clip(AppShapes.roundButton)
                .clickable {
                    viewModel.selectAll(allSelected.not())
                }
                .padding(end = 12.dp)) {
                Checkbox(
                    allSelected,
                    onCheckedChange = { all ->
                        viewModel.selectAll(all)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("全选")
            }

            val uploadState = remember { mutableStateOf<UploadParam?>(null) }
            val uploadParam = uploadState.value
            if (uploadParam != null) {
                showConfirmDialog(viewModel,
                    onConfirm = {
                        startUpload(uploadParam)
                        uploadState.value = null
                    }, onDismiss = {
                        uploadState.value = null
                    }
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    uploadState.value = viewModel.startDispatch()
                }
            ) {

                Text(
                    "发布更新",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}

private fun tryReloadMarketState(viewModel: ApkPageState) {
    // 控制刷新频率，防止应用市场接口限流
    val diff =
        (System.currentTimeMillis() - viewModel.lastUpdateMarketStateTime).milliseconds
    val threshold = 30.seconds
    val leftSeconds = (threshold - diff).inWholeSeconds
    if (leftSeconds > 0) {
        Toast.show("刷新太频繁了，请${leftSeconds}秒后重试")
    } else {
        viewModel.loadMarketState()
    }
}


@Composable
private fun showConfirmDialog(viewModel: ApkPageState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val apkInfo = viewModel.getApkInfoState().value ?: return
    val selectedChannels = viewModel.selectedChannels
    val message = buildString {
        append("包名:  ${apkInfo.applicationId}")
        append("\n")
        append("版本:  ${apkInfo.versionName}(${apkInfo.versionCode})")
        append("\n")
        append("渠道:  ${selectedChannels.joinToString(",")}")
    }


    ConfirmDialog(
        message, "确定上传",
        onConfirm = {
            onConfirm()
        }, onDismiss = {
            onDismiss()
        }
    )
}


@Preview
@Composable
private fun ChannelViewPreview() {
    Column(modifier = Modifier.background(AppColors.pageBackground).padding(10.dp)) {
        ChannelView(
            true,
            name = "华为",
            desc = "星题库-v5.30.0-HUAWEI.apk",
            state = "v5.2.1 审核中",
            onCheckChange = {})
    }
}

@Composable
private fun ChannelView(
    selected: Boolean,
    name: String,
    desc: String?,
    state: String,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.cardBackground)
            .clickable {
                onCheckChange(!selected)
            }
            .padding(16.dp)
    ) {
        Checkbox(
            selected,
            onCheckedChange = onCheckChange,
            colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
        )
        Text(
            name,
            fontSize = 14.sp,
            color = AppColors.fontBlack,
            modifier = Modifier.requiredWidthIn(min = 50.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            desc ?: "",
            fontSize = 12.sp,
            color = AppColors.fontGray
        )
        Spacer(modifier = Modifier.weight(1.0f))

        Text(
            state,
            fontSize = 12.sp,
            color = AppColors.fontBlack
        )
    }
}


