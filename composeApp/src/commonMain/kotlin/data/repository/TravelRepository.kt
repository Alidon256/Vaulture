package org.vaulture.com.data.repository

import org.vaulture.com.data.models.TravelDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TravelRepository {
    fun getAllDestinations(): Flow<List<TravelDestination>>
    fun getDestinationById(id: String): Flow<TravelDestination?>
    suspend fun toggleFavorite(id: String)
    fun getFavoriteDestinations(): Flow<List<TravelDestination>>
}

class TravelRepositoryImpl : TravelRepository {
    private val _destinations = MutableStateFlow<List<TravelDestination>>(generateDummyDestinations())
    
    override fun getAllDestinations(): Flow<List<TravelDestination>> = _destinations.asStateFlow()
    
    override fun getDestinationById(id: String): Flow<TravelDestination?> {
        return MutableStateFlow(_destinations.value.find { it.id == id })
    }
    
    override suspend fun toggleFavorite(id: String) {
        _destinations.update { destinations ->
            destinations.map { destination ->
                if (destination.id == id) {
                    destination.copy(isFavorite = !destination.isFavorite)
                } else {
                    destination
                }
            }
        }
    }
    
    override fun getFavoriteDestinations(): Flow<List<TravelDestination>> {
        return MutableStateFlow(_destinations.value.filter { it.isFavorite })
    }
    
    private fun generateDummyDestinations(): List<TravelDestination> {
        return listOf(
            TravelDestination(
                id = "1",
                name = "Bali, Indonesia",
                description = "A beautiful island known for its forested volcanic mountains, iconic rice paddies, beaches and coral reefs.",
                imageUrl = "travel.webp",
                rating = 4.8f,
                price = 1200.0,
                location = "Indonesia",
                tags = listOf("Beach", "Culture", "Nature")
            ),
            TravelDestination(
                id = "2",
                name = "Paris, France",
                description = "The City of Light is known for its iconic Eiffel Tower, world-class cuisine, and charming streets.",
                imageUrl = "travel2.webp",
                rating = 4.7f,
                price = 1500.0,
                location = "France",
                tags = listOf("City", "Culture", "Food")
            ),
            TravelDestination(
                id = "3",
                name = "Kyoto, Japan",
                description = "A city famous for its numerous classical Buddhist temples, gardens, imperial palaces, Shinto shrines and traditional wooden houses.",
                imageUrl = "travel.webp",
                rating = 4.9f,
                price = 1800.0,
                location = "Japan",
                tags = listOf("Culture", "History", "Nature")
            ),
            TravelDestination(
                id = "4",
                name = "Santorini, Greece",
                description = "Known for its stunning white-washed buildings with blue domes, overlooking the sea.",
                imageUrl = "travel2.webp",
                rating = 4.6f,
                price = 1600.0,
                location = "Greece",
                tags = listOf("Beach", "Romantic", "Views")
            ),
            TravelDestination(
                id = "5",
                name = "New York City, USA",
                description = "The Big Apple offers world-famous attractions, incredible shopping, diverse dining, and vibrant culture.",
                imageUrl = "travel.webp",
                rating = 4.5f,
                price = 2000.0,
                location = "USA",
                tags = listOf("City", "Shopping", "Culture")
            )
        )
    }
}