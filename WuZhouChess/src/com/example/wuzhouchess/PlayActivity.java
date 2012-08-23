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
		
		//用户交互消息处理
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
		if(playerType.equals("H2AI"))//人机对战
		{
			whitePlayer=new AIPlayer( "白方", Player.ChessColor_White, Player.PlayerType_AI);
			blackPlayer=new HumanPlayer( "黑方", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		else//人人对战
		{
			whitePlayer=new HumanPlayer( "白方", Player.ChessColor_White, Player.PlayerType_Human);
			blackPlayer=new HumanPlayer( "黑方", Player.ChessColor_Black, Player.PlayerType_Human);

		}
		//设置与chessView的通信handler
		cbv.setViewHolderHandler(viewHolderHandler);
		cbv.setPlayer(whitePlayer, blackPlayer);
		//先弹出框让用户选择开始，这时也正好往成界面大小的测量，可以在用户选择后设定棋盘位置
		new AlertDialog.Builder(PlayActivity.this)
		.setTitle("Ready.....")
		.setPositiveButton("Go!!!", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
			{	
				cbv.setChessBoard();
				//设置游戏状态
				//cbv.gameStatus=ChessView.GameStatus_Playing;
				cbv.gameStart();
				cbv.invalidate();				
			}
		})
		.setNegativeButton("取消",new DialogInterface.OnClickListener(){
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
				.setNegativeButton("取消",new DialogInterface.OnClickListener(){
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
				//设置游戏状态
				//cbv.gameStatus=ChessView.GameStatus_Playing;
				cbv.gameRestart();
				cbv.invalidate();
			}
		})
		.setNegativeButton("取消",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
			{
				
			}
		})
		.show();
	}
}
