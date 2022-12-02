package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fern.findaplant.databinding.FragmentObservationBinding
import com.fern.findaplant.models.Observation
import com.fern.findaplant.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ObservationFragment : Fragment() {

    private lateinit var binding: FragmentObservationBinding
    private lateinit var observation: Observation

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
        binding.commonName.text = observation.commonName
        binding.scientificName.text = observation.scientificName
        binding.metadata.text = "this is metadata info including the observer time and location"
        binding.description.text = observation.description
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