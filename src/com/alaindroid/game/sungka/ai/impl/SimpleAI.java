package com.alaindroid.game.sungka.ai.impl;

import com.alaindroid.game.sungka.SungkaBoard;
import com.alaindroid.game.sungka.SungkaMapper;
import com.alaindroid.game.sungka.game.PlayerInterface;

public class SimpleAI implements PlayerInterface {

	SungkaMapper mapper = new SungkaMapper();

	@Override
	public Integer onCallback(SungkaBoard board) {
		return mapper.getBestMove(board);
	}

}
