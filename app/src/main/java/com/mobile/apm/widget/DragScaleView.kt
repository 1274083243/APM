package com.mobile.apm.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout

class DragScaleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), ScaleGestureDetector.OnScaleGestureListener {
    lateinit var mChildView: View
    private var mLastScaleNum = 1f
    private var mCurrentScaleNum = 1f
    private val mScaleDetector = ScaleGestureDetector(context, this)
    private lateinit var mScrollGestureListener: ScrollGestureListener
    private lateinit var mScrollDetector: GestureDetector
    private var mIsScaleEnd = true
    override fun onFinishInflate() {
        super.onFinishInflate()
        mChildView = getChildAt(0)
        mScrollGestureListener = ScrollGestureListener(mChildView,this)
        mScrollDetector = GestureDetector(context, mScrollGestureListener)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor
        Log.d(TAG, "缩放因子参数:$scaleFactor")
        mCurrentScaleNum = scaleFactor * mLastScaleNum
        mChildView.scaleX = mCurrentScaleNum
        mChildView.scaleY = mCurrentScaleNum
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        mLastScaleNum = mCurrentScaleNum
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 1 && mIsScaleEnd) {
            mScrollDetector.onTouchEvent(event)
        } else if (event.pointerCount == 2 || !mIsScaleEnd) {
            mIsScaleEnd = event.actionMasked == MotionEvent.ACTION_UP
            mScaleDetector.onTouchEvent(event)
        }
        Log.d(TAG,"mIsScaleEnd:$mIsScaleEnd")
        return true
    }

    companion object {
        const val TAG = "ike"
    }
}