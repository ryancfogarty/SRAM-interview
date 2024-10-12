package com.example.stravaapp.features.explore.data.repository

import com.example.stravaapp.features.explore.data.api.ExploreResponseDto
import com.example.stravaapp.features.explore.data.api.SegmentDto
import com.example.stravaapp.features.explore.data.api.SegmentService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SegmentRepositoryTest {
    private val service: SegmentService = mockk(relaxed = true)
    private lateinit var sut: SegmentRepository

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        sut = SegmentRepository(service)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun callsServiceWithBounds() = runTest {
        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        sut.explore(southwestBound, northeastBound)

        coVerify(exactly = 1) { service.explore("1.0,2.0,3.0,4.0") }
    }

    @Test
    fun givenRequestSucceeds_whenExploreSegments_thenMapDtoToModel() = runTest {
        val segmentDto = SegmentDto(
            name = "name",
            distance = 12.4f,
            averageGrade = 4.5f
        )
        val responseDto = ExploreResponseDto(listOf(segmentDto))
        coEvery { service.explore("1.0,2.0,3.0,4.0") } returns responseDto

        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        val response = sut.explore(southwestBound, northeastBound)

        assertEquals(
            listOf(
                Segment(
                    name = segmentDto.name,
                    distance = segmentDto.distance,
                    averageGrade = segmentDto.averageGrade,
                )
            ),
            response
        )
    }
}