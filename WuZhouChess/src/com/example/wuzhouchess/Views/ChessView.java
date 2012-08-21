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


/**������
 * @author SmartGang
 * @version 1.0
 */
public class ChessView extends android.view.View {
	
	final private static String LOG_TGA="ChessView";
	
	//������Ϸ�ĸ���״̬
	final public static int GameStatus_Ready=1;
	final public static int GameStatus_Playing=2;
	final public static int GameStatus_Pause=3;
	final public static int GameStatus_Over=4;
	public int gameStatus=GameStatus_Ready;
	
	//�������ӵ�������ɫ
	final public static int ChessColor_Black=1;
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	final public static int GridEmpty=0;

	//����onTouch��actionHandler��Ϣ���ݵ�ֵ���������û���������
//	final public static int ACTION_MOVE=1;
	
	//���ڱ�ʾ��ǰ�İ���״̬���ǿ����ʾ��ǰ�Ѿ�ѡ������
	Chess choseChess=null;
	Chess movedChess=null;
	//����������ÿһ��Ŀ��,Ĭ��Ϊ20��ʵ�ʸ�����Ļ��С������
	private int boardGridLength=20;
	//�������Ͻǵ����꣬������Ļ���ĵ��λ�ã����ÿһ��Ŀ�������㣬Ĭ����600*480
	private int boardX=80;
	private int boardY=140;
	//������ɫ���������У���ʼ��Ϊ5������Ϊ0�����䣬�ﵽ10����Ӯ
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
	//��ʼ����������handler
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
				//���¼�ʱ����ˢ��������ʾ
				updateView();
			}
			
		};
		//����timeCounter�����handler
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
						//�ȴ��û�����,0.2����һ��
						while(isWaiting&&movedChess==null)Thread.sleep(200);
						Log.d(LOG_TGA,"user ating");
						//���û������ƶ��жϣ����û����������ʱ����ʵ��OnTouch���Ѿ�����isMoveable���ж�
						//AIʱû���ж�,����AI�жϵĻ�����Ҫ������ν��жϽ�������ظ�AI��
						Movement movement=currentPlayer.move(cb, movedChess, moveTargetX, moveTargetY);
						//֮���ƶ������ϵ�����
						move(movedChess,movement.toX,movement.toY);
						eat(movedChess,moveTargetX,moveTargetY);
						int win=isWin();
						if(win!=0)
						{
							//��Ϸ����
							gameOver();
							Message msg=new Message();
							msg.obj=gameStatus;
							msg.what=GameStatus_Over;
							viewHolderHandler.sendMessage(msg);
						}
						movedChess=null;
						//�����û�
						switchPlayer();
						//�����û��������ǰ�û���������ң�����Ҫ�ȴ��û�����
						if(currentPlayer.getPlayerType()==Player.PlayerType_Human)isWaiting=true;
						updateView();
					}
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			}
		});		
	}
	//��ʼ�����̣������ӻָ���ԭλ
	private void initialChessBorad()
	{
		//��ʼʼ�����̼��������ӣ��������ϣ���������
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
	//����һ�����ֵ���һ��
	private void switchPlayer()
	{
		Log.d(LOG_TGA,"switchPlayer");
		//����totalPlayTime
		currentPlayer.setTotalPlayTime(currentRoundTime+currentPlayer.getTotalPlayTime());
		if(currentPlayer.getColor()==Player.ChessColor_Black)currentPlayer=playerWhite;
		else currentPlayer=playerBlack;
		//���ü�����
		timeCounter.resetCounter();
		currentRoundTime=0;
	}
	
	/**�������
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
		//up��down���ᴥ���������
//		if(event.getAction()==MotionEvent.ACTION_UP)
//		{
		isTouched=!isTouched;
		if(isTouched==false)return true;
		Log.d(LOG_TGA,"onTouchEvent");
		if(currentPlayer.getPlayerType()==Player.PlayerType_AI)return true;
			Chess c=getTouchItem((int)event.getX(),(int)event.getY());
			//û��ѡ��λ�ã���ֱ�ӷ���
			if(c==null||c.x>4||c.y>4)return true;
			//choseChessΪnull,��ǰû��ѡ������
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
			{	//�����ǰ�Ѿ���ѡ�У��ڶ��ΰ���ʱ�ƶ��ɹ������ж�Ϊ�ƶ�
				if(true==isMovable(choseChess, c.x,c.y))
				{
					Log.d(LOG_TGA,"onTouchEvent moving");
					movedChess=new Chess(choseChess.x,choseChess.y,choseChess.color);
					choseChess=null;
					//ͨ��action�̣߳��û�������ƶ�
					moveTargetX=c.x;
					moveTargetY=c.y;
					isWaiting=false;
				}
				else if(cb.chessBoard[c.x][c.y]==choseChess.color)//�ڶ��ΰ���ʱ�ƶ�ʧ�ܣ���Ҫ�ж��Ƿ�ѡ��������������
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

	//���������·�����࣬�ֱ���ʾ����ѡ���ܹ����ѵ�ʱ��,���ϰ���
	private void drawTotalTime(Canvas canvas)
	{
		int blackTime=playerBlack.getTotalPlayTime();
		int whiteTime=playerWhite.getTotalPlayTime();
		Paint paint=new Paint();
		paint.setTextSize(boardGridLength/2);//������С��Ϊ�����һ��
		paint.setColor(Color.LTGRAY);
		String time=String.valueOf(blackTime/3600)+":"
						+String.valueOf((blackTime%3600)/60)+":"
						+String.valueOf(blackTime%60);
		//����Ҫ�Գƣ���������boardGridLength����2�Ǳ�Ҫ��
		canvas.drawText(time, boardX, boardY-boardGridLength/2, paint);
		String time2=String.valueOf(whiteTime/3600)+":"
						+String.valueOf((blackTime%3600)/60)+":"
						+String.valueOf(whiteTime%60);
		canvas.drawText(time2, boardX, boardY+boardGridLength*5, paint);
	}
	//������ǰ���û���Ϸʱ��
	private void drawCurrentRoundTime(Canvas canvas)
	{
		//λ����ʼ�㣬�����û���ɫ�Ĳ�ͬ�����ϰ���
		int startY;
		if(currentPlayer.getColor()==Player.ChessColor_Black)startY=boardY-boardGridLength/2;
		else startY=boardY+boardGridLength*5;
		Paint paint=new Paint();
		paint.setTextSize(boardGridLength/2);//������С��Ϊ�����һ��
		paint.setColor(Color.LTGRAY);
		String time=String.valueOf(currentRoundTime/3600)+":"
						+String.valueOf((currentRoundTime%3600)/60)+":"
						+String.valueOf(currentRoundTime%60);
		canvas.drawText(time, boardX+boardGridLength*2, startY, paint);
	}
	public void setChessBoard()
	{
		 //���򻮷�Ϊ6������ռ4�����Ҹ�������,����ķ��м�
		int left=getLeft();
		int top=getTop();
		int right=getRight();
		int bottom=getBottom();		
		boardGridLength=(right-left)/6;
		boardX=boardGridLength+1;
		boardY=(bottom-top)/2-boardGridLength*2;
	}
	//��ʼ
	public void gameStart()
	{
		Log.d(LOG_TGA,"gameStart");
		isWaiting=true;
		timeCounter.startCounter();
		timeCounter.start();
		actionThread.start();
		gameStatus=GameStatus_Playing;
	}
	//��Ϸ���������¿�ʼ
	public void gameRestart()
	{
		isWaiting=true;
		timeCounter.startCounter();	
		gameStatus=GameStatus_Playing;
		initialChessBorad();
	}
	//��ͣ
	public void gamePause()
	{
		timeCounter.pauseCounter();
		gameStatus=GameStatus_Pause;
	}
	//��ͣ��ָ�
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
	//�жϽ�chess�ƶ���x,y�Ƿ����
	public boolean isMovable(Chess chess, int x, int y)
	{
		if(cb.isOccupied(x,y)==false)return false;//���Ŀ����ѱ�ռ�ã�ֱ�ӷ���ʧ��
		//�ж��Ƿ��������������ƶ�
		if(((1==Math.abs(x-chess.x))&&(0==Math.abs(y-chess.y)))||//�����ƶ�
				((0==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y))))//�����ƶ�
		{
			return true;
		}
		else if((1==Math.abs(x-chess.x))&&(1==Math.abs(y-chess.y)))//б���ƶ�
		{
			if((chess.x+chess.y)%2==0)//�������Ͽ��������Ҹ�Ϊż����ռ��б���ƶ�
			{
				return true;
			}
		}
		Log.d(LOG_TGA,"moving is true");
		return false;		
	}
	//�����Ӵ�����λ���ƶ���ָ�����꣬�����ƶ����
	public void move(Chess chess, int x,int y)
	{
		cb.chessBoard[chess.x][chess.y]=GridEmpty;
		cb.chessBoard[x][y]=chess.color;
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
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
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
	
}
