package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Story
import com.example.myapplication.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onStoryClick: (Story) -> Unit,
    viewModel: NewsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar with Tab Row
        TopAppBar(
            title = { Text("Hacker News") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
        
        // Content with simplified hacker style components
        SimpleHackerNewsList(
            stories = uiState.stories,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onRefresh = { viewModel.refreshStories() },
            onStoryClick = onStoryClick,
            modifier = Modifier.fillMaxSize()
        )
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