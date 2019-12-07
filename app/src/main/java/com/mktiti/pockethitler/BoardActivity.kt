package com.mktiti.pockethitler

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.GameState
import com.mktiti.pockethitler.game.data.TableState
import com.mktiti.pockethitler.game.manager.FascistBoard
import com.mktiti.pockethitler.game.manager.GameEngine
import com.mktiti.pockethitler.game.manager.PlayerCount
import com.mktiti.pockethitler.util.DefaultResourceManager
import com.mktiti.pockethitler.view.board.BoardFragment
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textResource

const val STATE_KEY = "game-state"
private const val DYN_FRAG_TAG = "dynamic-fragment"

class BoardActivity : AppCompatActivity() {

    private lateinit var boardHolder: View
    private lateinit var boardFragment: BoardFragment

    private lateinit var dynamicHolder: View
    private lateinit var switchButton: Button

    private var boardShown = true

    private lateinit var engine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linearLayout {
            weightSum = 5F

            boardHolder = linearLayout {
                id = ViewCompat.generateViewId()
            }.lparams(width = 0, height = MATCH_PARENT, weight = 4f)

            dynamicHolder = linearLayout {
                id = ViewCompat.generateViewId()
                visibility = View.GONE
            }.lparams(width = 0, height = MATCH_PARENT, weight = 4f)

            switchButton = button {
                textResource = R.string.switch_frag_view
                setOnClickListener { switchFrags() }
            }.lparams(width = 0, height = MATCH_PARENT, weight = 1F)
        }

        boardFragment = BoardFragment()
        supportFragmentManager.beginTransaction().apply {
            add(boardHolder.id, boardFragment)
        }.commit()

        val stateJson = savedInstanceState?.getString(STATE_KEY) ?:
                    intent.extras?.getString(STATE_KEY) ?:
                    throw IllegalArgumentException("Game state missing!")

        val state = GameState.parse(stateJson)
        engine = GameEngine(
            state,
            this::showMessage,
            this::onPhaseFragment,
            DefaultResourceManager(resources)
        )
    }

    override fun onStart() {
        super.onStart()
        engine.start()

        val playerCount = engine.currentState().tableState.playersState.players.size
        boardFragment.setConfig(FascistBoard.actions(PlayerCount.of(playerCount)))
    }

    private fun switchFrags() {
        boardShown = !boardShown
        updateFrags()
    }

    private fun updateFrags() {
        boardHolder.visibility = if (boardShown) View.VISIBLE else View.GONE
        dynamicHolder.visibility = if (boardShown) View.GONE else View.VISIBLE
    }

    private fun showMessage(message: String) {
        Log.i("Game Message", message)
    }

    private fun onPhaseFragment(tableState: TableState, phaseFragment: Fragment?) {
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.findFragmentByTag(DYN_FRAG_TAG)?.let(this::remove)

            phaseFragment?.let {
                add(dynamicHolder.id, it, DYN_FRAG_TAG)
            }

            switchButton.isEnabled = phaseFragment != null
            boardShown = phaseFragment == null
            updateFrags()
        }.commit()

        boardFragment.setState(tableState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(STATE_KEY, engine.currentState().stringify())
    }
}
