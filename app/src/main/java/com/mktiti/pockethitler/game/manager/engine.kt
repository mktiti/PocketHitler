package com.mktiti.pockethitler.game.manager

import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.game.data.*
import com.mktiti.pockethitler.game.data.PhaseResult.*
import com.mktiti.pockethitler.game.data.PhaseState.*
import com.mktiti.pockethitler.game.data.PhaseState.PresidentialPowerUseState.KillState
import com.mktiti.pockethitler.game.data.PhaseState.PresidentialPowerUseState.PeekCardsState
import com.mktiti.pockethitler.util.ResourceManager
import com.mktiti.pockethitler.view.phase.*

class GameEngine(
    private val initState: GameState,
    private val messageCallback: (String) -> Unit,
    private val stateChangeCallback: (TableState, Fragment?) -> Unit,
    private val resourceManager: ResourceManager
) {

    private val playerManager = PlayerManager(initState.tableState.playersState)

    private val electionManager = ElectionManager(playerManager, initState.tableState.electionState)

    private val deck = ArticleDeck(initState.tableState.deckState)

    private val boards = Boards(
        playerManager.playerCount,
        initState.tableState.boardsState
    )

    private var phaseState: PhaseState = initState.phaseState

    fun start() {
        stateChangeCallback(initState.tableState, fragmentOfPhase(phaseState))
    }

    fun currentState(): GameState =
        GameState(
            tableState = TableState(
                playersState = playerManager.state,
                electionState = electionManager.state,
                boardsState = boards.state,
                deckState = deck.state
            ),
            phaseState = phaseState
        )

    private fun onPhaseResult(result: PhaseResult) {
        phaseState = when (result) {
            is PlainStateResult -> result.nestedPhase

            is IdentificationDone -> ChancellorSelectState(electionManager.possibleChancellors())

            is CandidatesSelected -> EnvelopeState(
                message = resourceManager.format(R.string.env_general, playerManager.livingPlayers.first().name),
                nestedState = VoteState(
                    candidates = Government(
                        president = electionManager.presidentCandidate,
                        chancellor = result.chancellor
                    ), futureVotes = playerManager.livingPlayers
                )
            )

            is VoteResult -> {
                messageCallback(resourceManager.format(R.string.vote_result, result.jas, result.neins))
                if (result.elected == null) {
                    if (electionManager.unsuccessful()) {
                        val article = deck.drawOne()
                        boards.place(article)

                        when (boards.finished) {
                            Article.LIBERAL -> GameWon(Party.LIBERAL)
                            Article.FASCIST -> GameWon(Party.FASCIST)
                            null -> ChancellorSelectState(electionManager.possibleChancellors())
                        }
                    } else {
                        ChancellorSelectState(electionManager.possibleChancellors())
                    }
                } else if (result.elected.chancellor.identity == PlayerIdentity.HITLER && boards.inRedZone) {
                    GameWon(Party.FASCIST)
                } else {
                    electionManager.elect(result.elected)
                    EnvelopeState(resourceManager.format(R.string.articles_for, result.elected.president.name), PresidentDiscardState(deck.drawThree()))
                }
            }

            is PresidentDiscardResult -> {
                deck.discard(result.discarded)
                EnvelopeState(resourceManager.format(R.string.env_for_chancellor, "XY"), ChancellorDiscardState(result.forwarded, boards.isVetoEnabled))
            }

            is ChancellorDiscardResult -> {
                deck.discard(result.discarded)
                when (boards.place(result.placed)) {
                    PresidentialAction.CHECK_PARTY -> PresidentialPowerUseState.CheckPartySelectState(playerManager.livingPlayers)
                    PresidentialAction.SNAP_ELECTION -> PresidentialPowerUseState.SnapSelectState(playerManager.livingPlayers)
                    PresidentialAction.PEEK_NEXT -> EnvelopeState(resourceManager[R.string.president_peek], PeekCardsState(deck.peekThree()))
                    PresidentialAction.KILL -> KillState(playerManager.livingPlayers)
                    null -> {
                        when (boards.finished) {
                            Article.LIBERAL -> GameWon(Party.LIBERAL)
                            Article.FASCIST -> GameWon(Party.FASCIST)
                            null -> nextRound()
                        }
                    }
                }
            }

            is Veto -> VetoConfirmState(result.cards)

            is KillTargetSelected -> {
                if (playerManager.kill(result.toKill)) {
                    GameWon(Party.LIBERAL)
                } else {
                    nextRound()
                }
            }

            is SnapSelected -> {
                electionManager.snapElection(result.nextPresidentCandidate)
                ChancellorSelectState(electionManager.possibleChancellors())
            }

            is CheckPartySelected -> EnvelopeState(resourceManager.format(R.string.env_for_president, "ABC"), PresidentialPowerUseState.CheckPartyViewState(result.selected))

            PresidentialPowerDone -> nextRound()
        }

        messageCallback("New State is: $phaseState")
        stateChangeCallback(currentState().tableState, fragmentOfPhase(phaseState))
    }

    private fun nextRound(): ChancellorSelectState {
        electionManager.nextPresidentCandidate()
        return ChancellorSelectState(electionManager.possibleChancellors())
    }

    private fun fragmentOfPhase(state: PhaseState): Fragment = when (state) {
        is EnvelopeState -> EnvelopeFragment(state, this::onPhaseResult)
        is IdentityInfoState -> IdentityFragment(state, this::onPhaseResult)
        is ChancellorSelectState -> chancellorSelectFrag(state, this::onPhaseResult)
        is VoteState -> VoteFragment(state, this::onPhaseResult)
        is PresidentDiscardState -> PresidentDiscardFragment(state, this::onPhaseResult)
        is ChancellorDiscardState -> ChancellorDiscardFragment(state, this::onPhaseResult)
        is VetoConfirmState -> VetoConfirmFragment(state, this::onPhaseResult)
        is PeekCardsState -> PeekCardsFragment(state, this::onPhaseResult)
        is KillState -> toKillSelectFrag(state, this::onPhaseResult)
        is PresidentialPowerUseState.CheckPartySelectState -> checkPartySelectFrag(state, this::onPhaseResult)
        is PresidentialPowerUseState.CheckPartyViewState -> CheckPartyViewFragment(state, this::onPhaseResult)
        is PresidentialPowerUseState.SnapSelectState -> snapSelectFrag(state, this::onPhaseResult)
        is GameWon -> GameOverFragment(state)
    }

}