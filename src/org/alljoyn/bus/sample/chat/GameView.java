package org.alljoyn.bus.sample.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager.LayoutParams;


public class GameView extends Activity implements Observer{
	

	private ChatApplication mChatApplication = null;
	private ArrayList<String[]> mHistoryList= new ArrayList<String[]>();

    private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
    private static final int HANDLE_GAME_STARTED_EVENT = 0;

	int currentX,currentY;
	String user;
	String currentMessage[]=new String[6];
	int score;
	int lifeTime;
	SharedPreferences sharedPref;
	Intent scoreIntent;


	int localX, localY, remoteX, remoteY, localX2, localY2, remoteX2, remoteY2;
	float localXspd, localYspd;
	boolean ownership1, ownership2;
	
	GameView2 gm2;
	int mScrWidth, mScrHeight;
	int mRemoteScrWidth, mRemoteScrHeight;
	android.graphics.PointF mBallPos, mBallSpd;

	boolean  currentOwnership, hasOwnership=true;
	

    private static final String TAG = "chat.GameView";
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		
		
		
		
    	Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
		//set app to full screen and keep screen on 
		getWindow().setFlags(0xFFFFFFFF,LayoutParams.FLAG_FULLSCREEN|LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		gm2=new GameView2(this);
		setContentView(gm2);
		
		//get screen dimensions
		Display display = getWindowManager().getDefaultDisplay();  
		mRemoteScrWidth=mScrWidth = display.getWidth(); 
		mRemoteScrHeight=mScrHeight = display.getHeight();
		mBallPos = new android.graphics.PointF();
		mBallSpd = new android.graphics.PointF();
		        
		//create variables for ball position and speed
		localX=localX2=remoteX=remoteX2 = mScrWidth/2; 
		localY=localY2=remoteY=remoteY2 = mScrHeight/2; 
		mBallSpd.x = 0;
		mBallSpd.y = 0; 
		
		score=0;
		lifeTime=100;
		
		user="Me";
		currentMessage[0]=user;
		currentMessage[1]=mBallSpd.x+"";
		currentMessage[2]=mBallSpd.y+"";
		currentMessage[3]=mScrWidth+"";
		currentMessage[4]=mScrHeight+"";
		currentMessage[5]=1+"";
		
		
		ownership1=true;
		ownership2=false;
		
		
		
		
		
		
		
		
		//register sensor
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
		
        Log.i(TAG, "before getting chat app");
        //connection
        mChatApplication = (ChatApplication)getApplication();
        //mChatApplication.checkin();
        updateHistory();
        Log.i(TAG, "after getting chat app");
        mChatApplication.addObserver(this);
        Log.i(TAG, "after adding observer");
        
        scoreIntent = new Intent(GameView.this, ScoreView.class);
        
	}

	SensorManager sensorManager;
    SensorEventListener sensorListener = new SensorEventListener() {
     
        public void onSensorChanged(SensorEvent sensorEvent) {
        	
       //set ball speed based on phone tilt (ignore Z axis)
            mBallSpd.x = -sensorEvent.values[0];
            mBallSpd.y = sensorEvent.values[1];
  
         
            Thread t=Thread.currentThread();
			if(t.equals(this)){
				try {
					t.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
            
        }

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {}
      };
	
      private void sendMessage(){

			localXspd=mBallSpd.x;
			localYspd=mBallSpd.y;
			
			if(ownership1){
				
				localX += localXspd * 2;
				localY += localYspd * 2;
				int state=1;
				boolean justChanged=false;
				
				//if ball goes off from local screen, reposition to opposite side of screen
		        if (localX > mScrWidth){
		        	localX=5;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }
		        if (localY > mScrHeight){
		        	localY=5;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }
		        if (localX < 0){
		        	localX=mRemoteScrWidth-40;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }
		        if (localY < 0){
		        	localY=mRemoteScrHeight-40;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }	
		        String []message={localX+"",localY+"",mScrWidth+"",mScrHeight+"",state+""};
		        mChatApplication.newLocalUserMessage(message);
		        if(justChanged){
					lifeTime -= 5;
		        	mChatApplication.newLocalUserMessage(message);
		        }
			}
			if(ownership2)
			{
				
				localX2 += localXspd;
				localY2 += localYspd;
				int state=2;
				boolean justChanged=false;
				
				//if ball goes off from local screen, reposition to opposite side of screen
		        if (localX2 > mScrWidth){
		        	localX2=5;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }
		        if (localY2 > mScrHeight){
		        	localY2=5;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }
		        if (localX2 < 0){
		        	localX2=mRemoteScrWidth-40;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }
		        if (localY2 < 0){
		        	localY2=mRemoteScrHeight-40;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }

		        
		        String []message={localX2+"",localY2+"",mScrWidth+"",mScrHeight+"",state+""};
		        mChatApplication.newLocalUserMessage(message);
		        if(justChanged){
		        	mChatApplication.newLocalUserMessage(message);
		        }
			}
      }
      
      
	
class GameView2 extends SurfaceView implements Runnable{


	
	Thread thread = null;
	SurfaceHolder sHolder;
	boolean isRunning = false;

	public GameView2(Context context) {
		super(context);
		sHolder=getHolder();
	}

	
	@Override
	public void run() {
		
		
		
		//loading bitmaps
		Bitmap wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		wall=Bitmap.createBitmap(wall, 10, 10, 60, 60);

		Bitmap tile = BitmapFactory.decodeResource(getResources(), R.drawable.tile);
		tile=Bitmap.createBitmap(tile, 70, 70, 60, 60);
		
		Bitmap flag = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
		flag=Bitmap.createBitmap(flag);
		
		//creating the maze
		
		//no of cols and rows
		int cellW=60;
		int cellH=60;
		int cols=(mScrWidth/cellW)+2;
		int rows=(mScrHeight/cellH)+2;
		
		//matrix
		Bitmap bg[][]=new Bitmap[rows][cols];
		Point gridPos[][]=new Point[rows][cols];


		int type[][]=new int[rows][cols];
		
		//fill cell types of the grid
		Random r=new Random();

		
		for(int i=0;i<rows;i++){
			int y=i*cellH;
			for(int j=0;j<cols;j++){
				int x=j*cellW;
				int k=r.nextInt(20);
				Log.i(TAG,"Cell   "+i+"  "+j);
				if(k<=8){
					type[i][j]=0;
				}else{
					type[i][j]=1;
				}
				
				//set x and y of left corner
				gridPos[i][j]=new Point();
				gridPos[i][j].x=x;
				gridPos[i][j].y=y;
				
				//set default ball position black
				if((mScrWidth/2)-100< gridPos[i][j].x & (mScrWidth/2)+100 > gridPos[i][j].x+cellH){
					if((mScrHeight/2)-100< gridPos[i][j].y & (mScrHeight/2)+100 > gridPos[i][j].y+cellH){
						type[i][j]=1;
					}
				}
				if(i==0 | j==0 | i==rows-1 | j== cols-1 | i==rows-2 | j== cols-2 | i==rows-3 | j== cols-3){
					type[i][j]=1;
				}
				
			}
		}
		
		
		
		
		//setting the goal
		Rect goal=new Rect(r.nextInt(rows-3), r.nextInt(cols-3), r.nextInt(rows-3)+120, r.nextInt(rows-3)+120);
		Paint pGoal=new Paint();
		pGoal.setColor(Color.TRANSPARENT);
		
	
		ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
        ShapeDrawable mRemoteDrawable = new ShapeDrawable(new OvalShape());
        
        mDrawable.setBounds(localX, localY, localX+50, localY+50);
        mRemoteDrawable.setBounds(localX2,localY2, localX2+40, localY2+40);
        
        mDrawable.getPaint().setShadowLayer(5f, 10.0f, 10.0f, Color.GRAY);
        mRemoteDrawable.getPaint().setShadowLayer(5f, 10.0f, 10.0f, Color.GRAY);
        
        int lastBlackCell[]=new int[2];
        int lastBlackCell2[]=new int[2];
        boolean won = false;
        boolean lost  =false;
        int ballWidth=35;

		while(isRunning==true){
			
			
			while(!sHolder.getSurface().isValid()){
				continue;
			}
			
			
			Canvas canvas=sHolder.lockCanvas();
			//background
			canvas.drawARGB(255,10,10,10);
			
	
			
			synchronized (canvas) {
				//update current x and y
				sendMessage();
				if(lifeTime<=0)
				{
					lost=true;
				}
				
				boolean blocked=false;
				
				//if player got control for his ball
				if(ownership1){
					
					mDrawable.getPaint().setColor(Color.RED);
					
					
					int currentCellX=(localX/cellW);
					int currentCellY=(localY/cellH);
					int currentCellX2=((localX+ballWidth)/cellW);
					int currentCellY2=((localY+ballWidth)/cellH);
				
					
					if(localX+ballWidth>=goal.left & localX<=goal.right & localY+ballWidth>=goal.top & localY <= goal.bottom){
						won=true;
						score+=100;
					}else{
						//check for current position
						if((type[currentCellY][currentCellX]==1) &
								(type[currentCellY2][currentCellX2]==1) &
								(type[currentCellY][currentCellX2]==1) &
								(type[currentCellY2][currentCellX]==1) 
								){
							//if in a tile let it move
							lastBlackCell[0]=localX;
							lastBlackCell[1]=localY;
							mDrawable.setBounds(localX-2, localY-2, (localX-2)+ballWidth, (localY-2)+ballWidth);
						}else{
							localX=lastBlackCell[0];
							localY=lastBlackCell[1];
						}
						
					
					
					}	

				}else{
					mDrawable.getPaint().setColor(Color.TRANSPARENT);
				}
				
				if(ownership2){
					
					mRemoteDrawable.getPaint().setColor(Color.BLUE);
					
					if(localX+ballWidth>=goal.left & localX<=goal.right & localY+ballWidth>=goal.top & localY <= goal.bottom){
						won=true;						
					}else{
						

						int currentCellX=(localX2/cellW);
						int currentCellY=(localY2/cellH);
						int currentCellX2=((localX2+ballWidth)/cellW);
						int currentCellY2=((localY2+ballWidth)/cellH);
						
						if((type[currentCellY][currentCellX]==1) &
								(type[currentCellY2][currentCellX2]==1) &
								(type[currentCellY][currentCellX2]==1) &
								(type[currentCellY2][currentCellX]==1) 
								){
							lastBlackCell2[0]=localX2;
							lastBlackCell2[1]=localY2;
							mRemoteDrawable.setBounds(localX2-2, localY2-2,( localX2-2)+ballWidth, (localY2-2)+ballWidth);
							
						}else{
							localX2=lastBlackCell2[0];
							localY2=lastBlackCell2[1];
							
						}

				}
				}else{
					mRemoteDrawable.getPaint().setColor(Color.TRANSPARENT);
				}
				
	            
			
		}	
			
		//draw black blocks
		for(int i=0;i<rows;i++){//8
			for(int j=0;j<cols;j++){//5
				if(type[i][j]==1){
					canvas.drawBitmap(tile, gridPos[i][j].x,gridPos[i][j].y, null);
				}
			}
		}
		
		//draw circles
		 mRemoteDrawable.draw(canvas);
		 mDrawable.draw(canvas);

		 Paint pWall=new Paint();
		 pWall.setShadowLayer(5f, 5f, 5f, Color.GRAY);
	        
        //draw yellow blocks
        for(int i=0;i<rows;i++){//8
    			for(int j=0;j<cols;j++){//5
    				if(type[i][j]==0){
    					canvas.drawBitmap(wall, gridPos[i][j].x,gridPos[i][j].y, pWall);
    				}
    				
    			}
    	} 
        
        //canvas.drawRect(goal, pGoal);
        canvas.drawBitmap(flag, 0,0, new Paint());
        Paint p=new Paint();
		p.setColor(Color.MAGENTA);
		p.setTextSize(40);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText("Score : "+score, mScrWidth-40, mScrHeight-40, p);
        canvas.drawText("Life Time : "+lifeTime+"%", mScrWidth-10, mScrHeight-10, p);

        
        if(lost){
        	String []message={localX+"",localY+"",mScrWidth+"",mScrHeight+"",6+""};//lost message
		    mChatApplication.newLocalUserMessage(message);
        	
        	writeData("win", 0);
			startActivity(scoreIntent);
			finish();
			
			
        }
        
        
        if(won){
        	p.setTextAlign(Align.CENTER);
        	p.setTextSize(60);

			
			String []message={localX+"",localY+"",mScrWidth+"",mScrHeight+"",5+""};//won message
		    mChatApplication.newLocalUserMessage(message);
		    writeData("win",1);
		    writeData("Score",score);
		    
		    isRunning=false;
			startActivity(scoreIntent);
			finish();
			
		}
        
		sHolder.unlockCanvasAndPost(canvas);
			
			
		}
		
	}

	public void pause(){
		isRunning=false;
		while(true){
			try {
				thread.join();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			break;
		}
		thread=null;
	}
	

	public void resume(){
		isRunning=true;
		thread = new Thread(this);        
		thread.start();
	}


	private void close(){
		isRunning=false;
		
	}
	
	
	
}

@Override
protected void onResume() {
	super.onResume();
	gm2.resume();      	

}

@Override
protected void onPause() {
	super.onPause();
	gm2.pause();
}

@Override
protected void onStop() {
	super.onStop();
	
	
}


//listener for menu item clicked
@Override
public boolean onOptionsItemSelected(MenuItem item) 
{
  // Handle item selection    
  if (item.getTitle() == "Exit") //user clicked Exit
      finish(); //will call onPause
  return super.onOptionsItemSelected(item);    
}

@Override
public synchronized void update(Observable o, Object arg) {
    String qualifier = (String)arg;

    
    if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
        Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
        mHandler.sendMessage(message);
    }
   if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
        Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
        mHandler.sendMessage(message);
    }
}

private void updateHistory() {

	Log.i(TAG, "onupdateHistory()");
    mHistoryList.clear();
    List<String[]> messages = mChatApplication.getHistory();
    for (String message[] : messages) {
        mHistoryList.add(message);
    }
    readMessage();
}


private void readMessage(){
	int size=mHistoryList.size();
	if(size>0){

		currentMessage=(String[])mHistoryList.get(mHistoryList.size()-1);
		
		user=currentMessage[0];
		currentX=Integer.parseInt(currentMessage[1]);
		currentY=Integer.parseInt(currentMessage[2]);
		int srnW=Integer.parseInt(currentMessage[3]);
		int srnH=Integer.parseInt(currentMessage[4]);
		int state=Integer.parseInt(currentMessage[5]);

		
		if(user!="Me"){
			
			
			mRemoteScrWidth=srnW;
			mRemoteScrHeight=srnH;
			
			
			if(state==3){
				ownership2=true;
				score+=20;
				localX2=currentX;
				localY2=currentY;
				
	        }
			
			if(state==4){
				ownership1=true;
				localX=currentX;
				localY=currentY;
				
	        }
			if(state==5){
				writeData("win", 0);
				startActivity(scoreIntent);
				finish();
				
	        }
			if(state==6){
				writeData("win", 1);
				startActivity(scoreIntent);
				finish();
				
	        }
			
		}
			
			
			
	}
		
	
	
	
}
private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
private Handler mHandler = new Handler() {

	
    public void handleMessage(Message msg) {
        switch (msg.what) {
       
        case HANDLE_HISTORY_CHANGED_EVENT:
            {
                updateHistory();
                break;
            }
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



void writeData(String key, int value){

	if(key=="Score"){
	sharedPref = this.getSharedPreferences("myScore", Context.MODE_PRIVATE); //0 is the default value
	Editor editor = sharedPref.edit();
	editor.putInt(key, sharedPref.getInt(key, 0)+value);
	editor.commit();
	}else if (key=="win"){
		sharedPref = this.getSharedPreferences("myState", Context.MODE_PRIVATE); //0 is the default value
		Editor editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}else{
		
	}
}

}
