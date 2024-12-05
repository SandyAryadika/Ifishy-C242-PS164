package com.ifishy.ui.fragment.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentCommentsModalBinding
import com.ifishy.ui.adapter.comments.PostsCommentsAdapter
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsModalFragment : BottomSheetDialogFragment() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val communityViewModel: CommunityViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentCommentsModalBinding? = null
    private val binding get() = _binding!!
    private val commentsAdapter by lazy { PostsCommentsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsModalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
            getAllComments(token, arguments?.getInt("ID")!!)
            preferenceViewModel.email.observe(viewLifecycleOwner) { email ->
                getMyProfile(token, email)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
            getAllComments(token, arguments?.getInt("ID")!!)
            preferenceViewModel.email.observe(viewLifecycleOwner) { email ->
                getMyProfile(token, email)
            }
        }

        binding.send.setOnClickListener {

            if (binding.commentsContent.text.isNotEmpty()) {
                preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
                    addComment(
                        token,
                        arguments?.getInt("ID")!!,
                        binding.commentsContent.text.toString()
                    )
                }
            }
        }
    }


    private fun getMyProfile(token: String, email: String) {
        profileViewModel.getProfile(token, email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        binding.profile.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.user_placeholder
                            )
                        )
                    }

                    is ResponseState.Success -> {
                        Glide.with(requireActivity())
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.user_placeholder
                                )
                            )
                            .error(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.user_placeholder
                                )
                            )
                            .into(binding.profile)
                    }

                    is ResponseState.Error -> {
                        binding.profile.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.user_placeholder
                            )
                        )
                    }
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.loading.visibility = View.VISIBLE
            binding.comments.visibility = View.INVISIBLE
            binding.error.visibility = View.GONE
        } else {
            binding.loading.visibility = View.GONE
            binding.comments.visibility = View.VISIBLE
        }
    }

    private fun getAllComments(token: String, id: Int) {
        communityViewModel.getAllComments(token, id).apply {
            communityViewModel.comments.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }

                    is ResponseState.Success -> {
                        isLoading(false)
                        if (response.data.comments?.isEmpty() == true){
                            binding.comments.visibility = View.GONE
                            binding.error.apply {
                                this.visibility = View.VISIBLE
                                this.text = context.getString(R.string.no_comments_yet)
                            }
                        }else{
                            binding.comments.visibility = View.VISIBLE
                            binding.error.apply {
                                this.visibility = View.GONE
                                this.text = ""
                            }
                            response.data.comments?.let { commentsAdapter.submitData(it) }
                            binding.comments.apply {
                                this.adapter = commentsAdapter
                                this.layoutManager = LinearLayoutManager(requireActivity())
                                commentsAdapter.onItemClicked(object : PostsCommentsAdapter.OnClick {
                                    override fun onClickedItem(id: Int) {
                                        val bundle = Bundle()
                                        bundle.putInt("ID", id)
                                        val replyDialog = ReplyModalFragment()
                                        replyDialog.arguments = bundle
                                        parentFragmentManager.commit {
                                            add(replyDialog,replyDialog::class.java.simpleName)
                                            addToBackStack(null)
                                        }
                                    }

                                    override fun like(id: Int) {
                                        like(token,id)
                                    }

                                    override fun unLike(id: Int) {
                                        unLike(token,id)
                                    }
                                })
                            }
                        }

                    }

                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.comments.visibility = View.GONE
                        binding.error.apply {
                            this.visibility = View.VISIBLE
                            this.text = response.message
                        }
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

    private fun addComment(token: String, id: Int, content: String) {
        communityViewModel.addComments(token, id, content).apply {
            communityViewModel.addComment.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        ResponseState.Loading -> {
                            isUploadLoading(true)
                        }

                        is ResponseState.Success -> {
                            isUploadLoading(false)
                            Toast.makeText(
                                requireActivity(),
                                response.data.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            getAllComments(token, id)
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

    private fun like(token: String,id: Int){
        communityViewModel.likePost(token,id).apply {
            communityViewModel.likePost.observe(viewLifecycleOwner){event->
                event.getContentIfNotHandled()
            }
        }
    }

    private fun unLike(token: String,id: Int){
        communityViewModel.unLikePost(token,id).apply{
            communityViewModel.unLikePost.observe(viewLifecycleOwner){event->
                event.getContentIfNotHandled()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}