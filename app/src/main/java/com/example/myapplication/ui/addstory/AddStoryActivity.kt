package com.example.myapplication.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityAddStoryBinding
import com.example.myapplication.ui.dialog.CustomDialogFragment
import com.example.myapplication.ui.story.MainActivity
import com.example.myapplication.ui.viewmodel.UploadStoryViewModel
import com.example.myapplication.utils.TokenProvider.token
import com.example.myapplication.utils.reduceFileImage
import com.example.myapplication.utils.rotateFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityAddStoryBinding
    private var lat: String? = null
    private var lon: String? = null
    private var getFile: File? = null
    private val uploadStoryViewModel by viewModels<UploadStoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra(EXTRA_PICTURE, File::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getSerializableExtra(EXTRA_PICTURE)
        }as? File
        val isBackCamera = intent?.getBooleanExtra(EXTRA_IS_BACK_CAMERA, true) as Boolean
        val isGallery = intent?.getBooleanExtra(EXTRA_IS_GALLERY, false) as Boolean

        myFile?.let{
            if(!isGallery) rotateFile(it, isBackCamera)
            binding.ivStoryPreview.setImageBitmap(BitmapFactory.decodeFile(it.path))
        }
        getFile = myFile


        uploadStoryViewModel.uploadResponse.observe(this){
            if (!it.error) {
                Intent(this, MainActivity::class.java).also {intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
        uploadStoryViewModel.isError.observe(this){
            if(!it.isNullOrEmpty()){
                val dialogFragment = CustomDialogFragment.newInstance(it)
                dialogFragment.show(supportFragmentManager, CustomDialogFragment::class.java.simpleName)
            }
        }

        uploadStoryViewModel.isLoading.observe(this){
            showLoading(it)
        }
        
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                getMyLocation()
            }
        }

        binding.buttonAdd.setOnClickListener {
            val desc = binding.edStoryDesc.text.toString()
            val isValidDesc = validateDesc(desc)

            if (isValidDesc) uploadStory()
        }

        binding.ivStoryPreview.setOnClickListener {
            val intentCamera = Intent(this, CameraActivity::class.java)
            startActivity(intentCamera)
        }
    }

    private fun uploadStory() {
        if (getFile != null) {

            val file = reduceFileImage(getFile as File)

            val description = binding.edStoryDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val positionLat = lat?.toRequestBody("text/plain".toMediaType())
            val positionLon = lon?.toRequestBody("text/plain".toMediaType())

            uploadStoryViewModel.uploadStory("Bearer $token",imageMultipart,description,positionLat,positionLon)

        }
    }

    private fun validateDesc(desc: String): Boolean {
        if (desc.isEmpty()) {
            binding.edStoryDesc.setBackgroundResource(R.drawable.error_edit_text)
            binding.edStoryDesc.error = getString(R.string.desc_empty)
            return false
        }
        return true
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                binding.checkBox.isChecked = true
                if (location != null) {
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            binding.checkBox.isChecked = false

        }
    }

    companion object {
        const val EXTRA_PICTURE = "PICTURE"
        const val EXTRA_IS_BACK_CAMERA = "IS_BACK_CAMERA"
        const val EXTRA_IS_GALLERY = "IS_GALLERY"
    }
}