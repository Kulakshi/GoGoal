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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class SensorActivity extends Activity implements SensorEventListener
{
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

    public class CustomDrawableView extends View
    {
        static final int width = 50;
        static final int height = 50;

        public CustomDrawableView(Context context)
        {
            super(context);

            mDrawable = new ShapeDrawable(new OvalShape());
            mDrawable.getPaint().setColor(0xff74AC23);
            mDrawable.setBounds(0, 0, x + width, y + height);
        }

        protected void onDraw(Canvas canvas)
        {
        	canvas.scale(2, 4);
            RectF oval = new RectF(SensorActivity.x, SensorActivity.y, SensorActivity.x +width,SensorActivity.y +height); // set bounds of rectangle
            Paint p = new Paint(); // set some paint options
            p.setColor(Color.BLUE);
            canvas.drawOval(oval, p);
            invalidate();
        }
        
        
       
    }
}