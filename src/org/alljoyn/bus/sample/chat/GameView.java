package org.alljoyn.bus.sample.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.database.MergeCursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
				
				localX += localXspd;
				localY += localYspd;
				int state=1;
				boolean justChanged=false;
				
				//if ball goes off from local screen, reposition to opposite side of screen
		        if (localX > mScrWidth){
		        	localX=0;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }
		        if (localY > mScrHeight){
		        	localY=0;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }
		        if (localX < 0){
		        	localX=mRemoteScrWidth;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }
		        if (localY < 0){
		        	localY=mRemoteScrHeight;
		        	ownership1= false;
		        	justChanged=true;
		        	state=3;
		        }	
		        String []message={localX+"",localY+"",mScrWidth+"",mScrHeight+"",state+""};
		        mChatApplication.newLocalUserMessage(message);
		        if(justChanged){
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
		        	localX2=0;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }
		        if (localY2 > mScrHeight){
		        	localY2=0;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }
		        if (localX2 < 0){
		        	localX2=mRemoteScrWidth;
		        	ownership2= false;
		        	justChanged=true;
		        	state=4;
		        }
		        if (localY2 < 0){
		        	localY2=mRemoteScrHeight;
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
	boolean isItOkay = false;

	public GameView2(Context context) {
		super(context);
		sHolder=getHolder();
	}

	
	@Override
	public void run() {
		
		//creating the maze
		
		//no of cols and rows
		int cellW=60;
		int cellH=60;
		int cols=(mScrWidth/cellW)+2;
		int rows=(mScrHeight/cellH)+2;
		
		//matrix
		Rect grid[][]=new Rect[rows][cols];
		int type[][]=new int[rows][cols];
		Paint pCell[][]=new Paint[rows][cols];
		
		//fill cell types of the grid
		Random r=new Random();

		
		for(int i=0;i<rows;i++){
			int y=i*cellH;
			for(int j=0;j<cols;j++){
				type[i][j]=r.nextInt(20);
				Log.i(TAG,"Cell   "+i+"  "+j);
				pCell[i][j]=new Paint();
				if(type[i][j]<=8){
					pCell[i][j].setColor(Color.YELLOW);
				}else{
					pCell[i][j].setColor(Color.BLACK);
				}
				
				//set x and y of left corner
				int x=j*cellW;
				grid[i][j]=new Rect(x,y,x+cellW,y+cellH);
				
				//set default ball position black
				if((mScrWidth/2)-200< grid[i][j].left & (mScrWidth/2)+200 > grid[i][j].right){
					if((mScrHeight/2)-200< grid[i][j].top & (mScrHeight/2)+200 > grid[i][j].bottom){
						pCell[i][j].setColor(Color.BLACK);
					}
				}
				if(i==0 | j==0 | i==rows-1 | j== cols-1){
					pCell[i][j].setColor(Color.BLACK);
				}
				
			}
		}
		
		
		//setting the goal
		Rect goal=new Rect(r.nextInt(rows-3), r.nextInt(cols-3), r.nextInt(rows-3)+120, r.nextInt(rows-3)+120);
		Paint pGoal=new Paint();
		pGoal.setColor(Color.GREEN);
		
	
		ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
        ShapeDrawable mRemoteDrawable = new ShapeDrawable(new OvalShape());
        
        mDrawable.setBounds(localX, localY, localX+50, localY+50);
        mRemoteDrawable.setBounds(localX2,localY2, localX2+40, localY2+40);
        
        int lastBlackCell[]=new int[2];
        int lastBlackCell2[]=new int[2];
        boolean won=false;

		while(isItOkay==true){
			
			
			while(!sHolder.getSurface().isValid()){
				continue;
			}
			
			
			Canvas canvas=sHolder.lockCanvas();
			//background
			canvas.drawARGB(255,10,10,10);
			
	
			
			synchronized (canvas) {
				//update current x and y
				sendMessage();
				
				
				boolean blocked=false;
				
			
				if(ownership1){
					
					mDrawable.getPaint().setColor(Color.RED);
					
					
					int currentCellX=(localX/cellW);
					int currentCellY=(localY/cellH);
					int currentCellX2=((localX+50)/cellW);
					int currentCellY2=((localY+50)/cellH);
				
					
					
					//Log.i(TAG, "####CCCC"+pCell[currentCellY][currentCellX].getColor()+"  "+currentCellX2+"  "+currentCellY2);	
					
					
					if(localX+50>=goal.left & localX<=goal.right & localY+50>=goal.top & localY <= goal.bottom){
						won=true;						
					}else{
						
					
					
					
						if((pCell[currentCellY][currentCellX].getColor()==Color.BLACK) &
								(pCell[currentCellY2][currentCellX2].getColor()==Color.BLACK) &
								(pCell[currentCellY][currentCellX2].getColor()==Color.BLACK) &
								(pCell[currentCellY2][currentCellX].getColor()==Color.BLACK) 
								){
							lastBlackCell[0]=localX;
							lastBlackCell[1]=localY;
							mDrawable.setBounds(localX, localY, localX+50, localY+50);
							Log.i(TAG, "In a black cell");
						}else{
							localX=lastBlackCell[0];
							localY=lastBlackCell[1];
							Log.i(TAG, "In a yellow cell");
						}
						
					
					
					}	
					
					//mDrawable.setBounds(currentCellX*60, currentCellY*60, (currentCellX*60)+50, (currentCellY*60)+50);
					Log.i(TAG, gm2+"STATE 1 - BIG RED");
				}
				
				if(ownership2){
					
					if(localX+50>=goal.left & localX<=goal.right & localY+50>=goal.top & localY <= goal.bottom){
						won=true;						
					}else{
						

						int currentCellX=(localX2/cellW);
						int currentCellY=(localY2/cellH);
						int currentCellX2=((localX2+40)/cellW);
						int currentCellY2=((localY2+40)/cellH);
						
						if((pCell[currentCellY][currentCellX].getColor()==Color.BLACK) &
								(pCell[currentCellY2][currentCellX2].getColor()==Color.BLACK) &
								(pCell[currentCellY][currentCellX2].getColor()==Color.BLACK) &
								(pCell[currentCellY2][currentCellX].getColor()==Color.BLACK) 
								){
							lastBlackCell2[0]=localX2;
							lastBlackCell2[1]=localY2;
							mRemoteDrawable.setBounds(localX2, localY2, localX2+50, localY2+50);
							
						}else{
							localX2=lastBlackCell2[0];
							localY2=lastBlackCell2[1];
							
						}
					
					//mRemoteDrawable.getPaint().setColor(Color.BLUE);
					//mRemoteDrawable.setBounds(localX2,localY2, localX2+40, localY2+40);
					Log.i(TAG, "STATE 2 - SMALL BLUE");
				}
				}
				
	            
			
		}	
			
		//draw black blocks
		for(int i=0;i<rows;i++){//8
			for(int j=0;j<cols;j++){//5
				if(pCell[i][j].getColor()==Color.BLACK){
					canvas.drawRect(grid[i][j], pCell[i][j]);
				}
				
				//Log.i(TAG, "INFO "+i+"  "+j+"  "+pCell[i][j].getColor());
			}
		}
		
		//draw circles
		 mRemoteDrawable.draw(canvas);
		 mDrawable.draw(canvas);

        //draw yellow blocks
        for(int i=0;i<rows;i++){//8
    			for(int j=0;j<cols;j++){//5
    				if(pCell[i][j].getColor()==Color.YELLOW){
    					canvas.drawRect(grid[i][j], pCell[i][j]);
    				}
    				
    				//Log.i(TAG, "INFO "+i+"  "+j+"  "+pCell[i][j].getColor());
    			}
    	} 
        
        canvas.drawRect(goal, pGoal);	
      //if it's a black one color it
	/*	if(pCell[currentCellX][currentCellY].getColor()==Color.BLACK){
			pCell[currentCellX][currentCellY].setColor(Color.MAGENTA);
			canvas.drawRect(grid[currentCellX][currentCellY], pCell[currentCellX][currentCellY]);
		}*/
        if(won){
			Paint p=new Paint();
			p.setColor(Color.MAGENTA);
			p.setTextSize(60);
			canvas.drawText("YOU WON!!!", 40, 40, p);
			Log.i(TAG, "####CCCC");	
			
		}
		sHolder.unlockCanvasAndPost(canvas);
			
			
		}
		
	}

	public void pause(){
		isItOkay=false;
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
		isItOkay=true;
		thread = new Thread(this);        
		thread.start();
	}


	private void close(){
		isItOkay=false;
		
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
  /*  if (qualifier.equals(ChatApplication.GAME_STARTED_EVENT)) {
        Message message = mHandler.obtainMessage(HANDLE_GAME_STARTED_EVENT);
        mHandler.sendMessage(message);
    }*/
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
				localX2=currentX;
				localY2=currentY;
				
	        }
			
			if(state==4){
				ownership1=true;
				localX=currentX;
				localY=currentY;
				
		        }
		}
			
			
			
	}
		
	
	
	
}

private Handler mHandler = new Handler() {

	
    public void handleMessage(Message msg) {
        switch (msg.what) {
       
        case HANDLE_HISTORY_CHANGED_EVENT:
            {
                updateHistory();
                break;
            }
        case HANDLE_GAME_STARTED_EVENT:
        {
            //updateHistory();
        	//gameInit()
            break;
        }
        default:
            break;
        }
    }
};

}




/*
 * 					for(int i=0;i<rows;i++){
						for(int j=0;j<cols;j++){
							
							if(pCell[i][j].getColor()==Color.BLACK){
								if(localX<=grid[i][j].left || localX+50>=grid[i][j].right || localY<=grid[i][j].top || localY+50 >= grid[i][j].bottom){
									
								}
							}
							
						}
					}
					
				////////////////////////////////////////////////////////////////	
					
				
				currentCellX=(localX/cellW);
				currentCellY=(localY/cellH);
					
					if(pCell[currentCellX][currentCellY].getColor()==Color.BLACK){
						lastBlackCell[0]=localX;
						lastBlackCell[1]=localY;
						mDrawable.setBounds(localX, localY, localX+50, localY+50);
						
					}else{
						localX=lastBlackCell[0];
						localY=lastBlackCell[1];
					}
					
					
					
			////////////////////////////////////////////////		
					
					for(int i=0;i<rows;i++){
						for(int j=0;j<cols;j++){
							
							if(pCell[i][j].getColor()==Color.YELLOW){
								if(localX+50>=grid[i][j].left || localX<=grid[i][j].right || localY+50>=grid[i][j].top || localY <= grid[i][j].bottom){
									localX=lastBlackCell[0];
									localY=lastBlackCell[1];
									mDrawable.setBounds(localX, localY, localX+50, localY+50);
									blocked=true;
									break;
								}
							}
							
						}
					}

					if(!blocked){
						lastBlackCell[0]=localX;
						lastBlackCell[1]=localY;
						mDrawable.setBounds(localX, localY, localX+50, localY+50);
					}
					
					
					
					
					
					
					
 */














