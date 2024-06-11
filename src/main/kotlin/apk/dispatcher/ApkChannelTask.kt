package apk.dispatcher

import apk.dispatcher.util.defaultLogger
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

abstract class ApkChannelTask {

    abstract val channelName: String


    private var listener: StateListener? = null

    /**
     * 声明需要的参数
     */
    protected abstract val paramDefine: List<Param>

    /**
     * 文件名标识
     */
    abstract val fileNameIdentify: String


    private val logger = defaultLogger

    /**
     * 初始化参数
     */
    abstract fun init(params: Map<Param, String?>)

    /**
     * 添加监听器
     */
    fun setListener(listener: StateListener) {
        this.listener = listener
    }

    fun getParams(): List<Param> {
        val fileName = Param(FILE_NAME_IDENTIFY, fileNameIdentify, "文件名标识,不区分大小写", 10)
        return paramDefine + fileName
    }

    @kotlin.jvm.Throws
    suspend fun startUpload(apkFile: File, updateDesc: String) {
        logger.info("开始上传")
        try {
            listener?.onStart()
            performUpload(apkFile, updateDesc) {
                listener?.onProgress(it)
            }
            listener?.onSuccess()
            logger.info("上传成功")
        } catch (e: Exception) {
            logger.log(Level.INFO, "上传失败", e)
            listener?.onError(e)
        }

    }


    /**
     * 执行结束，表示上传成功，抛出异常代表出错
     */
    @kotlin.jvm.Throws
    abstract suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit)


    /**
     * 声明需要的参数
     */
    data class Param(

        /**
         * 参数名称，如api_key
         */
        val name: String,

        /**
         * 默认参数
         */
        val defaultValue: String? = null,

        /**
         * 参数的描述，可为空
         */
        val desc: String? = null,

        /**
         * 期望行数，默认1行
         */
        val exceptLines: Int = 1
    )


    interface StateListener {

        fun onStart()

        /**
         * 取值范围0到100
         */
        fun onProgress(progress: Int)

        fun onSuccess()

        fun onError(exception: Exception)
    }

    companion object {
        const val FILE_NAME_IDENTIFY = "fileNameIdentify"
    }
}