package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseResult.PlainStateResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class VetoConfirmFragment(
    private val state: PhaseState.VetoConfirmState,
    private val resultCallback: (PhaseResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView("Confirm veto?")
            linearLayout {
                button("Accept Veto\n(discard both cards)").setOnClickListener {
                    resultCallback(PhaseResult.Veto(state.cards))
                }
                button("Reject Veto\n(force Chancellor to choose)").setOnClickListener {
                    resultCallback(PlainStateResult(EnvelopeState("Chancellor", PhaseState.ChancellorDiscardState(state.cards, false))))
                }
            }
        }
    }.view

}