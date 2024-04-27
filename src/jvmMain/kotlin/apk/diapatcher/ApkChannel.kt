package apk.diapatcher

import java.io.File

abstract class ApkChannel {

    abstract val channelName: String

    private var listener: StateListener? = null

    /**
     * 声明需要的参数
     */
    protected abstract val paramDefine: List<Param>

    /**
     * 文件名标识
     */
    protected abstract val fileNameIdentify: String

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

    fun startUpload(file: File) {
        try {
            listener?.onStart()
            performUpload(file) { listener?.onProgress(it) }
            listener?.onSuccess()
        } catch (e: Exception) {
            listener?.onError(e)
        }
    }

    /**
     * 执行结束，表示上传成功，抛出异常代表出错
     */
    @kotlin.jvm.Throws
    abstract fun performUpload(file: File, progress: (Int) -> Unit)


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
         * 期望的参数长度，用来指定输入框的大小
         */
        val exceptLength: Int = 100
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