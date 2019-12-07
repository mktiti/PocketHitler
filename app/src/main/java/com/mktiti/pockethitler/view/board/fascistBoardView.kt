package com.mktiti.pockethitler.view.board

import android.content.Context
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.view.card.ArticleHolderUiProvider
import com.mktiti.pockethitler.view.card.ArticleUiProvider

class FascistBoardView(context: Context) : BoardViewBase(6, ArticleUiProvider::fascistArticle, ArticleHolderUiProvider::fascistHolder, context) {

    fun setBoard(fascistPowers: List<PresidentialAction?>) {
        holders.zip(fascistPowers).forEach { (holder, action) ->
            holder.noCard(action)
        }
    }

}