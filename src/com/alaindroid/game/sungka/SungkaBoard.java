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

	private Rule rule;
	private Listener listener;

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

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	/*
	 * Convenience function
	 */
	public SungkaBoard getEnemyBoard() {
		return new SungkaBoard(enHoles, meHoles, enScore, meScore);
	}

	public boolean isGameEnd() {
		for (int meHole : meHoles) {
			if (meHole > 0) {
				return false;
			}
		}
		for (int enHole : enHoles) {
			if (enHole > 0) {
				return false;
			}
		}
		return true;
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

	public MoveResult move(boolean me, int location) {
		return move(me, location, Integer.MAX_VALUE);
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

		if (listener != null) {
			listener.onMove(this, me, location, step);
		}
		if (rule != null) {
			rule.preProcess(this, me, location);
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
		dropLoc = location + 1;
		if (onHand > 0) {
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
			if (listener != null) {
				listener.onAfterDrop(this, meSide, onHand, dropLoc);
			}
		}
		if (dropLoc == 0) {
			meTurn = true;
		}
		moveResult.meTurn = meTurn;
		moveResult.steps = step - stepsLeft;
		if (listener != null) {
			listener.onBeforePostProcess(this, moveResult);
		}
		if (rule != null) {
			rule.postProcess(this, moveResult);
		}
		if (listener != null) {
			listener.onAfterPostProcess(this);
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof SungkaBoard) {
			SungkaBoard other = (SungkaBoard) o;
			if (meScore.score != other.meScore.score || enScore.score != other.enScore.score
					|| enHoles.length != other.enHoles.length || meHoles.length != other.meHoles.length) {
				return false;
			}
			for (int i = 0; i < meHoles.length; i++) {
				if (meHoles[i] != other.meHoles[i]) {
					return false;
				}
			}
			for (int i = 0; i < enHoles.length; i++) {
				if (enHoles[i] != other.enHoles[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static class ScoreContainer {
		public ScoreContainer() {

		}

		public ScoreContainer(byte score) {
			this.score = score;
		}

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

	public static interface Rule {
		/**
		 * Transforms the sungkaboard based on the lastMoveResult
		 * 
		 * @param lastSungkaState
		 * @param lastMoveResult
		 * @return
		 */
		public void postProcess(SungkaBoard lastSungkaState, MoveResult lastMoveResult);

		public void preProcess(SungkaBoard lastSungkaState, boolean me, int location);
	}

	public static interface Listener {
		public void onMove(SungkaBoard board, boolean me, int location, int step);

		public void onAfterDrop(SungkaBoard board, boolean meSide, int onHand, int dropLoc);

		public void onBeforePostProcess(SungkaBoard board, MoveResult moveResult);

		public void onAfterPostProcess(SungkaBoard board);
	}
}
