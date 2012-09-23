/**
 * 
 */
package com.chessserver.www.model;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * @author SmartGang
 *
 */
public class MessageHolder {
	String from;
	MessageBody body;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageHolder []";
	}
	
	JSONObject codeToJSONObject()
	{
		JSONObject json=new JSONObject();
		try {
			json.put("body", body.codeToJSONObject());
			json.put("from", from);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	void decodeFromJSONObject(JSONObject json)
	{
		try {
			from=json.getString("from");
			JSONObject jsonBody=json.getJSONObject("body");
			switch(jsonBody.getInt(MessageBody.typeKey))
			{
			case MessageBody.MESSAGE_TYPE_CHATTING:
				body=new ChattingMessage(jsonBody);
				break;
//			case MessageBody.MESSAGE_TYPE_DATA:
//				body=new DataMessage(jsonBody);
//				break;
			default :break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
