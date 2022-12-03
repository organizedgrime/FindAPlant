package com.fern.findaplant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fern.findaplant.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var paths: ArrayList<String> = arrayListOf()
    private lateinit var cameraSelector: CameraSelector
    private lateinit var safeContext: Context
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FragmentCameraBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater)
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        binding.cameraFlipButton.setOnClickListener { flipCamera() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If permissions are already granted
        if (allPermissionsGranted()) {
            // Set default Camera state
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // Start the Camera
            startCamera()
        }
        // Otherwise
        else {
            // Request required permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Initialize the output directory and the Camera Executor
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // Determine an output directory for a new Photo
    private fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
    }

    // Capture photo from Camera
    private fun takePhoto() {
        Log.i(TAG, "takePhoto called")
        // Return if Image Capture is uninitialized
        val imageCapture = imageCapture ?: return

        // Create File to hold picture
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        Log.i(TAG, "Actually taking a photo!")

        // Take the picture
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(safeContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    // Add path to paths array
                    paths.add(photoFile.path)
                    // Update the next directory
                    outputDirectory = getOutputDirectory()
                    // Add the thumbnail
                    addThumbnail(savedUri)
                    // Update what the post button does
                    updatePostButton()
                }
            }
        )
    }

    private fun addThumbnail(uri: Uri) {
        // Make the post button visible
        binding.postButton.visibility = View.VISIBLE
        // Determine bitmap width and height
        val density = requireContext().resources.displayMetrics.density
        val thumbWidth = (100 * density).toInt()
        val buttonWidth = (30 * density).toInt()
        // Create a new frame layout
        val frameView = FrameLayout(requireContext())
        // Set the dimensions
        frameView.layoutParams = FrameLayout.LayoutParams(thumbWidth, thumbWidth)

        // Create the image view
        val thumbnailView = ImageView(context)
        thumbnailView.setImageURI(uri)
        thumbnailView.scaleType = ImageView.ScaleType.CENTER_CROP

        val deleteButton = ImageView(context)
        deleteButton.setImageResource(R.drawable.ic_outline_delete_24)
        val params = FrameLayout.LayoutParams(buttonWidth, buttonWidth)
        params.gravity = Gravity.RIGHT
        deleteButton.layoutParams = params
        deleteButton.setOnClickListener {
            Log.i(TAG, "Deleting this image")
            // Remove the last uri we added
            paths.removeLast()
            binding.thumbnailContainer.removeView(deleteButton.parent as View)
            if (paths.isEmpty()) {
                // Make the post button invisible again
                binding.postButton.visibility = View.INVISIBLE
            }
        }
        deleteButton.setBackgroundColor(resources.getColor(R.color.red))

        // Add them both to the frame layout
        frameView.addView(thumbnailView)
        frameView.addView(deleteButton)

        // Add the frame layout
        binding.thumbnailContainer.addView(frameView)
    }

    private fun updatePostButton() {
        binding.postButton.setOnClickListener {
            // Open the New Post Fragment with the associated uri
            (context as MainActivity).loadFragment(NewPostFragment.newInstance(paths))
        }
    }

    // Change between front and back Camera
    private fun flipCamera() {
        Log.i(TAG, "Flipping camera!")
        // Update CameraSelector
        cameraSelector =
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        // Restart the Camera
        startCamera()
    }

    // Initialize Camera hardware
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Build the Preview
            preview = Preview.Builder().build()
            // Build the Image Capture
            imageCapture = ImageCapture.Builder().build()
            // Attempt to set up camera
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            // If something went wrong
            catch (exc: Exception) {
                // Log the error
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(safeContext))
    }

    // Check if all the necessary permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // When the Fragment is destroyed
    override fun onDestroy() {
        super.onDestroy()
        // Also shutdown the Executor
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = listOf(Manifest.permission.CAMERA)
    }
}