/**
 * 
 */
package com.example.wuzhouchess.Views;

import java.util.ArrayList;

import com.example.classes.Chess;
import com.example.classes.ChessBoard;
import com.example.classes.Movement;
import com.example.classes.Player;
import com.example.classes.TimeCounter;
import com.example.wuzhouchess.PlayActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;
import android.util.Log;


/**五洲棋
 * @author SmartGang
 * @version 1.0
 */
public class ChessView extends android.view.View {
	
	final private static String LOG_TGA="ChessView";
	
	//定义游戏的各个状态
	final public static int GameStatus_Ready=1;
	final public static int GameStatus_Playing=2;
	final public static int GameStatus_Pause=3;
	final public static int GameStatus_Over=4;
	public int gameStatus=GameStatus_Ready;
	
	//定义棋子的两个颜色
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	final public static int GridEmpty=0;

	//定义onTouch与actionHandler消息传递的值，用于与用户操作交互
//	final public static int ACTION_MOVE=1;
	
	//用于表示当前的按键状态，非空则表示当前已经选中棋子
	Chess choseChess=null;
	Chess movedChess=null;
	//定义棋盘上每一格的宽高,默认为20，实际根据屏幕大小来计算
	private int boardGridLength=20;
	//棋盘左上角的坐标，根据屏幕中心点的位置，结合每一格的宽高来计算，默认是600*480
	private int boardX=80;
	private int boardY=140;
	//两个颜色的棋子序列，初始各为5个，减为0个则输，达到10个判赢
	private ArrayList<Chess> blackChessArray;
	private ArrayList<Chess> whiteChessArray;
	ChessBoard cb;
	Player playerWhite;
	Player playerBlack;
	Player currentPlayer;
	private TimeCounter timeCounter;
	private Handler timeCounterHandler;
	private Handler viewHolderHandler;
	private Handler actionHandler;
	private Thread actionThread;
	
	private boolean isTouched;
	/**
	 * @param viewHolerHandler the viewHolerHandler to set
	 */
	public void setViewHolderHandler(Handler viewHolderHandler) {
		this.viewHolderHandler = viewHolderHandler;
	}

	private boolean isWaiting=true;
	private int moveTargetX;
	private int moveTargetY;
	
	private int currentRoundTime;
	
	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initialCounterAndHandlers();
		initialChessBorad();

	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ChessView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initialCounterAndHandlers();
		initialChessBorad();
	}

	/**
	 * @param context
	 */
	public ChessView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialCounterAndHandlers();
		initialChessBorad();
	}	
	//初始化计算器和handler
	private void initialCounterAndHandlers()
	{
		timeCounterHandler=new Handler(){

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				currentRoundTime=msg.what;
//				Log.d(LOG_TGA,"updating currentRoundTime");
				//更新计时器后刷新整个显示
				updateView();
			}
			
		};
		//创建timeCounter并添加handler
		timeCounter=new TimeCounter(timeCounterHandler);
		
		actionHandler=new Handler(){

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				invalidate();
			}
			
		};
		
		actionThread=new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
	            try {
					while(gameStatus!=GameStatus_Pause&&gameStatus!=GameStatus_Over)
					{	
						//等待用户操作,0.2秒检测一次
						while(isWaiting&&movedChess==null)Thread.sleep(200);
						Log.d(LOG_TGA,"user ating");
						//由用户进行移动判断，当用户是人类玩家时，其实在OnTouch中已经做过isMoveable的判断
						//AI时没做判断,增加AI判断的话，需要考虑如何将判断结果反馈回给AI？
						Movement movement=currentPlayer.move(cb, movedChess, moveTargetX, moveTargetY);
						//之后移动棋盘上的内容
						move(movedChess,movement.toX,movement.toY);
						eat(movedChess,moveTargetX,moveTargetY);
						int win=isWin();
						if(win!=0)
						{
							//游戏结束
							gameOver();
							Message msg=new Message();
							msg.obj=gameStatus;
							msg.what=GameStatus_Over;
							viewHolderHandler.sendMessage(msg);
						}
						movedChess=null;
						//交换用户
						switchPlayer();
						//交换用户后如果当前用户是人类玩家，则需要等待用户操作
						if(currentPlayer.getPlayerType()==Player.PlayerType_Human)isWaiting=true;
						updateView();
					}
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			}
		});		
	}
	//初始化棋盘，将棋子恢复到原位
	private void initialChessBorad()
	{
		//初始始化棋盘及两列棋子，黑棋在上，白棋在下
		blackChessArray=new ArrayList<Chess>();
		whiteChessArray=new ArrayList<Chess>();
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
			cb=new ChessBoard();
		}
	}
	
	private void updateView()
	{
		Message msg;
		actionHandler.sendEmptyMessage(0);
	}
	//走完一步后，轮到下一方
	private void switchPlayer()
	{
		Log.d(LOG_TGA,"switchPlayer");
		//加上totalPlayTime
		currentPlayer.setTotalPlayTime(currentRoundTime+currentPlayer.getTotalPlayTime());
		if(currentPlayer.getColor()==Player.ChessColor_Black)currentPlayer=playerWhite;
		else currentPlayer=playerBlack;
		//重置计数器
		timeCounter.resetCounter();
		currentRoundTime=0;
	}
	
	/**设置玩家
	 * @param white
	 * @param black
	 */
	public void setPlayer(Player white,Player black)
	{
		playerWhite=white;
		playerBlack=black;
		playerBlack.setChessArray(blackChessArray);
		playerWhite.setChessArray(whiteChessArray);
		currentPlayer=playerBlack;
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
		isTouched=!isTouched;
		if(isTouched==false)return true;
		Log.d(LOG_TGA,"onTouchEvent");
		if(currentPlayer.getPlayerType()==Player.PlayerType_AI)return true;
			Chess c=getTouchItem((int)event.getX(),(int)event.getY());
			//没有选中位置，则直接返回
			if(c==null||c.x>4||c.y>4)return true;
			//choseChess为null,则当前没有选中棋子
			if(null==choseChess)
			{
				Log.d(LOG_TGA,"onTouchEvent selecting");
				for(int i=0;i<currentPlayer.getChessArray().size();i++)
				{
					if((c.x==currentPlayer.getChessArray().get(i).x)&&c.y==currentPlayer.getChessArray().get(i).y)
					{
						choseChess=c;
						choseChess.color=currentPlayer.getChessArray().get(i).color;
						updateView();
						return true;
					}
				}
			}
			else if(choseChess.color==currentPlayer.getColor())
			{	//如果当前已经有选中，第二次按下时移动成功，则判断为移动
				if(true==isMovable(choseChess, c.x,c.y))
				{
					Log.d(LOG_TGA,"onTouchEvent moving");
					movedChess=new Chess(choseChess.x,choseChess.y,choseChess.color);
					choseChess=null;
					//通过action线程，用户已完成移动
					moveTargetX=c.x;
					moveTargetY=c.y;
					isWaiting=false;
				}
				else if(cb.chessBoard[c.x][c.y]==choseChess.color)//第二次按下时移动失败，则要判断是否选中了其他的棋子
				{
					choseChess.x=c.x;
					choseChess.y=c.y;
					updateView();
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
		if(gameStatus==GameStatus_Playing)
		{	
			Log.d(LOG_TGA,"onDrawing");
			cb.draw(canvas,boardX, boardY,boardGridLength);
			for(int i=0;i<blackChessArray.size();i++)blackChessArray.get(i).draw(canvas, boardX, boardY, boardGridLength,ChessColor_Black);
			for(int i=0;i<whiteChessArray.size();i++)whiteChessArray.get(i).draw(canvas,boardX, boardY, boardGridLength,ChessColor_White);
			if(null!=choseChess)choseChess.draw(canvas, boardX, boardY, boardGridLength,  ChessColor_Yellow);	
			if(choseChess==null)Log.d(LOG_TGA,"choseChess is null");
			drawTotalTime(canvas);
			drawCurrentRoundTime(canvas);
		}
	}

	//左棋盘上下方的左侧，分别显示两边选择总共花费的时间,黑上白下
	private void drawTotalTime(Canvas canvas)
	{
		int blackTime=playerBlack.getTotalPlayTime();
		int whiteTime=playerWhite.getTotalPlayTime();
		Paint paint=new Paint();
		paint.setTextSize(boardGridLength/2);//字体在小设为间隔的一半
		paint.setColor(Color.LTGRAY);
		String time=String.valueOf(blackTime/3600)+":"
						+String.valueOf((blackTime%3600)/60)+":"
						+String.valueOf(blackTime%60);
		//上下要对称，所以这里boardGridLength除以2是必要的
		canvas.drawText(time, boardX, boardY-boardGridLength/2, paint);
		String time2=String.valueOf(whiteTime/3600)+":"
						+String.valueOf((blackTime%3600)/60)+":"
						+String.valueOf(whiteTime%60);
		canvas.drawText(time2, boardX, boardY+boardGridLength*5, paint);
	}
	//画出当前轮用户游戏时间
	private void drawCurrentRoundTime(Canvas canvas)
	{
		//位置起始点，根据用户颜色的不同，黑上白下
		int startY;
		if(currentPlayer.getColor()==Player.ChessColor_Black)startY=boardY-boardGridLength/2;
		else startY=boardY+boardGridLength*5;
		Paint paint=new Paint();
		paint.setTextSize(boardGridLength/2);//字体在小设为间隔的一半
		paint.setColor(Color.LTGRAY);
		String time=String.valueOf(currentRoundTime/3600)+":"
						+String.valueOf((currentRoundTime%3600)/60)+":"
						+String.valueOf(currentRoundTime%60);
		canvas.drawText(time, boardX+boardGridLength*2, startY, paint);
	}
	public void setChessBoard()
	{
		 //横向划分为6格，棋盘占4格，左右各空两格,竖向的放中间
		int left=getLeft();
		int top=getTop();
		int right=getRight();
		int bottom=getBottom();		
		boardGridLength=(right-left)/6;
		boardX=boardGridLength+1;
		boardY=(bottom-top)/2-boardGridLength*2;
	}
	//开始
	public void gameStart()
	{
		Log.d(LOG_TGA,"gameStart");
		isWaiting=true;
		timeCounter.startCounter();
		timeCounter.start();
		actionThread.start();
		gameStatus=GameStatus_Playing;
	}
	//游戏结束后重新开始
	public void gameRestart()
	{
		isWaiting=true;
		timeCounter.startCounter();	
		gameStatus=GameStatus_Playing;
		initialChessBorad();
	}
	//暂停
	public void gamePause()
	{
		timeCounter.pauseCounter();
		gameStatus=GameStatus_Pause;
	}
	//暂停后恢复
	public void gameResume()
	{
		timeCounter.startCounter();
		gameStatus=GameStatus_Playing;
	}
	public void gameOver()
	{
		Log.d(LOG_TGA,"gameOver");
		isWaiting=false;
		timeCounter.stopCounter();
//		actionThread.stop();
		gameStatus=GameStatus_Over;
	}
	//判断将chess移动到x,y是否可行
	public boolean isMovable(Chess chess, int x, int y)
	{
		if(cb.isOccupied(x,y)==false)return false;//如果目标格已被占用，直接返回失败
		//判断是否是向上下左右移动
		if(((1==Math.abs(x-chess.x))&&(0==Math.abs(y-chess.y)))||//左右移动
				((0==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y))))//上下移动
		{
			return true;
		}
		else if((1==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y)))//斜线移动
		{
			if((chess.x+chess.y)%2==0)//从棋盘上看，点左右各为偶数的占可斜线移动
			{
				return true;
			}
		}
		Log.d(LOG_TGA,"moving is true");
		return false;		
	}
	//将棋子从自身位置移动到指定坐标，返回移动结果
	public void move(Chess chess, int x,int y)
	{
		cb.chessBoard[chess.x][chess.y]=GridEmpty;
		cb.chessBoard[x][y]=chess.color;
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
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
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
	
}
