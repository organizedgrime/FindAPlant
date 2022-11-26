package com.fern.findaplant.database

import android.util.Log
import com.fern.findaplant.models.Observation
import com.fern.findaplant.models.User
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class PlantFirestore {
    // Initialize the firestore instance
    private val db = Firebase.firestore

    fun getUser(uid: String): User? {
        // Start out as null
        var user: User? = null
        // Get the user document at the provided document ID
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // If we are successful, set the user object
                user = documentSnapshot.toObject<User>()
            }

        // Return the user object we obtained or did not
        return user
    }

    fun getObservationsByUser(uid: String): List<Observation> {
        return emptyList()
    }

    fun getObservationsByName(name: String): List<Observation> {
        return emptyList()
    }

    fun getObservationsByArea(coordinate: GeoPoint, radius: Float): List<Observation> {
        return emptyList()
    }

    companion object {
        const val TAG = "PlantFirestore"
    }
}