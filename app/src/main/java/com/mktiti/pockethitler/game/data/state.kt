package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.game.manager.ArticleDeck
import com.mktiti.pockethitler.game.manager.PlayerManager
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class ElectionState(
    val failedElections: Int,
    val presidentCandidate: Player,
    val snapPresidentCandidate: Player?
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

@Serializable
data class GameState(
    val tableState: TableState,
    @Polymorphic val phaseState: PhaseState
) {

    companion object {
        fun parse(jsonValue: String): GameState = phaseJson.parse(serializer(), jsonValue)
    }

    fun stringify() = phaseJson.stringify(serializer(), this)

}

fun initNewState(players: List<String>): GameState {
    val playerState = PlayerManager.randomSetup(players)
    val firstPresident = playerState.players.random().player
    return GameState(
        tableState = TableState(
            playersState = playerState,
            electionState = ElectionState(0, firstPresident, null),
            deckState = ArticleDeck.newDeck(),
            boardsState = BoardsState()
        ),
        // phaseState = startState(playerState.players.map { it.player })
        // For testing
        phaseState = PhaseState.PresidentDiscardState(Triple(Article.LIBERAL, Article.FASCIST, Article.LIBERAL))
    )
}