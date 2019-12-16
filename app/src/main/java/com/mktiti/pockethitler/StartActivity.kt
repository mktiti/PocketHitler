package com.mktiti.pockethitler

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mktiti.pockethitler.game.data.GameInfo
import com.mktiti.pockethitler.game.data.identificationState
import com.mktiti.pockethitler.util.DefaultResourceManager
import com.mktiti.pockethitler.util.FileGameStore
import com.mktiti.pockethitler.util.ResourceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

private class GameView : AnkoComponent<ViewGroup> {

    companion object {
        const val dateId = 1
        const val descriptionId = 2
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        verticalLayout {

            setPadding(20, 20, 20, 20)

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                val color = resources.getColor(R.color.savedGameContainer, null)
                setStroke(2, color)
                setColor(color)
                padding = 25
                cornerRadius = 25F
            }

            textView {
                id = dateId
                textSize = 20F
            }.lparams(width = matchParent, height = wrapContent)
            textView("=") {
                id = descriptionId
                textSize = 15F
            }.lparams(width = matchParent, height = wrapContent)

            layoutParams = RecyclerView.LayoutParams(matchParent, wrapContent)
        }
    }

}

private class GameAdapter(
    games: List<GameInfo> = emptyList(),
    private val onItemClick: (View) -> Unit,
    private val context: Context,
    private val resourceManager: ResourceManager
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private val games: MutableList<GameInfo> = games.toMutableList()

    inner class GameViewHolder(playerView: View): RecyclerView.ViewHolder(playerView) {

        var dateView: TextView = playerView.findViewById(GameView.dateId)

        var descriptionView: TextView = playerView.findViewById(GameView.descriptionId)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = GameView().createView(AnkoContext.Companion.create(parent.context, parent))
        view.setOnClickListener {
            onItemClick(view)
        }

        return GameViewHolder(view)
    }

    override fun getItemCount() = games.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]

        val playerState = game.state.tableState.playersState
        val playerCount = playerState.players.size
        val deadCount = playerState.players.count { !it.alive }

        holder.apply {
            dateView.text = DateUtils.getRelativeDateTimeString(
                context,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(game.creationDate), ZoneOffset.UTC).atZone(
                    ZoneId.systemDefault()).toInstant().toEpochMilli(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0
            )
            descriptionView.text = if (deadCount == 0) {
                resourceManager.format(R.string.player_count_no_dead, playerCount)
            } else {
                resourceManager.format(R.string.player_count, playerCount, deadCount)
            }
        }
    }

    fun setGames(games: List<GameInfo>) {
        this.games.clear()
        this.games.addAll(games)
        notifyDataSetChanged()
    }

    operator fun get(childItemId: Int): GameInfo? = games.getOrNull(childItemId)

}

class StartActivity : AppCompatActivity() {

    private lateinit var resourceManager: ResourceManager

    private lateinit var savedView: RecyclerView
    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resourceManager = DefaultResourceManager(resources)

        gameAdapter = GameAdapter(
            games = emptyList(),
            onItemClick = this::onSavedOpen,
            resourceManager = resourceManager,
            context = this
        )

        verticalLayout {
            padding = dip(20)

            button(R.string.start_new) {
                padding = dip(20)
                backgroundColorResource = R.color.newGameContainer
            }.setOnClickListener {
                startActivity(intentFor<PlayerSetupActivity>().singleTop())
            }

            textView(R.string.saved_games) {
                setPadding(0, 25, 0, 15)
                textSize = 25F
            }

            savedView = recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = gameAdapter

                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View,  parent: RecyclerView, state: RecyclerView.State) {
                        with(outRect) {
                            if (parent.getChildAdapterPosition(view) != 0) {
                                top = 20
                            }
                        }
                    }
                })
            }.lparams(width = matchParent, height = matchParent)
        }
    }

    override fun onResume() {
        super.onResume()

        gameAdapter.setGames(FileGameStore(this).loadGames())
    }

    private fun onSavedOpen(gameView: View) {
        val game = gameAdapter[savedView.getChildAdapterPosition(gameView)] ?: return
        val players = game.state.tableState.playersState.players

        alert {
            var showIdsCheck: (() -> Boolean)? = null
            titleResource = R.string.open_saved
            customView {
                verticalLayout {
                    padding = dip(20)
                    textView(R.string.players_title) {
                        setTypeface(typeface, Typeface.BOLD)
                    }

                    players.forEach { p ->
                        textView {
                            text = if (p.alive) {
                                p.player.name
                            } else {
                                resourceManager.format(R.string.player_dead, p.player.name)
                            }
                        }
                    }

                    checkBox(R.string.show_identities) {
                        isChecked = true
                        showIdsCheck = this::isChecked
                    }
                }
            }
            isCancelable = true
            cancelButton {
                it.dismiss()
            }
            okButton {
                startSaved(game, showIdsCheck?.invoke() ?: true)
                it.dismiss()
            }
            show()
        }
    }

    private fun startSaved(info: GameInfo, showIds: Boolean) {
        val wrappedState = if (showIds) {
            with(info.state) {
                val players = tableState.playersState.players.filter { it.alive }.map { it.player }
                val phaseState = identificationState(players, this.phaseState, resourceManager)
                copy(phaseState = phaseState)
            }
        } else {
            info.state
        }

        startActivity(intentFor<BoardActivity>(
            ID_KEY to info.creationDate,
            STATE_KEY to wrappedState.stringify()
        ).singleTop())
    }

}
