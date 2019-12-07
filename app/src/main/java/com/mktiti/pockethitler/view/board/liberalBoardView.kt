package com.mktiti.pockethitler.view.board

import android.content.Context
import com.mktiti.pockethitler.view.card.ArticleUiProvider

class LiberalBoardView(context: Context) : BoardViewBase(5, ArticleUiProvider::liberalArticle, { _, last -> liberalHolder(last) },  context)