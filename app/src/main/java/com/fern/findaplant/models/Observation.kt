package com.fern.findaplant.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * @param id: Unique Observation ID
 * @param description: User-given description for Observation
 * @param commonName: Common name of the organism
 * @param scientificName: Scientific name of the organism
 * @param coordinate: Latitude and Longitude of the Observation
 * @param timestamp: Firestore timestamp type giving time of Observation. Given by device
 */
@IgnoreExtraProperties
data class Observation(
    @DocumentId
    val id: String,
    val description: String,
    val commonName: String,
    val scientificName: String,
    val coordinate: GeoPoint,
    val timestamp: Timestamp,
    var photo: String? = null,
) {
    companion object {
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_COMMON_NAME = "commonName"
        const val FIELD_SCIENTIFIC_NAME = "scientificName"
        const val FIELD_COORDINATE = "coordinate"
        const val FIELD_TIMESTAMP = "timestamp"
    }
}