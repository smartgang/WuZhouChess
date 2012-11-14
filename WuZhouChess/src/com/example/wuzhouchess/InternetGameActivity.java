/**
 * 
 */
package com.example.wuzhouchess;

import com.example.classes.HumanPlayer;
import com.example.classes.InternetPlayer;
import com.example.classes.Player;
import com.example.classes.model.DataMessage;
import com.example.classes.model.InformationMessage;
import com.example.classes.model.MessageBody;
import com.example.classes.model.MessageCenter;
import com.example.classes.model.SignalingMessage;
import com.example.internet.GameHall;
import com.example.internet.GamePlayer;
import com.example.internet.GameTable;
import com.example.wuzhouchess.Views.ChessView;

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

	Handler receivingHandler;
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
	
	MessageCenter messageCenter;
	InternetPlayer internetPlayer;
	private int internetStatus;
	final static int INTERNET_STATUS_INITIAL=0;
	final static int INTERNET_STATUS_CONNECT=1;
	final static int INTERNET_STATUS_LOGIN=2;
	final static int INTERNET_STATUS_CREATE_TABLE=3;
	final static int INTERNET_STATUS_IN_TABLE=4;
	final static int INTERNET_STATUS_READY=5;
	final static int INTERNET_STATUS_PLAYING=6;
	GamePlayer gamePlayer;
	GameHall gameHall;
	GameTable gameTable;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initHandlers();
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
				gamePlayer=new GamePlayer();
				gamePlayer.name=etName.getText().toString();
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
					break;
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
				InformationMessage playerMsg=new InformationMessage(InformationMessage.INFORMATION_TYPE_PLAYER,null,null,gamePlayer);
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
			//�յ�����table����Ϣ
			GameTable table=sMsg.getInformation().getGameTable();
			if(table.name.equals(gameTable.name)==false)return;
			gameTable=table;
			Message msg3=new Message();
			msg3.obj=0;
			msg3.what=GAME_STATUS_GAMETABLE;
			gameStatusHandler.sendMessage(msg3);
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
			internetPlayer.getInternetMovement(dMsg.getMovement());
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
				gameTable.setName(gamePlayer.name);
				gameTable.setID(gamePlayer.ID);
				gameTable.setPlayer1(gamePlayer);
				InformationMessage tableMsg=new InformationMessage(InformationMessage.INFORMATION_TYPE_TABLE,null,gameTable,gamePlayer);
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
        		gameTable=gameHall.tableList.get(arg2);
        		if(gameTable.playerNum==2)return ;
        		//�ǽ�����˵�table������Ҫ���Լ�����Ϊplayer2
        		gameTable.setPlayer2(gamePlayer);
				InformationMessage tableMsg=new InformationMessage(InformationMessage.INFORMATION_TYPE_TABLE,null,gameTable,gamePlayer);
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
		Log.d("GameTableView","inTableView");
		setContentView(R.layout.play_view);
		//��ʼ��topView
		View topHeader;
		topHeader=(View)findViewById(R.id.main_header);
		Button btnLeft=(Button)topHeader.findViewById(R.id.top_btn_left);
		btnLeft.setText("����");
//		btnLeft.setVisibility(View.INVISIBLE);
		Button btnRight=(Button)topHeader.findViewById(R.id.top_btn_right);
		btnRight.setText("��ʼ");
//		btnRight.setVisibility(View.INVISIBLE);
		TextView top_textView=(TextView)topHeader.findViewById(R.id.tv_toptitle);
		TextView tvPlayer1=(TextView)findViewById(R.id.tvPlayerBlack);
		TextView tvPlayer2=(TextView)findViewById(R.id.tvPlayerWhite);
		
		Handler viewHoldHandler;//��chessViewͨ�ŵ�handler
		
		ChessView cbv=(ChessView)findViewById(R.id.chessboard);
		
		Player selfPlayer;
		Player opponentPlayer;
		if(gameTable.player1.name.equals(gamePlayer.name))
		{
			//��һλ����Ǳ����û�
			selfPlayer=new HumanPlayer(gamePlayer.name,Player.ChessColor_Black,Player.PlayerType_Human);
			if(gameTable.player2!=null)opponentPlayer=new InternetPlayer(gameTable.player2.name,Player.ChessColor_White,Player.PlayerType_Internet);
		}
		else
		{
			//�ڶ�λ����Ǳ����û�
			selfPlayer=new HumanPlayer(gamePlayer.name,Player.ChessColor_White,Player.PlayerType_Human);
			opponentPlayer=new InternetPlayer(gameTable.player1.name,Player.ChessColor_Black,Player.PlayerType_Internet);			
		}
		
		//��chessBoard��ͨ��handler
		viewHoldHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{
				case ChessView.GAME_STATUS_GAMEPLAYING_END:;
					break;
				default:break;
				}
			}
			
		};
		
		//����������˳��ͱ���ʱ����
		btnLeft.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//�Ҽ���������ʼ��Ϸ����ͣ
		btnRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gameTable.status==GameTable.TABLE_STATUS_WAITING)
				{//�ȴ�״̬�£�����׼��״̬
					//����׼����Ϣ������������ͨ��gameStatusHandler���»���״̬
					gameTable.status=GameTable.TABLE_STATUS_READY;
					DataMessage readyMessage=new DataMessage(DataMessage.DATA_TYPE_CONTROL_READY,null);
					messageCenter.sendMessag(readyMessage);
					Message msg1=new Message();
					msg1.obj=0;
					msg1.what=GAME_STATUS_GAMEPLAYING_READY;
					gameStatusHandler.sendMessage(msg1);
				}
			}			
		});
		//������ʾ�����Ϣ,�����û�ʼ����ʾ���·��������û�ʼ����ʾ���Ϸ�
		tvPlayer1.setText(gamePlayer.name);
		if(gameTable.playerNum==2)tvPlayer2.setText(gameTable.player2.name);
		
		cbv.setViewHolderHandler(viewHoldHandler);
		
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
