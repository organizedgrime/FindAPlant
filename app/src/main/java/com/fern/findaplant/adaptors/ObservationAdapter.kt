package com.fern.findaplant.adaptors

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fern.findaplant.databinding.ItemObservationBinding
import com.fern.findaplant.models.Observation
import com.fern.findaplant.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

open class ObservationAdapter(_user: User, query: Query, private val listener: OnObservationSelectedListener) :
    FirestoreAdapter<ObservationAdapter.ViewHolder>(query) {
    private var user: User

    init {
        user = _user
    }

    interface OnObservationSelectedListener {
        fun onObservationSelected(observation: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create an Observation binding from the ViewGroup
        val binding = ItemObservationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, user)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(private val binding: ItemObservationBinding, private val user: User):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnObservationSelectedListener?
        ) {
            // Cast the snapshot to an Observation object, return if fail
            val observation = snapshot.toObject<Observation>() ?: return

            // If there is at least one image to display in the observation
            if (observation.photos.isNotEmpty()) {
                // Load image from object into thumbnail
                Glide
                    .with(binding.observationItemImage.context)
                    .load(observation.photos[0])
                    .into(binding.observationItemImage)
            }

            // Common Name String
            binding.observationItemCommonName.text = observation.commonName
            // Scientific Name String
            binding.observationItemScientificName.text = observation.scientificName
            // Date String
            binding.observationItemDate.text = observation.timestamp.toDate().toString()

            // Assign visibility based on the membership to the bookmarks array
            binding.bookmarked.visibility =
                if (user.bookmarks.map { it.id }.contains(observation.id)) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

            // When the bookmark button is clicked
            binding.bookmarkButton.setOnClickListener {
                // DocumentReference of the Observation clicked
                val reference = Firebase.firestore
                    .collection("observations")
                    .document(observation.id!!)

                val newDoc: Map<String, Any> = if (user.bookmarks.contains(reference)) {
                    hashMapOf("bookmarks" to FieldValue.arrayRemove(reference))
                } else {
                    hashMapOf("bookmarks" to FieldValue.arrayUnion(reference))
                }

                // Update the user document to reflect this
                Firebase.firestore
                    .collection("users")
                    .document(Firebase.auth.currentUser!!.uid)
                    .update(newDoc)
            }

            // Click listener
            binding.root.setOnClickListener {
                listener?.onObservationSelected(snapshot)
            }
        }
    }
}
