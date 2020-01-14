package com.nanoyatsu.nastodon.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ViewerConstraintLayout @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var scalableView: View? = null

    private val mScaleGestureDetector: ScaleGestureDetector =
        ScaleGestureDetector(context, ScaleListener())

    private var scaleFactor = 1.0f
    private var tmpTranslationX = 0f
    private var tmpTranslationY = 0f
    private var imageWidth = 0f
    private var imageHeight = 0f
    private var defaultImageWidth = 0f
    private var defaultImageHeight = 0f
    private val viewPortWidth: Float
        get() = this.width.toFloat()
    private val viewPortHeight: Float
        get() = this.height.toFloat()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleGestureDetector.onTouchEvent(event)

        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= mScaleGestureDetector.scaleFactor
            scaleFactor = max(0.2f, min(scaleFactor, 5.0f))
            scalableView?.scaleX = scaleFactor
            scalableView?.scaleY = scaleFactor
            imageWidth = defaultImageWidth * scaleFactor
            imageHeight = defaultImageHeight * scaleFactor

            adjustTranslation(tmpTranslationX, tmpTranslationY)

            return true
        }
    }

    private fun adjustTranslation(translationX: Float, translationY: Float) {
        val translationXMargin = abs((imageWidth - viewPortWidth) / 2)
        val translationYMargin = abs((imageHeight - viewPortHeight) / 2)

        tmpTranslationX =
            if (translationX < 0) max(translationX, -translationXMargin)
            else min(translationX, translationXMargin)
        tmpTranslationY =
            if (tmpTranslationY < 0) max(translationY, -translationYMargin)
            else min(translationY, translationYMargin)

        scalableView?.translationX = tmpTranslationX
        scalableView?.translationY = tmpTranslationY
    }

}

@BindingAdapter("scalable_view")
fun setScalableView(view: ViewerConstraintLayout, scalable: View?) {
    scalable?.let {
        view.scalableView = scalable
    }
}
