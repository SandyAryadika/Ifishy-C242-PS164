package com.ifishy.ui.adapter.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.data.model.community.response.PostsItem
import com.ifishy.databinding.CommunityItemBinding
import com.ifishy.utils.Date

class CommunityPostsAdapter(private val posts:List<PostsItem>): RecyclerView.Adapter<CommunityPostsAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: OnClick

    fun onItemClicked(onItemClickListener: OnClick){
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: CommunityItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PostsItem, context: Context){
            binding.date.text = item.createdAt?.let { Date.format(it) }
            binding.user.text = item.username
            binding.commentsCount.text = item.comments?.size.toString()
            binding.title.text = item.title
            binding.shareCount.text = String.format(0.toString())
            binding.voteCount.text = String.format(0.toString())
            Glide.with(context)
                .load(item.imageUrl)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommunityItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post,holder.itemView.context)
        holder.itemView.setOnClickListener{onItemClickListener.getDetail(post.id)}
    }

    interface OnClick{
        fun getDetail(id: Int?)
    }

}
