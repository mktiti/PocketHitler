package com.mktiti.pockethitler.game.manager

import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.GameState
import com.mktiti.pockethitler.game.data.TableState
import com.mktiti.pockethitler.game.manager.ArticleDeck
import com.mktiti.pockethitler.game.manager.Boards
import com.mktiti.pockethitler.game.manager.PlayerManager
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseResult.*
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.game.data.PhaseState.*
import com.mktiti.pockethitler.game.data.PhaseState.PresidentialPowerUseState.KillState
import com.mktiti.pockethitler.game.data.PhaseState.PresidentialPowerUseState.PeekCardsState
import com.mktiti.pockethitler.view.EnvelopeFragment
import com.mktiti.pockethitler.view.IdentityFragment

class GameEngine(
    initState: GameState,
    private val messageCallback: (String) -> Unit,
    private val phaseFragmentCallback: (Fragment?) -> Unit
) {

    private val playerManager =
        PlayerManager(initState.tableState.playersState)
    private val deck =
        ArticleDeck(initState.tableState.deckState)
    private val boards = Boards(
        playerManager.playerCount,
        initState.tableState.boardsState
    )

    private var phaseState: PhaseState = initState.phaseState

    init {
        phaseFragmentCallback(fragmentOfPhase(phaseState))
    }

    fun currentState(): GameState =
        GameState(
            tableState = TableState(
                playersState = playerManager.state,
                boardsState = boards.state,
                deckState = deck.state
            ),
            phaseState = phaseState
        )

    private fun onPhaseResult(result: PhaseResult) {
        val newState = when (result) {
            is PlainStateResult -> result.nestedPhase
            is IdentificationDone -> ChancellorSelectState(playerManager.possibleChancellors())
            is CandidatesSelected -> TODO()
            is VoteResult -> TODO()
            is ChancellorDiscardResult -> TODO()
        }

        messageCallback("New State is: $newState")
        phaseFragmentCallback(fragmentOfPhase(newState))
    }

    private fun fragmentOfPhase(state: PhaseState): Fragment = when (state) {
        is EnvelopeState -> EnvelopeFragment(state, this::onPhaseResult)
        is IdentityInfoState -> IdentityFragment(state, this::onPhaseResult)
        is ChancellorSelectState -> TODO()
        is VoteState -> TODO()
        is ChancellorDiscardState -> TODO()
        is PresidentDiscardState -> TODO()
        is PeekCardsState -> TODO()
        is KillState -> TODO()
    }

}