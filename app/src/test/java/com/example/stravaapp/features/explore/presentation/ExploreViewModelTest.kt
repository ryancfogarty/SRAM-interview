package com.example.stravaapp.features.explore.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.stravaapp.features.explore.data.repository.LatLong
import com.example.stravaapp.features.explore.data.repository.Segment
import com.example.stravaapp.features.explore.data.repository.SegmentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {
    private val repository: SegmentRepository = mockk(relaxed = true)
    private lateinit var sut: ExploreViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        sut = ExploreViewModel(repository, SavedStateHandle())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenExploreSegments_thenDisplayLoadingState() = runTest {
        sut.exploreSegments(mockk(), mockk())

        assertEquals(
            State.Loading,
            sut.uiState
        )
    }

    @Test
    fun givenRequestSucceeds_whenExploreSegments_thenDisplaySegments() = runTest {
        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        val segment = Segment(
            name = "name",
            distance = 15.5f,
            averageGrade = 4.1f,
        )
        coEvery { repository.explore(southwestBound, northeastBound) } returns listOf(segment)

        sut.exploreSegments(southwestBound, northeastBound)
        advanceUntilIdle()

        assertEquals(
            State.Success(
                segments = listOf(
                    SegmentUI(
                        name = segment.name,
                        distance = segment.distance,
                        averageGrade = segment.averageGrade,
                    )
                )
            ),
            sut.uiState,
        )
    }

    @Test
    fun givenRequestFails_whenExploreSegments_thenDisplayError() = runTest {
        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        coEvery { repository.explore(southwestBound, northeastBound) } throws Exception()

        sut.exploreSegments(southwestBound, northeastBound)
        advanceUntilIdle()

        assertEquals(
            State.Error,
            sut.uiState,
        )
    }
}