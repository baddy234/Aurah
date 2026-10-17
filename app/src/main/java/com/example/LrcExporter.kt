package com.example

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object LrcExporter {

    fun exportLrcFile(
        context: Context,
        trackTitle: String,
        trackArtist: String,
        lyricsText: String
    ): Result<String> {
        return try {
            val baseName = if (trackArtist.isNotBlank() && !trackArtist.equals("Unknown Artist", ignoreCase = true) && !trackArtist.equals("Device Audio", ignoreCase = true)) {
                "$trackTitle - $trackArtist"
            } else {
                trackTitle
            }
            val sanitized = baseName.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(80)
            val fileName = "$sanitized.lrc"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri: Uri? = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(lyricsText.toByteArray(Charsets.UTF_8))
                        outputStream.flush()
                    }
                    return Result.success("Saved to Downloads/$fileName")
                }
            }

            // Fallback for pre-Q or when MediaStore URI insertion is unavailable
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val targetFile = if (downloadsDir.exists() && downloadsDir.canWrite()) {
                File(downloadsDir, fileName)
            } else {
                File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            }

            FileOutputStream(targetFile).use { fos ->
                fos.write(lyricsText.toByteArray(Charsets.UTF_8))
                fos.flush()
            }
            Result.success("Saved to ${targetFile.name}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
