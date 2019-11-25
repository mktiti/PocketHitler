package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class ChancellorDiscardFragment(
    private val state: PhaseState.ChancellorDiscardState,
    private val resultCallback: (PhaseResult.ChancellorDiscardResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView("Select article to discard")

            linearLayout {
                state.cards.apply {
                    article(first, second)
                    article(second, first)
                }
            }
        }
    }.view

    private fun ViewManager.article(article: Article, other: Article) {
        button(if (article == Article.LIBERAL) "Liberal" else "Fascist").setOnClickListener {
            resultCallback(PhaseResult.ChancellorDiscardResult(other, other))
        }
    }

}