package com.fern.findaplant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fern.findaplant.databinding.FragmentFirestoreBinding
import com.fern.findaplant.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.ktx.storage
import java.net.URL

class FirestoreFragment : Fragment() {
    lateinit var binding: FragmentFirestoreBinding
    private lateinit var registration: ActivityResultLauncher<Intent>
    private lateinit var pfpURL: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate the layout.
        binding = FragmentFirestoreBinding.inflate(layoutInflater)

        // Bind various buttons to their functions
        binding.profilePicture.setOnClickListener { selectPhoto() }
        binding.logout.setOnClickListener { logout() }
        binding.saveData.setOnClickListener { saveData() }
        binding.signUp.setOnClickListener { signUp() }

        // Hide buttons based on Context
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
                Firebase.firestore
                    .collection("users")
                    .document(Firebase.auth.currentUser!!.uid)
                    .get()
                    // If the UserDoc already exists and we can grab it
                    .addOnSuccessListener { documentSnapshot ->
                        val user: User? = documentSnapshot.toObject<User>()
                        // If the cast succeeded
                        if (user != null) {
                            Log.i(TAG, "Found UserDoc in FirestoreFragment, proceeding to MainActivity")
                            // Simply navigate to the Main Activity
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        else {
                            Log.i(TAG, "DocumentSnapshot obtained but casting failed")
                        }
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Could not find UserDoc in FirestoreFragment, letting user create")
                    }
            }
            // If we're in the MainActivity
            else {
                Log.i(TAG, "Searching for user with auth uid ${Firebase.auth.currentUser!!.uid}")
                Firebase.firestore
                    .collection("users")
                    .document(Firebase.auth.currentUser!!.uid)
                    .addSnapshotListener{
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
            Log.e(TAG, "Auth user is NULL in FirestoreFragment")
            logout()
        }

        // Return the root view.
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Activity results need to be registered for on attachment
        registration = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            // If the Activity returned with success
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                //  you will get result here in result.data
                val selectedImage: Uri = requireNotNull(result.data!!.data)

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                (requireContext() as MainActivity).viewModel
                    .uploadPhoto("users/${Firebase.auth.currentUser!!.uid}", selectedImage) { url ->
                        (requireContext() as MainActivity).viewModel.updateProfilePicture(url)
                    }
            }
        }
    }

    // Allows the user to pick a photo from Camera Roll
    private fun selectPhoto() {
        // Create the associated intent for picking something
        registration.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )
    }

    private fun saveData() {
        Log.i(TAG, "Updating UserDoc in firestore")
        val userDoc: Map<String, Any> = hashMapOf(

        )
    }

    private fun signUp() {
        // Default User Document
        val userDoc: Map<String, Any> = hashMapOf(
            "name" to binding.name.text.toString(),
            "userName" to binding.userName.text.toString(),
            "bookmarks" to arrayListOf<DocumentReference>(),
            // TODO: Keep track of URL string for an uploaded picture
            "profilePicture" to ""
        )

        // Set the UserDoc in the Firestore database
        Firebase.firestore
            .collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .set(userDoc)
            // When the set operation completes successfully
            .addOnSuccessListener {
                // Simply navigate to the Main Activity
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
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
        const val TAG = "FirestoreFragment"
    }
}