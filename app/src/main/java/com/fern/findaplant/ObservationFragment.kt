package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fern.findaplant.databinding.FragmentObservationBinding
import com.fern.findaplant.models.Observation
import com.fern.findaplant.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ObservationFragment : Fragment() {

    private lateinit var binding: FragmentObservationBinding
    private lateinit var observation: Observation
    private var imageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObservationBinding.inflate(layoutInflater)
        val observationID = requireArguments().getString("observationID")!!
        Firebase.firestore
            .collection("observations")
            .document(observationID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // If the document snapshot is valid
                if (documentSnapshot != null) {
                    Log.i(TAG, "Updating late init Observation")
                    // Construct an Observation object from the Snapshot
                    observation = requireNotNull(documentSnapshot.toObject<Observation>())
                    listenForUser()
                    updateBindings()
                }
            }

        return binding.root
    }

    private fun listenForUser() {
        // Listen for user document changes
        (context as MainActivity).viewModel.user.observe(viewLifecycleOwner) { user ->
            // Assign visibility based on the membership to the bookmarks array
            binding.bookmarked.visibility =
                if (user.bookmarks.map { it.id }.contains(observation.id)) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

            // When the bookmark button is clicked
            binding.bookmarkButton.setOnClickListener {
                // DocumentReference of the Observation clicked
                val reference = Firebase.firestore
                    .collection("observations")
                    .document(observation.id!!)

                val newDoc: Map<String, Any> = if (user.bookmarks.contains(reference)) {
                    hashMapOf("bookmarks" to FieldValue.arrayRemove(reference))
                } else {
                    hashMapOf("bookmarks" to FieldValue.arrayUnion(reference))
                }

                // Update the user document to reflect this
                Firebase.firestore
                    .collection("users")
                    .document(Firebase.auth.currentUser!!.uid)
                    .update(newDoc)
            }
        }
    }

    // Using a new version of the observation, update all bindings
    private fun updateBindings() {
        // If we are the observer of this Observation
        if (observation.observer?.id == Firebase.auth.currentUser?.uid) {
            // Make the delete button visible
            binding.deleteButton.visibility = View.VISIBLE
            // Link it to an action
            binding.deleteButton.setOnClickListener {
                if (observation.id != null) {
                    //
                    (context as MainActivity).viewModel.deleteObservation(observation.id!!) {
                        Log.i(TAG, "DELETED observation!")
                        (context as MainActivity).viewModel.deletePhotos("observations/${observation.id!!}", observation.photos.size) {
                            Log.i(TAG, "DELETED observation photos from Storage!")
                            // Return to previous fragment
                            parentFragmentManager.popBackStack()
                        }
                    }


                }
            }
        }
        binding.commonName.text = observation.commonName
        binding.scientificName.text = observation.scientificName
        val geoText = observation.coordinate.toString()
        val timeText = observation.timestamp.toDate().toString()
        binding.metadata.text = "$geoText\n$timeText"
        binding.description.text = observation.description
        binding.nextImage.setOnClickListener {
            if (imageIndex < observation.photos.size - 1) {
                imageIndex += 1
                loadCurrentImage()
            }
        }
        binding.previousImage.setOnClickListener {
            if (imageIndex > 0) {
                imageIndex -= 1
                loadCurrentImage()
            }
        }
        loadCurrentImage()
    }

    private fun loadCurrentImage() {
        // If there is at least one image to display in the observation
        if (observation.photos.isNotEmpty()) {
            // Load image from object into thumbnail
            Glide
                .with(binding.currentImage.context)
                .load(observation.photos[imageIndex])
                .into(binding.currentImage)
        }
    }

    companion object {
        const val TAG = "ObservationFragment"

        fun newInstance(observationID: String): ObservationFragment {
            val fragment = ObservationFragment()
            val args = Bundle()
            args.putString("observationID", observationID)
            fragment.arguments = args
            return fragment
        }
    }
}