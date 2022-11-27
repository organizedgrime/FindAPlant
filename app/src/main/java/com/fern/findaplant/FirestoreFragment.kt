package com.fern.findaplant

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fern.findaplant.databinding.FragmentFirestoreBinding

class FirestoreFragment : Fragment() {
    private lateinit var binding: FragmentFirestoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout
        binding = FragmentFirestoreBinding.inflate(layoutInflater)

        // Simply navigate to Main Activity
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

        // Force unwrap UID, since we must already be authenticated to be here
        // If a user exists at this UID
//        if (plantFirestore.) {

//        }
        // Otherwise the User Document still needs to be made

        // Return root
        return binding.root
    }

    companion object {
        const val TAG = "FirestoreFragment"
    }
}