package com.example.internet;

import org.json.JSONException;
import org.json.JSONObject;



public class GameTable {
	public String name;
	final static String NameKey="NameKey";
	public int ID;
	final static String IDKey="IDKey";
	public GamePlayer player1;
	final static String Player1Key="Player1Key";
	public GamePlayer player2;
	final static String Player2Key="Player2Key";
	private GamePlayer currentPlayer;
	public int playerNum;
	final static String PlayerNumKey="PlayerNumKey";
	public int status;
	final static String StatusKey="StatusKey";
	public final static int TABLE_STATUS_WAITING=1;
	public final static int TABLE_STATUS_READY=2;
	public final static int TABLE_STATUS_PLAYING=3;
	
	public GameTable()
	{
		playerNum=0;
		status=TABLE_STATUS_WAITING;
	}

	public GameTable(JSONObject jsonObject) {
		// TODO Auto-generated constructor stub
		try {
			name=jsonObject.getString(NameKey);
			ID=jsonObject.getInt(IDKey);
			JSONObject jsonPlayer1=jsonObject.optJSONObject(Player1Key);
			if(jsonPlayer1!=null)player1=new GamePlayer(jsonPlayer1);
			playerNum=jsonObject.getInt(PlayerNumKey);
			JSONObject jsonPlayer2=jsonObject.optJSONObject(Player2Key);
			if(jsonPlayer2!=null)player2=new GamePlayer(jsonPlayer2);
			status=jsonObject.getInt(StatusKey);	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * @return the player1
	 */
	public GamePlayer getPlayer1() {
		return player1;
	}

	/**
	 * @param player1 the player1 to set
	 */
	public void setPlayer1(GamePlayer player1) {
		if(player1==null)playerNum++;
		this.player1 = player1;
	}

	/**
	 * @return the player2
	 */
	public GamePlayer getPlayer2() {
		return player2;
	}

	/**
	 * @param player2 the player2 to set
	 */
	public void setPlayer2(GamePlayer player2) {
		if(player2==null)playerNum++;
		this.player2 = player2;
	}

	/**
	 * @return the playerNum
	 */
	public int getPlayerNum() {
		return playerNum;
	}

	/**
	 * @param playerNum the playerNum to set
	 */
	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	public JSONObject toJSONObject() {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		try {
			json.put(NameKey, name);
			json.put(IDKey, ID);
			if(player1!=null)json.put(Player1Key, player1.toJSONObject());
			json.put(PlayerNumKey, playerNum);
			if(player2!=null)json.put(Player2Key, player2.toJSONObject());
			json.put(StatusKey, status);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public void playerOut(GamePlayer player)
	{
		if(player1.name.equals(player.name))
			player1=null;
		else player2=null;
		playerNum--;	
	}
}
