/**
 * 
 */
package com.example.classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author SmartGang
 *������
 */
public class Chess {
	
	//�������ӵ�������ɫ
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	final public static int GridEmpty=0;
	
	private boolean eatFlag;//���Եı�ʶ����ʾ�������Ƿ�ձ��ԣ�������ɫ�����Ժ�Ҫ��Ӧ�б��Ե��Ķ���
	/**
	 * @param eatFlag the eatFlag to set
	 */
	public void setEatFlag() {
		this.eatFlag = true;
	}
	private int eatAnimationCount;//������Ϊ�����ı�ʶ�������ӱ���ʱ��ÿһ�θ��£�0.1�룩���ֵ��1
								//�����ֵΪ4��5��6ʱ������ʾ�����ӣ���ʾ��˸��Ч��
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
	//�ڻ����ϻ����ӣ���x,yΪ�������꣬������ɫ�Ĳ�ͬ��һ��Բ���뾶Ϊ����С��1/4
	public void draw(Canvas canvas, int boardX, int boardY, int boardGridLength, int color)
	{
		Paint p=new Paint();
		//�ж������Ƿ����ѡ�е����ӣ��ǵĻ����ɻ�ɫ
		switch(color)
		{
		case ChessColor_Black:p.setColor(Color.BLACK);break;
		case ChessColor_White:p.setColor(Color.LTGRAY);break;
		case ChessColor_Yellow:p.setColor(Color.YELLOW);break;
		default:p.setColor(Color.WHITE);
		}
		if(eatFlag)eatAnimationCount++;
		if(eatAnimationCount<4)//�ӵ�0.4�뿪ʼ����ʾ
		{
			canvas.drawCircle(boardX+x*boardGridLength, boardY+y*boardGridLength, boardGridLength/4, p);
		}
		else if(eatAnimationCount>7)//�ӵ�0.7�뿪ʼ��ʾ
		{
			eatFlag=false;
			eatAnimationCount=0;
		}
	}
}