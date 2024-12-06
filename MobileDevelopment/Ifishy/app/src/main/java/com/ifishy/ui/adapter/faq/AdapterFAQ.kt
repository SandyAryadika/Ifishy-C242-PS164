package com.ifishy.ui.adapter.faq

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifishy.databinding.ItemFaqBinding
import com.ifishy.ui.viewmodel.FaqItem

class AdapterFAQ(
    private var faqList: List<FaqItem>,
    private val onFaqClick: (Int) -> Unit
) : RecyclerView.Adapter<AdapterFAQ.FaqViewHolder>() {

    inner class FaqViewHolder(private val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(faqItem: FaqItem) {
            binding.question.text = faqItem.question
            binding.answer.text = faqItem.answer

            binding.expandButton.animate()
                .rotation(if (faqItem.isExpanded) 90f else 270f)
                .setDuration(300)
                .withEndAction {

                    binding.expandButton.rotation = if (faqItem.isExpanded) 90f else 270f
                }
                .start()

            val visibilityAnim = ObjectAnimator.ofFloat(binding.answer, "alpha", if (faqItem.isExpanded) 1f else 0f)
            visibilityAnim.duration = 300
            visibilityAnim.start()

            binding.answer.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE


            binding.expandButton.setOnClickListener {
                onFaqClick(adapterPosition)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(faqList[position])
    }

    override fun getItemCount(): Int = faqList.size

    fun updateData(newFaqList: List<FaqItem>) {
        faqList = newFaqList
        notifyDataSetChanged()
    }

}


