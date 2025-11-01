package com.example.myapplication.ui.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HackerWebViewScreen(
    url: String,
    title: String,
    onBackClick: () -> Unit,
    onShareClick: (String) -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HackerColors.Black)
    ) {
        // Hacker 风格顶部栏
        TerminalTopBar(
            title = title,
            onBackClick = onBackClick,
            onShareClick = { onShareClick(url) },
            onRefreshClick = {
                webView?.reload()
                onRefreshClick()
            },
            canGoBack = canGoBack,
            canGoForward = canGoForward,
            onGoBack = { webView?.goBack() },
            onGoForward = { webView?.goForward() }
        )
        
        // 进度条
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = HackerColors.Green,
                trackColor = HackerColors.Grey
            )
        }
        
        // WebView
        TerminalWebView(
            url = url,
            isLoading = { isLoading = it },
            canGoBack = { canGoBack = it },
            canGoForward = { canGoForward = it },
            onWebViewCreated = { webView = it },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TerminalTopBar(
    title: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onRefreshClick: () -> Unit,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(HackerColors.Grey),
        colors = CardDefaults.cardColors(
            containerColor = HackerColors.Grey
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧按钮组
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 返回按钮
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
                
                // WebView 导航按钮
                IconButton(
                    onClick = onGoBack,
                    enabled = canGoBack,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "WebView Back",
                        tint = if (canGoBack) HackerColors.Green else HackerColors.Grey,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onGoForward,
                    enabled = canGoForward,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "WebView Forward",
                        tint = if (canGoForward) HackerColors.Green else HackerColors.Grey,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // 中间标题
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WEB_BROWSER",
                    color = HackerColors.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title.take(30) + if (title.length > 30) "..." else "",
                    color = HackerColors.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
            
            // 右侧按钮组
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 刷新按钮
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = HackerColors.Cyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // 分享按钮
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = HackerColors.Green,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun TerminalWebView(
    url: String,
    isLoading: (Boolean) -> Unit,
    canGoBack: (Boolean) -> Unit,
    canGoForward: (Boolean) -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                        isLoading(true)
                        super.onPageStarted(view, url, favicon)
                    }
                    
                    override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading(false)
                        super.onPageFinished(view, url)
                    }
                }
                
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        isLoading(newProgress < 100)
                    }
                }
                
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                
                // 应用 Hacker 风格样式
                setBackgroundColor(HackerColors.Black.hashCode())
                
                onWebViewCreated(this)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
        modifier = modifier
    )
}