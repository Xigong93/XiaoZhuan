package apk.diapatcher.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
   Column {
       Text(
           title,
           color = Color.Black,
           fontSize = 16.sp,
           fontWeight = FontWeight.Bold
       )
       Spacer(Modifier.height(12.dp))
       Column {
           content()
       }
   }
}