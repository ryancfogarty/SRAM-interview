package com.example.stravaapp.features.explore.data.api

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query

interface SegmentService {
    @GET("explore")
    suspend fun explore(@Query("bounds") bounds: String): ExploreResponseDto

}

class ExploreResponseDto(
    val segments: List<SegmentDto>
)

class SegmentDto(
    val name: String,
    val distance: Float,
    @Json(name = "avg_grade") val averageGrade: Float,
)