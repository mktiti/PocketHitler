package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseResult.PlainStateResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
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

            textView("Next 3 cards")

            state.cards.apply {
                article(first)
                article(second)
                article(third)
            }

            button("OK").setOnClickListener {
                resultCallback(PhaseResult.PresidentialPowerDone)
            }
        }
    }.view

    private fun ViewManager.article(article: Article) {
        textView(if (article == Article.LIBERAL) "Liberal" else "Fascist")
    }

}