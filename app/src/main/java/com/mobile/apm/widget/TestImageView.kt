package com.mobile.apm.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class TestImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val mMatrix=Matrix()
    private val mPaint=Paint().apply {
        color=Color.RED
        style=Paint.Style.FILL
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mMatrix.setTranslate(100.0f,100.0f)
        canvas.setMatrix(matrix)
        canvas.translate(100f,100f)
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(),50f,mPaint)
    }
}