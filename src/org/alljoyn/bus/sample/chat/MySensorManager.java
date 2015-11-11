package org.alljoyn.bus.sample.chat;






//import org.alljoyn.bus.sample.chat.SensorActivity.CustomDrawableView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;


public class MySensorManager extends Thread implements SensorEventListener
{
	int currentX,currentY;
	String user;
	String currentMessage[];
	
	int x,y,z;
	int mScrWidth, mScrHeight;
	android.graphics.PointF mBallPos, mBallSpd;
	
	private ChatApplication mChatApplication = null;
	
	
	
	public MySensorManager() {
		mChatApplication=new ChatApplication();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {

	 	
	       //set ball speed based on phone tilt (ignore Z axis)
	            mBallSpd.x = -sensorEvent.values[0];
	            mBallSpd.y = sensorEvent.values[1];
	            
	            x +=mBallSpd.x*10;
	            y +=mBallSpd.y*10;
	            
	            String []message={x+"",y+""};
	            mChatApplication.newLocalUserMessage(message);
	           
		
	}

	
 
	
	
	
	
}