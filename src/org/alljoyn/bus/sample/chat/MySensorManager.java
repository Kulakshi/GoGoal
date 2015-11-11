package org.alljoyn.bus.sample.chat;






import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class MySensorManager 
{
 
	private static final String TAG = "chat.MySensorManager";
	final float[] mValuesMagnet      = new float[3];
    final float[] mValuesAccel       = new float[3];
    final float[] mValuesOrientation = new float[3];
    final float[] mRotationMatrix    = new float[9];
    Context context;
    
     MySensorManager(Context context)
    {
        this.context = context;
    	SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);        

    	Log.i(TAG, "in Sensor manager class");

        final SensorEventListener mEventListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                // Handle the events for which we registered
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        System.arraycopy(event.values, 0, mValuesAccel, 0, 3);
                        break;

                    case Sensor.TYPE_ORIENTATION:
                    	System.arraycopy(event.values, 0, mValuesOrientation, 0, 3);
                    	break;
                    	
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
                        break;
                }
            };
        };
        
        
     // You have set the event lisetner up, now just need to register this with the
        // sensor manager along with the sensor wanted.
        setListners(sensorManager, mEventListener);
        
    } 
     
     
     
  // Register the event listener and sensor type.
     public void setListners(SensorManager sensorManager, SensorEventListener mEventListener)
     {
         sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                 SensorManager.SENSOR_DELAY_NORMAL);
         sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
                 SensorManager.SENSOR_DELAY_NORMAL);
     }
     
     
     public String onCall()
     {
        	  SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
              SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);
              final CharSequence test;
              test = "results: " + mValuesOrientation[0] +" "+mValuesOrientation[1]+ " "+ mValuesOrientation[2];
              return test+"";
     }
     

    
}