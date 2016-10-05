package com.alaindroid.game.sungka;

import java.security.InvalidParameterException;

public class SungkaBoard {
	public byte[] meHoles;
	public ScoreContainer meScore;
	public byte[] enHoles;
	public ScoreContainer enScore;

	public byte[] meDiff;
	public int meHDiff;
	public byte[] enDiff;

	private int stepsLeft;
	private boolean meSide;
	private int onHand;
	private int dropLoc;
	private boolean meTurn;

	/*
	 * Constructors
	 */
	public SungkaBoard() {
		meHoles = new byte[7];
		enHoles = new byte[7];
		meDiff = new byte[7];
		enDiff = new byte[7];
		for (int i = 0; i < meHoles.length; i++) {
			meHoles[i] = 7;
			enHoles[i] = 7;
			meDiff[i] = 1;
			enDiff[i] = 1;
		}
		this.meScore = new ScoreContainer();
		this.enScore = new ScoreContainer();
		meHDiff = 1;
	}

	public SungkaBoard(byte[] meHoles, byte[] enHoles, ScoreContainer meScore, ScoreContainer enScore) {
		this.meHoles = meHoles;
		this.enHoles = enHoles;
		this.meScore = meScore;
		this.enScore = enScore;
		this.meDiff = new byte[7];
		this.enDiff = new byte[7];
		for (int i = 0; i < meHoles.length; i++) {
			this.meDiff[i] = 1;
			this.enDiff[i] = 1;
		}
		this.meHDiff = 1;
	}

	public SungkaBoard(byte[] meHoles, byte[] enHoles, ScoreContainer meScore, ScoreContainer enScore, byte[] meDiff,
			byte[] enDiff, byte meHDiff) {
		this.meHoles = meHoles;
		this.enHoles = enHoles;
		this.meScore = meScore;
		this.enScore = enScore;
		this.meDiff = meDiff;
		this.enDiff = enDiff;
		this.meHDiff = meHDiff;
	}

	/*
	 * Convenience function
	 */
	public SungkaBoard getEnemyBoard() {
		return new SungkaBoard(enHoles, meHoles, enScore, meScore);
	}

	public SungkaBoard clone() {
		int meLen = this.meHoles.length;
		int enLen = this.enHoles.length;
		byte[] meHoles = new byte[meLen];
		byte[] enHoles = new byte[enLen];
		ScoreContainer meScore = new ScoreContainer();
		ScoreContainer enScore = new ScoreContainer();
		System.arraycopy(this.meHoles, 0, meHoles, 0, meLen);
		System.arraycopy(this.enHoles, 0, enHoles, 0, enLen);
		meScore.score = this.meScore.score;
		enScore.score = this.enScore.score;
		return new SungkaBoard(meHoles, enHoles, meScore, enScore);
	}

	/**
	 * Pickup and move a piece
	 * 
	 * @param location
	 *            0 - (holes length -1)
	 */
	public MoveResult move(boolean me, int location, int step) {
		if (location < 0 || location >= meHoles.length) {
			throw new InvalidParameterException("Invalid location " + location);
		}
		meTurn = false;
		stepsLeft = step;
		meSide = me;
		onHand = 0;

		if (meSide) {
			onHand = meHoles[location];
			meHoles[location] = 0;
		} else {
			onHand = enHoles[location];
			enHoles[location] = 0;
		}
		MoveResult moveResult = new MoveResult();
		if (onHand > 0) {
			dropLoc = location + 1;
			while (onHand > 0) {
				// me side drops
				moveResult.lastMeSide = meSide;
				moveResult.lastIndex = dropLoc;
				if (meSide) {
					// score hole
					if (dropLoc >= meHoles.length) {
						int diff = meHDiff;
						meScore.score += diff;
						meSide = false;
						onHand -= diff;
						dropLoc = 0;
					} else {
						int diff = meDiff[dropLoc];
						meHoles[dropLoc] += diff;
						onHand -= diff;
						if (onHand == 0 && meHoles[dropLoc] > diff) {
							if (stepsLeft > 0) {
								stepsLeft--;
								onHand = meHoles[dropLoc];
								meHoles[dropLoc] = 0;
							} else {
								meTurn = true;
							}
						}
						dropLoc++;
					}
				} else { // enemy side
					// score hole
					if (dropLoc >= enHoles.length) {
						meSide = true;
						dropLoc = 0;
						continue;
					} else {
						int diff = enDiff[dropLoc];
						enHoles[dropLoc] += diff;
						onHand -= diff;
						if (onHand == 0 && enHoles[dropLoc] > diff) {
							if (stepsLeft > 0) {
								stepsLeft--;
								onHand = enHoles[dropLoc];
								enHoles[dropLoc] = 0;
							} else {
								meTurn = true;
							}
						}
						dropLoc++;
					}
				}
			}
		}
		if (dropLoc == 0) {
			meTurn = true;
		}
		moveResult.meTurn = meTurn;
		moveResult.steps = step - stepsLeft;
		return moveResult;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(enScore).append("<<");
		for (int i = enHoles.length - 1; i >= 0; i--) {
			sb.append(enHoles[i]).append("<");
		}
		sb.append("\r\n  ");
		for (int i = 0; i < meHoles.length; i++) {
			sb.append(">").append(meHoles[i]);
		}
		sb.append(">>").append(meScore);
		return sb.toString();
	}

	public static class ScoreContainer {
		byte score;

		@Override
		public String toString() {
			return String.valueOf(score);
		}
	}

	public static class MoveResult {
		boolean meTurn;
		boolean lastMeSide;
		int lastIndex;
		int steps;

		@Override
		public String toString() {
			return "[meTurn=" + meTurn + "; lastMoveSide=" + lastMeSide + "; lastIndex=" + lastIndex + "; steps="
					+ steps + "]";
		}
	}
}
