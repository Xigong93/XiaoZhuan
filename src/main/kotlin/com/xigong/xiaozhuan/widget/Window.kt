package com.xigong.xiaozhuan.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Window {

    private val frames = mutableListOf<Frame>()

    fun add(frame: Frame) {
        frames.add(frame)
    }

    fun remove(frame: Frame) {
        frames.remove(frame)
    }

    @Composable
    fun render() {
        Box(modifier = Modifier.fillMaxSize()) {
            frames.sortBy { it.zIndex }
            frames.forEach { it.content() }
        }
    }
}


class Frame(
    val zIndex: Int = 0,
    val content: @Composable () -> Unit
)
