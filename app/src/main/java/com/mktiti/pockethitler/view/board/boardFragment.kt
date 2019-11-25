package com.mktiti.pockethitler.view.board

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.game.PresidentialAction
import com.mktiti.pockethitler.game.data.TableState
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout

class BoardFragment : Fragment() {

    private lateinit var deckView: DeckView
    private lateinit var liberalBoardView: LiberalBoardView
    private lateinit var fascistBoardView: FascistBoardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)
            weightSum = 2F

            linearLayout {
                fascistBoardView = ankoView(::FascistBoardView, 0) {}
            }.lparams(matchParent, matchParent, 1F)

            linearLayout {
                deckView = ankoView(::DeckView, 0) {}

                liberalBoardView = ankoView(::LiberalBoardView, 0) {}
            }.lparams(matchParent, matchParent, 1F)

        }
    }.view

    fun setConfig(powers: List<PresidentialAction?>) {
        fascistBoardView.setBoard(powers)
    }

    fun setState(tableState: TableState) {
        deckView.setState(tableState.deckState)
        liberalBoardView.setState(tableState.boardsState.liberalCards)
        fascistBoardView.setState(tableState.boardsState.fascistCards)
    }

}