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

class CheckPartySelectFragment(
    private val state: PhaseState.PresidentialPowerUseState.CheckPartySelectState,
    private val resultCallback: (PhaseResult.CheckPartySelected) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        linearLayout {
            ankoView(factory = {
                PlayerSelectView(
                    message = "Select your player to inspect.",
                    subtitle = "Selectable players:",
                    allowedPlayers = state.selectablePlayers,
                    selectCallback = { resultCallback(PhaseResult.CheckPartySelected(it)) }
                ).createView(ui = AnkoContext.Companion.create(ctx, this))
            }, theme = 0) {}
        }
    }.view

}