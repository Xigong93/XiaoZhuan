package com.xigong.xiaozhuan.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xigong.xiaozhuan.page.splash.SplashPage
import com.xigong.xiaozhuan.page.version.NewVersionDialog
import com.xigong.xiaozhuan.style.AppColors

@Composable
fun RootWindow(
    closeClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(AppColors.pageBackground),
    ) {
        content()
        SplashPage()
        NewVersionDialog()
        Toast.UI()
    }
}
