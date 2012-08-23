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
	
	private boolean eatFlag;//被吃的标识，表示该棋子是否刚被吃，更换颜色，被吃后，要相应有被吃掉的动画
	/**
	 * @param eatFlag the eatFlag to set
	 */
	public void setEatFlag() {
		this.eatFlag = true;
	}
	private int eatAnimationCount;//用来做为动画的标识，当棋子被吃时，每一次更新（0.1秒）这个值加1
								//在这个值为4，5，6时，不显示该棋子，显示闪烁的效果
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
		eatFlag=false;
		eatAnimationCount=0;
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
		if(eatFlag)eatAnimationCount++;
		if(eatAnimationCount<4)//从第0.4秒开始不显示
		{
			canvas.drawCircle(boardX+x*boardGridLength, boardY+y*boardGridLength, boardGridLength/4, p);
		}
		else if(eatAnimationCount>7)//从第0.7秒开始显示
		{
			eatFlag=false;
			eatAnimationCount=0;
		}
	}
}