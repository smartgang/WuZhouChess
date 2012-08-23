/**
 * 
 */
package com.example.wuzhouchess;

import com.example.classes.AIPlayer;
import com.example.classes.HumanPlayer;
import com.example.classes.Player;
import com.example.wuzhouchess.Views.ChessView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author SmartGang
 *
 */
public class PlayActivity extends Activity {

	ChessView cbv;
	Button btnPause;
	Player whitePlayer;
	Player blackPlayer;
	Handler viewHolderHandler;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_view);
		cbv=(ChessView)findViewById(R.id.chessboard);
		btnPause=(Button)findViewById(R.id.btnPause);
		
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
				case ChessView.GameStatus_Over:gameOver();
					break;
				default:break;
				}
				
			}			
		};
		
		
		
		String playerType=getIntent().getStringExtra("PlayerType");
		if(playerType.equals("H2AI"))//�˻���ս
		{
			whitePlayer=new AIPlayer( "�׷�", Player.ChessColor_White, Player.PlayerType_AI);
			blackPlayer=new HumanPlayer( "�ڷ�", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		else//���˶�ս
		{
			whitePlayer=new HumanPlayer( "�׷�", Player.ChessColor_White, Player.PlayerType_Human);
			blackPlayer=new HumanPlayer( "�ڷ�", Player.ChessColor_Black, Player.PlayerType_Human);

		}
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
		
		btnPause.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cbv.gamePause();
				new AlertDialog.Builder(PlayActivity.this)
				.setTitle("Pausing.....")
				.setPositiveButton("Resume!!!", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton)
					{
						cbv.gameResume();
					}
				})
				.setNegativeButton("ȡ��",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton)
					{
						
					}
				})
				.show();				
				
			}
			
		});
	}
	
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
}
