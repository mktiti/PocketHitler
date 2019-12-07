package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.textView

class MiscView(context: Context) : LinearLayout(context) {

    private val chancellorView: TextView
    private val electionTracker: TextView
    private val presidentView: TextView

    init {
        weightSum = 3F
        gravity = Gravity.CENTER_VERTICAL

        chancellorView = textView {
            textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            layoutParams = generateDefaultLayoutParams().apply {
                width = 0
                weight = 1F
            }
        }
        electionTracker = textView {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            layoutParams = generateDefaultLayoutParams().apply {
                width = 0
                weight = 1F
            }
        }
        presidentView = textView {
            textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            layoutParams = generateDefaultLayoutParams().apply {
                width = 0
                weight = 1F
            }
        }

        setChancellor(null)
        setPresident(null)
        setFailedElections(0)
    }

    fun setChancellor(name: String?) {
        chancellorView.text = if (name == null) {
            "No Chancellor"
        } else {
            "Chancellor: $name"
        }
    }

    fun setPresident(name: String?) {
        presidentView.text = if (name == null) {
            "No President"
        } else {
            "President: $name"
        }
    }

    fun setFailedElections(count: Int) {
        electionTracker.text = "Election tracker: " +
        (
                generateSequence { "○" }.take(count) +
                listOf("●") +
                generateSequence { "○" }.take(2 - count)

        ).joinToString(" - ")
    }

}
