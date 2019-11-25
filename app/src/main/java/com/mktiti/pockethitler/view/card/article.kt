package com.mktiti.pockethitler.view.card

import android.graphics.*
import androidx.core.graphics.applyCanvas
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.game.data.Article

interface ArticleHolderUiProvider {

    fun liberalHolder(last: Boolean): Bitmap

    fun fascistHolder(presidentialAction: PresidentialAction?, last: Boolean): Bitmap

}

interface ArticleUiProvider {

    fun article(article: Article): Bitmap = when (article) {
        Article.LIBERAL -> liberalArticle()
        Article.FASCIST -> fascistArticle()
    }

    fun liberalArticle(): Bitmap

    fun fascistArticle(): Bitmap

}

fun writeToCanvasMiddle(text: String, canvas: Canvas, ratio: Float, basePaint: Paint) {
    val desireWidth = canvas.width * ratio

    Paint(basePaint).apply {
        textSize = 48F
        val bounds = Rect()
        getTextBounds(text, 0, text.length, bounds)

        textSize *= desireWidth / bounds.width()

        val textHeight = desireWidth / bounds.width() * bounds.height()

        canvas.drawText(text, (canvas.width - desireWidth) / 2F, (canvas.height - textHeight) / 2F + textHeight, this)
    }
}

class DefaultArticleProvider(private val width: Int, private val height: Int) : ArticleUiProvider {

    companion object {

        private val map = mutableMapOf<Pair<Int, Int>, ArticleUiProvider>()

        fun forSize(width: Int, height: Int) = map.computeIfAbsent(width to height, ::DefaultArticleProvider)

    }

    constructor(size: Pair<Int, Int>) : this(size.first, size.second)

    private val borderWidth = 0.1F * width

    private val whitePaint = Paint().apply { color = Color.WHITE }

    private val libArticle: Bitmap by lazy {
        createArticle("Liberal", Color.rgb(96, 140, 169))
    }

    private val fashArticle: Bitmap by lazy {
        createArticle("Fascist", Color.rgb(195, 101, 99))
    }

    private fun createArticle(text: String, color: Int) = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
        val paint = Paint().apply { this.color = color }

        drawRect(RectF(0F, 0F, width.toFloat(), height.toFloat()), paint)
        drawRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth, whitePaint)

        writeToCanvasMiddle(text, this, 0.5F, paint)
    }

    override fun liberalArticle(): Bitmap = libArticle

    override fun fascistArticle(): Bitmap = fashArticle

}