/**
 * 
 */
package com.example.wuzhouchess;

import com.example.classes.InternetPlayer;
import com.example.classes.model.DataMessage;
import com.example.classes.model.InformationMessage;
import com.example.classes.model.MessageBody;
import com.example.classes.model.MessageCenter;
import com.example.classes.model.SignalingMessage;
import com.example.internet.GameHall;
import com.example.internet.GamePlayer;
import com.example.internet.GameTable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
/**
 * @author SmartGang
 *GameCenter
 *�̳���PlayActivity��������Internetͨ�Ź���
 */
public class InternetGameActivity extends PlayActivity{

	Thread receivingThread;
	Handler receivingHandler;
	Handler playerMessageHandler;
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
	private void playGame()
	{
//		super.onCreate(savedInstanceState);

		//�������ͳ�internetPlayer
		internetPlayer=(InternetPlayer) whitePlayer;
		internetPlayer.setGameCenterHandler(playerMessageHandler);
		blackPlayer.setGameCenterHandler(playerMessageHandler);
		initHandlers();
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
		//��InternetPlayer��������Ϣ
		playerMessageHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{
				//�����movement����ֱ�ӷ��͸��Է�
				case DataMessage.DATA_TYPE_MOVEMENT:
					messageCenter.sendMessag((DataMessage)msg.obj);
					break;
				default:
					//��������ӿ��Ʋ����Ĵ�����
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
		}
	}
	//����Data��Ϣ
	private void processData(MessageBody msg)
	{
		DataMessage dMsg=(DataMessage)msg;
		if(dMsg.dataType==DataMessage.DATA_TYPE_MOVEMENT)
		{	//ͨ�����ú�������ʽ�����յ����ƶ���Ϣ���ݸ�player
			internetPlayer.getInternetMovement(dMsg.getMovement());
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
}
