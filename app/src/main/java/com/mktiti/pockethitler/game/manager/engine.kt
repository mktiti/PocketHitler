package com.mktiti.pockethitler.game.manager

import android.util.Log
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
                        president = electionManager.currentPresCandidate()!!,
                        chancellor = result.chancellor
                    ), futureVotes = playerManager.livingPlayers
                )
            )

            is VoteResult -> {
                messageCallback(when (result.elected) {
                    null -> resourceManager.format(
                                R.string.vote_result_fail,
                                result.jas.map(Player::name),
                                result.neins.map(Player::name)
                    )
                    else -> resourceManager.format(
                                R.string.vote_result_succ,
                                result.jas.map(Player::name),
                                result.neins.map(Player::name),
                                result.elected.president.name,
                                result.elected.chancellor.name
                    )
                })

                if (result.elected == null) {
                    if (electionManager.unsuccessful()) {
                        val article = deck.drawOne()
                        boards.place(article)

                        when (boards.finished) {
                            Article.LIBERAL -> winByArticle(Party.LIBERAL)
                            Article.FASCIST -> winByArticle(Party.FASCIST)
                            null -> ChancellorSelectState(electionManager.possibleChancellors())
                        }
                    } else {
                        ChancellorSelectState(electionManager.possibleChancellors())
                    }
                } else if (result.elected.chancellor.identity == PlayerIdentity.HITLER && boards.inRedZone) {
                    messageCallback(resourceManager.format(R.string.fash_win_by_hitler, result.elected.chancellor.name))
                    GameWon(Party.FASCIST)
                } else {
                    electionManager.elect(result.elected)
                    EnvelopeState(resourceManager.format(R.string.articles_for, result.elected.president.name), PresidentDiscardState(deck.drawThree()))
                }
            }

            is PresidentDiscardResult -> {
                deck.discard(result.discarded)
                EnvelopeState(resourceManager.format(R.string.env_for_chancellor, electionManager.currentGov()?.chancellor?.name!!), ChancellorDiscardState(result.forwarded, boards.isVetoEnabled))
            }

            is ChancellorDiscardResult -> {
                deck.discard(result.discarded)

                messageCallback(result.placed.name)

                when (boards.place(result.placed)) {
                    PresidentialAction.CHECK_PARTY -> PresidentialPowerUseState.CheckPartySelectState(playerManager.livingPlayers)
                    PresidentialAction.SNAP_ELECTION -> PresidentialPowerUseState.SnapSelectState(playerManager.livingPlayers)
                    PresidentialAction.PEEK_NEXT -> EnvelopeState(resourceManager[R.string.president_peek], PeekCardsState(deck.peekThree()))
                    PresidentialAction.KILL -> KillState(playerManager.livingPlayers)
                    null -> {
                        when (boards.finished) {
                            Article.LIBERAL -> winByArticle(Party.LIBERAL)
                            Article.FASCIST -> winByArticle(Party.FASCIST)
                            null -> nextRound()
                        }
                    }
                }
            }

            is Veto -> VetoConfirmState(result.cards)

            is KillTargetSelected -> {
                messageCallback(resourceManager.format(R.string.player_killed, result.toKill.name))

                if (playerManager.kill(result.toKill)) {
                    messageCallback(resourceManager.format(R.string.lib_win_by_assassination, result.toKill.name))
                    GameWon(Party.LIBERAL)
                } else {
                    nextRound()
                }
            }

            is SnapSelected -> {
                electionManager.snapElection(result.nextPresidentCandidate)
                ChancellorSelectState(electionManager.possibleChancellors())
            }

            is CheckPartySelected -> EnvelopeState(
                message = resourceManager.format(R.string.env_for_president, electionManager.currentGov()?.president?.name!!),
                nestedState = PresidentialPowerUseState.CheckPartyViewState(result.selected)
            )

            PresidentialPowerDone -> nextRound()
        }

        Log.i("Engine state", "New State is: $phaseState")
        stateChangeCallback(currentState().tableState, fragmentOfPhase(phaseState))
    }

    private fun winByArticle(party: Party): PhaseState {
        val message = resourceManager[when (party) {
            Party.LIBERAL -> R.string.lib_win_by_articles
            Party.FASCIST -> R.string.fash_win_by_articles
        }]
        messageCallback(message)
        return GameWon(party)
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