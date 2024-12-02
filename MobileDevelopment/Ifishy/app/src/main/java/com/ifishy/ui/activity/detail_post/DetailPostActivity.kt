package com.ifishy.ui.activity.detail_post

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityDetailPostBinding
import com.ifishy.ui.fragment.community.CommentsModalFragment
import com.ifishy.ui.viewmodel.CommunityViewModel
import com.ifishy.utils.Date
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPostActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityDetailPostBinding
    private val communityViewModel : CommunityViewModel by viewModels()
    private val preferencesViewModel: PreferenceViewModel by viewModels()
    private var id:Int?=null

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

        binding.back.setOnClickListener(this)
        binding.comments.setOnClickListener(this)

        id = intent.getIntExtra(POST_ID,0)
        preferencesViewModel.token.observe(this@DetailPostActivity){token->
            id?.let { getDetail(token,it) }
        }

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.apply {
                this.loading.visibility = View.VISIBLE
                this.postsContent.visibility = View.GONE
                this.error.visibility = View.GONE
            }
        }else{
            binding.apply {
                this.loading.visibility = View.GONE
                this.postsContent.visibility = View.VISIBLE
            }
        }
    }

    private fun getDetail(token:String, id:Int){
        communityViewModel.getPostById(token,id).apply {
            communityViewModel.postById.observe(this@DetailPostActivity){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
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
                        isLoading(false)
                        binding.postsContent.visibility = View.GONE
                        binding.error.apply {
                            this.visibility = View.VISIBLE
                            this.text = response.message
                        }
                    }
                }
            }
        }
    }

    companion object{
        const val POST_ID = "POST_ID"
    }

    override fun onClick(v: View?) {
        when(v){
            binding.back->{
                finish()
            }
            binding.comments->{
                val modal = CommentsModalFragment()
                val bundle = Bundle()
                id?.let { bundle.putInt("ID",it)}
                modal.arguments = bundle
                modal.show(supportFragmentManager,modal::class.java.simpleName)
            }
        }
    }

}