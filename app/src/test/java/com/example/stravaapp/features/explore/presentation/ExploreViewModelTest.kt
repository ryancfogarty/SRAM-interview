package com.example.stravaapp.features.explore.presentation

import com.example.stravaapp.features.explore.data.repository.LatLong
import com.example.stravaapp.features.explore.data.repository.Segment
import com.example.stravaapp.features.explore.data.repository.SegmentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
        Dispatchers.setMain(UnconfinedTestDispatcher())
        sut = ExploreViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenExploreSegments_thenDisplayLoadingState() = runTest {
        val states = mutableListOf<State>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            sut.uiState.toList(states)
        }
        sut.exploreSegments(mockk(), mockk())

        assertEquals(
            states[1],
            State.Loading
        )
    }

    @Test
    fun givenRequestSucceeds_whenExploreSegments_thenDisplaySegments() {
        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        val segment = Segment(
            name = "name",
            distance = 15.5f,
            averageGrade = 4.1f,
        )
        coEvery { repository.explore(southwestBound, northeastBound) } returns listOf(segment)

        sut.exploreSegments(southwestBound, northeastBound)

        assertEquals(
            sut.uiState.value,
            State.Success(
                segments = listOf(segment)
            )
        )
    }

    @Test
    fun givenRequestFails_whenExploreSegments_thenDisplayError() {
        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        coEvery { repository.explore(southwestBound, northeastBound) } throws Exception()

        sut.exploreSegments(southwestBound, northeastBound)

        assertEquals(
            sut.uiState.value,
            State.Error,
        )
    }
}