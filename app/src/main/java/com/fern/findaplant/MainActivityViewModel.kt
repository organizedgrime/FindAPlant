package com.fern.findaplant

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.fern.findaplant.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.whenAllSuccess
import com.google.android.gms.tasks.Tasks.whenAllComplete
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList


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

    fun uploadPhoto(path: String, uri: Uri, onSuccess: (url: String) -> Unit) {
        Firebase.storage.reference
            .child(path)
            .putFile(uri)
            .onSuccessTask {
                // Obtain the URL for the image we just uploaded
                Firebase.storage.reference
                    .child(path)
                    .downloadUrl
            }
            .addOnCompleteListener { url ->
                if (url.isSuccessful) {
                    // Run the callback
                    onSuccess(url.toString())
                }
                else {
                    Log.w(TAG, "Failed to upload new photo to Storage")
                }
            }
    }

    // Upload multiple photos at once
    fun uploadPhotos(basePath: String, uris: List<Uri>, onSuccess: (urls: List<String>) -> Unit) {
        val children = List(uris.size) { index ->
            "$basePath/$index"
        }

        // Create a list of Tasks based on URIs and path
        val tasks = uris.mapIndexed { index, uri ->
            Firebase.storage.reference
                .child(children[index])
                .putFile(uri)
                .onSuccessTask {
                    Firebase.storage.reference
                        .child(children[index])
                        .downloadUrl
                }
        }

        // Wait for the upload tasks complete
        whenAllSuccess<Uri>(tasks)
            .addOnSuccessListener { urlResults ->
                // Call onSuccess
                onSuccess(urlResults.map { url -> url.toString() })
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

    fun postObservation(
        observationID: String,
        map: MutableMap<String, Any>,
        onSuccess: () -> Unit
    ) {
        map["commonName"] = "commonName template"
        map["scientificName"] = "scientificName template"
        map["observer"] = Firebase.firestore.collection("users").document(user.value?.id!!)

        // Finally, create a new document in the `observations` collection using it
        Firebase.firestore
            .collection("observations")
            .document(observationID)
            .set(map)
            .addOnSuccessListener {
                Log.i(TAG, "New Observation posted successfully")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e(TAG, "New Observation failed to post")
            }
    }

    companion object {
        const val TAG = "MainActivityViewModel"
    }
}