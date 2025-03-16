package com.waqas028.ktor_upload_file_with_progress.repo

import android.util.Log
import com.waqas028.ktor_upload_file_with_progress.model.AddFileResponse
import com.waqas028.ktor_upload_file_with_progress.model.MediaWithFile
import com.waqas028.ktor_upload_file_with_progress.utils.Constant
import com.waqas028.ktor_upload_file_with_progress.utils.FileReader
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import javax.inject.Inject

class UploadFileRepo @Inject constructor(
    private val httpClient: HttpClient,
    private val fileReader: FileReader,
) {
    fun uploadTempFile(tempId: Int, mediaWithFile: MediaWithFile): Flow<ProgressUpdate> = channelFlow {
        val info = fileReader.uriToFileInfo(mediaWithFile.uri)
        Log.i("TAG", "uploadFileInfo: ${info.mimeType}. ..  ${info.name}.  Index:${mediaWithFile.index}")
        val formData = formData {
            append("temp_id", tempId)
            /*append("pdf_documents[label]", mediaWithFile.label)
            append("pdf_documents[media]", info.bytes, Headers.build {
                append(HttpHeaders.ContentType, info.mimeType)
                append(HttpHeaders.ContentDisposition, "filename=${mediaWithFile.media.name}")
            })*/

            append("label", mediaWithFile.label)
            append("index", mediaWithFile.index)
            append("media", info.bytes, Headers.build {
                append(HttpHeaders.ContentType, info.mimeType)
                append(HttpHeaders.ContentDisposition, "filename=${mediaWithFile.media.name}")
            })
        }
        val response = httpClient.submitFormWithBinaryData(
            url = Constant.BASE_URL + "task/addTaskFile",
            formData = formData
        ) {
            onUpload { bytesSentTotal, totalBytes ->
                if(totalBytes > 0L) {
                    send(ProgressUpdate(bytesSentTotal, totalBytes))
                }
            }
        }

        if (response.status == HttpStatusCode.OK) {
            val responseData: AddFileResponse = response.body()
            send(ProgressUpdate(0, 0, file = mediaWithFile.media, addFileResponse = responseData))
        }

    }

    suspend fun removeTempFile(mediaId: Map<String, Int>): AddFileResponse {
        return httpClient.post(Constant.BASE_URL + "task/removeTaskFile") {
            contentType(ContentType.Application.Json)
            setBody(mediaId)
        }.body()
    }
}

data class ProgressUpdate(
    val bytesSent: Long,
    val totalBytes: Long,
    val file: File? = null,
    val addFileResponse: AddFileResponse? = null,
)

data class UploadState(
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null,
    val file: File? = null,
    val index: Int = 0
)