package com.fern.findaplant.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

/**
 * @param id: Unique Observation ID
 * @param description: User-given description for Observation
 * @param commonName: Common name of the organism
 * @param speciesName: Species name of the organism
 * @param coordinate: Latitude and Longitude of the Observation
 * @param timestamp: Firestore timestamp type giving time of Observation. Given by device
 */
data class Observation(
    @DocumentId
    val id: String,
    val description: String,
    val commonName: String,
    val speciesName: String,
    val coordinate: GeoPoint,
    val timestamp: Timestamp,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Observation
        return id == other.id
    }

    override fun hashCode(): Int { return id.hashCode() }
}