package com.mktiti.pockethitler.game.manager

import com.mktiti.pockethitler.game.data.Government
import com.mktiti.pockethitler.game.data.Player

class ElectionManager(
    private val playerManager: PlayerManager
) {

    var failedElections: Int = 0
        private set

    var lastPresidentCandidate = playerManager.randomPlayer()
        private set

    var lastElected: Government? = null
        private set

    fun elect(government: Government) {
        lastPresidentCandidate = government.president
        lastElected = government
    }

    fun unsuccessful(): Boolean {
        lastPresidentCandidate

        return if (++failedElections == 3) {
            failedElections = 0
            true
        } else {
            false
        }
    }

    fun presidentCandidate(): Player = playerManager.nextLiving(lastPresidentCandidate)

}