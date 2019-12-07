package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.Player
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class SelectFragmentBase(
    private val titleRes: Int,
    private val subtitleRes: Int?,
    private val options: List<Player>,
    private val confirmMessageProvider: ((String) -> String)?,
    private val selectCallback: (Player) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            lparams(matchParent, matchParent)

            textView(titleRes) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }.lparams(width = matchParent, height = wrapContent)

            subtitleRes?.apply {
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
    }.view

}

fun chancellorSelectFrag(
    state: PhaseState.ChancellorSelectState,
    stateChangeCallback: (PhaseResult.CandidatesSelected) -> Unit
) = SelectFragmentBase(
    titleRes = R.string.select_chancellor,
    subtitleRes = R.string.allowed_candidates,
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { stateChangeCallback(PhaseResult.CandidatesSelected(it)) }
)

fun checkPartySelectFrag(
    state: PhaseState.PresidentialPowerUseState.CheckPartySelectState,
    resultCallback: (PhaseResult.CheckPartySelected) -> Unit
) = SelectFragmentBase(
    titleRes = R.string.select_to_inspect,
    subtitleRes = R.string.allowed_selectable,
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { resultCallback(PhaseResult.CheckPartySelected(it)) }
)

fun snapSelectFrag(
    state: PhaseState.PresidentialPowerUseState.SnapSelectState,
    resultCallback: (PhaseResult.SnapSelected) -> Unit
) = SelectFragmentBase(
    titleRes = R.string.select_snap,
    subtitleRes = R.string.allowed_selectable,
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { resultCallback(PhaseResult.SnapSelected(it)) }
)

fun toKillSelectFrag(
    state: PhaseState.PresidentialPowerUseState.KillState,
    resultCallback: (PhaseResult.KillTargetSelected) -> Unit
) = SelectFragmentBase(
    titleRes = R.string.select_to_kill,
    subtitleRes = R.string.allowed_selectable,
    options = state.selectablePlayers,
    confirmMessageProvider = null,
    selectCallback = { resultCallback(PhaseResult.KillTargetSelected(it)) }
)
