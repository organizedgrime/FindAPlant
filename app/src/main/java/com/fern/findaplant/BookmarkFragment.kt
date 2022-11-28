package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fern.findaplant.adaptors.ObservationAdapter
import com.fern.findaplant.databinding.FragmentBookmarkBinding
import com.fern.findaplant.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class BookmarkFragment : Fragment(),
    ObservationAdapter.OnObservationSelectedListener {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var adapter: ObservationAdapter

    private lateinit var viewModel: BookmarkFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate binding
        binding = FragmentBookmarkBinding.inflate(layoutInflater)

        // Initialize the CounterViewModel variable defined above
        viewModel = ViewModelProvider(this)[BookmarkFragmentViewModel::class.java]
        // Bind CounterViewModel variable to the activity lifecycle
        viewModel.bindToActivityLifecycle(requireActivity())

        // Return root
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beginObservingUser()
    }

    private fun beginObservingUser() {
        // Now that the user model has been loaded
        viewModel.user.observe(viewLifecycleOwner) { user ->
            Log.i(TAG, "BookmarkFragment received User observation")
            if (user.bookmarks.isNotEmpty()) {
                // Represent the bookmarks as DocumentIDs
                val bookmarkIDs = user.bookmarks.map { it.id }
                val query = Firebase.firestore.collection("observations")
                    .whereIn(FieldPath.documentId(), bookmarkIDs)
                // Load the adapter using this query
                loadAdapter(user, query)
                // Start listening for Firestore updates
                adapter.startListening()
            }
        }
    }

    private fun loadAdapter(user: User, query: Query) {
        // Adapter for RecyclerView
        adapter = object : ObservationAdapter(user, query, this@BookmarkFragment) {
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
    }

    override fun onObservationSelected(observation: DocumentSnapshot) {
        Log.i(TAG, "Selection made")
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "BookmarkFragment"
    }
}