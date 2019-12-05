package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.game.manager.FascistBoard
import com.mktiti.pockethitler.game.manager.PlayerCount
import com.mktiti.pockethitler.view.card.ArticleHolderUiProvider
import com.mktiti.pockethitler.view.card.ArticleUiProvider
import com.mktiti.pockethitler.view.card.DefaultArticleHolderProvider
import com.mktiti.pockethitler.view.card.DefaultArticleProvider
import org.jetbrains.anko.imageView
import org.jetbrains.anko.verticalLayout

class FascistBoardView(context: Context) : LinearLayout(context) {

    private val articleProvider: ArticleUiProvider = DefaultArticleProvider.forSize(320, 500)
    private val holderProvider: ArticleHolderUiProvider = DefaultArticleHolderProvider.forSize(320, 500)

    private inner class CardHolder(
        private val view: ImageView,
        private val last: Boolean = false
    ) {
        init {
            noCard(null)
        }

        fun noCard(presidentialAction: PresidentialAction?) {
            view.setImageBitmap(holderProvider.fascistHolder(presidentialAction, last))
        }

        fun addArticle() {
            view.setImageBitmap(articleProvider.fascistArticle())
        }
    }

    private val holders: List<CardHolder>

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 1F)
        gravity = Gravity.CENTER
        val cardCount = FascistBoard.actions(PlayerCount.MANY).size
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

    fun setBoard(fascistPowers: List<PresidentialAction?>) {
        holders.zip(fascistPowers).forEach { (holder, action) ->
            holder.noCard(action)
        }
    }

    fun setState(cardCount: Int) {
        holders.take(cardCount).forEach {
            it.addArticle()
        }
    }

}