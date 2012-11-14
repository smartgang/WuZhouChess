/**
 * 
 */
package com.example.classes;



/**
 * @author SmartGang
 *
 */
public class HumanPlayer extends Player {

	private Movement lastMovement=null;
	/**
	 * @param playerName
	 * @param color
	 * @param playerType
	 */
	public HumanPlayer(String playerName, int color, int playerType) {
		super(playerName, color, playerType);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.example.classes.Player#move(com.example.classes.ChessBoard, com.example.classes.Chess, int, int)
	 */
	@Override
	public Movement move(ChessBoard chessBorad, Chess c, int targetX, int targetY) {
		// TODO Auto-generated method stub
		
		int chessCount=chessArray.size();
		for(int i=0;i<chessCount;i++)
		{
			Chess chess=chessArray.get(i);
			if(chess.x==c.x&&
					chess.y==c.y&&
					chess.color==c.color)
			{
				chess.x=targetX;
				chess.y=targetY;
				Movement movement=new Movement(c.x,c.y,targetX,targetY);
				lastMovement=movement;
				return movement;
			}
		}
		lastMovement=null;
		return null;
	}

}
