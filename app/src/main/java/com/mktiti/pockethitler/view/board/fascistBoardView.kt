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

class FascistBoardView(context: Context) : BoardViewBase(6, ArticleUiProvider::fascistArticle, ArticleHolderUiProvider::fascistHolder, context) {

    fun setBoard(fascistPowers: List<PresidentialAction?>) {
        holders.zip(fascistPowers).forEach { (holder, action) ->
            holder.noCard(action)
        }
    }

}