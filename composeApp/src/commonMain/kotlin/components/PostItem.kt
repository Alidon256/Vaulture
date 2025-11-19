package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import data.Story // Assuming Story is your data class
import utils.formatTimestamp

@Composable
fun PostItem(
    post: Story,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onOptionClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // Use surface, not background
            .padding(vertical = 12.dp)
    ) {
        // Post Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.userProfileUrl,
                contentDescription = "${post.userName}'s avatar",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick(post.userId) },
                contentScale = ContentScale.Crop,
                // In Coil3, you can define placeholders in the ImageLoader
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { onProfileClick(post.userId) }
                )
                // You will need a multiplatform function to format the timestamp
                Text(
                    text = formatTimestamp(post.timestamp), // Placeholder for formatted timepost.timestamp?.toString() ?: "Just now", // Placeholder for formatted time
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onOptionClick(post.storyId) }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Post Text Content
        if (!post.textContent.isNullOrBlank()) {
            Text(
                text = post.textContent!!,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Post Media Content (Image or Video)
        when (post.contentType) {
            Story.ContentType.PHOTO -> if (!post.contentUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = post.contentUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(post.aspectRatio ?: 1f),
                    contentScale = ContentScale.Crop,
                )
            }
            Story.ContentType.VIDEO -> if (!post.contentUrl.isNullOrEmpty()) {
                // Use the placeholder for cross-platform compatibility
                VideoPlayerPlaceholder(
                    thumbnailUrl = post.thumbnailUrl,
                    aspectRatio = post.aspectRatio ?: (16f / 9f)
                )
            }
            else -> { /* No media for TEXT type or if URL is null */ }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Post Stats
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("${post.likeCount} Likes", style = MaterialTheme.typography.bodySmall)
            Text("${post.commentCount} Comments", style = MaterialTheme.typography.bodySmall)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // Post Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PostActionButton(
                icon = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                text = "Like",
                onClick = { onLikeClick(post.storyId) },
                isActivated = post.isLiked,
                activeColor = MaterialTheme.colorScheme.primary
            )
            PostActionButton(
                icon = Icons.AutoMirrored.Filled.Comment,
                text = "Comment",
                onClick = { onCommentClick(post.storyId) }
            )
            PostActionButton(
                icon = Icons.AutoMirrored.Filled.Send,
                text = "Share",
                onClick = { onShareClick(post.storyId) }
            )
        }
    }
}

@Composable
fun PostActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isActivated: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    val contentColor = if (isActivated) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = text, tint = contentColor)
        Text(text, fontWeight = FontWeight.SemiBold, color = contentColor)
    }
}

/**
 * A placeholder for a cross-platform video player.
 * In a real app, this would use an expect/actual implementation.
 */
@Composable
fun VideoPlayerPlaceholder(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 9f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Video thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        // Play button overlay
        Icon(
            imageVector = Icons.Filled.PlayCircleFilled,
            contentDescription = "Play Video",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(64.dp)
        )
    }
}
