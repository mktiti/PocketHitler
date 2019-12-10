package com.mktiti.pockethitler

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mktiti.pockethitler.game.data.initNewState
import com.mktiti.pockethitler.util.DefaultResourceManager
import org.jetbrains.anko.*

private const val PLAYERS_KEY = "selectablePlayers"

class StartActivity : AppCompatActivity() {

    //private val selectablePlayers = mutableListOf<String>()
    //private val players = (0 .. 7).map { "Player #$it" }
    private val players = listOf(
        "Titi", "Peti", "Máté", "Benő", "Juhász", "Patrik", "Roli"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linearLayout {
            button {
                textResource = R.string.start
            }.setOnClickListener {
                startActivity(intentFor<BoardActivity>(
                    STATE_KEY to initNewState(players, DefaultResourceManager(resources)).stringify()
                ).singleTop())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putStringArrayList(PLAYERS_KEY, ArrayList(players))
    }
}
