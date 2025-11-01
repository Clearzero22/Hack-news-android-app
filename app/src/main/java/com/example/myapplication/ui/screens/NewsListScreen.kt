package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Story
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.HackerColors
import com.example.myapplication.ui.viewmodels.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onStoryClick: (Story) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: NewsViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteUiState by favoriteViewModel.uiState.collectAsState()
    
    // 弹出框状态
    var showFavoriteDialog by remember { mutableStateOf(false) }
    var selectedStory by remember { mutableStateOf<Story?>(null) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar with Tab Row
        TopAppBar(
            title = { Text("Hacker News") },
            actions = {
                IconButton(onClick = onFavoritesClick) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorites",
                        tint = HackerColors.Green
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = HackerColors.Grey,
                titleContentColor = HackerColors.Green,
                actionIconContentColor = HackerColors.Green
            )
        )
        
        // Tab Row for story types
        TabRow(
            selectedTabIndex = uiState.selectedStoryType.ordinal
        ) {
            StoryType.values().forEach { type ->
                Tab(
                    selected = uiState.selectedStoryType == type,
                    onClick = { viewModel.selectStoryType(type) },
                    text = { 
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    }
                )
            }
        }
        
        // Content with optimized components
        SimpleHackerNewsList(
            stories = uiState.stories,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onRefresh = { viewModel.refreshStories() },
            onStoryClick = onStoryClick,
            onStoryLongPress = { story ->
                selectedStory = story
                showFavoriteDialog = true
                // 检查收藏状态
                val isFavorite = favoriteUiState.favoriteStatus[story.id] ?: false
                favoriteViewModel.updateFavoriteStatus(story.id, isFavorite)
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // 收藏弹出框
        if (showFavoriteDialog && selectedStory != null) {
            FavoriteDialog(
                story = selectedStory!!,
                isFavorite = favoriteUiState.favoriteStatus[selectedStory!!.id] ?: false,
                onDismiss = { 
                    showFavoriteDialog = false
                    selectedStory = null 
                },
                onToggleFavorite = { story ->
                    favoriteViewModel.toggleFavorite(story)
                },
                onViewFavorites = onFavoritesClick
            )
        }
    }
}

@Composable
fun LoadingShimmerList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(10) {
            StoryShimmer()
        }
    }
}