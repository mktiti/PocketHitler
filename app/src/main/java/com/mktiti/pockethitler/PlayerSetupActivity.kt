package com.mktiti.pockethitler

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mktiti.pockethitler.game.data.initNewState
import com.mktiti.pockethitler.util.DefaultResourceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

private class PlayerView : AnkoComponent<ViewGroup> {

    companion object {
        const val nameId = 1
        const val grabId = 2
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        linearLayout {
            weightSum = 10F
            textView {
                id = nameId
                textSize = 20F
                gravity = Gravity.CENTER_VERTICAL
            }.lparams(width = 0, height = wrapContent, weight = 7F)
            textView("=") {
                id = grabId
                textSize = 35F
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            }.lparams(width = 0, height = wrapContent, weight = 3F)

            layoutParams = RecyclerView.LayoutParams(matchParent, wrapContent)
        }
    }

}

private class PlayerTouchCallback(private val adapter: PlayerAdapter) : ItemTouchHelper.Callback() {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.move(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.remove(viewHolder.adapterPosition)
    }

    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        )
    }
}

private class PlayerAdapter(
    private val changeCallback: (List<String>) -> Unit,
    private val grabStartCallback: (PlayerViewHolder) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    private val players: MutableList<String> = LinkedList(listOf("Asd", "Titi", "Hello", "General Kenobi"))

    inner class PlayerViewHolder(playerView: View): RecyclerView.ViewHolder(playerView) {

        var nameView: TextView = playerView.findViewById(PlayerView.nameId)

        var grabView: View = playerView.findViewById(PlayerView.grabId)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(PlayerView().createView(AnkoContext.Companion.create(parent.context, parent))).apply {
            grabView.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    grabStartCallback(this)
                }

                return@setOnTouchListener true
            }
        }
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.apply {
            nameView.text = players[position]
        }
    }

    fun move(from: Int, to: Int) {
        Collections.swap(players, from, to)
        notifyItemMoved(from, to)
        changeCallback(state())
    }

    fun remove(index: Int) {
        players.removeAt(index)
        notifyItemRemoved(index)
        changeCallback(state())
    }

    fun tryAdd(player: String): Boolean {
        return if (player.isNotEmpty() && player !in players) {
            players += player
            notifyItemInserted(players.size - 1)
            changeCallback(state())
            true
        } else {
            false
        }
    }

    fun state(): List<String> = players

}

class PlayerSetupActivity : AppCompatActivity() {

    private companion object {
        private val playerRange = (5..10)
    }

    private lateinit var startButton: Button
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerAdapter = PlayerAdapter(this::onPlayersChange, this::startPlayerDrag)

        relativeLayout {
            padding = dip(15)

            layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)

            val titleRow = linearLayout {
                id = 10
                weightSum = 10F
                textView(R.string.players_title) {
                    textSize = 30F
                }.lparams(width = 0, height = wrapContent, weight = 7F)

                startButton = button(R.string.start_players).apply {
                    lparams(width = 0, height = wrapContent, weight = 3F)
                    setOnClickListener {

                        val intent = intentFor<BoardActivity>(
                            ID_KEY to LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(),
                            STATE_KEY to initNewState(playerAdapter.state(), DefaultResourceManager(resources)).stringify()
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)

                    }
                }
            }.lparams(width = matchParent, height = wrapContent) {
                alignParentTop()
            }

            val bottomRow = linearLayout {
                id = 1
                weightSum = 10F

                val newName = editText {
                    hintResource = R.string.name_hint
                    inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                }.lparams(width = 0, height = wrapContent, weight = 7F)

                button("+").lparams(width = 0, height = wrapContent, weight = 3F).setOnClickListener {
                    if (playerAdapter.tryAdd(newName.text.toString())) {
                        newName.text = null
                    }
                }
            }.lparams(width = matchParent, height = wrapContent) {
                alignParentBottom()
            }

            val rw = recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = playerAdapter
            }.lparams(width = matchParent, height = wrapContent) {
                below(titleRow)
                above(bottomRow)
            }

            touchHelper = ItemTouchHelper(PlayerTouchCallback(playerAdapter)).apply {
                attachToRecyclerView(rw)
            }

            onPlayersChange(playerAdapter.state())
        }
    }

    private fun onPlayersChange(players: List<String>) {
        startButton.isEnabled = players.size in playerRange
    }

    private fun startPlayerDrag(playerView: PlayerAdapter.PlayerViewHolder) {
        touchHelper.startDrag(playerView)
    }

}
