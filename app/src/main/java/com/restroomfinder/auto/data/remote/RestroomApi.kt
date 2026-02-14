package com.restroomfinder.auto.data.remote

import com.restroomfinder.auto.data.model.Restroom
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API interface for Refuge Restrooms (https://www.refugerestrooms.org)
 * A public API for finding restroom locations.
 */
interface RestroomApi {

    @GET("restrooms/by_location")
    suspend fun searchByLocation(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("ada") accessible: Boolean = false,
        @Query("unisex") unisex: Boolean = false,
        @Query("per_page") perPage: Int = 20
    ): Response<List<Restroom>>

    @GET("restrooms/{id}")
    suspend fun getRestroom(
        @Path("id") id: Long
    ): Response<Restroom>

    @GET("restrooms/search")
    suspend fun searchByQuery(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20
    ): Response<List<Restroom>>
}
