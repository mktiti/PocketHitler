package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.Bi
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.view.card.ArticleUiProvider
import com.mktiti.pockethitler.view.card.DefaultArticleProvider
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class PresidentDiscardFragment(
    private val state: PhaseState.PresidentDiscardState,
    private val resultCallback: (PhaseResult.PresidentDiscardResult) -> Unit
) : Fragment() {

    companion object {
        private val articleProvider: ArticleUiProvider = DefaultArticleProvider.forSize(320, 500)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            lparams(matchParent, matchParent)

            textView("Select article to discard") {
                textSize = 20F
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, 50, 0, 50)
            }

            linearLayout {
                gravity = Gravity.CENTER_HORIZONTAL
                state.cards.apply {
                    article(first, Bi(second, third))
                    article(second, Bi(first, third))
                    article(third, Bi(first, second))
                }
            }
        }
    }.view

    private fun ViewManager.article(article: Article, others: Bi<Article>) {
        imageView {
            imageBitmap = articleProvider.article(article)
        }.setOnClickListener {
            resultCallback(PhaseResult.PresidentDiscardResult(others, article))
        }
    }

}