package com.mktiti.pockethitler.view.phase

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.PhaseResult.PlainStateResult
import com.mktiti.pockethitler.game.data.PhaseState.EnvelopeState
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class EnvelopeFragment(
    private val state: EnvelopeState,
    private val resultCallback: (PlainStateResult) -> Unit
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)

            textView(state.message) {
                textSize += 3F
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            button(R.string.env_show).lparams(width = wrapContent, height = wrapContent).setOnClickListener {
                resultCallback(PlainStateResult(state.nestedState))
            }
        }
    }.view

}