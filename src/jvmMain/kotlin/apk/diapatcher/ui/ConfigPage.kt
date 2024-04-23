package apk.diapatcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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

    private val inputAppName = mutableStateOf("")

    /**
     * 开启渠道包
     */
    private val enableMultiChannel = mutableStateOf(true)

    /**
     * 32位和64位兼容包
     */
    private val enableCombinedApk = mutableStateOf(true)

    @Composable
    fun render() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().weight(1.0f)) {
                Row(modifier = Modifier.width(200.dp)) {
                    channels()
                }
                configList()

            }
            bottomButtons()
        }
    }


    @Composable
    fun channels() {
        VerticalTabBar(titles, currentIndex.value) {
            currentIndex.value = it
        }
    }

    @Composable
    fun configList() {
        val index = currentIndex.value
        if (index == 0) {
            basicInfo()
        } else {
            val params = channels[index - 1].getParams()
            Column {
                for (param in params) {
                    ParamInput(param, null).render()
                }
            }
        }
    }


    @Composable
    fun basicInfo() {
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
                    enableMultiChannel.value,
                    onCheckedChange = { enable -> enableMultiChannel.value = enable },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("开启渠道包")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    enableCombinedApk.value,
                    onCheckedChange = { enable -> enableCombinedApk.value = enable },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("32位和64位兼容包")
            }

        }
    }


    @Composable
    fun bottomButtons() {
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

    private fun saveApkConfig() {
        val apkConfigDao = ApkConfigDao()
        val appName = inputAppName.value
        val extension = ApkConfig.ExtensionConfig(enableMultiChannel.value, enableCombinedApk.value)
        val apkConfig = ApkConfig(
            name = appName,
            createTime = System.currentTimeMillis(),
            channels = emptyList(),
            extension = extension
        )
        val oldApkConfig = this.apkConfig
        if (oldApkConfig != null) {
            apkConfigDao.removeApkConfig(oldApkConfig)
        }
        apkConfigDao.saveApkConfig(apkConfig)
        onCloseClick()
    }
}