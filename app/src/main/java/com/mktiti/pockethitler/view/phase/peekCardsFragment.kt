package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class PeekCardsFragment(
    private val state: PhaseState.PresidentialPowerUseState.PeekCardsState,
    private val resultCallback: (PhaseResult.PresidentialPowerDone) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView(R.string.next_3_articles)

            state.cards.apply {
                article(first)
                article(second)
                article(third)
            }

            button(R.string.ok).setOnClickListener {
                resultCallback(PhaseResult.PresidentialPowerDone)
            }
        }
    }.view

    private fun ViewManager.article(article: Article) {
        textView(
            when (article) {
                Article.LIBERAL -> R.string.liberal
                Article.FASCIST -> R.string.fascist
            }
        )
    }

}