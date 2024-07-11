package com.xigong.xiaozhuan.channel

import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.util.ApkInfo
import com.xigong.xiaozhuan.util.getApkInfo
import java.io.File
import kotlin.jvm.Throws

abstract class ChannelTask {

    abstract val channelName: String


    private var submitStateListener: SubmitStateListener? = null

    /**
     * 声明需要的参数
     */
    protected abstract val paramDefine: List<Param>

    /**
     * 文件名标识
     */
    abstract val fileNameIdentify: String


    /**
     * 初始化参数
     */
    abstract fun init(params: Map<Param, String?>)

    /**
     * 添加监听器
     */
    fun setSubmitStateListener(listener: SubmitStateListener) {
        this.submitStateListener = listener
    }

    fun getParams(): List<Param> {
        val fileName = Param(FILE_NAME_IDENTIFY, fileNameIdentify, "文件名标识,不区分大小写")
        return paramDefine + fileName
    }

    @kotlin.jvm.Throws
    suspend fun startUpload(apkFile: File, updateDesc: String) {
        AppLogger.info(channelName, "开始提交新版本")
        val listener = submitStateListener
        try {
            val apkInfo = getApkInfo(apkFile)
            AppLogger.info(channelName, "准备提交Apk信息:$apkInfo")
            listener?.onStart()
            listener?.onProcessing("请求中")
            performUpload(apkFile, apkInfo, updateDesc, ::notifyProgress)
            listener?.onSuccess()
            AppLogger.info(channelName, "提交新版本成功,$apkInfo")
        } catch (e: Throwable) {
            AppLogger.error(channelName, "提交新版本失败", e)
            listener?.onError(e)
        }
    }


    private fun notifyProgress(progress: Int) {
        val listener = submitStateListener
        if (progress == 100) {
            listener?.onProcessing("提交中")
        } else {
            listener?.onProgress(progress)
        }
    }

    /**
     * 执行结束，表示上传成功，抛出异常代表出错
     */
    @Throws
    abstract suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit)


    /**
     * 获取APP应用市场状态
     * @param applicationId 包名
     */
    @Throws
    abstract suspend fun getMarketState(applicationId: String): MarketInfo

    /**
     * 声明需要的参数
     */
    data class Param(

        /** 参数名称，如api_key */
        val name: String,

        /** 默认参数 */
        val defaultValue: String? = null,

        /** 参数的描述，可为空 */
        val desc: String? = null,
        /**
         * 参数类型
         */
        val type: ParmaType = ParmaType.Text
    )

    /**
     * 参数类型
     */
    sealed class ParmaType {
        /**
         * 文本类型
         */
        data object Text : ParmaType()

        /**
         * 纯文字类型的文件
         * @param fileExtension 允许的文件扩展名
         */
        data class TextFile(val fileExtension: String) : ParmaType()
    }


    interface SubmitStateListener {

        fun onStart()

        fun onProcessing(action: String)

        /**
         * 取值范围0到100
         */
        fun onProgress(progress: Int)

        fun onSuccess()

        fun onError(exception: Throwable)
    }

    companion object {
        const val FILE_NAME_IDENTIFY = "fileNameIdentify"

    }
}