package com.example.myapplication.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.myapplication.data.model.Favorite
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    
    fun exportFavoritesToCsv(
        context: Context,
        favorites: List<Favorite>,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val fileName = "hacker_news_favorites_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
            val downloadsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "exports")
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            val csvFile = File(downloadsDir, fileName)
            
            FileWriter(csvFile).use { writer ->
                // 写入 CSV 头部
                writer.append("ID,Title,URL,Author,Posted Time,Score,Comments,Favorited Time\n")
                
                // 写入收藏数据
                favorites.forEach { favorite ->
                    val postedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(favorite.timestamp * 1000))
                    val favoritedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(favorite.favoriteTime))
                    
                    writer.append("${favorite.id},")
                    writer.append("\"${escapeCsv(favorite.title)}\",")
                    writer.append("\"${favorite.url ?: ""}\",")
                    writer.append("\"${escapeCsv(favorite.author)}\",")
                    writer.append("$postedDate,")
                    writer.append("${favorite.score},")
                    writer.append("${favorite.commentCount},")
                    writer.append("$favoritedDate\n")
                }
            }
            
            onSuccess(csvFile)
            
        } catch (e: IOException) {
            onError("Failed to export CSV: ${e.message}")
        } catch (e: Exception) {
            onError("Unexpected error: ${e.message}")
        }
    }
    
    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }
    
    fun shareCsvFile(context: Context, csvFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            csvFile
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Hacker News Favorites")
            putExtra(Intent.EXTRA_TEXT, "Here are my favorite Hacker News stories")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share CSV File"))
    }
}