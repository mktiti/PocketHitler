package com.mktiti.pockethitler.view.board

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.data.TableState
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.custom.customView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class BoardFragment : Fragment() {

    lateinit var deckView: DeckView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)
            weightSum = 2F

            linearLayout {

            }.lparams(matchParent, matchParent, 1F)

            linearLayout {
                deckView = ankoView(::DeckView, 0) {}
            }.lparams(matchParent, matchParent, 1F)

        }
    }.view

    fun setState(tableState: TableState) {
        deckView.setState(tableState.deckState)
    }

}