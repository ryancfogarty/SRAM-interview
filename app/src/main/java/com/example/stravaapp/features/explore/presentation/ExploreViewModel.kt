package com.example.stravaapp.features.explore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stravaapp.features.explore.data.repository.LatLong
import com.example.stravaapp.features.explore.data.repository.Segment
import com.example.stravaapp.features.explore.data.repository.SegmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExploreViewModel @Inject constructor(
    private val segmentRepository: SegmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<State>(State.Initialized)
    val uiState: StateFlow<State> = _uiState

    fun exploreSegments(
        southwestBound: LatLong,
        northeastBound: LatLong,
    ) {
        _uiState.value = State.Loading
        viewModelScope.launch {
            try {
                _uiState.value = State.Success(
                    segments = segmentRepository.explore(southwestBound, northeastBound)
                )
            } catch (_: Throwable) {
                _uiState.value = State.Error
            }
        }
    }
}

interface State {
    object Initialized : State
    object Loading : State
    object Error : State
    data class Success(
        val segments: List<Segment>
    ) : State
}