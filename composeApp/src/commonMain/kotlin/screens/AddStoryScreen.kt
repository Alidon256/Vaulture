package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.Story
import org.jetbrains.compose.ui.tooling.preview.Preview
import viewmodels.SpaceViewModel

// A platform-agnostic representation of picked media
data class MediaFile(
    val content: ByteArray,
    val thumbnail: ByteArray? = null,
    val type: Story.ContentType,
    val aspectRatio: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    viewModel: SpaceViewModel, // Pass the ViewModel
    onStoryAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var textContent by remember { mutableStateOf("") }
    var selectedMediaFile by remember { mutableStateOf<MediaFile?>(null) }
    var isFeed by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var progressMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val handleAddStoryClick: () -> Unit = {
        isLoading = true
        errorMessage = null

        val contentType = selectedMediaFile?.type ?: Story.ContentType.TEXT

        if (contentType == Story.ContentType.TEXT && textContent.isBlank()) {
            errorMessage = "Please enter some text."
            isLoading = false
        }
        if (contentType != Story.ContentType.TEXT && selectedMediaFile == null) {
            errorMessage = "Please select a media file."
            isLoading = false
        }

        viewModel.addStory(
            mediaContent = selectedMediaFile?.content,
            thumbnailContent = selectedMediaFile?.thumbnail,
            textContent = textContent,
            contentType = contentType,
            isFeed = isFeed,
            aspectRatio = selectedMediaFile?.aspectRatio ?: 1f,
            onProgress = { message -> progressMessage = message },
            onSuccess = {
                isLoading = false
                progressMessage = null
                onStoryAdded()
            },
            onError = { errorMsg ->
                isLoading = false
                progressMessage = null
                errorMessage = errorMsg
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = handleAddStoryClick,
                        enabled = !isLoading
                    ) {
                        Text("Post")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Content Input
            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                label = { Text("What's on your mind?") },
                placeholder = { Text("Share your thoughts, add a photo, or post a video.") },
                shape = RoundedCornerShape(16.dp)
            )

            // Media Preview (if selected)
            if (selectedMediaFile != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(selectedMediaFile?.aspectRatio ?: 1f)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Media Preview (${selectedMediaFile?.type?.name})")
                    // In a real app, you'd use AsyncImage(model = selectedMediaFile.content, ...)
                }
            }

            // Media Selection Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {
                // TODO: Implement a multiplatform file picker here.
                // This would call a function that returns a MediaFile object.
                Button(onClick = { /* launchPhotoPicker() */ }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo")
                    Spacer(Modifier.width(8.dp))
                    Text("Photo")
                }
                Button(onClick = { /* launchVideoPicker() */ }) {
                    Icon(Icons.Default.Videocam, contentDescription = "Add Video")
                    Spacer(Modifier.width(8.dp))
                    Text("Video")
                }
            }

            // Toggle for "Add to Feed"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isFeed,
                    onCheckedChange = { isFeed = it }
                )
                Spacer(Modifier.width(8.dp))
                Text("Post to main feed")
            }

            // Loading / Error State
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(progressMessage ?: "Loading...")
                }
            }
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
