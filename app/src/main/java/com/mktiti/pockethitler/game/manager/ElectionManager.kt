package com.mktiti.pockethitler.game.manager

import com.mktiti.pockethitler.game.data.ElectedStatus.InSession
import com.mktiti.pockethitler.game.data.ElectedStatus.VotingState
import com.mktiti.pockethitler.game.data.ElectedStatus.VotingState.*
import com.mktiti.pockethitler.game.data.ElectionState
import com.mktiti.pockethitler.game.data.Government
import com.mktiti.pockethitler.game.data.Player

class ElectionManager(
    private val playerManager: PlayerManager,
    private var failedElections: Int,
    electionState: ElectionState
) {

    constructor(playerManager: PlayerManager, state: ElectionState) : this(playerManager, state.failedElections, state)

    private var electedStatus = electionState.electedStatus

    val state: ElectionState
        get() = ElectionState(failedElections, electedStatus)

    fun nextPresidentCandidate(): Player {
        return playerManager.nextLiving(state.electedStatus.nextFrom).also { next ->
            electedStatus = when (val s = electedStatus) {
                is NoneElectedYet -> s.copy(presidentCandidate = next)
                is InSession -> OutOfSession(s.government, next)
                is OutOfSession -> s.copy(presidentCandidate = next)
                is SnapElection -> if (s.lastElected == null) {
                    NoneElectedYet(next)
                } else {
                    OutOfSession(s.lastElected, next)
                }
            }
        }
    }

    fun currentPresCandidate(): Player? = when (val s = electedStatus) {
        is VotingState -> s.presidentCandidate
        else -> null
    }

    fun currentGov(): Government? = when (val s = electedStatus) {
        is InSession -> s.government
        else -> null
    }

    fun possibleChancellors(): List<Player> {
        val candidate = when (val s = electedStatus) {
            is VotingState -> s.presidentCandidate
            else -> return emptyList()
        }


        return playerManager.livingPlayers.filter {
            val lastGov: Government? = when (val s = electedStatus) {
                is InSession -> s.government
                is OutOfSession -> s.lastElected
                is SnapElection -> s.lastElected
                is NoneElectedYet -> null
            }

            lastGov?.let { gov ->
                if (it == gov.president || it == gov.chancellor) {
                    return@filter false
                }
            }

            candidate != it
        }
    }

    fun snapElection(selected: Player) {
        val state: InSession = (electedStatus as? InSession) ?: throw IllegalStateException("Cannot call snap election from this state")
        electedStatus = SnapElection(
            lastElected = state.government,
            jumpedFrom = state.government.president,
            presidentCandidate = selected
        )
    }

    fun elect(government: Government) {
        failedElections = 0
        electedStatus = InSession(
            government = government,
            jumpedFrom = when (val s = electedStatus) {
                is SnapElection -> s.jumpedFrom
                else -> null
            }
        )
    }

    fun unsuccessful(): Boolean {
        nextPresidentCandidate()

        return if (++failedElections == 3) {
            failedElections = 0
            true
        } else {
            false
        }
    }

}