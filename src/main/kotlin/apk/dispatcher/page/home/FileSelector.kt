package apk.dispatcher.page.home

import apk.dispatcher.widget.Toast
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.pickFile
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFileChooser.DIRECTORIES_ONLY
import javax.swing.JFileChooser.FILES_ONLY
import javax.swing.filechooser.FileNameExtensionFilter

private fun showSelectedDirDialog(apkViewModel: ApkViewModel): File? {
    val chooser = JFileChooser(apkViewModel.getLastApkDir())
    chooser.fileSelectionMode = DIRECTORIES_ONLY;
    val result = chooser.showOpenDialog(null)
    if (result != JFileChooser.APPROVE_OPTION) return null
    return chooser.selectedFile
}

fun showSelectedDirDialog2(apkViewModel: ApkViewModel) {
    apkViewModel.mainScope.launch {
        val dir = if (FileKit.isDirectoryPickerSupported()) {
            FileKit.pickDirectory("请选择Apk目录", apkViewModel.getLastApkDir()?.absolutePath)?.file
        } else {
            showSelectedDirDialog(apkViewModel)
        }
        if (dir != null && !apkViewModel.selectedApkDir(dir)) {
            Toast.show("无效目录,未包含有效的Apk文件")
        }
    }
}

private fun showSelectedApkDialog(apkViewModel: ApkViewModel) {
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

fun showSelectedApkDialog2(apkViewModel: ApkViewModel) {
    apkViewModel.mainScope.launch {
        val dir = FileKit.pickFile(
            PickerType.File(listOf("apk")),
            "请选择Apk目录",
            apkViewModel.getLastApkDir()?.absolutePath
        )
        if (dir != null && !apkViewModel.selectedApkDir(dir.file)) {
            Toast.show("未包含有效的Apk文件")
        }
    }
}
