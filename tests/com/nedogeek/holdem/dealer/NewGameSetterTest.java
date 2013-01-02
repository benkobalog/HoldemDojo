package com.nedogeek.holdem.dealer;

import com.nedogeek.holdem.GameSettings;
import com.nedogeek.holdem.PlayerStatus;
import com.nedogeek.holdem.gamingStuff.Bank;
import com.nedogeek.holdem.gamingStuff.Desk;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * User: Konstantin Demishev
 * Date: 07.12.12
 * Time: 7:24
 */
public class NewGameSetterTest {
    private NewGameSetter newGameSetter;

    private Desk deskMock;
    private PlayersManager playersManagerMock;
    private Bank bankMock;

    @Before
    public void setUp() throws Exception {
        resetDeskMock();
        resetPlayerManagerMock();
        resetBankMock();

        newGameSetter = new NewGameSetter(deskMock, playersManagerMock, bankMock);

    }

    private void resetBankMock() {
        bankMock = mock(Bank.class);

        when(bankMock.getPlayerBalance(anyInt())).thenReturn(GameSettings.COINS_AT_START);
    }

    private void resetPlayerManagerMock() {
        playersManagerMock = mock(PlayersManager.class);

        setPlayersQuantity(2);
        setDealerNumber(0);

        setPlayerStatus(anyInt(), PlayerStatus.NotMoved);
    }

    private void resetDeskMock() {
        deskMock = mock(Desk.class);
    }

    private void setPlayerStatus(int playerNumber, PlayerStatus playerStatus) {
        when(playersManagerMock.getPlayerStatus(playerNumber)).thenReturn(playerStatus);
    }

    private void setDealerNumber(int dealerNumber) {
        when(playersManagerMock.getDealerNumber()).thenReturn(dealerNumber);
    }

    private void setPlayersQuantity(int playersQuantity) {
        when(playersManagerMock.getPlayersQuantity()).thenReturn(playersQuantity);
    }

    @Test
    public void shouldSetFirstPlayerStatusNotMovedWhenDefaultNewGameSet() throws Exception {
        newGameSetter.setNewGame();

        verify(playersManagerMock).setPlayerStatus(0, PlayerStatus.NotMoved);
    }

    @Test
    public void shouldSetSecondPlayerStatusNotMovedWhenDefaultNewGameSet() throws Exception {
        newGameSetter.setNewGame();

        verify(playersManagerMock).setPlayerStatus(1, PlayerStatus.NotMoved);
    }

    @Test
    public void shouldNotLostPlayerSetNotMoves() throws Exception {
        setPlayersQuantity(3);
        setPlayerStatus(0, PlayerStatus.Lost);
        setPlayerStatus(2, PlayerStatus.NotMoved);

        newGameSetter.setNewGame();

        verify(playersManagerMock, never()).setPlayerStatus(0, PlayerStatus.NotMoved);
    }

    @Test
    public void shouldBeGivenCardsToFirstPlayerWhenDefaultNewGame() throws Exception {
        newGameSetter.setNewGame();

        verify(deskMock).giveCardsToPlayer(0);
    }

    @Test
    public void shouldBeGivenCardsToSecondPlayerWhenDefaultNewGame() throws Exception {
        newGameSetter.setNewGame();

        verify(deskMock).giveCardsToPlayer(1);
    }

    @Test
    public void shouldBeGivenCardsToThirdPlayerWhen3PlayersNewGame() throws Exception {
        setPlayersQuantity(3);
        setPlayerStatus(3, PlayerStatus.NotMoved);

        newGameSetter.setNewGame();

        verify(deskMock).giveCardsToPlayer(2);
    }

    @Test
    public void shouldNotBeGivenCardsToThirdPlayerWhenDefaultNewGame() throws Exception {
        newGameSetter.setNewGame();

        verify(deskMock, never()).giveCardsToPlayer(2);
    }

    @Test
    public void shouldNotBeGivenCardsToThirdPlayerWhen3PlayersAnd3IsLostNewGame() throws Exception {
        setPlayersQuantity(3);
        setPlayerStatus(2, PlayerStatus.Lost);

        newGameSetter.setNewGame();

        verify(deskMock, never()).giveCardsToPlayer(2);
    }

    @Test
    public void shouldFirstPlayerGiveBigBlindWhenGameStarted() throws Exception {
        newGameSetter.setNewGame();

        verify(bankMock).setPlayerBet(0, GameSettings.SMALL_BLIND_AT_START * 2);
    }

    @Test
    public void shouldSetDealerPlayerNumber1WhenPreviousDealerPlayerNumberWas0() throws Exception {
        newGameSetter.setNewGame();

        verify(playersManagerMock).changeDealer();
    }
}