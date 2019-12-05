package com.mktiti.pockethitler.view.card

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.applyCanvas
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.util.ListLruCache
import com.mktiti.pockethitler.util.LruCache
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.fashPaint
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.libPaint
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.maxTextSize
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.whitePaint
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.writeToCanvasMiddle

interface ArticleUiProvider {

    fun article(article: Article): Bitmap = when (article) {
        Article.LIBERAL -> liberalArticle()
        Article.FASCIST -> fascistArticle()
    }

    fun liberalArticle(): Bitmap

    fun fascistArticle(): Bitmap

}

class DefaultArticleProvider(private val width: Int, private val height: Int) : ArticleUiProvider {

    companion object {

        private const val libText = "Liberal"
        private const val fashText = "Fascist"
        private val allTexts = listOf(libText, fashText)

        private val cache: LruCache<Pair<Int, Int>, ArticleUiProvider> = ListLruCache()

        fun forSize(width: Int, height: Int) = cache.getOrCreate(width to height, ::DefaultArticleProvider)

    }

    constructor(size: Pair<Int, Int>) : this(size.first, size.second)

    private val textSize: Float = maxTextSize(0.5F * width, allTexts)

    private val borderWidth = 0.15F * width

    private val libArticle: Bitmap = createArticle(libText, libPaint)

    private val fashArticle: Bitmap = createArticle(fashText, fashPaint)

    private fun createArticle(text: String, paint: Paint) = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
        drawRect(RectF(0F, 0F, width.toFloat(), height.toFloat()), paint)
        drawRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth, whitePaint)

        writeToCanvasMiddle(text, this, textSize, paint)
    }

    override fun liberalArticle(): Bitmap = libArticle

    override fun fascistArticle(): Bitmap = fashArticle

}