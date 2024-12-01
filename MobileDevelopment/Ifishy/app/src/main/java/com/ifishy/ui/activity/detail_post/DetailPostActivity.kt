package com.ifishy.ui.activity.detail_post

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityDetailPostBinding
import com.ifishy.ui.viewmodel.CommunityViewModel
import com.ifishy.utils.Date
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailPostBinding
    private val communityViewModel : CommunityViewModel by viewModels()
    private val preferencesViewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getIntExtra(POST_ID,0)
        preferencesViewModel.token.observe(this@DetailPostActivity){token->
            getDetail(token,id)
        }

    }

    private fun getDetail(token:String, id:Int){
        communityViewModel.getPostById(token,id).apply {
            communityViewModel.postById.observe(this@DetailPostActivity){response->
                when(response){
                    is ResponseState.Loading -> {
                        Toast.makeText(applicationContext,"loading",Toast.LENGTH_SHORT).show()
                    }
                    is ResponseState.Success -> {
                        binding.apply {
                            this.title.text = response.data.post.title
                            this.author.text = response.data.post.username
                            this.dateUpload.text = response.data.post.createdAt?.let { Date.format(it) }
                            this.descriptionArticle.text = response.data.post.content
                            Glide.with(this@DetailPostActivity)
                                .load(response.data.post.imageUrl)
                                .into(binding.image)
                        }
                    }
                    is ResponseState.Error -> {
                        Toast.makeText(applicationContext,response.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object{
        const val POST_ID = "POST_ID"
    }

}