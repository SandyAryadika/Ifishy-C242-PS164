package com.ifishy.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.ifishy.R

class ScanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val scanBorder = ContextCompat.getDrawable(context, R.drawable.scan_border)

    private val overlayPaint = Paint().apply {
        color = Color.BLACK
        alpha = (0.4 * 255).toInt()
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val cornerRadius: Float = context.resources.displayMetrics.density * 10

    private val lottieView: LottieAnimationView = LottieAnimationView(context).apply {
        setAnimation(R.raw.scan)
        repeatCount = LottieDrawable.INFINITE
        scaleType = ImageView.ScaleType.CENTER_CROP
        playAnimation()
    }

    init {
        addView(lottieView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        val holeWidth = width * 0.8f
        val holeHeight = height * 0.4f
        val left = (width - holeWidth) / 2
        val top = (height - holeHeight) / 2
        val right = left + holeWidth
        val bottom = top + holeHeight

        canvas.drawRoundRect(left, top, right, bottom, cornerRadius,cornerRadius,clearPaint)

        scanBorder?.let {
            val drawableLeft = left.toInt()
            val drawableTop = top.toInt()
            val drawableRight = right.toInt()
            val drawableBottom = bottom.toInt()

            it.setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
            it.draw(canvas)
        }
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)

        val holeWidth = width * 0.77f
        val holeHeight = height * 0.39f
        val holeLeft = (width - holeWidth) / 2
        val holeTop = (height - holeHeight) / 2

        val lottieLeft = holeLeft.toInt()
        val lottieTop = holeTop.toInt()
        val lottieRight = (holeLeft + holeWidth).toInt()
        val lottieBottom = (holeTop + holeHeight).toInt()

        lottieView.layout(lottieLeft, lottieTop, lottieRight, lottieBottom)
    }
}