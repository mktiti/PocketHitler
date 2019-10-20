package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import com.mktiti.pockethitler.game.data.DeckState
import org.jetbrains.anko.textView

class DeckView(context: Context) : LinearLayout(context) {

    private val remainingText: TextView

    init {
        orientation = VERTICAL
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, 1F)
        gravity = Gravity.CENTER

        textView("Remaining:")
        remainingText = textView {
            textSize += 10
        }

    }

    fun setState(deckState: DeckState) {
        remainingText.text = deckState.drawStack.size.toString()
    }

}