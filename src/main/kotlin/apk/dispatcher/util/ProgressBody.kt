package apk.dispatcher.util

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.IOException

/**
 * 取值范围[0,1]
 */
typealias ProgressChange = (progress: Float) -> Unit

class ProgressBody(
    private val mediaType: MediaType,
    private val file: File,
    private val progressChange: ProgressChange
) : RequestBody() {
    override fun contentType(): MediaType {
        return mediaType
    }

    override fun contentLength(): Long {
        return file.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val length = contentLength()
        require(length != 0L) { "contentLength can't be zero!" }
        file.source().use {
            var total: Long = 0
            var read: Long
            while (it.read(sink.buffer, SEGMENT_SIZE.toLong()).also { read = it } != -1L) {
                total += read
                sink.flush()
                val percent = (total * 1.0f / length).coerceIn(0f, 1f)
                progressChange(percent)
            }
        }
    }


    companion object {
        private const val SEGMENT_SIZE = 2048 // okio.Segment.SIZE
    }
}