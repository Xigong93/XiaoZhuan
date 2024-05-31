package apk.dispatcher.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Toast {

    private val messageState = mutableStateOf("")

    @Composable
    fun install() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            val message = messageState.value
            val scope = rememberCoroutineScope()
            val showToast = message.isNotEmpty()
            AnimatedVisibility(showToast) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xC0000000))
                        .sizeIn(minWidth = 80.dp, maxWidth = 300.dp)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        message,
                        fontSize = 15.sp,
                        color = Color.White,
                        maxLines = 2
                    )
                }
            }
            if (showToast) {
                scope.launch {
                    delay(1000)
                    messageState.value = ""
                }
            }

        }

    }

    fun show(message: String) {
        messageState.value = message

    }
}