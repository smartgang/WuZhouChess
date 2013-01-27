/**
 * 
 */
package com.example.wuzhouchess;

import com.example.classes.AIPlayer;
import com.example.classes.Chess;
import com.example.classes.HumanPlayer;
import com.example.classes.InternetPlayer;
import com.example.classes.Movement;
import com.example.classes.Player;
import com.example.classes.model.DataMessage;
import com.example.classes.model.InformationMessage;
import com.example.classes.model.MessageBody;
import com.example.classes.model.MessageCenter;
import com.example.classes.model.SignalingMessage;
import com.example.internet.GameHall;
import com.example.internet.GamePlayer;
import com.example.internet.GameTable;
import com.example.wuzhouchess.Views.ChessBoardView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author SmartGang
 *GameCenter
 *继承自PlayActivity，并增加Internet通信功能
 */
public class InternetGameActivity extends Activity{

	//用于从messageCenter接收消息，并在处理过程中将消息传递到各个实体中
	Handler receivingHandler;
	//消息中心，与server连接进行消息交互
	MessageCenter messageCenter;
	
	//用于控制游戏界面状态，根据不同的消息调整不同的状态
	Handler gameStatusHandler;
	final static int GAME_STATUS_LOGIN=0;
	final static int GAME_STATUS_GAMEHALL=1;
	final static int GAME_STATUS_GAMETABLE=2;
	final static int GAME_STATUS_GAMEPLAYING_READY=3;
	final static int GAME_STATUS_GAMEPLAYING_START=4;
	final static int GAME_STATUS_GAMEPLAYING_PAUSE=5;
	final static int GAME_STATUS_GAMEPLAYING_RESUME=6;
	final static int GAME_STATUS_GAMEPLAYING_END=7;
	final static int GAME_STATUS_GAMEPLAYING_PLAYING=8;
	final static int GAME_DATA_MOVMENT=9;
	final static int GAME_STATUS_GAMEPLAYING_EXIT=10;
	
	//当前联网状态
	private int internetStatus;	
	final static int INTERNET_STATUS_INITIAL=0;
	final static int INTERNET_STATUS_CONNECT=1;
	final static int INTERNET_STATUS_LOGIN=2;
	final static int INTERNET_STATUS_CREATE_TABLE=3;
	final static int INTERNET_STATUS_IN_TABLE=4;
	final static int INTERNET_STATUS_READY=5;
	final static int INTERNET_STATUS_PLAYING=6;
	
	//游戏元素：本地玩家，远程玩家，游戏大厅和游戏桌
	GamePlayer localPlayer;
	GamePlayer remotePlayer;
	GameHall gameHall;
	GameTable gameTable;
	
	Player localChessBoardPlayer;
	Player remoteChessBoardPlayer;
	
	//游戏实体 chessboardview
	ChessBoardView cbv;
	//用于从cvb接收游戏消息
	Handler cbvHandler;
	//ChattingView chattingView;
	//InformationView informationView;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initHandlers();
		
		//启动cbv
		cbv=new ChessBoardView(this,cbvHandler);
		cbv.startHeartBeat();
		
		internetStatus=INTERNET_STATUS_INITIAL;
		
		messageCenter=new MessageCenter();
		messageCenter.setGameCenterHandler(receivingHandler);
		gotoLogIn();
	}

	private void gotoLogIn()
	{
		setContentView(R.layout.internet_login);
		Button btnLogin=(Button)findViewById(R.id.btnLogin);
		Button btnCancel=(Button)findViewById(R.id.btnCancel);
		final EditText etName=(EditText)findViewById(R.id.etUserName);
		EditText etPassword=(EditText)findViewById(R.id.etPassword);
		btnLogin.setOnClickListener(new OnClickListener()
		{
			//发送接入请求SIGNALING_TYPE_CONNECT
			//正式做时，需要添加验证环节
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_CONNECT, null);
				localPlayer=new GamePlayer();
				localPlayer.name=etName.getText().toString();
				messageCenter.sendMessag(sMsg);
				
			}
			
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	
	private void initHandlers()
	{
		//从messageCenter接收消息
		receivingHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{
				case MessageBody.MESSAGE_TYPE_SIGNALING:processSignaling((MessageBody) msg.obj);break;
				case MessageBody.MESSAGE_TYPE_DATA:processData((MessageBody) msg.obj);break;
				case MessageBody.MESSAGE_TYPE_INFORMATION:processInformation((MessageBody) msg.obj);break;
				case MessageBody.MESSAGE_TYPE_CHATTING:processChatting((MessageBody) msg.obj);break;
				default: break;				
				}
			}			
		};
		//用来控制游戏的状态，包括gameHall下的状态和游戏过程中gameTable的状态
		gameStatusHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
//				super.handleMessage(msg);
				switch(msg.what)
				{
				case GAME_STATUS_LOGIN:break;
				case GAME_STATUS_GAMEHALL:
					gotoGameHallView();
					break;
				case GAME_STATUS_GAMETABLE:
					gotoGameTableView();
					break;
				case GAME_STATUS_GAMEPLAYING_READY:
					//添加根据gameTable中gamePlayer的玩家状态更新画面状态
					break;
				case GAME_STATUS_GAMEPLAYING_START:
					//添加游戏开始代码
					Message msg2=new Message();
					msg2.what=ChessBoardView.ACTION_MSG_START;
					if(cbv!=null)ChessBoardView.actionHandler.sendMessage(msg2);
					break;
				}
			}
			
		};
		
		//用于从chessBoardView接收消息,将消息转发出去
		cbvHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				DataMessage dmsg=new DataMessage(DataMessage.DATA_TYPE_CONTROL_RESUME,null);
				switch(msg.what)
				{
				case ChessBoardView.ACTION_MSG_MOVEMENT://调用移动棋子函数
					dmsg.setDataType(DataMessage.DATA_TYPE_MOVEMENT);
					dmsg.setMovement((Movement)msg.obj);
					messageCenter.sendMessag(dmsg);
					break;
				case ChessBoardView.ACTION_MSG_START:
					dmsg.setDataType(DataMessage.DATA_TYPE_CONTROL_START);
					messageCenter.sendMessag(dmsg);
					break;
				case ChessBoardView.ACTION_MSG_PAUSE:
					dmsg.setDataType(DataMessage.DATA_TYPE_CONTROL_PAUSE);
					messageCenter.sendMessag(dmsg);
					break;
				case ChessBoardView.ACTION_MSG_RESUME:
					dmsg.setDataType(DataMessage.DATA_TYPE_CONTROL_RESUME);
					messageCenter.sendMessag(dmsg);
					break;
				case ChessBoardView.ACTION_MSG_OVER:
					dmsg.setDataType(DataMessage.DATA_TYPE_CONTROL_LOSE);
					messageCenter.sendMessag(dmsg);
					break;
				case ChessBoardView.ACTION_MSG_RESTART:
					dmsg.setDataType(DataMessage.DATA_TYPE_CONTROL_START);
					messageCenter.sendMessag(dmsg);
					break;
				case ChessBoardView.ACTION_MSG_EXIT:
					//游戏界面中选择退出游戏:
					//1.将退出消息发送给服务器（EXIT)
					//2.服务器收到后近回EXIT_RSP，携带table list
					//3.在Singaling中处理table list，刷新进入到GameTable界面，此处不做处理
					SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_INFO_EXIT,null);
					messageCenter.sendMessag(sMsg);
					break;
					default:break;				
				}
			}
			
		};
	}
	
	//处理Signaling消息
	private void processSignaling(MessageBody msg)
	{
		SignalingMessage sMsg=(SignalingMessage)msg;
		switch(sMsg.getSignalingType())
		{
		case SignalingMessage.SIGNALING_TYPE_LOGIN_REQ:
			if(internetStatus==INTERNET_STATUS_INITIAL)
			{
				InformationMessage playerMsg=new InformationMessage(InformationMessage.INFORMATION_TYPE_PLAYER,null,null,localPlayer);
				sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_LOGIN_RSP, playerMsg);
				messageCenter.sendMessag(sMsg);
			}
			break;
		case SignalingMessage.SIGNALING_TYPE_LOGIN_ACP:
			//收到LOGIN_ACP之后，会携带gameHall信息，保存之后进入gameHallview
		case SignalingMessage.SIGNALING_TYPE_INFO_RSP:
			//INFO_RSP消息携带GameHall内容采用跟LOGIN_ACP一样的处理方式			
			internetStatus=INTERNET_STATUS_CONNECT;
			gameHall=sMsg.getInformation().getGameHall();
			Message msg2=new Message();
			msg2.obj=0;
			msg2.what=GAME_STATUS_GAMEHALL;
			gameStatusHandler.sendMessage(msg2);
			break;
		case SignalingMessage.SIGNALING_TYPE_IN_TABLE:
			//TODO:别人进入到本地建立的gameTable,带的是player信息
			//1.取出player信息,保存到remotePlayer中
			//3.创建remoteChessBoardPlayer,设置到cbv中
			remotePlayer=sMsg.getInformation().getPlayer();
			remoteChessBoardPlayer=new InternetPlayer(remotePlayer.name, remotePlayer.color, Player.PlayerType_Internet);
    		cbv.setPlayer(remoteChessBoardPlayer);
    		
			Message msg3=new Message();
			msg3.obj=0;
			msg3.what=GAME_STATUS_GAMETABLE;
			gameStatusHandler.sendMessage(msg3);
			break;
		case SignalingMessage.SIGNALING_TYPE_INFO_EXIT:
			//TODO:收到EXIT消息，表示是游戏对手退出
			//1.调用chessBoardView中的oppoentOut函数通知游戏界面
			cbv.opponentOut();
			gameTable.playerOut(remotePlayer);
			remotePlayer=null;
			break;
		case SignalingMessage.SIGNALING_TYPE_IFNO_EXIT_RSP:
			//TODO:收到EXIT_RSP消息，表示服务器接收了退出请求,消息附带GameHall信息
			//1.刷新GameHall信息
			//2.进入GameHall界面
			gameTable=null;
			gameHall=sMsg.getInformation().getGameHall();
			Message msg4=new Message();
			msg4.obj=0;
			msg4.what=GAME_STATUS_GAMEHALL;
			gameStatusHandler.sendMessage(msg4);
			break;
		default:break;
		}
	}
	//处理Data消息
	private void processData(MessageBody msg)
	{
		DataMessage dMsg=(DataMessage)msg;
		switch(dMsg.dataType)
		{
		case DataMessage.DATA_TYPE_MOVEMENT:
			//通过调用函数的形式将接收到的移动信息传递给player
			Message msg2=new Message();
			msg2.what=ChessBoardView.ACTION_MSG_MOVEMENT;
			msg2.obj=dMsg.getMovement();
			if(cbv!=null)ChessBoardView.actionHandler.sendMessage(msg2);
			break;
		case DataMessage.DATA_TYPE_CONTROL_READY:
			//接收到对方发来的准备消息
			if(gameTable.getPlayer2()!=null)
			{
				gameTable.getPlayer2().status=GamePlayer.STATUS_READY;
				Message msg1=new Message();
				msg1.obj=0;
				msg1.what=GAME_STATUS_GAMEPLAYING_READY;
				gameStatusHandler.sendMessage(msg1);
			}
			break;
		case DataMessage.DATA_TYPE_CONTROL_START:
			//两边都准备好了之后，服务器会下发开始指令
			Message msg1=new Message();
			msg1.obj=0;
			msg1.what=GAME_STATUS_GAMEPLAYING_START;
			gameStatusHandler.sendMessage(msg1);			
			break;
		default:break;
		}
	}
	//处理Information消息
	private void processInformation(MessageBody msg)
	{
		
	}
	//处理Chatting消息
	private void processChatting(MessageBody msg)
	{
		
	}
	
	private void gotoGameHallView()
	{
		setContentView(R.layout.game_hall_view);
		ListView tableList=(ListView)findViewById(R.id.lvTableList);
		Button btnNewTable=(Button)findViewById(R.id.btnNewTable);
		//初始化topView
		View topHeader;
		topHeader=(View)findViewById(R.id.main_header);
		Button btnLeft=(Button)topHeader.findViewById(R.id.top_btn_left);
		btnLeft.setText("返回");
//		btnLeft.setVisibility(View.INVISIBLE);
		Button btnRight=(Button)topHeader.findViewById(R.id.top_btn_right);
		btnRight.setText("刷新");
//		btnRight.setVisibility(View.INVISIBLE);
		TextView top_textView=(TextView)topHeader.findViewById(R.id.tv_toptitle);
		top_textView.setText("游戏 大厅");
		
		TableListAdapter tableListAdapter=new TableListAdapter(this);
		tableList.setAdapter(tableListAdapter);
		//退出
		btnLeft.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		//刷新，发送INFO_REQ消息给服务器请求更新gameHall信息
		btnRight.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_INFO_REQ, null);
				messageCenter.sendMessag(sMsg);
			}
			
		});
		//创建新的Table,创建后进入游戏table
		btnNewTable.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//发送table消息给服务器，并进入tableView
				gameTable=new GameTable();
				gameTable.setName(localPlayer.name);
				gameTable.setID(localPlayer.ID);
				gameTable.setPlayer1(localPlayer);
				
				if(cbv!=null)
				{
					localChessBoardPlayer=new HumanPlayer(localPlayer.name, Player.ChessColor_Black, Player.PlayerType_Human);
					localPlayer.color=Player.ChessColor_Black;
					cbv.setPlayer(localChessBoardPlayer);
				}
				
				InformationMessage tableMsg=new InformationMessage(InformationMessage.INFORMATION_TYPE_TABLE,null,gameTable,null);
				SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_CREATE_TABLE, tableMsg);
				messageCenter.sendMessag(sMsg);
				Message msg2=new Message();
				msg2.obj=0;
				msg2.what=GAME_STATUS_GAMETABLE;
				gameStatusHandler.sendMessage(msg2);
			}
			
		});
		//listView的响应函数，点击后进入所选择的table
		tableList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3)
        	{
        		//TODO:进别入的gameTable
        		//1.将自动设进gameTable的player2中
        		//2.将gameTable中的player1读出设置为remotePlayer，以及remoteChessBoardPlayer
        		//3.新建player将自己设到localChessBoardPlayer中
        		//4.将player设置到cbv中
        		//5.发送进入table的消息给服务器
        		gameTable=gameHall.tableList.get(arg2);
        		if(gameTable.playerNum==2)return ;
        		//是进入别人的table，要判断已存在的人是player1还是player2
        		if(gameTable.player1==null)
        		{
        			gameTable.setPlayer1(localPlayer);
        			remotePlayer=gameTable.getPlayer2();
        		}
        		else
        		{
        			gameTable.setPlayer2(localPlayer);
        			remotePlayer=gameTable.getPlayer1();//将remotePlayer读出
        		}        		
        		if(remotePlayer.color==Player.ChessColor_Black)localPlayer.color=Player.ChessColor_White;
        		else localPlayer.color=Player.ChessColor_Black;
        		remoteChessBoardPlayer=new InternetPlayer(remotePlayer.name, remotePlayer.color, Player.PlayerType_Internet);
        		localChessBoardPlayer=new HumanPlayer(localPlayer.name, localPlayer.color, Player.PlayerType_Human);
				cbv.setPlayer(remoteChessBoardPlayer);
				cbv.setPlayer(localChessBoardPlayer);
				
        		gameTable.playerNum=2;       		
				InformationMessage tableMsg=new InformationMessage(InformationMessage.INFORMATION_TYPE_TABLE,null,gameTable,null);
				SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_IN_TABLE, tableMsg);
				messageCenter.sendMessag(sMsg);
				Message msg2=new Message();
				msg2.obj=0;
				msg2.what=GAME_STATUS_GAMETABLE;
				gameStatusHandler.sendMessage(msg2);
        	}
        });
	}
	
	private void gotoGameTableView()
	{
		setContentView(cbv);
	}

	class TableListAdapter extends BaseAdapter
	{
		private LayoutInflater layoutInflater;
		Context context;
		
		public TableListAdapter(Context context)
		{
			this.context = context;
			layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(gameHall==null)return 0;
			return gameHall.tableList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if(gameHall==null)return null;
			return gameHall.tableList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout linearLayout 	= (LinearLayout)layoutInflater.inflate(R.layout.table_list_item, null);
			TextView tvPlayer1		 	= (TextView) linearLayout.findViewById(R.id.tvTableListPlayer1);
			TextView tvPlayer2			= (TextView)linearLayout.findViewById(R.id.tvTableListPlayer2);
			TextView tvStatus			= (TextView)linearLayout.findViewById(R.id.tvTableListStatus);

			GameTable table=(GameTable)getItem(position);
			if(table!=null)
			{
				tvPlayer1.setText(table.getPlayer1().name);
				if(table.playerNum==2)tvPlayer2.setText(table.getPlayer2().name);
				tvStatus.setText(String.valueOf(table.status));
			}				
			return linearLayout;
		}
		
	}
}
