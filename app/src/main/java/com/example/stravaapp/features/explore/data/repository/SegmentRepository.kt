package com.example.stravaapp.features.explore.data.repository

import com.example.stravaapp.features.explore.data.api.SegmentService
import javax.inject.Inject

class SegmentRepository @Inject constructor(
    private val service: SegmentService
) {
    suspend fun explore(southwestBound: LatLong, northeastBound: LatLong): List<Segment> {
        val bounds = buildList {
            add(southwestBound.lat)
            add(southwestBound.long)
            add(northeastBound.lat)
            add(northeastBound.long)
        }

        return service.explore(bounds).map { dto ->
            Segment(
                name = dto.name,
                distance = dto.distance,
                averageGrade = dto.averageGrade,
            )
        }
    }
}

data class Segment(
    val name: String,
    val distance: Float,
    val averageGrade: Float,
)

data class LatLong(
    val lat: Float,
    val long: Float,
)