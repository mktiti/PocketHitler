package com.mktiti.pockethitler.game.manager

import com.mktiti.pockethitler.Tri
import com.mktiti.pockethitler.game.data.Article
import com.mktiti.pockethitler.game.data.DeckState
import com.mktiti.pockethitler.repeatList
import java.util.*

class ArticleDeck(drawStack: List<Article>, discardStack: List<Article>) {

    private val drawStack: MutableList<Article> = LinkedList(drawStack)
    private val discardStack: MutableList<Article> = LinkedList(discardStack)

    companion object {
        fun newDeck(fascistCount: Int = 11, liberalCount: Int = 6): DeckState
            = DeckState(
                drawStack = (Article.FASCIST.repeatList(fascistCount) + Article.LIBERAL.repeatList(liberalCount)).shuffled(),
                discardStack = emptyList()
            )
    }

    constructor(state: DeckState) : this(state.drawStack, state.discardStack)

    val state: DeckState
        get() = DeckState(drawStack, discardStack)

    val drawCount: Int
        get() = drawStack.size

    val discardCount: Int
        get() = discardStack.size

    private fun draw(): Article = drawStack.removeAt(0)

    fun drawThree(): Tri<Article> = Tri(draw(), draw(), draw())

    fun peekThree(): Tri<Article> = Tri(drawStack[0], drawStack[1], drawStack[2])

    fun discard(article: Article) {
        discardStack += article
    }

    fun shuffleIfNeeded(): Boolean = if (drawStack.size < 3) {
        drawStack += discardStack
        drawStack.shuffle()
        discardStack.clear()
        true
    } else {
        false
    }

}