package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun SimpleHackerBackground(
    modifier: Modifier = Modifier
) {
    val hackerGreen = Color(0xFF00FF41)
    val darkGreen = Color(0xFF00A826)
    
    // 创建简单的扫描线效果
    val scanLine by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // 黑色背景
        drawRect(color = HackerColors.Black)
        
        // 扫描线
        val scanLineY = scanLine * size.height
        drawRect(
            color = hackerGreen.copy(alpha = 0.1f),
            topLeft = Offset(0f, scanLineY),
            size = androidx.compose.ui.geometry.Size(size.width, 2f)
        )
        
        // 随机点缀效果
        val time = System.currentTimeMillis() / 1000f
        for (i in 0..20) {
            val x = (time * 50f + i * 100f) % size.width
            val y = ((time * 30f + i * 150f) % size.height)
            
            if (Random.nextFloat() < 0.1f) {
                drawRect(
                    color = hackerGreen.copy(alpha = 0.3f),
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(2f, 2f)
                )
            }
        }
    }
}