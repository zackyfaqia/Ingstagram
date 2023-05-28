package com.zackyfaqia.ingstagram.ui.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.zackyfaqia.ingstagram.R
import com.zackyfaqia.ingstagram.data.Constants.REQUEST_CODE_PERMISSIONS
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.databinding.ActivityAddStoryBinding
import com.zackyfaqia.ingstagram.util.ViewModelFactory
import com.zackyfaqia.ingstagram.util.createCustomTempFile
import com.zackyfaqia.ingstagram.util.reduceFileImage
import com.zackyfaqia.ingstagram.util.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var isError = false

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var TOKEN = ""
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_failed),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddStoryViewModel::class.java]

        addStoryViewModel.message.observe(this) {
            AlertDialog.Builder(this@AddStoryActivity).apply {
                setTitle("Status!")
                setMessage(
                    getString(
                        if (isError) {
                            R.string.upload_failed
                        } else {
                            R.string.upload_success
                        }
                    )
                )
                setPositiveButton("OK") { _, _ -> finish() }
                create()
                show()
            }
        }

        addStoryViewModel.isUpload.observe(this) {
            showUpload(it)
        }

        addStoryViewModel.isError.observe(this) {
            isError = it
        }

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.add_story)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showUpload(isUploading: Boolean) {
        if (isUploading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnUpload.isEnabled = true
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnUpload.isEnabled = true
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.zackyfaqia.ingstagram",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.picture_choose))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        val desc = binding.edtDescription.text.toString()

        when {
            desc.isEmpty() -> binding.edtDescription.error = getString(R.string.description_empty)
            getFile == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    getString(R.string.image_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val description =
                    binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
                addStoryViewModel.uploadToServer(
                    TOKEN,
                    imageMultipart,
                    description
                )
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.ivAddImage.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ivAddImage.setImageURI(selectedImg)
        }
    }
}