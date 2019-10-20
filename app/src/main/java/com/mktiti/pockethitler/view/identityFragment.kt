package com.mktiti.pockethitler.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PlayerIdentity.*
import com.mktiti.pockethitler.game.data.PhaseState.*
import com.mktiti.pockethitler.game.data.PhaseResult.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.support.v4.UI

class IdentityFragment(
    private val state: IdentityInfoState,
    private val resultCallback: (PhaseResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val player = state.identities[0]

        linearLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            verticalLayout {
                weightSum = 3F

                textView {
                    text = when (player.identity) {
                        LIBERAL -> "You are a boring"
                        FASCIST -> "You are a dirty"
                        HITLER -> "Sig Heil,"
                    }
                }.lparams(weight = 1F)

                textView {
                    text = when (player.identity) {
                        LIBERAL -> "Liberal"
                        FASCIST -> "Fascist"
                        HITLER -> "Hitler"
                    }
                }.lparams(weight = 1F)

            }
            verticalLayout {
                weightSum = state.fascistNames.size + 2F

                textView("Hitler: ${if (player.identity == FASCIST) state.hitlerName else "???"}").lparams(weight = 1F)

                state.fascistNames.forEachWithIndex { i, fascist ->
                    textView("Fascist #${i + 1}: ${if (player.identity == FASCIST) fascist else "???"}").lparams(
                        weight = 1F
                    )
                }
            }

            button("OK").setOnClickListener {
                val restPlayers = state.identities.drop(1)
                resultCallback(if (restPlayers.isEmpty()) {
                    IdentificationDone
                } else {
                    PlainStateResult(EnvelopeState(
                        message = restPlayers[0].name,
                        nestedState = state.copy(identities = restPlayers)
                    ))
                })
            }
        }
    }.view

}