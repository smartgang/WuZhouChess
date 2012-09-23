/**
 * 
 */
package com.example.classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author SmartGang
 *
 */
//定义一个5*5的棋盘，总共10个棋子，每一个的内容为棋子的颜色
public class ChessBoard{
	
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	final public static int GridEmpty=0;
	public int[][] chessBoard;
	
	//初始化棋盘
	public ChessBoard() {
		chessBoard=new int[5][5];
		for(int i=0;i<5;i++)
		{
			chessBoard[i][0]=ChessColor_Black;
			chessBoard[i][4]=ChessColor_White;
		}
	}
	
	//在画布上画出自己
	public void draw(Canvas canvas, int boardX, int boardY, int boardGridLength)
	{
		Paint p=new Paint();
		p.setColor(Color.BLUE);
		p.setStrokeWidth(2);
		int boardGridLength_2=boardGridLength*2;
		int boardGridLength_4=boardGridLength*4;
		//画横线和竖线
		for(int i=0;i<5;i++)
		{
			canvas.drawLine(boardX, boardY+boardGridLength*i, boardX+boardGridLength_4, boardY+boardGridLength*i, p);
			canvas.drawLine(boardX+boardGridLength*i, boardY, boardX+boardGridLength*i, boardY+boardGridLength_4, p);
		}
		//三条左斜线
		canvas.drawLine(boardX, boardY, boardX+boardGridLength_4, boardY+boardGridLength_4, p);
		canvas.drawLine(boardX, boardY+boardGridLength_2, boardX+boardGridLength_2, boardY+boardGridLength_4, p);
		canvas.drawLine(boardX+boardGridLength_2, boardY, boardX+boardGridLength_4, boardY+boardGridLength_2, p);
		//三条右斜线
		canvas.drawLine(boardX+boardGridLength_4, boardY, boardX, boardY+boardGridLength_4, p);
		canvas.drawLine(boardX+boardGridLength_2, boardY, boardX, boardY+boardGridLength_2, p);
		canvas.drawLine(boardX+boardGridLength_4, boardY+boardGridLength_2, boardX+boardGridLength_2, boardY+boardGridLength_4, p);
	}

	//判断当前位置是否已经有棋子占用
	public boolean isOccupied(int x, int y)
	{
		if(x<0||x>4)return false;
		if(y<0||y>4)return false;
		if(chessBoard[x][y]==GridEmpty)return true;
		return false;
	}
	
}
