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
    private lateinit var miscView: MiscView
    private lateinit var fascistBoardView: FascistBoardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        verticalLayout {
            setPadding(10, 10, 10, 10)
            gravity = Gravity.CENTER
            lparams(matchParent, matchParent)
            weightSum = 9F

            linearLayout {
                fascistBoardView = ankoView(::FascistBoardView, 0) {}
            }.lparams(matchParent, 0, 4F)

            miscView = ankoView(::MiscView, 0) {}.lparams(matchParent, 0, 1F)

            linearLayout {
                weightSum = 6F

                deckView = ankoView(::DeckView, 0) {}
                    .lparams(width = 0, height = matchParent, weight = 1F)

                liberalBoardView = ankoView(::LiberalBoardView, 0) {}
                    .lparams(width = 0, height = matchParent, weight = 5F)
            }.lparams(matchParent, 0, 4F)

        }
    }.view

    fun setConfig(powers: List<PresidentialAction?>) {
        fascistBoardView.setBoard(powers)
    }

    fun setState(tableState: TableState) {
        deckView.setState(tableState.deckState)
        liberalBoardView.setState(tableState.boardsState.liberalCards)
        fascistBoardView.setState(tableState.boardsState.fascistCards)

        // miscView.setPresident(tableState.electionState.)
        // miscView.setPresident(tableState.playersState.)
        miscView.setFailedElections(tableState.electionState.failedElections)
    }

}