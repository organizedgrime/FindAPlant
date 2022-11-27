package com.fern.findaplant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fern.findaplant.databinding.ActivityNoAuthBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NoAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Firebase connectivity
        FirebaseApp.initializeApp(this)

        binding = ActivityNoAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start in the No Auth Fragment- from which we can login or register
        loadFragment(NoAuthFragment())

        // If the current Auth user exists
       if (Firebase.auth.currentUser != null) {
            Log.i(TAG, "Loading FirestoreFragment from NoAuthActivity")
            // Install the Firestore Fragment if there is still Auth
            loadFragment(FirestoreFragment())
        }
    }

    fun loadFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }

    companion object {
        const val TAG = "NoAuthActivity"
    }
}