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
//����һ��5*5�����̣��ܹ�10�����ӣ�ÿһ��������Ϊ���ӵ���ɫ
public class ChessBoard{
	
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	final public static int GridEmpty=0;
	public int[][] chessBoard;
	
	//��ʼ������
	public ChessBoard() {
		chessBoard=new int[5][5];
		for(int i=0;i<5;i++)
		{
			chessBoard[i][0]=ChessColor_Black;
			chessBoard[i][4]=ChessColor_White;
		}
	}
	
	//�ڻ����ϻ����Լ�
	public void draw(Canvas canvas, int boardX, int boardY, int boardGridLength)
	{
		Paint p=new Paint();
		p.setColor(Color.BLUE);
		p.setStrokeWidth(2);
		int boardGridLength_2=boardGridLength*2;
		int boardGridLength_4=boardGridLength*4;
		//�����ߺ�����
		for(int i=0;i<5;i++)
		{
			canvas.drawLine(boardX, boardY+boardGridLength*i, boardX+boardGridLength_4, boardY+boardGridLength*i, p);
			canvas.drawLine(boardX+boardGridLength*i, boardY, boardX+boardGridLength*i, boardY+boardGridLength_4, p);
		}
		//������б��
		canvas.drawLine(boardX, boardY, boardX+boardGridLength_4, boardY+boardGridLength_4, p);
		canvas.drawLine(boardX, boardY+boardGridLength_2, boardX+boardGridLength_2, boardY+boardGridLength_4, p);
		canvas.drawLine(boardX+boardGridLength_2, boardY, boardX+boardGridLength_4, boardY+boardGridLength_2, p);
		//������б��
		canvas.drawLine(boardX+boardGridLength_4, boardY, boardX, boardY+boardGridLength_4, p);
		canvas.drawLine(boardX+boardGridLength_2, boardY, boardX, boardY+boardGridLength_2, p);
		canvas.drawLine(boardX+boardGridLength_4, boardY+boardGridLength_2, boardX+boardGridLength_2, boardY+boardGridLength_4, p);
	}

	//�жϵ�ǰλ���Ƿ��Ѿ�������ռ��
	public boolean isOccupied(int x, int y)
	{
		if(x<0||x>4)return false;
		if(y<0||y>4)return false;
		if(chessBoard[x][y]==GridEmpty)return true;
		return false;
	}
	
}
