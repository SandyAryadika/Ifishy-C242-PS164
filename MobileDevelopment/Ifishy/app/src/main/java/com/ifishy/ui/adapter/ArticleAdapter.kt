package com.ifishy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifishy.databinding.ItemFullFeatureBinding

class ArticleAdapter : ListAdapter<, CommunityAdapter.ArticleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemFullFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArticleViewHolder(private val binding: ItemFullFeatureBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    class DiffCallback : DiffUtil.ItemCallback<>() {
        override fun areItemsTheSame(oldItem: , newItem: ) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: , newItem: ) = oldItem == newItem
    }
}
