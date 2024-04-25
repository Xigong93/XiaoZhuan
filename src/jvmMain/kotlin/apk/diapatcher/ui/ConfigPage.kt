package apk.diapatcher.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.diapatcher.ApkChannelRegistry
import apk.diapatcher.ApkConfig
import apk.diapatcher.ApkConfigDao
import apk.diapatcher.style.AppColors
import apk.diapatcher.widget.Section
import apk.diapatcher.widget.VerticalTabBar

/**
 * 配置页面
 */
class ConfigPage(
    private val apkConfig: ApkConfig? = null,
    private val onCloseClick: () -> Unit
) {

    private val currentIndex = mutableStateOf(0)

    private val channels = ApkChannelRegistry.channels

    private val titles = listOf("基本信息") + channels.map { it.channelName }

    /**
     * App名称
     */
    private val inputAppName = mutableStateOf(apkConfig?.name ?: "")

    /**
     * 渠道的配置参数
     */
    private val inputParams = mutableMapOf<String, MutableMap<String, String>>()

    /**
     * 开启渠道包
     */
    private val enableChannels = mutableStateOf(true)

    /**
     * 32位和64位兼容包
     */
    private val enableCombinedAbi = mutableStateOf(true)

    @Composable
    fun render() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().weight(1.0f)) {
                Row(modifier = Modifier.width(200.dp)) {
                    Channels()
                }
                ConfigList()

            }
            BottomButtons()
        }
    }


    @Composable
    fun Channels() {
        VerticalTabBar(titles, currentIndex.value) {
            currentIndex.value = it
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ConfigList() {
        val index = currentIndex.value
        AnimatedContent(index) {
            if (index == 0) {
                BasicInfo()
            } else {
                val channel = channels[index - 1]
                val params = channel.getParams()
                val cName = channel.channelName
                val saveChannel = apkConfig?.channels?.firstOrNull { it.name == cName }
                Column {
                    for (param in params) {
                        val pName = param.name
                        val saveParams = saveChannel?.params?.firstOrNull { it.name == pName }
                        val paramInput = remember(cName, pName) {
                            ParamInput(param, saveParams?.value) { value ->
                                inputParams.getOrPut(cName, ::mutableMapOf)[pName] = value
                            }
                        }
                        paramInput.render()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

    }


    @Composable
    fun BasicInfo() {
        Column {
            Section("App名称") {
                OutlinedTextField(
                    value = inputAppName.value,
                    onValueChange = { inputAppName.value = it },
                    textStyle = TextStyle(fontSize = 14.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.primary,
                        backgroundColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    enableChannels.value,
                    onCheckedChange = { enable -> enableChannels.value = enable },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("开启渠道包")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    enableCombinedAbi.value,
                    onCheckedChange = { enable -> enableCombinedAbi.value = enable },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("32位和64位兼容包")
            }

        }
    }


    @Composable
    fun BottomButtons() {
        Row {
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                onClick = {
                    saveApkConfig()
                }
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
                onClick = {
                    onCloseClick()
                }
            ) {
                Text(
                    "关闭",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }

    private fun createChannels(): List<ApkConfig.Channel> {
        return inputParams.entries
            .sortedBy { it.key }
            .map { ApkConfig.Channel(it.key, createParams(it.value)) }

    }

    private fun getMergedChannels(): List<ApkConfig.Channel> {
        val newChannels = createChannels().associateBy(ApkConfig.Channel::name)
        val oldChannels = apkConfig?.channels ?: emptyList()
        val channels = oldChannels.associateBy(ApkConfig.Channel::name).toMutableMap()
        channels.putAll(newChannels)
        return channels.values.sortedBy(ApkConfig.Channel::name)
    }

    private fun createParams(params: Map<String, String>): List<ApkConfig.Param> {
        return params.entries
            .sortedBy { it.key }
            .map { ApkConfig.Param(it.key, it.value) }
    }

    private fun saveApkConfig() {
        val apkConfigDao = ApkConfigDao()
        val appName = inputAppName.value
        val extension = ApkConfig.ExtensionConfig(enableChannels.value, enableCombinedAbi.value)
        val apkConfig = ApkConfig(
            name = appName,
            createTime = apkConfig?.createTime ?: System.currentTimeMillis(),
            channels = getMergedChannels(),
            extension = extension
        )
        try {
            val oldApkConfig = this.apkConfig
            if (oldApkConfig != null) {
                apkConfigDao.removeApkConfig(oldApkConfig)
            }
            apkConfigDao.saveApkConfig(apkConfig)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onCloseClick()
    }
}