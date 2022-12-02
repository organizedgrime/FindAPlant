package com.fern.findaplant

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fern.findaplant.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
                context,
                getString(R.string.login_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                context,
                getString(R.string.password_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Login successful!",
                        Toast.LENGTH_LONG
                    ).show()

                    // Navigate to the Firestore Fragment
                    Log.i(TAG, "Launching FirestoreFragment from LoginFragment")
                    (context as NoAuthActivity).loadFragment(FirestoreFragment())
                } else {
                    Toast.makeText(
                        context,
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}
