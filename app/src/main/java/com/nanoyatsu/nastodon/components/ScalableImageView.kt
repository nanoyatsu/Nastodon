package com.nanoyatsu.nastodon.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign


class ScalableImageView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val transformMatrix = Matrix()
    private var scale = 1f
    private var focusX = 0f
    private var focusY = 0f
    private var transX = 0f
    private var transY = 0f
    private val viewRect = RectF()
    private val drawableRect = RectF()
    private val matrixToFit = Matrix()
    private val matToFit = FloatArray(9)
    private val imageMat = FloatArray(9)

    private val gestureDetector: GestureDetectorCompat
    private val scaleGestureDetector: ScaleGestureDetector

    init {
        scaleGestureDetector =
            ScaleGestureDetector(getContext(), object : SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scale = detector.scaleFactor
                    focusX = detector.focusX
                    focusY = detector.focusY
                    return true
                }
            })
        gestureDetector = GestureDetectorCompat(getContext(), object : SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
            ): Boolean {
                if (e2.eventTime - e1.eventTime < 200 && shouldGoNextPage(distanceX))
                    return false

                transX = -distanceX
                transY = -distanceY
                return true
            }

            private fun shouldGoNextPage(distX: Float): Boolean {
                if (abs(distX) < 0.1)
                    return false
                if (drawable == null)
                    return true
                if (height.toFloat() == drawable.intrinsicHeight * imageMat[Matrix.MSCALE_Y])
                    return true

                return if (distX > 0) {
                    val maxTransX = width - drawable.intrinsicWidth * imageMat[Matrix.MSCALE_X]
                    (maxTransX <= 0 && abs(maxTransX - imageMat[Matrix.MTRANS_X]) < 0.01)
                } else
                    abs(imageMat[Matrix.MTRANS_X]) < 0.01

            }
        })
        gestureDetector.setIsLongpressEnabled(false)
        scaleType = ScaleType.MATRIX
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        val scaling = scaleGestureDetector.isInProgress
        val scrolled = gestureDetector.onTouchEvent(event)
        val invalidated = scaling || scrolled

        if (scaling) transformMatrix.postScale(scale, scale, focusX, focusY)
        if (scrolled) transformMatrix.postTranslate(transX, transY)
        if (invalidated) {
            parent.requestDisallowInterceptTouchEvent(true)
            invalidate()
        }

        return invalidated || super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable != null && !transformMatrix.isIdentity) {
            val matrix = imageMatrix
            matrix.postConcat(transformMatrix)
            matrix.getValues(imageMat)
            imageMat[Matrix.MSCALE_X] = max(imageMat[Matrix.MSCALE_X], matToFit[Matrix.MSCALE_X])
            imageMat[Matrix.MSCALE_Y] = max(imageMat[Matrix.MSCALE_Y], matToFit[Matrix.MSCALE_Y])

            val maxTransX = width - drawable.intrinsicWidth * imageMat[Matrix.MSCALE_X]
            if (abs(maxTransX) < abs(imageMat[Matrix.MTRANS_X]))
                imageMat[Matrix.MTRANS_X] = maxTransX
            else if (sign(maxTransX) * imageMat[Matrix.MTRANS_X] < 0)
                imageMat[Matrix.MTRANS_X] = 0f

            val maxTransY = height - drawable.intrinsicHeight * imageMat[Matrix.MSCALE_Y]
            if (abs(maxTransY) < abs(imageMat[Matrix.MTRANS_Y]))
                imageMat[Matrix.MTRANS_Y] = maxTransY
            else if (sign(maxTransY) * imageMat[Matrix.MTRANS_Y] < 0)
                imageMat[Matrix.MTRANS_Y] = 0f

            matrix.setValues(imageMat)
            imageMatrix = matrix
            transformMatrix.reset()
        }
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewRect[0f, 0f, w.toFloat()] = h.toFloat()
        updateMatrixToFit()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable == null)
            drawableRect.setEmpty()
        else
            drawableRect[0f, 0f, drawable.intrinsicWidth.toFloat()] =
                drawable.intrinsicHeight.toFloat()

        updateMatrixToFit()
    }

    private fun updateMatrixToFit() {
        if (drawableRect.isEmpty || viewRect.isEmpty) {
            matrixToFit.reset()
            transformMatrix.reset()
        } else {
            matrixToFit.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER)
            imageMatrix = matrixToFit
            invalidate()
        }
        matrixToFit.getValues(matToFit)
    }

    override fun setImageMatrix(matrix: Matrix) {
        super.setImageMatrix(matrix)
        matrix.getValues(imageMat)
    }
}
