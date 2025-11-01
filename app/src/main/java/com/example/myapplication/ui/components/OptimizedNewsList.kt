package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Story
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedNewsList(
    stories: List<Story>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onStoryClick: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        when {
            error != null -> {
                ErrorMessage(
                    message = error,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            stories.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.fillMaxWidth()
                )
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
                        EnhancedStoryItem(
                            story = story,
                            onStoryClick = onStoryClick
                        )
                    }
                }
            }
        }
    }
}