/**
 * 
 */
package com.chessserver.www.model;

import net.sf.json.JSONObject;


/**
 * @author SmartGang
 *
 */
public abstract class MessageBody {

	//目前有三种类型的消息
	public final static int MESSAGE_TYPE_SIGNALING=1;//控制信令
	public final static int MESSAGE_TYPE_DATA=2;//游戏数据，包括用户聊天
	public final static int MESSAGE_TYPE_INFORMATION=3;//各类信息
	public final static int MESSAGE_TYPE_CHATTING=4;//聊天信息
	public final static String typeKey="messageType";
	int messageType;
	//编码函数，将消息编码成JSON并返回
	abstract JSONObject codeToJSONObject();
	//解码函数，将传入的JSONObject解码成消息体
	abstract MessageBody decodeFromJSON(JSONObject json);
	abstract void setMessageType();
	public int getMessageType()
	{
		return messageType;
	}
	public String toString()
	{
		
		return codeToJSONObject().toString();
	}
}
