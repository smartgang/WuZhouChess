/**
 * 
 */
package com.chessserver.www.model;

import com.chessserver.www.GameHall;
import com.chessserver.www.GamePlayer;
import com.chessserver.www.GameTable;

import net.sf.json.JSONObject;

/**
 * @author SmartGang
 *
 */
public class InformationMessage extends MessageBody {

	private int informationType;
	private final static String InformationTypeKey="InformationTypeKey";
	public final static int INFORMATION_TYPE_PLAYER=1;
	public final static int INFORMATION_TYPE_HALL=2;
	public final static int INFORMATION_TYPE_TABLE=3;
	GameHall gameHall;
	private final static String GameHallKey="GameHallKey";
	GameTable gameTable;
	private final static String GameTableKey="GameTableKey";
	GamePlayer player;
	private final static String GamePlayerKey="GamePlayerKey";
	
	/**
	 * @param informationType
	 * @param gameHall
	 * @param gameTable
	 * @param player
	 */
	public InformationMessage(int informationType, GameHall gameHall,
			GameTable gameTable, GamePlayer player) {
		super();
		this.informationType = informationType;
		this.gameHall = gameHall;
		this.gameTable = gameTable;
		this.player = player;
		messageType=MessageBody.MESSAGE_TYPE_INFORMATION;
	}

	public InformationMessage(JSONObject jsonObject) {
		// TODO Auto-generated constructor stub
		messageType=MessageBody.MESSAGE_TYPE_INFORMATION;
		decodeFromJSON(jsonObject);
	}

	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#codeToJSONObject()
	 */
	@Override
	JSONObject codeToJSONObject() {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		json.put(typeKey, messageType);
		json.put(InformationTypeKey, informationType);
		switch(informationType)
		{
		case INFORMATION_TYPE_PLAYER:
			if(player!=null)json.put(GamePlayerKey, player.toJSONObject());
			break;
		case INFORMATION_TYPE_HALL:
			if(gameHall!=null)json.put(GameHallKey, gameHall.toJSONObject());
			break;
		case INFORMATION_TYPE_TABLE:
			if(gameTable!=null)json.put(GameTableKey, gameTable.toJSONObject());
			break;
		default:break;		
		}
		return json;
	}

	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#decodeFromJSON(net.sf.json.JSONObject)
	 */
	@Override
	MessageBody decodeFromJSON(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		messageType=MessageBody.MESSAGE_TYPE_INFORMATION;
		informationType=jsonObject.getInt(InformationTypeKey);
		switch(informationType)
		{
		case INFORMATION_TYPE_PLAYER:
			player=new GamePlayer(jsonObject.getJSONObject(GamePlayerKey));
			break;
		case INFORMATION_TYPE_HALL:
			gameHall=new GameHall(jsonObject.getJSONObject(GameHallKey));
			break;
		case INFORMATION_TYPE_TABLE:
			gameTable=new GameTable(jsonObject.getJSONObject(GameTableKey));
			break;
		default:break;
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.chessserver.www.model.MessageBody#setMessageType()
	 */
	@Override
	void setMessageType() {
		// TODO Auto-generated method stub
		messageType=MessageBody.MESSAGE_TYPE_INFORMATION;
	}

	/**
	 * @return the gameHall
	 */
	public GameHall getGameHall() {
		return gameHall;
	}

	/**
	 * @param gameHall the gameHall to set
	 */
	public void setGameHall(GameHall gameHall) {
		this.gameHall = gameHall;
	}

	/**
	 * @return the gameTable
	 */
	public GameTable getGameTable() {
		return gameTable;
	}

	/**
	 * @param gameTable the gameTable to set
	 */
	public void setGameTable(GameTable gameTable) {
		this.gameTable = gameTable;
	}

	/**
	 * @return the player
	 */
	public GamePlayer getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(GamePlayer player) {
		this.player = player;
	}

}
