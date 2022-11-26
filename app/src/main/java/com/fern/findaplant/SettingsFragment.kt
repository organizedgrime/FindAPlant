package com.fern.findaplant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.fern.findaplant.databinding.FragmentSettingsBinding

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate the layout.
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.login.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_loginFragment
            )
        }

        binding.register.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_registrationFragment
            )
        }

        // Return the root view.
        return binding.root
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}