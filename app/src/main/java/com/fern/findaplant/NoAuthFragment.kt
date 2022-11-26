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
            Log.i(TAG, "navigating to login")
            (activity as NoAuthActivity).loadFragment(LoginFragment())
        }

        binding.register.setOnClickListener {
            Log.i(TAG, "navigating to registration")
            (activity as NoAuthActivity).loadFragment(RegistrationFragment())
        }

        // Return root
        return binding.root
    }

    companion object {
        const val TAG = "NoAuthFragment"
    }
}