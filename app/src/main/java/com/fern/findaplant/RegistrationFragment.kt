package com.fern.findaplant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fern.findaplant.databinding.FragmentRegistrationBinding
import com.fern.findaplant.models.Validators
import com.google.firebase.auth.FirebaseAuth

class RegistrationFragment : Fragment() {
    private var validator = Validators()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Extract the Auth instance
        auth = requireNotNull(FirebaseAuth.getInstance())
        // Inflate the layout
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        // Assign the Registration function to the button
        binding.register.setOnClickListener { registerNewUser() }
        // Return the root view
        return binding.root
    }

    private fun registerNewUser() {
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        // If the email is invalid
        if (!validator.validEmail(email)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_email),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        // If the password is invalid
        if (!validator.validPassword(password)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_password),
                Toast.LENGTH_LONG
            ).show()

            return
        }

        //
        binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_success_string),
                        Toast.LENGTH_LONG
                    ).show()

                    // Navigate to the MainActivity
                    startActivity(Intent(context, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_failed_string),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}