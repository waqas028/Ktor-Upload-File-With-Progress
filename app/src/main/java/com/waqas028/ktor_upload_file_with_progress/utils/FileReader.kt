package com.waqas028.ktor_upload_file_with_progress.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class FileReader @Inject constructor(@ApplicationContext val context: Context) {

    suspend fun uriToFileInfo(contentUri: Uri): FileInfo {
        return withContext(Dispatchers.IO) {
            val bytes = context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                } ?: byteArrayOf()

            val fileName = UUID.randomUUID().toString()
            val mimeType = context.contentResolver.getType(contentUri) ?: ""
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""

            FileInfo(
                name = fileName,
                mimeType = mimeType,
                extension = extension,
                bytes = bytes
            )
        }
    }

    suspend fun fileToFileInfo(file: File): FileInfo {
        return withContext(Dispatchers.IO) {
            val bytes = file.readBytes()

            val name = file.nameWithoutExtension
            val extension = file.extension

            // You can try to get MIME type using extension (best effort)
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.lowercase()) ?: ""

            FileInfo(
                name = name,
                mimeType = mimeType,
                extension = extension,
                bytes = bytes
            )
        }
    }
}

class FileInfo(
    val name: String,
    val mimeType: String,
    val extension: String,
    val bytes: ByteArray
)