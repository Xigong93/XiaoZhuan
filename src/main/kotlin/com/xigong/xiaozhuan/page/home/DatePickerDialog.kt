package com.xigong.xiaozhuan.page.home

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import java.util.*

/**
 * 日期选择对话框
 */
@Composable
fun DatePickerDialog(title: String, defaultDate: Date, onDismiss: () -> Unit, onDateSelected: (Date) -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = remember { DialogProperties(dismissOnClickOutside = false) }
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(AppShapes.largeCorner))
                .width(IntrinsicSize.Min)
                .background(Color.White)
                .padding(20.dp),
        ) {
            Text(title, color = AppColors.fontBlack, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(Modifier.height(26.dp))
            val controller = remember { defaultController(defaultDate) }
            DatePickerView(controller)
            Spacer(Modifier.height(26.dp))
            Row {
                Spacer(Modifier.weight(1f))
                val hoverSource = remember { MutableInteractionSource() }
                val hovered = hoverSource.collectIsHoveredAsState().value
                val borderColor = if (hovered) AppColors.primary else AppColors.fontGray
                val textColor = if (hovered) AppColors.primary else AppColors.fontBlack
                Row(
                    modifier = Modifier
                        .hoverable(hoverSource)
                        .border(0.5.dp, borderColor, AppShapes.roundButton)
                        .clickable { onDismiss() }
                ) {
                    Text(
                        "取消",
                        color = textColor,
                        letterSpacing = 3.sp,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)

                    )
                }

                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .clip(AppShapes.roundButton)
                        .background(AppColors.primary)
                        .clickable { onDateSelected(controller.selectedDate) }
                ) {
                    Text(
                        "确定",
                        color = Color.White,
                        letterSpacing = 3.sp,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

        }
    }


}

/**
 * 开始时间默认是明天,
 * 结束时间默认是下个月
 * 选中时间默认是三天后的0点
 */
private fun defaultController(selectedTime: Date): DatePickerController {
    val startTime = Date()
    val endTime = {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        calendar.time
    }
    return DatePickerController(startTime, endTime(), selectedTime)
}
