package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.Party.FASCIST
import com.mktiti.pockethitler.game.data.Party.LIBERAL
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.util.DefaultResourceManager
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class CheckPartyViewFragment(
    private val state: PhaseState.PresidentialPowerUseState.CheckPartyViewState,
    private val resultCallback: (PhaseResult.PresidentialPowerDone) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val resourceManager = DefaultResourceManager(resources)

        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView(resourceManager.format(R.string.is_a, state.player.name))
            textView(
                when (state.player.identity.party) {
                    LIBERAL -> R.string.liberal
                    FASCIST -> R.string.fascist
                }
            )

            button(R.string.ok).setOnClickListener {
                resultCallback(PhaseResult.PresidentialPowerDone)
            }
        }
    }.view

}