package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.Story

// 简化的黑客风格颜色
object HackerColors {
    val Green = Color(0xFF00FF41)
    val DarkGreen = Color(0xFF00A826)
    val Black = Color(0xFF0A0A0A)
    val Grey = Color(0xFF1A1A1A)
    val White = Color(0xFFE0E0E0)
    val Cyan = Color(0xFF00D4FF)
    val Red = Color(0xFFFF5555)
}

// 简化的黑客风格新闻项
@Composable
fun SimpleHackerStoryItem(
    story: Story,
    onStoryClick: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    // 闪烁动画
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(HackerColors.Grey)
            .clickable { onStoryClick(story) },
        colors = CardDefaults.cardColors(
            containerColor = HackerColors.Grey
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HackerColors.Black,
                            HackerColors.Grey,
                            HackerColors.Black
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // 终端标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "HACKER_NEWS",
                    color = HackerColors.Green,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "[${story.id}]",
                    color = HackerColors.Green.copy(alpha = alpha),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 命令提示符和标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ ",
                    color = HackerColors.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = story.title,
                    color = HackerColors.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 元数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${story.author} @ ${story.getTimeAgo()}",
                    color = HackerColors.Cyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Text(
                    text = "+${story.score}",
                    color = HackerColors.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 域名和评论
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                story.getDomain()?.let { domain ->
                    Text(
                        text = "($domain)",
                        color = HackerColors.Green.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
                if (story.descendants > 0) {
                    Text(
                        text = "<${story.descendants}> comments",
                        color = HackerColors.White.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

// 简化的黑客风格加载指示器
@Composable
fun SimpleHackerLoading(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = HackerColors.Grey
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "HACKER_NEWS_TERMINAL",
                color = HackerColors.Green,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            // 进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(HackerColors.Black)
                    .clip(RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(HackerColors.Green)
                )
            }
            
            Text(
                text = "LOADING_DATA...",
                color = HackerColors.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }
    }
}

// 简化的黑客风格错误消息
@Composable
fun SimpleHackerError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = HackerColors.Grey
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SYSTEM_ERROR",
                color = HackerColors.Red,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Text(
                text = message,
                color = HackerColors.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HackerColors.Green,
                    contentColor = HackerColors.Black
                )
            ) {
                Text(
                    text = "[RETRY]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}