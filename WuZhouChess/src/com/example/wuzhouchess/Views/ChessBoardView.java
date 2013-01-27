/**
 * 
 */
package com.example.wuzhouchess.Views;

import java.util.ArrayList;

import com.example.classes.AIPlayer;
import com.example.classes.Chess;
import com.example.classes.ChessBoard;
import com.example.classes.Movement;
import com.example.classes.Player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author SmartGang
 *
 */
public class ChessBoardView extends View {

	final private static String LOG_TGA="ChessBoardView";
	
	//定义棋子的颜色
	final public static int GridEmpty=0;
	final public static int ChessColor_Black=1;	
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	
	//定义游戏的状态，使用一个变量进行控制
	final public static int GAME_STATUS_PREPARE=0;
	final public static int GAME_STATUS_PLAYING=1;
	final public static int GAME_STATUS_PAUSE=2;
	final public static int GAME_STATUS_OVER=3;
	private int gameStatus=0;
	
	//定义消息,用msg.what承载
	final public static int ACTION_MSG_EMPTY=0;
	final public static int ACTION_MSG_MOVEMENT=1;//msg.obj保存movement信息
	final public static int ACTION_MSG_START=2;
	final public static int ACTION_MSG_PAUSE=3;
	final public static int ACTION_MSG_RESUME=4;
	final public static int ACTION_MSG_OVER=5;//msg.obj保存胜利或者失败
	final public static int ACTION_MSG_RESTART=6;
	final public static int ACTION_MSG_VITORY=7;
	final public static int ACTION_MSG_FAIL=8;
	final public static int ACTION_MSG_EXIT=9;
	
	private Handler messageHandler;//用于与载体通信,在构造函数中带入
	private static Handler heartBeatHandler;
	private Thread heartBeatThread;
	public static Handler actionHandler;
	//两个玩家
	private Player playerUp;
	private Player playerDown;
	private Player currentPlayer;
	private Player opponentPlayer;
	//当前回合玩家所花费的时间
	private int currentRoundTime;
	//两个颜色的棋子序列，初始各为5个，减为0个则输，达到10个判赢
	private ArrayList<Chess> blackChessArray;
	private ArrayList<Chess> whiteChessArray;
	private ChessBoard chessBoard;
	private int isTouchable=0;
	final private static int TOUCH_GAP=5;
	private Chess lastChosedChess;//保存玩家操作过程中，上一轮选中的棋子
	
	private boolean heartBeatFlag=false;
	//定义一系列界面元素
	private ViewItem leftButton;
	final private static int LEFT_BUTTON_STARTX=5;
	final private static int LEFT_BUTTON_STARTY=30;
	final private static int LEFT_BUTTON_HEIGTH=45;
	final private static int LEFT_BUTTON_LENGTH=100;
	private ViewItem rightButton;
	final private static int RIGHT_BUTTON_STARTX=5;
	final private static int RIGHT_BUTTON_STARTY=30;
	final private static int RIGHT_BUTTON_HEIGTH=45;
	final private static int RIGHT_BUTTON_LENGTH=100;
	private ViewItem upTotalTime;
	final private static int DISTANCE_TIME_TO_BOARD=20;
	private ViewItem downTotalTime;
	private ViewItem upCurrentTime;
	private ViewItem downCurrentTime;
	final private static int DISTANCE_NAME_TO_BOARD=40;
	private ViewItem upPlayerName;
	private ViewItem downPlayerName;
	private int boardX;
	private int boardY;
	private int boardGridLength;
		
	
	public ChessBoardView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param context
	 * @param messageHandler
	 */
	public ChessBoardView(Context context, Handler messageHandler) {
		super(context);
		this.messageHandler = messageHandler;
		gameInitialize();
	}
	public void startHeartBeat()
	{
		heartBeatThread.start();		
	}
	//游戏初始化，设置所有需要的资源，启动心跳
	private void gameInitialize()
	{
		chessBoardInitialize();
		initialControlResource();
		initialViewResource();
		gameStatus=GAME_STATUS_PREPARE;
		heartBeatFlag=true;
//		heartBeatThread.start();
	}
	//初始始化棋盘及两列棋子，黑棋在上，白棋在下
	private void chessBoardInitialize()
	{
		chessBoard=new ChessBoard();
		if(playerUp!=null)playerUp.setChessArray(getChessArray(0,playerUp.getColor()));
		if(playerDown!=null)playerDown.setChessArray(getChessArray(4,playerDown.getColor()));
/*		blackChessArray=new ArrayList<Chess>();
		whiteChessArray=new ArrayList<Chess>();
		for(int i=0;i<5;i++)
		{
			Chess chess=new Chess(i,0,ChessColor_Black);
			blackChessArray.add(chess);
			Chess chess2=new Chess(i,4,ChessColor_White);
			whiteChessArray.add(chess2);
		}
*/
	}
	//初始化棋子，用于附给玩家
	private ArrayList<Chess> getChessArray(int position, int color)
	{
		if(color==Chess.ChessColor_Black)
		{
			blackChessArray=new ArrayList<Chess>();
			for(int i=0;i<5;i++)
			{
				Chess chess=new Chess(i,position,ChessColor_Black);
				blackChessArray.add(chess);
				chessBoard.chessBoard[i][position]=color;
			}
			return blackChessArray;
		}
		else if(color==Chess.ChessColor_White)
		{
			whiteChessArray=new ArrayList<Chess>();
			for(int i=0;i<5;i++)
			{
				Chess chess=new Chess(i,position,ChessColor_White);
				whiteChessArray.add(chess);
				chessBoard.chessBoard[i][position]=color;
			}
			return whiteChessArray;
		}
		return null;
	}
	//游戏开始，会重新初始化heartBeat线程
	private void gameStart()
	{
		// TODO:
		//1.检查玩家到位情况
		//2.设置游戏 
		//3.检查是否是AI先走
		if(playerUp==null||playerDown==null)return;
		//黑棋先走
		if(playerDown.getColor()==Player.ChessColor_Black)
		{
			currentPlayer=playerDown;
			opponentPlayer=playerUp;
		}
		else
		{
			currentPlayer=playerUp;
			opponentPlayer=playerDown;
		}
		
		upPlayerName.setText(playerUp.getPlayerName());
		downPlayerName.setText(playerDown.getPlayerName());
		
		gameStatus=GAME_STATUS_PLAYING;//开始游戏
		
		//要对AI玩家进行判断，如果AI先走，则在这里要做判断
		if(currentPlayer.getPlayerType()==Player.PlayerType_AI)
		{
			//强制向下塑形成AIPlayer
			Movement bestMovement=((AIPlayer)currentPlayer).getBestMovement(chessBoard);
			Message msgMovement=new Message();
			msgMovement.what=ACTION_MSG_MOVEMENT;
			msgMovement.obj=bestMovement;
			actionHandler.sendMessage(msgMovement);
		}				
	}
	//暂停游戏，将游戏状态设置为暂停
	private void gamePause()
	{
		gameStatus=GAME_STATUS_PAUSE;
	}
	//恢复游戏，将游戏状态设置为游戏中
	private void gameResume()
	{
		gameStatus=GAME_STATUS_PLAYING;
	}
	//重新开始
	private void gameRestart()
	{
		//TODO
		//1.重新初始化棋盘
		//2.新局要双方交换棋子颜色（但是玩家位置不变）
		//3.再重新开始
		
		int color=playerDown.getColor();
		playerDown.setColor(playerUp.getColor());
		playerUp.setColor(color);

		playerDown.setTotalPlayTime(0);
		playerUp.setTotalPlayTime(0);
		
		chessBoardInitialize();
		
		gameStart();
		
	}
	//游戏结束
	private void gameOver()
	{
		gameStatus=GAME_STATUS_OVER;
	}
	//初始始化控制资源
	private void initialControlResource()
	{
		//TODO:
		//1.heartBeatHandler
		//2.actionHandler
		//3.heartBeat线程初始化
		
		//设置heartBeatHandler，处理心跳信息
		heartBeatHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				//1.计时
				//2.更新画面
				
				if(gameStatus==GAME_STATUS_PLAYING&&msg.what==10)
				{
					currentRoundTime++;
					//每加一秒，就顺便把时间显示好
					String time=String.valueOf(currentRoundTime/3600)+":"
								+String.valueOf((currentRoundTime%3600)/60)+":"
								+String.valueOf(currentRoundTime%60);
					if(currentPlayer.getColor()==playerUp.getColor())
					{
						upCurrentTime.setText(time);
					}
					else downCurrentTime.setText(time);
				}
				//更新画面
				invalidate();
			}			
		};
		
		//设置actionHandler，用于接收来自界面和网络的操作信息，并执行相应的操作
		actionHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				// 消息类型包括：移动，暂停，恢复，重新开始，认输，胜利
				switch(msg.what){
				case ACTION_MSG_MOVEMENT://调用移动棋子函数
					 //* 1.先调用player.move，再调用chessBoard.move
					 //* 2.根据对手的类型做下一步操作
					 //* 2.1 如果对手是internet玩家，则要将movement通过messageHandler发送出去
					 //* 2.2 移动后再做吃子、输赢的判断
					 //* 2.2 如果对手是AI玩家，则switchPlaer之后，要再执行一下move的操作，以执行AI的走棋
					Movement lastMove=(Movement)msg.obj;
					//判断要移动的棋子是否属于当前玩家，防止误走
					if(chessBoard.chessBoard[lastMove.fromX][lastMove.fromY]!=currentPlayer.getColor()) break;
					if(moveChess(lastMove)==false)break;
					if(opponentPlayer.getPlayerType()==Player.PlayerType_Internet)
					{	//如果对手是网络玩家，直接将消息转发出去
						//注意：由于两边的角度是互换的，所以movement的信息也要互换
						Message msg2Internet=new Message();
						msg2Internet.what=msg.what;
						Movement remoteMovemen=new Movement(4-lastMove.fromX,4-lastMove.fromY,4-lastMove.toX,4-lastMove.toY);
						msg2Internet.obj=remoteMovemen;
						messageHandler.sendMessage(msg2Internet);						
					}
					//判断吃子
					eat(new Chess(lastMove.fromX,lastMove.fromY,currentPlayer.getColor()),lastMove.toX,lastMove.toY);
					//判断输赢
					if(isWin())
					{
						Message msgWin=new Message();
						msgWin.what=ACTION_MSG_OVER;
						msgWin.obj=ACTION_MSG_VITORY;
						actionHandler.sendMessage(msgWin);
						break;
					}
					//如果没有赢，则交换玩家并接着走
					switchPlayer();
					//如果交换后当前玩家是AI，则还要让AI走棋
					if(currentPlayer.getPlayerType()==Player.PlayerType_AI)
					{
						//强制向下塑形成AIPlayer
						Movement bestMovement=((AIPlayer)currentPlayer).getBestMovement(chessBoard);
						Message msgMovement=new Message();
						msgMovement.what=ACTION_MSG_MOVEMENT;
						msgMovement.obj=bestMovement;
						actionHandler.sendMessage(msgMovement);
					}
					break;
				case ACTION_MSG_START:
					rightButton.setText("暂停");//改变状态时，要改变界面元素的值
					gameStart();//开始游戏
					break;
				case ACTION_MSG_PAUSE:
					rightButton.setText("恢复");
					gamePause();
					break;
				case ACTION_MSG_RESUME:
					rightButton.setText("暂停");
					gameResume();
					break;
				case ACTION_MSG_OVER:
					rightButton.setText("开始");
					gameOver();
					break;
				case ACTION_MSG_RESTART:
					gameRestart();
					break;
					default:break;
				}
				//本地命令执行完后，要判断该命令是本地命令还是远程命令，如果是本地的，则要发出去给对方
				
			}
			
		};

		heartBeatThread=new Thread(new Runnable()
		{
			private int counter=0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(heartBeatFlag)
				{
					try
					{
						//每隔0.1秒计一次时
						Thread.sleep(100);
						if(boardGridLength==0)
						{
							initialViewResource();
						}
						//这里做防闪按的控制
						if(isTouchable<TOUCH_GAP)isTouchable++;
						counter++;
						Message msg=new Message();
		                msg.obj=counter;
		                msg.what=counter;
		                //用counter来在1秒内计数，达到10表示1S
		                heartBeatHandler.sendMessage(msg);
		                if(counter==10)counter=0;
					}catch (InterruptedException e) {
		            e.printStackTrace();
					}
				}
			}			
		});
	}
	//初始化界面资源
	private void initialViewResource()
	{
		//TODO:
		//1.测量界面大小(viewSizeLength,viewSizeHeigth,左上角坐标为0,0)
		//2.计数各按键的位置和大小，并初始化(左上角的返回键，右上角的暂停键）
		//3.计数棋盘的位置和大小并初始化
		//4.计数各显示字体的位置并初始化(上下的时间，以及用户名)
		int heigth=this.getHeight();
		int width=this.getWidth();
		leftButton=new ViewItem(LEFT_BUTTON_STARTX,LEFT_BUTTON_STARTY,LEFT_BUTTON_LENGTH,LEFT_BUTTON_HEIGTH);
		leftButton.setText("退出");
		rightButton=new ViewItem(width-RIGHT_BUTTON_LENGTH-RIGHT_BUTTON_STARTX,RIGHT_BUTTON_STARTY,RIGHT_BUTTON_LENGTH,RIGHT_BUTTON_HEIGTH);
		rightButton.setText("开始");
		//设置棋盘位置
		boardGridLength=width/6;
		boardX=boardGridLength+1;
		boardY=heigth/2-boardGridLength*2;
		
		upTotalTime=new ViewItem(boardX,boardY-boardGridLength/4,boardGridLength*2,boardGridLength/2);
		downTotalTime=new ViewItem(boardX,boardY+boardGridLength*5+boardGridLength/2,boardGridLength*2,boardGridLength/2);
		upCurrentTime=new ViewItem(boardX+boardGridLength*2,boardY-boardGridLength/4,boardGridLength*2,boardGridLength/2);
		downCurrentTime=new ViewItem(boardX+boardGridLength*2,boardY+boardGridLength*5+boardGridLength/2,boardGridLength*2,boardGridLength/2);
		
		upPlayerName=new ViewItem(boardX,boardY-boardGridLength*2,boardGridLength*2,boardGridLength/2);
		downPlayerName=new ViewItem(boardX,boardY+boardGridLength*5+2,boardGridLength*2,boardGridLength/2);
	}
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//根据当成的游戏状态绘制不同的画面：准备态，游戏态，暂停态，结束态
		super.onDraw(canvas);
		leftButton.draw(canvas);
		rightButton.draw(canvas);
		switch(gameStatus)
		{
		case GAME_STATUS_PREPARE://准备态下，只绘制棋盘、棋子，并根据玩家信息绘制玩家
		case GAME_STATUS_PLAYING://游戏态下，绘制棋盘、棋子和玩家信息
		case GAME_STATUS_PAUSE: //暂停
			chessBoard.draw(canvas, boardX, boardY, boardGridLength);
			if(playerUp!=null)
			{
				for(int i=0;i<playerUp.getChessArray().size();i++)
					playerUp.getChessArray().get(i)
					.draw(canvas, boardX, boardY, boardGridLength, playerUp.getColor());
				upPlayerName.draw(canvas);
			}
			if(playerDown!=null)
			{
				for(int i=0;i<playerDown.getChessArray().size();i++)
					playerDown.getChessArray().get(i)
					.draw(canvas, boardX, boardY, boardGridLength, playerDown.getColor());
				downPlayerName.draw(canvas);
			}
			if(lastChosedChess!=null)lastChosedChess.draw(canvas, boardX, boardY, boardGridLength, ChessColor_Yellow);
			if(gameStatus==GAME_STATUS_PLAYING)
			{
				upTotalTime.draw(canvas);
				downTotalTime.draw(canvas);
				upCurrentTime.draw(canvas);
				downCurrentTime.draw(canvas);
			}
			break;
		case GAME_STATUS_OVER://结束状态下不显示棋盘，显示游戏提示信息
			Paint paint=new Paint();
			paint.setColor(Color.MAGENTA);
			paint.setTextSize(boardGridLength/2);
			canvas.drawText("游戏结，请重新开始", boardX, boardY+boardGridLength*2, paint);
			break;
		default:
			break;
		}
	}
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//判断用户具体操作点，通过相应的处理函数构造相应的消息发送给actionHandler
//		return super.onTouchEvent(event);
		//TODO:
		//1.判断控制操作
		//2.判断走棋
//		isTouched=!isTouched;
		if(isTouchable<TOUCH_GAP)return true;//在心跳中增加判断，必须加到5之后才有效，也就是说一次按下之后500ms才能再按
		isTouchable=0;
		Message localMsg=new Message();
		Message remoteMsg=new Message();
		localMsg.what=ACTION_MSG_EMPTY;
		remoteMsg.what=ACTION_MSG_EMPTY;
		int touchX=(int)event.getX();
		int touchY=(int)event.getY();
		if(leftButton.isInside(touchX,touchY ))
		{
			localMsg.what=ACTION_MSG_EXIT;
			remoteMsg.what=ACTION_MSG_EXIT;
		}
		else if(rightButton.isInside(touchX,touchY ))
		{	//如果按上的是右键，则要根据当前的游戏状态判断该键的功能
			switch(gameStatus)
			{
			case GAME_STATUS_PREPARE://准备状态下按键，表示要开始游戏
				localMsg.what=ACTION_MSG_START;
				remoteMsg.what=ACTION_MSG_START;
				break;
			case GAME_STATUS_PLAYING://游戏状态下按键，表示要暂停
				localMsg.what=ACTION_MSG_PAUSE;
				remoteMsg.what=ACTION_MSG_PAUSE;
				break;
			case GAME_STATUS_PAUSE://暂停态下按键，表示要恢复
				localMsg.what=ACTION_MSG_RESUME;
				remoteMsg.what=ACTION_MSG_RESUME;
				break;
			case GAME_STATUS_OVER://结束态下按键，表示要重新开始
				localMsg.what=ACTION_MSG_RESTART;
				remoteMsg.what=ACTION_MSG_RESTART;
				break;
			default:break;
			}
		}
		else//接下来要做走棋的逻辑判断
		{	
			//对游戏状态进行判断，非游戏状态时，触摸无效
			if(gameStatus!=GAME_STATUS_PLAYING)return true;
			//当前非本地人员玩家时，触摸无效			
			if(currentPlayer.getPlayerType()!=Player.PlayerType_Human)return true;
			//这里只是拿到一个选中点的坐标，尚未判断是否选中棋子
			Chess chosedChess=getTouchItem((int)event.getX(), (int)event.getY());
			if(chosedChess==null)return true;
			//判断是否选中已方棋子
			for(int i=0;i<currentPlayer.getChessArray().size();i++)
			{
				if((chosedChess.x==currentPlayer.getChessArray().get(i).x)&&chosedChess.y==currentPlayer.getChessArray().get(i).y)
				{
					lastChosedChess=currentPlayer.getChessArray().get(i);
					return true;
				}
			}
			//如果没有选中已方棋子，则要判断是否为移动操作
			if(lastChosedChess!=null&&isMovable(lastChosedChess,chosedChess.x,chosedChess.y))
			{
				Movement movement=new Movement(lastChosedChess.x,lastChosedChess.y,chosedChess.x,chosedChess.y);
				lastChosedChess=null;
				localMsg.what=ACTION_MSG_MOVEMENT;
				localMsg.obj=movement;
			}			
		}
		if(localMsg.what!=ACTION_MSG_EMPTY)actionHandler.sendMessage(localMsg);
		if(remoteMsg.what!=ACTION_MSG_EMPTY)messageHandler.sendMessage(remoteMsg);
//		return super.onTouchEvent(event);
		return true;
	}
	/**移动棋子
	 * @param movement
	 */
	private boolean moveChess(Movement movement){
		if(chessBoard.move(movement)==false)return false;
		Chess movedChess=new Chess(movement.fromX,movement.fromY,currentPlayer.getColor());
		currentPlayer.move(chessBoard, movedChess, movement.toX, movement.toY);
		return true;
	}
	//将chess移动到目标x,y后，要判断是否吃子，并将被吃的子做为结果返回
	//函数前提是前面已经做过move的判断，并且为成功，所以这里不对move进行判断
	private void eat(Chess chess, int x, int y)
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
					chessBoard.chessBoard[chess.x][chess.y]=GridEmpty;
					chessBoard.chessBoard[eat.x][eat.y]=chess.color;
					eat.setEatFlag();
					myArray.add(eat);
				}
			}
		}
	}
	//吃法：顶
	//在一条模，纵，斜线上，移动后该子与相邻同色子顶着另一色子，并且该线上没有其他的棋子
	private int eatDing(ArrayList<Chess> eatResult,Chess chess, int x, int y)
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
			if(chessBoard.chessBoard[i][y]==GridEmpty)continue;
			else if(chessBoard.chessBoard[i][y]==chess.color)
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
			if(chessBoard.chessBoard[x][i]==GridEmpty)continue;
			else if(chessBoard.chessBoard[x][i]==chess.color)
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
			if(chessBoard.chessBoard[ix][i]==GridEmpty)continue;
			else if(chessBoard.chessBoard[ix][i]==chess.color)
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
			if(chessBoard.chessBoard[i][iy]==GridEmpty)continue;
			else if(chessBoard.chessBoard[i][iy]==chess.color)
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
	private int eatJia(ArrayList<Chess> eatResult,Chess chess, int x, int y)
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
			if(chessBoard.chessBoard[i][y]==GridEmpty)continue;
			else if(chessBoard.chessBoard[i][y]==chess.color)
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
			if(chessBoard.chessBoard[x][i]==GridEmpty)continue;
			else if(chessBoard.chessBoard[x][i]==chess.color)
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
			if(chessBoard.chessBoard[ix][i]==GridEmpty)continue;
			else if(chessBoard.chessBoard[ix][i]==chess.color)
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
			if(chessBoard.chessBoard[i][iy]==GridEmpty)continue;
			else if(chessBoard.chessBoard[i][iy]==chess.color)
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
	private int eatTiao(ArrayList<Chess> eatResult,Chess chess, int x, int y)
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
			if(chessBoard.chessBoard[i][y]==GridEmpty)continue;
			else if(chessBoard.chessBoard[i][y]==chess.color)
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
			if(chessBoard.chessBoard[x][i]==GridEmpty)continue;
			else if(chessBoard.chessBoard[x][i]==chess.color)
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
			if(chessBoard.chessBoard[ix][i]==GridEmpty)continue;
			else if(chessBoard.chessBoard[ix][i]==chess.color)
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
			if(chessBoard.chessBoard[i][iy]==GridEmpty)continue;
			else if(chessBoard.chessBoard[i][iy]==chess.color)
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
	//判断是否有一方胜利，游戏结束,返回true表示赢棋
	//首先对方棋子的数量，如果对方棋子为0，则直接判断获胜
	//其次判断对方是否有棋子可以移动，如果无移动，则获胜
	private boolean isWin()
	{
		int opponentCount=opponentPlayer.getChessArray().size();		
		if(opponentCount==0)return true;
		//遍历对手所有的棋子，如果有棋子可以移动，则返回false，表示还没获胜
		for(int i=0;i<opponentCount;i++)
		{
			Chess loser=opponentPlayer.getChessArray().get(i);
			//先判断上下左右是否都被占用,如果目标位置被占用，isOccupied函数返回false
			if(chessBoard.isOccupied(loser.x-1, loser.y)==true)return false;
			if(chessBoard.isOccupied(loser.x, loser.y-1)==true)return false;
			if(chessBoard.isOccupied(loser.x+1, loser.y)==true)return false;
			if(chessBoard.isOccupied(loser.x, loser.y+1)==true)return false;
			if((loser.x+loser.y)%2==0)
			{
				if(chessBoard.isOccupied(loser.x-1, loser.y-1)==true)return false;
				if(chessBoard.isOccupied(loser.x+1, loser.y-1)==true)return false;
				if(chessBoard.isOccupied(loser.x-1, loser.y+1)==true)return false;
				if(chessBoard.isOccupied(loser.x+1, loser.y+1)==true)return false;
			}
		}
		return true;
	}
	//走完一步后，交换玩家
	private void switchPlayer()
	{
		//加上totalPlayTime
		currentPlayer.setTotalPlayTime(currentRoundTime+currentPlayer.getTotalPlayTime());
		if(currentPlayer.getColor()==playerUp.getColor())
		{//在交换玩家的时候，顺便把总时间给设好
			int time=currentPlayer.getTotalPlayTime();
			upTotalTime.setText(String.valueOf(time/3600)+":"
						+String.valueOf((time%3600)/60)+":"
						+String.valueOf(time%60));
			currentPlayer=playerDown;
			opponentPlayer=playerUp;
		}
		else
		{
			int time=currentPlayer.getTotalPlayTime();
			downTotalTime.setText(String.valueOf(time/3600)+":"
						+String.valueOf((time%3600)/60)+":"
						+String.valueOf(time%60));
			currentPlayer=playerUp;
			opponentPlayer=playerDown;
		}
		//重置计数器
		currentRoundTime=0;
	}
	//判断是否点击到棋子
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
	//判断将chess移动到x,y是否可行
	private boolean isMovable(Chess chess, int x, int y)
	{
		if(chessBoard.isOccupied(x,y)==false)return false;//如果目标格已被占用，直接返回失败
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
		return false;		
	}
	//提供给外部调用，传入玩家的信息，包括玩家所占用的棋子颜色
	public boolean setPlayer(Player player)
	{
		//TODO：
		//1.对传入玩家的类型进行判断，如果是非人类玩家，则优先设在上方
		//2.根据传入的玩家颜色选择棋子，如果被占用则返回失败
		//3.暂时不考虑旁观者模式
		
		//先对颜色进行过滤
		if(playerDown!=null&&playerDown.getColor()==player.getColor())return false;
		if(playerUp!=null&&playerUp.getColor()==player.getColor())return false;
		
		//然后安排位置，非人类玩家优先上面
		if(player.getPlayerType()!=Player.PlayerType_Human)
		{
			if(playerUp!=null)return false;//上方被占用，直接返回失败
			//先检查与下方玩家的颜色是否相同
			playerUp=player;
			playerUp.setChessArray(getChessArray(0,playerUp.getColor()));
		}
		else //人类玩家优先设在下方
		{	
			if(playerDown==null)
			{				
				playerDown=player;
				playerDown.setChessArray(getChessArray(4,playerDown.getColor()));
			}
			else if(playerUp==null)
			{				
				playerUp=player;
				playerUp.setChessArray(getChessArray(0,playerUp.getColor()));
			}
			else return false;			
		}
		return true;
	}
	//供外部调用，传入对手退出的信息
	public void opponentOut()
	{
		if(gameStatus!=GAME_STATUS_OVER)
		{//如果游戏没有结束，对方退出，则判已方胜
			Message msgWin=new Message();
			msgWin.what=ACTION_MSG_OVER;
			msgWin.obj=ACTION_MSG_VITORY;
			actionHandler.sendMessage(msgWin);
		}
		playerUp=null;
		opponentPlayer=null;
	}
}
