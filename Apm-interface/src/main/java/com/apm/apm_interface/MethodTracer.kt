package com.apm.apm_interface

import android.util.Log

/**
 * 耗时方法检测类
 */
object MethodTracer {
    const val TAG="MethodTracer"
    private var mStartTime=0L
    private val mMap= HashMap<String,Long>()
    @JvmStatic
    fun recordMethodStart(methodName:String){
        mMap[methodName] = System.currentTimeMillis()
    }
    @JvmStatic
    fun recordMethodEnd(methodName:String){
        val mLastTime = mMap.remove(methodName)
        mLastTime?.let {
            Log.d(TAG,"方法耗时:${methodName}:${System.currentTimeMillis()- mLastTime}")
        }
    }

}