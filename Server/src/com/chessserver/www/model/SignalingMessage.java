/**
 * 
 */
package com.chessserver.www.model;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * @author SmartGang
 *
 */
public class SignalingMessage extends MessageBody {

	private int signalingType;
	/**
	 * @return the signalingType
	 */
	public int getSignalingType() {
		return signalingType;
	}

	private final static String SingnalingTypeKey="SingnalingTypeKey";
	public final static int SIGNALING_TYPE_CONNECT=0;
	public final static int SIGNALING_TYPE_LOGIN_REQ=1;
	public final static int SIGNALING_TYPE_LOGIN_RSP=2;
	public final static int SIGNALING_TYPE_LOGIN_ACP=3;
	public final static int SIGNALING_TYPE_CREATE_TABLE=4;
	public final static int SIGNALING_TYPE_IN_TABLE=5;
	public final static int SIGNALING_TYPE_OUT_TABLE=6;
	public final static int SIGNALING_TYPE_LOGIN_OUT=7;
	InformationMessage information;
	private final static String InformationKey="InformationKey";
	/**
	 * @param signalingType
	 * @param information
	 */
	public SignalingMessage(int signalingType, InformationMessage information) {
		super();
		this.signalingType = signalingType;
		this.information = information;
		messageType=MessageBody.MESSAGE_TYPE_SIGNALING;
	}

	/**
	 * 
	 */
	public SignalingMessage(JSONObject json) {
//		super();
		decodeFromJSON(json);
	}

	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#codeToJSONObject()
	 */
	@Override
	JSONObject codeToJSONObject() {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		try {
			json.put(typeKey, messageType);
			json.put(SingnalingTypeKey, signalingType);
			switch(signalingType)
			{
				case SIGNALING_TYPE_CONNECT:break;
				case SIGNALING_TYPE_LOGIN_REQ:break;				
				case SIGNALING_TYPE_LOGIN_OUT:break;
				default:
//				case SIGNALING_TYPE_LOGIN_RSP://带playerInfo
//				case SIGNALING_TYPE_LOGIN_ACP://带gameHallInfo
//				case SIGNALING_TYPE_CREATE_TABLE://带tableInfo
//				case SIGNALING_TYPE_IN_TABLE://带tableInfo
//				case SIGNALING_TYPE_OUT_TABLE://带tableInfo
					json.put(InformationKey, information.codeToJSONObject());
					break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#decodeFromJSON(net.sf.json.JSONObject)
	 */
	@Override
	MessageBody decodeFromJSON(JSONObject json) {
		// TODO Auto-generated method stub
		messageType=MessageBody.MESSAGE_TYPE_INFORMATION;
		try {
			signalingType=json.getInt("SingnalingTypeKey");
			switch(signalingType)
			{//有三种消息是不携带information
			case SIGNALING_TYPE_CONNECT:break;
			case SIGNALING_TYPE_LOGIN_REQ:break;
			case SIGNALING_TYPE_LOGIN_OUT:break;
			default:
				information=new InformationMessage(json.getJSONObject(InformationKey));
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#setMessageType()
	 */
	@Override
	void setMessageType() {
		// TODO Auto-generated method stub
		messageType=MessageBody.MESSAGE_TYPE_SIGNALING;
	}

	/**
	 * @return the information
	 */
	public InformationMessage getInformation() {
		return information;
	}

	/**
	 * @param information the information to set
	 */
	public void setInformation(InformationMessage information) {
		this.information = information;
	}

}
