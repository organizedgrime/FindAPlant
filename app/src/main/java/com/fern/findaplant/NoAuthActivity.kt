package com.fern.findaplant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fern.findaplant.databinding.ActivityNoAuthBinding
import com.google.firebase.FirebaseApp

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
    }

    fun loadFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }
}