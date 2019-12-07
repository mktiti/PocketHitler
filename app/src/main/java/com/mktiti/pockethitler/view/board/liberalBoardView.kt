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

class LiberalBoardView(context: Context) : BoardViewBase(5, ArticleUiProvider::liberalArticle, { _, last -> liberalHolder(last) },  context)