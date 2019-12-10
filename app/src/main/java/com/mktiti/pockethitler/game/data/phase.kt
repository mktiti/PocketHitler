package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import com.mktiti.pockethitler.game.data.PhaseState.IdentityInfoState
import com.mktiti.pockethitler.game.data.PlayerIdentity.FASCIST
import com.mktiti.pockethitler.game.data.PlayerIdentity.HITLER
import com.mktiti.pockethitler.util.Bi
import com.mktiti.pockethitler.util.ResourceManager
import com.mktiti.pockethitler.util.Tri
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

fun startState(players: List<Player>, resourceManager: ResourceManager): PhaseState =
    EnvelopeState(
        message = resourceManager.format(R.string.env_secret_id, players.first().name),
        nestedState = IdentityInfoState(
            identities = players,
            hitlerName = players.find { it.identity == HITLER }?.name!!,
            fascistNames = players.filter { it.identity == FASCIST }.map { it.name }
        )
    )

@Serializable
sealed class PhaseState {

    @Serializable
    data class EnvelopeState(
        val message: String,
        @Polymorphic val nestedState: PhaseState
    ) : PhaseState()

    @Serializable
    data class IdentityInfoState(
        val identities: List<Player>,
        val hitlerName: String,
        val fascistNames: List<String>
    ) : PhaseState()

    @Serializable
    data class ChancellorSelectState(
        val selectablePlayers: List<Player>
    ) : PhaseState()

    @Serializable
    data class VoteState(
        val candidates: Government,
        val votes: List<Pair<Player, Boolean>> = emptyList(),
        val futureVotes: List<Player>
    ) : PhaseState() {

        fun nextVoter(): Player = futureVotes.first()

        fun addVote(vote: Boolean): VoteState = copy(
            votes = votes + (futureVotes.first() to vote),
            futureVotes = futureVotes.drop(1)
        )

        fun result(): PhaseResult.VoteResult? = if (futureVotes.isNotEmpty()) {
            null
        } else {
            val success = votes.count { it.second } >= (votes.size / 2 + votes.size % 2)
            PhaseResult.VoteResult(
                votes = votes,
                elected = if (success) candidates else null
            )
        }

    }

    @Serializable
    data class PresidentDiscardState(
        val cards: Tri<Article>
    ) : PhaseState()

    @Serializable
    data class ChancellorDiscardState(
        val cards: Bi<Article>,
        val canVeto: Boolean
    ) : PhaseState()

    @Serializable
    data class VetoConfirmState(
        val cards: Bi<Article>
    ) : PhaseState()

    @Serializable
    sealed class PresidentialPowerUseState : PhaseState() {

        @Serializable
        data class PeekCardsState(
            val cards: Tri<Article>
        ) : PresidentialPowerUseState()

        @Serializable
        data class SnapSelectState(
            val selectablePlayers: List<Player>
        ) : PresidentialPowerUseState()

        @Serializable
        data class CheckPartySelectState(
            val selectablePlayers: List<Player>
        ) : PresidentialPowerUseState()

        @Serializable
        data class CheckPartyViewState(
            val player: Player
        ) : PresidentialPowerUseState()

        @Serializable
        data class KillState(
            val selectablePlayers: List<Player>
        ) : PresidentialPowerUseState()

    }

    @Serializable
    data class GameWon(val winner: Party) : PhaseState()

}