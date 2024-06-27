package apk.dispatcher.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes


class DialogShowState : State<Boolean> {
    override var value: Boolean = false
    fun show() {
        value = true
    }

    fun dismiss() {
        value = false
    }
}

@Composable
fun rememberDialogShowState(): DialogShowState {
    return remember { DialogShowState() }
}

@Composable
fun ConfirmDialog(
    message: String,
    title: String = "提示",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = remember { DialogProperties(dismissOnClickOutside = false) }
    ) {
        Column(
            modifier = Modifier
                .width(500.dp)
                .clip(RoundedCornerShape(AppShapes.largeCorner))
                .background(Color.White)
                .padding(20.dp),
        ) {
            Text(title, color = AppColors.fontBlack, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(Modifier.height(14.dp))
            Text(message, color = AppColors.fontGray, fontSize = 14.sp, maxLines = 8, lineHeight = 28.sp)
            Spacer(Modifier.height(18.dp))
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
                        .clickable { onConfirm() }
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


@Preview
@Composable
fun ConfirmDialogPreview() {
    ConfirmDialog(
        title = "Title",
        message = "Some contents...",
    )

}

