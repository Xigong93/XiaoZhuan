package apk.dispatcher.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.channel.MarketState
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.page.upload.UploadDialog
import apk.dispatcher.widget.ConfirmDialog
import apk.dispatcher.widget.Section
import apk.dispatcher.widget.Toast
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * 渠道页面
 */
@Composable
fun ChannelGroupPage(viewModel: ApkViewModel) {
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
                Row(modifier = Modifier.size(60.dp, 30.dp)) {
                    if (viewModel.loadingMarkState) {
                        CircularProgressIndicator(Modifier.size(30.dp), color = AppColors.primary)
                    } else {
                        Box(Modifier
                            .clip(AppShapes.roundButton)
                            .fillMaxSize()
                            .clickable {
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
                            }) {
                            Text(
                                "刷新",
                                color = AppColors.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                    }
                }

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
            var showUploading by remember { mutableStateOf(false) }
            if (showUploading) {
                UploadDialog(viewModel) { showUploading = false }
            }
            var showAlert by remember { mutableStateOf(false) }
            if (showAlert) {
                showConfirmDialog(viewModel,
                    onConfirm = {
                        showAlert = false
                        showUploading = true
                    }, onDismiss = {
                        showAlert = false

                    }
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    if (viewModel.checkForm()) {
                        showAlert = true
                    }
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


@Composable
private fun showConfirmDialog(viewModel: ApkViewModel, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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


