package com.mktiti.pockethitler.view

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mktiti.pockethitler.game.data.Player
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

private class PlayerView : AnkoComponent<ViewGroup> {

    companion object {
        val nameId = 1
        val selectButtonId = 2
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        linearLayout {
            textView { id = nameId }
            button("Select") { id = selectButtonId }
        }
    }

}

class PlayerAdapter(
    private val allowedPlayers: List<Player>,
    private val selectCallback: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(playerView: View): RecyclerView.ViewHolder(playerView) {

        var nameView: TextView = playerView.findViewById(PlayerView.nameId)
        var selectButtonId: Button = playerView.findViewById(PlayerView.selectButtonId)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = PlayerViewHolder(PlayerView().createView(AnkoContext.Companion.create(parent.context, parent)))

    override fun getItemCount() = allowedPlayers.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = allowedPlayers[position]
        holder.apply {
            nameView.text = player.name
            selectButtonId.setOnClickListener {
                selectCallback(player)
            }
        }
    }
}

class PlayerSelectView(
    private val message: String,
    private val subtitle: String,
    private val allowedPlayers: List<Player>,
    private val selectCallback: (Player) -> Unit
) : AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        verticalLayout {
            textView(message) {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            textView(subtitle) {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = PlayerAdapter(allowedPlayers, selectCallback)
            }
        }
    }

}