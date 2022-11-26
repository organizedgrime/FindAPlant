package com.fern.findaplant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fern.findaplant.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

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

        // By default, start the user off in the Camera Fragment
        loadFragment(CameraFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.bookmarks -> {
                    loadFragment(BookmarkFragment())
                    return@setOnItemReselectedListener
                }
                R.id.search -> {
                    loadFragment(SearchFragment())
                    return@setOnItemReselectedListener
                }
                R.id.camera -> {
                    loadFragment(CameraFragment())
                    return@setOnItemReselectedListener
                }
                R.id.explore -> {
                    loadFragment(ExploreFragment())
                    return@setOnItemReselectedListener
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    return@setOnItemReselectedListener
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}