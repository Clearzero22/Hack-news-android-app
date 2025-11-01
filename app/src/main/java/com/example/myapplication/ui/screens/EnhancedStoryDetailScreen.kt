package com.example.myapplication.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Story
import com.example.myapplication.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedStoryDetailScreen(
    storyId: Int,
    onBackClick: () -> Unit,
    onOpenUrl: (String) -> Unit,
    viewModel: StoryDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(storyId) {
        viewModel.loadStory(storyId)
    }
    
    // 如果有 WebView URL，显示 WebView
    uiState.webViewUrl?.let { url ->
        val story = uiState.story
        HackerWebViewScreen(
            url = url,
            title = story?.title ?: "Loading...",
            onBackClick = { viewModel.closeWebView() },
            onShareClick = { sharedText ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, sharedText)
                }
                context.startActivity(Intent.createChooser(intent, "Share Story"))
            },
            onRefreshClick = { viewModel.loadStory(storyId) }
        )
        return
    }
    
    // 否则显示详情页面
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Hacker 风格顶部栏
        TerminalTopAppBar(
            title = "STORY_DETAILS",
            onBackClick = onBackClick,
            actions = {
                IconButton(onClick = {
                    val shareText = viewModel.shareStory()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Story"))
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = HackerColors.Green
                    )
                }
            }
        )
        
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleHackerLoading(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleHackerError(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadStory(storyId) },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            uiState.story != null -> {
                StoryDetailContent(
                    story = uiState.story!!,
                    onOpenUrl = { url ->
                        // 优先打开 WebView，如果没有 URL 则回退到外部浏览器
                        if (url.isNotEmpty()) {
                            viewModel.openWebView()
                        }
                    },
                    onOpenInBrowser = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun TerminalTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = HackerColors.Grey
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = HackerColors.Green,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = title,
                    color = HackerColors.Green,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row {
                actions()
            }
        }
    }
}

@Composable
private fun StoryDetailContent(
    story: Story,
    onOpenUrl: (String) -> Unit,
    onOpenInBrowser: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 主要信息卡片
        item {
            SimpleHackerStoryItem(
                story = story,
                onStoryClick = { 
                    story.url?.let { onOpenUrl(it) }
                }
            )
        }
        
        // 详细信息卡片
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = HackerColors.Grey
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "SYSTEM_INFO",
                        color = HackerColors.Green,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    InfoRow("STORY_ID", story.id.toString())
                    InfoRow("AUTHOR", story.author)
                    InfoRow("TIMESTAMP", "${story.getTimeAgo()}")
                    InfoRow("SCORE", "+${story.score}")
                    InfoRow("COMMENTS", "${story.descendants}")
                    
                    story.getDomain()?.let { domain ->
                        InfoRow("DOMAIN", domain)
                    }
                }
            }
        }
        
        // 操作按钮
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 在应用内打开
                if (story.url != null) {
                    Button(
                        onClick = { onOpenUrl(story.url!!) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HackerColors.Green,
                            contentColor = HackerColors.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[OPEN_IN_APP]",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 在外部浏览器打开
                if (story.url != null) {
                    OutlinedButton(
                        onClick = { onOpenInBrowser(story.url!!) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HackerColors.Cyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, HackerColors.Cyan
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[OPEN_IN_BROWSER]",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
        
        // 故事内容（如果有文本内容）
        story.text?.let { text ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = HackerColors.Grey
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "STORY_CONTENT",
                            color = HackerColors.Green,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = text,
                            color = HackerColors.White,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            color = HackerColors.Cyan,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = HackerColors.White,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}