/**
 * 
 */
package com.chessserver.www;

import java.io.*;
import java.net.*;

/**
 * @author SmartGang
 *
 */
public class Server {

	static int userNum;
	static PlayerAgent player;
	static GameHall gameHall;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerSocket ss=new ServerSocket(9999);
		System.out.println("Listening on 9999...");
		userNum=0;
		gameHall=new GameHall("GameHall_1", 1, 2);
		while(true)
		{
			Socket sc=ss.accept();
			userNum++;
			DataInputStream din=new DataInputStream(sc.getInputStream());
			DataOutputStream dout=new DataOutputStream(sc.getOutputStream());
			System.out.println("<#user connected#>1");
//			dout.writeUTF("<#hello!!#>1");
			player=new PlayerAgent(sc,din,dout);
			player.setGameHall(gameHall);
			player.start();
		}
	}

}
