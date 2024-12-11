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
import android.view.Surface
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityScanBinding
import com.ifishy.ui.activity.result.ResultActivity
import com.ifishy.ui.dialog.ScanResultDialog
import com.ifishy.ui.viewmodel.history.HistoryViewModel
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.ui.viewmodel.scan.ScanViewModel
import com.ifishy.utils.Dialog
import com.ifishy.utils.ImageProcessing
import com.ifishy.utils.ResponseState
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private var imageCapture: ImageCapture?=null
    private val scanViewModel: ScanViewModel by viewModels()
    private var resultDialog: ScanResultDialog? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private var isProcessing = false

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
        binding.gallery.isEnabled = true
        if(uri != null){
            scanViewModel.imageScan = uri
            preferenceViewModel.token.observe(this@ScanActivity){token->
                preferenceViewModel.email.observe(this@ScanActivity){email->
                    getProfileInfo(token,email)
                }
            }
        }else{
            Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.back.setOnClickListener {
            finish()
        }

        binding.take.setOnClickListener {
            binding.take.isEnabled = false
            takePhoto()
        }

        binding.gallery.setOnClickListener {
            binding.gallery.isEnabled = false
            lauchPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }

    private fun takePhoto() {
        val photoFile = File(cacheDir, "classification.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.targetRotation = Surface.ROTATION_0

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    binding.take.isEnabled = true
                    scanViewModel.imageScan = Uri.fromFile(photoFile)
                    preferenceViewModel.token.observe(this@ScanActivity){token->
                        preferenceViewModel.email.observe(this@ScanActivity){email->
                            getProfileInfo(token,email)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    binding.take.isEnabled = true
                    Toast.makeText(this@ScanActivity,
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

    private fun getProfileInfo(token: String, email: String) {
        profileViewModel.getProfile(token, email).apply {
            profileViewModel.profile.observe(this@ScanActivity) { response ->
                when (response) {
                    is ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        predict(response.data.profile?.id!!)
                    }
                    is ResponseState.Error -> {}
                }
            }
        }
    }


    private fun confidenceProcess(percentage: String): Float {
        return percentage.replace("%", "").toFloat()
    }

    private fun save(
        userId: Int,
        disease: String,
        confidence: Float,
        description: String,
        treatment:String,
        validation:String,
        image: Uri,
        onSaveComplete: () -> Unit
    ) {
        val inputStream = applicationContext.contentResolver.openInputStream(
            Uri.fromFile(ImageProcessing.compressImageFromUri(this, image))
        )
        val file = File(applicationContext.cacheDir, "scan_img_temp")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val fish = MultipartBody.Part.createFormData(
            "fishImage", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val diseaseRequest = disease.toRequestBody("text/plain".toMediaTypeOrNull())
        val confidenceRequest = confidence.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdRequest = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionRequest = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val treatmentRequest = treatment.toRequestBody("text/plain".toMediaTypeOrNull())
        val validationRequest = validation.toRequestBody("text/plain".toMediaTypeOrNull())

        historyViewModel.saveScanHistory(fish, userIdRequest, diseaseRequest, confidenceRequest,descriptionRequest,treatmentRequest,validationRequest).apply {
            historyViewModel.saveScanHistory.observe(this@ScanActivity) { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is ResponseState.Loading -> {}
                        is ResponseState.Success -> {
                            Toast.makeText(
                                this@ScanActivity,
                                getString(R.string.disimpan_di_riwayat),
                                Toast.LENGTH_SHORT
                            ).show()
                            onSaveComplete()
                        }
                        is ResponseState.Error -> {
                            resultDialog?.dismiss()
                            Dialog.messageDialog(
                                supportFragmentManager,
                                getString(R.string.gagal_simpan),
                                response.message
                            )

                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resultDialog?.dismiss()
        isProcessing = false
    }


    private fun predict(userId: Int) {
        if (isProcessing) return

        isProcessing = true

        val inputStream = applicationContext.contentResolver.openInputStream(Uri.fromFile(ImageProcessing.resizeAndCompressImageFromUri(this, scanViewModel.imageScan!!)))
        val file = File(applicationContext.cacheDir, "scan_img_temp")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val image = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))

        scanViewModel.predict(image).apply {
            scanViewModel.predictResult.observe(this@ScanActivity) { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is ResponseState.Loading -> {
                            resultDialog = Dialog.resultDialog(supportFragmentManager, scanViewModel.imageScan!!)
                        }
                        is ResponseState.Success -> {
                            if (confidenceProcess(response.data.confidence!!) < 40) {
                                resultDialog?.dismiss()
                                Dialog.messageDialog(supportFragmentManager,
                                    getString(R.string.scanning_error),
                                    getString(R.string.ikan_tidak_terdeteksi),
                                    getString(R.string.ulangi)
                                )
                            } else {
                                response.data.details.penyebab.let {
                                    save(
                                        userId,
                                        response.data.disease,
                                        confidenceProcess(response.data.confidence),
                                        it,
                                        response.data.details.rekomendasiPengobatan,
                                        response.data.details.validasi
                                        ,scanViewModel.imageScan!!
                                    ) {
                                        resultDialog?.dismiss()
                                        val intent = Intent(this@ScanActivity, ResultActivity::class.java).apply {
                                            putExtra(ResultActivity.ID, userId)
                                            putExtra(ResultActivity.DISEASE_IMAGE, scanViewModel.imageScan!!.toString())
                                            putExtra(ResultActivity.DISEASE_NAME, response.data.disease)
                                            putExtra(ResultActivity.DISEASE_CAUSE, response.data.details.penyebab)
                                            putExtra(ResultActivity.DISEASE_TREATMENT, response.data.details.rekomendasiPengobatan)
                                            putExtra(ResultActivity.VALIDATION, response.data.details.validasi)
                                            putExtra(ResultActivity.PERCENTAGE, response.data.confidence)
                                        }
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                        is ResponseState.Error -> {
                            resultDialog?.dismiss()
                            Dialog.messageDialog(supportFragmentManager,
                                getString(R.string.scanning_error), response.message){}
                        }
                    }
                }
            }
        }
    }
}