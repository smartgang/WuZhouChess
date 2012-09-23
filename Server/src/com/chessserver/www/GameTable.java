/**
 * 
 */
package com.chessserver.www;

import net.sf.json.JSONObject;

import com.chessserver.www.rules.Movement;

/**
 * @author SmartGang
 *
 */

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
	final static int TABLE_STATUS_WAITING=1;
	final static int TABLE_STATUS_READY=2;
	final static int TABLE_STATUS_PLAYING=3;
	
	GameTable()
	{
		
	}

	public GameTable(JSONObject jsonObject) {
		// TODO Auto-generated constructor stub
		name=jsonObject.getString(NameKey);
		ID=jsonObject.getInt(IDKey);
		player1=new GamePlayer();
		player1.name=jsonObject.getString(Player1Key);
		playerNum=jsonObject.getInt(PlayerNumKey);
		if(playerNum==2)
		{
			player2=new GamePlayer();
			//如果player2还没有人，则默认为"null"
			player2.name=jsonObject.optString(Player2Key, "null");
		}
		status=jsonObject.getInt(StatusKey);		
	}

	public void gameStart()
	{
		
	}
	
	public void playerMove(PlayerAgent player,Movement move)
	{
		
	}
	
	public void gameEnd()
	{
		
	}
	
	public void inTable(PlayerAgent player)
	{
		
	}
	
	public void outTable(PlayerAgent player)
	{
		
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public JSONObject toJSONObject() {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		json.put(NameKey, name);
		json.put(IDKey, ID);
		json.put(Player1Key, player1.name);
		json.put(PlayerNumKey, playerNum);
		if(playerNum==2)json.put(Player2Key, player2.name);
		json.put(StatusKey, status);
		return json;
	}

}

