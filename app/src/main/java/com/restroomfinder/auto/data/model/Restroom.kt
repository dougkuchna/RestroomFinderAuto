package com.restroomfinder.auto.data.model

import com.google.gson.annotations.SerializedName

data class Restroom(
    val id: Long,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("accessible")
    val isAccessible: Boolean = false,
    @SerializedName("unisex")
    val isUnisex: Boolean = false,
    @SerializedName("changing_table")
    val hasChangingTable: Boolean = false,
    val directions: String? = null,
    val comment: String? = null,
    @SerializedName("distance")
    val distanceMeters: Int = 0
) {
    fun getFormattedDistance(): String {
        return when {
            distanceMeters < 1000 -> "${distanceMeters}m"
            else -> String.format("%.1fkm", distanceMeters / 1000.0)
        }
    }

    fun getAccessibilityInfo(): String {
        val features = mutableListOf<String>()
        if (isAccessible) features.add("Accessible")
        if (isUnisex) features.add("Unisex")
        if (hasChangingTable) features.add("Changing Table")
        return features.joinToString(" • ")
    }
}
