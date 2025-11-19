package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.PostItem
import data.Story
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import viewmodels.SpaceViewModel

@Composable
fun MemoriesScreen(modifier: Modifier = Modifier, viewModel: SpaceViewModel) {
    val realFeeds by viewModel.feeds.collectAsState()
    val mockFeeds = remember { createMockFeeds() }
    val feedsToDisplay = if (realFeeds.isNotEmpty()) realFeeds else mockFeeds

    if (feedsToDisplay.isEmpty()) {
        EmptyMemoriesPlaceholder(modifier)
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 72.dp)
        ) {
            items(feedsToDisplay, key = { it.storyId }) { post ->
                PostItem(
                    post = post,
                    onLikeClick = {},
                    onCommentClick = {},
                    onShareClick = {},
                    onProfileClick = {}
                )
                ListItemDivider()
            }
        }
    }
}

// Placeholder for when there are no memories to show ---
@Composable
private fun EmptyMemoriesPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoAlbum,
            contentDescription = "Memories Icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Your Memories",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "This is where your shared stories and memories will appear.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ListItemDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp, horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    )
}

private fun createMockFeeds(): List<Story> {
    return listOf(
        Story(
            storyId = "mock_1",
            userName = "Really Rinah",
            userProfileUrl = "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            contentType = Story.ContentType.PHOTO,
            contentUrl = "https://images.pexels.com/photos/1107717/pexels-photo-1107717.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            textContent = "Exploring the ancient wonders of Egypt. The scale of the pyramids is just breathtaking!",
            likeCount = 124,
            commentCount = 12,
            isLiked = true
        ),
        Story(
            storyId = "mock_2",
            userName = "David Kabogere",
            userProfileUrl = "https://images.pexels.com/photos/1043474/pexels-photo-1043474.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            contentType = Story.ContentType.PHOTO,
            contentUrl = "https://images.pexels.com/photos/247502/pexels-photo-247502.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            textContent = "Safari in the Maasai Mara was a dream come true. Witnessed the great migration.",
            likeCount = 302,
            commentCount = 45
        ),
        Story(
            storyId = "mock_3",
            userName = "Maria Mukasa",
            userProfileUrl = "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            contentType = Story.ContentType.VIDEO,
            contentUrl = "https://videos.pexels.com/video-files/4434242/4434242-uhd_2732_1440_25fps.mp4",
            thumbnailUrl = "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            textContent = "The turquoise waters of Zanzibar are paradise on Earth. Can't wait to come back!",
            likeCount = 588,
            commentCount = 98,
            isLiked = true
        ),
        Story(
            storyId = "mock_4",
            userName = "Mugumya Ali",
            userProfileUrl = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            contentType = Story.ContentType.PHOTO,
            contentUrl = "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            textContent = "Cape Town has it all: mountains, city life, and incredible food. What a vibe!",
            likeCount = 215,
            commentCount = 33
        )
    )
}


@Preview
@Composable
private fun MemoriesScreenPreview() {
    // The preview will now show the mock data by default
    // We can't pass a real ViewModel in preview easily, so this setup is ideal.
    val mockViewModel = SpaceViewModel(dev.gitlive.firebase.Firebase.firestore, dev.gitlive.firebase.Firebase.storage, dev.gitlive.firebase.Firebase.auth)
    AppTheme {
        MemoriesScreen(viewModel = mockViewModel)
    }
}

