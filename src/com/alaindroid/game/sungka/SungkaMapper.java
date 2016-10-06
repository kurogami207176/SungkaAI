package com.alaindroid.game.sungka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

import com.alaindroid.game.sungka.SungkaBoard.MoveResult;
import com.alaindroid.game.sungka.game.PlayerInterface;

public class SungkaMapper implements PlayerInterface {
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

	public Integer onCallback(SungkaBoard board) {
		return getBestMove(board);
	}

	public Integer getBestMove(SungkaBoard board) {
		Map<Integer, MovesResponse> moveNodes = getMoveResults(board);
		List<MovesResponse> response = new ArrayList<MovesResponse>(moveNodes.values());
		Collections.sort(response, new Comparator<MovesResponse>() {

			@Override
			public int compare(MovesResponse o1, MovesResponse o2) {
				int retVal = o1.nextState.meScore.score - o2.nextState.meScore.score;
				if (retVal == 0) {
					return Boolean.valueOf(o1.moveResult.meTurn).compareTo(o2.moveResult.meTurn);
				}
				return retVal;
			}
		});
		return response.get(0).move;
	}

	@Deprecated
	/**
	 * Takes a lot of memory
	 * 
	 * @param board
	 * @param depth
	 * @return
	 */
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
					// sb.append(currPrefix).append(moveNode.toString(currPrefix)).append("\r\n");
					sb.append(moveNode.toString(currPrefix)).append("\r\n");
				}
			}
			return sb.toString();
		}

		public List<ChainResult> getChainResult() {
			List<ChainResult> myChainResult = new ArrayList<ChainResult>();
			Stack<Integer> myStack = new Stack<Integer>();
			getChainResult(myChainResult, myStack);
			return myChainResult;
		}

		public void getChainResult(List<ChainResult> chainResult, Stack<Integer> stack) {
			List<ChainResult> myChainResult = chainResult;
			if (myChainResult == null) {
				myChainResult = new ArrayList<ChainResult>();
			}
			Stack<Integer> myStack = stack;
			if (myStack == null) {
				myStack = new Stack<Integer>();
			}
			// sb.append(inde).append(board).append("\r\n");
			for (Integer key : movesResponses.keySet()) {
				MoveNode moveNode = movesNodes.get(key);
				myStack.push(key);
				if (moveNode == null) {
					MovesResponse moveResponse = movesResponses.get(key);
					ChainResult myC = new ChainResult();
					myC.endResult = moveResponse.nextState;
					myC.link = new ArrayList<Integer>(myStack);
					myChainResult.add(myC);
				} else {
					moveNode.getChainResult(myChainResult, myStack);
				}
				myStack.pop();
			}
		}
	}

	public static class ChainResult {
		public SungkaBoard endResult;
		public List<Integer> link;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			// sb.append("size").append(link.size()).append("\t");
			for (Integer ln : link) {
				sb.append("«").append(ln).append("»");
			}
			sb.append(endResult);
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
		// SungkaBoard meSB = new SungkaBoard(new byte[] { 4, 4, 4, 4, 4, 4 },
		// new byte[] { 4, 4, 4, 4, 4, 4 }, new ScoreContainer(), new
		// ScoreContainer());
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
		MoveNode node = mapper.getMoveChain(meSB.clone(), Integer.MAX_VALUE);
		// MoveNode node = mapper.getMoveChain(meSB.clone(), 4);
		System.out.println("chain");
		for (ChainResult chn : node.getChainResult()) {
			System.out.println(chn);
		}
		// System.out.println("nodes");
		// System.out.println(node);

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
		try (Scanner sc = new Scanner(System.in)) {
			while (true) {
				System.out.print("Select a number 0-6: ");
				int nextInt = sc.nextInt();
				if (nextInt > 6 || nextInt < 0) {
					break;
				}
				MoveResult result = meSB.move(true, nextInt, Integer.MAX_VALUE);
				System.out.println(result.toString());
				System.out.println(meSB.toString());
			}
		}

	}

}
