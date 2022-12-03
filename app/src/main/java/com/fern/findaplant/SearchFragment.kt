package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fern.findaplant.adaptors.ObservationAdapter
import com.fern.findaplant.databinding.FragmentSearchBinding
import com.fern.findaplant.models.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment(),
    ObservationAdapter.OnObservationSelectedListener{
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: ObservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beginObservingUser()
    }

    private fun beginObservingUser() {
        // Now that the user model has been loaded
        (context as MainActivity).viewModel.user.observe(viewLifecycleOwner) { user ->
            Log.i(TAG, "SearchFragment received User")
            // When the user clicks the search button
            binding.searchButton.setOnClickListener {
                val queryString = binding.queryText.text.toString()
                // If the query string actually has some contents
                if (queryString.isNotEmpty()) {
                    val query = Firebase.firestore
                        .collection("observations")
                        .whereGreaterThan("commonName", queryString)

                    // Load the adapter using this query
                    loadAdapter(user, query)
                    // Start listening for Firestore updates
                    adapter.startListening()
                }
            }
        }
    }

    private fun loadAdapter(user: User, query: Query) {
        // Create Adapter for RecyclerView
        adapter = object : ObservationAdapter(user, query, this@SearchFragment) {
            override fun onDataChanged() {
                // If there are no observations
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
                    context,
                    "Error: check logs for info.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        // Attach the Adapter
        binding.recyclerObservations.adapter = adapter
    }

    override fun onObservationSelected(observation: DocumentSnapshot) {
        Log.i(TAG, "Selection made")
        (requireContext() as MainActivity)
            .loadFragment(ObservationFragment.newInstance(observation.id))
    }

    companion object {
        const val TAG = "BookmarkFragment"
    }
}