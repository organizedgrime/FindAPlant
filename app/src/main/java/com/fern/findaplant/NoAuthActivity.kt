package com.fern.findaplant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fern.findaplant.databinding.ActivityNoAuthBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class NoAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Firebase connectivity
        FirebaseApp.initializeApp(this)

        // If the current Auth user exists
        FirebaseAuth.getInstance().currentUser.let {
            // Start the MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding = ActivityNoAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start in the No Auth Fragment- from which we can login or register
        loadFragment(NoAuthFragment())
    }

    fun loadFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }
}