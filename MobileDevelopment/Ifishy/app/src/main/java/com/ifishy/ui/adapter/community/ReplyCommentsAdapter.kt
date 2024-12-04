package com.ifishy.ui.adapter.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.comments.RepliesItem
import com.ifishy.databinding.ReplyItemBinding

class ReplyCommentsAdapter(private val reply: List<RepliesItem?>): RecyclerView.Adapter<ReplyCommentsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ReplyItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: RepliesItem,context: Context){
            binding.content.text = item.content
            binding.username.text = item.username
            binding.date.text = item.createdAt
            Glide.with(context)
                .load(item.profilePicture)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_placeholder))
                .error(ContextCompat.getDrawable(context, R.drawable.user_placeholder))
                .into(binding.profilePicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReplyItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = reply.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val replies = reply.reversed()[position]
        if (replies != null) {
            holder.bind(replies,holder.itemView.context)
        }
    }

}