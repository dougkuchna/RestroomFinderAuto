package com.restroomfinder.auto.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    enum class DistanceUnit {
        METRIC, IMPERIAL
    }

    var distanceUnit: DistanceUnit
        get() {
            val unit = prefs.getString(KEY_DISTANCE_UNIT, DistanceUnit.METRIC.name)
            return DistanceUnit.valueOf(unit ?: DistanceUnit.METRIC.name)
        }
        set(value) {
            prefs.edit().putString(KEY_DISTANCE_UNIT, value.name).apply()
        }

    companion object {
        private const val KEY_DISTANCE_UNIT = "distance_unit"
    }
}
