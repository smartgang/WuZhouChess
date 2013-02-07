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
		btnLeft.setText("返回");
//		btnLeft.setVisibility(View.INVISIBLE);
		btnPause=(Button)topHeader.findViewById(R.id.top_btn_right);
		btnPause.setText("暂停");
//		btnRight.setVisibility(View.INVISIBLE);
		TextView top_textView=(TextView)topHeader.findViewById(R.id.tv_toptitle);
		top_textView.setText("棋盘");
*/		
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
				case ChessBoardView.ACTION_MSG_EXIT:
					PlayActivity.this.finish();
					break;
				default:break;
				}
				
			}			
		};
				
		String playerType=getIntent().getStringExtra("PlayerType");
		if(playerType.equals("H2AI"))//人机对战
		{
			whitePlayer=new AIPlayer( "电脑", Player.ChessColor_White, Player.PlayerType_AI);
			blackPlayer=new HumanPlayer( "你", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		else if(playerType.equals("H2H"))//人人对战
		{
			whitePlayer=new HumanPlayer( "路人甲", Player.ChessColor_White, Player.PlayerType_Human);
			blackPlayer=new HumanPlayer( "路人乙", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		else //网络对战
		{
			whitePlayer=new InternetPlayer( "白方", Player.ChessColor_White, Player.PlayerType_Internet);
			blackPlayer=new HumanPlayer( "黑方", Player.ChessColor_Black, Player.PlayerType_Human);
		}
		cbv=new ChessBoardView(this,viewHolderHandler);
		cbv.setPlayer(whitePlayer);
		cbv.setPlayer(blackPlayer);
		setContentView(cbv);
		cbv.startHeartBeat();
	}
/*
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
*/
}
