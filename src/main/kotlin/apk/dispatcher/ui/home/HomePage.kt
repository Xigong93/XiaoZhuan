package apk.dispatcher.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import apk.dispatcher.style.AppColors
import apk.dispatcher.ui.AppScreens
import apk.dispatcher.widget.HorizontalTabBar

@Composable
fun HomePage(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(AppColors.pageBackground)
    ) {
        val viewModel = remember { HomePageVM() }
        Content(viewModel)

        if (viewModel.showMenu) {
            MenuDialog(listener = remember {
                object : MenuDialogListener {
                    override fun onAddClick() {
                        navController.navigate(AppScreens.Edit.name + "/0")
                    }

                    override fun onEditClick() {
                        navController.navigate("${AppScreens.Edit.name}/${viewModel.currentApk.applicationId}")
                    }

                    override fun onDeleteClick() {
                        viewModel.deleteCurrent()
                    }

                }
            }) {
                viewModel.showMenu = false
            }
        }
    }

}


@Composable
private fun Content(viewModel: HomePageVM) {
    Column(Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Title()
            Spacer(Modifier.width(100.dp))
            val titles = viewModel.apkConfigs.value.map { it.name }
            HorizontalTabBar(titles, viewModel.selectedTab.value) {
                viewModel.selectedTab.value = it
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Image(
                painterResource("menu.png"),
                contentDescription = "菜单",
                modifier = Modifier.size(32.dp)
                    .clip(CircleShape)
                    .clickable {
                        viewModel.showMenu = true
                    }
            )
        }
        Divider()
        val apkPage = viewModel.pages[viewModel.selectedTab.value]
        AnimatedContent(apkPage) {
            apkPage.render()
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

