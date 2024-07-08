package apk.dispatcher.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.channel.ApiException
import kotlin.reflect.jvm.jvmName

@Composable
fun ErrorPopup(exception: Throwable, onDismiss: () -> Unit) {
    DropdownMenu(true, onDismissRequest = onDismiss) {
        Content(exception)
    }
}


@Composable
private fun Content(exception: Throwable) {
    Column(
        modifier = Modifier.widthIn(min = 200.dp, max = 400.dp)
            .padding(horizontal = 14.dp)
    ) {
        SelectionContainer {
            val message = getErrorMessage(exception)
            Text(text = message, fontSize = 14.sp, color = Color.Red)
        }
    }
}

@Preview
@Composable
fun ErrorPopupPreview() {
    Content(ApiException(400, "获取token", "请检测api key"))
}

private fun getErrorMessage(e: Throwable): String {
    return if (e is ApiException) {
        "${e.action}失败,code: ${e.code},message: ${e.message}"
    } else {
        "${e::class.jvmName}: ${e.message}"
    }
}