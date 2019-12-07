package com.mktiti.pockethitler.view.phase

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mktiti.pockethitler.R
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.PhaseResult
import com.mktiti.pockethitler.game.data.PhaseState
import com.mktiti.pockethitler.util.Bi
import com.mktiti.pockethitler.util.DefaultResourceManager
import com.mktiti.pockethitler.view.card.ArticleUiProvider
import com.mktiti.pockethitler.view.card.DefaultArticleProvider
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

abstract class ArticleDiscardFragment(
    private val titleRes: Int,
    protected val cards: List<Article>
) : Fragment() {

    protected val selectIndices = mutableSetOf<Int>()

    private lateinit var okButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        val articleProvider = DefaultArticleProvider.forSize(320, 500) { DefaultResourceManager(resources) }

        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            lparams(matchParent, matchParent)

            textView(titleRes) {
                textSize = 20F
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, 50, 0, 50)
            }

            verticalLayout {
                linearLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
                    cards.forEachIndexed { index, article ->
                        articleHolder(articleProvider, article, index)
                    }
                }.lparams(width = wrapContent)

                okButton = button(R.string.ok) {
                    isEnabled = false
                    setOnClickListener {
                        onOk()
                    }
                }.lparams(width = matchParent)
            }.lparams(width = wrapContent) {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
    }.view

    protected abstract fun onOk()

    private fun ViewManager.articleHolder(articleProvider: ArticleUiProvider, article: Article, index: Int) {
        frameLayout {
            padding = 10
            imageView {
                imageBitmap = articleProvider.article(article)
            }.setOnClickListener {
                if (selectIndices.remove(index)) {
                    this.backgroundColor = Color.TRANSPARENT
                } else if (selectIndices.size < cards.size - 1) {
                    selectIndices += index
                    this.backgroundColor = Color.GREEN
                }

                okButton.isEnabled = (selectIndices.size == cards.size - 1)
            }
        }
    }

}

class PresidentDiscardFragment(
    state: PhaseState.PresidentDiscardState,
    private val resultCallback: (PhaseResult.PresidentDiscardResult) -> Unit
) : ArticleDiscardFragment(R.string.articles_to_pass, state.cards.toList()) {

    override fun onOk() {
        if (selectIndices.size == 2) {
            val indices = selectIndices.sorted()

            val toPass = Bi(cards[indices[0]], cards[indices[1]])
            val toDiscard = cards[(setOf(0, 1, 2) - selectIndices).first()]

            resultCallback(PhaseResult.PresidentDiscardResult(toPass, toDiscard))
        }
    }

}

class ChancellorDiscardFragment(
    state: PhaseState.ChancellorDiscardState,
    private val resultCallback: (PhaseResult.ChancellorDiscardResult) -> Unit
) : ArticleDiscardFragment(R.string.articles_to_play, state.cards.toList()) {

    override fun onOk() {
        selectIndices.firstOrNull()?.let { selected ->
            val toPlace = cards[selected]
            val toDiscard = cards[1 - selected]

            resultCallback(PhaseResult.ChancellorDiscardResult(placed = toPlace, discarded = toDiscard))
        }
    }

}