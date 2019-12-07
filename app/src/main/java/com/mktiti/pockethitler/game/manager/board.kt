package com.mktiti.pockethitler.game.manager

import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.game.PresidentialAction.*
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.BoardsState

sealed class Board(
    private val maxCount: Int,
    count : Int = 0
) {

    val isCompleted: Boolean
        get() = count == maxCount

    var count: Int = count
        protected set

    abstract fun place(): PresidentialAction?

}

enum class PlayerCount(val fascistCount: Int) {
    FEW(1), MEDIUM(2), MANY(3);

    companion object {
        fun of(count: Int): PlayerCount = when(count) {
            5, 6  -> FEW
            7, 8  -> MEDIUM
            9, 10 -> MANY
            else  -> throw IllegalArgumentException("Invalid player count: $count, must be in range 5-10")
        }
    }
}

class FascistBoard(
    playerCount: PlayerCount,
    cardsDown: Int = 0
) : Board(6, cardsDown) {

    companion object {
        fun actions(playerCount: PlayerCount): List<PresidentialAction?> = listOf(
            byPlayerCount(playerCount, null, null, CHECK_PARTY),
            byPlayerCount(playerCount, null, CHECK_PARTY, CHECK_PARTY),
            byPlayerCount(playerCount, PEEK_NEXT, SNAP_ELECTION, SNAP_ELECTION),
            KILL, KILL, null
        )

        private fun <T> byPlayerCount(playerCount: PlayerCount, few: T, medium: T, many: T): T = when (playerCount) {
            PlayerCount.FEW -> few
            PlayerCount.MEDIUM -> medium
            PlayerCount.MANY -> many
        }
    }

    private val actions: List<PresidentialAction?> = actions(playerCount)

    val inRedZone: Boolean
        get() = count >= 3

    val isVetoEnabled: Boolean
        get() = count >= 5

    override fun place(): PresidentialAction? = actions[count++]

}

class LiberalBoard(cardsDown: Int = 0) : Board(5, cardsDown) {

    override fun place(): PresidentialAction? {
        count++
        return null
    }

}

class Boards(numberOfPlayers: Int, fascistCards: Int = 0, liberalCards: Int = 0) {
    private val fascistBoard = FascistBoard(
        PlayerCount.of(numberOfPlayers), fascistCards
    )
    private val liberalBoard = LiberalBoard(liberalCards)

    constructor(numberOfPlayers: Int, state: BoardsState) : this(numberOfPlayers, state.fascistCards, state.liberalCards)

    val state: BoardsState
        get() = BoardsState(
            fascistBoard.count,
            liberalBoard.count
        )

    val finished: Article?
        get() = when {
            liberalBoard.isCompleted -> Article.LIBERAL
            fascistBoard.isCompleted -> Article.FASCIST
            else -> null
        }

    val inRedZone: Boolean
        get() = fascistBoard.inRedZone

    val isVetoEnabled: Boolean
        get() = fascistBoard.isVetoEnabled

    fun place(article: Article): PresidentialAction? = when (article) {
        Article.FASCIST -> fascistBoard.place()
        Article.LIBERAL -> liberalBoard.place()
    }
}