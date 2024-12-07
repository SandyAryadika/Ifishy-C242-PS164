package com.ifishy.ui.activity.bookmark.fragmentBookmark


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentBookmarkPostBinding
import com.ifishy.ui.activity.detail_post.DetailPostActivity
import com.ifishy.ui.adapter.community.post.CommunityPostsBookmarkAdapter
import com.ifishy.ui.viewmodel.BookmarkViewModel
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkPostFragment : Fragment() {

    private var _binding: FragmentBookmarkPostBinding?=null
    private val binding get() = _binding!!
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val communityViewModel: CommunityViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val adapter by lazy { CommunityPostsBookmarkAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkPostBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            getAllPostBookmark(token)
        }

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loading.apply {
                this.startShimmer()
                this.visibility =View.VISIBLE
            }
            binding.rvPost.visibility = View.GONE
            binding.error.visibility = View.GONE
        }else{
            binding.loading.apply {
                this.stopShimmer()
                this.visibility =View.GONE
            }
            binding.rvPost.visibility = View.VISIBLE
            binding.error.visibility = View.GONE
        }
    }

    private fun getAllPostBookmark(token:String){
        bookmarkViewModel.listPost.clear()
        bookmarkViewModel.getAllBookmark(token).apply {
            bookmarkViewModel.allBookmark.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        response.data.bookmarks?.filter { it.type == "post" }.let { article ->
                            val listId: MutableList<Int> = mutableListOf()
                            article?.forEach { item->
                                item.itemId?.let { listId.add(it) }
                            }

                            communityViewModel.getAllPosts(token).apply {
                                communityViewModel.posts.observe(viewLifecycleOwner){ posts->
                                    when(posts){
                                        is ResponseState.Loading -> {}
                                        is ResponseState.Success -> {
                                            val filtered = posts.data.posts?.filter { it.id in listId }
                                            if (filtered?.isEmpty() == true){
                                                isLoading(false)
                                                binding.error.apply {
                                                    this.text = ContextCompat.getString(requireContext(),R.string.no_bookmark_yet)
                                                    this.visibility = View.VISIBLE
                                                }
                                            }else{
                                                filtered?.forEach { item->
                                                    bookmarkViewModel.listPost.add(item)
                                                }
                                                isLoading(false)
                                                adapter.submitData(bookmarkViewModel.listPost)
                                                adapter.onItemClicked(object : CommunityPostsBookmarkAdapter.OnClick{
                                                    override fun getDetail(id: Int?) {
                                                        startActivity(Intent(requireContext(),DetailPostActivity::class.java)
                                                            .putExtra(DetailPostActivity.POST_ID,id)
                                                        )
                                                    }

                                                })
                                                binding.rvPost.adapter = adapter
                                                binding.rvPost.layoutManager = LinearLayoutManager(requireActivity())

                                            }
                                        }
                                        is ResponseState.Error -> {
                                            isLoading(false)
                                            binding.error.apply {
                                                this.text = ContextCompat.getString(requireContext(),R.string.no_bookmark_yet)
                                                this.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.error.apply {
                            this.text = response.message
                            this.visibility = View.VISIBLE
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