package com.restroomfinder.auto.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    var maxResults: Int
        get() = prefs.getInt(KEY_MAX_RESULTS, DEFAULT_MAX_RESULTS)
        set(value) = prefs.edit().putInt(KEY_MAX_RESULTS, value).apply()

    companion object {
        private const val PREFS_NAME = "restroom_finder_prefs"
        private const val KEY_MAX_RESULTS = "max_results"
        const val DEFAULT_MAX_RESULTS = 20

        val RESULTS_OPTIONS = listOf(10, 20, 30, 40, 50)
    }
}
