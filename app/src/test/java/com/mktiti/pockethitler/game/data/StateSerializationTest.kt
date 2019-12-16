package com.mktiti.pockethitler.game.data

import com.mktiti.pockethitler.util.Bi
import com.mktiti.pockethitler.util.NopResourceManager
import com.mktiti.pockethitler.util.Tri
import org.junit.Assert.assertEquals
import org.junit.Test

class StateSerializationTest {

    companion object {
        val initState = initNewState((1..7).map { "Player #it" }, NopResourceManager)

        val testPlayers = (0..20).map {
            Player(
                name = "Player #$it name",
                identity = when (it % 3) {
                    0 -> PlayerIdentity.LIBERAL
                    1 -> PlayerIdentity.FASCIST
                    else -> PlayerIdentity.HITLER
                }
            )
        }

        fun assertStaysSame(state: GameState) {
            assertEquals(
                state,
                GameState.parse(state.stringify())
            )
        }
    }

    @Test
    fun `test init`() {
        assertStaysSame(initState)
    }

    @Test
    fun `test identity info`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.IdentityInfoState(
                    identities = testPlayers,
                    fascistNames = (0..5).map { "Fash #$it" },
                    hitlerName = "Hitler name",
                    startPhase = PhaseState.ChancellorSelectState(testPlayers)
                )
            )
        )
    }

    @Test
    fun `test chancellor select`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.ChancellorSelectState(
                    selectablePlayers = testPlayers
                )
            )
        )
    }

    @Test
    fun `test chancellor select no candidates`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.ChancellorSelectState(
                    selectablePlayers = emptyList()
                )
            )
        )
    }

    @Test
    fun `test vote select`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.VoteState(
                    candidates = Government(
                        Player("asd", PlayerIdentity.LIBERAL),
                        Player("dsa", PlayerIdentity.FASCIST)
                    ),
                    votes = testPlayers.mapIndexed { i, it -> it to (i % 2 == 0)  },
                    futureVotes = testPlayers
                )
            )
        )
    }

    @Test
    fun `test vote select no votes`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.VoteState(
                    candidates = Government(
                        Player("asd", PlayerIdentity.LIBERAL),
                        Player("dsa", PlayerIdentity.FASCIST)
                    ),
                    votes = emptyList(),
                    futureVotes = testPlayers
                )
            )
        )
    }

    @Test
    fun `test vote select no future`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.VoteState(
                    candidates = Government(
                        Player("asd", PlayerIdentity.LIBERAL),
                        Player("dsa", PlayerIdentity.FASCIST)
                    ),
                    votes = testPlayers.mapIndexed { i, it -> it to (i % 2 == 0)  },
                    futureVotes = emptyList()
                )
            )
        )
    }

    @Test
    fun `test president discard`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.PresidentDiscardState(
                    cards = Tri(Article.LIBERAL, Article.FASCIST, Article.LIBERAL)
                )
            )
        )
    }

    @Test
    fun `test chancellor discard`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.ChancellorDiscardState(
                    cards = Bi(Article.LIBERAL, Article.FASCIST),
                    canVeto = true
                )
            )
        )
    }

    @Test
    fun `test veto confirm`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.VetoConfirmState(
                    cards = Bi(Article.FASCIST, Article.LIBERAL)
                )
            )
        )
    }

    @Test
    fun `test won`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.GameWon(
                    winner = Party.LIBERAL
                )
            )
        )
    }

    @Test
    fun `test peek cards`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.PresidentialPowerUseState.PeekCardsState(
                    cards = Tri(Article.LIBERAL, Article.FASCIST, Article.LIBERAL)
                )
            )
        )
    }

    @Test
    fun `test snap select`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.PresidentialPowerUseState.SnapSelectState(
                    selectablePlayers = testPlayers
                )
            )
        )
    }

    @Test
    fun `test investigate select`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.PresidentialPowerUseState.CheckPartySelectState(
                    selectablePlayers = testPlayers
                )
            )
        )
    }

    @Test
    fun `test kill select`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.PresidentialPowerUseState.KillState(
                    selectablePlayers = testPlayers
                )
            )
        )
    }

    @Test
    fun `test envelope`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState,
                phaseState = PhaseState.EnvelopeState(
                    message = "Envelope message",
                    nestedState = PhaseState.VoteState(
                        candidates = Government(
                            Player("asd", PlayerIdentity.LIBERAL),
                            Player("dsa", PlayerIdentity.FASCIST)
                        ),
                        votes = testPlayers.mapIndexed { i, it -> it to (i % 2 == 0)  },
                        futureVotes = emptyList()
                    )
                )
            )
        )
    }

    @Test
    fun `test player state`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    playersState = PlayersState(
                        testPlayers.mapIndexed { i, p -> PlayerData(p, i % 2 == 0) }
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }

    @Test
    fun `test election state no last elected`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    electionState = initState.tableState.electionState.copy(
                        electedStatus = ElectedStatus.VotingState.NoneElectedYet(
                            presidentCandidate = Player("asd", PlayerIdentity.FASCIST)
                        )
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }

    @Test
    fun `test election state in session`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    electionState = initState.tableState.electionState.copy(
                        electedStatus = ElectedStatus.InSession(
                            government = Government(
                                Player("asd", PlayerIdentity.LIBERAL),
                                Player("dsa", PlayerIdentity.FASCIST)
                            ),
                            jumpedFrom = null
                        )
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }

    @Test
    fun `test election state in session snap`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    electionState = initState.tableState.electionState.copy(
                        electedStatus = ElectedStatus.InSession(
                            government = Government(
                                Player("asd", PlayerIdentity.LIBERAL),
                                Player("dsa", PlayerIdentity.FASCIST)
                            ),
                            jumpedFrom = Player("xyz", PlayerIdentity.LIBERAL)
                        )
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }

    @Test
    fun `test election state out of session`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    electionState = initState.tableState.electionState.copy(
                        electedStatus = ElectedStatus.VotingState.OutOfSession(
                            lastElected = Government(
                                Player("asd", PlayerIdentity.LIBERAL),
                                Player("dsa", PlayerIdentity.FASCIST)
                            ),
                            presidentCandidate = Player("sss", PlayerIdentity.LIBERAL)
                        )
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }

    @Test
    fun `test election state snap election`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    electionState = initState.tableState.electionState.copy(
                        electedStatus = ElectedStatus.VotingState.SnapElection(
                            lastElected = Government(
                                Player("asd", PlayerIdentity.LIBERAL),
                                Player("dsa", PlayerIdentity.FASCIST)
                            ),
                            jumpedFrom = Player("sss", PlayerIdentity.LIBERAL),
                            presidentCandidate = Player("ooo", PlayerIdentity.LIBERAL)
                        )
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }


    @Test
    fun `test deck state`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    deckState = DeckState(
                        drawStack = listOf(Article.LIBERAL, Article.FASCIST, Article.FASCIST, Article.LIBERAL),
                        discardStack = listOf(Article.LIBERAL, Article.FASCIST)
                    )
                ),
                phaseState = initState.phaseState
            )
        )
    }

    @Test
    fun `test board state`() {
        assertStaysSame(
            GameState(
                tableState = initState.tableState.copy(
                    boardsState = BoardsState(5, 20)
                ),
                phaseState = initState.phaseState
            )
        )
    }

}