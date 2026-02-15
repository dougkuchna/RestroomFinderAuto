package com.restroomfinder.auto.auto

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.restroomfinder.auto.RestroomFinderApplication
import com.restroomfinder.auto.data.model.Restroom
import com.restroomfinder.auto.location.LocationProvider
import kotlinx.coroutines.launch
import java.util.Locale

class RestroomListScreen(carContext: CarContext) : Screen(carContext) {

    companion object {
        private const val ITEMS_PER_PAGE = 5 // Android Auto list limit is 6, minus 1 for location header
    }

    private var restrooms: List<Restroom> = emptyList()
    private var isLoading = true
    private var errorMessage: String? = null
    private var currentPage = 0
    private var currentLocation: Location? = null
    private var currentLocationName: String? = null

    private val locationProvider = LocationProvider(carContext)
    private val repository = RestroomFinderApplication.instance.restroomRepository
    private val preferencesManager = RestroomFinderApplication.instance.preferencesManager

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
                    currentLocation = location
                    currentLocationName = getLocationName(location)
                    restrooms = repository.findNearbyRestrooms(
                        location.latitude,
                        location.longitude,
                        perPage = preferencesManager.maxResults
                    )
                    currentPage = 0
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
        val startIndex = currentPage * ITEMS_PER_PAGE
        val endIndex = minOf(startIndex + ITEMS_PER_PAGE, restrooms.size)
        val currentPageItems = restrooms.subList(startIndex, endIndex)
        val hasMoreItems = endIndex < restrooms.size
        val hasPreviousItems = currentPage > 0

        // Add location header row
        val locationText = currentLocationName ?: currentLocation?.let {
            "%.4f, %.4f".format(it.latitude, it.longitude)
        } ?: "Unknown location"
        listBuilder.addItem(
            Row.Builder()
                .setTitle("📍 $locationText")
                .build()
        )

        currentPageItems.forEach { restroom ->
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

        val actionStripBuilder = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setTitle("Refresh")
                    .setOnClickListener {
                        isLoading = true
                        currentPage = 0
                        invalidate()
                        loadRestrooms()
                    }
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("Settings")
                    .setOnClickListener {
                        screenManager.push(SettingsScreen(carContext))
                    }
                    .build()
            )

        if (hasPreviousItems) {
            actionStripBuilder.addAction(
                Action.Builder()
                    .setTitle("Previous")
                    .setOnClickListener {
                        currentPage--
                        invalidate()
                    }
                    .build()
            )
        }

        if (hasMoreItems) {
            actionStripBuilder.addAction(
                Action.Builder()
                    .setTitle("Show More")
                    .setOnClickListener {
                        currentPage++
                        invalidate()
                    }
                    .build()
            )
        }

        val totalPages = (restrooms.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
        val title = if (totalPages > 1) {
            "Nearby Restrooms (${currentPage + 1}/$totalPages)"
        } else {
            "Nearby Restrooms"
        }

        return ListTemplate.Builder()
            .setTitle(title)
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .setActionStrip(actionStripBuilder.build())
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

    @Suppress("DEPRECATION")
    private fun getLocationName(location: Location): String? {
        return try {
            val geocoder = Geocoder(carContext, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var result: String? = null
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    result = addresses.firstOrNull()?.let { address ->
                        listOfNotNull(
                            address.thoroughfare,
                            address.locality
                        ).joinToString(", ").ifEmpty { address.getAddressLine(0) }
                    }
                }
                result
            } else {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                addresses?.firstOrNull()?.let { address ->
                    listOfNotNull(
                        address.thoroughfare,
                        address.locality
                    ).joinToString(", ").ifEmpty { address.getAddressLine(0) }
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
