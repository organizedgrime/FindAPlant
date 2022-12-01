package com.fern.findaplant

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fern.findaplant.databinding.FragmentNewPostBinding
import java.io.File
import java.net.URI

class NewPostFragment : Fragment() {

    private lateinit var binding: FragmentNewPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(layoutInflater)

        val path = requireArguments().getStringArrayList("paths")?.get(0)!!
        val uri = Uri.fromFile(File(path))

        binding.observationImage.setImageURI(uri)

        return binding.root
    }

    companion object {
        const val TAG = "NewPostFragment"

        fun newInstance(paths: ArrayList<String>): NewPostFragment {
            val fragment = NewPostFragment()
            val args = Bundle()
            args.putStringArrayList("paths", paths)
            fragment.arguments = args
            return fragment
        }
    }
}