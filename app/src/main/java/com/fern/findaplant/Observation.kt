package com.fern.findaplant

import com.google.firebase.Timestamp
/**
 * @param id: Unique Observation ID
 * @param userID: Firebase Auth User ID for user who generated Observation
 * @param description: User-given description for Observation
 * @param species: User-given species name observed in Observation
 * @param lat: Latitude of the Observation given by device
 * @param long: Longitude of the Observation given by device
 * @param geoHash: Geohash of lat and long, see: https://firebase.google.com/docs/firestore/solutions/geoqueries
 * @param timestamp: Firestore timestamp type giving time of Observation. Given by device
 * @param photoUrlArray: An array containing URLs for the photos associated with the observation
 */
data class Observation(val id: Int, val userId: String, val description: String,
                       val species: String, val lat: Float, val long: Float,
                       val geoHash: String, val timestamp: Timestamp,
                       val photoUrlArray: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Observation
        return id == other.id
    }
    override fun hashCode(): Int { return id }
}

// I'm not yet sure what the best way to handle photos is