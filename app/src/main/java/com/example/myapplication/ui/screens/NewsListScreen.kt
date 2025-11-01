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
import com.example.myapplication.ui.components.ErrorMessage
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.components.StoryItem

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
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
                
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error!!,
                        onRetry = { viewModel.refreshStories() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                uiState.stories.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No stories available",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.stories) { story ->
                            StoryItem(
                                story = story,
                                onStoryClick = onStoryClick
                            )
                        }
                    }
                }
            }
        }
    }
}