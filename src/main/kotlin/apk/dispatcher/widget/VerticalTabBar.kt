package apk.dispatcher.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors

@Composable
fun VerticalTabBar(tabs: List<String>, selectedIndex: Int = 0, tabClick: (index: Int) -> Unit) {
    Column {
        tabs.withIndex().forEach { (index, label) ->
            TabItem(
                title = label,
                selected = index == selectedIndex,
                modifier = Modifier.clickable {
                    tabClick(index)
                }
            )
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    selected: Boolean,
    selectedColor: Color = AppColors.primary,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Divider(
            color = if (selected) selectedColor else Color.Transparent,
            modifier = Modifier
                .size(width = 4.dp, height = 20.dp)
                .clip(RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            title,
            color = if (selected) selectedColor else AppColors.fontGray,
            fontSize = 16.sp
        )

    }
}

@Preview
@Composable
private fun TabItemPreview1() {
    TabItem("安卓", false)
}

@Preview
@Composable
private fun TabItemPreview2() {
    TabItem("苹果", true)
}