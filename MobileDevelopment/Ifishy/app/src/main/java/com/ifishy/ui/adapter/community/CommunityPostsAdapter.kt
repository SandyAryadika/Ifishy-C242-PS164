package com.ifishy.ui.adapter.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.community.response.PostsItem
import com.ifishy.databinding.CommunityItemBinding
import com.ifishy.utils.Date

class CommunityPostsAdapter:
    ListAdapter<PostsItem,CommunityPostsAdapter.ViewHolder>(DiffCallback()) {

    private lateinit var onItemClickListener: OnClick

    fun onItemClicked(onItemClickListener: OnClick) {
        this.onItemClickListener = onItemClickListener
    }

    class DiffCallback : DiffUtil.ItemCallback<PostsItem>() {
        override fun areItemsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: CommunityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostsItem, context: Context) {
            binding.content.setOnClickListener {
                onItemClickListener.getDetail(item.id)
            }
            binding.date.text = item.createdAt?.let { Date.format(it) }
            binding.user.text = item.username
            binding.commentsCount.text = item.comments?.size.toString()
            binding.title.text = item.title
            binding.shareCount.text = item.shareCount.toString()
            binding.voteCount.text = item.voteCount.toString()
            binding.upvote.setImageDrawable(
                if (item.voteStatus == "upvote") ContextCompat.getDrawable(
                    context,
                    R.drawable.vote_fill
                ) else ContextCompat.getDrawable(context, R.drawable.vote_false)
            )
            binding.downvote.imageTintList =
                if (item.voteStatus == "downvote") ContextCompat.getColorStateList(
                    context,
                    R.color.hint_edit_text
                ) else ContextCompat.getColorStateList(context, R.color.secondary_light)
            fun setUpvoteAction() {
                binding.upvote.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.vote_fill
                    )
                )
                binding.downvote.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.secondary_light)
                onItemClickListener.upVote(item.id)
            }

            fun setDownvoteAction() {
                binding.upvote.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.vote_false
                    )
                )
                binding.downvote.imageTintList = ContextCompat.getColorStateList(context, R.color.hint_edit_text)
                onItemClickListener.downVote(item.id)
            }

            when (item.voteStatus) {
                "upvote" -> {
                    binding.downvote.setOnClickListener { setDownvoteAction() }
                    binding.upvote.setOnClickListener { setUpvoteAction() }
                }

                "downvote" -> {
                    binding.upvote.setOnClickListener { setUpvoteAction() }
                    binding.downvote.setOnClickListener { setDownvoteAction() }
                }

                null -> {
                    binding.upvote.setOnClickListener {
                        if (binding.voteCount.text.toString().toInt() == item.voteCount?.plus(1)){
                            setUpvoteAction()
                        }else{
                            binding.voteCount.text = item.voteCount?.plus(1).toString()
                            setUpvoteAction()
                        }
                    }
                    binding.downvote.setOnClickListener {
                        if (binding.voteCount.text.toString().toInt() == item.voteCount?.plus(1)){
                            setDownvoteAction()
                        }else{
                            binding.voteCount.text = item.voteCount?.plus(1).toString()
                            setDownvoteAction()
                        }
                    }
                }
            }
            Glide.with(context)
                .load(item.imageUrl)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CommunityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, holder.itemView.context)
    }

    interface OnClick {
        fun getDetail(id: Int?)
        fun upVote(id: Int?)
        fun downVote(id: Int?)
    }

}
