package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.game.data.PlayerIdentity.FASCIST
import com.mktiti.pockethitler.game.data.PlayerIdentity.HITLER
import com.mktiti.pockethitler.util.Bi
import com.mktiti.pockethitler.util.Tri
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import com.mktiti.pockethitler.game.data.PhaseState.*

fun startState(players: List<Player>): PhaseState =
    PhaseState.EnvelopeState(
        message = "Secret identity of ${players.first().name}",
        nestedState = PhaseState.IdentityInfoState(
            identities = players,
            hitlerName = players.find { it.identity == HITLER }?.name!!,
            fascistNames = players.filter { it.identity == FASCIST }.map { it.name }
        )
    )

private val phaseModule = SerializersModule {
    polymorphic<PhaseState> {
        EnvelopeState::class with EnvelopeState.serializer()
        IdentityInfoState::class with IdentityInfoState.serializer()
        ChancellorSelectState::class with ChancellorSelectState.serializer()
        VoteState::class with VoteState.serializer()
        ChancellorDiscardState::class with ChancellorDiscardState.serializer()
        PresidentDiscardState::class with PresidentDiscardState.serializer()
        VetoConfirmState::class with VetoConfirmState.serializer()
        PresidentialPowerUseState.PeekCardsState::class with PresidentialPowerUseState.PeekCardsState.serializer()
        PresidentialPowerUseState.CheckPartySelectState::class with PresidentialPowerUseState.CheckPartySelectState.serializer()
        PresidentialPowerUseState.CheckPartyViewState::class with PresidentialPowerUseState.CheckPartyViewState.serializer()
        PresidentialPowerUseState.KillState::class with PresidentialPowerUseState.KillState.serializer()
        GameWon::class with GameWon.serializer()
    }
}

val phaseJson = Json(context = phaseModule)

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