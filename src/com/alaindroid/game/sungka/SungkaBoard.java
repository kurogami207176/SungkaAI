package com.alaindroid.game.sungka;

import java.security.InvalidParameterException;

public class SungkaBoard {
	public byte[] meHoles;
	public ScoreContainer meScore;
	public byte[] enHoles;
	public ScoreContainer enScore;

	public byte[] meDiff;
	public byte[] enDiff;

	private int stepsLeft;
	private boolean meSide;
	private int onHand;
	private int dropLoc;

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
	}

	public SungkaBoard(byte[] meHoles, byte[] enHoles, ScoreContainer meScore, ScoreContainer enScore, byte[] meDiff,
			byte[] enDiff) {
		this.meHoles = meHoles;
		this.enHoles = enHoles;
		this.meScore = meScore;
		this.enScore = enScore;
		this.meDiff = meDiff;
		this.enDiff = enDiff;
	}

	/*
	 * Convenience function
	 */
	public SungkaBoard getEnemyBoard() {
		return new SungkaBoard(enHoles, meHoles, enScore, meScore);
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
		dropLoc = location + 1;
		MoveResult moveResult = new MoveResult();
		while (onHand > 0) {
			// me side drops
			moveResult.lastMeSide = meSide;
			moveResult.lastIndex = dropLoc;
			if (meSide) {
				// score hole
				int diff = meDiff[dropLoc];
				if (dropLoc >= meHoles.length) {
					meScore.score += diff;
					meSide = false;
					onHand -= diff;
					dropLoc = 0;
				} else {
					meHoles[dropLoc] += diff;
					onHand -= diff;
					if (onHand == 0 && stepsLeft > 0) {
						stepsLeft--;
						onHand = meHoles[dropLoc];
						meHoles[dropLoc] = 0;
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
					enHoles[dropLoc]++;
					onHand--;
					if (onHand == 0 && stepsLeft > 0) {
						stepsLeft--;
						onHand = enHoles[dropLoc];
						enHoles[dropLoc] = 0;
					}
					dropLoc++;
				}
			}
		}
		if (dropLoc == 0) {
			moveResult.meTurn = true;
		} else {
			moveResult.meTurn = false;
		}
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

	public static void main(String[] args) {
		SungkaBoard meSB = new SungkaBoard();
		SungkaBoard enSB = meSB.getEnemyBoard();
		System.out.println(meSB.toString());
		System.out.println(enSB.toString());
		// MoveResult result = meSB.move(true, 1, 1);
		MoveResult result = meSB.move(true, 1, 0);
		System.out.println(meSB.toString());
		System.out.println(enSB.toString());
		result = meSB.move(result.lastMeSide, result.lastIndex, 0);
		System.out.println(result);
		System.out.println(meSB.toString());
		System.out.println(enSB.toString());

		// TODO: Fix
		// System.out.println("converter");
		// System.out.println(Util.fromBytes(Util.toBytes(meSB)).toString());
		// System.out.println(Util.fromBytes(Util.toBytes(enSB)).toString());

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

		@Override
		public String toString() {
			return "[moveResult=" + meTurn + "; lastMoveSide=" + lastMeSide + "; lastIndex=" + lastMeSide + "]";
		}
	}
}
