package apk.diapatcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.diapatcher.style.AppColors

class EditMenu() {
    var eventListener: EventListener? = null


    @Composable
    fun render() {

        Column(
            modifier = Modifier.width(180.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(
                    1.dp, AppColors.pageBackground,
                    shape = RoundedCornerShape(6.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item("新增") {}
            Divider()
            item("编辑") {}
            Divider()
            item("删除", color = Color.Red) {}
        }

    }

    @Composable
    fun item(title: String, color: Color = AppColors.fontBlack, onClick: () -> Unit) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 20.dp)) {
            Text(
                text = title,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    interface EventListener {
        fun onAddClick();
        fun onEditClick()
        fun onDeleteClick()
    }
}

@Preview
@Composable
fun editMenuPreview() {
    EditMenu().render()
}