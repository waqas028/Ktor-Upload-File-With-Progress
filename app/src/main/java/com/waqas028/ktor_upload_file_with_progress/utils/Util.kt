package com.waqas028.ktor_upload_file_with_progress.utils

import android.content.Context
import android.net.Uri
import com.waqas028.ktor_upload_file_with_progress.model.MediaWithFile
import com.waqas028.ktor_upload_file_with_progress.repo.UploadState
import com.waqas028.ktor_upload_file_with_progress.model.TempFile
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import java.io.File
import java.io.FileNotFoundException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


fun getApiErrorMessage(cause: Throwable): String {
    return when (cause) {
        is OutOfMemoryError -> "File too large!"
        is FileNotFoundException -> "File not found!"

        is UnknownHostException -> "No internet connection!" // Specific first
        is UnresolvedAddressException -> "Invalid host or no internet!"
        is ConnectTimeoutException, is SocketTimeoutException -> "Server is taking too long!"
        is SSLHandshakeException -> "SSL certificate error!"

        is ClientRequestException -> "Client error! Please check your request."
        is ServerResponseException -> "Server error! Try again later."
        is ResponseException -> "Unexpected API response!"

        is SerializationException -> "Data format error!"
        is IllegalStateException -> "Invalid response from server!"

        is IOException -> "Network issue!"

        else -> "Something went wrong!"
    }
}

fun Uri.getMimeType(context: Context): String? {
    return context.contentResolver.getType(this)
}

fun getMediaTypeFromFile(file: File): String {
    return when (file.extension.lowercase()) {
        "jpg", "jpeg", "png", "gif", "bmp", "webp" -> "image"
        "pdf" -> "pdf"
        else -> "other"
    }
}

fun alreadyUploadingOrUploaded(
    mediaWithFile: MediaWithFile,
    uploadedList: List<TempFile>,
    uploadStates: Map<Uri, UploadState>
): Boolean {
    val isUploadingPdf = mediaWithFile.media.extension.equals("pdf", ignoreCase = true)

    // 1️⃣ Check in already uploaded list
    val alreadyUploadedSameType = if (isUploadingPdf) {
        uploadedList.any { it.mediaType.equals("pdf", ignoreCase = true) }
    } else {
        uploadedList.any { !it.mediaType.equals("pdf", ignoreCase = true) }
    }

    // 2️⃣ Check in currently uploading files
    val alreadyUploadingSameType = uploadStates
        .filter { (_, state) ->
            getMediaTypeFromFile(state.file ?: File("")).equals("pdf", ignoreCase = true) == isUploadingPdf
        }
        .isNotEmpty()

    return alreadyUploadedSameType || alreadyUploadingSameType
}