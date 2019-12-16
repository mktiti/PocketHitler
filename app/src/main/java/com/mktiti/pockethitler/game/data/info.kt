package com.mktiti.pockethitler.game.data

import java.time.LocalDateTime

data class GameInfo(
    val creationDate: LocalDateTime,
    val state: GameState
)