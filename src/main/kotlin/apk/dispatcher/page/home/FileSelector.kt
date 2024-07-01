package apk.dispatcher.page.home

import apk.dispatcher.widget.Toast
import javax.swing.JFileChooser
import javax.swing.JFileChooser.DIRECTORIES_ONLY
import javax.swing.JFileChooser.FILES_ONLY
import javax.swing.filechooser.FileNameExtensionFilter

fun showSelectedDirDialog(apkViewModel: ApkViewModel) {
    val chooser = JFileChooser(apkViewModel.getLastApkDir())
    chooser.fileSelectionMode = DIRECTORIES_ONLY;
    val result = chooser.showOpenDialog(null)
    if (result != JFileChooser.APPROVE_OPTION) return
    val dir = chooser.selectedFile ?: return
    if (!apkViewModel.selectedApkDir(dir)) {
        Toast.show("无效目录,未包含有效的Apk文件")
    }
}


fun showSelectedApkDialog(apkViewModel: ApkViewModel) {
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

