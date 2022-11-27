package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fern.findaplant.adaptors.ObservationAdapter
import com.fern.findaplant.databinding.FragmentBookmarkBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookmarkFragment : Fragment(),
    ObservationAdapter.OnObservationSelectedListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var adapter: ObservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate binding
        binding = FragmentBookmarkBinding.inflate(layoutInflater)

        // Extract the Auth instance
        firebaseAuth = requireNotNull(FirebaseAuth.getInstance())

        // Firestore
        firestore = Firebase.firestore

        // Set the query to be a call to our observations collection
        query = firestore
            .collection("observations")
            //TODO: Add filtering to ensure that they are in our list of bookmarks

        // Adapter for RecyclerView
        adapter = object : ObservationAdapter(query, this@BookmarkFragment) {
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
        binding.recyclerObservations.layoutManager = LinearLayoutManager(context)
        binding.recyclerObservations.adapter = adapter

        // Return root
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
        Log.i(TAG, "Selection made")
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "BookmarkFragment"
    }
}