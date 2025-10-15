package data.repository

import data.models.Channel
import data.models.Video


sealed class SearchableItem{
    abstract val id: String
    abstract val title: String // Common property for display
    abstract val type: ItemType
    abstract val thumbnailUrl: String // Common property for display

    data class ChannelItem(
        val channel: Channel
    ) : SearchableItem() {
        override val id: String get() = channel.id
        override val title: String get() = channel.displayName
        override val type: ItemType get() = ItemType.CHANNEL
        override val thumbnailUrl: String get() = channel.imageUrl
    }

    data class VideoItem(
        val video: Video
    ) : SearchableItem() {
        override val id: String get() = video.videoId
        override val title: String get() = video.title
        override val type: ItemType get() = if (video.isShort) ItemType.SHORTS else ItemType.VIDEO
        override val thumbnailUrl: String get() = video.thumbnailUrl
    }
}
enum class ItemType{
    VIDEO,
    AUDIO,
    SHORTS,
    CHANNEL,
    SONG,
    ARTIST,
    ALBUM,
    PLAYLIST,
    GENRE,
    PODCAST
}

enum class SearchContext{
    ALL,
    CHATS
}
// Add this class somewhere in your file or in a shared location:
data class SimpleItem(
    override val title: String,
    override val type: ItemType
) : SearchableItem() {
    override val id: String get() = title
    override val thumbnailUrl: String get() = ""
}


/*object SearchSuggestionRepository {
    suspend fun getSuggestions(
        query:String,
        context: SearchContext,
        firestore: FirebaseFirestore
    ): List<SearchableItem>{

    }

}*/