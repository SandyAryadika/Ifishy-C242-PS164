package com.ifishy.ui.adapter.article

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.article.DataItem
import com.ifishy.databinding.ArticleItemVerticalBinding
import com.ifishy.utils.Date

class ArticleVerticalAdapter(private var articles: List<DataItem>, private val size:Int?=null): RecyclerView.Adapter<ArticleVerticalAdapter.ViewHolder>() {

    private lateinit var onItemClicked: OnClickArticlesVertical

    fun onItemClicked(onItemClicked: OnClickArticlesVertical){
        this.onItemClicked = onItemClicked
    }

    inner class ViewHolder(private val binding: ArticleItemVerticalBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: DataItem,context: Context){
            binding.author.text = item.author
            binding.title.text = item.title
            binding.date.text = Date.format(item.publishedAt!!)
            Glide.with(context)
                .load(item.coverImage)
                .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.shimmer)))
                .error(ColorDrawable(ContextCompat.getColor(context, R.color.shimmer)))
                .into(binding.articleImages)
        }
    }

    fun submitData(newDataItem: List<DataItem>){
        this.articles = newDataItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleVerticalAdapter.ViewHolder {
        val binding = ArticleItemVerticalBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleVerticalAdapter.ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article,holder.itemView.context)
        holder.itemView.setOnClickListener { onItemClicked.toDetail(article.id!!)
         }
    }

    override fun getItemCount(): Int = size ?: articles.size

    interface OnClickArticlesVertical{
        fun toDetail(id:Int)
    }

}