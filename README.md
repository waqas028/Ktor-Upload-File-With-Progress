# 📤 Upload File with Progress using Ktor (Kotlin + MVVM + Compose)

This project demonstrates how to **upload images and PDFs to a server using Ktor**, while displaying a **real-time progress bar** to track upload progress (bytes uploaded).

## 🚀 Features:
- Upload **Image/PDF** files to server.
- Shows **progress bar** with uploaded bytes.
- Built with **Kotlin, MVVM architecture & Jetpack Compose**.
- Clean and maintainable code structure.

## Demo
https://github.com/user-attachments/assets/92b7ad59-7c8e-4e52-a169-a495d6c6a41f

## KtorClient
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object KtorClient {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.ANDROID
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Ignore unknown fields in JSON
                    isLenient = true         // Allow lenient parsing
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60_000 // 60 seconds
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
        }
    }
}
```

## suspend fun UploadTempFileRepo
```kotlin
fun uploadTempFile(tempId: Int, mediaWithFile: MediaWithFile): Flow<ProgressUpdate> = channelFlow {
        val info = fileReader.uriToFileInfo(mediaWithFile.uri)
        Log.i("TAG", "uploadFileInfo: ${info.mimeType}. ..  ${info.name}.  Index:${mediaWithFile.index}")
        val formData = formData {
            append("temp_id", tempId)
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
```

## UploadTem File in VM
```kotlin
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
```

## 🔧 Tech Stack:
- **Kotlin**
- **Ktor Client**
- **MVVM Architecture**
- **Jetpack Compose**

## 💡 Open Question:
Has anyone implemented a similar file upload feature with **Retrofit** including progress tracking?  
Feel free to share your experience or approach! Let’s collaborate and learn together.

---

## 🔑 How to Use:
1. Clone the repo.
2. Configure your server URL and API endpoint.
3. Run the app and select a file to upload.
4. Observe real-time upload progress!

---
## 🌟 Developed By

<a href="https://twitter.com/Shahzad_Ansari3" target="_blank">
  <img src="https://github.com/user-attachments/assets/99199ad1-a69d-4ceb-8599-e495a1ed937b" width="70" align="left">
</a>

**Muhammad Waqas**  
[![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/muhammad-waqas-4399361a3)  
[![GitHub](https://img.shields.io/badge/-GitHub-181717?logo=github&logoColor=white)](https://github.com/waqas028)  
[![Twitter](https://img.shields.io/badge/-Twitter-000000?logo=x&logoColor=white)](https://x.com/waqas028?s=08)

