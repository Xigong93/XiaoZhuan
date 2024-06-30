package apk.dispatcher.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Title()
            Spacer(Modifier.width(80.dp))
            ApkSelector(viewModel)
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
        ApkPage(viewModel.currentApk)
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

