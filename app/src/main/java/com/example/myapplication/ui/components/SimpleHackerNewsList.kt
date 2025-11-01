package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.Story
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SimpleHackerNewsList(
    stories: List<Story>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onStoryClick: (Story) -> Unit,
    onStoryLongPress: (Story) -> Unit = {},
    modifier: Modifier = Modifier
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        when {
            error != null -> {
                SimpleHackerError(
                    message = error,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
            
            stories.isEmpty() && !isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "NO_DATA_FOUND",
                        color = HackerColors.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "TERMINAL_EMPTY",
                        color = HackerColors.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
            
            isLoading -> {
                // 显示加载中的骨架屏，避免空白
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(5) {
                        // 使用简单的骨架屏替代加载指示器
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = HackerColors.Grey
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 模拟标题行
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(16.dp)
                                        .background(
                                            HackerColors.Green.copy(alpha = 0.3f)
                                        )
                                )
                                
                                // 模拟元数据行
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(12.dp)
                                            .background(
                                                HackerColors.Cyan.copy(alpha = 0.3f)
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(12.dp)
                                            .background(
                                                HackerColors.Green.copy(alpha = 0.3f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = stories,
                        key = { it.id }
                    ) { story ->
                        SimpleHackerStoryItem(
                            story = story,
                            onStoryClick = onStoryClick,
                            onLongPress = onStoryLongPress
                        )
                    }
                }
            }
        }
    }
}