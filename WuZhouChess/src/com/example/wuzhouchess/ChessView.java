/**
 * 
 */
package com.example.wuzhouchess;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.widget.Toast;



/**������
 * @author SmartGang
 * @version 1.0
 */
public class ChessView extends android.view.View {

	//�������ӵ�������ɫ
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int GridEmpty=0;
	//������ʾ�ֵ���һ����ɫ��ѡ������
	public int playerTurn;
	//���ڱ�ʾ��ǰ�İ���״̬���ǿ����ʾ��ǰ�Ѿ�ѡ������
	Chess choseChess=null;
	//����������ÿһ��Ŀ��,Ĭ��Ϊ20��ʵ�ʸ�����Ļ��С������
	private int boardGridLength=20;
	//�������Ͻǵ����꣬������Ļ���ĵ��λ�ã����ÿһ��Ŀ�������㣬Ĭ����600*480
	private int boardX=80;
	private int boardY=140;
	//������ɫ���������У���ʼ��Ϊ5������Ϊ0�����䣬�ﵽ10����Ӯ
	private ArrayList<Chess> blackChessArray= new ArrayList<Chess>();
	private ArrayList<Chess> whiteChessArray= new ArrayList<Chess>();
	ChessBoard cb;
	
	
	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//��ʼʼ�����̼��������ӣ��������ϣ���������
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
			//�ڷ�����
			playerTurn=ChessColor_Black;
			
		}
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ChessView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//��ʼʼ�����̼��������ӣ��������ϣ���������
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
			//�ڷ�����
			playerTurn=ChessColor_Black;
		}
	}

	/**
	 * @param context
	 */
	public ChessView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//��ʼʼ�����̼��������ӣ��������ϣ���������
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
			//�ڷ�����
			playerTurn=ChessColor_Black;
		}
	}	
	
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		return super.onTouchEvent(event);
		//up��down���ᴥ���������
//		if(event.getAction()==MotionEvent.ACTION_UP)
//		{
			Chess c=getTouchItem((int)event.getX(),(int)event.getY());
			//û��ѡ��λ�ã���ֱ�ӷ���
			if(c==null||c.x>4||c.y>4)return true;
			//choseChessΪnull,��ǰû��ѡ������
			if(null==choseChess)
			{
				if(playerTurn==ChessColor_Black)
				{
					for(int i=0;i<blackChessArray.size();i++)
					{
						if((c.x==blackChessArray.get(i).x)&&c.y==blackChessArray.get(i).y)
						{
							choseChess=c;
							choseChess.color=blackChessArray.get(i).color;
							invalidate();
							return true;
						}
					}
				}
				else 
				{
					for(int i=0;i<whiteChessArray.size();i++)
					{
						if((c.x==whiteChessArray.get(i).x)&&c.y==whiteChessArray.get(i).y)
						{
							choseChess=c;
							choseChess.color=whiteChessArray.get(i).color;
							invalidate();
							return true;
						}
					}
				}
			}
			else if(choseChess.color==playerTurn)
			{	//�����ǰ�Ѿ���ѡ�У��ڶ��ΰ���ʱ�ƶ��ɹ������ж�Ϊ�ƶ�
				if(true==move(choseChess, c.x,c.y))
				{
					eat(choseChess,c.x,c.y);
					choseChess=null;
					if(playerTurn==ChessColor_Black)playerTurn=ChessColor_White;
					else playerTurn=ChessColor_Black;
					invalidate();
					int win=isWin();
					if(win!=0)
					{
						if(win==ChessColor_White)Toast.makeText(this.getContext(), "�׷���ʤ", Toast.LENGTH_SHORT).show();
						else Toast.makeText(this.getContext(), "�ڷ���ʤ", Toast.LENGTH_SHORT).show();
					}
				}
				else if(cb.chessBoard[c.x][c.y]==choseChess.color)//�ڶ��ΰ���ʱ�ƶ�ʧ�ܣ���Ҫ�ж��Ƿ�ѡ��������������
				{
					choseChess.x=c.x;
					choseChess.y=c.y;
					invalidate();
				}
			}
			return true;
//		}
//		return false;
	}
	
	private Chess getTouchItem(int coordinateX,int coordinateY)
	{
		int offset=boardGridLength/4;
		//�ж��Ƿ����ڽ���
		if(coordinateX<(boardX-offset)||coordinateX>(boardX+boardGridLength*4+offset))return null;
		if(coordinateY<(boardY-offset)||coordinateY>(boardY+boardGridLength*4+offset))return null;
		int x=Math.abs(coordinateX-boardX);
		int y=Math.abs(coordinateY-boardY);
		Chess c=new Chess(10,10,GridEmpty);
		//�ж�x�����ֵ
		if((x%boardGridLength)>(offset*3))c.x=x/boardGridLength+1;
		else if(x%boardGridLength<offset)c.x=x/boardGridLength;
		//�ж�y�����ֵ
		if(y%boardGridLength>offset*3)c.y=y/boardGridLength+1;
		else if(y%boardGridLength<offset)c.y=y/boardGridLength;
		return c;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		cb.draw(canvas);
		for(int i=0;i<blackChessArray.size();i++)blackChessArray.get(i).draw(canvas);
		for(int i=0;i<whiteChessArray.size();i++)whiteChessArray.get(i).draw(canvas);
	}

	public void setChessBoard(DisplayMetrics metrics)
	{
		 int heigth=metrics.heightPixels;
		 int width=metrics.widthPixels;
		 
		 //���򻮷�Ϊ6������ռ4�����Ҹ�������,����ķ��м�
		 boardGridLength=width/6;
		 boardX=boardGridLength+1;
		 boardY=heigth/2-boardGridLength*2;
	}

	//�����Ӵ�����λ���ƶ���ָ�����꣬�����ƶ����
	public boolean move(Chess chess, int x,int y)
	{
		if(cb.isOccupied(x,y)==false)return false;//���Ŀ����ѱ�ռ�ã�ֱ�ӷ���ʧ��
		//�ж��Ƿ��������������ƶ�
		if(((1==Math.abs(x-chess.x))&&(0==Math.abs(y-chess.y)))||//�����ƶ�
				((0==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y))))//�����ƶ�
		{
			cb.chessBoard[chess.x][chess.y]=GridEmpty;
			cb.chessBoard[x][y]=chess.color;
			if(chess.color==ChessColor_Black)
			{
				for(int i=0;i<blackChessArray.size();i++)
				{
					Chess c=blackChessArray.get(i);
					if((chess.x==c.x)&&chess.y==c.y)
					{
						c.x=x;
						c.y=y;
						return true;
					}
				}
			}
			else
			{
				for(int i=0;i<whiteChessArray.size();i++)
				{
					Chess c=whiteChessArray.get(i);
					if((chess.x==c.x)&&chess.y==c.y)
					{
						c.x=x;
						c.y=y;
						return true;
					}
				}
			}
		}
		else if((1==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y)))//б���ƶ�
		{
			if((chess.x+chess.y)%2==0)//�������Ͽ��������Ҹ�Ϊż����ռ��б���ƶ�
			{
				cb.chessBoard[chess.x][chess.y]=GridEmpty;
				cb.chessBoard[x][y]=chess.color;
				if(chess.color==ChessColor_Black)
				{
					for(int i=0;i<blackChessArray.size();i++)
					{
						Chess c=blackChessArray.get(i);
						if((chess.x==c.x)&&chess.y==c.y)
						{
							c.x=x;
							c.y=y;
							return true;
						}
					}
				}
				else
				{
					for(int i=0;i<whiteChessArray.size();i++)
					{
						Chess c=whiteChessArray.get(i);
						if((chess.x==c.x)&&chess.y==c.y)
						{
							c.x=x;
							c.y=y;
							return true;
						}
					}
				}
			}
			else return false;
		}
		return false;
	}
	//��chess�ƶ���Ŀ��x,y��Ҫ�ж��Ƿ���ӣ��������Ե�����Ϊ�������
	//����ǰ����ǰ���Ѿ�����move���жϣ�����Ϊ�ɹ����������ﲻ��move�����ж�
	public void eat(Chess chess, int x, int y)
	{
		ArrayList<Chess> eatResult= new ArrayList<Chess>();
		ArrayList<Chess> myArray=blackChessArray;
		ArrayList<Chess> yourArray=whiteChessArray;
		if(chess.color==ChessColor_White)
		{
			myArray=whiteChessArray;
			yourArray=blackChessArray;
		}
		int eatCount=eatDing(eatResult,chess,x,y);
		eatCount+=eatJia(eatResult,chess,x,y);
		eatCount+=eatTiao(eatResult,chess,x,y);
		for(int i=0;i<eatCount;i++)
		{
			Chess eat=eatResult.get(i);
			for(int j=0;j<yourArray.size();j++)
			{
				if(yourArray.get(j).x==eat.x&&yourArray.get(j).y==eat.y)
				{
					yourArray.remove(j);
					eat.color=chess.color;
					cb.chessBoard[chess.x][chess.y]=GridEmpty;
					cb.chessBoard[eat.x][eat.y]=chess.color;
					myArray.add(eat);
				}
			}
		}
	}
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ������������ͬɫ�Ӷ�����һɫ�ӣ����Ҹ�����û������������
	public int eatDing(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
//		ArrayList<Chess> eatResult= new ArrayList<Chess>();
		if(eatResult==null)return 0;
		int eatCount=0;
//		ArrayList<Chess> myArray=blackChessArray;
//		ArrayList<Chess> yourArray=whiteChessArray;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
//			myArray=whiteChessArray;
//			yourArray=blackChessArray;
			yourColor=ChessColor_Black;
		}
		//���Ĳ������жϣ��ᣬ������б����б
		//���߳ԣ�
		int myCount=0;
		int yourCount=0;
		Chess yourChess = null;
		Chess mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[i][y]==GridEmpty)continue;
			else if(cb.chessBoard[i][y]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,y,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,y,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.x-x)+(yourChess.x-mySecondChess.x))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//���߳�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[x][i]==GridEmpty)continue;
			else if(cb.chessBoard[x][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(x,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(x,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.y-y)+(yourChess.y-mySecondChess.y))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		if((x+y)%2!=0)return eatCount;//���жϸõ��ǲ���ż����
		//��б�߳�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int ix=i+(x-y);
			if(ix<0||ix>4)continue;
			if(cb.chessBoard[ix][i]==GridEmpty)continue;
			else if(cb.chessBoard[ix][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(ix,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(ix,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.x-x)+(yourChess.x-mySecondChess.x))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//��б�߳�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int iy=x+y-i;
			if(iy<0||iy>4)continue;
			if(cb.chessBoard[i][iy]==GridEmpty)continue;
			else if(cb.chessBoard[i][iy]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,iy,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,iy,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.x-x)+(yourChess.x-mySecondChess.x))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		return eatCount;
	}

	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ������ͬɫ�����Ӽ�����һɫ�ӣ����Ҹ�����û������������
	public int eatJia(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//���Ĳ������жϣ��ᣬ������б����б
		//���߼У�
		int myCount=0;
		int yourCount=0;
		Chess yourChess = null;
		Chess mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[i][y]==GridEmpty)continue;
			else if(cb.chessBoard[i][y]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,y,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,y,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.x-x)==1&&Math.abs(yourChess.x-mySecondChess.x)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//���߼�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[x][i]==GridEmpty)continue;
			else if(cb.chessBoard[x][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(x,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(x,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.y-y)==1&&Math.abs(yourChess.y-mySecondChess.y)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		if((x+y)%2!=0)return eatCount;//���жϸõ��ǲ���ż����
		//��б�߼�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int ix=i+(x-y);
			if(ix<0||ix>4)continue;
			if(cb.chessBoard[ix][i]==GridEmpty)continue;
			else if(cb.chessBoard[ix][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(ix,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(ix,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.x-x)==1&&Math.abs(yourChess.x-mySecondChess.x)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//��б�߼�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int iy=x+y-i;
			if(iy<0||iy>4)continue;
			if(cb.chessBoard[i][iy]==GridEmpty)continue;
			else if(cb.chessBoard[i][iy]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,iy,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,iy,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.x-x)==1&&Math.abs(yourChess.x-mySecondChess.x)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		return eatCount;
	}
	
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ�������������߸���һ����ɫ�ӣ��Ҵ�����ֻ���������ӣ�������ɫ�ӱ���
	public int eatTiao(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//���Ĳ������жϣ��ᣬ������б����б
		//��������
		int myCount=0;
		int yourCount=0;
		Chess yourFirstChess = null;
		Chess yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[i][y]==GridEmpty)continue;
			else if(cb.chessBoard[i][y]==chess.color)
			{
				myCount++;
//				if(x!=i)mySecondChess=new Chess(i,y,chess.color);
			}
			else
			{
				yourCount++;
//				yourChess=new Chess(i,y,yourColor);
				if(yourCount==1)yourFirstChess=new Chess(i,y,yourColor);
				else yourSecondChess=new Chess(i,y,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.x-x)==1&&Math.abs(yourSecondChess.x-x)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		//������
		myCount=0;
		yourCount=0;
		yourFirstChess = null;
		yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[x][i]==GridEmpty)continue;
			else if(cb.chessBoard[x][i]==chess.color)
			{
				myCount++;
//				if(y!=i)mySecondChess=new Chess(x,i,chess.color);
			}
			else
			{
				yourCount++;
				if(yourCount==1)yourFirstChess=new Chess(x,i,yourColor);
				else yourSecondChess=new Chess(x,i,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.y-y)==1&&Math.abs(yourSecondChess.y-y)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		if((x+y)%2!=0)return eatCount;//���жϸõ��ǲ���ż����
		//��б����
		myCount=0;
		yourCount=0;
		yourFirstChess = null;
		yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			int ix=i+(x-y);
			if(ix<0||ix>4)continue;
			if(cb.chessBoard[ix][i]==GridEmpty)continue;
			else if(cb.chessBoard[ix][i]==chess.color)
			{
				myCount++;
//				if(y!=i)mySecondChess=new Chess(ix,i,chess.color);
			}
			else
			{
				yourCount++;
				if(yourCount==1)yourFirstChess=new Chess(ix,i,yourColor);
				else yourSecondChess=new Chess(ix,i,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.x-x)==1&&Math.abs(yourSecondChess.x-x)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		//��б����
		myCount=0;
		yourCount=0;
		yourFirstChess = null;
		yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			int iy=x+y-i;
			if(iy<0||iy>4)continue;
			if(cb.chessBoard[i][iy]==GridEmpty)continue;
			else if(cb.chessBoard[i][iy]==chess.color)
			{
				myCount++;
//				if(x!=i)mySecondChess=new Chess(i,iy,chess.color);
			}
			else
			{
				yourCount++;
				if(yourCount==1)yourFirstChess=new Chess(i,iy,yourColor);
				else yourSecondChess=new Chess(i,iy,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.x-x)==1&&Math.abs(yourSecondChess.x-x)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		return eatCount;
	}
	
	//�ж��Ƿ���һ��ʤ������Ϸ����
	//���ȿ��Ƿ���һ����������Ϊ0�����Ϊ0��ʧ�ܣ���һ��ʤ��
	//�����һ��������Ϊ1����������ߣ���÷�ʧ�ܣ���һ��ʤ��
	private int isWin()
	{
		int blackCount=blackChessArray.size();
		int whiteCount=whiteChessArray.size();
		int smaller=Math.min(blackCount,whiteCount);
		if(smaller==0)
		{
			if(blackCount==smaller)
			{
				return ChessColor_White;
			}
			else return ChessColor_Black;	
		}
		else if(smaller==1)
		{
			Chess loser=whiteChessArray.get(0);;
			Chess winer=blackChessArray.get(0);;
			if(blackCount==smaller)
			{
				loser=blackChessArray.get(0);
				winer=whiteChessArray.get(0);
			}
			int moveCount=0;
			//���ж����������Ƿ񶼱�ռ��
			if(cb.isOccupied(loser.x-1, loser.y)==false)moveCount++;
			if(cb.isOccupied(loser.x, loser.y-1)==false)moveCount++;
			if(cb.isOccupied(loser.x+1, loser.y)==false)moveCount++;
			if(cb.isOccupied(loser.x, loser.y+1)==false)moveCount++;
			if((loser.x+loser.y)%2!=0&&moveCount==4)return winer.color;
			if((loser.x+loser.y)%2==0)
			{
				if(cb.isOccupied(loser.x-1, loser.y-1)==false)moveCount++;
				if(cb.isOccupied(loser.x+1, loser.y-1)==false)moveCount++;
				if(cb.isOccupied(loser.x-1, loser.y+1)==false)moveCount++;
				if(cb.isOccupied(loser.x+1, loser.y+1)==false)moveCount++;
				if(moveCount==8)return winer.color;
			}
		}
		return 0;
	}
	
	//����һ��5*5�����̣��ܹ�10�����ӣ�ÿһ��������Ϊ���ӵ���ɫ
	private class ChessBoard{
		private int[][] chessBoard;
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
		private void draw(Canvas canvas)
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
		private boolean isOccupied(int x, int y)
		{
			if(x<0||x>4)return false;
			if(y<0||y>4)return false;
			if(chessBoard[x][y]==GridEmpty)return true;
			return false;
		}
		
	}
	//������
	private class Chess {
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
		//�ڻ����ϻ����ӣ���x,yΪ�������꣬������ɫ�Ĳ�ͬ��һ��Բ���뾶Ϊ����С��1/4
		private void draw(Canvas canvas)
		{
			Paint p=new Paint();
			//�ж������Ƿ����ѡ�е����ӣ��ǵĻ����ɻ�ɫ
			if(choseChess!=null&&this.x==choseChess.x&&this.y==choseChess.y)p.setColor(Color.YELLOW);
			else if(ChessColor_Black==color)p.setColor(Color.BLACK);
			else p.setColor(Color.LTGRAY);
			canvas.drawCircle(boardX+x*boardGridLength, boardY+y*boardGridLength, boardGridLength/4, p);
		}
	}
}
