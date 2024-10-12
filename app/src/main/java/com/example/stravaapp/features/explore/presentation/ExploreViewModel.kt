package com.example.stravaapp.features.explore.presentation

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.stravaapp.features.explore.data.repository.LatLong
import com.example.stravaapp.features.explore.data.repository.SegmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val segmentRepository: SegmentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    @OptIn(SavedStateHandleSaveableApi::class)
    var uiState: State by savedStateHandle.saveable {
        mutableStateOf(State.Initialized)
    }
        private set

    fun exploreSegments(
        southwestBound: LatLong,
        northeastBound: LatLong,
    ) {
        uiState = State.Loading
        viewModelScope.launch {
            uiState = try {
                State.Success(
                    segments = segmentRepository.explore(southwestBound, northeastBound)
                        .map { segment ->
                            SegmentUI(
                                name = segment.name,
                                distance = segment.distance,
                                averageGrade = segment.averageGrade
                            )
                        }
                )
            } catch (t: Throwable) {
                State.Error
            }
        }
    }
}

interface State : Parcelable {
    @Parcelize
    object Initialized : State

    @Parcelize
    object Loading : State

    @Parcelize
    object Error : State

    @Parcelize
    data class Success(
        val segments: List<SegmentUI>
    ) : State
}

@Parcelize
data class SegmentUI(
    val name: String,
    val averageGrade: Float,
    val distance: Float,
) : Parcelable