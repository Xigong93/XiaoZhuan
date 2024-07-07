package apk.dispatcher.channel

import kotlin.jvm.Throws

class ApiException(
    val code: Int,
    val action: String,
    override val message: String
) : RuntimeException("执行${action}失败，原因:${message}")


@Throws(ApiException::class)
fun checkApiSuccess(code: Int, successCode: Int, action: String, message: String) {
    if (code != successCode) {
        throw ApiException(code, action, message)
    }
}