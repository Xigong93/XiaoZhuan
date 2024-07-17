package com.xigong.xiaozhuan.page.config

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xigong.xiaozhuan.Api
import com.xigong.xiaozhuan.channel.ChannelRegistry
import com.xigong.xiaozhuan.config.ApkConfig
import com.xigong.xiaozhuan.page.Page
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import com.xigong.xiaozhuan.util.browser
import com.xigong.xiaozhuan.widget.NegativeButton
import com.xigong.xiaozhuan.widget.PositiveButton
import com.xigong.xiaozhuan.widget.VerticalTabBar
import kotlinx.coroutines.launch

fun NavController.showApkConfigPage(appId: String?) {
    navigate("edit?id=${appId ?: ""}")
}


/**
 * 配置页面
 */
@ExperimentalFoundationApi
@Composable
fun ApkConfigPage(
    navController: NavController,
    appId: String? = null,
) {
    val viewModel = viewModel { ApkConfigVM(appId) }
    val channels = ChannelRegistry.channels
    val titles = remember { listOf("基本信息") + channels.map { it.channelName } }
    val pageState = rememberPagerState(pageCount = { titles.size })

    val scope = rememberCoroutineScope()
    Page {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth().weight(1.0f)) {

                Row(modifier = Modifier.width(200.dp)) {
                    VerticalTabBar(titles, pageState.currentPage) {
                        scope.launch {
                            pageState.animateScrollToPage(it)
                        }
                    }
                }
                VerticalPager(
                    pageState,
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                ) { page ->
                    ConfigList(page, viewModel)
                }
            }
            BottomButtons(
                onSaveClick = {
                    scope.launch {
                        if (viewModel.saveApkConfig()) {
                            navController.popBackStack()
                        }
                    }
                }, onCloseClick = {
                    navController.popBackStack()

                }
            )
        }
    }
}


@Composable
private fun ConfigList(tabIndex: Int, viewModel: ApkConfigVM) {
    if (tabIndex == 0) {
        BasicApkConfig(viewModel.apkConfigState) {
            viewModel.apkConfigState = it
        }
    } else {
        val channel = viewModel.apkConfigState.channels[tabIndex - 1]
        val params = requireNotNull(ChannelRegistry.getChannel(channel.name)).getParams()
        ChannelConfigPage(viewModel.apkConfigState.enableChannel, params, channel) {
            viewModel.updateChannel(it)
        }
    }

}


@Composable
private fun BottomButtons(onSaveClick: () -> Unit, onCloseClick: () -> Unit) {
    Row(modifier = Modifier.padding(bottom = 20.dp)) {
        NegativeButton("取消", modifier = Modifier.width(140.dp)) {
            onCloseClick()
        }
        Spacer(modifier = Modifier.width(20.dp))
        PositiveButton("保存", modifier = Modifier.width(140.dp)) {
            onSaveClick()
        }
    }
}


@Composable
private fun BasicApkConfig(apkConfig: ApkConfig, onValueChange: (ApkConfig) -> Unit) {
    Column {
        val spaceHeight = Modifier.height(12.dp)
        TextRaw("App名称", "", apkConfig.name) {
            onValueChange(apkConfig.copy(name = it))
        }
        Spacer(modifier = spaceHeight)
        TextRaw("ApplicationId", "应用包名", apkConfig.applicationId) {
            onValueChange(apkConfig.copy(applicationId = it))

        }
        Spacer(modifier = spaceHeight)
        CheckboxRow(Modifier, "开启渠道包", apkConfig.enableChannel) {
            onValueChange(apkConfig.copy(enableChannel = it))
        }
        Spacer(modifier = Modifier.height(60.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .clip(AppShapes.roundButton)
                .clickable { browser(Api.INSTRUCTIONS_URL) }
                .padding(8.dp)
        ) {
            Image(
                painterResource("config_help.png"),
                contentDescription = null,
                colorFilter = ColorFilter.tint(AppColors.primary),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "操作说明",
                fontSize = 14.sp,
                color = AppColors.primary,
            )
        }


    }
}
