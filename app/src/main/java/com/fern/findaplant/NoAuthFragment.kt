package com.fern.findaplant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fern.findaplant.databinding.FragmentNoAuthBinding


class NoAuthFragment : Fragment() {
    private lateinit var binding: FragmentNoAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoAuthBinding.inflate(layoutInflater)

        binding.login.setOnClickListener {
            Log.i(TAG, "navigating to registration")
        }

        binding.register.setOnClickListener {
            Log.i(TAG, "navigating to registration")

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_auth, container, false)
    }

    companion object {
        const val TAG = "NoAuthFragment"
    }
}