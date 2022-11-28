package com.fern.findaplant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fern.findaplant.databinding.FragmentFirestoreBinding
import com.fern.findaplant.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class FirestoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate the layout.
        val binding = FragmentFirestoreBinding.inflate(inflater, container, false)

        // When the logout button is clicked
        binding.logout.setOnClickListener { logout() }

        binding.saveData.setOnClickListener { saveData() }

        if (context is NoAuthActivity) {
            // Make the Save Data button invisible
            binding.saveData.visibility = View.INVISIBLE
            // Make the Logout Button invisible
            binding.logout.visibility = View.INVISIBLE
        }
        else {
            // Make the SignUp button invisible
            binding.signUp.visibility = View.INVISIBLE
        }

        // If there is authentication loaded
        if (Firebase.auth.currentUser != null) {
            // If we're in the No Auth activity
            if (context is NoAuthActivity) {
                // Simply navigate to the Main Activity
                // Return to the No Auth Activity
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            // If we're in the MainActivity
            else {
                Log.i(TAG, "Searching for user with auth uid ${Firebase.auth.currentUser!!.uid}")
                val documentReference = Firebase.firestore
                    .collection("users")
                    .document(Firebase.auth.currentUser!!.uid)
                documentReference.addSnapshotListener{
                        documentSnapshot, _ ->
                    // If the document snapshot is valid
                    if (documentSnapshot != null) {
                        // Construct a User object from the Snapshot
                        val user: User = requireNotNull(documentSnapshot.toObject<User>())

                        // Update name and userName
                        binding.name.setText(user.name)
                        binding.userName.setText(user.userName)

                        // If the UserDoc pfp URL is non-empty
                        if (user.profilePicture!!.isNotEmpty()) {
                            // Reset color filter
                            binding.profilePicture.setColorFilter(
                                ContextCompat.getColor(requireContext(), R.color.white),
                                android.graphics.PorterDuff.Mode.MULTIPLY
                            )

                            // Load image from object into thumbnail
                            Glide
                                .with(binding.profilePicture.context)
                                .load(user.profilePicture)
                                .into(binding.profilePicture)
                        }
                        else {
                            // Set the image
                            binding.profilePicture.setImageResource(R.drawable.ic_baseline_account_circle_24)
                            // Set the color tint
                            binding.profilePicture.setColorFilter(
                                ContextCompat.getColor(requireContext(), R.color.green_900),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                }
            }
        }
        else {
            Log.e(TAG, "Auth user is NULL in MainActivity")
            logout()
        }

        // Return the root view.
        return binding.root
    }

    private fun saveData() {
        Log.i(TAG, "Updating UserDoc in firestore")
    }

    private fun logout() {
        Log.i(TAG, "Logging out user from Auth")

        // Get the instance of Firestore Auth, sign out of it
        Firebase.auth.signOut()

        // Provide a Toast message indicating logout was successful
        Toast.makeText(
            requireContext(),
            "You are now logged out!",
            Toast.LENGTH_SHORT
        ).show()

        // Return to the No Auth Activity
        val intent = Intent(context, NoAuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}