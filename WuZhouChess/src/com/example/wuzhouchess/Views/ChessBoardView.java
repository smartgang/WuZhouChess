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
	
	//�������ӵ���ɫ
	final public static int GridEmpty=0;
	final public static int ChessColor_Black=1;	
	final public static int ChessColor_White=2;
	final public static int ChessColor_Yellow=3;
	
	//������Ϸ��״̬��ʹ��һ���������п���
	final public static int GAME_STATUS_PREPARE=0;
	final public static int GAME_STATUS_PLAYING=1;
	final public static int GAME_STATUS_PAUSE=2;
	final public static int GAME_STATUS_OVER=3;
	private int gameStatus=0;
	
	//������Ϣ,��msg.what����
	final public static int ACTION_MSG_EMPTY=0;
	final public static int ACTION_MSG_MOVEMENT=1;//msg.obj����movement��Ϣ
	final public static int ACTION_MSG_START=2;
	final public static int ACTION_MSG_PAUSE=3;
	final public static int ACTION_MSG_RESUME=4;
	final public static int ACTION_MSG_OVER=5;//msg.obj����ʤ������ʧ��
	final public static int ACTION_MSG_RESTART=6;
	final public static int ACTION_MSG_VITORY=7;
	final public static int ACTION_MSG_FAIL=8;
	final public static int ACTION_MSG_EXIT=9;
	
	private Handler messageHandler;//����������ͨ��,�ڹ��캯���д���
	private static Handler heartBeatHandler;
	private Thread heartBeatThread;
	public static Handler actionHandler;
	//�������
	private Player playerUp;
	private Player playerDown;
	private Player currentPlayer;
	private Player opponentPlayer;
	//��ǰ�غ���������ѵ�ʱ��
	private int currentRoundTime;
	//������ɫ���������У���ʼ��Ϊ5������Ϊ0�����䣬�ﵽ10����Ӯ
	private ArrayList<Chess> blackChessArray;
	private ArrayList<Chess> whiteChessArray;
	private ChessBoard chessBoard;
	private int isTouchable=0;
	final private static int TOUCH_GAP=5;
	private Chess lastChosedChess;//������Ҳ��������У���һ��ѡ�е�����
	
	private boolean heartBeatFlag=false;
	//����һϵ�н���Ԫ��
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
	//��Ϸ��ʼ��������������Ҫ����Դ����������
	private void gameInitialize()
	{
		chessBoardInitialize();
		initialControlResource();
		initialViewResource();
		gameStatus=GAME_STATUS_PREPARE;
		heartBeatFlag=true;
//		heartBeatThread.start();
	}
	//��ʼʼ�����̼��������ӣ��������ϣ���������
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
	//��ʼ�����ӣ����ڸ������
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
	//��Ϸ��ʼ�������³�ʼ��heartBeat�߳�
	private void gameStart()
	{
		// TODO:
		//1.�����ҵ�λ���
		//2.������Ϸ 
		//3.����Ƿ���AI����
		if(playerUp==null||playerDown==null)return;
		//��������
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
		
		gameStatus=GAME_STATUS_PLAYING;//��ʼ��Ϸ
		
		//Ҫ��AI��ҽ����жϣ����AI���ߣ���������Ҫ���ж�
		if(currentPlayer.getPlayerType()==Player.PlayerType_AI)
		{
			//ǿ���������γ�AIPlayer
			Movement bestMovement=((AIPlayer)currentPlayer).getBestMovement(chessBoard);
			Message msgMovement=new Message();
			msgMovement.what=ACTION_MSG_MOVEMENT;
			msgMovement.obj=bestMovement;
			actionHandler.sendMessage(msgMovement);
		}				
	}
	//��ͣ��Ϸ������Ϸ״̬����Ϊ��ͣ
	private void gamePause()
	{
		gameStatus=GAME_STATUS_PAUSE;
	}
	//�ָ���Ϸ������Ϸ״̬����Ϊ��Ϸ��
	private void gameResume()
	{
		gameStatus=GAME_STATUS_PLAYING;
	}
	//���¿�ʼ
	private void gameRestart()
	{
		//TODO
		//1.���³�ʼ������
		//2.�¾�Ҫ˫������������ɫ���������λ�ò��䣩
		//3.�����¿�ʼ
		
		int color=playerDown.getColor();
		playerDown.setColor(playerUp.getColor());
		playerUp.setColor(color);

		playerDown.setTotalPlayTime(0);
		playerUp.setTotalPlayTime(0);
		
		chessBoardInitialize();
		
		gameStart();
		
	}
	//��Ϸ����
	private void gameOver()
	{
		gameStatus=GAME_STATUS_OVER;
	}
	//��ʼʼ��������Դ
	private void initialControlResource()
	{
		//TODO:
		//1.heartBeatHandler
		//2.actionHandler
		//3.heartBeat�̳߳�ʼ��
		
		//����heartBeatHandler������������Ϣ
		heartBeatHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				//1.��ʱ
				//2.���»���
				
				if(gameStatus==GAME_STATUS_PLAYING&&msg.what==10)
				{
					currentRoundTime++;
					//ÿ��һ�룬��˳���ʱ����ʾ��
					String time=String.valueOf(currentRoundTime/3600)+":"
								+String.valueOf((currentRoundTime%3600)/60)+":"
								+String.valueOf(currentRoundTime%60);
					if(currentPlayer.getColor()==playerUp.getColor())
					{
						upCurrentTime.setText(time);
					}
					else downCurrentTime.setText(time);
				}
				//���»���
				invalidate();
			}			
		};
		
		//����actionHandler�����ڽ������Խ��������Ĳ�����Ϣ����ִ����Ӧ�Ĳ���
		actionHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				// ��Ϣ���Ͱ������ƶ�����ͣ���ָ������¿�ʼ�����䣬ʤ��
				switch(msg.what){
				case ACTION_MSG_MOVEMENT://�����ƶ����Ӻ���
					 //* 1.�ȵ���player.move���ٵ���chessBoard.move
					 //* 2.���ݶ��ֵ���������һ������
					 //* 2.1 ���������internet��ң���Ҫ��movementͨ��messageHandler���ͳ�ȥ
					 //* 2.2 �ƶ����������ӡ���Ӯ���ж�
					 //* 2.2 ���������AI��ң���switchPlaer֮��Ҫ��ִ��һ��move�Ĳ�������ִ��AI������
					Movement lastMove=(Movement)msg.obj;
					//�ж�Ҫ�ƶ��������Ƿ����ڵ�ǰ��ң���ֹ����
					if(chessBoard.chessBoard[lastMove.fromX][lastMove.fromY]!=currentPlayer.getColor()) break;
					if(moveChess(lastMove)==false)break;
					if(opponentPlayer.getPlayerType()==Player.PlayerType_Internet)
					{	//���������������ң�ֱ�ӽ���Ϣת����ȥ
						//ע�⣺�������ߵĽǶ��ǻ����ģ�����movement����ϢҲҪ����
						Message msg2Internet=new Message();
						msg2Internet.what=msg.what;
						Movement remoteMovemen=new Movement(4-lastMove.fromX,4-lastMove.fromY,4-lastMove.toX,4-lastMove.toY);
						msg2Internet.obj=remoteMovemen;
						messageHandler.sendMessage(msg2Internet);						
					}
					//�жϳ���
					eat(new Chess(lastMove.fromX,lastMove.fromY,currentPlayer.getColor()),lastMove.toX,lastMove.toY);
					//�ж���Ӯ
					if(isWin())
					{
						Message msgWin=new Message();
						msgWin.what=ACTION_MSG_OVER;
						msgWin.obj=ACTION_MSG_VITORY;
						actionHandler.sendMessage(msgWin);
						break;
					}
					//���û��Ӯ���򽻻���Ҳ�������
					switchPlayer();
					//���������ǰ�����AI����Ҫ��AI����
					if(currentPlayer.getPlayerType()==Player.PlayerType_AI)
					{
						//ǿ���������γ�AIPlayer
						Movement bestMovement=((AIPlayer)currentPlayer).getBestMovement(chessBoard);
						Message msgMovement=new Message();
						msgMovement.what=ACTION_MSG_MOVEMENT;
						msgMovement.obj=bestMovement;
						actionHandler.sendMessage(msgMovement);
					}
					break;
				case ACTION_MSG_START:
					rightButton.setText("��ͣ");//�ı�״̬ʱ��Ҫ�ı����Ԫ�ص�ֵ
					gameStart();//��ʼ��Ϸ
					break;
				case ACTION_MSG_PAUSE:
					rightButton.setText("�ָ�");
					gamePause();
					break;
				case ACTION_MSG_RESUME:
					rightButton.setText("��ͣ");
					gameResume();
					break;
				case ACTION_MSG_OVER:
					rightButton.setText("��ʼ");
					gameOver();
					break;
				case ACTION_MSG_RESTART:
					gameRestart();
					break;
					default:break;
				}
				//��������ִ�����Ҫ�жϸ������Ǳ��������Զ���������Ǳ��صģ���Ҫ����ȥ���Է�
				
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
						//ÿ��0.1���һ��ʱ
						Thread.sleep(100);
						if(boardGridLength==0)
						{
							initialViewResource();
						}
						//�������������Ŀ���
						if(isTouchable<TOUCH_GAP)isTouchable++;
						counter++;
						Message msg=new Message();
		                msg.obj=counter;
		                msg.what=counter;
		                //��counter����1���ڼ������ﵽ10��ʾ1S
		                heartBeatHandler.sendMessage(msg);
		                if(counter==10)counter=0;
					}catch (InterruptedException e) {
		            e.printStackTrace();
					}
				}
			}			
		});
	}
	//��ʼ��������Դ
	private void initialViewResource()
	{
		//TODO:
		//1.���������С(viewSizeLength,viewSizeHeigth,���Ͻ�����Ϊ0,0)
		//2.������������λ�úʹ�С������ʼ��(���Ͻǵķ��ؼ������Ͻǵ���ͣ����
		//3.�������̵�λ�úʹ�С����ʼ��
		//4.��������ʾ�����λ�ò���ʼ��(���µ�ʱ�䣬�Լ��û���)
		int heigth=this.getHeight();
		int width=this.getWidth();
		leftButton=new ViewItem(LEFT_BUTTON_STARTX,LEFT_BUTTON_STARTY,LEFT_BUTTON_LENGTH,LEFT_BUTTON_HEIGTH);
		leftButton.setText("�˳�");
		rightButton=new ViewItem(width-RIGHT_BUTTON_LENGTH-RIGHT_BUTTON_STARTX,RIGHT_BUTTON_STARTY,RIGHT_BUTTON_LENGTH,RIGHT_BUTTON_HEIGTH);
		rightButton.setText("��ʼ");
		//��������λ��
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
		//���ݵ��ɵ���Ϸ״̬���Ʋ�ͬ�Ļ��棺׼��̬����Ϸ̬����̬ͣ������̬
		super.onDraw(canvas);
		leftButton.draw(canvas);
		rightButton.draw(canvas);
		switch(gameStatus)
		{
		case GAME_STATUS_PREPARE://׼��̬�£�ֻ�������̡����ӣ������������Ϣ�������
		case GAME_STATUS_PLAYING://��Ϸ̬�£��������̡����Ӻ������Ϣ
		case GAME_STATUS_PAUSE: //��ͣ
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
		case GAME_STATUS_OVER://����״̬�²���ʾ���̣���ʾ��Ϸ��ʾ��Ϣ
			Paint paint=new Paint();
			paint.setColor(Color.MAGENTA);
			paint.setTextSize(boardGridLength/2);
			canvas.drawText("��Ϸ�ᣬ�����¿�ʼ", boardX, boardY+boardGridLength*2, paint);
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
		//�ж��û���������㣬ͨ����Ӧ�Ĵ�����������Ӧ����Ϣ���͸�actionHandler
//		return super.onTouchEvent(event);
		//TODO:
		//1.�жϿ��Ʋ���
		//2.�ж�����
//		isTouched=!isTouched;
		if(isTouchable<TOUCH_GAP)return true;//�������������жϣ�����ӵ�5֮�����Ч��Ҳ����˵һ�ΰ���֮��500ms�����ٰ�
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
		{	//������ϵ����Ҽ�����Ҫ���ݵ�ǰ����Ϸ״̬�жϸü��Ĺ���
			switch(gameStatus)
			{
			case GAME_STATUS_PREPARE://׼��״̬�°�������ʾҪ��ʼ��Ϸ
				localMsg.what=ACTION_MSG_START;
				remoteMsg.what=ACTION_MSG_START;
				break;
			case GAME_STATUS_PLAYING://��Ϸ״̬�°�������ʾҪ��ͣ
				localMsg.what=ACTION_MSG_PAUSE;
				remoteMsg.what=ACTION_MSG_PAUSE;
				break;
			case GAME_STATUS_PAUSE://��̬ͣ�°�������ʾҪ�ָ�
				localMsg.what=ACTION_MSG_RESUME;
				remoteMsg.what=ACTION_MSG_RESUME;
				break;
			case GAME_STATUS_OVER://����̬�°�������ʾҪ���¿�ʼ
				localMsg.what=ACTION_MSG_RESTART;
				remoteMsg.what=ACTION_MSG_RESTART;
				break;
			default:break;
			}
		}
		else//������Ҫ��������߼��ж�
		{	
			//����Ϸ״̬�����жϣ�����Ϸ״̬ʱ��������Ч
			if(gameStatus!=GAME_STATUS_PLAYING)return true;
			//��ǰ�Ǳ�����Ա���ʱ��������Ч			
			if(currentPlayer.getPlayerType()!=Player.PlayerType_Human)return true;
			//����ֻ���õ�һ��ѡ�е�����꣬��δ�ж��Ƿ�ѡ������
			Chess chosedChess=getTouchItem((int)event.getX(), (int)event.getY());
			if(chosedChess==null)return true;
			//�ж��Ƿ�ѡ���ѷ�����
			for(int i=0;i<currentPlayer.getChessArray().size();i++)
			{
				if((chosedChess.x==currentPlayer.getChessArray().get(i).x)&&chosedChess.y==currentPlayer.getChessArray().get(i).y)
				{
					lastChosedChess=currentPlayer.getChessArray().get(i);
					return true;
				}
			}
			//���û��ѡ���ѷ����ӣ���Ҫ�ж��Ƿ�Ϊ�ƶ�����
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
	/**�ƶ�����
	 * @param movement
	 */
	private boolean moveChess(Movement movement){
		if(chessBoard.move(movement)==false)return false;
		Chess movedChess=new Chess(movement.fromX,movement.fromY,currentPlayer.getColor());
		currentPlayer.move(chessBoard, movedChess, movement.toX, movement.toY);
		return true;
	}
	//��chess�ƶ���Ŀ��x,y��Ҫ�ж��Ƿ���ӣ��������Ե�����Ϊ�������
	//����ǰ����ǰ���Ѿ�����move���жϣ�����Ϊ�ɹ����������ﲻ��move�����ж�
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
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ������������ͬɫ�Ӷ�����һɫ�ӣ����Ҹ�����û������������
	private int eatDing(ArrayList<Chess> eatResult,Chess chess, int x, int y)
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
		//���߳�
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
		//��б�߳�
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
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ������ͬɫ�����Ӽ�����һɫ�ӣ����Ҹ�����û������������
	private int eatJia(ArrayList<Chess> eatResult,Chess chess, int x, int y)
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
		//���߼�
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
		//��б�߼�
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
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ�������������߸���һ����ɫ�ӣ��Ҵ�����ֻ���������ӣ�������ɫ�ӱ���
	private int eatTiao(ArrayList<Chess> eatResult,Chess chess, int x, int y)
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
		//������
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
		//��б����
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
	//�ж��Ƿ���һ��ʤ������Ϸ����,����true��ʾӮ��
	//���ȶԷ����ӵ�����������Է�����Ϊ0����ֱ���жϻ�ʤ
	//����ж϶Է��Ƿ������ӿ����ƶ���������ƶ������ʤ
	private boolean isWin()
	{
		int opponentCount=opponentPlayer.getChessArray().size();		
		if(opponentCount==0)return true;
		//�����������е����ӣ���������ӿ����ƶ����򷵻�false����ʾ��û��ʤ
		for(int i=0;i<opponentCount;i++)
		{
			Chess loser=opponentPlayer.getChessArray().get(i);
			//���ж����������Ƿ񶼱�ռ��,���Ŀ��λ�ñ�ռ�ã�isOccupied��������false
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
	//����һ���󣬽������
	private void switchPlayer()
	{
		//����totalPlayTime
		currentPlayer.setTotalPlayTime(currentRoundTime+currentPlayer.getTotalPlayTime());
		if(currentPlayer.getColor()==playerUp.getColor())
		{//�ڽ�����ҵ�ʱ��˳�����ʱ������
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
		//���ü�����
		currentRoundTime=0;
	}
	//�ж��Ƿ���������
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
	//�жϽ�chess�ƶ���x,y�Ƿ����
	private boolean isMovable(Chess chess, int x, int y)
	{
		if(chessBoard.isOccupied(x,y)==false)return false;//���Ŀ����ѱ�ռ�ã�ֱ�ӷ���ʧ��
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
		return false;		
	}
	//�ṩ���ⲿ���ã�������ҵ���Ϣ�����������ռ�õ�������ɫ
	public boolean setPlayer(Player player)
	{
		//TODO��
		//1.�Դ�����ҵ����ͽ����жϣ�����Ƿ�������ң������������Ϸ�
		//2.���ݴ���������ɫѡ�����ӣ������ռ���򷵻�ʧ��
		//3.��ʱ�������Թ���ģʽ
		
		//�ȶ���ɫ���й���
		if(playerDown!=null&&playerDown.getColor()==player.getColor())return false;
		if(playerUp!=null&&playerUp.getColor()==player.getColor())return false;
		
		//Ȼ����λ�ã������������������
		if(player.getPlayerType()!=Player.PlayerType_Human)
		{
			if(playerUp!=null)return false;//�Ϸ���ռ�ã�ֱ�ӷ���ʧ��
			//�ȼ�����·���ҵ���ɫ�Ƿ���ͬ
			playerUp=player;
			playerUp.setChessArray(getChessArray(0,playerUp.getColor()));
		}
		else //����������������·�
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
	//���ⲿ���ã���������˳�����Ϣ
	public void opponentOut()
	{
		if(gameStatus!=GAME_STATUS_OVER)
		{//�����Ϸû�н������Է��˳��������ѷ�ʤ
			Message msgWin=new Message();
			msgWin.what=ACTION_MSG_OVER;
			msgWin.obj=ACTION_MSG_VITORY;
			actionHandler.sendMessage(msgWin);
		}
		playerUp=null;
		opponentPlayer=null;
	}
}
