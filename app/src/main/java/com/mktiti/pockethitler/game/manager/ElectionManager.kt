package com.mktiti.pockethitler.game.manager

import com.mktiti.pockethitler.game.data.ElectionState
import com.mktiti.pockethitler.game.data.Government
import com.mktiti.pockethitler.game.data.Player

class ElectionManager(
    private val playerManager: PlayerManager,
    failedElections: Int,
    president: Player,
    snapPresident: Player?
) {

    constructor(playerManager: PlayerManager, state: ElectionState) : this(playerManager, state.failedElections, state.presidentCandidate, state.snapPresidentCandidate)

    val state: ElectionState
        get() = ElectionState(failedElections, presidentCandidate, snapPresidentCandidate)

    private var failedElections: Int = failedElections

    private var snapPresidentCandidate: Player? = snapPresident

    var presidentCandidate = president
        private set

    private var lastElected: Government? = null

    fun nextPresidentCandidate(): Player = playerManager.nextLiving(presidentCandidate).apply {
        snapPresidentCandidate = null
        presidentCandidate = this
    }

    fun possibleChancellors(): List<Player> = playerManager.livingPlayers.filter {
        lastElected?.let { gov ->
            if (it == gov.president || it == gov.chancellor) {
                return@filter false
            }
        }

        it != presidentCandidate
    }

    fun snapElection(presidentCandidate: Player) {
        snapPresidentCandidate = presidentCandidate
    }

    fun elect(government: Government) {
        lastElected = government
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