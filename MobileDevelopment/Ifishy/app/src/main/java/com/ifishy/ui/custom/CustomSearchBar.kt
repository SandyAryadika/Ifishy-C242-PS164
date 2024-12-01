package com.ifishy.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ifishy.R

class CustomSearchBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) :
    AppCompatEditText(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_shape)
        setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.search_icon),null,null,null)
        typeface = ResourcesCompat.getFont(context,R.font.poppins_medium)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        textSize = 16f
        compoundDrawablePadding = 20
        hint = ContextCompat.getString(context,R.string.hint_search)
    }
}