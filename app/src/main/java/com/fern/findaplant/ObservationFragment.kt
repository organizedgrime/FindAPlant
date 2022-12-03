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
                    updateBindings()
                }
            }

        return binding.root
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
                        // TODO make this aware of previous frag
                        (context as MainActivity).loadFragment(MyObservationsFragment())
                    }
                }
            }
        }
        binding.commonName.text = observation.commonName
        binding.scientificName.text = observation.scientificName
        binding.metadata.text = "this is metadata info including the observer time and location"
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