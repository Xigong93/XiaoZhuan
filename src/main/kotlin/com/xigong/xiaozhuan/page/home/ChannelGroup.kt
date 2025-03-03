package com.xigong.xiaozhuan.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xigong.xiaozhuan.channel.MarketState
import com.xigong.xiaozhuan.page.upload.UploadParam
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import com.xigong.xiaozhuan.widget.ConfirmDialog
import com.xigong.xiaozhuan.widget.ErrorPopup
import com.xigong.xiaozhuan.widget.Toast
import java.text.SimpleDateFormat
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * 渠道页面
 */
@Composable
fun ChannelGroup(viewModel: ApkPageState, startUpload: (UploadParam) -> Unit) {
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
                        ChannelView(selected, name, desc, marketState) { checked ->
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
//                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(AppShapes.roundButton)
                    .clickable {
                        viewModel.enableScheduledRelease = !viewModel.enableScheduledRelease
                    }
                    .padding(end = 12.dp)) {
                Checkbox(
                    viewModel.enableScheduledRelease,
                    onCheckedChange = { all ->
                        viewModel.enableScheduledRelease = !viewModel.enableScheduledRelease
                    },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("定时发布")
            }

            Spacer(Modifier.width(40.dp))

            if (viewModel.enableScheduledRelease) {
                DateInput(viewModel.releaseTime) {
                    viewModel.releaseTime = it
                }
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
        append("\n")
        if (viewModel.enableScheduledRelease) {
            val date = viewModel.releaseTime.getData()
            val dateStr = SimpleDateFormat("yyyy年MM月dd日 HH时").format(date)
            append("发布方式:  于 $dateStr 定时发布")
        } else {
            append("发布方式:  审核通过后立即发布")
        }
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
            marketState = null,
            onCheckChange = {})
    }
}

@Composable
private fun ChannelView(
    selected: Boolean,
    name: String,
    desc: String?,
    marketState: MarketState?,
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
        val state = when (marketState) {
            null -> ""
            is MarketState.Loading -> "加载中"
            is MarketState.Success -> {
                val info = marketState.info
                val version = info.lastVersion?.name?.let { "v$it" } ?: "未知版本"
                "$version ${info.reviewState.desc}"
            }

            is MarketState.Error -> {
                "获取状态失败"
            }
        }
        Text(
            state,
            fontSize = 12.sp,
            color = AppColors.fontBlack
        )
        if (marketState is MarketState.Error) {
            Row {
                var showError by remember { mutableStateOf(false) }
                if (showError) {
                    ErrorPopup(marketState.exception) {
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
}


