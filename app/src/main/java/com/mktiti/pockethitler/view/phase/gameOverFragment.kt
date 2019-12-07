package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.Party
import com.mktiti.pockethitler.game.data.PhaseState
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class GameOverFragment(
    private val state: PhaseState.GameWon
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView(
                when (state.winner) {
                    Party.LIBERAL -> R.string.liberals
                    Party.FASCIST -> R.string.fascists
                }
            )
            textView(R.string.won_the_game)
        }
    }.view

}