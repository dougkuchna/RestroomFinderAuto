package com.restroomfinder.auto.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.restroomfinder.auto.RestroomFinderApplication
import com.restroomfinder.auto.data.PreferencesManager

class SettingsScreen(carContext: CarContext) : Screen(carContext) {

    private val preferencesManager = RestroomFinderApplication.instance.preferencesManager

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        PreferencesManager.RESULTS_OPTIONS.forEach { count ->
            val isSelected = preferencesManager.maxResults == count
            listBuilder.addItem(
                Row.Builder()
                    .setTitle("$count restrooms")
                    .addText(if (isSelected) "✓ Selected" else "")
                    .setOnClickListener {
                        preferencesManager.maxResults = count
                        invalidate()
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setTitle("Max Results to Load")
            .setHeaderAction(Action.BACK)
            .setSingleList(listBuilder.build())
            .build()
    }
}
