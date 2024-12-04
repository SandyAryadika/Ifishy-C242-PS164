package com.ifishy.ui.adapter.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.comments.CommentsItem
import com.ifishy.databinding.CommentsItemBinding

class PostsCommentsAdapter(private val comments: List<CommentsItem>) :
    RecyclerView.Adapter<PostsCommentsAdapter.ViewHolder>() {

    private lateinit var onItemClick: OnClick

    fun onItemClicked(onItemClick: OnClick) {
        this.onItemClick = onItemClick
    }

    inner class ViewHolder(private val binding: CommentsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentsItem, context: Context) {

            var liked: Boolean = item.userLiked

            binding.content.text = item.content
            binding.username.text = item.username
            binding.date.text = item.createdAt
            binding.reply.text = context.getString(R.string.reply, item.replies?.size ?: 0)
            binding.likeCount.text = item.likeCount.toString()

            binding.content.setOnClickListener { onItemClick.onClickedItem(item.id!!) }

            fun updateLikeIcon(isLiked: Boolean) {
                val icon = if (isLiked) R.drawable.like_fill else R.drawable.like_false
                binding.likeIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
            }

            updateLikeIcon(liked)

            binding.likeIcon.setOnClickListener {
                if (liked) {
                    updateLikeIcon(false).apply {
                        onItemClick.unLike(item.id!!)
                        binding.likeCount.text = String.format((binding.likeCount.text.toString().toInt().minus(1)).toString())
                        liked = false
                    }
                } else {
                    updateLikeIcon(true).apply {
                        onItemClick.like(item.id!!)
                        binding.likeCount.text = String.format((binding.likeCount.text.toString().toInt().plus(1)).toString())
                        liked = true
                    }
                }
            }

            Glide.with(context)
                .load(item.profilePic)
                .error(ContextCompat.getDrawable(context, R.drawable.user_placeholder))
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_placeholder))
                .into(binding.profilePicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CommentsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments.reversed()[position]
        holder.bind(comment, holder.itemView.context)


    }

    interface OnClick {
        fun onClickedItem(id: Int)
        fun like(id: Int)
        fun unLike(id: Int)
    }

}