package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Favorite
import com.example.myapplication.ui.theme.HackerColors
import com.example.myapplication.ui.viewmodels.FavoriteViewModel
import com.example.myapplication.utils.CsvExporter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onStoryClick: (Favorite) -> Unit,
    viewModel: FavoriteViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // 导出状态
    var isExporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HackerColors.Black)
    ) {
        Column {
            // TopAppBar
            TopAppBar(
                title = { 
                    Text(
                        "FAVORITES",
                        color = HackerColors.Green,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = HackerColors.Green
                        )
                    }
                },
                actions = {
                    if (uiState.favorites.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                isExporting = true
                                CsvExporter.exportFavoritesToCsv(
                                    context = context,
                                    favorites = uiState.favorites,
                                    onSuccess = { csvFile ->
                                        isExporting = false
                                        exportMessage = "Export successful: ${csvFile.name}"
                                        // 自动分享导出的文件
                                        CsvExporter.shareCsvFile(context, csvFile)
                                    },
                                    onError = { error ->
                                        isExporting = false
                                        exportMessage = error
                                    }
                                )
                            },
                            enabled = !isExporting
                        ) {
                            if (isExporting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = HackerColors.Green,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Export",
                                    tint = HackerColors.Green
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HackerColors.Grey
                )
            )
            
            // Content
            when {
                uiState.isLoading -> {
                    LoadingFavorites()
                }
                uiState.favorites.isEmpty() -> {
                    EmptyFavorites()
                }
                else -> {
                    FavoritesList(
                        favorites = uiState.favorites,
                        onStoryClick = onStoryClick,
                        onDeleteClick = { favorite ->
                            viewModel.deleteFavorite(favorite)
                        }
                    )
                }
            }
        }
        
        // Error Message
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // TODO: Show error message
                viewModel.clearError()
            }
        }
        
        // Export Message Snackbar
        exportMessage?.let { message ->
            LaunchedEffect(message) {
                // 这里可以显示 Toast 或 Snackbar
                kotlinx.coroutines.delay(3000)
                exportMessage = null
            }
        }
        
        // 导出进度提示
        if (isExporting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HackerColors.Grey
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = HackerColors.Green,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Exporting favorites...",
                            color = HackerColors.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingFavorites() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = HackerColors.Green,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Loading favorites...",
                color = HackerColors.Green,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun EmptyFavorites() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = "No favorites",
                tint = HackerColors.DarkGreen,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No favorites yet",
                color = HackerColors.Green,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Add stories to favorites to see them here",
                color = HackerColors.DarkGreen,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun FavoritesList(
    favorites: List<Favorite>,
    onStoryClick: (Favorite) -> Unit,
    onDeleteClick: (Favorite) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(favorites, key = { it.id }) { favorite ->
            FavoriteItem(
                favorite = favorite,
                onClick = { onStoryClick(favorite) },
                onDelete = { onDeleteClick(favorite) }
            )
        }
    }
}

@Composable
fun FavoriteItem(
    favorite: Favorite,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val favoritedDate = dateFormat.format(Date(favorite.favoriteTime))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
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
                .padding(16.dp)
        ) {
            // Header with title and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "> ${favorite.title}",
                        color = HackerColors.Green,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Metadata
                    Text(
                        text = "user@${favorite.author}",
                        color = HackerColors.DarkGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    Text(
                        text = "[+${favorite.score}] <${favorite.commentCount}> favorited: $favoritedDate",
                        color = HackerColors.DarkGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete favorite",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // URL if available
            favorite.url?.let { url ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "|_ $url",
                    color = HackerColors.DarkGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Click indicator
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$ click to read >",
                color = HackerColors.Green,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}