package com.restroomfinder.auto.data.repository

import android.content.Context
import com.restroomfinder.auto.data.model.Restroom
import com.restroomfinder.auto.data.remote.RestroomApi
import com.restroomfinder.auto.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestroomRepository(private val context: Context) {

    private val api: RestroomApi = RetrofitClient.restroomApi

    suspend fun findNearbyRestrooms(
        latitude: Double,
        longitude: Double,
        accessibleOnly: Boolean = false,
        unisexOnly: Boolean = false,
        perPage: Int = 20
    ): List<Restroom> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchByLocation(
                lat = latitude,
                lng = longitude,
                accessible = accessibleOnly,
                unisex = unisexOnly,
                perPage = perPage
            )
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Return empty list on network errors
            emptyList()
        }
    }

    suspend fun getRestroomById(id: Long): Restroom? = withContext(Dispatchers.IO) {
        try {
            val response = api.getRestroom(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
