package com.restroomfinder.auto.auto

import android.Manifest
import android.content.pm.PackageManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.restroomfinder.auto.RestroomFinderApplication
import com.restroomfinder.auto.data.model.Restroom
import com.restroomfinder.auto.location.LocationProvider
import kotlinx.coroutines.launch

class RestroomListScreen(carContext: CarContext) : Screen(carContext) {

    private var restrooms: List<Restroom> = emptyList()
    private var isLoading = true
    private var errorMessage: String? = null

    private val locationProvider = LocationProvider(carContext)
    private val repository = RestroomFinderApplication.instance.restroomRepository

    init {
        loadRestrooms()
    }

    private fun loadRestrooms() {
        if (ContextCompat.checkSelfPermission(
                carContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLoading = false
            errorMessage = "Location permission required"
            invalidate()
            return
        }

        lifecycleScope.launch {
            try {
                val location = locationProvider.getCurrentLocation()
                if (location != null) {
                    restrooms = repository.findNearbyRestrooms(
                        location.latitude,
                        location.longitude
                    )
                    isLoading = false
                    errorMessage = if (restrooms.isEmpty()) "No restrooms found nearby" else null
                } else {
                    isLoading = false
                    errorMessage = "Unable to get location"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error loading restrooms"
            }
            invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        if (isLoading) {
            return buildLoadingTemplate()
        }

        if (errorMessage != null) {
            return buildErrorTemplate(errorMessage!!)
        }

        return buildListTemplate()
    }

    private fun buildLoadingTemplate(): Template {
        return MessageTemplate.Builder("Finding nearby restrooms...")
            .setTitle("Restroom Finder")
            .setLoading(true)
            .build()
    }

    private fun buildErrorTemplate(message: String): Template {
        return MessageTemplate.Builder(message)
            .setTitle("Restroom Finder")
            .addAction(
                Action.Builder()
                    .setTitle("Retry")
                    .setOnClickListener {
                        isLoading = true
                        invalidate()
                        loadRestrooms()
                    }
                    .build()
            )
            .build()
    }

    private fun buildListTemplate(): Template {
        val listBuilder = ItemList.Builder()

        restrooms.take(6).forEach { restroom ->
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(restroom.name)
                    .addText(restroom.address)
                    .addText(buildRestroomSubtext(restroom))
                    .setOnClickListener {
                        screenManager.push(RestroomDetailScreen(carContext, restroom))
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setTitle("Nearby Restrooms")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle("Refresh")
                            .setOnClickListener {
                                isLoading = true
                                invalidate()
                                loadRestrooms()
                            }
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun buildRestroomSubtext(restroom: Restroom): String {
        val parts = mutableListOf<String>()
        parts.add(restroom.getFormattedDistance())
        if (restroom.isAccessible) parts.add("♿")
        if (restroom.isUnisex) parts.add("⚧")
        if (restroom.hasChangingTable) parts.add("🚼")
        return parts.joinToString(" • ")
    }
}
