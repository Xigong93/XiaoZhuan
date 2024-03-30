import androidx.compose.runtime.Composable

abstract class Page(val title: String) {

    @Composable
    abstract fun render()
}