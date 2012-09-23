/**
 * 
 */
package com.chessserver.www.rules;

/**
 * @author SmartGang
 *
 */
public class Movement {
	public int fromX;
	public int fromY;
	public int toX;
	public int toY;
	Movement()
	{
		fromX=10;
		fromY=10;
		toX=10;
		toY=10;
	}
	/**
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 */
	public Movement(int fromX, int fromY, int toX, int toY) {
		super();
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}

}
