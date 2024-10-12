package com.example.stravaapp.features.explore.data.repository

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

        coVerify(exactly = 1) { service.explore(listOf(1f, 2f, 3f, 4f)) }
    }

    @Test
    fun givenRequestSucceeds_whenExploreSegments_thenMapDtoToModel() = runTest {
        val dto = SegmentDto(
            name = "name",
            distance = 12.4f,
            averageGrade = 4.5f
        )
        coEvery { service.explore(listOf(1f, 2f, 3f, 4f)) } returns listOf(dto)

        val southwestBound = LatLong(1f, 2f)
        val northeastBound = LatLong(3f, 4f)
        val response = sut.explore(southwestBound, northeastBound)

        assertEquals(
            listOf(
                Segment(
                    name = dto.name,
                    distance = dto.distance,
                    averageGrade = dto.averageGrade,
                )
            ),
            response
        )
    }
}