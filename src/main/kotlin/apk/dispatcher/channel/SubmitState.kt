package apk.dispatcher.channel

/**
 * 提交状态
 */
sealed class SubmitState {
    data object Waiting : SubmitState()
    data class Processing(val action: String) : SubmitState()

    /**
     * @param progress 取值范围[0,100]
     */
    data class Uploading(val progress: Int) : SubmitState()
    data object Success : SubmitState()
    data class Error(val message: String) : SubmitState()

    val finish: Boolean get() = this == Success || this is Error
    val success: Boolean get() = this == Success
}