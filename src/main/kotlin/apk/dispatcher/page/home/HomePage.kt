package apk.dispatcher.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Divider
import androidx.compose.material.Text
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
import androidx.navigation.NavController
import apk.dispatcher.page.Page
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes

@Composable
fun HomePage(navController: NavController) {
    Page {
        val viewModel = remember { HomePageVM() }
        Content(viewModel)

        if (viewModel.showMenu) {
            MenuDialog(listener = remember {
                object : MenuDialogListener {
                    override fun onAddClick() {
                        navController.navigate("edit")
                    }

                    override fun onEditClick() {
                        val id = viewModel.currentApk.applicationId
                        navController.navigate("edit?id=$id")
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
        var showApkMenu by remember { mutableStateOf(false) }
        if (showApkMenu) {
            CursorDropdownMenu(
                true,
                onDismissRequest = {
                    showApkMenu = false
                }, modifier = Modifier.width(180.dp)
            ) {
                viewModel.apkList.forEachIndexed { index, apk ->
                    if (index != 0) {
                        Divider(Modifier.fillMaxWidth())
                    }
                    item(apk.name) {
                        viewModel.currentApk = apk
                        showApkMenu = false
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Title()
            Spacer(Modifier.width(80.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clip(AppShapes.roundButton)
                    .clickable {
                        showApkMenu = true
                    }
                    .padding(12.dp)
            ) {
                Text(viewModel.currentApk.name, fontSize = 18.sp, color = AppColors.primary)
                Spacer(Modifier.width(4.dp))
                Image(
                    painterResource("arrow_down.png"),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppColors.primary),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.weight(1f))
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
        AnimatedContent(viewModel.currentApk) {
            ApkPage(viewModel.currentApk)
        }
    }
}


@Composable
private fun item(title: String, color: Color = AppColors.fontBlack, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(vertical = 20.dp, horizontal = 12.dp)) {
        Text(
            text = title,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
        )
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

