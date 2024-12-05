package com.ifishy.ui.adapter.article

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.data.model.article.DataItem
import com.ifishy.databinding.ArticleItemHorizontalBinding
import com.ifishy.utils.Date

class ArticleHorizontalAdapter(private val articles: List<DataItem>): RecyclerView.Adapter<ArticleHorizontalAdapter.ViewHolder>() {

    private lateinit var onItemClicked: OnClickArticle

    fun onItemClicked(onItemClicked: OnClickArticle){
        this.onItemClicked = onItemClicked
    }

    inner class ViewHolder(private val binding: ArticleItemHorizontalBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: DataItem,context: Context){
            binding.author.text = item.author
            binding.title.text = item.title
            binding.datePublished.text = Date.format(item.publishedAt!!)
            Glide.with(context)
                .load(item.coverImage)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleHorizontalAdapter.ViewHolder {
        val binding = ArticleItemHorizontalBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleHorizontalAdapter.ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article,holder.itemView.context)
        holder.itemView.setOnClickListener{ article.id?.let { it1 -> onItemClicked.toDetail(it1) } }
    }

    override fun getItemCount(): Int = 6

    interface OnClickArticle{
        fun toDetail(id:Int)
    }

}