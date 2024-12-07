package com.ifishy.ui.adapter.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.comments.RepliesItem
import com.ifishy.databinding.ReplyItemBinding
import com.ifishy.utils.Date

class ReplyCommentsAdapter: RecyclerView.Adapter<ReplyCommentsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ReplyItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: RepliesItem,context: Context){
            binding.content.text = item.content
            binding.username.text = item.username
            binding.date.text = Date.format(item.createdAt!!)

            Glide.with(context)
                .load(item.profilePicture)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_placeholder))
                .error(ContextCompat.getDrawable(context, R.drawable.user_placeholder))
                .into(binding.profilePicture)
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<RepliesItem>() {
        override fun areItemsTheSame(oldItem: RepliesItem, newItem: RepliesItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RepliesItem, newItem: RepliesItem): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun submitData( data: List<RepliesItem>){
        asyncListDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReplyItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val replies = asyncListDiffer.currentList[position]
        if (replies != null) {
            holder.bind(replies,holder.itemView.context)
        }
    }

}