package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.Player
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class SelectFragmentBase(
    private val title: String,
    private val subtitle: String?,
    private val options: List<Player>,
    private val confirmMessageProvider: ((String) -> String)?,
    private val selectCallback: (Player) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            lparams(matchParent, matchParent)

            textView(title) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }.lparams(width = matchParent, height = wrapContent)

            subtitle?.apply {
                textView(this) {
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }.lparams(width = matchParent, height = wrapContent)
            }

            gridLayout {
                columnCount = 3

                options.forEach { player ->
                    button(player.name).setOnClickListener {
                        selectCallback(player)
                    }
                }

            }.lparams(wrapContent, wrapContent)
        }

        /*
        linearLayout {
            verticalLayout {
                textView(title) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                textView(subtitle) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                recyclerView {
                    layoutManager = LinearLayoutManager(context)
                    adapter = PlayerAdapter(options, selectCallback)
                }
            }
        }
         */
    }.view

}

fun chancellorSelectFrag(
    state: PhaseState.ChancellorSelectState,
    stateChangeCallback: (PhaseResult.CandidatesSelected) -> Unit
) = SelectFragmentBase(
    title = "Select your Chancellor candidate.",
    subtitle = "Allowed nominees:",
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { stateChangeCallback(PhaseResult.CandidatesSelected(it)) }
)

fun checkPartySelectFrag(
    state: PhaseState.PresidentialPowerUseState.CheckPartySelectState,
    resultCallback: (PhaseResult.CheckPartySelected) -> Unit
) = SelectFragmentBase(
    title = "Select your player to inspect.",
    subtitle = "Selectable players:",
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { resultCallback(PhaseResult.CheckPartySelected(it)) }
)

fun snapSelectFrag(
    state: PhaseState.PresidentialPowerUseState.SnapSelectState,
    resultCallback: (PhaseResult.SnapSelected) -> Unit
) = SelectFragmentBase(
    title = "Select presidential candidate for snap election.",
    subtitle = "Allowed selectablePlayers:",
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { resultCallback(PhaseResult.SnapSelected(it)) }
)

fun toKillSelectFrag(
    state: PhaseState.PresidentialPowerUseState.KillState,
    resultCallback: (PhaseResult.KillTargetSelected) -> Unit
) = SelectFragmentBase(
    title = "Select player to kill.",
    subtitle = "Allowed selectablePlayers:",
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { resultCallback(PhaseResult.KillTargetSelected(it)) }
)
