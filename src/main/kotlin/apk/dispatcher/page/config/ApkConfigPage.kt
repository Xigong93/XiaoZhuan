package apk.dispatcher.page.config

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import apk.dispatcher.channel.ChannelRegistry
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.page.Page
import apk.dispatcher.style.AppColors
import apk.dispatcher.widget.VerticalTabBar
import kotlinx.coroutines.launch

/**
 * 配置页面
 */
@ExperimentalFoundationApi
@Composable
fun ApkConfigPage(
    navController: NavController,
    appId: String? = null,
) {
    val viewModel = remember {
        val apkConfig = appId?.let { ApkConfigDao().getConfig(it) }
        ApkConfigVM(apkConfig)
    }

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
                    pageState, modifier =
                    Modifier.fillMaxSize().padding(20.dp)
                ) { page ->
                    ConfigList(page, viewModel)
                }
            }
            BottomButtons(
                onSaveClick = {
                    if (viewModel.saveApkConfig()) {
                        navController.popBackStack()
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
        ChannelConfigPage(viewModel.apkConfigState.enableChannel, channel) {
            viewModel.updateChannel(it)
        }
    }

}


@Composable
private fun BottomButtons(onSaveClick: () -> Unit, onCloseClick: () -> Unit) {
    Row {
        Button(
            colors = ButtonDefaults.buttonColors(AppColors.primary),
            onClick = { onSaveClick() }
        ) {
            Text(
                "保存",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            colors = ButtonDefaults.buttonColors(AppColors.fontGray),
            onClick = { onCloseClick() }
        ) {
            Text(
                "关闭",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}


@Composable
private fun BasicApkConfig(apkConfig: ApkConfig, onValueChange: (ApkConfig) -> Unit) {
    Column {
        val spaceHeight = Modifier.height(12.dp)
        InputRaw("App名称", "", apkConfig.name) {
            onValueChange(apkConfig.copy(name = it))
        }
        Spacer(modifier = spaceHeight)
        InputRaw("ApplicationId", "", apkConfig.applicationId) {
            onValueChange(apkConfig.copy(applicationId = it))

        }
        Spacer(modifier = spaceHeight)
        CheckboxRow(Modifier, "开启渠道包", apkConfig.enableChannel) {
            onValueChange(apkConfig.copy(enableChannel = it))
        }


    }
}
