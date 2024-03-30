import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun App() {
//    val selectedTab by remember { mutableStateOf(0) }
    val appWindow = remember { AppWindow() }
    appWindow.render()

}

private class AppWindow {
    val selectedTab = mutableStateOf(0)

    val pages = listOf(
        AndroidPage(),
        ApplePage(),
        OfficialWebsitePage()
    )

    val titles = pages.map { it.title }

    @Composable
    fun render() {
        Column(Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Title()
                Spacer(Modifier.width(100.dp))
                TabBar(titles, selectedTab.value) {
                    selectedTab.value = it
                }
            }
            Divider()
            pages[selectedTab.value].render()
        }
    }
}

@Composable
private fun Title() {
    Text(
        "软件版本更新",
        color = Color.Black,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

