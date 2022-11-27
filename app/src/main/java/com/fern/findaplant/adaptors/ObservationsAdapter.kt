package com.fern.findaplant.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fern.findaplant.databinding.ItemObservationBinding
import com.fern.findaplant.models.Observation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

open class ObservationsAdapter(query: Query, private val listener: OnObservationSelectedListener) :
    FirestoreAdapter<ObservationsAdapter.ViewHolder>(query) {

    interface OnObservationSelectedListener {
        fun onObservationSelected(observation: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemObservationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(private val binding: ItemObservationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnObservationSelectedListener?
        ) {
            // Cast the snapshot to an Observation object, return if fail
            val observation = snapshot.toObject<Observation>() ?: return

            //
            val resources = binding.root.resources

            // Load image from object
            Glide.with(binding.observationItemImage.context)
                .load(observation.photo)
                .into(binding.observationItemImage)

            // Common Name String
            binding.observationItemCommonName.text = observation.commonName
            // Scientific Name String
            binding.observationItemScientificName.text = observation.scientificName
            // Date String
            binding.observationItemDate.text = observation.timestamp.toString()

            // Click listener
            binding.root.setOnClickListener {
                listener?.onObservationSelected(snapshot)
            }
        }
    }
}
