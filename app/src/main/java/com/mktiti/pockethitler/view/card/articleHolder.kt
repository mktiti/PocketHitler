package com.mktiti.pockethitler.view.card

import android.graphics.Bitmap
import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.game.PresidentialAction.*
import com.mktiti.pockethitler.util.ListLruCache
import com.mktiti.pockethitler.util.LruCache
import com.mktiti.pockethitler.util.ResourceManager
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.fashPaint
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.libPaint
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.maxTextSize
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.whitePaint
import com.mktiti.pockethitler.view.card.DefaultArticleHelper.writeToCanvasMiddle

interface ArticleHolderUiProvider {

    fun liberalHolder(last: Boolean): Bitmap

    fun fascistHolder(presidentialAction: PresidentialAction?, last: Boolean): Bitmap

}

class DefaultArticleHolderProvider(
    private val width: Int,
    private val height: Int,
    private val resourceManager: ResourceManager
) : ArticleHolderUiProvider {

    companion object {

        private val cache: LruCache<Pair<Int, Int>, ArticleHolderUiProvider> = ListLruCache()

        fun forSize(width: Int, height: Int, resourceManager: () -> ResourceManager) = cache.getOrCreate(width to height) {
            DefaultArticleHolderProvider(it.first, it.second, resourceManager())
        }

    }

    private val victoryText = resourceManager[R.string.victory]
    private val allTexts =  PresidentialAction.values().map(this::actionTitle) + victoryText

    private fun actionTitle(action: PresidentialAction): String {
        val key = when (action) {
            CHECK_PARTY -> R.string.pres_act_investigate
            SNAP_ELECTION -> R.string.pres_act_snap
            PEEK_NEXT -> R.string.pres_act_peek
            KILL -> R.string.pres_act_kill
        }
        return resourceManager[key]
    }

    private val widthF = width.toFloat()
    private val heightF = height.toFloat()

    private val borderWidth: Float = 0.1F * width

    private val borderLines = floatArrayOf(
        0F, 0F,
        widthF, 0F,
        widthF, borderWidth,
        widthF, heightF,
        widthF - borderWidth, heightF,
        0F, heightF,
        0F, heightF - borderWidth,
        0F, borderWidth
    )

    private val fontSize: Float = maxTextSize(0.7F * width, allTexts)

    private val libHolder: Bitmap = createHolder("", libPaint)

    private val libEndHolder: Bitmap = createHolder(victoryText, libPaint, inverted = true)

    private val fashHolder: Bitmap = createHolder("", fashPaint)

    private val fashHolderMap: Map<PresidentialAction, Bitmap> = PresidentialAction.values().mapIndexed { i, action ->
        action to createHolder(actionTitle(action), fashPaint, inverted = i >= 3)
    }.toMap()

    private val fashEndHolder: Bitmap = createHolder(victoryText, fashPaint, inverted = true)

    private fun createHolder(text: String, paint: Paint, inverted: Boolean = false) = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
        val borderPaint = Paint(paint).apply {
            pathEffect = DashPathEffect(floatArrayOf(borderWidth, borderWidth), 0F)
            strokeWidth = 2F * borderWidth
        }

        drawRect(0F, 0F, widthF, heightF, whitePaint)
        drawLines(borderLines, borderPaint)

        val (bgPaint, textPaint, borderWidth: Float) = if (inverted) {
            drawRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth, whitePaint)
            Triple(paint, whitePaint, borderWidth * 2F)
        } else {
            Triple(whitePaint, paint, borderWidth)
        }

        drawRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth, bgPaint)
        writeToCanvasMiddle(text, this, fontSize, textPaint)
    }

    override fun liberalHolder(last: Boolean) = if (last) libEndHolder else libHolder

    override fun fascistHolder(presidentialAction: PresidentialAction?, last: Boolean) = if (last) {
        fashEndHolder
    } else {
        fashHolderMap[presidentialAction] ?: fashHolder
    }

}