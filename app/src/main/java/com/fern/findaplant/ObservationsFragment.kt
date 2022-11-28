package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fern.findaplant.adaptors.ObservationAdapter
import com.fern.findaplant.databinding.FragmentBookmarkBinding
import com.fern.findaplant.databinding.FragmentObservationsBinding
import com.fern.findaplant.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ObservationsFragment : Fragment(),
    ObservationAdapter.OnObservationSelectedListener{

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query

    private lateinit var binding: FragmentObservationsBinding
    private lateinit var adapter: ObservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //
        binding = FragmentObservationsBinding.inflate(layoutInflater)

        // Firebase Init
        auth = Firebase.auth
        firestore = Firebase.firestore

        // Set the query to be a call to our observations collection
        query = firestore
            .collection("observations")
            .whereEqualTo("observer", firestore.document("users/${Firebase.auth.currentUser!!.uid}"))
        //TODO: Add filtering to ensure that they are in our list of bookmarks

        // Adapter for RecyclerView
        adapter = object : ObservationAdapter(User(), query, this@ObservationsFragment) {
            override fun onDataChanged() {
                // If there are no bookmarks
                if (itemCount == 0) {
                    binding.recyclerObservations.visibility = View.GONE
                } else {
                    binding.recyclerObservations.visibility = View.VISIBLE
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

        // Attach the Adapter and LayoutManager
        val manager = LinearLayoutManager(context)
        binding.recyclerObservations.layoutManager = manager
        binding.recyclerObservations.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerObservations.context,
            manager.orientation
        )
        // Add the item decorator
        binding.recyclerObservations.addItemDecoration(dividerItemDecoration)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Start listening for Firestore updates
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onObservationSelected(observation: DocumentSnapshot) {
        Log.i(BookmarkFragment.TAG, "Selection made")
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "ObservationsFragment"
    }
}