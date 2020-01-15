package com.nanoyatsu.nastodon.components

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ViewerConstraintLayout @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var scalableView: View? = null

    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private val panGestureDetector = GestureDetectorCompat(context, PanListener())
    private val translationMarginX // 最大移動量X
        get() = scalableView?.let { max(0f, (it.width * scaleFactor - width) / 2f) } ?: 0f
    private val translationMarginY // 最大移動量Y
        get() = scalableView?.let { max(0f, (it.height * scaleFactor - height) / 2f) } ?: 0f

    private var scaleFactor = 1.0f // 拡縮係数
    private var totalTranslationX = 0f // 移動量X
    private var totalTranslationY = 0f // 移動量Y

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        panGestureDetector.onTouchEvent(event)

        // 横移動しきっていない時、親(たとえばViewPager)のTouchEventを機能させない
        if (translationMarginX != abs(totalTranslationX))
            parent.requestDisallowInterceptTouchEvent(true)
        return true
    }

    private inner class PanListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            val translationX = totalTranslationX - distanceX
            val translationY = totalTranslationY - distanceY
            adjustTranslation(translationX, translationY)
            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(0.2f, min(scaleFactor, 5.0f))
            adjustScale(scaleFactor)
            return true
        }
    }

    private fun adjustScale(scale: Float) = scalableView?.also {
        it.scaleX = scale
        it.scaleY = scale

        // 移動が不要なサイズまで縮小した時に移動量0に矯正する
        adjustTranslation(totalTranslationX, totalTranslationY)
    }

    // fixme なぜかit.heightが見た目より大きいので縦の移動が想定通りでない
    private fun adjustTranslation(translationX: Float, translationY: Float) {
        scalableView?.also {
            // 移動量を各最大幅までに抑える
            val translationSuppress: (Float, Float) -> Float =
                { dist, margin -> if (dist < 0) max(-margin, dist) else min(dist, margin) }

            totalTranslationX = translationSuppress(translationX, translationMarginX)
            totalTranslationY = translationSuppress(translationY, translationMarginY)
            it.translationX = totalTranslationX
            it.translationY = totalTranslationY
        }
    }
}

@BindingAdapter("scalable_view")
fun setScalableView(view: ViewerConstraintLayout, scalable: View?) {
    scalable?.let {
        view.scalableView = scalable
    }
}
