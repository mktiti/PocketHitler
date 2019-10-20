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
    private val playerCount: PlayerCount,
    cardsDown: Int = 0
) : Board(6, cardsDown) {

    val inRedZone: Boolean
        get() = count >= 3

    val isVetoEnabled: Boolean
        get() = count >= 5

    private fun <T> byPlayerCount(few: T, medium: T, many: T): T = when (playerCount) {
        PlayerCount.FEW -> few
        PlayerCount.MEDIUM -> medium
        PlayerCount.MANY -> many
    }

    override fun place(): PresidentialAction? = when (++count) {
        1 -> byPlayerCount(null, null, CHECK_PARTY)
        2 -> byPlayerCount(null, CHECK_PARTY, CHECK_PARTY)
        3 -> byPlayerCount(PEEK_NEXT, SNAP_ELECTION, SNAP_ELECTION)
        4, 5 -> KILL
        else -> null
    }

}

class LiberalBoard(cardsDown: Int = 0) : Board(5, cardsDown) {

    override fun place(): PresidentialAction? = null

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

    val inRedZone: Boolean
        get() = fascistBoard.inRedZone

    val isVetoEnabled: Boolean
        get() = fascistBoard.isVetoEnabled

    fun place(article: Article): PresidentialAction? = when (article) {
        Article.FASCIST -> fascistBoard.place()
        Article.LIBERAL -> liberalBoard.place()
    }
}