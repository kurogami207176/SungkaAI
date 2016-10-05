package com.alaindroid.game.sungka;

import java.io.ByteArrayOutputStream;

import com.alaindroid.game.sungka.SungkaBoard.ScoreContainer;

public class Util {

	public static SungkaBoard fromBytes(SungkaBoard storage, byte[] input) {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		int curr = 0;
		int len = input.length;
		byte[] meHoles = storage.meHoles;
		byte[] enHoles = storage.enHoles;
		ScoreContainer meScore = storage.meScore;
		ScoreContainer enScore = storage.enScore;
		boolean meSide = true;
		while (curr < len) {
			byte thisByte = input[curr];
			if (meSide) {
				if (thisByte == Byte.MAX_VALUE) {
					byte[] tmp = array.toByteArray();
					array = new ByteArrayOutputStream();
					meScore.score = input[++curr];
					curr++;
					System.arraycopy(tmp, 0, meHoles, 0, meHoles.length);
					meSide = false;
				} else {
					array.write(thisByte);
				}
			} else {
				if (thisByte == Byte.MAX_VALUE) {
					byte[] tmp = array.toByteArray();
					array = new ByteArrayOutputStream();
					enScore.score = input[++curr];
					curr = len;
					System.arraycopy(tmp, 0, enHoles, 0, enHoles.length);
				} else {
					array.write(thisByte);
				}
			}
			curr++;
		}
		return storage;
	}

	public static SungkaBoard fromBytes(byte[] input) {
		return fromBytes(new SungkaBoard(), input);
	}

	public static byte[] toBytes(SungkaBoard input) {
		byte[] retVal = new byte[input.meHoles.length + 3 + input.enHoles.length + 2];
		int index = 0;
		System.arraycopy(input.meHoles, 0, retVal, index, input.meHoles.length);
		index = index + input.meHoles.length - 1;
		retVal[++index] = Byte.MAX_VALUE;
		retVal[++index] = (byte) input.meScore.score;
		retVal[++index] = Byte.MAX_VALUE;
		System.arraycopy(input.enHoles, 0, retVal, ++index, input.enHoles.length);
		index = index + input.enHoles.length - 1;
		retVal[++index] = Byte.MAX_VALUE;
		retVal[++index] = (byte) input.enScore.score;
//		for(byte b: retVal){
//			System.out.print(b + " ");
//		}
//		System.out.println();
		return retVal;
	}

}
