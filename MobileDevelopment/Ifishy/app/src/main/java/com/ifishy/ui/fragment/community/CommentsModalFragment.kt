package com.ifishy.ui.fragment.community

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentCommentsModalBinding
import com.ifishy.ui.adapter.community.PostsCommentsAdapter
import com.ifishy.ui.viewmodel.CommunityViewModel
import com.ifishy.ui.viewmodel.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsModalFragment : BottomSheetDialogFragment() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val communityViewModel: CommunityViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentCommentsModalBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentsAdapter: PostsCommentsAdapter

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
                        commentsAdapter = response.data.comments?.let { PostsCommentsAdapter(it) }!!
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
                            })
                        }
                    }

                    is ResponseState.Error -> {
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun addComment(token: String, id: Int, content: String) {
        communityViewModel.addComments(token, id, content).apply {
            communityViewModel.addComment.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        ResponseState.Loading -> {

                        }

                        is ResponseState.Success -> {
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