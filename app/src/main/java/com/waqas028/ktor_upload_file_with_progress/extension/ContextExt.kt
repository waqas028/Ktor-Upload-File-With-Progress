package com.waqas028.ktor_upload_file_with_progress.extension

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

fun Context.getFileType(uri: Uri): String {
    val mimeType = contentResolver.getType(uri)
    if (mimeType != null) {
        return when {
            mimeType.startsWith("image") -> "Image"
            mimeType == "application/pdf" -> "PDF"
            else -> "Unknown"
        }
    }
    val fileName = uri.lastPathSegment?.lowercase()
    return when {
        fileName?.endsWith(".jpg") == true || fileName?.endsWith(".jpeg") == true || fileName?.endsWith(".png") == true || fileName?.endsWith(".gif") == true -> "Image"
        fileName?.endsWith(".pdf") == true -> "PDF"
        else -> "Unknown"
    }
}

fun Context.createTempFileFromUri(uri: Uri): File? {
    return try {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileExtension = getFileExtension(uri) ?: "jpg"
        val tempFile = File.createTempFile("temp_image${System.currentTimeMillis()}", ".$fileExtension", cacheDir)

        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.getFileExtension(uri: Uri): String? {
    return contentResolver.getType(uri)?.let { mimeType ->
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
}