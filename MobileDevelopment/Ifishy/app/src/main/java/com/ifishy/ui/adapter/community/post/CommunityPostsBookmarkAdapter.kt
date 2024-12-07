package com.ifishy.ui.adapter.community.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.community.response.PostsItem
import com.ifishy.databinding.CommunityBookmarkItemBinding
import com.ifishy.databinding.CommunityItemBinding
import com.ifishy.utils.Date

class CommunityPostsBookmarkAdapter : RecyclerView.Adapter<CommunityPostsBookmarkAdapter.ViewHolder>(){

    private lateinit var onItemClickListener: OnClick

    fun onItemClicked(onItemClickListener: OnClick) {
        this.onItemClickListener = onItemClickListener
    }

    private val diffUtil = object : DiffUtil.ItemCallback<PostsItem>() {
        override fun areItemsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun submitData( data: List<PostsItem>){
        asyncListDiffer.submitList(data)
    }

    inner class ViewHolder(private val binding: CommunityBookmarkItemBinding) :
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

            Glide.with(context)
                .load(item.imageUrl)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CommunityBookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = asyncListDiffer.currentList[position]
        holder.bind(post, holder.itemView.context)
    }

    interface OnClick {
        fun getDetail(id: Int?)
    }

}
