package org.alljoyn.bus.sample.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreView extends Activity implements Observer{
	
	ChatApplication mChatApplication;
	
	int highScore;
	SharedPreferences sharedPref;
	Intent scoreView;
	Intent gameView;
	Intent menuView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);
		

		mChatApplication=(ChatApplication) getApplication();
		mChatApplication.addObserver(this);
		
		TextView winView = (TextView)findViewById(R.id.win);
		
		
		//storage
				sharedPref = this.getSharedPreferences("myState", Context.MODE_PRIVATE);
				if(sharedPref.getInt("win",1)==0){
					winView.setText("Try again..");
				}else{
					winView.setText("Congrates!!");
				}
				
				sharedPref = this.getSharedPreferences("myScore", Context.MODE_PRIVATE);
				highScore = sharedPref.getInt("Score", 0); //0 is the default value
				
				TextView scoreView = (TextView)findViewById(R.id.score);
				scoreView.setText("High Score : "+highScore);
				
				Button playButton=(Button) findViewById(R.id.play);
				playButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(ScoreView.this, GameView.class);
						startActivity(myIntent); 						
						finish();
					}
				});
				
				Button menuButton=(Button) findViewById(R.id.mainMenu);
				menuButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(ScoreView.this, TabWidget.class);
						startActivity(myIntent); 						
						finish();
					}
				});
				
				
				Button exitButton=(Button) findViewById(R.id.exit);
				exitButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						mChatApplication.quit();

						
					}
				});
				
		
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
	    String qualifier = (String)arg;

	   if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
	        Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
	        mHandler.sendMessage(message);
	    }
	}
	
	private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
	private Handler mHandler = new Handler() {

		
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	  
	        case HANDLE_APPLICATION_QUIT_EVENT:
	        {
	         
	        	finish();
	            break;
	        }
	        default:
	            break;
	        }
	    }
	};
}
