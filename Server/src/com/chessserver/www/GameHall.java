/**
 * 
 */
package com.chessserver.www;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
	public HashMap<String,PlayerAgent> playerList;	
	public ArrayList<GameTable> tableList;
	private final static String TableListKey="TableListKey";
	

	/**
	 * @param name
	 * @param iD
	 * @param maxTableNum
	 */
	public GameHall(String name, int iD, int maxTableNum) {
		super();
		this.name = name;
		ID = iD;
		this.maxTableNum = maxTableNum;
		playerList=new HashMap<String,PlayerAgent>();
		tableList=new ArrayList<GameTable>();
	}

	/**player进入Gamehall
	 * @param player
	 */
	public void playerIn(PlayerAgent player)
	{
		playerList.put(player.getPlayerName(),player);
		player.setGameHall(this);
		currentPlayerNum++;
		System.out.println(player.getPlayerName()+" get in the GameHall "+name);
	}
	
	/**player退出GameHall
	 * @param player
	 */
	public void playerOut(PlayerAgent player)
	{
		player.setGameHall(null);
		playerList.remove(player.getName());
		currentPlayerNum--;
		System.out.println(player.getName()+" go out of the GameHall "+name);
	}
	
	/**player在Gamehall中选择创建新的table
	 * @param player
	 * @return 新创建的GameTable句柄
	 */
	public GameTable newTable(PlayerAgent player)
	{
		GameTable table=new GameTable();
		//按创建table的人的名字来命令table
		table.setName(player.getName());
		System.out.println("new table "+ table.getName()+" by "+player.getName());
		currentTableNum++;
		table.inTable(player);// 创建后自动进入房间				
		return table;
	}

	/**player进入GameTable
	 * @param table
	 * @param player
	 * @return 进入结果
	 */
	public boolean inTable(GameTable table, PlayerAgent player)
	{
		return false;
	}
	/**用户退出table
	 * @param table
	 * @param player
	 */
	public void outTable(GameTable table, PlayerAgent player)
	{
		
	}
	
	/**
	 *根据json创建gameHall信息，用于解码网络信息
	 */
	public GameHall(JSONObject json) {
//		super();
		name=json.getString(NameKey);
		ID=json.getInt(IDKey);
		currentPlayerNum=json.getInt(PlayerNumKey);
		currentTableNum=json.getInt(TableNumKey);
		JSONArray jsonArray=json.getJSONArray(TableListKey);
		for(int i=0;i<jsonArray.size();i++)
		{
			GameTable gameTable=new GameTable(jsonArray.getJSONObject(i));
			tableList.add(gameTable);
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
		json.put(NameKey, name);
		json.put(IDKey, ID);
		json.put(PlayerNumKey, currentPlayerNum);
		json.put(TableNumKey, currentTableNum);
		JSONArray tableArray=new JSONArray();
		for(int i=0;i<tableList.size();i++)
		{
			JSONObject tableJSON=tableList.get(i).toJSONObject();
			tableArray.add(tableJSON);			
		}
		json.put(TableListKey,tableArray);
		return json;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
