package com.alaindroid.game.sungka;

public class SungkaGame {
	SungkaBoard sungkaBoard;
	GameState gameState;

	public SungkaGame() {
		SungkaBoard sungkaBoard = new SungkaBoard();
		gameState = GameState.PLAYER_ONE;
	}

	public void move(int x, int y) {

	}

	public enum GameState {
		PLAYER_ONE, PLAYER_TWO, WINNER_ONE, WINNER_TWO, DRAW;
	}
}
