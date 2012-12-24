/**
 * 
 */
package com.example.classes;

import java.util.ArrayList;

import com.example.wuzhouchess.Views.ChessView;


/**
 * @author SmartGang
 *
 */
public abstract class Player {
	
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	
	final public static int GridEmpty=0;
	public final static int PlayerType_Human=1;
	public final static int PlayerType_AI=2;
	public final static int PlayerType_Internet=3;
	public ArrayList<Chess> chessArray;
	protected int color;
	protected ChessView chessView;
	
	private String playerName;
	private int playerType;
	private int totalPlayTime;
	/**
	 * @return the chessView
	 */
	public ChessView getChessView() {
		return chessView;
	}
	/**
	 * @param chessView the chessView to set
	 */
	public void setChessView(ChessView chessView) {
		this.chessView = chessView;
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
	
	
	
	/**
	 * @param playerName
	 * @param playerType
	 */
	public Player(String playerName, int playerType) {
		super();
		this.playerName = playerName;
		this.playerType = playerType;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}
	public void addChess(Chess c)
	{
		Chess chess=new Chess(c.x,c.y, c.color);
		chessArray.add(chess);
	}
	
	
	
	/**
	 * @return the chessArray
	 */
	public ArrayList<Chess> getChessArray() {
		return chessArray;
	}


	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}


	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}


	/**
	 * @return the playerType
	 */
	public int getPlayerType() {
		return playerType;
	}
	
	
	/**
	 * @return the seconds
	 */
	public int getTotalPlayTime() {
		return totalPlayTime;
	}
	public abstract Movement move(ChessBoard chessBorad,Chess c, int targetX, int targetY);
	
	
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
	/**
	 * @param chessArray the chessArray to set
	 */
	public void setChessArray(ArrayList<Chess> chessArray) {
		this.chessArray = chessArray;
	}
	
	/**
	 * @param playerName the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	/**
	 * @param seconds the seconds to set
	 */
	public void setTotalPlayTime(int seconds) {
		this.totalPlayTime = seconds;
	}

}
