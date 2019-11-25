package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import com.mktiti.pockethitler.view.card.ArticleUiProvider
import com.mktiti.pockethitler.view.card.DefaultArticleProvider
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageView
import org.jetbrains.anko.textView

class LiberalBoardView(context: Context) : LinearLayout(context) {

    //private val statusText: TextView

    private val articleProvider: ArticleUiProvider = DefaultArticleProvider.forSize(320, 500)

    init {
        orientation = HORIZONTAL

        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 1F)
        gravity = Gravity.CENTER

        imageView {
            imageBitmap = articleProvider.liberalArticle()
        }

        //statusText = textView("0/5")
    }

    fun setState(cardCount: Int) {
        //statusText.text = "$cardCount/5"
    }

}