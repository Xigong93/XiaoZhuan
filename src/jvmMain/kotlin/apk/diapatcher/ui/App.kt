package apk.diapatcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.diapatcher.ApkConfig
import apk.diapatcher.ApkConfigDao
import apk.diapatcher.style.AppColors
import apk.diapatcher.widget.HorizontalTabBar

@Composable
@Preview
fun App() {
//    val selectedTab by remember { mutableStateOf(0) }
    val appWindow = remember { AppWindow() }
    appWindow.render()

}

private class AppWindow {
    val selectedTab = mutableStateOf(0)

    val apkConfigDao = ApkConfigDao()

    val apkConfigs = mutableStateOf<List<ApkConfig>>(emptyList())

    /**
     * 设置配置页面
     */
    val configPage = mutableStateOf<ConfigPage?>(null)

    init {
        apkConfigs.value = apkConfigDao.getApkConfigList()
    }

    val pages = apkConfigs.value.map { ApkPage(it) }

    val titles = pages.map { it.title }

    @Composable
    fun render() {
        val configPage = configPage.value
        if (configPage != null) {
            configPage.render()
        } else if (pages.isEmpty()) {
            empty()
        } else {
            content()
        }
    }

    @Composable
    fun empty() {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    configPage.value = ConfigPage {
                        configPage.value = null
                    }
                }
            ) {
                Text(
                    "新建App",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }

    @Composable
    fun content() {
        Column(Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Title()
                Spacer(Modifier.width(100.dp))
                HorizontalTabBar(titles, selectedTab.value) {
                    selectedTab.value = it
                }
            }
            Divider()
            pages[selectedTab.value].render()
        }
    }
}

@Composable
private fun Title() {
    Text(
        "软件版本更新",
        color = Color.Black,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

