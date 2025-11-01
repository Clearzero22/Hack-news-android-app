package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.model.Story
import com.example.myapplication.data.model.Favorite
import com.example.myapplication.ui.screens.NewsListScreen
import com.example.myapplication.ui.screens.StoryDetailScreen
import com.example.myapplication.ui.screens.EnhancedStoryDetailScreen
import com.example.myapplication.ui.screens.FavoritesScreen
import com.example.myapplication.ui.theme.HackerTheme
import com.example.myapplication.ui.components.SimpleHackerBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackerTheme {
                HackerNewsApp()
            }
        }
    }
}

@Composable
fun HackerNewsApp() {
    val navController = rememberNavController()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 简化的黑客背景
        SimpleHackerBackground()
        
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "news_list",
                modifier = Modifier.padding(innerPadding)
            ) {
            composable("news_list") {
                NewsListScreen(
                    onStoryClick = { story ->
                        navController.navigate("story_detail/${story.id}")
                    },
                    onFavoritesClick = {
                        navController.navigate("favorites")
                    }
                )
            }
            
            composable("favorites") {
                FavoritesScreen(
                    onBackClick = { navController.popBackStack() },
                    onStoryClick = { favorite ->
                        // 收藏列表点击时跳转到新闻详情页
                        navController.navigate("story_detail/${favorite.id}")
                    }
                )
            }
            
            composable(
                route = "story_detail/{storyId}",
                arguments = listOf(navArgument("storyId") { type = NavType.IntType })
            ) { backStackEntry ->
                val storyId = backStackEntry.arguments?.getInt("storyId") ?: return@composable
                
                EnhancedStoryDetailScreen(
                    storyId = storyId,
                    onBackClick = { navController.popBackStack() },
                    onOpenUrl = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        navController.context.startActivity(intent)
                    }
                )
            }
        }
    }
    }
}