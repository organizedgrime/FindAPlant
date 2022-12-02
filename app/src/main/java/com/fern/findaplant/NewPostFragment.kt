package com.fern.findaplant

import android.annotation.SuppressLint
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fern.findaplant.databinding.FragmentNewPostBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class NewPostFragment : Fragment() {

    private lateinit var binding: FragmentNewPostBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mainViewModel: MainActivityViewModel
    private var coordinate: GeoPoint = GeoPoint(0.0, 0.0)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        obtainLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(layoutInflater)
        mainViewModel = (requireContext() as MainActivity).viewModel

        // Extract file paths
        val paths = requireArguments().getStringArrayList("paths")!!
        // Represent as URIs
        val uris = paths.map { Uri.fromFile(File(it)) }

        // Update the image to be the first
        binding.observationImage.setImageURI(uris[0])
        // Update the time label
        binding.timeLabel.text = Date().toString()

        val observationID = UUID.randomUUID().toString()
        Log.i(TAG, "New observation id is $observationID")

        binding.sendButton.setOnClickListener {
            // Upload the photo associated with it
            mainViewModel
                // TODO replace this with a function that will allow us
                //  to upload multiple photos at once
                .uploadPhotos("observations/$observationID", uris) { urls ->
                    // Construct a map
                    val map: MutableMap<String, Any> = hashMapOf(
                        "photos" to urls,
                        "coordinate" to coordinate,
                        "timestamp" to Timestamp(Date()),
                        "description" to binding.description.text.toString()
                    )

                    // Post the observation with these params and id
                    mainViewModel.postObservation(observationID, map) {
                        // Having succeeded in posting the observation, show the user them
                        (requireContext() as MainActivity).loadFragment(ObservationsFragment())
                    }
                }
        }

        return binding.root
    }

    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                coordinate = GeoPoint(location!!.latitude, location.longitude)
            }
    }

    companion object {
        const val TAG = "NewPostFragment"

        fun newInstance(paths: ArrayList<String>): NewPostFragment {
            val fragment = NewPostFragment()
            val args = Bundle()
            args.putStringArrayList("paths", paths)
            fragment.arguments = args
            return fragment
        }
    }
}