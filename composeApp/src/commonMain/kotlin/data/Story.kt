package data

import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

// --- Top-Level Helper Function ---
// This isolates the timestamp comparison logic to prevent compiler errors.
private fun isTimestampAfterNow(timestamp: Timestamp?): Boolean {
    if (timestamp == null) return false
    val now = Timestamp.now()
    // Direct comparison of seconds and nanoseconds is the most reliable cross-platform approach.
    return timestamp.seconds > now.seconds || (timestamp.seconds == now.seconds && timestamp.nanoseconds > now.nanoseconds)
}

// CORRECTED: Added @Serializable for automatic Firestore mapping.
@Serializable
data class Story(
    val storyId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileUrl: String? = null,
    val contentType: ContentType = ContentType.PHOTO,
    val contentUrl: String? = null,
    val thumbnailUrl: String? = null,
    val textContent: String? = null, // Used for captions or text-only posts
    val duration: Int = 5,

    // CORRECTED: Use @EncodeDefault to have Firebase set the timestamp on the server.
    // The type is now FieldValue, which is the marker for serverTimestamp().
    @EncodeDefault val timestamp: FieldValue = FieldValue.serverTimestamp,
    val expiryTimestamp: Timestamp? = null,

    val isViewed: Boolean = false,
    val viewCount: Int = 0,
    val interactiveElements: List<InteractiveElement> = emptyList(),
    val locationTag: String? = null,
    val musicTrack: String? = null,
    val aspectRatio: Float = 1.0f,
    val privacySetting: PrivacySetting = PrivacySetting.PUBLIC,
    val isArchived: Boolean = false,
    val callToAction: String? = null,
    val linkUrl: String? = null,
    val isBusinessStory: Boolean = false,
    val likeCount: Int = 0,
    val likedByUserIds: List<String> = emptyList(),
    val commentCount: Int = 0,
    val comments: List<Comment> = emptyList(),
    val shareCount: Int = 0,
    val sharedByUserIds: List<String> = emptyList(),
    val isFeed: Boolean = false,
    val isLiked: Boolean = false
) {
    // REMOVED: The no-argument constructor is no longer needed with kotlinx.serialization.

    enum class ContentType {
        PHOTO, VIDEO, TEXT, AUDIO, GIF
    }

    enum class PrivacySetting {
        PUBLIC, FRIENDS, CUSTOM
    }

    // CORRECTED: Added @Serializable.
    @Serializable
    data class InteractiveElement(
        val type: Type,
        val content: String
    ) {
        enum class Type {
            STICKER, POLL, QUESTION, GIF, EMOJI
        }
    }

    @Serializable
    data class Comment(
        val commentId: String = "",
        val userId: String = "",
        val userName: String = "",
        val userProfileUrl: String? = null,
        val text: String = "",
        // CORRECTED: Also use server timestamp for comments.
        @EncodeDefault val timestamp: FieldValue = FieldValue.serverTimestamp
    )

    // CORRECTED: Use the robust helper function for comparison.
    fun isActive(): Boolean {
        return isTimestampAfterNow(expiryTimestamp)
    }

    fun timeRemainingMinutes(): Long {
        if (expiryTimestamp == null) return 0
        val now = Timestamp.now()
        val remainingSeconds = (expiryTimestamp.seconds - now.seconds).coerceAtLeast(0)
        return remainingSeconds / 60
    }

    // REMOVED: The fromMap companion object is no longer needed.
    // The library now handles this automatically with document.data<Story>().
}
