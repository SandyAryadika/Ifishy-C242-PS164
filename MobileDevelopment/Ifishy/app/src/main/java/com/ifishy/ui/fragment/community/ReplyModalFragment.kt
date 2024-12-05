package com.ifishy.ui.fragment.community


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentReplyModalBinding
import com.ifishy.ui.adapter.comments.ReplyCommentsAdapter
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplyModalFragment : BottomSheetDialogFragment() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val communityViewModel: CommunityViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var _binding:FragmentReplyModalBinding?=null
    private val binding get() = _binding!!
    private val adapter by lazy { ReplyCommentsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReplyModalBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            getAllComments(token,arguments?.getInt("ID")!!)
            preferenceViewModel.email.observe(viewLifecycleOwner){email->
                getMyProfile(token,email)
            }
        }

        binding.send.setOnClickListener{
            if(binding.commentsContent.text.isNotEmpty()){
                preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
                    addReply(token,arguments?.getInt("ID")!!,binding.commentsContent.text.toString())
                }
            }
        }
    }

    private fun getMyProfile(token: String,email:String){
        profileViewModel.getProfile(token,email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        binding.profile.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                    }
                    is ResponseState.Success -> {
                        Glide.with(requireActivity())
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                            .error(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                            .into(binding.profile)
                    }
                    is ResponseState.Error -> {
                        binding.profile.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                    }
                }
            }
        }
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loading.visibility = View.VISIBLE
            binding.comments.visibility = View.INVISIBLE
            binding.parentComment.visibility = View.GONE
        }else{
            binding.loading.visibility = View.GONE
            binding.comments.visibility = View.VISIBLE
            binding.parentComment.visibility = View.VISIBLE
        }
    }

    private fun getAllComments(token:String,id:Int){
        communityViewModel.getCommentById(token,id).apply {
            communityViewModel.commentById.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        response.data.comment?.replies?.let { adapter.submitData(it) }
                        binding.comments.apply {
                            this.adapter = this@ReplyModalFragment.adapter
                            this.layoutManager = LinearLayoutManager(requireActivity())
                        }
                        response.data.comment?.let {
                            binding.likeIcon.setImageDrawable(if (it.userLiked == true) ContextCompat.getDrawable(requireActivity(),R.drawable.like_fill) else ContextCompat.getDrawable(requireActivity(),R.drawable.like_false) )
                            binding.dateParent.text = it.createdAt
                            binding.usernameParent.text = it.username
                            binding.contentParent.text = it.content
                            binding.likeCount.text = String.format((it.likeCount ?: 0).toString())
                            binding.replyParent.text = requireContext().getString(R.string.reply, it.replies?.size ?: 0)
                            Glide.with(requireActivity())
                                .load(it.profilePicture)
                                .error(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                                .placeholder(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                                .into(binding.profilePicture)
                        }
                    }
                    is ResponseState.Error -> {
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun isUploadLoading(loading: Boolean){
        if (loading){
            binding.loadingUpload.visibility = View.VISIBLE
            binding.send.apply {
                this.setImageDrawable(null)
                this.isEnabled = false
            }
        }else{
            binding.loadingUpload.visibility = View.GONE
            binding.send.apply {
                this.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.send))
                this.isEnabled = true
            }
        }
    }

    private fun addReply(token: String,id: Int,content : String){
        communityViewModel.addReply(token,id,content).apply {
            communityViewModel.addReply.observe(viewLifecycleOwner){event->
                event.getContentIfNotHandled()?.let { response->
                    when(response){
                        is ResponseState.Loading -> {
                            isUploadLoading(true)
                        }
                        is ResponseState.Success -> {
                            isUploadLoading(false)
                            Toast.makeText(
                                requireActivity(),
                                response.data.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            getAllComments(token,id)
                            binding.commentsContent.text.clear()
                            binding.commentsContent.clearFocus()
                        }
                        is ResponseState.Error -> {
                            isUploadLoading(false)
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.error_comments),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}