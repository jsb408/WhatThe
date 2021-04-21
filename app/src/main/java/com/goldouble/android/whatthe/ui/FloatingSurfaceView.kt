package com.goldouble.android.whatthe.ui

import android.content.Context
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class FloatingSurfaceView(context: Context): FrameLayout(context) {
    val surfaceView = SurfaceView(context).apply {
        holder.addCallback(SurfaceViewHolder(context))
    }

    //크기 설정
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(0, 0)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        addView(surfaceView)
    }

    override fun addView(child: View?) {
        child?.parent?.let { (it as ViewGroup).removeView(child) }
        super.addView(child)
    }
}