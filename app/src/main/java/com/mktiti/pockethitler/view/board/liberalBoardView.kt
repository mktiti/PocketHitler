package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import com.mktiti.pockethitler.view.card.ArticleHolderUiProvider
import com.mktiti.pockethitler.view.card.ArticleUiProvider
import com.mktiti.pockethitler.view.card.DefaultArticleHolderProvider
import com.mktiti.pockethitler.view.card.DefaultArticleProvider
import org.jetbrains.anko.imageView
import org.jetbrains.anko.verticalLayout

class LiberalBoardView(context: Context) : LinearLayout(context) {

    private val articleProvider: ArticleUiProvider = DefaultArticleProvider.forSize(320, 500)
    private val holderProvider: ArticleHolderUiProvider = DefaultArticleHolderProvider.forSize(320, 500)

    private inner class CardHolder(
        private val view: ImageView,
        private val last: Boolean = false
    ) {
        init {
            noCard()
        }

        fun noCard() {
            view.setImageBitmap(holderProvider.liberalHolder(last))
        }

        fun addArticle() {
            view.setImageBitmap(articleProvider.liberalArticle())
        }
    }

    private val holders: List<CardHolder>

    init {
        orientation = HORIZONTAL

        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 1F)
        gravity = Gravity.CENTER
        val cardCount = 5
        weightSum = cardCount.toFloat()

        holders = ArrayList<CardHolder>(cardCount).apply {
            repeat(cardCount) { i ->
                verticalLayout {
                    gravity = Gravity.CENTER
                    add(CardHolder(imageView(), i == cardCount - 1))
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