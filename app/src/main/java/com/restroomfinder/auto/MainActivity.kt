package com.restroomfinder.auto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.restroomfinder.auto.databinding.ActivityMainBinding
import com.restroomfinder.auto.location.LocationProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationProvider: LocationProvider

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                loadNearbyRestrooms()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                loadNearbyRestrooms()
            }
            else -> {
                binding.statusText.text = getString(R.string.location_permission_denied)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationProvider = LocationProvider(this)

        binding.findRestroomsButton.setOnClickListener {
            checkLocationPermissionAndLoad()
        }

        binding.refreshButton.setOnClickListener {
            checkLocationPermissionAndLoad()
        }
    }

    private fun checkLocationPermissionAndLoad() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadNearbyRestrooms()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun loadNearbyRestrooms() {
        binding.statusText.text = getString(R.string.searching)

        lifecycleScope.launch {
            try {
                val location = locationProvider.getCurrentLocation()
                if (location != null) {
                    val repository = (application as RestroomFinderApplication).restroomRepository
                    val restrooms = repository.findNearbyRestrooms(
                        location.latitude,
                        location.longitude
                    )

                    if (restrooms.isNotEmpty()) {
                        val nearest = restrooms.first()
                        binding.statusText.text = getString(
                            R.string.nearest_restroom_found,
                            nearest.name,
                            nearest.distanceMeters
                        )
                        binding.restroomDetails.text = buildString {
                            append("Address: ${nearest.address}\n")
                            append("Distance: ${nearest.distanceMeters}m\n")
                            if (nearest.isAccessible) append("♿ Accessible\n")
                            if (nearest.hasChangingTable) append("🚼 Changing Table\n")
                            if (nearest.isUnisex) append("⚧ Unisex")
                        }
                    } else {
                        binding.statusText.text = getString(R.string.no_restrooms_found)
                    }
                } else {
                    binding.statusText.text = getString(R.string.location_unavailable)
                }
            } catch (e: Exception) {
                binding.statusText.text = getString(R.string.error_finding_restrooms)
            }
        }
    }
}
