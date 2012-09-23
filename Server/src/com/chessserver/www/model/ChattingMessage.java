/**
 * 
 */
package com.chessserver.www.model;

import net.sf.json.*;

/**
 * @author SmartGang
 *
 */
public class ChattingMessage extends MessageBody {

	final static String toKey="to";
	final static String fromKey="from";
	final static String contentKey="content";
	final static String placeKey="place";
	
	String to;
	String from;
	String content;
	String place;

	public String toString() {
		return codeToJSONObject().toString();
	}
	
	/**
	 * 从json对象创建消息
	 */
	public ChattingMessage(JSONObject json) {
		decodeFromJSON(json);
	}

	/**
	 * @param to
	 * @param from
	 * @param content
	 * @param place
	 */
	public ChattingMessage(String to, String from, String content, String place) {
		super();
		this.to = to;
		this.from = from;
		this.content = content;
		this.place = place;
		setMessageType();
	}
	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#codeToJSONObject()
	 */
	@Override
	JSONObject codeToJSONObject() {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		try {
			json.put(toKey, to);
			json.put(fromKey, from);
			json.put(contentKey, content);
			json.put(placeKey, place);
			json.put(typeKey, messageType);
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
		try {
			to=json.getString(toKey);
			from=json.getString(fromKey);
			content=json.getString(contentKey);
			place=json.getString(placeKey);
			messageType=json.getInt(typeKey);
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
		// TODO Auto-generated method stub
		messageType=MessageBody.MESSAGE_TYPE_CHATTING;
	}

}
