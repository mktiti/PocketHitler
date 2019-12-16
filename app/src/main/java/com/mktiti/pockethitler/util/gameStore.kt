package com.mktiti.pockethitler.util

import android.content.Context
import android.util.Log
import com.mktiti.pockethitler.game.data.GameInfo
import com.mktiti.pockethitler.game.data.GameState
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonException
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

interface GameStore {

    fun loadGames(): List<GameInfo>

    fun saveGame(id: Long, state: GameState) = saveGame(id, state.stringify())

    fun saveGame(id: Long, state: String)

}

class FileGameStore(
    private val context: Context
) : GameStore {

    companion object {
        private const val saveDir = "saves"

        private const val logTag = "FileGameStore"
    }

    override fun loadGames(): List<GameInfo> {
        return (File(context.filesDir, saveDir).listFiles()?.toList() ?: emptyList<File>()).mapNotNull { file ->
            if (file == null) return@mapNotNull null

            try {
                val timestamp = file.nameWithoutExtension.toLongOrNull() ?: return@mapNotNull null
                val gameState = GameState.parse(file.readText())

                GameInfo(timestamp, gameState)
            } catch (se: SerializationException) {
                Log.e(logTag, "Failed to deserialize saved game", se)
                null
            } catch (jse: JsonException) {
                Log.e(logTag, "Failed to deserialize saved game", jse)
                null
            }
        }
    }

    override fun saveGame(id: Long, state: String) {
        val saveDirFile = File(context.filesDir, saveDir)
        if (!saveDirFile.exists()) {
            saveDirFile.mkdir()
        }

        FileWriter(File(saveDirFile, id.toString())).use { fw ->
            BufferedWriter(fw).use { writer ->
                writer.write(state)
            }
        }
    }

}
