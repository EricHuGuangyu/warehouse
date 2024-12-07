package com.example.warehouse.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.warehouse.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToMain: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(Color.White.value)), // 启动背景色
        contentAlignment = Alignment.Center
    ) {
        // 显示应用的 Logo 或动画
        Image(
            painter = painterResource(id = R.drawable.ic_logo), // 替换为你的资源
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )
    }

    // 在延迟后调用导航回调
    LaunchedEffect(Unit) {
        delay(1000) // 延迟 2 秒
        onNavigateToMain()
    }
}