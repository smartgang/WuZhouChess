/**
 * 
 */
package com.example.wuzhouchess.Views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * @author SmartGang
 * ChessBoardView����Ԫ��
 * ������ť������
 */
public class ViewItem {

	private String text;
	private Drawable backgroundDrawable;
	//���Ͻ����꣬��������ChessBoardView�ϵ�����
	private int startX;
	private int startY;
	private int length;//Ԫ�س���
	private int heigth;//Ԫ�ظ߶�
	
	/**
	 * @param startX
	 * @param startY
	 * @param length
	 * @param heigth
	 */
	public ViewItem(int startX, int startY, int length, int heigth) {
		super();
		this.startX = startX;
		this.startY = startY;
		this.length = length;
		this.heigth = heigth;
	}
	//�����Լ�
	public void draw(Canvas canvas)
	{
		if(backgroundDrawable!=null)
			backgroundDrawable.draw(canvas);
		if(text!=null)
		{
			Paint paint=new Paint();
//			paint.setTextScaleX(length-8);
			paint.setTextSize(heigth);
			paint.setColor(Color.GRAY);
			canvas.drawText(text, startX, startY+4, paint);
		}

	}
	//�жϴ������������Ƿ��ڸ�Ԫ�ط�Χ�ڣ���Ҫ�����ж��Ƿ�touched��
	public boolean isInside(int x, int y)
	{
		if(x<startX||x>(startX+length))return false;
		if(y<startY||y>(startY+heigth))return false;
		return true;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @param backgroundDrawable the backgroundDrawable to set
	 */
	public void setBackgroundDrawable(Drawable backgroundDrawable) {
		this.backgroundDrawable = backgroundDrawable;
		backgroundDrawable.setBounds(startX, startY, startX+length, startY+heigth);
	}
	/**
	 * @param startX the startX to set
	 */
	public void setStartX(int startX) {
		this.startX = startX;
	}
	/**
	 * @param startY the startY to set
	 */
	public void setStartY(int startY) {
		this.startY = startY;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}
	
}
