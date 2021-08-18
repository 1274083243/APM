package com.mobile.apm.widget

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class ScrollGestureListener(private val mTargetView: View, private val mParentView: View) :
    GestureDetector.SimpleOnGestureListener() {
    private var mTotalScrollX = 0f
    private var mTotalScrollY = 0f
    private var mMaxTranslateLeft = 0f
    private var mMaxTranslateRight = 0f
    private var mMaxTranslateTop = 0f
    private var mMaxTranslateBottom = 0f
    private var mHasInit = false
    private var mScaleWidth = 0f
    private var mScaleHeight = 0f
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d("ScrollGestureListener", "onScroll:$distanceX,$distanceY")
        initBorderData()
        calculateScaleWidthAndHeight()
        mTargetView.scaleY
        val innerDistanceX = -distanceX
        val innerDistanceY = -distanceY
        // 往左滑动
        if (innerDistanceX < 0) {
            if (abs(mTotalScrollX + innerDistanceX) <= mMaxTranslateLeft) {
                mTotalScrollX += innerDistanceX
                Log.e(TAG, "没有到达边界:$mTotalScrollX")
            } else {
                mTotalScrollX = -mMaxTranslateLeft
                Log.e(TAG, "到达边界:$mTotalScrollX")
            }
        } else {
            if (mTotalScrollX + innerDistanceX <= mMaxTranslateRight) {
                mTotalScrollX += innerDistanceX
            } else {
                mTotalScrollX = mMaxTranslateRight
            }
        }
        // 往下滑
        if (innerDistanceY < 0) {
            if (abs(mTotalScrollY + innerDistanceY) <= mMaxTranslateBottom) {
                mTotalScrollY += innerDistanceY
            } else {
                mTotalScrollY = -mMaxTranslateBottom
            }
        } else {
            if (mTotalScrollY + innerDistanceY <= mMaxTranslateBottom) {
                mTotalScrollY += innerDistanceY
            } else {
                mTotalScrollY = mMaxTranslateBottom
            }
        }
        mTargetView.translationX = mTotalScrollX
        mTargetView.translationY = mTotalScrollY
        return super.onScroll(e1, e2, innerDistanceX, innerDistanceY)
    }

    private fun calculateScaleWidthAndHeight() {
        mScaleWidth = mTargetView.width * mTargetView.scaleY
        mScaleHeight = mTargetView.height * mTargetView.scaleY
        if (mScaleWidth < mParentView.width) {
            mMaxTranslateLeft = mTargetView.left - (mScaleWidth - mTargetView.width) / 2
            mMaxTranslateRight =
                mParentView.width - mTargetView.right - (mScaleWidth - mTargetView.width) / 2
        }
        if (mScaleHeight < mParentView.height) {
            mMaxTranslateTop = mTargetView.top - (mScaleHeight - mTargetView.height) / 2
            mMaxTranslateBottom =
                mParentView.height - mTargetView.bottom - (mScaleHeight - mTargetView.height) / 2
        }
        if (mScaleWidth > mParentView.width) {
            mMaxTranslateLeft =
                (mScaleWidth - mTargetView.width) / 2 - (mParentView.width - mTargetView.right)
            mMaxTranslateRight = (mScaleWidth - mTargetView.width) / 2 - mTargetView.left
        }
        if (mScaleHeight > mParentView.height) {
            mMaxTranslateTop =
                (mScaleHeight - mTargetView.height) / 2 - (mParentView.height - mTargetView.bottom)
            mMaxTranslateBottom=(mScaleHeight - mTargetView.height) / 2-mTargetView.top
        }

    }

    private fun initBorderData() {
        if (!mHasInit) {
            mHasInit = true
            mMaxTranslateLeft = mTargetView.left.toFloat()
            mMaxTranslateRight = (mParentView.width - mTargetView.right).toFloat()
            mMaxTranslateTop = mTargetView.top.toFloat()
            mMaxTranslateBottom = (mParentView.height - mTargetView.bottom).toFloat()
            Log.d(
                TAG,
                "mMaxTranslateLeft:$mMaxTranslateLeft,mMaxTranslateRight:$mMaxTranslateRight," +
                        "mMaxTranslateTop:$mMaxTranslateTop,mMaxTranslateBottom:$mMaxTranslateBottom"
            )
        }
    }

    companion object {
        const val TAG = "ScrollGestureListener"
    }
}