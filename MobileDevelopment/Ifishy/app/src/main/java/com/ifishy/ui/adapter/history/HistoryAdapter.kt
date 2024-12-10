package com.ifishy.ui.adapter.history

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.history.DataItem
import com.ifishy.databinding.HistoryItemBinding


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: OnClick

    fun setOnItemClickListener(listener: OnClick) {
        this.onItemClickListener = listener
    }

    private val diffUtil = object : DiffUtil.ItemCallback<DataItem>() {
        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun submitData(data: List<DataItem>){
        asyncListDiffer.submitList(data)
    }

    inner class ViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem, context: Context) {
            binding.judulArtikelHistory.text = item.disease
            binding.dateHistoryItem.text = item.scanTimestamp

            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.shimmer)))
                .error(ColorDrawable(ContextCompat.getColor(context, R.color.shimmer)))
                .into(binding.itemPhoto)

            binding.root.setOnClickListener {
                onItemClickListener.onItemClicked(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item, holder.itemView.context)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    interface OnClick {
        fun onItemClicked(id: Int?)
    }
}