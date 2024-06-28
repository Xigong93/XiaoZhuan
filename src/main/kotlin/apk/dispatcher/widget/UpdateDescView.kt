package apk.dispatcher.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import apk.dispatcher.style.AppColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UpdateDescView(updateDesc: MutableState<String>) {
    val textSize = 14.sp
    val interactionSource = remember { MutableInteractionSource() }
    val clearVisible by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .hoverable(interactionSource)

    ) {
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = updateDesc.value,
            placeholder = {
                Text(
                    "请填写更新描述",
                    color = AppColors.fontGray,
                    fontSize = textSize
                )
            },
            onValueChange = { updateDesc.value = it },
            textStyle = TextStyle(fontSize = textSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.primary,
                backgroundColor = Color.White
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .width(300.dp)
                .height(120.dp)
        )


        AnimatedVisibility(
            clearVisible && updateDesc.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {

            Image(painter = painterResource("input_clear.png"),
                contentDescription = "清空",
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .size(22.dp)
                    .clickable {
                        updateDesc.value = ""
                        focusRequester.requestFocus()
                    }
            )
        }

    }
}