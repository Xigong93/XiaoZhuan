package apk.dispatcher.page.home

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
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
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes


@Composable
fun ApkSelector(viewModel: HomePageVM) {
    var showApkMenu by remember { mutableStateOf(false) }
    Column {
        val width = 180.dp
        val source = remember { MutableInteractionSource() }
        val hovered = source.collectIsHoveredAsState().value
        val textColor = if (hovered || showApkMenu) AppColors.primary else AppColors.fontBlack
        val borderColor = if (hovered || showApkMenu) AppColors.primary else AppColors.border
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clip(AppShapes.roundButton)
                .width(width)
                .hoverable(source)
                .border(1.dp, borderColor, AppShapes.roundButton)
                .clickable {
                    showApkMenu = true
                }
                .padding(12.dp)
        ) {
            Text(viewModel.currentApk?.name ?: "", fontSize = 14.sp, color = textColor)
            Spacer(Modifier.weight(1f))
            Image(
                painterResource("arrow_down.png"),
                contentDescription = null,
                colorFilter = ColorFilter.tint(AppColors.border),
                modifier = Modifier.size(16.dp)
            )

        }
        if (showApkMenu) {
            DropdownMenu(
                true,
                onDismissRequest = {
                    showApkMenu = false
                }, modifier = Modifier.width(width)
                    .padding(horizontal = 8.dp)
                    .heightIn(max = 400.dp)
            ) {
                viewModel.apkList.forEach { apk ->
                    val background = if (apk == viewModel.currentApk) AppColors.auxiliary else Color.Transparent
                    item(apk.name, modifier = Modifier.background(background)) {
                        viewModel.currentApk = apk
                        showApkMenu = false
                    }
                }
            }
        }
    }
}

@Composable
private fun item(
    title: String,
    color: Color = AppColors.fontBlack,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier
        .clip(AppShapes.roundButton)
        .clickable { onClick() }
        .then(modifier)
        .fillMaxWidth()
        .padding(vertical = 14.dp, horizontal = 12.dp)

    ) {
        Text(
            text = title,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
        )
    }
}