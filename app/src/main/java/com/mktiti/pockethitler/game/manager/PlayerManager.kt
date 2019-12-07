package com.mktiti.pockethitler.game.manager

import com.mktiti.pockethitler.game.data.Player
import com.mktiti.pockethitler.game.data.PlayerData
import com.mktiti.pockethitler.game.data.PlayerIdentity
import com.mktiti.pockethitler.game.data.PlayerIdentity.*
import com.mktiti.pockethitler.game.data.PlayersState
import com.mktiti.pockethitler.util.forever
import java.util.*

class PlayerManager(players: List<PlayerData>) {

    private val allPlayers:  MutableList<PlayerData> = players.toMutableList()

    val livingPlayers: List<Player>
        get() = allPlayers.filter { it.alive }.map(PlayerData::player)

    val state: PlayersState
        get() = PlayersState(allPlayers)

    val playerCount: Int
        get() = allPlayers.size

    companion object {
        fun randomSetup(players: List<String>): PlayersState {
            val playerCount =
                PlayerCount.of(players.size)

            val remaining = players.mapIndexed { index, name -> index to name }.toMutableList()

            fun Pair<Int, String>.toPlayer(identity: PlayerIdentity): Pair<Int, Player> {
                return first to Player(second, identity)
            }

            val random = Random()

            val hitler = remaining.removeAt(random.nextInt(remaining.size)).toPlayer(HITLER)
            val fascists = (0..playerCount.fascistCount).map {
                remaining.removeAt(random.nextInt(remaining.size)).toPlayer(FASCIST)
            }
            val liberals = remaining.map { it.toPlayer(LIBERAL) }

            val allPlayers = (listOf(hitler) + fascists + liberals)
                                .sortedBy { it.first }
                                .map {
                                    PlayerData(
                                        it.second,
                                        true
                                    )
                                }

            return PlayersState(allPlayers)
        }
    }

    constructor(playersState: PlayersState) : this(playersState.players)

    init {
        PlayerCount.of(players.size) // Size check
    }

    private fun index(player: Player): Int = allPlayers.indexOfFirst { it.player == player }

    fun kill(player: Player): Boolean {
        allPlayers[index(player)] = PlayerData(player, false)
        return player.identity == HITLER
    }

    private fun nextFiltered(current: Player, predicate: (PlayerData) -> Boolean): Player {
        var index = index(current)

        forever {
            index = (index + 1) % allPlayers.size
            val data = allPlayers[index]
            if (predicate(data)) {
                return data.player
            }
        }
    }

    fun nextLiving(current: Player): Player = nextFiltered(current) { it.alive }

}