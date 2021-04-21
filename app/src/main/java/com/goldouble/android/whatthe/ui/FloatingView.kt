package com.goldouble.android.whatthe.ui

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.goldouble.android.whatthe.R

class FloatingView(context: Context, private val windowManager: WindowManager, private val windowLayoutParams: WindowManager.LayoutParams, val tapListener: () -> Unit)
    : FrameLayout(context) {
    private val background = ImageView(context).apply {
        setImageResource(R.drawable.floating_button_background)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    var startX = 0f
    var startY = 0f
    var widgetStartX = 0
    var widgetStartY = 0

    var isTouch = false

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        addView(background)

        setOnTouchListener { view, event ->
            view.performClick()
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isTouch = true
                    startX = event.rawX
                    startY = event.rawY

                    widgetStartX = windowLayoutParams.x
                    widgetStartY = windowLayoutParams.y
                }
                MotionEvent.ACTION_MOVE -> {
                    isTouch = false
                    val deltaX = startX - event.rawX
                    val deltaY = startY - event.rawY

                    windowLayoutParams.x = widgetStartX - deltaX.toInt()
                    windowLayoutParams.y = widgetStartY - deltaY.toInt()
                    windowManager.updateViewLayout(this, windowLayoutParams)
                }
                MotionEvent.ACTION_UP -> {
                    if (isTouch) {
                        Log.e("FLOAT", "TOUCH")
                        tapListener()
                    }
                }
            }
            false
        }
    }

    override fun addView(child: View?) {
        child?.parent?.let { (it as ViewGroup).removeView(child) }
        super.addView(child)
    }

    fun show() {
        windowManager.addView(this, windowLayoutParams)
    }
}