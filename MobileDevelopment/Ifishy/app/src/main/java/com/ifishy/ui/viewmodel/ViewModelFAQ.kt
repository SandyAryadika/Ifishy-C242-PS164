package com.ifishy.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ifishy.R


class FaqItem (
    val question : String,
    val answer : String,
    var isExpanded : Boolean = false
)

class ViewModelFAQ(private val context: Context): ViewModel() {

    private val _faqlist = MutableLiveData<List<FaqItem>>()
    val faqlist: LiveData<List<FaqItem>> = _faqlist


    init {
        _faqlist.value = listOf(
            FaqItem(context.getString(R.string.q1), context.getString(R.string.a1)),
            FaqItem(context.getString(R.string.q2), context.getString(R.string.a2)),
            FaqItem(context.getString(R.string.q3), context.getString(R.string.a3)),
            FaqItem(context.getString(R.string.q4), context.getString(R.string.a4)),
            FaqItem(context.getString(R.string.q5), context.getString(R.string.a5)),
            FaqItem(context.getString(R.string.q6), context.getString(R.string.a6)),
            FaqItem(context.getString(R.string.q7), context.getString(R.string.a7)),
            FaqItem(context.getString(R.string.q8), context.getString(R.string.a8)),
            FaqItem(context.getString(R.string.q9), context.getString(R.string.a9)),
            FaqItem(context.getString(R.string.q10), context.getString(R.string.a10))
        )
    }

    fun toggleFaqExpansion(index: Int) {
        _faqlist.value?.let { list ->
            val updatedList = list.toMutableList()
            updatedList[index].isExpanded = !updatedList[index].isExpanded
            _faqlist.value = updatedList
        }
    }
}