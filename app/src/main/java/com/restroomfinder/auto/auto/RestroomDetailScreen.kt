package com.restroomfinder.auto.auto

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.restroomfinder.auto.data.model.Restroom

class RestroomDetailScreen(
    carContext: CarContext,
    private val restroom: Restroom
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val paneBuilder = Pane.Builder()

        // Add restroom information rows
        paneBuilder.addRow(
            Row.Builder()
                .setTitle("Address")
                .addText(restroom.address)
                .build()
        )

        paneBuilder.addRow(
            Row.Builder()
                .setTitle("Distance")
                .addText(restroom.getFormattedDistance())
                .build()
        )

        // Add features row if any features exist
        val features = restroom.getAccessibilityInfo()
        if (features.isNotEmpty()) {
            paneBuilder.addRow(
                Row.Builder()
                    .setTitle("Features")
                    .addText(features)
                    .build()
            )
        }

        // Add directions if available
        if (!restroom.directions.isNullOrBlank()) {
            paneBuilder.addRow(
                Row.Builder()
                    .setTitle("Directions")
                    .addText(restroom.directions)
                    .build()
            )
        }

        // Add comments if available
        if (!restroom.comment.isNullOrBlank()) {
            paneBuilder.addRow(
                Row.Builder()
                    .setTitle("Notes")
                    .addText(restroom.comment)
                    .build()
            )
        }

        // Add navigation action
        paneBuilder.addAction(
            Action.Builder()
                .setTitle("Navigate")
                .setOnClickListener {
                    navigateToRestroom()
                }
                .build()
        )

        return PaneTemplate.Builder(paneBuilder.build())
            .setTitle(restroom.name)
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun navigateToRestroom() {
        val gmmIntentUri = Uri.parse(
            "google.navigation:q=${restroom.latitude},${restroom.longitude}"
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        carContext.startCarApp(mapIntent)
    }
}
