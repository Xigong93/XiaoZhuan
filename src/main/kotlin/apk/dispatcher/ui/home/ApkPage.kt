package apk.dispatcher.ui.home

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
import apk.dispatcher.widget.Toast
import apk.dispatcher.widget.TwoPage
import apk.dispatcher.widget.UpdateDescView
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFileChooser.DIRECTORIES_ONLY
import javax.swing.JFileChooser.FILES_ONLY
import javax.swing.filechooser.FileNameExtensionFilter


class ApkPage(private val apkConfig: ApkConfig) : Page(apkConfig.name) {

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
                OutlinedButton(onClick = {
                    if (apkConfig.enableChannel) {
                        showSelectedDirDialog()
                    } else {
                        showSelectedApkDialog()
                    }
                }) {
                    val text = if (apkConfig.enableChannel) "选择Apk文件夹" else "选择Apk文件"
                    Text(text, color = AppColors.fontGray)
                }
                Spacer(Modifier.width(10.dp))
                val apkPath = apkViewModel.getApkDirState().value?.path ?: ""
                Text(apkPath, color = AppColors.fontGray, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(dividerHeight))
        Section("Apk信息") {
            ApkInfoBox()
        }
        Spacer(Modifier.height(dividerHeight))
        Section("更新描述") {
            updateDescView.render()
        }
    }


    private fun showSelectedDirDialog() {
        val chooser = JFileChooser(apkViewModel.getLastApkDir())
        chooser.fileSelectionMode = DIRECTORIES_ONLY;
        val result = chooser.showOpenDialog(null)
        if (result != JFileChooser.APPROVE_OPTION) return
        val dir = chooser.selectedFile ?: return
        if (!apkViewModel.selectedApkDir(dir)) {
            Toast.show("无效目录,未包含有效的Apk文件")
        }
    }

    private fun showSelectedApkDialog() {
        val chooser = JFileChooser(apkViewModel.getLastApkDir())
        chooser.fileSelectionMode = FILES_ONLY;
        chooser.fileFilter = FileNameExtensionFilter("Apk 文件", "apk")
        val result = chooser.showOpenDialog(null)
        if (result != JFileChooser.APPROVE_OPTION) return
        val dir = chooser.selectedFile ?: return
        if (!apkViewModel.selectedApkDir(dir)) {
            Toast.show("解析Apk文件失败")
        }
    }

    @Composable
    private fun ApkInfoBox() {
        val textSize = 14.sp
        Column(
            modifier = Modifier.width(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            val apkInfo = apkViewModel.getApkInfoState().value
            val version = apkInfo?.versionName?.let { "v${it}" }
            val applicationId = apkInfo?.applicationId ?: ""
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
                    "文件大小:",
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
            Spacer(Modifier.height(12.dp))
            Row {
                Text(
                    "包名:",
                    color = AppColors.fontGray,
                    fontSize = textSize,
                    modifier = Modifier.width(70.dp)
                )
                Text(
                    applicationId,
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


    private fun getFileSize(): String {
        val apkInfo = apkViewModel.getApkInfoState().value
        val fileSize = apkInfo?.path?.let { File(it) }?.length() ?: 0
        val mb = fileSize.toFloat() / (1024 * 1024)
        return "%.1fMB".format(mb)
    }

}



