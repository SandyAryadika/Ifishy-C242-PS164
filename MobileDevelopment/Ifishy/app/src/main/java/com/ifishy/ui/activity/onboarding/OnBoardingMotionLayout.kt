package com.ifishy.ui.activity.onboarding

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import com.airbnb.lottie.LottieAnimationView
import com.ifishy.R
import com.ifishy.ui.activity.opening.OpeningActivity

class OnBoardingMotionLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
): MotionLayout(context, attributeSet, defStyleAttr) {

    private var animated = false

    init {
        this.setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                animated = true
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, cuurentId: Int) {
                animated = false
                if(cuurentId == R.id.end){
                    val lottieView = findViewById<LottieAnimationView>(R.id.anim_2)
                    lottieView?.playAnimation()
                }
                if (cuurentId == R.id.last){
                    val intent = Intent(context, OpeningActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent,ActivityOptions.makeCustomAnimation(context,android.R.anim.fade_in,android.R.anim.fade_out).toBundle())
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (animated) return true
        return super.dispatchTouchEvent(ev)
    }
}