package com.waqas028.ktor_upload_file_with_progress.model

import android.net.Uri
import android.os.Parcelable
import java.io.File
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaWithFile(
    val label: String,
    val media: File,
    val index: Int,
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val uri: Uri = Uri.EMPTY
) : Parcelable