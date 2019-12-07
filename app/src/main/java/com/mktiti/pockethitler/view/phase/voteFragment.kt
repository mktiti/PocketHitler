package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import com.mktiti.pockethitler.game.data.PhaseState.VoteState
import com.mktiti.pockethitler.util.DefaultResourceManager
import com.mktiti.pockethitler.util.ResourceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class VoteFragment(
    private val state: VoteState,
    private val resultCallback: (PhaseResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val resourceManager = DefaultResourceManager(resources)

        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView(resourceManager.format(R.string.voting, state.nextVoter().name)) {
                textSize += 1

                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            textView(resourceManager.format(R.string.pres_candidate_is, state.candidates.president.name)) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            textView(resourceManager.format(R.string.chancellor_candidate_is, state.candidates.chancellor.name)) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            linearLayout {
                button(R.string.ja).setOnClickListener { vote(resourceManager, true) }
                button(R.string.nein).setOnClickListener { vote(resourceManager, false) }

                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams(matchParent, wrapContent)
        }
    }.view

    private fun vote(resourceManager: ResourceManager, vote: Boolean) {
        val voteState = state.addVote(vote)
        val result = voteState.result()
        return if (result == null) {
            resultCallback(PhaseResult.PlainStateResult(EnvelopeState(resourceManager.format(R.string.env_general, state.nextVoter().name), voteState)))
        } else {
            resultCallback(result)
        }
    }

}