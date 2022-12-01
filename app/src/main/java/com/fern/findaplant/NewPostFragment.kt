package com.fern.findaplant

import android.annotation.SuppressLint
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fern.findaplant.databinding.FragmentNewPostBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class NewPostFragment : Fragment() {

    private lateinit var binding: FragmentNewPostBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

        // Extract file paths
        val path = requireArguments().getStringArrayList("paths")?.get(0)!!
        // Represent as URIs
        val uri = Uri.fromFile(File(path))

        // Update the image
        binding.observationImage.setImageURI(uri)
        // Update the time label
        binding.timeLabel.text = Date().toString()

        binding.sendButton.setOnClickListener {
            (requireContext() as MainActivity).viewModel.postObservation(
                arrayListOf(),
                coordinate,
                Date(),
                binding.description.toString()
            )
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