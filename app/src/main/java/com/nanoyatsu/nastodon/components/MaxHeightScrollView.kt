package com.nanoyatsu.nastodon.components

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.nanoyatsu.nastodon.R

/**
 * 最大幅を指定できる ScrollView ↓を参考
 * https://stackoverflow.com/questions/17683789/android-scrollview-set-height-for-displayed-content
 */
class MaxHeightScrollView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    private var maxHeight = 0

    init {
        if (context is Context && attrs is AttributeSet) {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView)
            maxHeight = styledAttrs
                .getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, 200)
            styledAttrs.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val suppressedHeight = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, suppressedHeight)
    }
}