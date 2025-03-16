package com.waqas028.ktor_upload_file_with_progress.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddFileResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val fileList: List<TempFile>
)

@Serializable
data class TempFile(
    val id: Int,
    @SerialName("temp_id")
    val tempId: Int,
    val label: String,
    val index: Int,
    val media: String,
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)