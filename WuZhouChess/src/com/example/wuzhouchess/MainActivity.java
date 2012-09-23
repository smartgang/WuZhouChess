package com.example.wuzhouchess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button bottomBtn;
	//	ChessView cb;
	Button topBtn;
	Button btnInternetGame;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topBtn=(Button)findViewById(R.id.button1);
        bottomBtn=(Button)findViewById(R.id.button2);
        btnInternetGame=(Button)findViewById(R.id.button3);
        
        topBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,PlayActivity.class);
				intent.putExtra("PlayerType", "H2H");
				startActivity(intent); 
			}
        	
        });
        bottomBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,PlayActivity.class);
				intent.putExtra("PlayerType", "H2AI");
				startActivity(intent); 
			}
        	
        });
        btnInternetGame.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,InternetGameActivity.class);
				//internet vs internet
				intent.putExtra("PlayerType", "I2I");
				startActivity(intent); 				
			}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
