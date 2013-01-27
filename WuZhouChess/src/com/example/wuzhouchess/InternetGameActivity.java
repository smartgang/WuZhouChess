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
 *�̳���PlayActivity��������Internetͨ�Ź���
 */
public class InternetGameActivity extends Activity{

	//���ڴ�messageCenter������Ϣ�����ڴ�������н���Ϣ���ݵ�����ʵ����
	Handler receivingHandler;
	//��Ϣ���ģ���server���ӽ�����Ϣ����
	MessageCenter messageCenter;
	
	//���ڿ�����Ϸ����״̬�����ݲ�ͬ����Ϣ������ͬ��״̬
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
	
	//��ǰ����״̬
	private int internetStatus;	
	final static int INTERNET_STATUS_INITIAL=0;
	final static int INTERNET_STATUS_CONNECT=1;
	final static int INTERNET_STATUS_LOGIN=2;
	final static int INTERNET_STATUS_CREATE_TABLE=3;
	final static int INTERNET_STATUS_IN_TABLE=4;
	final static int INTERNET_STATUS_READY=5;
	final static int INTERNET_STATUS_PLAYING=6;
	
	//��ϷԪ�أ�������ң�Զ����ң���Ϸ��������Ϸ��
	GamePlayer localPlayer;
	GamePlayer remotePlayer;
	GameHall gameHall;
	GameTable gameTable;
	
	Player localChessBoardPlayer;
	Player remoteChessBoardPlayer;
	
	//��Ϸʵ�� chessboardview
	ChessBoardView cbv;
	//���ڴ�cvb������Ϸ��Ϣ
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
		
		//����cbv
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
			//���ͽ�������SIGNALING_TYPE_CONNECT
			//��ʽ��ʱ����Ҫ�����֤����
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
		//��messageCenter������Ϣ
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
		//����������Ϸ��״̬������gameHall�µ�״̬����Ϸ������gameTable��״̬
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
					//��Ӹ���gameTable��gamePlayer�����״̬���»���״̬
					break;
				case GAME_STATUS_GAMEPLAYING_START:
					//�����Ϸ��ʼ����
					Message msg2=new Message();
					msg2.what=ChessBoardView.ACTION_MSG_START;
					if(cbv!=null)ChessBoardView.actionHandler.sendMessage(msg2);
					break;
				}
			}
			
		};
		
		//���ڴ�chessBoardView������Ϣ,����Ϣת����ȥ
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
				case ChessBoardView.ACTION_MSG_MOVEMENT://�����ƶ����Ӻ���
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
					//��Ϸ������ѡ���˳���Ϸ:
					//1.���˳���Ϣ���͸���������EXIT)
					//2.�������յ������EXIT_RSP��Я��table list
					//3.��Singaling�д���table list��ˢ�½��뵽GameTable���棬�˴���������
					SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_INFO_EXIT,null);
					messageCenter.sendMessag(sMsg);
					break;
					default:break;				
				}
			}
			
		};
	}
	
	//����Signaling��Ϣ
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
			//�յ�LOGIN_ACP֮�󣬻�Я��gameHall��Ϣ������֮�����gameHallview
		case SignalingMessage.SIGNALING_TYPE_INFO_RSP:
			//INFO_RSP��ϢЯ��GameHall���ݲ��ø�LOGIN_ACPһ���Ĵ���ʽ			
			internetStatus=INTERNET_STATUS_CONNECT;
			gameHall=sMsg.getInformation().getGameHall();
			Message msg2=new Message();
			msg2.obj=0;
			msg2.what=GAME_STATUS_GAMEHALL;
			gameStatusHandler.sendMessage(msg2);
			break;
		case SignalingMessage.SIGNALING_TYPE_IN_TABLE:
			//TODO:���˽��뵽���ؽ�����gameTable,������player��Ϣ
			//1.ȡ��player��Ϣ,���浽remotePlayer��
			//3.����remoteChessBoardPlayer,���õ�cbv��
			remotePlayer=sMsg.getInformation().getPlayer();
			remoteChessBoardPlayer=new InternetPlayer(remotePlayer.name, remotePlayer.color, Player.PlayerType_Internet);
    		cbv.setPlayer(remoteChessBoardPlayer);
    		
			Message msg3=new Message();
			msg3.obj=0;
			msg3.what=GAME_STATUS_GAMETABLE;
			gameStatusHandler.sendMessage(msg3);
			break;
		case SignalingMessage.SIGNALING_TYPE_INFO_EXIT:
			//TODO:�յ�EXIT��Ϣ����ʾ����Ϸ�����˳�
			//1.����chessBoardView�е�oppoentOut����֪ͨ��Ϸ����
			cbv.opponentOut();
			gameTable.playerOut(remotePlayer);
			remotePlayer=null;
			break;
		case SignalingMessage.SIGNALING_TYPE_IFNO_EXIT_RSP:
			//TODO:�յ�EXIT_RSP��Ϣ����ʾ�������������˳�����,��Ϣ����GameHall��Ϣ
			//1.ˢ��GameHall��Ϣ
			//2.����GameHall����
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
	//����Data��Ϣ
	private void processData(MessageBody msg)
	{
		DataMessage dMsg=(DataMessage)msg;
		switch(dMsg.dataType)
		{
		case DataMessage.DATA_TYPE_MOVEMENT:
			//ͨ�����ú�������ʽ�����յ����ƶ���Ϣ���ݸ�player
			Message msg2=new Message();
			msg2.what=ChessBoardView.ACTION_MSG_MOVEMENT;
			msg2.obj=dMsg.getMovement();
			if(cbv!=null)ChessBoardView.actionHandler.sendMessage(msg2);
			break;
		case DataMessage.DATA_TYPE_CONTROL_READY:
			//���յ��Է�������׼����Ϣ
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
			//���߶�׼������֮�󣬷��������·���ʼָ��
			Message msg1=new Message();
			msg1.obj=0;
			msg1.what=GAME_STATUS_GAMEPLAYING_START;
			gameStatusHandler.sendMessage(msg1);			
			break;
		default:break;
		}
	}
	//����Information��Ϣ
	private void processInformation(MessageBody msg)
	{
		
	}
	//����Chatting��Ϣ
	private void processChatting(MessageBody msg)
	{
		
	}
	
	private void gotoGameHallView()
	{
		setContentView(R.layout.game_hall_view);
		ListView tableList=(ListView)findViewById(R.id.lvTableList);
		Button btnNewTable=(Button)findViewById(R.id.btnNewTable);
		//��ʼ��topView
		View topHeader;
		topHeader=(View)findViewById(R.id.main_header);
		Button btnLeft=(Button)topHeader.findViewById(R.id.top_btn_left);
		btnLeft.setText("����");
//		btnLeft.setVisibility(View.INVISIBLE);
		Button btnRight=(Button)topHeader.findViewById(R.id.top_btn_right);
		btnRight.setText("ˢ��");
//		btnRight.setVisibility(View.INVISIBLE);
		TextView top_textView=(TextView)topHeader.findViewById(R.id.tv_toptitle);
		top_textView.setText("��Ϸ ����");
		
		TableListAdapter tableListAdapter=new TableListAdapter(this);
		tableList.setAdapter(tableListAdapter);
		//�˳�
		btnLeft.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		//ˢ�£�����INFO_REQ��Ϣ���������������gameHall��Ϣ
		btnRight.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SignalingMessage sMsg=new SignalingMessage(SignalingMessage.SIGNALING_TYPE_INFO_REQ, null);
				messageCenter.sendMessag(sMsg);
			}
			
		});
		//�����µ�Table,�����������Ϸtable
		btnNewTable.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//����table��Ϣ����������������tableView
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
		//listView����Ӧ����������������ѡ���table
		tableList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3)
        	{
        		//TODO:�������gameTable
        		//1.���Զ����gameTable��player2��
        		//2.��gameTable�е�player1��������ΪremotePlayer���Լ�remoteChessBoardPlayer
        		//3.�½�player���Լ��赽localChessBoardPlayer��
        		//4.��player���õ�cbv��
        		//5.���ͽ���table����Ϣ��������
        		gameTable=gameHall.tableList.get(arg2);
        		if(gameTable.playerNum==2)return ;
        		//�ǽ�����˵�table��Ҫ�ж��Ѵ��ڵ�����player1����player2
        		if(gameTable.player1==null)
        		{
        			gameTable.setPlayer1(localPlayer);
        			remotePlayer=gameTable.getPlayer2();
        		}
        		else
        		{
        			gameTable.setPlayer2(localPlayer);
        			remotePlayer=gameTable.getPlayer1();//��remotePlayer����
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
