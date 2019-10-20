package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.Bi

sealed class PhaseResult {

    data class PlainStateResult(
        val nestedPhase: PhaseState
    ) : PhaseResult()

    object IdentificationDone : PhaseResult()

    data class CandidatesSelected(
        val chancellor: Player?
    ) : PhaseResult()

    data class VoteResult(
        val votes: List<Pair<Player, Boolean>>,
        val elected: Government?
    ) : PhaseResult()

    data class ChancellorDiscardResult(
        val cards: Bi<Article>
    ) : PhaseResult()

}