package com.fern.findaplant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fern.findaplant.databinding.FragmentObservationBinding


class ObservationFragment : Fragment() {

    private lateinit var binding: FragmentObservationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObservationBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        const val TAG = "ObservationFragment"
    }
}