package com.mobile.apm.widget

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.*
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

    private lateinit var mScaleMatrix: Matrix
    private var mMatrixValues = FloatArray(9)
    override fun onFinishInflate() {
        super.onFinishInflate()
        mChildView = getChildAt(0)
        mScrollGestureListener = ScrollGestureListener(mChildView, this)
        mScrollDetector = GestureDetector(context, mScrollGestureListener)
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                initScaleMatrix()
                mScaleMatrix.postScale(2.0f, 2.0f)
                mScaleMatrix.getValues(mMatrixValues)
                val fl = mMatrixValues[Matrix.MSCALE_X]
                Log.d(TAG, "当前的缩放比例:$fl")
                mChildView.invalidate()

            }

        })
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor
        mCurrentScaleNum = scaleFactor * mLastScaleNum
        if (mCurrentScaleNum < MIN_SCALE) {
            mCurrentScaleNum = MIN_SCALE
        } else if (mCurrentScaleNum > MAX_SCALE) {
            mCurrentScaleNum = MAX_SCALE
        }
        Log.d(TAG, "缩放因子参数:$scaleFactor，$mCurrentScaleNum")
//        mChildView.scaleX = mCurrentScaleNum
        mChildView.scaleY = mCurrentScaleNum
        mScaleMatrix.postScale(2.0f, 2.0f)
        mChildView.matrix.set(mScaleMatrix)
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {

        return true
    }

    fun initScaleMatrix() {
        if (!this::mScaleMatrix.isInitialized) {
            mScaleMatrix = mChildView.matrix
        }
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
//        Log.d(TAG, "mIsScaleEnd:$mIsScaleEnd")
        return true
    }

    companion object {
        const val TAG = "DragScaleView"
        const val MIN_SCALE = 1f
        const val MAX_SCALE = 2f
    }
}