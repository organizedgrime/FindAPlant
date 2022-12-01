package com.fern.findaplant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fern.findaplant.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()

        // Super on create
        super.onCreate(savedInstanceState)

        // Keep this code here for debugging the splash screen when need be
//        splashScreen.setKeepOnScreenCondition() {
//            true
//        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        // Initialize the CounterViewModel variable defined above
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        // Bind CounterViewModel variable to the activity lifecycle
        viewModel.bindToActivityLifecycle(this)

        setContentView(binding.root)

        // By default, start the user off in the Bookmarks fragment
        loadFragment(BookmarkFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bookmarks -> {
                    loadFragment(BookmarkFragment())
                }
                R.id.observations -> {
                    loadFragment(ObservationsFragment())
                }
                R.id.camera -> {
                    loadFragment(CameraFragment())
                }
                R.id.search -> {
                    loadFragment(SearchFragment())
                }
                R.id.settings -> {
                    loadFragment(FirestoreFragment())
                }
                else -> {
                    false
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }
}