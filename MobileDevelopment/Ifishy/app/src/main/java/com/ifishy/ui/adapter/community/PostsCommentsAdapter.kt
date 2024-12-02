package com.ifishy.ui.adapter.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifishy.R
import com.ifishy.data.model.comments.CommentsItem
import com.ifishy.databinding.CommentsItemBinding

class PostsCommentsAdapter(private val comments: List<CommentsItem>): RecyclerView.Adapter<PostsCommentsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CommentsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: CommentsItem,context: Context){
            binding.content.text = item.content
            binding.username.text = item.username
            binding.date.text = item.createdAt
            binding.reply.text = context.getString(R.string.reply, item.replies?.size ?: 0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommentsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val comment = comments[position]
        holder.bind(comment,holder.itemView.context)
    }

}