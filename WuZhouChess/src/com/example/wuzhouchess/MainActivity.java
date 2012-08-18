package com.example.wuzhouchess;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	ChessView cb;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cb=(ChessView)findViewById(R.id.chess);
        DisplayMetrics dm=new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        cb.setChessBoard(dm);
        cb.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
