package com.mktiti.pockethitler.view.board

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.util.DefaultResourceManager
import com.mktiti.pockethitler.util.ResourceManager
import org.jetbrains.anko.textView

class MiscView(context: Context) : LinearLayout(context) {

    private val resourceManager: ResourceManager = DefaultResourceManager(context.resources)

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
            resourceManager[R.string.no_chancellor]
        } else {
            resourceManager.format(R.string.chancellor_is, name)
        }
    }

    fun setPresident(name: String?) {
        presidentView.text = if (name == null) {
            resourceManager[R.string.no_president]
        } else {
            resourceManager.format(R.string.president_is, name)
        }
    }

    fun setFailedElections(count: Int) {
        electionTracker.text = resourceManager.resources(R.array.election_tracker).getOrNull(count) ?: ""
    }

}
