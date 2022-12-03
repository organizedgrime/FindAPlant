package com.fern.findaplant

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fern.findaplant.databinding.FragmentNewPostBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class NewPostFragment : Fragment() {
    private var imageIndex = 0

    private lateinit var paths: ArrayList<String>
    private lateinit var uris: List<Uri>
    private lateinit var binding: FragmentNewPostBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mainViewModel: MainActivityViewModel
    private var coordinate: GeoPoint = GeoPoint(0.0, 0.0)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If permissions are already granted
        if (allPermissionsGranted()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            obtainLocation()
        }
        // Otherwise
        else {
            // Request required permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
            // Relaunch the activity so that the updated permissions set is processed
            (requireContext() as MainActivity).loadFragment(newInstance(paths))
        }
    }

    // Check if all the necessary permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(layoutInflater)
        mainViewModel = (requireContext() as MainActivity).viewModel

        // Extract file paths
        paths = requireArguments().getStringArrayList("paths")!!
        // Represent as URIs
        uris = paths.map { Uri.fromFile(File(it)) }
        // Update the image to be the first
        loadCurrentImage()

        binding.nextImage.setOnClickListener {
            if (imageIndex < uris.size - 1) {
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
                        "commonName" to binding.commonName.text.toString(),
                        "scientificName" to binding.scientificName.text.toString(),
                        "description" to binding.description.text.toString()
                    )

                    // Post the observation with these params and id
                    mainViewModel.postObservation(observationID, map) {
                        // Having succeeded in posting the observation, show the user them
                        (requireContext() as MainActivity).loadFragment(
                            ObservationFragment.newInstance(observationID)
                        )
                    }
                }
        }

        return binding.root
    }

    private fun loadCurrentImage() {
        // If there is at least one image to display in the observation
        if (uris.isNotEmpty()) {
            // Load current image
            binding.currentImage.setImageURI(uris[imageIndex])
        }
    }

    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    coordinate = GeoPoint(location.latitude, location.longitude)
                    // Update the coordinate label
                    binding.coordinateLabel.text = coordinate.toString()
                }
            }
    }

    companion object {
        const val TAG = "NewPostFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = listOf(Manifest.permission.ACCESS_COARSE_LOCATION)

        fun newInstance(paths: ArrayList<String>): NewPostFragment {
            val fragment = NewPostFragment()
            val args = Bundle()
            args.putStringArrayList("paths", paths)
            fragment.arguments = args
            return fragment
        }
    }
}