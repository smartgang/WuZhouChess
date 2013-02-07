/**
 * 
 */
package com.example.classes.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author SmartGang
 * ��Ϣ���ڣ����ڹ����������������ͨ��
 * ��һ�����Կ��ǲ��õ���SingleTon��ģʽ��ʵ��
 */
public class MessageCenter {

	Socket sc;
	DataInputStream din;
	DataOutputStream dout;
	Handler gameCenterHandler;
	/**
	 * @param gameCenterHandler the gameCenterHandler to set
	 */
	public void setGameCenterHandler(Handler gameCenterHandler) {
		this.gameCenterHandler = gameCenterHandler;
	}

	Thread receivingThread;
	boolean connected=false;
	
	//����ʱ�Զ����ӷ�����
	public MessageCenter()
	{
		new Thread(){
            @Override
            public void run()
            {
            	try {
        			//��������ַ�Ͷ˿ں�
        			sc=new Socket("192.168.1.100",9999);
        			din=new DataInputStream(sc.getInputStream());
        			dout=new DataOutputStream(sc.getOutputStream());
//        			dout.writeUTF("<#Connect#>say hi to server");
        			//����������Ϣ
        			connected=true;
        			receviedMessage();
        			receivingThread.start();
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            }
            }.start();
		
	}
	
	//������Ϣ��������
	public void sendMessag(MessageBody msg)
	{
		String str=msg.toString();
		Log.d("MessageCenter", "sending message: "+str);
		try {
			dout.writeUTF(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//���������̣߳����Ͻ�����Ϣ�����͸�GameCenter
	public void receviedMessage()
	{
		receivingThread=new Thread(new Runnable()
		{
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(connected)
				{
					try {
						String msg=din.readUTF();
						JSONObject json=new JSONObject(msg);
						Log.d("MessageCenter", "receiving message: "+json.toString());
						MessageBody msgBody=null;
						Message msg1=new Message();
						int msgType=json.getInt("messageType");
						switch(msgType)
						{
						case MessageBody.MESSAGE_TYPE_SIGNALING:msgBody=new SignalingMessage(json);break;
						case MessageBody.MESSAGE_TYPE_DATA:msgBody=new DataMessage(json);break;
						case MessageBody.MESSAGE_TYPE_INFORMATION:msgBody=new InformationMessage(json);break;
						case MessageBody.MESSAGE_TYPE_CHATTING:msgBody=new ChattingMessage(json);break;
						default: break;
						}
						msg1.obj=msgBody;
						msg1.what=msgType;
						gameCenterHandler.sendMessage(msg1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						connected=false;
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		});
	}
	
	public void disconnect()
	{
		try {
			connected=false;
			din.close();
			dout.close();
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
