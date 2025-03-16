package com.waqas028.ktor_upload_file_with_progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.waqas028.ktor_upload_file_with_progress.model.MediaWithFile
import com.waqas028.ktor_upload_file_with_progress.repo.UploadState
import com.waqas028.ktor_upload_file_with_progress.ui.theme.KtorUploadFileWithProgressTheme
import com.waqas028.ktor_upload_file_with_progress.utils.getMimeType
import java.io.File

@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    mediaWithFile: MediaWithFile,
    uploadState: UploadState?,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current
    val mimeType = mediaWithFile.uri.getMimeType(context)

    Box(modifier) {
        Card(
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(10.dp)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    mimeType?.startsWith("image") == true -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(mediaWithFile.uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Document Image",
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.baseline_image_24),
                            error = painterResource(id = R.drawable.ic_launcher_background),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    mimeType == "application/pdf" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_picture_as_pdf_24),
                            contentDescription = "Document Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(48.dp)
                                .weight(1f)
                        )
                    }
                }
                Text(
                    text = mediaWithFile.label,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.tertiary, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 7.dp, top = 15.dp)
                .size(22.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Text(
                text = mediaWithFile.index.toString(),
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (uploadState?.isUploadComplete == true) Icon(
            imageVector = Icons.Filled.Close,
            tint = MaterialTheme.colorScheme.background,
            contentDescription = "Remove Item",
            modifier = Modifier
                .size(30.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(7.dp)
                .align(Alignment.TopEnd)
                .clickable { onRemove() }
        ) else CircularProgressIndicator(
            progress = { if(uploadState?.progress?.isNaN() == true) 0f else uploadState?.progress ?: 0f },
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = Color.Black.copy(alpha = .7f), shape = RoundedCornerShape(10.dp))
                .padding(6.dp)
        )
    }
}

@Preview
@Composable
private fun MediaCardPreview(){
    KtorUploadFileWithProgressTheme {
        MediaCard(
            mediaWithFile = MediaWithFile(label = "test", media = File(""), index = 1),
            uploadState = UploadState(),
            onRemove = {},
        )
    }
}