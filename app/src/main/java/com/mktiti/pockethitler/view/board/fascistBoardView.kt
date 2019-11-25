package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import com.mktiti.pockethitler.game.PresidentialAction
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class FascistBoardView(context: Context) : LinearLayout(context) {

    private class CardHolder(
        private val actionView: TextView,
        private val holderView: TextView
    ) {
        init {
            setCard(false)
        }

        fun setCard(occupied: Boolean) {
            holderView.text = if (occupied) {
                "PLACED"
            } else {
                "X"
            }
        }

        fun setAction(action: PresidentialAction?, last: Boolean) {
            actionView.text = when {
                action != null -> action.name
                last -> "FASCIST WIN"
                else -> "FASCIST"
            }
        }
    }

    private val holders: List<CardHolder>

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 1F)
        gravity = Gravity.CENTER

        holders = ArrayList(6)
        repeat(6) {
            verticalLayout {
                gravity = Gravity.CENTER

                holders += CardHolder(
                    actionView = textView(),
                    holderView = textView()
                )
            }
        }
    }

    fun setBoard(fascistPowers: List<PresidentialAction?>) {
        holders.zip(fascistPowers).forEachIndexed { i, (holder, action) ->
            holder.setAction(action, i == holders.size - 1)
        }
    }

    fun setState(cardCount: Int) {
        holders.take(cardCount).forEach {
            it.setCard(true)
        }
    }

}