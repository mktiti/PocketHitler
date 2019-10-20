package com.mktiti.pockethitler.game.data

import kotlinx.serialization.Serializable

enum class Party {
    LIBERAL, FASCIST
}

enum class PlayerIdentity(val party: Party) {
    LIBERAL(Party.LIBERAL),
    FASCIST(Party.FASCIST),
    HITLER(Party.FASCIST)
}

@Serializable
data class Player(
    val name: String,
    val identity: PlayerIdentity
) {

    override fun equals(other: Any?) = (other as? Player)?.name == name

    override fun hashCode() = name.hashCode()

}

@Serializable
data class PlayerData(
    val player: Player,
    val alive: Boolean
)

enum class Article {
    LIBERAL, FASCIST
}

@Serializable
data class Government(
    val president: Player,
    val chancellor: Player
)