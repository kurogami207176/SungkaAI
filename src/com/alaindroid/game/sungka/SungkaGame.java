package com.alaindroid.game.sungka;

import com.alaindroid.game.sungka.SungkaBoard.MoveResult;
import com.alaindroid.game.sungka.ai.impl.SimpleAI;
import com.alaindroid.game.sungka.game.PlayerInterface;

public class SungkaGame {
	SungkaBoard sungkaBoard1;
	PlayerInterface player1;

	SungkaBoard sungkaBoard2;
	PlayerInterface player2;
	boolean player1Turn;

	public SungkaGame(SungkaBoard sungkaBoard, PlayerInterface player1, PlayerInterface player2) {
		this.sungkaBoard1 = sungkaBoard;
		this.sungkaBoard2 = sungkaBoard.getEnemyBoard();
		this.player1 = player1;
		this.player2 = player2;
		player1Turn = true;
	}

	public Result start() {
		Result retVal = null;
		while (retVal == null) {
			if (player1Turn) {
				Integer move = player1.onCallback(sungkaBoard1);
				if (move == null) {
					// No move available, change player
					player1Turn = !player1Turn;
				} else {
					MoveResult result = sungkaBoard1.move(true, move, Integer.MAX_VALUE);
					if (!result.meTurn) {
						player1Turn = !player1Turn;
					}
				}
				System.out.println("move1: " + move);
				System.out.println("result1: " + sungkaBoard1);
			} else {
				Integer move = player2.onCallback(sungkaBoard2);
				if (move == null) {
					// No move available, change player
					player1Turn = !player1Turn;
				} else {
					MoveResult result = sungkaBoard2.move(true, move, Integer.MAX_VALUE);
					if (!result.meTurn) {
						player1Turn = !player1Turn;
					}
				}
				System.out.println("move2: " + move);
				System.out.println("result2: " + sungkaBoard1);
			}
			if (sungkaBoard1.isGameEnd()) {
				retVal = new Result();
				retVal.result = sungkaBoard1;
				if (sungkaBoard1.meScore.score > sungkaBoard2.meScore.score) {
					retVal.winner = ResultWinner.ONE;
				} else if (sungkaBoard1.meScore.score < sungkaBoard2.meScore.score) {
					retVal.winner = ResultWinner.TWO;
				} else {
					retVal.winner = ResultWinner.DRAW;
				}
			}
		}
		return retVal;
	}

	public static class Result {
		SungkaBoard result;
		ResultWinner winner; // null means draw
	}

	public enum ResultWinner {
		ONE, TWO, DRAW
	}

	public static void main(String[] args) {
		PlayerInterface player1 = new SimpleAI();
		PlayerInterface player2 = new SimpleAI();
		SungkaBoard board = new SungkaBoard();
		SungkaGame game = new SungkaGame(board, player1, player2);
		Result result = game.start();
		System.out.println("WINNER: " + result.winner.name());
		System.out.println("BOARD: " + result.result);
	}

}
