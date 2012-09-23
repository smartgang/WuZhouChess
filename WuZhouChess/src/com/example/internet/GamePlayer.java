package com.example.internet;

import org.json.JSONException;
import org.json.JSONObject;


public class GamePlayer {
	
	public int ID;
	final static String IDKey="IDKey";
	public String name;
	final static String NameKey="NameKey";
	public int score;
	final static String ScoreKey="ScoreKey";
	public int gender;
	final static String GenderKey="GenderKey";
	private final static int GENDER_MALE=1;
	private final static int GENDER_FEMALE=2;
	
	/**
	 * 
	 */
	public GamePlayer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public GamePlayer(JSONObject jsonObject) {
		
		try {
			name=jsonObject.getString(NameKey);
			ID=jsonObject.getInt(IDKey);
			score=jsonObject.getInt(ScoreKey);
			gender=jsonObject.getInt(GenderKey);	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public JSONObject toJSONObject()
	{
		JSONObject json=new JSONObject();
		try {
			json.put(NameKey, name);
			json.put(IDKey, ID);
			json.put(ScoreKey, score);
			json.put(GenderKey, gender);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
}
