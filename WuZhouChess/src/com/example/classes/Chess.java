/**
 * 
 */
package com.example.classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author SmartGang
 *棋子类
 */
public class Chess {
	
	//定义棋子的两个颜色
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	final public static int GridEmpty=0;
	
	public int x;
	public int y;
	public int color;
	/**
	 * @param x
	 * @param y
	 * @param color
	 */
	public Chess(int x, int y, int color) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
	}
	//在画布上画棋子，以x,y为中心坐标，根据颜色的不同画一个圆，半径为棋格大小的1/4
	public void draw(Canvas canvas, int boardX, int boardY, int boardGridLength, int color)
	{
		Paint p=new Paint();
		//判断自身是否就是选中的棋子，是的话画成黄色
		switch(color)
		{
		case ChessColor_Black:p.setColor(Color.BLACK);break;
		case ChessColor_White:p.setColor(Color.LTGRAY);break;
		case ChessColor_Yellow:p.setColor(Color.YELLOW);break;
		default:p.setColor(Color.WHITE);
		}
		canvas.drawCircle(boardX+x*boardGridLength, boardY+y*boardGridLength, boardGridLength/4, p);
	}
}