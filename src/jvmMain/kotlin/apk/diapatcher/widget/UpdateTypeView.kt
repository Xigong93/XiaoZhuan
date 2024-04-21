package apk.diapatcher.widget

import apk.diapatcher.style.AppColors
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class UpdateTypeView {

    private val selection = listOf("提示更新", "强制更新")

    val selectedIndex = mutableStateOf(0)

    @Composable
    fun render() {

        Row {
            selection.withIndex().forEach { (index, label) ->
                option(label, selectedIndex.value == index) {
                    selectedIndex.value = index
                }
                Spacer(Modifier.width(12.dp))

            }
        }

    }

    @Composable
    private fun option(label: String, selected: Boolean, onClick: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    onClick()
                }
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = AppColors.primary)
            )
//            Spacer(Modifier.width(2.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
        }

    }
}

@Preview
@Composable
private fun UpdateTypeViewPreview() {
    val updateTypeView = remember { UpdateTypeView() }
    updateTypeView.render()
}