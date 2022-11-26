package com.fern.findaplant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fern.findaplant.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate the layout.
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // When the logout button is clicked
        binding.logout.setOnClickListener {
            // Get the instance of Firestore Auth, sign out of it
            FirebaseAuth.getInstance().signOut()

            // Provide a Toast message indicating logout was successful
            Toast.makeText(
                requireContext(),
                "You are now logged out!",
                Toast.LENGTH_SHORT
            ).show()

            //TODO: Return to the no auth fragment
//            findNavController().popBackStack(R.id.noAuthFragment, false)
        }

        // Return the root view.
        return binding.root
    }
}