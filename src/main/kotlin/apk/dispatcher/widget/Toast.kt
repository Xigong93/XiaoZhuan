package apk.dispatcher.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

object Toast {

    private var job: Job? = null

    private var message by mutableStateOf("")

    private var show by mutableStateOf(false)

    private val mainScope = CoroutineScope(EmptyCoroutineContext)


    @Composable
    fun install() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedVisibility(
                visible = show,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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


        }

    }

    fun show(message: String) {
        this.message = message
        show = true
        if (job?.isActive == true) {
            job?.cancel()
        }
        job = null
        job = mainScope.launch {
            delay(2000)
            show = false
        }
    }
}