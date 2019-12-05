package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.util.Bi

sealed class PhaseResult {

    data class PlainStateResult(
        val nestedPhase: PhaseState
    ) : PhaseResult()

    object IdentificationDone : PhaseResult()

    data class CandidatesSelected(
        val chancellor: Player
    ) : PhaseResult()

    data class VoteResult(
        val votes: List<Pair<Player, Boolean>>,
        val elected: Government?
    ) : PhaseResult() {

        val jas: List<Player> by lazy {
            votes.filter { it.second }.map { it.first }
        }

        val neins: List<Player> by lazy {
            votes.filter { !it.second }.map { it.first }
        }

    }

    data class PresidentDiscardResult(
        val forwarded: Bi<Article>,
        val discarded: Article
    ) : PhaseResult()

    data class ChancellorDiscardResult(
        val placed: Article,
        val discarded: Article
    ) : PhaseResult()

    data class Veto(
        val cards: Bi<Article>
    ) : PhaseResult()

    data class KillTargetSelected(
        val toKill: Player
    ) : PhaseResult()

    data class SnapSelected(
        val nextPresidentCandidate: Player
    ) : PhaseResult()

    data class CheckPartySelected(
        val selected: Player
    ) : PhaseResult()

    object PresidentialPowerDone : PhaseResult()

}