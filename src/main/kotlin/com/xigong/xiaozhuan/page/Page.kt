package com.xigong.xiaozhuan.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xigong.xiaozhuan.style.AppColors

@Composable
fun Page(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        content = content,
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.pageBackground)
            .then(modifier)
    )
}