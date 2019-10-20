package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import com.mktiti.pockethitler.game.data.PhaseState.VoteState
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class VoteFragment(
    private val state: VoteState,
    private val resultCallback: (PhaseResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView("Voting (${state.nextVoter().name})")
            textView("President candidate: ${state.candidates.president.name}")
            textView("Chancellor candidate: ${state.candidates.chancellor.name}")

            linearLayout {
                button("Ja").setOnClickListener { vote(true) }
                button("Nein").setOnClickListener { vote(false) }
            }
        }
    }.view

    private fun vote(vote: Boolean) {
        val voteState = state.addVote(vote)
        val result = voteState.result()
        return if (result == null) {
            resultCallback(PhaseResult.PlainStateResult(EnvelopeState("Only for ${voteState.nextVoter().name}", voteState)))
        } else {
            resultCallback(result)
        }
    }

}