/**
 * 
 */
package com.example.classes;

import java.util.ArrayList;


import android.text.format.Time;

/**
 * @author SmartGang
 *
 */
public abstract class Player {
	
	public final static int PlayerType_Human=1;
	public final static int PlayerType_AI=2;
	
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	
	private String playerName;
	private int totalPlayTime;
	/**
	 * @return the playerType
	 */
	public int getPlayerType() {
		return playerType;
	}

	private int color;
	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	private int playerType;
	public ArrayList<Chess> chessArray;
	
	
	
	/**
	 * @return the chessArray
	 */
	public ArrayList<Chess> getChessArray() {
		return chessArray;
	}


	/**
	 * @param chessArray the chessArray to set
	 */
	public void setChessArray(ArrayList<Chess> chessArray) {
		this.chessArray = chessArray;
	}


	/**
	 * @param playerName
	 * @param color
	 * @param playerType
	 */
	public Player(String playerName, int color, int playerType) {
		super();
		this.playerName = playerName;
		this.color = color;
		this.playerType = playerType;
	}


	public abstract Movement move(ChessBoard chessBorad,Chess c, int targetX, int targetY);
	
	
	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}
	/**
	 * @param playerName the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	/**
	 * @return the seconds
	 */
	public int getTotalPlayTime() {
		return totalPlayTime;
	}
	/**
	 * @param seconds the seconds to set
	 */
	public void setTotalPlayTime(int seconds) {
		this.totalPlayTime = seconds;
	}
	
	public void addChess(Chess c)
	{
		Chess chess=new Chess(c.x,c.y, c.color);
		chessArray.add(chess);
	}
	
	public void removeChess(Chess c)
	{
		int chessCount=chessArray.size();
		for(int i=0;i<chessCount;i++)
		{
			if(chessArray.get(i).x==c.x&&
					chessArray.get(i).y==c.y&&
					chessArray.get(i).color==c.color)
			{
				chessArray.remove(i);
				break;
			}
		}
	}

}
