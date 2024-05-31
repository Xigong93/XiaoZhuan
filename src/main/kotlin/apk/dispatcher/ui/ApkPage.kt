package apk.dispatcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.ApkConfig
import apk.dispatcher.style.AppColors
import apk.dispatcher.widget.Section
import apk.dispatcher.widget.TwoPage
import apk.dispatcher.widget.UpdateDescView
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFileChooser.DIRECTORIES_ONLY


class ApkPage(apkConfig: ApkConfig) : Page(apkConfig.name) {

    private val apkViewModel = ApkViewModel(apkConfig)

    private val updateDescView = UpdateDescView(apkViewModel.updateDesc)

    private val channelGroupPage = ChannelGroupPage(apkViewModel)

    @Composable
    override fun render() {
        TwoPage(
            leftPage = { LeftPage() },
            rightPage = { RightPage() },
        )
    }

    @Composable
    private fun ColumnScope.LeftPage() {
        val dividerHeight = 30.dp
        Section("操作") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { selectFile() }) {
                    Text("选择Apk文件夹", color = AppColors.fontGray)
                }
                Spacer(Modifier.width(10.dp))
                val apkPath = apkViewModel.getApkDirState().value?.path ?: ""
                Text(apkPath, color = AppColors.fontGray, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(dividerHeight))
        Section("版本信息") {
            VersionCodeBox()
        }
        Spacer(Modifier.height(dividerHeight))
        Section("更新描述") {
            updateDescView.render()
        }
    }

    private fun selectFile() {
//        val frame = JFrame()
//        val fileDialog = FileDialog(frame, "选择Apk文件夹", FileDialog.LOAD)
//        fileDialog.isVisible = true
//        val dir = fileDialog.directory
//        selectedApkDir.value = dir ?: ""
        // 创建 JFrame 实例

        val chooser = JFileChooser()
        chooser.fileSelectionMode = DIRECTORIES_ONLY;
        chooser.showOpenDialog(null)
        val selectedFile = chooser.selectedFile
        if (selectedFile != null) {
            apkViewModel.selectedApkDir(selectedFile)
        }

    }

    @Composable
    private fun VersionCodeBox() {
        val textSize = 14.sp
        Column(
            modifier = Modifier.width(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            val apkInfo = apkViewModel.getApkInfoState().value
            val version = apkInfo?.versionName?.let { "v${it}" }
            Row {
                Text(
                    "版本号:",
                    color = AppColors.fontGray,
                    fontSize = textSize,
                    modifier = Modifier.width(70.dp)
                )
                Text(
                    version ?: "",
                    color = AppColors.fontBlack,
                    fontSize = textSize
                )
            }
            Spacer(Modifier.height(12.dp))
            Row {
                Text(
                    "Apk大小:",
                    color = AppColors.fontGray,
                    fontSize = textSize,
                    modifier = Modifier.width(70.dp)
                )
                Text(
                    getFileSize(),
                    color = AppColors.fontBlack,
                    fontSize = textSize
                )
            }
        }
    }

    @Composable
    private fun ColumnScope.RightPage() {
        channelGroupPage.render()
    }


    fun getFileSize(): String {
        val apkInfo = apkViewModel.getApkInfoState().value
        val fileSize = apkInfo?.path?.let { File(it) }?.length() ?: 0
        val mb = fileSize.toFloat() / (1024 * 1024)
        return "%.1fMB".format(mb)
    }

}



