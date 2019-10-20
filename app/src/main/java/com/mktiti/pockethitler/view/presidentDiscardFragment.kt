package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.Bi
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseResult.PlainStateResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class PresidentDiscardFragment(
    private val state: PhaseState.PresidentDiscardState,
    private val resultCallback: (PhaseResult.PresidentDiscardResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView("Select article to discard")

            linearLayout {
                state.cards.apply {
                    article(first, Bi(second, third))
                    article(second, Bi(first, third))
                    article(third, Bi(first, second))
                }
            }
        }
    }.view

    private fun ViewManager.article(article: Article, others: Bi<Article>) {
        button(if (article == Article.LIBERAL) "Liberal" else "Fascist").setOnClickListener {
            resultCallback(PhaseResult.PresidentDiscardResult(others, article))
        }
    }

}