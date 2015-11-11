package org.alljoyn.bus.sample.chat;

import android.R.dimen;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class SensorActivity extends Activity implements SensorEventListener
{
	
	private static final String TAG = "chat.Sensor Manager";
    /** Called when the activity is first created. */
    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public static int x;
    public static int y;
 	int widthx;
	int heightx;
    //public static int z;
    private SensorManager sensorManager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);
        // setContentView(R.layout.main);
        DisplayMetrics metrics = this.getBaseContext().getResources().getDisplayMetrics();
    	widthx = metrics.widthPixels;
    	heightx = metrics.heightPixels;
    	

    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // the values you were calculating originally here were over 10000!
                x = (int) Math.pow(sensorEvent.values[0], 2); 
                Log.i(TAG, "XXXXXXXXXXXXXXXXXXXXXXXX"+x);
                y = (int) Math.pow(sensorEvent.values[1], 2);
                //z = (int) Math.pow(sensorEvent.values[3], 2);

            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            }
        }
    }

    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        // ...and the orientation sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop()
    {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }
    
    
    /*
     * updates x and y when object goes into corners
     */
    public void setCoordinates(){
    	Display mdisp = getWindowManager().getDefaultDisplay();
        int maxX= mdisp.getWidth()/25; //a near location for max X for my device 
        int maxY= mdisp.getHeight()/10;
        
        /*
         * when the object gets to corners of the screen display it from opposite side
         */
        if(x>=maxX){
        	x=x-maxX;
        }
        if(y>=maxY){
        	y=y-maxY;
        }
    	
    }
    
    

    public class CustomDrawableView extends View
    {
        static final int width = 10;
        static final int height = 10;

        public CustomDrawableView(Context context)
        {
            super(context);

            mDrawable = new ShapeDrawable(new OvalShape());
            mDrawable.getPaint().setColor(0xff74AC23);
            mDrawable.setBounds(0, 0, x + width, y + height);
        }
        
      

        protected void onDraw(Canvas canvas)
        {
        	
        	
        	//get width and height of the screen
        	Display mdisp = getWindowManager().getDefaultDisplay();
            int maxX= mdisp.getWidth()/25; //a near location for max X for my device 
            int maxY= mdisp.getHeight()/10;
            
            /*
             * when the object gets to corners of the screen display it from opposite side
             */
            if(x>=maxX){
            	x=x-maxX;
            }
            if(y>=maxY){
            	y=y-maxY;
            }
           
            
        	canvas.scale(6, 8);
            RectF oval = new RectF(x, y, x +width,y +height); // set bounds of rectangle
            RectF text=new RectF(maxX-10,maxY-10,maxX,maxY);
            Paint p = new Paint(); // set some paint options
            p.setColor(Color.BLUE);
            canvas.drawOval(oval, p);
            canvas.drawText("X : "+ x +" Y : "+ y, maxX, maxY, p);
            invalidate();
            
        }
        
        
        
       
    }
}