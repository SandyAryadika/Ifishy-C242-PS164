package com.ifishy.ui.activity.add_post

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityAddPostBinding
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.utils.Dialog
import com.ifishy.utils.ResponseState
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class AddPostActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddPostBinding
    private val communityViewModel: CommunityViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()


    private val launchPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri->
        binding.cover.isEnabled = true
        if(uri !=null){
            communityViewModel.imagePost = uri
            binding.cover.setImageURI(uri)
        }else{
            binding.send.isEnabled = true
            Toast.makeText(applicationContext,
                getString(R.string.no_image_selected),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cover.apply {
            this.setImageURI(communityViewModel.imagePost)
            this.setOnClickListener(this@AddPostActivity)
        }
        binding.send.setOnClickListener(this)
        binding.back.setOnClickListener(this)

    }

    private fun isLoading(loading: Boolean){
        if (loading){
            binding.loading.visibility = View.VISIBLE
            binding.send.setImageDrawable(null)
        }else{
            binding.loading.visibility = View.GONE
            binding.send.setImageResource(R.drawable.send)
        }
    }

    private fun upload(){
        val title = binding.title.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val content = binding.content.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val inputStream = applicationContext.contentResolver.openInputStream(communityViewModel.imagePost!!)
        val file = File(applicationContext.cacheDir, "post_img_temp")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val image = MultipartBody.Part.createFormData("image",file.name,file.asRequestBody("image/jpeg".toMediaTypeOrNull()))

        preferenceViewModel.token.observe(this@AddPostActivity){token->
            communityViewModel.uploadPost(token,title,content,image).apply {
                communityViewModel.uploadPost.observe(this@AddPostActivity){event->
                    event.getContentIfNotHandled()?.let { response->
                        when(response){
                            is ResponseState.Loading -> {
                                isLoading(true)
                            }
                            is ResponseState.Success -> {
                                isLoading(false)
                                Dialog.messageDialog(supportFragmentManager,"Success Upload",
                                    response.data.message.toString()){
                                    finish()
                                }
                            }
                            is ResponseState.Error -> {
                                isLoading(false)
                                binding.send.isEnabled = true
                                Dialog.messageDialog(supportFragmentManager,
                                    getString(R.string.error_upload),response.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isFormNotEmpty(): Boolean{
        return binding.title.text.isNotEmpty() && binding.content.text.isNotEmpty() && communityViewModel.imagePost != null
    }

    private fun isEmpty():Boolean{
        return binding.title.text.trim().isNotEmpty() || binding.content.text.trim().isNotEmpty() || communityViewModel.imagePost != null
    }

    override fun onClick(v: View?) {
        when(v){
            binding.cover->{
                binding.cover.isEnabled = false
                launchPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            binding.send->{
                binding.send.isEnabled = false
                if (isFormNotEmpty()){
                    upload()
                }else{
                    Dialog.messageDialog(supportFragmentManager,getString(R.string.error_upload),
                        getString(R.string.form_empty))
                }
            }
            binding.back->{
                if (isEmpty()){
                    Dialog.confirmDialog(supportFragmentManager,
                        getString(R.string.are_you_sure), getString(R.string.post_not_save)){
                        finish()
                    }
                }else{
                    finish()
                }
            }
        }
    }
}