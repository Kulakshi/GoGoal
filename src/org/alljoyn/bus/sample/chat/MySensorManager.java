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

	
	private static final String TAG = "chat.Sensor Manager";
	
    
    public static int x;
    public static int y;	
    private SensorManager sensorManager = null;

   public MySensorManager(Context context) {

    	// Get a reference to a SensorManager
    	SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE); 
    	//	
	}
    
     
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	 public void onSensorChanged(SensorEvent sensorEvent)
    {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // the values you were calculating originally here were over 10000!
                x = (int) Math.pow(sensorEvent.values[0], 2); 
                y = (int) Math.pow(sensorEvent.values[1], 2);
                //z = (int) Math.pow(sensorEvent.values[3], 2);

            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            }
        }
    }
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		UseActivity ua=new UseActivity();
		ua.x=x;
		ua.y=y;
		
		try {
			sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	


 
	
	
	
	
}