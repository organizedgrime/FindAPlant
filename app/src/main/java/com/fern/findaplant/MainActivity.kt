package com.fern.findaplant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fern.findaplant.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()

        // Super on create
        super.onCreate(savedInstanceState)
        // Initialize the Firebase connectivity
        FirebaseApp.initializeApp(this)

        // Keep this code here for debugging the splash screen when need be
//        splashScreen.setKeepOnScreenCondition() {
//            true
//        }

        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}