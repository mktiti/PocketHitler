package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.view.PlayerSelectView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI

class ToKillSelectFragment(
    private val state: PhaseState.PresidentialPowerUseState.KillState,
    private val stateChangeCallback: (PhaseResult.KillTargetSelected) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        linearLayout {
            ankoView(factory = {
                PlayerSelectView(
                    message = "Select player to kill.",
                    subtitle = "Allowed selectablePlayers:",
                    allowedPlayers = state.selectablePlayers,
                    selectCallback = { stateChangeCallback(PhaseResult.KillTargetSelected(it)) }
                ).createView(ui = AnkoContext.Companion.create(ctx, this))
            }, theme = 0) {}
        }
    }.view

}