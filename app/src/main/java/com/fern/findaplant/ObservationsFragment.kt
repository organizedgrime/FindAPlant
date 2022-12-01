package com.fern.findaplant

import android.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fern.findaplant.adaptors.ObservationAdapter
import com.fern.findaplant.databinding.DetailObservationBinding
import com.fern.findaplant.databinding.FragmentBookmarkBinding
import com.fern.findaplant.databinding.FragmentObservationsBinding
import com.fern.findaplant.databinding.ItemObservationBinding
import com.fern.findaplant.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ObservationsFragment : Fragment(),
    ObservationAdapter.OnObservationSelectedListener{

    private lateinit var query: Query
    private lateinit var theDetailedView: DetailObservationBinding
    private lateinit var binding: FragmentObservationsBinding
    private lateinit var adapter: ObservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //
        binding = FragmentObservationsBinding.inflate(layoutInflater)
        //binding.recyclerObservations.setOnClickListener(onObservationSelected(binding.recyclerObservations.))

        //theDetailedView  = DetailObservationBinding.inflate(layoutInflater)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beginObservingUser()
    }

    private fun beginObservingUser() {
        // Now that the user model has been loaded
        (context as MainActivity).viewModel.user.observe(viewLifecycleOwner) { user ->
            Log.i(BookmarkFragment.TAG, "ObservationsFragment received User observation")
            // Set the query to be a call to our observations collection
            query = Firebase.firestore
                .collection("observations")
                .whereEqualTo(
                    "observer",
                    Firebase.firestore.document(
                        "users/${Firebase.auth.currentUser!!.uid}"
                    )
                )
            // Load the adapter using this query
            loadAdapter(user, query)
            // Start listening for Firestore updates
            adapter.startListening()
        }
    }

    private fun loadAdapter(user: User, query: Query) {
        // Create Adapter for RecyclerView
        adapter = object : ObservationAdapter(user, query, this@ObservationsFragment) {
            override fun onDataChanged() {
                // If there are no Observations
                if (itemCount == 0) {
                    binding.recyclerObservations.visibility = View.GONE
                    binding.noneLabel.visibility = View.VISIBLE
                } else {
                    binding.recyclerObservations.visibility = View.VISIBLE
                    binding.noneLabel.visibility = View.INVISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Toast.makeText(
                    requireContext(),
                    "Error: check logs for info.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Attach the Adapter
        binding.recyclerObservations.adapter = adapter
    }

    override fun onObservationSelected(observation: DocumentSnapshot) {
        Log.i(BookmarkFragment.TAG, "Selection made")
        TODO("Not yet implemented")
        //binding.root.addView(theDetailedView.root)
        //Inflate once selected
        val attempt = observation.toObject<Observation>() ?: return
        detailedview = DetailObservationBinding.inflate(layoutInflater)
        detailedview.observationItemCommonName.text = attempt.commonName
        detailedview.observationItemDescription.text = attempt.description
        detailedview.observationItemTimestamp.text = attempt.timestamp.toString()
        detailedview.observationItemScientificName.text = attempt.scientificName
        detailedview.observationItemCoordinate.text = attempt.coordinate.toString()
        //ToDO figure out photo conversion
        binding.root.addView(detailedview.root)

    }

    companion object {
        const val TAG = "ObservationsFragment"
    }
}