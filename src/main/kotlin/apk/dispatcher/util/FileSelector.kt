package apk.dispatcher.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Window
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFileChooser.*
import javax.swing.filechooser.FileNameExtensionFilter

interface FileSelector {


    /**
     * 选择目录
     * @param defaultDir 默认打开的文件夹
     */
    suspend fun selectedDir(defaultDir: File? = null): File?

    /**
     * 选择文件
     * @param defaultFile 默认选中的文件夹
     * @param desc 描述
     * @param extensions 文件名扩展名,不可为空
     */
    suspend fun selectedFile(defaultFile: File? = null, desc: String?, extensions: List<String>): File?

    companion object : FileSelector by JFileSelector

}

private object JFileSelector : FileSelector {
    override suspend fun selectedDir(defaultDir: File?): File? = withContext(Dispatchers.IO) {
        JFileChooser(defaultDir).apply {
            fileSelectionMode = DIRECTORIES_ONLY
        }.getUserSelectedFile()
    }

    override suspend fun selectedFile(
        defaultFile: File?, desc: String?, extensions: List<String>
    ): File? = withContext(Dispatchers.IO) {
        require(extensions.isNotEmpty()) { "文件扩展名不能为空" }
        JFileChooser(defaultFile).apply {
            fileSelectionMode = FILES_ONLY
            fileFilter = FileNameExtensionFilter(desc, * extensions.toTypedArray())
        }.getUserSelectedFile()
    }

    private fun JFileChooser.getUserSelectedFile(): File? {
        val result = showOpenDialog(getWindow())
        return selectedFile?.takeIf { result == APPROVE_OPTION }
    }

}

private fun getWindow(): Window? {
    return Window.getWindows().firstOrNull()
}