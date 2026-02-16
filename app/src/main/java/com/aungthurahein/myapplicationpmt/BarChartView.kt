package com.aungthurahein.myapplicationpmt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<Pair<String, Int>> = emptyList()
    private var maxValue: Int = 0

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.pomodoro_primary)
        style = Paint.Style.FILL
    }

    private val barEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFF0F0F0.toInt()
        style = Paint.Style.FILL
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_secondary)
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_primary)
        textSize = 26f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val barRadius = 12f

    fun setData(newData: List<Pair<String, Int>>) {
        data = newData
        maxValue = newData.maxOfOrNull { it.second } ?: 0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val bottomPadding = 48f
        val topPadding = 36f
        val barAreaHeight = h - bottomPadding - topPadding

        val barCount = data.size
        val totalSpacing = w * 0.3f
        val barWidth = (w - totalSpacing) / barCount
        val spacing = totalSpacing / (barCount + 1)

        for (i in data.indices) {
            val (label, value) = data[i]
            val left = spacing + i * (barWidth + spacing)
            val right = left + barWidth
            val centerX = left + barWidth / 2

            val bgRect = RectF(left, topPadding, right, h - bottomPadding)
            canvas.drawRoundRect(bgRect, barRadius, barRadius, barEmptyPaint)

            if (maxValue > 0 && value > 0) {
                val ratio = value.toFloat() / maxValue
                val barHeight = barAreaHeight * ratio
                val barTop = h - bottomPadding - barHeight
                val barRect = RectF(left, barTop, right, h - bottomPadding)
                canvas.drawRoundRect(barRect, barRadius, barRadius, barPaint)

                canvas.drawText(
                    "${value}m",
                    centerX,
                    barTop - 8f,
                    valuePaint
                )
            }

            canvas.drawText(label, centerX, h - 12f, labelPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 500
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeight.coerceAtMost(heightSize)
            else -> desiredHeight
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY))
    }
}
