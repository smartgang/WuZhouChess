/**
 * 
 */
package com.example.classes;

import android.os.Handler;

/**
 * @author SmartGang
 *
 */
public class InternetPlayer extends Player {

//	Handler gameCenterHandler;
	Movement movement;
	
	public InternetPlayer(String playerName, int color, int playerType) {
		super(playerName, color, playerType);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.example.classes.Player#move(com.example.classes.ChessBoard, com.example.classes.Chess, int, int)
	 */
	@Override
	public Movement move(ChessBoard chessBorad, Chess c, int targetX,
			int targetY) {
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
//				Movement movement=new Movement(c.x,c.y,targetX,targetY);
				return movement;
			}
		}
		return null;
	}

	@Override
	public void tellOpponet() {
		// TODO Auto-generated method stub
		
	}

	public void getInternetMovement(Movement movement)
	{
		//将movement传递进来给move函数使用
		this.movement=movement;
		Chess movedChess=new Chess(movement.fromX,movement.fromY,this.color);
		chessView.setMovedChess(movedChess);
		chessView.setWaiting(false);
	}
}
