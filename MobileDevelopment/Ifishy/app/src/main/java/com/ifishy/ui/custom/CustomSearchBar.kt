package com.ifishy.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ifishy.R

class CustomSearchBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) :
    AppCompatEditText(context, attrs), View.OnTouchListener {


        private var clearIcon: Drawable?=null
        private var searchIcon: Drawable?=null

    init {
        searchIcon = ContextCompat.getDrawable(context,R.drawable.search_icon)
        clearIcon = ContextCompat.getDrawable(context,R.drawable.clear)
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_shape)

        setCompoundDrawablesRelativeWithIntrinsicBounds(searchIcon,null,null,null)
        typeface = ResourcesCompat.getFont(context,R.font.poppins_medium)

        includeFontPadding = false

        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(text: Editable?) {
                text?.let {
                    if(text.isNotEmpty()){
                        show()
                    }else{
                        hide()
                    }
                }
            }
        })

        hint = ContextCompat.getString(context,R.string.hint_search)

        setOnTouchListener(this)
    }

    fun show(){
        setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.search_icon),null,clearIcon,null)
    }

    fun hide(){
        setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.search_icon),null,null,null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        textSize = 16f
        compoundDrawablePadding = 20
        maxLines = 1
        isSingleLine = true
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val clear: Drawable? = compoundDrawablesRelative[2]
            if (clear != null) {
                val width = clear.bounds.width()
                val touchX = width - paddingEnd - width
                if (event.x >= touchX) {
                    setText("")
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }


}