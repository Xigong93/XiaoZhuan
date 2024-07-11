package com.xigong.xiaozhuan.util

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
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

    companion object : FileSelector by FileKitSelector

}

/**
 * 使用Swing内置的JFileChooser 实现的文件选择器
 */
private object JFileSelector : FileSelector {
    override suspend fun selectedDir(defaultDir: File?): File? {
        return JFileChooser(defaultDir).apply {
            fileSelectionMode = DIRECTORIES_ONLY
        }.awaitSelectedFile()
    }

    override suspend fun selectedFile(
        defaultFile: File?, desc: String?, extensions: List<String>
    ): File? {
        require(extensions.isNotEmpty()) { "文件扩展名不能为空" }
        return JFileChooser(defaultFile).apply {
            fileSelectionMode = FILES_ONLY
            fileFilter = FileNameExtensionFilter(desc, * extensions.toTypedArray())
        }.awaitSelectedFile()
    }

    private suspend fun JFileChooser.awaitSelectedFile(): File? = withContext(Dispatchers.IO) {
        val result = showOpenDialog(getWindow())
        selectedFile?.takeIf { result == APPROVE_OPTION }
    }

}

private fun getWindow(): Window? {
    return Window.getWindows().firstOrNull()
}


/**
 * 开源的FileKit 实现的文件选择器
 */
private object FileKitSelector : FileSelector {
    override suspend fun selectedDir(defaultDir: File?): File? {
        check(FileKit.isDirectoryPickerSupported()) { "当前平台不支持选择目录" }
        return FileKit.pickDirectory(
            initialDirectory = defaultDir?.absolutePath,
            platformSettings = FileKitPlatformSettings(getWindow())
        )?.file
    }

    override suspend fun selectedFile(defaultFile: File?, desc: String?, extensions: List<String>): File? {
        return FileKit.pickFile(
            mode = PickerMode.Single,
            type = PickerType.File(extensions),
            initialDirectory = defaultFile?.absolutePath,
            platformSettings = FileKitPlatformSettings(getWindow())
        )?.file
    }

}