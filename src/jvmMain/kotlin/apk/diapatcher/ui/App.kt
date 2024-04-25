package apk.diapatcher.ui

import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import apk.diapatcher.ApkConfig
import apk.diapatcher.ApkConfigDao
import apk.diapatcher.style.AppColors
import apk.diapatcher.widget.HorizontalTabBar

@Composable
@Preview
fun App() {
    val appWindow = remember { AppWindow() }
    appWindow.Render()
}

private class AppWindow {

    val selectedTab = mutableStateOf(0)

    val apkConfigDao = ApkConfigDao()

    val editMenu = EditMenu().apply {
        eventListener = object : EditMenu.EventListener {
            override fun onAddClick() {
                hideMenu()
                showConfigPage(null)
            }

            override fun onEditClick() {
                hideMenu()
                val current = currentApkConfig()
                showConfigPage(current)
            }

            override fun onDeleteClick() {
                hideMenu()
                val current = currentApkConfig() ?: return
                apkConfigDao.removeApkConfig(current)
                reload()
            }

            private fun currentApkConfig(): ApkConfig? {
                return apkConfigs.value.getOrNull(selectedTab.value)
            }

            private fun hideMenu() {
                showMenu.value = false
            }

        }
    }

    val apkConfigs = mutableStateOf<List<ApkConfig>>(emptyList())

    /**
     * 设置配置页面
     */
    val configPage = mutableStateOf<ConfigPage?>(null)

    /**
     * 是否显示菜单
     */
    val showMenu = mutableStateOf(false)

    /**
     * 菜单的绝对位置
     */
    var menuCoo: LayoutCoordinates? = null

    var pages = apkConfigs.value.map { ApkPage(it) }


    init {
        reload()
    }

    fun reload() {
        apkConfigs.value = apkConfigDao.getApkConfigList()

        pages = apkConfigs.value.map { ApkPage(it) }

        selectedTab.value = selectedTab.value.coerceIn(0, (apkConfigs.value.size - 1).coerceAtLeast(0))
    }

    @Composable
    fun Render() {
        Box(modifier = Modifier.fillMaxWidth()) {
            PopupMenu()
            val configPage = configPage.value
            if (configPage != null) {
                configPage.render()
            } else if (pages.isEmpty()) {
                Empty()
            } else {
                Content()
            }
        }

    }

    @Composable
    fun PopupMenu() {
        val position = menuCoo?.takeIf { it.isAttached }?.positionInWindow() ?: Offset.Zero
        val density = LocalDensity.current
        val x = with(density) { position.x.toDp() }
        val y = with(density) { position.y.toDp() }
        Box(modifier = Modifier.zIndex(100f).fillMaxSize()) {
            AnimatedVisibility(
                visible = showMenu.value,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0f, 0f, 0f, 0.4f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showMenu.value = false
                        }
                )
            }

            AnimatedVisibility(
                visible = showMenu.value,
                enter = slideInHorizontally(initialOffsetX = { it * 2 }),
                exit = slideOutHorizontally(targetOffsetX = { it * 2 })
            ) {
                Box(
                    modifier = Modifier.offset(x - 150.dp, y + 46.dp)
                ) {
                    editMenu.render()
                }
            }
        }

    }

    @Composable
    fun Empty() {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    showConfigPage(null)
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

    private fun showConfigPage(apkConfig: ApkConfig?) {
        configPage.value = ConfigPage(apkConfig) {
            configPage.value = null
            reload()
        }
    }

    @Composable
    fun Content() {
        Column(Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Title()
                Spacer(Modifier.width(100.dp))
                val titles = remember(apkConfigs.value) { apkConfigs.value.map { it.name } }
                HorizontalTabBar(titles, selectedTab.value) {
                    selectedTab.value = it
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Image(
                    painterResource("menu.png"),
                    contentDescription = "菜单",
                    modifier = Modifier.size(32.dp)
                        .clip(CircleShape)
                        .clickable {
                            showMenu.value = true
                        }
                        .onGloballyPositioned {
                            menuCoo = it
                        }


                )
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

