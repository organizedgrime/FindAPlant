package com.fern.findaplant

import android.util.Log
import androidx.lifecycle.*
import com.fern.findaplant.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivityViewModel: ViewModel(), DefaultLifecycleObserver {
    // Reference to the user Document
    private lateinit var userReference: DocumentReference

    // The User object that will be loaded in from Firestore
    private var _user: MutableLiveData<User> = MutableLiveData<User>()
    internal val user: LiveData<User> get() = _user

    internal fun bindToActivityLifecycle(mainActivity: MainActivity) {
        // Add the current instance of CounterViewModel as a LifeCycleObserver to the MainActivity
        mainActivity.lifecycle.addObserver(this);
    }

    override fun onCreate(owner: LifecycleOwner) {
        // Update the appropriate count variable
        Log.i(TAG, "Entered onCreate")
        Log.i(TAG, "Searching for user with auth uid ${Firebase.auth.currentUser!!.uid}")

        // Initialize the reference using the current user's uid
        userReference = Firebase.firestore
            .collection("users")
            .document(Firebase.auth.currentUser!!.uid)

        // Add a snapshot listener to the user reference
        userReference.addSnapshotListener { documentSnapshot, _ ->
            // If the document snapshot is valid
            if (documentSnapshot != null) {
                Log.i(TAG, "Updating MutableLiveData")
                // Construct a User object from the Snapshot
                _user.value = requireNotNull(documentSnapshot.toObject<User>())
            }
        }
    }

    // Updates the Firestore User Document with a new profile picture
    fun updateProfilePicture(newURL: String) {
        val userDoc: Map<String, Any> = hashMapOf(
            "profilePicture" to newURL
        )

        // Update the user reference
        userReference
            .update(userDoc)
            .addOnSuccessListener {
                Log.i(TAG, "Profile Picture updated successfully in UserDoc")
            }
            .addOnFailureListener {
                Log.i(TAG, "Profile Picture failed to update in UserDoc")
            }
    }

    companion object {
        const val TAG = "MainActivityViewModel"
    }
}