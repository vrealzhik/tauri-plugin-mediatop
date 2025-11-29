package com.plugin.mediatop

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResult
import app.tauri.annotation.ActivityCallback
import app.tauri.annotation.Command
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.Invoke
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@TauriPlugin
class MediaConverterPlugin(private val activity: Activity) : Plugin(activity) {

    private var pendingInvoke: Invoke? = null

    @Command
    fun pickAndConvertVideo(invoke: Invoke) {
        pendingInvoke = invoke
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        startActivityForResult(invoke, intent, "onVideoSelected")
    }

    @ActivityCallback
    fun onVideoSelected(invoke: Invoke, result: ActivityResult) {
        try {
            if (result.resultCode != Activity.RESULT_OK || result.data == null) {
                pendingInvoke?.reject("User cancelled video selection")
                return
            }

            val uri: Uri = result.data!!.data!!
            val context = activity

            var inputPath: String? = null
            if (uri.scheme == "file") {
                inputPath = uri.path
            } else if (uri.scheme == "content") {
                val fileName = getFileNameFromUri(context, uri)
                val tempFile = File(context.cacheDir, "media_converter/${fileName ?: "video.mp4"}")
                tempFile.parentFile?.mkdirs()

                context.contentResolver.openInputStream(uri)?.use { stream ->
                    FileOutputStream(tempFile).use { output ->
                        stream.copyTo(output)
                    }
                }
                if (tempFile.exists()) {
                    inputPath = tempFile.absolutePath
                }
            }

            if (inputPath == null) {
                pendingInvoke?.reject("Failed to access video file")
                return
            }

            val outputFileName = "converted_${System.currentTimeMillis()}.mp3"
            val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            outputDir.mkdirs()
            val outputPath = File(outputDir, outputFileName).absolutePath

            val cmd = arrayOf(
                "-i", inputPath,
                "-vn",                    
                "-acodec", "libmp3lame",  
                "-ab", "128k",            
                "-ar", "44100",           
                "-ac", "2",               
                outputPath
            )

            val session: FFmpegSession = FFmpegKit.execute(cmd)

            if (ReturnCode.isSuccess(session.getReturnCode())) {
                val res = JSObject()
                res.put("success", true)
                res.put("output_path", outputPath)
                pendingInvoke?.resolve(res)
            } else {
                pendingInvoke?.reject("FFmpeg failed: ${session.getFailStackTrace()}")
            }

        } catch (e: Exception) {
            pendingInvoke?.reject("Conversion error: ${e.message}")
        } finally {
            pendingInvoke = null
        }
    }

    private fun getFileNameFromUri(context: Activity, uri: Uri): String? {
        if (uri.scheme == "content") {
            val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) {
                        return cursor.getString(idx)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        return uri.lastPathSegment ?: "video.mp4"
    }
}