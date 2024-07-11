package com.xigong.xiaozhuan.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


suspend fun OkHttpClient.getJsonResult(request: Request): JsonObject = withContext(Dispatchers.IO) {
    val text = getTextResult(request)
    JsonParser.parseString(text).asJsonObject
}

suspend fun OkHttpClient.getTextResult(request: Request): String = withContext(Dispatchers.IO) {
    val response = newCall(request).execute()
    check(response.isSuccessful)
    checkNotNull(response.body).string()
}