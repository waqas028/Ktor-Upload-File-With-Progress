package com.waqas028.ktor_upload_file_with_progress

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.waqas028.ktor_upload_file_with_progress.extension.createTempFileFromUri
import com.waqas028.ktor_upload_file_with_progress.extension.getFileType
import com.waqas028.ktor_upload_file_with_progress.model.MediaWithFile
import com.waqas028.ktor_upload_file_with_progress.model.MenuItem
import com.waqas028.ktor_upload_file_with_progress.ui.theme.KtorUploadFileWithProgressTheme
import com.waqas028.ktor_upload_file_with_progress.utils.getMediaTypeFromFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UploadFileScreen(
    modifier: Modifier = Modifier,
    taskVM: TaskVM = hiltViewModel(),
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uploadStates = taskVM.uploadStates
    var mediaUriList by rememberSaveable { mutableStateOf(emptyList<MediaWithFile>()) }
    val tempFileResponse by taskVM.tempFileResponse.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val fileType = context.getFileType(uri)
            val file = context.createTempFileFromUri(uri)
            when (fileType) {
                "Image" -> {
                    file?.let {
                        mediaUriList = mediaUriList + MediaWithFile(label = file.name, media = file, index = mediaUriList.size+1, uri = uri)
                    }
                }
                "PDF" -> {
                    file?.let {
                        mediaUriList = mediaUriList + MediaWithFile(label = file.name, media = file, index = mediaUriList.size+1, uri = uri)
                    }
                }
            }

        }
    }
    val menuItems = listOf(
        MenuItem(
            title = "Image",
            icon = R.drawable.baseline_image_24
        ),
        MenuItem(
            title = "PDF File",
            icon = R.drawable.baseline_picture_as_pdf_24
        )
    )

    LaunchedEffect(uploadStates) {
        mediaUriList.map { media ->
            uploadStates[media.uri]?.let {uploadState->
                mediaUriList = if (uploadState.isUploading) mediaUriList.map { media ->
                    if (media.media == uploadState.file) {
                        media.copy(isUploading = true)
                    } else {
                        media
                    }
                } else mediaUriList.map { media ->
                    if (media.media == uploadState.file) {
                        media.copy(isUploadComplete = uploadState.isUploadComplete, isUploading = false)
                    } else {
                        media
                    }
                }
            }
        }
    }

    LaunchedEffect(mediaUriList) {
        scope.launch(Dispatchers.IO) {
            mediaUriList.forEach { mediaWithFile ->
                if (!mediaWithFile.isUploadComplete && !mediaWithFile.isUploading) {
                    /*val alreadyUploaded = alreadyUploadingOrUploaded(mediaWithFile, tempFileResponse?.fileList.orEmpty(), uploadStates)
                    if (alreadyUploaded) {
                        mediaUriList = mediaUriList.filter { localFile ->
                            localFile.uri != mediaWithFile.uri
                        }
                    } else {
                        mediaUriList = mediaUriList.map { media ->
                            if (media.media == mediaWithFile.media) {
                                media.copy(isUploading = true)
                            } else {
                                media
                            }
                        }
                        taskVM.uploadTempFile(mediaWithFile)
                    }*/
                    mediaUriList = mediaUriList.map { media ->
                        if (media.media == mediaWithFile.media) {
                            media.copy(isUploading = true)
                        } else {
                            media
                        }
                    }
                    taskVM.uploadTempFile(mediaWithFile)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.upload_file_progress),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(vertical = 10.dp)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = stringResource(R.string.upload_files),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = !expanded
                },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
            ) {
                menuItems.forEachIndexed { menuIndex, menuItem ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            when (menuIndex) {
                                0 -> launcher.launch("image/*")
                                1 -> launcher.launch("application/pdf")
                            }
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = menuItem.icon),
                                contentDescription = menuItem.title,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        text = {
                            Text(
                                text = menuItem.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                    if (menuItem.title != menuItems.last().title) HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.all_files),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
        )
        LazyVerticalGrid(
            modifier = Modifier.heightIn(max = 800.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(mediaUriList) { mediaWithFile ->
                MediaCard(
                    modifier = Modifier.aspectRatio(1f),
                    mediaWithFile = mediaWithFile,
                    uploadState = uploadStates[mediaWithFile.uri],
                    onRemove = {
                        mediaUriList = mediaUriList.filter { it != mediaWithFile }

                        val mediaType = getMediaTypeFromFile(mediaWithFile.media)
                        val matchingTempFileId = tempFileResponse?.fileList?.find {
                            it.label == mediaWithFile.label &&
                                    it.index == mediaWithFile.index &&
                                    it.mediaType == mediaType
                        }?.id
                        matchingTempFileId?.let {
                            taskVM.removeTempFile(matchingTempFileId)
                        }
                    }
                )
            }
        }
        }
}

@Preview
@Composable
private fun UploadFileScreenPreview(){
    KtorUploadFileWithProgressTheme {
        UploadFileScreen(
            modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
        )
    }
}