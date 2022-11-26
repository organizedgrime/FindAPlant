package com.fern.findaplant

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fern.findaplant.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Extract the Auth instance
        firebaseAuth = requireNotNull(FirebaseAuth.getInstance())
        // Inflate the layout
        binding = FragmentLoginBinding.inflate(layoutInflater)
        // Assign Login function to the button
        binding.login.setOnClickListener { loginUserAccount() }
        // Return the root view
        return binding.root
    }

    // Function for logging into existing Auth account
    private fun loginUserAccount() {
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.login_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.password_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Login successful!",
                        Toast.LENGTH_LONG
                    ).show()

                    //TODO: Navigate to the MainActivity
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
