package data

import androidx.compose.runtime.Immutable

// Represents a single conversation in the chat list
@Immutable
data class Chat(
    val id: String,
    val partnerName: String,
    val partnerAvatarUrl: String,
    val lastMessage: String,
    val lastMessageTimestamp: String,
    val unreadCount: Int
)

// Represents a single message within a chat
@Immutable
data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val authorAvatarUrl: String
)

// A repository to provide mock chat data
object ChatRepository {

    fun getChats(): List<Chat> {
        return listOf(
            Chat("1", "Jenna", "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "See you there!", "10:42 AM", 2),
            Chat("2", "David", "https://images.pexels.com/photos/1043474/pexels-photo-1043474.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Haha, that's hilarious.", "9:15 AM", 0),
            Chat("3", "Maria", "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Are you joining the call?", "Yesterday", 1),
            Chat("4", "Travel Buddies", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Anthony: Cape Town has it all!", "Yesterday", 5),
            Chat("5", "Anthony", "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Sounds good, I'll book the tickets.", "2d ago", 0),
            Chat("6", "Uganda Crew '24", "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Jenna: Don't forget your rain jacket!", "3d ago", 0)
        )
    }

    fun getMessagesForChat(chatId: String): List<ChatMessage> {
        val jennaAvatar = "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
        val myAvatar = "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2" // A generic "me" avatar

        return when (chatId) {
            "1" -> listOf(
                ChatMessage("m1", "Hey! Are we still on for the trip planning tonight?", "9:30 AM", false, jennaAvatar),
                ChatMessage("m2", "Absolutely! I've got some great ideas for the itinerary.", "9:31 AM", true, myAvatar),
                ChatMessage("m3", "Awesome! I found a cool spot we can meet at.", "10:40 AM", false, jennaAvatar),
                ChatMessage("m4", "Perfect. See you there!", "10:42 AM", true, myAvatar)
            )
            else -> listOf(
                ChatMessage("m_default", "This is a placeholder message.", "1:00 PM", false, "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
            )
        }
    }
}
