package apk.dispatcher.channel

import kotlin.jvm.Throws

class ApiException(
    code: Int,
    action: String,
    message: String
) : RuntimeException() {
    override val message = "${action}失败，code:$code，message:$message"
}


@Throws(ApiException::class)
fun checkApiSuccess(code: Int, successCode: Int, action: String, message: String) {
    if (code != successCode) {
        throw ApiException(code, action, message)
    }
}