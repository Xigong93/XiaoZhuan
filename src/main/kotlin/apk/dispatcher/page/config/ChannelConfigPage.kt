package apk.dispatcher.page.config

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.config.ApkConfig


@Composable
fun ChannelConfigPage(
    enableChannel: Boolean,
    config: ApkConfig.Channel,
    onConfigChange: (newConfig: ApkConfig.Channel) -> Unit
) {
    Column {
        CheckboxRow(modifier = Modifier.padding(vertical = 8.dp), name = "是否启用", check = config.enable) {
            onConfigChange(config.copy(enable = it))
        }
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .verticalScroll(rememberScrollState())
                .alpha(if (config.enable) 1.0f else 0.5f)
        ) {
            for (param in config.params) {
                // 未启用渠道包时，不显示这个选项
                if (param.name == ChannelTask.FILE_NAME_IDENTIFY && !enableChannel) {
                    continue
                }
                InputRaw(param.name, "", param.value) { newValue ->
                    onConfigChange(createNewChannel(config, param, newValue))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}

private fun createNewChannel(
    oldChannel: ApkConfig.Channel,
    param: ApkConfig.Param,
    newValue: String
): ApkConfig.Channel {
    val newParams = oldChannel.params.map { p ->
        if (p.name == param.name) {
            p.copy(value = newValue)
        } else {
            p
        }
    }
    return oldChannel.copy(params = newParams)
}

