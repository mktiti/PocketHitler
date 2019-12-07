package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseResult.IdentificationDone
import com.mktiti.pockethitler.game.data.PhaseResult.PlainStateResult
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import com.mktiti.pockethitler.game.data.PhaseState.IdentityInfoState
import com.mktiti.pockethitler.game.data.PlayerIdentity.*
import com.mktiti.pockethitler.util.DefaultResourceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.support.v4.UI

class IdentityFragment(
    private val state: IdentityInfoState,
    private val resultCallback: (PhaseResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val resourceManager = DefaultResourceManager(resources)
        val player = state.identities.first()

        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            lparams(matchParent, matchParent)

            textView(
                when (player.identity) {
                    LIBERAL -> R.string.you_are_lib
                    FASCIST -> R.string.you_are_fash
                    HITLER -> R.string.you_are_hitler
                }
            ) {
                textSize = 25F
                textAlignment = TextureView.TEXT_ALIGNMENT_CENTER
            }.lparams(matchParent, wrapContent)

            fun row(pos: String, name: String) {
                linearLayout {
                    weightSum = 2F
                    gravity = Gravity.CENTER_HORIZONTAL
                    textView(pos) {
                        textAlignment = TextureView.TEXT_ALIGNMENT_VIEW_END
                        setPadding(0, 10, 30, 10)
                    }.lparams(width = 0, height = wrapContent, weight = 1F)
                    textView(name).lparams(width = 0, height = wrapContent, weight = 1F)
                }
            }

            val unknown = resourceManager[R.string.unknown_identity]
            row(resourceManager[R.string.id_of_hitler], if (player.identity != LIBERAL) state.hitlerName else unknown)
            state.fascistNames.forEachWithIndex { i, fascist ->
                val show = player.identity != LIBERAL && (player.identity == FASCIST || state.fascistNames.size == 1)
                row(resourceManager.format(R.string.id_of_fash, i + 1), if (show) fascist else unknown)
            }

            button(R.string.ok).lparams(width = wrapContent, height = wrapContent).setOnClickListener {
                val restPlayers = state.identities.drop(1)
                resultCallback(
                    if (restPlayers.isEmpty()) {
                        IdentificationDone
                    } else {
                        PlainStateResult(
                            EnvelopeState(
                                message = resourceManager.format(R.string.env_secret_id, restPlayers.first().name),
                                nestedState = state.copy(identities = restPlayers)
                            )
                        )
                    }
                )
            }
        }
    }.view

}