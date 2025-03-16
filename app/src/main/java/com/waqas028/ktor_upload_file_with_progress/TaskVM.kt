package com.waqas028.ktor_upload_file_with_progress

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waqas028.ktor_upload_file_with_progress.model.AddFileResponse
import com.waqas028.ktor_upload_file_with_progress.model.MediaWithFile
import com.waqas028.ktor_upload_file_with_progress.repo.UploadFileRepo
import com.waqas028.ktor_upload_file_with_progress.repo.UploadState
import com.waqas028.ktor_upload_file_with_progress.utils.getApiErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class TaskVM @Inject constructor(
    private val uploadFileRepo: UploadFileRepo,
): ViewModel() {
    private var uploadJob: Job? = null
    val uploadStates = mutableStateMapOf<Uri, UploadState>()
    private val _tempFileResponse = MutableStateFlow<AddFileResponse?>(null)
    val tempFileResponse : StateFlow<AddFileResponse?> = _tempFileResponse
    fun uploadTempFile(mediaWithFile: MediaWithFile) {
        Log.i("TAG", "uploadTempFileInfo: $mediaWithFile")
        uploadJob = uploadFileRepo
            .uploadTempFile(132, mediaWithFile) // you can pass your API Params data here according to your API request
            .onStart {
                uploadStates[mediaWithFile.uri] = UploadState(
                    isUploading = true,
                    isUploadComplete = false,
                    errorMessage = null,
                    progress = 0f,
                    file = mediaWithFile.media,
                    index = mediaWithFile.index
                )

            }
            .onEach { progressUpdate ->
                uploadStates[mediaWithFile.uri] = UploadState(
                    progress = progressUpdate.bytesSent / progressUpdate.totalBytes.toFloat(),
                    file = progressUpdate.file
                )
                if (progressUpdate.addFileResponse != null) _tempFileResponse.value = progressUpdate.addFileResponse
            }
            .onCompletion { cause ->
                if(cause == null) {
                    uploadStates[mediaWithFile.uri] = UploadState(
                        isUploading = false,
                        isUploadComplete = true,
                        progress = 0f
                    )
                } else if(cause is CancellationException) {
                    uploadStates[mediaWithFile.uri] = UploadState(
                        isUploading = false,
                        errorMessage = "The upload was cancelled!",
                        isUploadComplete = false,
                        progress = 0f
                    )
                }
            }
            .catch { cause ->
                val message = getApiErrorMessage(cause)
                uploadStates[mediaWithFile.uri] = UploadState(
                    isUploading = false,
                    errorMessage = message
                )
            }
            .launchIn(viewModelScope)
    }

    fun removeTempFile(tempMediaId: Int) = viewModelScope.launch {
        val response = uploadFileRepo.removeTempFile(mapOf("media_id" to tempMediaId))
        //_tempFileResponse.value = response
        _tempFileResponse.value = _tempFileResponse.value?.fileList?.filter {
            it.id != tempMediaId
        }?.let { _tempFileResponse.value?.copy(fileList = it) }
    }
}