package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.game.data.PhaseState.*
import com.mktiti.pockethitler.game.manager.ArticleDeck
import com.mktiti.pockethitler.game.manager.PlayerManager
import com.mktiti.pockethitler.util.ResourceManager
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@Serializable
sealed class ElectedStatus {

    abstract val nextFrom: Player

    @Serializable
    data class InSession(
        val government: Government,
        val jumpedFrom: Player?
    ) : ElectedStatus() {
        override val nextFrom: Player
            get() = jumpedFrom ?: government.president
    }

    @Serializable
    sealed class VotingState : ElectedStatus() {

        abstract val presidentCandidate: Player

        @Serializable
        data class NoneElectedYet(
            override val presidentCandidate: Player
        ) : VotingState() {

            override val nextFrom: Player
                get() = presidentCandidate

        }

        @Serializable
        data class OutOfSession(
            val lastElected: Government,
            override val presidentCandidate: Player
        ) : VotingState() {

            override val nextFrom: Player
                get() = presidentCandidate

        }

        @Serializable
        data class SnapElection(
            val lastElected: Government?,
            val jumpedFrom: Player,
            override val presidentCandidate: Player
        ) : VotingState() {

            override val nextFrom: Player
                get() = jumpedFrom

        }
    }

}

@Serializable
data class ElectionState(
    val failedElections: Int,
    @Polymorphic val electedStatus: ElectedStatus
)

@Serializable
data class PlayersState(
    val players: List<PlayerData>
)

@Serializable
data class DeckState(
    val drawStack: List<Article>,
    val discardStack: List<Article>
)

@Serializable
data class BoardsState(
    val fascistCards: Int = 0,
    val liberalCards: Int = 0
)

@Serializable
data class TableState(
    val playersState: PlayersState,
    val electionState: ElectionState,
    val deckState: DeckState,
    val boardsState: BoardsState
)


private val stateModule = SerializersModule {
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
        PresidentialPowerUseState.SnapSelectState::class with PresidentialPowerUseState.SnapSelectState.serializer()
        GameWon::class with GameWon.serializer()
    }

    polymorphic<ElectedStatus> {
        ElectedStatus.InSession::class with ElectedStatus.InSession.serializer()
        ElectedStatus.VotingState.NoneElectedYet::class with ElectedStatus.VotingState.NoneElectedYet.serializer()
        ElectedStatus.VotingState.OutOfSession::class with ElectedStatus.VotingState.OutOfSession.serializer()
        ElectedStatus.VotingState.SnapElection::class with ElectedStatus.VotingState.SnapElection.serializer()
    }
}

private val stateJson = Json(context = stateModule)

@Serializable
data class GameState(
    val tableState: TableState,
    @Polymorphic val phaseState: PhaseState
) {

    companion object {
        fun parse(jsonValue: String): GameState = stateJson.parse(serializer(), jsonValue)
    }

    fun stringify() = stateJson.stringify(serializer(), this)

}

fun initNewState(players: List<String>, resourceManager: ResourceManager): GameState {
    val playerState = PlayerManager.randomSetup(players)
    val firstPresident = playerState.players.random().player
    return GameState(
        tableState = TableState(
            playersState = playerState,
            electionState = ElectionState(0, ElectedStatus.VotingState.NoneElectedYet(firstPresident)),
            deckState = ArticleDeck.newDeck(),
            boardsState = BoardsState()
        ),
        phaseState = startState(playerState.players.map { it.player }, resourceManager)
        // For testing
        // phaseState = PresidentDiscardState(Triple(Article.LIBERAL, Article.FASCIST, Article.LIBERAL))
    )
}