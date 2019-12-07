package com.mktiti.pockethitler.view.board

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.util.DefaultResourceManager
import com.mktiti.pockethitler.view.card.ArticleHolderUiProvider
import com.mktiti.pockethitler.view.card.ArticleUiProvider
import com.mktiti.pockethitler.view.card.DefaultArticleHolderProvider
import com.mktiti.pockethitler.view.card.DefaultArticleProvider
import org.jetbrains.anko.imageView
import org.jetbrains.anko.verticalLayout

abstract class BoardViewBase(
    private val cardCount: Int,
    private val cardSelector: ArticleUiProvider.() -> Bitmap,
    private val cardHolderSelector: ArticleHolderUiProvider.(PresidentialAction?, Boolean) -> Bitmap,
    context: Context) : LinearLayout(context) {

    private val articleProvider: ArticleUiProvider = DefaultArticleProvider.forSize(320, 500) { DefaultResourceManager(resources) }
    private val holderProvider: ArticleHolderUiProvider = DefaultArticleHolderProvider.forSize(320, 500) { DefaultResourceManager(resources) }

    protected inner class CardHolder(
        private val view: ImageView,
        private val last: Boolean = false
    ) {
        init {
            noCard(null)
        }

        fun noCard(action: PresidentialAction?) {
            view.setImageBitmap(holderProvider.cardHolderSelector(action, last))
        }

        fun addArticle() {
            view.setImageBitmap(articleProvider.cardSelector())
        }
    }

    protected val holders: List<CardHolder>

    init {
        orientation = HORIZONTAL

        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 1F)
        gravity = Gravity.CENTER
        weightSum = cardCount.toFloat()

        holders = ArrayList<CardHolder>(cardCount).apply {
            repeat(cardCount) { i ->
                verticalLayout {
                    gravity = Gravity.CENTER
                    val imageView = imageView {
                        setPadding(10, 0, 10, 0)
                    }
                    add(CardHolder(imageView, i == cardCount - 1))
                    layoutParams = generateDefaultLayoutParams().apply {
                        weight = 1F
                    }
                }
            }
        }
    }

    fun setState(cardCount: Int) {
        holders.take(cardCount).forEach {
            it.addArticle()
        }
    }

}