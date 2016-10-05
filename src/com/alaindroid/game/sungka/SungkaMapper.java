package com.alaindroid.game.sungka;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import com.alaindroid.game.sungka.SungkaBoard.MoveResult;

public class SungkaMapper {
	public Map<Integer, MovesResponse> getMoveResults(SungkaBoard board) {
		Map<Integer, MovesResponse> retVal = new TreeMap<Integer, MovesResponse>();
		int len = board.meHoles.length;
		for (int i = 0; i < len; i++) {
			SungkaBoard clone = board.clone();
			MoveResult moveResult = clone.move(true, i, Integer.MAX_VALUE);
			// MoveResult moveResult = clone.move(true, i, 1);
			if (!board.equals(clone)) {
				retVal.put(i, new MovesResponse(clone, moveResult, i));
			}
		}
		return retVal;
	}

	// TODO: Do not user recursive
	public MoveNode getMoveChain(SungkaBoard board, int depth) {
		if (depth > 0) {
			Map<Integer, MovesResponse> moveNodes = getMoveResults(board);
			MoveNode node = new MoveNode(board, moveNodes);
			for (Integer key : moveNodes.keySet()) {
				MovesResponse resp = moveNodes.get(key);
				if (resp.moveResult.meTurn) {
					MoveNode nextNode = getMoveChain(resp.nextState, depth - 1);
					if (nextNode != null) {
						node.movesNodes.put(key, nextNode);
					}
				}
			}
			return node;
		}
		return null;
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
			return toString("");
		}

		public String toString(String prefix) {
			StringBuilder sb = new StringBuilder();
			// sb.append(inde).append(board).append("\r\n");
			for (Integer key : movesResponses.keySet()) {
				MovesResponse moveResponse = movesResponses.get(key);
				MoveNode moveNode = movesNodes.get(key);
				String currPrefix = prefix + "•" + key + "•";
				if (moveNode == null) {
					sb.append(currPrefix).append("\r\n").append(moveResponse.nextState).append("***\r\n");
				} else {
					sb.append(currPrefix).append(moveNode.toString(currPrefix)).append("\r\n");
				}
			}
			return sb.toString();
		}

		public ChainResult getChainResult(ChainResult chainResult, Stack<Integer> stack) {
			StringBuilder sb = new StringBuilder();
			ChainResult myChainResult = chainResult;
			if (myChainResult == null) {
				myChainResult = new ChainResult();
			}
			Stack<Integer> myStack = stack;
			if (myStack == null) {
				myStack = new Stack<Integer>();
			}
			// sb.append(inde).append(board).append("\r\n");
			for (Integer key : movesResponses.keySet()) {
				MovesResponse moveResponse = movesResponses.get(key);
				MoveNode moveNode = movesNodes.get(key);
				myStack.push(key);
				if (moveNode == null) {
					chainResult.endResult = moveResponse.nextState;
				} else {
					getChainResult(myChainResult, myStack);
					myStack.pop();
				}
			}
			return myChainResult;
		}
	}

	public static class ChainResult {
		public SungkaBoard endResult;
		byte[] link;
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
		// MoveNode node = mapper.getMoveChain(meSB.clone(), Integer.MAX_VALUE);
		MoveNode node = mapper.getMoveChain(meSB.clone(), 20);
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
