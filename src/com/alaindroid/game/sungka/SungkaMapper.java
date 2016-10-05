package com.alaindroid.game.sungka;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.alaindroid.game.sungka.SungkaBoard.MoveResult;

public class SungkaMapper {
	public Map<Integer, MovesResponse> getMoveResults(SungkaBoard board) {
		Map<Integer, MovesResponse> retVal = new TreeMap<Integer, MovesResponse>();
		int len = board.meHoles.length;
		int highestScore = 0;
		for (int i = 0; i < len; i++) {
			SungkaBoard clone = board.clone();
			MoveResult moveResult = clone.move(true, i, Integer.MAX_VALUE);
			// MoveResult moveResult = clone.move(true, i, 1);
			if (clone.meScore.score > highestScore) {
				retVal.put(i, new MovesResponse(clone, moveResult, i));
			}
		}
		return retVal;
	}

	public MoveNode getMoveChain(SungkaBoard board) {
		Map<Integer, MovesResponse> moveNodes = getMoveResults(board);
		MoveNode node = new MoveNode(board, moveNodes);
		for (Integer key : moveNodes.keySet()) {
			MovesResponse resp = moveNodes.get(key);
			if (resp.moveResult.meTurn) {
				MoveNode nextNode = getMoveChain(resp.nextState);
				node.movesNodes.put(key, nextNode);
			}
		}
		return node;
	}

	public static class MoveNode {
		MoveNode(SungkaBoard board, Map<Integer, MovesResponse> movesResponses) {
			this.board = board;
			this.movesResponses = movesResponses;
			movesNodes = new HashMap<Integer, MoveNode>();
		}

		public SungkaBoard board;
		public Map<Integer, MovesResponse> movesResponses;
		public Map<Integer, MoveNode> movesNodes;

		@Override
		public String toString() {
			return toString(0);
		}

		public String toString(int level) {
			StringBuilder sb = new StringBuilder();
			StringBuilder ind = new StringBuilder();
			for (int i = 0; i < level; i++) {
				ind.append("\t");
			}
			String inde = ind.toString();
			sb.append(inde).append(board).append("\r\n");
			for (Integer key : movesResponses.keySet()) {
				MovesResponse moveResponse = movesResponses.get(key);
				MoveNode moveNode = movesNodes.get(key);
				if (moveNode == null) {
					sb.append(inde).append(moveResponse.nextState).append("***\r\n");
				} else {
					sb.append(inde).append(moveNode.toString(level + 1)).append("\r\n");
				}
			}
			return sb.toString();
		}
	}

	public static class MovesResponse {
		public MovesResponse(SungkaBoard nextState, MoveResult moveResult, int move) {
			this.nextState = nextState;
			this.move = move;
			this.moveResult = moveResult;
		}

		public MoveResult moveResult;
		public SungkaBoard nextState;
		public int move;
	}

	public static void main(String[] args) {
		SungkaBoard meSB = new SungkaBoard();
		SungkaBoard enSB = meSB.getEnemyBoard();
		System.out.println("start");
		System.out.println(meSB.toString());
		System.out.println(enSB.toString());
		// MoveResult result = meSB.move(true, 1, 1);

		SungkaMapper mapper = new SungkaMapper();
		Map<Integer, MovesResponse> resp = mapper.getMoveResults(meSB);
		for (Integer key : resp.keySet()) {
			MovesResponse res = resp.get(key);
			System.out.println("#" + res.move);
			System.out.println(res.moveResult);
			System.out.println(res.nextState);
			// System.out.println(res.nextState.getEnemyBoard());
		}
		MoveNode node = mapper.getMoveChain(meSB.clone());
		System.out.println("nodes");
		System.out.println(node);
		// SungkaBoard clone = meSB.clone();
		// int moveLoc = 0;
		// boolean meSide = true;
		// boolean meTurn = true;
		// for (int i = 0; i < 33; i++) {
		// System.out.println("#" + i);
		// System.out.println(moveLoc);
		// System.out.println(meSide);
		// if (meTurn && moveLoc < clone.enHoles.length) {
		// MoveResult result = clone.move(meSide, moveLoc, 0);
		// System.out.println(result);
		// System.out.println(clone);
		// moveLoc = result.lastIndex;
		// meSide = result.lastMeSide;
		// meTurn = result.meTurn;
		// }
		// }
		System.out.println("original");
		System.out.println(meSB.toString());
		System.out.println(enSB.toString());

	}

}
