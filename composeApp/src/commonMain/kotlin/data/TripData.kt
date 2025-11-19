package data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import auth.User
import coil3.compose.AsyncImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import screens.HeroItem
import screens.PopularDestinationItem
import screens.SideNavItem
import kotlin.time.ExperimentalTime

// 1. Updated data model for a Trip
@Immutable
data class Trip(
    val id: String,
    val title: String,
    val country: String,
    val imageUrl: String,
    val description: String,
    val rating: Double,
    val reviews: Int,
    val gallery: List<String>, // New: For the image gallery
    val location: Location // New: For map coordinates
)

@Immutable
data class Location(
    val latitude: Double,
    val longitude: Double
)

// 2. Expanded repository for mock data
object TripRepository {
    private val allTrips = mutableListOf(
        Trip(
            "uganda",
            "Murchison Falls",
            "Uganda",
            "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Experience the raw power of the Nile River forcing its way through a narrow gorge, creating a thunderous roar and a beautiful rainbow.",
            4.8,
            120,
            gallery = listOf(
                "https://images.pexels.com/photos/3354427/pexels-photo-3354427.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/247376/pexels-photo-247376.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/1183266/pexels-photo-1183266.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(2.266, 31.691)
        ),
        Trip(
            "kenya",
            "Maasai Mara",
            "Kenya",
            "https://images.pexels.com/photos/5472260/pexels-photo-5472260.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Witness the breathtaking Great Migration, a spectacle of millions of wildebeest and zebra crossing the Mara River.",
            4.9,
            250,
            gallery = listOf(
                "https://images.pexels.com/photos/247502/pexels-photo-247502.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/326900/pexels-photo-326900.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/70455/pexels-photo-70455.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(-1.500, 35.000)
        ),
        Trip(
            "egypt",
            "Pyramids of Giza",
            "Egypt",
            "https://images.unsplash.com/photo-1569949381669-ecf31ae8e613?q=80&w=2670&auto=format&fit=crop",
            "Journey back in time and marvel at the last remaining wonder of the ancient world, an architectural feat that has puzzled historians for centuries.",
            4.7,
            580,
            gallery = listOf(
                "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/1577903/pexels-photo-1577903.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/3889856/pexels-photo-3889856.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(29.979, 31.134)
        ),
        Trip(
            "tanzania",
            "Zanzibar Beaches",
            "Tanzania",
            "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Relax on the pristine white sands and swim in the turquoise waters of this tropical paradise. Explore the historic Stone Town.",
            4.6,
            180,
            gallery = listOf(
                "https://images.pexels.com/photos/1174732/pexels-photo-1174732.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/338504/pexels-photo-338504.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/221457/pexels-photo-221457.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(-6.165, 39.199)
        ),
        Trip(
            "south_africa",
            "Cape Town",
            "South Africa",
            "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Discover a vibrant city nestled between the iconic Table Mountain and the sparkling Atlantic Ocean. A hub of culture, cuisine, and adventure.",
            4.8,
            320,
            gallery = listOf(
                "https://images.pexels.com/photos/1020016/pexels-photo-1020016.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/2362002/pexels-photo-2362002.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/1580271/pexels-photo-1580271.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(-33.924, 18.424)
        ),
        Trip(
            "namibia",
            "Deadvlei",
            "Namibia",
            "https://images.pexels.com/photos/3514066/pexels-photo-3514066.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Walk among ancient, skeletal camel thorn trees on a white clay pan, surrounded by the towering red dunes of Sossusvlei.",
            4.9,
            95,
            gallery = listOf(
                "https://images.pexels.com/photos/46160/field-mountains-sky-sun-46160.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/688660/pexels-photo-688660.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/33045/lion-wild-africa-african.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(-24.757, 15.293)
        ),
        Trip(
            "ethiopia",
            "Lalibela",
            "Ethiopia",
            "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Explore a complex of 11 monolithic rock-hewn churches, a testament to incredible faith and craftsmanship in a stunning highland landscape.",
            4.7,
            110,
            gallery = listOf(
                "https://images.pexels.com/photos/931007/pexels-photo-931007.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/1591373/pexels-photo-1591373.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/1659437/pexels-photo-1659437.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(12.031, 39.042)
        ),
        Trip(
            "zimbabwe",
            "Victoria Falls",
            "Zimbabwe",
            "https://images.pexels.com/photos/3354427/pexels-photo-3354427.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Feel the immense power and spray of 'The Smoke that Thunders', one of the world's largest and most breathtaking waterfalls.",
            4.9,
            410,
            gallery = listOf(
                "https://images.pexels.com/photos/2662116/pexels-photo-2662116.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/3408744/pexels-photo-3408744.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/33041/antelope-canyon-lower-canyon-arizona.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(-17.924, 25.857)
        ),
        Trip(
            "rwanda",
            "Volcanoes National Park",
            "Rwanda",
            "https://images.pexels.com/photos/176343/pexels-photo-176343.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Embark on a once-in-a-lifetime trek to see the majestic mountain gorillas in their natural habitat. A profoundly moving wildlife encounter.",
            4.9,
            150,
            gallery = listOf(
                "https://images.pexels.com/photos/927451/pexels-photo-927451.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/145939/pexels-photo-145939.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/167699/pexels-photo-167699.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(-1.462, 29.492)
        ),
        Trip(
            "morocco",
            "Marrakech",
            "Morocco",
            "https://images.pexels.com/photos/3889987/pexels-photo-3889987.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Immerse yourself in the vibrant chaos of the Djemaa el-Fna square, explore colorful souks, and relax in serene riads.",
            4.5,
            480,
            gallery = listOf(
                "https://images.pexels.com/photos/243138/pexels-photo-243138.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/3573382/pexels-photo-3573382.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/1631677/pexels-photo-1631677.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(31.629, -7.981)
        ),
        Trip(
            "senegal",
            "Lake Retba",
            "Senegal",
            "https://images.pexels.com/photos/4090647/pexels-photo-4090647.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Visit the famous 'Pink Lake', whose waters get their distinct color from a salt-loving algae. A surreal and beautiful natural wonder.",
            4.4,
            75,
            gallery = listOf(
                "https://images.pexels.com/photos/2356045/pexels-photo-2356045.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/2387873/pexels-photo-2387873.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                "https://images.pexels.com/photos/2325446/pexels-photo-2325446.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            location = Location(14.839, -17.234)
        )
    )

    fun getTrips(): List<Trip> {
        return allTrips
    }

    // New: Function to get filter categories from trips
    fun getTripFilterCategories(): List<String> {
        // Categories can be hardcoded or generated dynamically
        return listOf("All", "Safari", "Beach", "Cultural", "City", "Adventure")
    }

    // New: Function to filter trips by category
    fun getTripsByFilter(category: String): List<Trip> {
        if (category == "All") return allTrips
        // This is a simple logic, you might want to add a 'category' field in Trip for more robust filtering
        return allTrips.filter { trip ->
            when (category.lowercase()) {
                "safari" -> trip.description.contains("wildlife", true) || trip.title.contains("Mara", true) || trip.id in listOf("kenya", "uganda", "zimbabwe", "tanzania", "rwanda")
                "beach" -> trip.description.contains("beach", true) || trip.title.contains("Zanzibar", true)
                "cultural" -> trip.id in listOf("egypt", "ethiopia", "morocco")
                "city" -> trip.id in listOf("south_africa", "morocco")
                "adventure" -> trip.id in listOf("namibia", "zimbabwe", "uganda")
                else -> false
            }
        }
    }
}


// --- Keep other data classes as they are ---

@Immutable
data class TravelCategory(
    val id: String,
    val name: String,
    val imageUrl: String
)

@Immutable
data class TravelStory(
    val id: String,
    val title: String,
    val author: Member, // Re-using Member data class for the creator
    val coverImageUrl: String,
    val durationInMins: Int
)

@Immutable
data class ForYouSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val rating: Double,
    val reviews: Int
)
@Immutable
data class CommunityTrip(
    val id: String,
    val title: String,
    val destination: String,
    val coverImageUrl: String,
    val host: Member, // The user who created the trip
    val participantCount: Int,
    val tripDate: String // e.g., "August 2024"
)



object HomeRepository { // You can merge this with another repository or keep it separate

    private val currentUser = User(
        uid = "anthony_123",
        displayName = "Anthony", // Hardcoded for preview purposes
        email = "anthony@example.com",
        isAnonymous = false,
        photoUrl = "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
    )

    fun getCurrentUser(): User = currentUser

    private val members = listOf(
        Member("user_1", "Alex", "https://images.pexels.com/photos/775358/pexels-photo-775358.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_2", "Diana", "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_3", "Sam", "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_4", "Julia", "https://images.pexels.com/photos/762020/pexels-photo-762020.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_5", "Mike", "https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_6", "Chloe", "https://images.pexels.com/photos/1587009/pexels-photo-1587009.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_7", "Ben", "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_8", "Aisha", "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_9", "Omar", "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_10", "Sofia", "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
    )

    fun getTravelCategories(): List<TravelCategory> {
        return listOf(
            TravelCategory("cat_safari", "Safari", "https://images.pexels.com/photos/247376/pexels-photo-247376.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_beaches", "Beaches", "https://images.pexels.com/photos/1174732/pexels-photo-1174732.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_adventure", "Adventure", "https://images.pexels.com/photos/688660/pexels-photo-688660.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_cultural", "Cultural", "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_cities", "Cities", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_mountains", "Mountains", "https://images.pexels.com/photos/691668/pexels-photo-691668.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_deserts", "Deserts", "https://images.pexels.com/photos/688660/pexels-photo-688660.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_forests", "Forests", "https://images.pexels.com/photos/612999/pexels-photo-612999.jpeg?_gl=1*5diktz*_ga*NzgxOTUzNDc4LjE3NTgwMDkxNDc.*_ga_8JE65Q40S6*czE3NjA5NDMxNDAkbzYkZzEkdDE3NjA5NDMzODUkajYwJGwwJGgw"),
            TravelCategory("cat_islands", "Islands", "https://images.pexels.com/photos/1631677/pexels-photo-1631677.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_lakes", "Lakes", "https://images.pexels.com/photos/3408744/pexels-photo-3408744.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }

    fun getTravelStories(): List<TravelStory> {
        return listOf(
            TravelStory("story_1", "A Week in the Wilds of Kenya", members[0], "https://images.pexels.com/photos/2387873/pexels-photo-2387873.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 7),
            TravelStory("story_2", "Cape Town's Culinary Scene", members[1], "https://images.pexels.com/photos/6267/menu-restaurant-vintage-table.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 5),
            TravelStory("story_3", "Trekking in the Atlas Mountains", members[2], "https://images.pexels.com/photos/1631677/pexels-photo-1631677.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 10),
            TravelStory("story_4", "Gorilla Watching in Rwanda", members[3], "https://images.pexels.com/photos/176343/pexels-photo-176343.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 4),
            TravelStory("story_5", "Sailing the Nile", members[4], "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 8),
            TravelStory("story_6", "Zanzibar's Spice Markets", members[5], "https://images.pexels.com/photos/1174732/pexels-photo-1174732.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 6),
            TravelStory("story_7", "The Dunes of Namibia", members[6], "https://www.pexels.com/photo/green-bushes-on-desert-998653/", 9),
            TravelStory("story_8", "Discovering Ancient Lalibela", members[7], "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 3),
            TravelStory("story_9", "Victoria Falls Adventure", members[8], "https://www.pexels.com/photo/photo-of-waterfall-1875480/", 5),
            TravelStory("story_10", "Vibrant Life in Marrakech", members[9], "https://images.pexels.com/photos/3889987/pexels-photo-3889987.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 7)
        )
    }

    fun getForYouSuggestions(): List<ForYouSuggestion> {
        return listOf(
            ForYouSuggestion("foryou_1", "Top 5 Restaurants in Cairo", "Near your upcoming trip", "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.9,2000),
            ForYouSuggestion("foryou_2", "Hidden Gems of the Nile", "Based on your interest in Egypt", "https://images.pexels.com/photos/1577903/pexels-photo-1577903.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.3,1600),
            ForYouSuggestion("foryou_3", "Hiking Trails Near Cape Town", "For your adventure list", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.4,1800),
            ForYouSuggestion("foryou_4", "Best Beaches in Zanzibar", "Relax and unwind", "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.8,1700),
            ForYouSuggestion("foryou_5", "Cultural Tours in Ethiopia", "Explore history", "https://images.pexels.com/photos/931007/pexels-photo-931007.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.6,1680),
            ForYouSuggestion("foryou_6", "Safari Guide for Maasai Mara", "Plan your wildlife trip", "https://images.pexels.com/photos/247502/pexels-photo-247502.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.0,4600),
            ForYouSuggestion("foryou_7", "Day Trips from Marrakech", "Explore beyond the city", "https://images.pexels.com/photos/243138/pexels-photo-243138.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.2,2600),
            ForYouSuggestion("foryou_8", "Navigating the Souks", "A shopper's guide", "https://images.pexels.com/photos/3573382/pexels-photo-3573382.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.6,1690),
            ForYouSuggestion("foryou_9", "Photography Hotspots in Namibia", "Capture the perfect shot", "https://images.pexels.com/photos/46160/field-mountains-sky-sun-46160.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.7,1600),
            ForYouSuggestion("foryou_10", "What to Pack for Uganda", "Travel essentials", "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",4.3,1900)
        )
    }
    fun getHeroItems(): List<HeroItem>{
        return listOf(
            HeroItem("uganda_hero", "Uganda", "Discover the breathtaking Murchison Falls and diverse wildlife.", "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("kenya_hero", "Kenya", "Witness the great wildebeest migration in the Maasai Mara.", "https://images.pexels.com/photos/7139704/pexels-photo-7139704.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("egypt_hero", "Egypt", "Explore the ancient pyramids and mysteries of the Pharaohs.", "https://images.unsplash.com/photo-1569949381669-ecf31ae8e613?q=80&w=2670&auto=format&fit=crop"),
            HeroItem("tanzania_hero", "Tanzania", "Ascend Mount Kilimanjaro or relax on the beaches of Zanzibar.", "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("south_africa_hero", "South Africa", "Experience the vibrant culture of Cape Town and its stunning Table Mountain.", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("namibia_hero", "Namibia", "Marvel at the surreal landscapes of Deadvlei and the Fish River Canyon.", "https://images.pexels.com/photos/3514066/pexels-photo-3514066.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("ethiopia_hero", "Ethiopia", "Uncover the rock-hewn churches of Lalibela and ancient history.", "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("botswana_hero", "Botswana", "Glide through the Okavango Delta in a traditional mokoro.", "https://images.pexels.com/photos/1618526/pexels-photo-1618526.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("zimbabwe_hero", "Zimbabwe", "Feel the thunder of Victoria Falls, one of the Seven Natural Wonders.", "https://images.pexels.com/photos/3354427/pexels-photo-3354427.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("seychelles_hero", "Seychelles", "Relax on pristine white-sand beaches and turquoise waters.", "https://images.pexels.com/photos/1174732/pexels-photo-1174732.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }

    fun getPopularDestinations(): List<PopularDestinationItem>{
        return listOf(
            PopularDestinationItem("cameroon", "Cameroon", "https://images.pexels.com/photos/1107717/pexels-photo-1107717.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("madagascar", "Madagascar", "https://images.pexels.com/photos/756856/pexels-photo-756856.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("nigeria", "Nigeria", "https://images.pexels.com/photos/440731/pexels-photo-440731.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("rwanda", "Rwanda", "https://images.pexels.com/photos/176343/pexels-photo-176343.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("algeria", "Algeria", "https://images.pexels.com/photos/2356045/pexels-photo-2356045.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("senegal", "Senegal", "https://images.pexels.com/photos/4090647/pexels-photo-4090647.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("mozambique", "Mozambique", "https://images.pexels.com/photos/3225517/pexels-photo-3225517.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("zambia", "Zambia", "https://images.pexels.com/photos/612999/pexels-photo-612999.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"),
            PopularDestinationItem("tunisia", "Tunisia", "https://images.pexels.com/photos/777059/pexels-photo-777059.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("ivory_coast", "Côte d'Ivoire", "https://images.pexels.com/photos/5643242/pexels-photo-5643242.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }


    fun getRecommendedDestinations(): List<PopularDestinationItem>{
        return listOf(
            PopularDestinationItem("morocco", "Morocco", "https://images.pexels.com/photos/931007/pexels-photo-931007.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("ghana", "Ghana", "https://images.pexels.com/photos/3225531/pexels-photo-3225531.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("libya", "Libya", "https://images.pexels.com/photos/1624496/pexels-photo-1624496.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("sudan", "Sudan", "https://images.pexels.com/photos/8885133/pexels-photo-8885133.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("angola", "Angola", "https://images.pexels.com/photos/1078981/pexels-photo-1078981.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("gabon", "Gabon", "https://images.pexels.com/photos/2356045/pexels-photo-2356045.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("mali", "Mali", "https://images.pexels.com/photos/1485637/pexels-photo-1485637.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("somalia", "Somalia", "https://images.pexels.com/photos/440731/pexels-photo-440731.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("togo", "Togo", "https://images.pexels.com/photos/2387873/pexels-photo-2387873.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("benin", "Benin", "https://images.pexels.com/photos/2325446/pexels-photo-2325446.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }
    fun getCommunityTrips(): List<CommunityTrip> {
        return listOf(
            CommunityTrip(
                id = "comm_1",
                title = "Gorilla Trekking Adventure",
                destination = "Rwanda",
                coverImageUrl = "https://images.pexels.com/photos/176343/pexels-photo-176343.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                host = Member("three","Jenna", "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
                participantCount = 4,
                tripDate = "Sep 2024"
            ),
            CommunityTrip(
                id = "comm_2",
                title = "Sahara Desert Expedition",
                destination = "Morocco",
                coverImageUrl = "https://images.pexels.com/photos/2564491/pexels-photo-2564491.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                host = Member("one","David", "https://images.pexels.com/photos/1043474/pexels-photo-1043474.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
                participantCount = 8,
                tripDate = "Oct 2024"
            ),
            CommunityTrip(
                id = "comm_3",
                title = "Nile Cruise & History",
                destination = "Egypt",
                coverImageUrl = "https://images.pexels.com/photos/379964/pexels-photo-379964.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                host = Member("two","Maria", "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
                participantCount = 12,
                tripDate = "Nov 2024"
            )
        )
    }


}

@OptIn(ExperimentalTime::class)
@Composable
fun Greeting(name: String?) {
    val hour = remember { kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour }
    val greetingText = when (hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        Text(greetingText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!name.isNullOrBlank()) {
            Text(name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
private fun HomeDrawerContent(
    userName: String,
    sideNavItems: List<SideNavItem>,
    onNavItemClick: (SideNavItem) -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedSideNavItem by remember { mutableStateOf<SideNavItem?>(null) }

    ModalDrawerSheet(modifier = Modifier.widthIn(max = 280.dp)) {
        // Profile Header
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                contentDescription = "User Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("anthony@vaulture.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Navigation Items
        sideNavItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title) },
                icon = { Icon(item.icon, contentDescription = item.title) },
                selected = item == selectedSideNavItem,
                onClick = {
                    selectedSideNavItem = item
                    onNavItemClick(item)
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(Modifier.weight(1f)) // Pushes logout to the bottom

        // Logout
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        NavigationDrawerItem(
            label = { Text("Logout") },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") },
            selected = false,
            onClick = onLogoutClick,
            modifier = Modifier.padding(12.dp)
        )
    }
}

