package com.ifishy.ui.fragment.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.profile.request.EditProfileRequest
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityEditProfileBinding
import com.ifishy.ui.activity.auth.LoginActivity
import com.ifishy.ui.activity.main.MainActivity
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.utils.Dialog
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private var email: String? = null

    private val lauchPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        binding.ppOverlay.isEnabled = true
        if (uri != null) {
            profileViewModel.imageProfile = uri
            binding.pp.setImageURI(uri)
        } else {
            Toast.makeText(this@EditProfileActivity, R.string.no_image_selected, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        profileViewModel.imageProfile?.let {
            binding.pp.setImageURI(it)
        }

        preferenceViewModel.token.observe(this@EditProfileActivity) { token ->
            preferenceViewModel.email.observe(this@EditProfileActivity) { email ->
                getProfile(token, email)
                this@EditProfileActivity.email = email
            }
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.ppOverlay.setOnClickListener {
            binding.ppOverlay.isEnabled = false
            lauchPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.update.setOnClickListener {
            preferenceViewModel.token.observe(this@EditProfileActivity) { token ->

                if (profileViewModel.imageProfile != null){
                    updatePhoto(token)
                }

                editProfile(
                    token, EditProfileRequest(
                        email = binding.email.text.toString(),
                        username = binding.username.text.toString(),
                        password = binding.oldPassword.text.toString(),
                        newPassword = binding.newPassword.text.toString()
                    )
                )

            }
        }

    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.loadingUpdate.visibility = View.VISIBLE
            binding.update.setImageDrawable(null)
            binding.update.isEnabled = false
        } else {
            binding.loadingUpdate.visibility = View.GONE
            binding.update.setImageResource(R.drawable.send)
            binding.update.isEnabled = true
        }
    }

    private fun updatePhoto(token: String) {
        val file = File(applicationContext.cacheDir, "profile_img_temp")
        val inputStream =
            applicationContext.contentResolver.openInputStream(profileViewModel.imageProfile!!)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val image = MultipartBody.Part.createFormData(
            "image",
            file.name,
            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )

        profileViewModel.updatePhoto(token, image).apply {
            profileViewModel.updatePhoto.observe(this@EditProfileActivity) { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is ResponseState.Loading -> {}
                        is ResponseState.Success -> {}
                        is ResponseState.Error -> {
                            Toast.makeText(this@EditProfileActivity,
                                getString(R.string.upload_photo_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun editProfile(token: String, userData: EditProfileRequest) {
        profileViewModel.editProfile(token, userData).apply {
            profileViewModel.editProfile.observe(this@EditProfileActivity) { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is ResponseState.Loading -> {
                            isLoading(true)
                        }

                        is ResponseState.Success -> {
                            isLoading(false)
                            Dialog.messageDialog(
                                supportFragmentManager,
                                getString(R.string.edit_profile_success),
                                getString(R.string.your_profile_updated)
                            ) {
                                if (userData.email != email) {
                                    preferenceViewModel.clearSession()
                                    startActivity(
                                        Intent(this@EditProfileActivity, LoginActivity::class.java)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(this@EditProfileActivity, MainActivity::class.java)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    )
                                    finish()
                                }
                            }
                        }

                        is ResponseState.Error -> {
                            isLoading(false)
                            Dialog.messageDialog(
                                supportFragmentManager,
                                getString(R.string.edit_profile_error), response.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getProfile(token: String, email: String) {
        profileViewModel.getProfile(token, email).apply {
            profileViewModel.profile.observe(this@EditProfileActivity) { response ->
                when (response) {
                    is ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        Glide.with(this@EditProfileActivity)
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(R.drawable.user_placeholder)
                            .error(R.drawable.user_placeholder)
                            .into(binding.pp)
                        binding.username.text = Editable.Factory.getInstance()
                            .newEditable(response.data.profile?.username)
                        binding.email.text =
                            Editable.Factory.getInstance().newEditable(response.data.profile?.email)
                    }

                    is ResponseState.Error -> {
                        Toast.makeText(
                            this@EditProfileActivity,
                            getString(R.string.load_profile_error), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

}