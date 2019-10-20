package com.mktiti.pockethitler

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mktiti.pockethitler.game.data.*
import kotlinx.serialization.json.Json
import org.jetbrains.anko.button
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.singleTop

private const val PLAYERS_KEY = "players"

class StartActivity : AppCompatActivity() {

    //private val players = mutableListOf<String>()
    private val players = (0 .. 7).map { "Player #$it" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linearLayout {
            button("Start").setOnClickListener {
                startActivity(intentFor<BoardActivity>(
                    STATE_KEY to initNewState(players).stringify()
                ).singleTop())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putStringArrayList(PLAYERS_KEY, ArrayList(players))
    }
}
