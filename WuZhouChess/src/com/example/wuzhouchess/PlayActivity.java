/**
 * 
 */
package com.example.wuzhouchess;

import com.example.classes.AIPlayer;
import com.example.classes.HumanPlayer;
import com.example.classes.InternetPlayer;
import com.example.classes.Player;
import com.example.wuzhouchess.Views.ChessBoardView;
import com.example.wuzhouchess.Views.ChessView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * @author SmartGang
 *
 */
public class PlayActivity extends Activity {

	Player blackPlayer;
//	Button btnPause;
	ChessBoardView cbv;
	static Handler viewHolderHandler;
	Player whitePlayer;
/*	
	void gameOver()
	{
		new AlertDialog.Builder(PlayActivity.this)
		.setTitle("One more?")
		.setPositiveButton("Go!!!", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
			{	
//				cbv.setChessBoard();
				//������Ϸ״̬
				//cbv.gameStatus=ChessView.GameStatus_Playing;
				cbv.gameRestart();
				cbv.invalidate();
			}
		})
		.setNegativeButton("ȡ��",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
			{
				
			}
		})
		.show();
	}
*/	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
/*
		setContentView(R.layout.play_view);
		cbv=(ChessView)findViewById(R.id.chessboard);
//		btnPause=(Button)findViewById(R.id.btnPause);
		View topHeader;
		topHeader=(View)findViewById(R.id.main_header);
		Button btnLeft=(Button)topHeader.findViewById(R.id.top_btn_left);
		btnLeft.setText("����");
//		btnLeft.setVisibility(View.INVISIBLE);
		btnPause=(Button)topHeader.findViewById(R.id.top_btn_right);
		btnPause.setText("��ͣ");
//		btnRight.setVisibility(View.INVISIBLE);
		TextView top_textView=(TextView)topHeader.findViewById(R.id.tv_toptitle);
		top_textView.setText("����");
*/		
		//�û�������Ϣ����
		viewHolderHandler=new Handler()
		{

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{
				case ChessBoardView.ACTION_MSG_EXIT:
					PlayActivity.this.finish();
					break;
				default:break;
				}
				
			}			
		};
				
		String playerType=getIntent().getStringExtra("PlayerType");
		if(playerType.equals("H2AI"))//�˻���ս
		{
			whitePlayer=new AIPlayer( "����", Player.ChessColor_White, Player.PlayerType_AI);
			blackPlayer=new HumanPlayer( "��", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		else if(playerType.equals("H2H"))//���˶�ս
		{
			whitePlayer=new HumanPlayer( "·�˼�", Player.ChessColor_White, Player.PlayerType_Human);
			blackPlayer=new HumanPlayer( "·����", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		else //�����ս
		{
			whitePlayer=new InternetPlayer( "�׷�", Player.ChessColor_White, Player.PlayerType_Internet);
			blackPlayer=new HumanPlayer( "�ڷ�", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		cbv=new ChessBoardView(this,viewHolderHandler);
		cbv.setPlayer(whitePlayer);
		cbv.setPlayer(blackPlayer);
		setContentView(cbv);
		cbv.startHeartBeat();
	}
/*
		//������chessView��ͨ��handler
		cbv.setViewHolderHandler(viewHolderHandler);
		cbv.setPlayer(whitePlayer, blackPlayer);
		//�ȵ��������û�ѡ��ʼ����ʱҲ�������ɽ����С�Ĳ������������û�ѡ����趨����λ��
		new AlertDialog.Builder(PlayActivity.this)
		.setTitle("Ready.....")
		.setPositiveButton("Go!!!", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
			{	
				cbv.setChessBoard();
				//������Ϸ״̬
				//cbv.gameStatus=ChessView.GameStatus_Playing;
				cbv.gameStart();
				cbv.invalidate();				
			}
		})
		.setNegativeButton("ȡ��",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
			{
				
			}
		})
		.show();
*/
}
