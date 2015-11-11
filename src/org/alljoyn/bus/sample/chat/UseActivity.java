/*
 * Copyright (c) 2011, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package org.alljoyn.bus.sample.chat;

import org.alljoyn.bus.sample.chat.ChatApplication;
import org.alljoyn.bus.sample.chat.Observable;
import org.alljoyn.bus.sample.chat.Observer;
import org.alljoyn.bus.sample.chat.DialogBuilder;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class UseActivity extends Activity implements Observer {
    private static final String TAG = "chat.UseActivity";
    
    ///
    //Context context=this;
    
    //graphics
    int x,y;
    CustomDrawableView mCustomDrawableView = null;
   

	FrameLayout useFrame;
	String currentX,currentY,user;
    
    
  //Sensor
    SensorManager sensorManager;
    SensorEventListener sensorListener = new SensorEventListener() {
     
        public void onSensorChanged(SensorEvent sensorEvent) {
          //pass the values to view for display
        	x = (int) Math.pow(sensorEvent.values[0], 2); 
            y = (int) Math.pow(sensorEvent.values[1], 2);
            
            
            String []message={x+"",y+""};
            mChatApplication.newLocalUserMessage(message);
            Log.i(TAG, "useMessage.onEditorAction(): got message " + message + ")");
            
        }

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
      };
    
    
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.use);
                
       
        ///////should change into the moving oval
       // mHistoryList = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        mHistoryList = new ArrayList<String[]>();
        //ListView hlv = (ListView) findViewById(R.id.useHistoryList);
       // hlv.setAdapter(mHistoryList);
        
        
        
        
      //graphics
       // mCustomDrawableView = new CustomDrawableView(this);
        
        useFrame=(FrameLayout) findViewById(R.id.useFrame);
        mCustomDrawableView=new CustomDrawableView(this);
        useFrame.addView(mCustomDrawableView);
        
        
      //sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL); 
        
       
        //text box where we write the message
        //when written and clicked msg is passed to the client
        /*        EditText messageBox = (EditText)findViewById(R.id.useMessage);
       messageBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                	//String message = view.getText().toString();
                	String message=" X "+x+"  Y "+y;
                    Log.i(TAG, "useMessage.onEditorAction(): got message " + message + ")");
                    //message passes to the interface         
    	            //mChatApplication.newLocalUserMessage(message+"my message here");
    	           
                    Log.i(TAG, "before calling sensor*****");
                    
                   // passing message x,y
                    mChatApplication.newLocalUserMessage(message);
                                    
    	            view.setText("");
                }
                return true;
            }
        });
   */             
        mJoinButton = (Button)findViewById(R.id.useJoin);
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_JOIN_ID);
        	}
        });

        mLeaveButton = (Button)findViewById(R.id.useLeave);
        mLeaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_LEAVE_ID);
            }
        });
        
        mChannelName = (TextView)findViewById(R.id.useChannelName);
        mChannelStatus = (TextView)findViewById(R.id.useChannelStatus);
        
        /*
         * Keep a pointer to the Android Application class around.  We use this
         * as the Model for our MVC-based application.    Whenever we are started
         * we need to "check in" with the application so it can ensure that our
         * required services are running.
         */
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.checkin();
        
        /*
         * Call down into the model to get its current state.  Since the model
         * outlives its Activities, this may actually be a lot of state and not
         * just empty.
         */
        updateChannelState();
        updateHistory();
        
        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        mChatApplication.addObserver(this);

    }

    
    public class CustomDrawableView extends View
    {
        static final int width = 10;
        static final int height = 10;
        ShapeDrawable mDrawable = new ShapeDrawable();
        ShapeDrawable mRemoteDrawable= new ShapeDrawable();

        public CustomDrawableView(Context context)
        {
            super(context);

            mDrawable = new ShapeDrawable(new OvalShape());
            mRemoteDrawable = new ShapeDrawable(new OvalShape());
            
           mDrawable.getPaint().setColor(Color.WHITE);
           mRemoteDrawable.getPaint().setColor(Color.MAGENTA);
            
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
            canvas.scale(4, 4);
            mDrawable.setBounds(x, y, x +width,y +height);
            mRemoteDrawable.setBounds(x+20, y+20, x+20 +width,y+20 +height);
            mDrawable.draw(canvas);
            mRemoteDrawable.draw(canvas);
            invalidate();
        	/*
        	
        	
            RectF oval = new RectF(x, y, x +width,y +height); // set bounds of rectangle
            RectF remoteOval= new RectF(x+10, y+10, x +width,y +height);
            
            RectF text=new RectF(maxX-10,maxY-10,maxX,maxY);
            
            Paint p = new Paint();
            Paint pRemote=new Paint();// set some paint options
            p.setColor(Color.BLUE);
            canvas.drawOval(oval, p);
            p.setColor(Color.RED);
            canvas.drawOval(remoteOval, p);
            
            
            canvas.drawText("X : "+ currentX+" Y : "+ currentY, maxX, maxY, p);
            invalidate();*/
            
        }
        
        
        
       
    }
    

    
    
    
	public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.deleteObserver(this);
    	super.onDestroy();
 	}
    
    public static final int DIALOG_JOIN_ID = 0;
    public static final int DIALOG_LEAVE_ID = 1;
    public static final int DIALOG_ALLJOYN_ERROR_ID = 2;

    protected Dialog onCreateDialog(int id) {
    	Log.i(TAG, "onCreateDialog()");
        Dialog result = null;
        switch(id) {
        case DIALOG_JOIN_ID:
	        { 
	        	DialogBuilder builder = new DialogBuilder();
	        	result = builder.createUseJoinDialog(this, mChatApplication);
	        }        	
        	break;
        case DIALOG_LEAVE_ID:
	        { 
	        	DialogBuilder builder = new DialogBuilder();
	        	result = builder.createUseLeaveDialog(this, mChatApplication);
	        }
	        break;
        case DIALOG_ALLJOYN_ERROR_ID:
	        { 
	        	DialogBuilder builder = new DialogBuilder();
	        	result = builder.createAllJoynErrorDialog(this, mChatApplication);
	        }
	        break;	        
        }
        return result;
    }
    
    public synchronized void update(Observable o, Object arg) {
        Log.i(TAG, "update(" + arg + ")");
        String qualifier = (String)arg;
        
        if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }
        
        if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }
        
        if (qualifier.equals(ChatApplication.USE_CHANNEL_STATE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }
        
        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }
    
    private void updateHistory() {
        Log.i(TAG, "updateHistory()");
	    mHistoryList.clear();
	    List<String[]> messages = mChatApplication.getHistory();
        for (String message[] : messages) {
            mHistoryList.add(message);
        }
	    //mHistoryList.notifyDataSetChanged();
    }
    
    
    //set x and y values from the data comming from the stream
    private void setCurrentXY(){
    	String currentMsg[]=(String[])mHistoryList.get(mHistoryList.size()-1);
    	/*StringTokenizer st=new StringTokenizer(currentMsg, "|");
    	
    	user=st.nextToken();
    	currentMsg=st.nextToken();
    	st=new StringTokenizer(currentMsg,"=");
    	st.nextToken();
    	currentX=currentMsg;
    	st.nextToken();
    	currentY=st.nextToken();
    	*/
    
    	
    	currentX=currentMsg[1];
    	currentY=currentMsg[2];
    	
    	
    }
    
    private void updateChannelState() {
        Log.i(TAG, "updateHistory()");
    	AllJoynService.UseChannelState channelState = mChatApplication.useGetChannelState();
    	String name = mChatApplication.useGetChannelName();
    	if (name == null) {
    		name = "Not set";
    	}
        mChannelName.setText(name);
        
        switch (channelState) {
        case IDLE:
            mChannelStatus.setText("Idle");
            mJoinButton.setEnabled(true);
            mLeaveButton.setEnabled(false);
            break;
        case JOINED:
            mChannelStatus.setText("Joined");
            mJoinButton.setEnabled(false);
            mLeaveButton.setEnabled(true);
            break;	
        }
    }
    

    /**
     * An AllJoyn error has happened.  Since this activity pops up first we
     * handle the general errors.  We also handle our own errors.
     */
    private void alljoynError() {
    	if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
    		mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
    		showDialog(DIALOG_ALLJOYN_ERROR_ID);
    	}
    }
    
    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
    private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 2;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_APPLICATION_QUIT_EVENT:
	            {
	                Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
	                finish();
	            }
	            break; 
            case HANDLE_HISTORY_CHANGED_EVENT:
                {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_HISTORY_CHANGED_EVENT");
                    updateHistory();
                    break;
                }
            case HANDLE_CHANNEL_STATE_CHANGED_EVENT:
	            {
	                Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
	                updateChannelState();
	                break;
	            }
            case HANDLE_ALLJOYN_ERROR_EVENT:
	            {
	                Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
	                alljoynError();
	                break;
	            }
            default:
                break;
            }
        }
    };
    
    private ChatApplication mChatApplication = null;
    
    //private ArrayAdapter<String> mHistoryList;
    private ArrayList mHistoryList;
    
    private Button mJoinButton;
    private Button mLeaveButton;
    
    private TextView mChannelName;
      
    private TextView mChannelStatus;

}
