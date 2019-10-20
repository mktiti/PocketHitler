package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import com.mktiti.pockethitler.game.data.PhaseState.VoteState
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class VoteFragment(
    private val state: VoteState,
    private val stateChangeCallback: (PhaseState) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val result = state.result()
        if (result == null) {
            linearLayout {
                gravity = Gravity.CENTER
                lparams(matchParent, matchParent)

                textView("Voting (${state.nextVoter().name})")
                textView("President candidate: ${state.candidates.president}")
                textView("Chancellor candidate: ${state.candidates.chancellor}")

                verticalLayout {
                    button("Ja").setOnClickListener { vote(true) }
                    button("Nein").setOnClickListener { vote(false) }
                }
            }
        } else {
            linearLayout {
                gravity = Gravity.CENTER
                lparams(matchParent, matchParent)

                textView("Vote result")
                textView("President candidate: ${state.candidates.president}")
                textView("Chancellor candidate: ${state.candidates.chancellor}")

                textView(if (result) "Success!" else "Fail!" )

                button("Continue").setOnClickListener {
                    val newState = if (result) {
                        TODO()
                        //PresidentDiscardState()
                    } else {
                        TODO()
                    }
                }
            }
        }
    }.view

    private fun vote(vote: Boolean) {
        val voteState = state.addVote(vote)
        if (voteState.result() == null) {
            stateChangeCallback(EnvelopeState("Only for ${voteState.nextVoter().name}", voteState))
        } else {
            stateChangeCallback(EnvelopeState("Show result", voteState))
        }
    }

}