package com.mktiti.pockethitler

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.manager.GameEngine
import com.mktiti.pockethitler.game.data.GameState
import com.mktiti.pockethitler.view.BoardFragment
import org.jetbrains.anko.linearLayout

const val STATE_KEY = "game-state"
private const val DYN_FRAG_TAG = "dynamic-fragment"

class BoardActivity : AppCompatActivity() {

    private lateinit var boardHolder: View
    private lateinit var dynamicHolder: View

    private lateinit var engine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linearLayout {
            weightSum = 2f

            boardHolder = linearLayout {
                id = ViewCompat.generateViewId()
            }.lparams(-1, -1, 1f)

            dynamicHolder = linearLayout {
                id = ViewCompat.generateViewId()
            }.lparams(-1, -1, 1f)
        }

        supportFragmentManager.beginTransaction().apply {
            add(boardHolder.id, BoardFragment())
        }.commit()

        val stateJson = savedInstanceState?.getString(STATE_KEY) ?:
                    intent.extras?.getString(STATE_KEY) ?:
                    throw IllegalArgumentException("Game state missing!")

        val state = GameState.parse(stateJson)
        engine = GameEngine(
            state,
            this::showMessage,
            this::onPhaseFragment
        )
    }

    private fun showMessage(message: String) {
        Log.i("Game Message", message)
    }

    private fun onPhaseFragment(phaseFragment: Fragment?) {
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.findFragmentByTag(DYN_FRAG_TAG)?.let(this::remove)
            phaseFragment?.let {
                add(dynamicHolder.id, phaseFragment, DYN_FRAG_TAG)
            }
        }.commit()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(STATE_KEY, engine.currentState().stringify())
    }
}
