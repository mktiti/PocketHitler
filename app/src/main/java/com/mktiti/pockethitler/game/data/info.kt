package com.mktiti.pockethitler.game.data

import java.time.LocalDateTime
import java.time.ZoneOffset

data class GameInfo(
    val creationDate: Long,
    val state: GameState
)

fun gameInfo(state: GameState, creationTime: LocalDateTime = LocalDateTime.now()) = GameInfo(
    creationDate = creationTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
    state = state
)
