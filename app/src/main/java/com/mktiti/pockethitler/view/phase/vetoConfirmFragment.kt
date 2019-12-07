package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseResult.PlainStateResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import com.mktiti.pockethitler.util.DefaultResourceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class VetoConfirmFragment(
    private val state: PhaseState.VetoConfirmState,
    private val resultCallback: (PhaseResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val resourceManager = DefaultResourceManager(resources)

        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView(R.string.veto_confirm_title)
            linearLayout {
                button(R.string.accept_veto).setOnClickListener {
                    resultCallback(PhaseResult.Veto(state.cards))
                }
                button(R.string.reject_veto).setOnClickListener {
                    resultCallback(PlainStateResult(EnvelopeState(resourceManager.format(R.string.env_for_chancellor, "XY"), PhaseState.ChancellorDiscardState(state.cards, false))))
                }
            }
        }
    }.view

}