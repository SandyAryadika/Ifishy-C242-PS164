package com.ifishy.ui.activity.scan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ifishy.R
import com.ifishy.databinding.ActivityScanBinding
import com.ifishy.databinding.ActivityScanGuestBinding
import com.ifishy.ui.activity.result.ResultActivity
import com.ifishy.ui.dialog.ScanResultDialog
import com.ifishy.ui.viewmodel.scan.ScanViewModel
import com.ifishy.utils.Dialog
import com.ifishy.utils.ResponseState
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ScanGuestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanGuestBinding
    private var imageCapture: ImageCapture?=null
    private val scanViewModel: ScanViewModel by viewModels()
    private var resultDialog: ScanResultDialog? = null

    private val uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                scanViewModel.imageScan = it
                predict()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isAllowed->
        if (isAllowed) {
            startCamera()
        }else{
            Toast.makeText(this, getString(R.string.camera_must_allowed), Toast.LENGTH_SHORT).show()
        }

    }

    private val lauchPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri->
        if(uri != null){
            cropImage(uri)
        }else{
            Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityScanGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.back.setOnClickListener {
            finish()
        }

        binding.take.setOnClickListener {
            takePhoto()
        }

        binding.gallery.setOnClickListener {
            lauchPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun cropImage(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "classification_crop.jpg"))
        val options = UCrop.Options()
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this,R.color.primary_light))
        val intent = UCrop.of(uri, destinationUri)
            .getIntent(this)

        uCropLauncher.launch(intent)
    }

    private fun takePhoto() {
        val photoFile = File(cacheDir, "classification.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    cropImage(Uri.fromFile(photoFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ScanGuestActivity,
                        getString(R.string.error_while_take_photo), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.camera.surfaceProvider
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraFace = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraFace,
                    preview,
                    imageCapture
                )
            }catch (e: Exception){
                Toast.makeText(this, getString(R.string.failed_to_start_camera), Toast.LENGTH_SHORT).show()
            }

        },ContextCompat.getMainExecutor(this))
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            getCameraPermission()
        } else {
            startCamera()
        }
    }

    private fun getCameraPermission(){
        cameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun predict(){
        val inputStream = applicationContext.contentResolver.openInputStream(scanViewModel.imageScan!!)
        val file = File(applicationContext.cacheDir, "scan_img_temp")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val image = MultipartBody.Part.createFormData("file",file.name,file.asRequestBody("image/jpeg".toMediaTypeOrNull()))

        scanViewModel.predict(image).apply {
            scanViewModel.predictResult.observe(this@ScanGuestActivity){ event->
                event.getContentIfNotHandled()?.let { response->
                    when(response){
                        is ResponseState.Loading -> {
                            resultDialog = Dialog.resultDialog(supportFragmentManager,scanViewModel.imageScan!!)
                            resultDialog
                        }
                        is ResponseState.Success -> {
                            lifecycleScope.launch {
                                val intent =  Intent(this@ScanGuestActivity,ResultActivity::class.java)
                                    .putExtra(ResultActivity.DISEASE_IMAGE,file.path)
                                    .putExtra(ResultActivity.DISEASE_NAME,"${response.data.disease}")
                                    .putExtra(ResultActivity.DISEASE_CAUSE,"${response.data.details?.penyebab}")
                                    .putExtra(ResultActivity.DISEASE_TREATMENT, "${response.data.details?.rekomendasiPengobatan}")
                                    .putExtra(ResultActivity.VALIDATION,"${response.data.details?.validasi}")
                                    .putExtra(ResultActivity.PERCENTAGE,"${response.data.confidence}")
                                startActivity(intent)
                                resultDialog?.dismiss()
                            }
                        }
                        is ResponseState.Error -> {
                            resultDialog?.dismiss()
                            Dialog.messageDialog(supportFragmentManager,"Scanning Error",response.message)
                        }
                    }
                }
            }
        }

    }
}