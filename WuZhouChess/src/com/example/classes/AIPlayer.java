/**
 * 
 */
package com.example.classes;

import java.util.ArrayList;

/**
 * @author SmartGang
 *2012-8-22:����ֻ�ж�һ������ɨ�赱ǰ���ߵĲ�������õ�
 */
public class AIPlayer extends Player {

	private ChessBoard cb;
	private ArrayList<Movement> movementList;
	private ArrayList<Chess> opponentChessList;
	private int opponentColor;
	
	public AIPlayer(String playerName, int color, int playerType) {
		super(playerName, color, playerType);
		// TODO Auto-generated constructor stub
		movementList=new ArrayList<Movement>();
		cb=new ChessBoard();
		if(color==ChessColor_Black)opponentColor=ChessColor_White;
		else opponentColor=ChessColor_Black;
	}

	private void copyChessBoard(ChessBoard chessBoard)
	{
		cb=new ChessBoard();
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				cb.chessBoard[i][j]=chessBoard.chessBoard[i][j];
			}
		}
	}
	/*********************** �����ǳԷ����жϣ��ճ�ChessView�������******************************/
		//��chess�ƶ���Ŀ��x,y��Ҫ�ж��Ƿ���ӣ��������Ե�����Ϊ�������
		//����ǰ����ǰ���Ѿ�����move���жϣ�����Ϊ�ɹ����������ﲻ��move�����ж�
		private int eat(Chess chess, int x, int y)
		{
			ArrayList<Chess> eatResult= new ArrayList<Chess>();
			ArrayList<Chess> myArray=chessArray;
			ArrayList<Chess> yourArray=opponentChessList;
			int eatCount=eatDing(eatResult,chess,x,y);
			eatCount+=eatJia(eatResult,chess,x,y);
			eatCount+=eatTiao(eatResult,chess,x,y);
			return eatCount;
	/*
			for(int i=0;i<eatCount;i++)
			{
				Chess eat=eatResult.get(i);
				for(int j=0;j<yourArray.size();j++)
				{
					if(yourArray.get(j).x==eat.x&&yourArray.get(j).y==eat.y)
					{
						yourArray.remove(j);
						eat.color=chess.color;
						cb.chessBoard[chess.x][chess.y]=GridEmpty;
						cb.chessBoard[eat.x][eat.y]=chess.color;
						myArray.add(eat);
					}
				}
			}
	*/
		}
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ������������ͬɫ�Ӷ�����һɫ�ӣ����Ҹ�����û������������
	private int eatDing(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//���Ĳ������жϣ��ᣬ������б����б
		//���߳ԣ�
		int myCount=0;
		int yourCount=0;
		Chess yourChess = null;
		Chess mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[i][y]==GridEmpty)continue;
			else if(cb.chessBoard[i][y]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,y,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,y,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.x-x)+(yourChess.x-mySecondChess.x))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//���߳�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[x][i]==GridEmpty)continue;
			else if(cb.chessBoard[x][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(x,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(x,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.y-y)+(yourChess.y-mySecondChess.y))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		if((x+y)%2!=0)return eatCount;//���жϸõ��ǲ���ż����
		//��б�߳�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int ix=i+(x-y);
			if(ix<0||ix>4)continue;
			if(cb.chessBoard[ix][i]==GridEmpty)continue;
			else if(cb.chessBoard[ix][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(ix,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(ix,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.x-x)+(yourChess.x-mySecondChess.x))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//��б�߳�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int iy=x+y-i;
			if(iy<0||iy>4)continue;
			if(cb.chessBoard[i][iy]==GridEmpty)continue;
			else if(cb.chessBoard[i][iy]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,iy,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,iy,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs((yourChess.x-x)+(yourChess.x-mySecondChess.x))==3)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		return eatCount;
	}
	
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ������ͬɫ�����Ӽ�����һɫ�ӣ����Ҹ�����û������������
	private int eatJia(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//���Ĳ������жϣ��ᣬ������б����б
		//���߼У�
		int myCount=0;
		int yourCount=0;
		Chess yourChess = null;
		Chess mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[i][y]==GridEmpty)continue;
			else if(cb.chessBoard[i][y]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,y,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,y,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.x-x)==1&&Math.abs(yourChess.x-mySecondChess.x)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//���߼�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[x][i]==GridEmpty)continue;
			else if(cb.chessBoard[x][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(x,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(x,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.y-y)==1&&Math.abs(yourChess.y-mySecondChess.y)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		if((x+y)%2!=0)return eatCount;//���жϸõ��ǲ���ż����
		//��б�߼�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int ix=i+(x-y);
			if(ix<0||ix>4)continue;
			if(cb.chessBoard[ix][i]==GridEmpty)continue;
			else if(cb.chessBoard[ix][i]==chess.color)
			{
				myCount++;
				if(y!=i)mySecondChess=new Chess(ix,i,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(ix,i,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.x-x)==1&&Math.abs(yourChess.x-mySecondChess.x)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		//��б�߼�
		myCount=0;
		yourCount=0;
		yourChess = null;
		mySecondChess = null;
		for(int i=0;i<5;i++)
		{
			int iy=x+y-i;
			if(iy<0||iy>4)continue;
			if(cb.chessBoard[i][iy]==GridEmpty)continue;
			else if(cb.chessBoard[i][iy]==chess.color)
			{
				myCount++;
				if(x!=i)mySecondChess=new Chess(i,iy,chess.color);
			}
			else
			{
				yourCount++;
				yourChess=new Chess(i,iy,yourColor);
			}
		}
		if(myCount==2&&yourCount==1)
		{
			if(Math.abs(yourChess.x-x)==1&&Math.abs(yourChess.x-mySecondChess.x)==1)
			{
				eatResult.add(yourChess);
				eatCount++;
			}
		}
		return eatCount;
	}
	//�Է�����
	//��һ��ģ���ݣ�б���ϣ��ƶ�������������߸���һ����ɫ�ӣ��Ҵ�����ֻ���������ӣ�������ɫ�ӱ���
	private int eatTiao(ArrayList<Chess> eatResult,Chess chess, int x, int y)
	{
		if(eatResult==null)return 0;
		int eatCount=0;
		int yourColor=ChessColor_White;
		if(chess.color==ChessColor_White)
		{
			yourColor=ChessColor_Black;
		}
		//���Ĳ������жϣ��ᣬ������б����б
		//��������
		int myCount=0;
		int yourCount=0;
		Chess yourFirstChess = null;
		Chess yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[i][y]==GridEmpty)continue;
			else if(cb.chessBoard[i][y]==chess.color)
			{
				myCount++;
//				if(x!=i)mySecondChess=new Chess(i,y,chess.color);
			}
			else
			{
				yourCount++;
//				yourChess=new Chess(i,y,yourColor);
				if(yourCount==1)yourFirstChess=new Chess(i,y,yourColor);
				else yourSecondChess=new Chess(i,y,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.x-x)==1&&Math.abs(yourSecondChess.x-x)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		//������
		myCount=0;
		yourCount=0;
		yourFirstChess = null;
		yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			if(cb.chessBoard[x][i]==GridEmpty)continue;
			else if(cb.chessBoard[x][i]==chess.color)
			{
				myCount++;
//				if(y!=i)mySecondChess=new Chess(x,i,chess.color);
			}
			else
			{
				yourCount++;
				if(yourCount==1)yourFirstChess=new Chess(x,i,yourColor);
				else yourSecondChess=new Chess(x,i,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.y-y)==1&&Math.abs(yourSecondChess.y-y)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		if((x+y)%2!=0)return eatCount;//���жϸõ��ǲ���ż����
		//��б����
		myCount=0;
		yourCount=0;
		yourFirstChess = null;
		yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			int ix=i+(x-y);
			if(ix<0||ix>4)continue;
			if(cb.chessBoard[ix][i]==GridEmpty)continue;
			else if(cb.chessBoard[ix][i]==chess.color)
			{
				myCount++;
//				if(y!=i)mySecondChess=new Chess(ix,i,chess.color);
			}
			else
			{
				yourCount++;
				if(yourCount==1)yourFirstChess=new Chess(ix,i,yourColor);
				else yourSecondChess=new Chess(ix,i,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.x-x)==1&&Math.abs(yourSecondChess.x-x)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		//��б����
		myCount=0;
		yourCount=0;
		yourFirstChess = null;
		yourSecondChess = null;
		for(int i=0;i<5;i++)
		{
			int iy=x+y-i;
			if(iy<0||iy>4)continue;
			if(cb.chessBoard[i][iy]==GridEmpty)continue;
			else if(cb.chessBoard[i][iy]==chess.color)
			{
				myCount++;
//				if(x!=i)mySecondChess=new Chess(i,iy,chess.color);
			}
			else
			{
				yourCount++;
				if(yourCount==1)yourFirstChess=new Chess(i,iy,yourColor);
				else yourSecondChess=new Chess(i,iy,yourColor);
			}
		}
		if(myCount==1&&yourCount==2)
		{
			if(Math.abs(yourFirstChess.x-x)==1&&Math.abs(yourSecondChess.x-x)==1)
			{
				eatResult.add(yourFirstChess);
				eatResult.add(yourSecondChess);
				eatCount+=2;
			}
		}
		return eatCount;
	}
	
	public Movement getBestMovement(ChessBoard chessBoard)
	{
		copyChessBoard(chessBoard);
		getOpponentChessList();
		Movement bestMovement=null;
		Movement tempMovement=null;
		int bestMovementRate=0;//�������浱ǰ��ò���ĵ÷�
		int tempRate=0;
		 //�ܳԱ��ˣ�һ����10��
		 //��ֹ���˳ԣ�һ����10��
		 //���˱�һ���ӿ�10��
		int movementCount=getMovementList();
		if(movementCount>0)bestMovement=movementList.get(0);
		for(int i=0;i<movementCount;i++)
		{
			tempMovement=movementList.get(i);
			move(tempMovement);//�������Ͻ�׼���ƶ������ƺã��Ա����Ե��ж�
			tempRate=eat(new Chess(tempMovement.fromX,tempMovement.fromY,color),
								tempMovement.toX,
								tempMovement.toY)*10;
			reverseMoving(tempMovement);//�жϺ�Ҫ�������ƻ���
			if(tempRate>bestMovementRate)
			{
				bestMovement=tempMovement;
				bestMovementRate=tempRate;
			}			
		}
		if(bestMovementRate==0)
		{
			int i=(int)(Math.random()*movementCount);
			bestMovement=movementList.get(i);
		}
		return bestMovement;
	}

	//��ȡ���ƶ����б������ƶ���
	private int getMovementList()
	{
		
		movementList.clear();
		int chessCount=chessArray.size();
		for(int i=0;i<chessCount;i++)
		{	
			int fromX=chessArray.get(i).x;
			int fromY=chessArray.get(i).y;
			for(int j=0;j<8;j++)
			{
				int toX=fromX;
				int toY=fromY;
				//��8����������ж�
				switch(j)
				{
				case 0:	toX--;break;//���
				case 1:	if((fromX+fromY)%2!=0)break;
						toX--;//����
						toY--;
						break;
				case 2:	toY--;break;//�Ϸ�
				case 3:	if((fromX+fromY)%2!=0)break;
						toX++;//����
						toY--;
						break;
				case 4:	toX++;break;//�ұ�
				case 5:	if((fromX+fromY)%2!=0)break;
						toX++;//����
						toY++;
						break;
				case 6:	toY++;break;//�·�
				case 7:	if((fromX+fromY)%2!=0)break;
						toX--;//����
						toY++;
						break;
				default:break;
				}
				Movement m=new Movement(fromX,fromY,toX,toY);
				if(isMovable(m))movementList.add(m);
			}
		}
		return movementList.size();
	}
	
	private int getOpponentChessList()
	{
		//���ɶ��ֵ���������
		opponentChessList=new ArrayList<Chess>();
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				if(cb.chessBoard[i][j]==opponentColor)
				{
					Chess c=new Chess(i,j,opponentColor);
					opponentChessList.add(c);
				}
			}
		}
		return opponentChessList.size();
	}
	private boolean isMovable(Movement movement)
	{
		if(cb.chessBoard[movement.fromX][movement.fromY]!=color)return false;
		if(movement.toX<0||movement.toX>4)return false;
		if(movement.toY<0||movement.toY>4)return false;
		if(cb.chessBoard[movement.toX][movement.toY]==GridEmpty)return true;
		return false;
	}
	/* (non-Javadoc)
	 * @see com.example.classes.Player#move(com.example.classes.ChessBoard, com.example.classes.Chess, int, int)
	 */
	@Override
	public Movement move(ChessBoard chessBoard, Chess c, int targetX, int targetY) {
		// TODO Auto-generated method stub
		//11.19ˢ��ChessBoardView,��move�������и�д��ִֻ�У�AIpalyer����getBestMovment����������
//		copyChessBoard(chessBoard);
//		getOpponentChessList();
//		Movement m=getBestMovement();
//		if(c!=null)
//		{
//			Chess temp;
		Movement movement=null;
			for(int i=0;i<chessArray.size();i++)
			{	
				Chess chess=chessArray.get(i);
				if(chess.x==c.x&&
						chess.y==c.y&&
						chess.color==c.color)
				{
					chess.x=targetX;
					chess.y=targetY;
					movement=new Movement(c.x,c.y,targetX,targetY);
				}
			}
		return movement;
//		}
//		return m;
	}

	private void move(Movement m)
	{
		int color =cb.chessBoard[m.fromX][m.fromY];
		cb.chessBoard[m.fromX][m.fromY]=GridEmpty;
		cb.chessBoard[m.toX][m.toY]=color;
	}
	
	private void reverseMoving(Movement m)
	{
		int color =cb.chessBoard[m.toX][m.toY];
		cb.chessBoard[m.toX][m.toY]=GridEmpty;
		cb.chessBoard[m.fromX][m.fromY]=color;
	}


}
