package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.myapplication.ui.screens.NewsListScreen
import com.example.myapplication.ui.screens.StoryDetailScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HackerNewsApp()
            }
        }
    }
}

@Composable
fun HackerNewsApp() {
    val navController = rememberNavController()
    
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
                    }
                )
            }
            
            composable(
                route = "story_detail/{storyId}",
                arguments = listOf(navArgument("storyId") { type = NavType.IntType })
            ) { backStackEntry ->
                val storyId = backStackEntry.arguments?.getInt("storyId") ?: return@composable
                
                StoryDetailScreen(
                    story = Story(
                        id = storyId,
                        title = "Loading...",
                        author = "",
                        score = 0,
                        timestamp = System.currentTimeMillis() / 1000,
                        descendants = 0
                    ),
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