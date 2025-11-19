package utils

import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import androidx.compose.runtime.Composable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Formats a Firebase timestamp into a relative "time ago" string.
 * This is a cross-platform function.
 *
 * @param timestamp The FieldValue which can be a ServerTimestamp or an actual Timestamp.
 * @return A formatted string like "5m ago", "2h ago", or "Just now".
 */
@OptIn(ExperimentalTime::class)
@Composable
fun formatTimestamp(timestamp: FieldValue?): String {
    // The timestamp from Firestore can be one of two things:
    // 1. A FieldValue.serverTimestamp marker before it's saved.
    // 2. An actual Timestamp object after it's been read back.
    val actualTimestamp = (timestamp as? Timestamp) ?: return "Just now"

    val now = kotlin.time.Clock.System.now()
    val postTime = kotlin.time.Instant.fromEpochSeconds(actualTimestamp.seconds)
    val duration = now - postTime

    return when {
        duration < 1.minutes -> "Just now"
        duration < 1.hours -> "${duration.inWholeMinutes}m ago"
        duration < 24.hours -> "${duration.inWholeHours}h ago"
        duration < 7.days -> "${duration.inWholeDays}d ago"
        else -> {
            // For older posts, you might want to show the actual date.
            // This part can be expanded with a date formatting library.
            val date = postTime.toLocalDateTime(TimeZone.currentSystemDefault())
            "${date.day} ${date.month.name.take(3)} ${date.year}"
        }
    }
}


