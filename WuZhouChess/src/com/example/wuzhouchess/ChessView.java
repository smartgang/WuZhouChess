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



/**五洲棋
 * @author SmartGang
 * @version 1.0
 */
public class ChessView extends android.view.View {

	//定义棋子的两个颜色
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int GridEmpty=0;
	//用来表示轮到哪一下颜色的选手走棋
	public int playerTurn;
	//用于表示当前的按键状态，非空则表示当前已经选中棋子
	Chess choseChess=null;
	//定义棋盘上每一格的宽高,默认为20，实际根据屏幕大小来计算
	private int boardGridLength=20;
	//棋盘左上角的坐标，根据屏幕中心点的位置，结合每一格的宽高来计算，默认是600*480
	private int boardX=80;
	private int boardY=140;
	//两个颜色的棋子序列，初始各为5个，减为0个则输，达到10个判赢
	private ArrayList<Chess> blackChessArray= new ArrayList<Chess>();
	private ArrayList<Chess> whiteChessArray= new ArrayList<Chess>();
	ChessBoard cb;
	
	
	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//初始始化棋盘及两列棋子，黑棋在上，白棋在下
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
			//黑方先走
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
		//初始始化棋盘及两列棋子，黑棋在上，白棋在下
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
			//黑方先走
			playerTurn=ChessColor_Black;
		}
	}

	/**
	 * @param context
	 */
	public ChessView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//初始始化棋盘及两列棋子，黑棋在上，白棋在下
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
			//黑方先走
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
		//up和down均会触发这个函数
//		if(event.getAction()==MotionEvent.ACTION_UP)
//		{
			Chess c=getTouchItem((int)event.getX(),(int)event.getY());
			//没有选中位置，则直接返回
			if(c==null||c.x>4||c.y>4)return true;
			//choseChess为null,则当前没有选中棋子
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
			{	//如果当前已经有选中，第二次按下时移动成功，则判断为移动
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
						if(win==ChessColor_White)Toast.makeText(this.getContext(), "白方获胜", Toast.LENGTH_SHORT).show();
						else Toast.makeText(this.getContext(), "黑方获胜", Toast.LENGTH_SHORT).show();
					}
				}
				else if(cb.chessBoard[c.x][c.y]==choseChess.color)//第二次按下时移动失败，则要判断是否选中了其他的棋子
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
		//判断是否是在界外
		if(coordinateX<(boardX-offset)||coordinateX>(boardX+boardGridLength*4+offset))return null;
		if(coordinateY<(boardY-offset)||coordinateY>(boardY+boardGridLength*4+offset))return null;
		int x=Math.abs(coordinateX-boardX);
		int y=Math.abs(coordinateY-boardY);
		Chess c=new Chess(10,10,GridEmpty);
		//判断x坐标的值
		if((x%boardGridLength)>(offset*3))c.x=x/boardGridLength+1;
		else if(x%boardGridLength<offset)c.x=x/boardGridLength;
		//判断y坐标的值
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
		 
		 //横向划分为6格，棋盘占4格，左右各空两格,竖向的放中间
		 boardGridLength=width/6;
		 boardX=boardGridLength+1;
		 boardY=heigth/2-boardGridLength*2;
	}

	//将棋子从自身位置移动到指定坐标，返回移动结果
	public boolean move(Chess chess, int x,int y)
	{
		if(cb.isOccupied(x,y)==false)return false;//如果目标格已被占用，直接返回失败
		//判断是否是向上下左右移动
		if(((1==Math.abs(x-chess.x))&&(0==Math.abs(y-chess.y)))||//左右移动
				((0==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y))))//上下移动
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
		else if((1==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y)))//斜线移动
		{
			if((chess.x+chess.y)%2==0)//从棋盘上看，点左右各为偶数的占可斜线移动
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
	//将chess移动到目标x,y后，要判断是否吃子，并将被吃的子做为结果返回
	//函数前提是前面已经做过move的判断，并且为成功，所以这里不对move进行判断
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
	//吃法：顶
	//在一条模，纵，斜线上，移动后该子与相邻同色子顶着另一色子，并且该线上没有其他的棋子
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
		//分四步进行判断：横，竖，左斜，右斜
		//横线吃：
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
		//竖线吃
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
		if((x+y)%2!=0)return eatCount;//先判断该点是不是偶数点
		//左斜线吃
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
		//右斜线吃
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

	//吃法：夹
	//在一条模，纵，斜线上，移动后该与同色的棋子夹着另一色子，并且该线上没有其他的棋子
	public int eatJia(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//分四步进行判断：横，竖，左斜，右斜
		//横线夹：
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
		//竖线夹
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
		if((x+y)%2!=0)return eatCount;//先判断该点是不是偶数点
		//左斜线夹
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
		//右斜线夹
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
	
	//吃法：挑
	//在一条模，纵，斜线上，移动后该子左右两边各有一个异色子，且此线上只有这三个子，两个异色子被挑
	public int eatTiao(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//分四步进行判断：横，竖，左斜，右斜
		//横线挑：
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
		//竖线挑
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
		if((x+y)%2!=0)return eatCount;//先判断该点是不是偶数点
		//左斜线挑
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
		//右斜线挑
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
	
	//判断是否有一方胜利，游戏结束
	//首先看是否有一方的棋子数为0，如果为0则失败，另一方胜利
	//如果有一方棋子数为1，则无棋可走，则该方失败，另一方胜利
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
			//先判断上下左右是否都被占用
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
	
	//定义一个5*5的棋盘，总共10个棋子，每一个的内容为棋子的颜色
	private class ChessBoard{
		private int[][] chessBoard;
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
		private void draw(Canvas canvas)
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
		private boolean isOccupied(int x, int y)
		{
			if(x<0||x>4)return false;
			if(y<0||y>4)return false;
			if(chessBoard[x][y]==GridEmpty)return true;
			return false;
		}
		
	}
	//棋子类
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
		//在画布上画棋子，以x,y为中心坐标，根据颜色的不同画一个圆，半径为棋格大小的1/4
		private void draw(Canvas canvas)
		{
			Paint p=new Paint();
			//判断自身是否就是选中的棋子，是的话画成黄色
			if(choseChess!=null&&this.x==choseChess.x&&this.y==choseChess.y)p.setColor(Color.YELLOW);
			else if(ChessColor_Black==color)p.setColor(Color.BLACK);
			else p.setColor(Color.LTGRAY);
			canvas.drawCircle(boardX+x*boardGridLength, boardY+y*boardGridLength, boardGridLength/4, p);
		}
	}
}
