/**
 * 
 */
package com.example.internet;
/**
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author SmartGang
 *
 */
public class GameHall {

	public String name;
	private final static String NameKey="NameKey";
	public int ID;
	private final static String IDKey="IDKey";
	public int maxTableNum;
	public int currentTableNum;
	private final static String TableNumKey="TableNumKey";
	public int currentPlayerNum;
	private final static String PlayerNumKey="PlayerNumKey";
//	public ArrayList<PlayerAgent> playerList;
//	public HashMap<String,PlayerAgent> playerList;	
	public ArrayList<GameTable> tableList;
	private final static String TableListKey="TableListKey";
	
	/**
	 *根据json创建gameHall信息，用于解码网络信息
	 */
	public GameHall(JSONObject json) {
//		super();
		try {
			name=json.getString(NameKey);
			ID=json.getInt(IDKey);
			currentPlayerNum=json.getInt(PlayerNumKey);
			currentTableNum=json.getInt(TableNumKey);
			JSONArray jsonArray=json.getJSONArray(TableListKey);
			for(int i=0;i<jsonArray.length();i++)
			{
				GameTable gameTable=new GameTable(jsonArray.getJSONObject(i));
				tableList.add(gameTable);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//
	
	/**
	 * 将gameHall信息转换为JSONObject，用于网络传送
	 * @return
	 */
	public JSONObject toJSONObject()
	{
		JSONObject json=new JSONObject();
		try {
			json.put(NameKey, name);
			json.put(IDKey, ID);
			json.put(PlayerNumKey, currentPlayerNum);
			json.put(TableNumKey, currentTableNum);
			JSONArray tableArray=new JSONArray();
			for(int i=0;i<tableList.size();i++)
			{
				JSONObject tableJSON=tableList.get(i).toJSONObject();
				tableArray.put(tableJSON);			
			}
			json.put(TableListKey,tableArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

