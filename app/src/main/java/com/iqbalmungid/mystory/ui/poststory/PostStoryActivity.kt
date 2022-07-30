package com.iqbalmungid.mystory.ui.poststory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iqbalmungid.mystory.R
import com.iqbalmungid.mystory.data.local.datastore.Account
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.databinding.ActivityPostStoryBinding
import com.iqbalmungid.mystory.helper.ApiCallbackString
import com.iqbalmungid.mystory.helper.reduceFileImage
import com.iqbalmungid.mystory.helper.rotateBitmap
import com.iqbalmungid.mystory.helper.uriToFile
import com.iqbalmungid.mystory.ui.camera.CameraActivity
import com.iqbalmungid.mystory.ui.main.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class PostStoryActivity : AppCompatActivity() {
    private lateinit var viewModel: PostViewModel
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var account: Account
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile: File? = null
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AccountPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[PostViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.apply {
            btnCamera.setOnClickListener {
                launcherIntentCameraX.launch(
                    Intent(
                        this@PostStoryActivity,
                        CameraActivity::class.java
                    )
                )
            }

            btnGallery.setOnClickListener {
                startGallery()
            }

            btnUpload.setOnClickListener {
                uploadImage()
            }

            imgClose.setOnClickListener {
                cameraResult.setImageResource(R.drawable.ic_photo)
                close(false)
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }

            viewModel.getLocationSettings()
                .observe(this@PostStoryActivity) { isLocationActive: Boolean ->
                    if (isLocationActive) {
                        getMyLocation()
                        switchLocation.isChecked = true
                        switchLocation.text = "Location ON "
                    } else {
                        location = null
                        switchLocation.isChecked = false
                        txtLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_off, 0, 0, 0)
                        txtLoc.text = getString(R.string.error_location)
                        switchLocation.text = "Location OFF "
                    }
                }

            switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                viewModel.saveLocationSetting(isChecked)
            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            binding.cameraResult.setImageBitmap(result)
            close(true)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_img))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.cameraResult.setImageURI(selectedImg)
            close(true)
        }
    }

    private fun uploadImage() {
        val getDesc = binding.txtDesc.text

        if (getFile != null) {
            if (getDesc != null && getDesc.isNotEmpty()) {
                val file = reduceFileImage(getFile as File)
                val desc = getDesc.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                var lat: RequestBody? = null
                var lon: RequestBody? = null
                if (location != null) {
                    lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                    lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                }

                viewModel.getUser().observe(this) {
                    account = Account(
                        it.name,
                        it.email,
                        it.password,
                        it.userId,
                        it.token,
                        it.isLogin
                    )
                    viewModel.postStories(
                        account,
                        desc,
                        imageMultipart,
                        lat,
                        lon,
                        object : ApiCallbackString {
                            override fun onResponse(state: Boolean, message: String) {
                                process(state, message)
                            }
                        })
                    load(true)
                }
            } else {
                Toast.makeText(this, getString(R.string.desc_empty), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.img_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun process(state: Boolean, message: String) {
        if (state) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.info))
                setMessage(getString(R.string.post_success))
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    finish()
                }
                setCancelable(false)
                load(false)
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.info))
                setMessage("${getString(R.string.post_failed)}, $message")
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    binding.progress.visibility = View.GONE
                }
                setCancelable(false)
                load(false)
                create()
                show()
            }
        }
    }

    private fun load(state: Boolean) {
        if (state) {
            binding.progress.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            binding.progress.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun close(state: Boolean) {
        if (state) {
            binding.imgClose.visibility = View.VISIBLE
        } else {
            binding.imgClose.visibility = View.GONE
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    location = it
                    val lat = it.latitude
                    val lon = it.longitude
                    val loc = binding.txtLoc

                    fun getAddress(lat: Double, lon: Double): String {
                        val geocoder = Geocoder(this)
                        val list = geocoder.getFromLocation(lat, lon, 1)
                        return list[0].getAddressLine(0)
                    }
                    loc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0)
                    binding.txtLoc.text = getAddress(lat, lon)
                } else {
                    Toast.makeText(this, "allow first", Toast.LENGTH_SHORT).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getMyLocation()
        } else binding.switchLocation.isChecked = false
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}