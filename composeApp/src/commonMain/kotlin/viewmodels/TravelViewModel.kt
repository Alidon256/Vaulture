package viewmodels

import data.models.TravelDestination
import data.repository.TravelRepository
import data.repository.TravelRepositoryImpl
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


open class TravelViewModel : ViewModel() {
    private val repository: TravelRepository = TravelRepositoryImpl()
    
    private var _uiState = MutableStateFlow(TravelUiState())
    open var uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()
    
    init {
        loadDestinations()
    }
    
    private fun loadDestinations() {
        viewModelScope.launch {
            repository.getAllDestinations().collect { destinations ->
                _uiState.update { currentState ->
                    currentState.copy(
                        destinations = destinations,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }
    
    fun selectDestination(destination: TravelDestination) {
        _uiState.update { it.copy(selectedDestination = destination) }
    }
    
    fun clearSelectedDestination() {
        _uiState.update { it.copy(selectedDestination = null) }
    }
    
    fun showFavoritesOnly(show: Boolean) {
        _uiState.update { it.copy(showFavoritesOnly = show) }
        if (show) {
            viewModelScope.launch {
                repository.getFavoriteDestinations().collect { favorites ->
                    _uiState.update { it.copy(destinations = favorites) }
                }
            }
        } else {
            loadDestinations()
        }
    }
}

data class TravelUiState(
    val destinations: List<TravelDestination> = emptyList(),
    val selectedDestination: TravelDestination? = null,
    val isLoading: Boolean = true,
    val showFavoritesOnly: Boolean = false,
    val error: String? = null
)