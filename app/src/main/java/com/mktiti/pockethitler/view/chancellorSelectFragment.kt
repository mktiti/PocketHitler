package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI

class ChancellorSelectFragment(
    private val state: PhaseState.ChancellorSelectState,
    private val stateChangeCallback: (PhaseResult.CandidatesSelected) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        linearLayout {
            ankoView(factory = {
                PlayerSelectView(
                    message = "Select your Chancellor candidate.",
                    subtitle = "Allowed nominees:",
                    allowedPlayers = state.selectablePlayers,
                    selectCallback = { stateChangeCallback(PhaseResult.CandidatesSelected(it)) }
                ).createView(ui = AnkoContext.Companion.create(ctx, this))
            }, theme = 0) {}
        }
    }.view

}