package com.mktiti.pockethitler.view.card

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

object DefaultArticleHelper {

    val whitePaint = Paint().apply { color = Color.WHITE }

    val libPaint = Paint().apply { color = Color.rgb(96, 140, 169) }

    val fashPaint = Paint().apply { color = Color.rgb(195, 101, 99) }

    private val calcPaint = Paint().apply { textSize = 48F }

    fun maxTextSize(width: Float, texts: Iterable<String>): Float {
        return width / texts.map { text ->
            Rect().apply { calcPaint.getTextBounds(text, 0, text.length, this) }.width()
        }.max()!! * calcPaint.textSize
    }

    fun writeToCanvasMiddle(text: String, canvas: Canvas, fontSize: Float, basePaint: Paint) {
        Paint(basePaint).apply {
            textSize = fontSize
            val bounds = Rect().apply { getTextBounds(text, 0, text.length, this) }
            val width = bounds.width()
            val height = bounds.height()

            canvas.drawText(text, (canvas.width - width) / 2F, (canvas.height - height) / 2F + height, this)
        }
    }
}