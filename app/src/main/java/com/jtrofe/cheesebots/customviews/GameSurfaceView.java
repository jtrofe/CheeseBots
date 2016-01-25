package com.jtrofe.cheesebots.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.game.UI;
import com.jtrofe.cheesebots.game.gameobjects.Bot;
import com.jtrofe.cheesebots.game.gameobjects.Cheese;
import com.jtrofe.cheesebots.game.gameobjects.Flail;
import com.jtrofe.cheesebots.game.physics.Engine;
import com.jtrofe.cheesebots.game.physics.Vec;

import java.util.Random;

/**
 *
 */
public class GameSurfaceView extends SurfaceView implements Runnable{

    private final static int MAX_FPS = 40; //desired fps
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    private SurfaceHolder holder;
    private boolean isRunning = false;
    private Thread gameThread;

    private int screenWidth;
    private int screenHeight;

    private Engine mEngine;
    private Bundle mSavedState;

    public UI UserInterface;

    public GameSurfaceView(Context context, Bundle savedInstanceState){
        super(context);

        System.out.println("____SurfaceView() created");
        if(savedInstanceState != null) {
            this.mSavedState = (Bundle) savedInstanceState.clone();
        }else{
            this.mSavedState = null;
        }

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder){

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                screenWidth = width;
                screenHeight = height;

                mEngine.SetWorldBounds(screenWidth, screenHeight);

                Initialize();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        UserInterface = new UI();

        int w = 1000;
        int h = 1500;

        mEngine = new Engine(w, h, this);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mEngine.Dragging = true;
            mEngine.TouchPoint = new Vec(event.getX(), event.getY());
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            mEngine.Dragging = false;
            mEngine.TouchPoint = null;
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            mEngine.TouchPoint = new Vec(event.getX(), event.getY());
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * On lifecycle resume
     */
    public void Resume(){
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * On lifecycle pause
     */
    public void Pause(){
        isRunning = false;
        boolean retry = true;
        while(retry){
            try{
                gameThread.join();
                retry = false;
            }catch (InterruptedException e){
                retry = true;
            }
        }
    }

    protected void render(Canvas canvas){
        canvas.drawColor(Color.BLACK);

        mEngine.Draw(canvas);
    }

    protected void step(){
        float TIME_STEP = 0.8f;

        mEngine.Step(TIME_STEP);
    }

    @Override
    public void run(){
        while(isRunning){
            if(!holder.getSurface().isValid()) continue;

            long started = System.currentTimeMillis();


            // Update
            step();

            // Draw
            Canvas canvas = holder.lockCanvas();
            if(canvas != null){
                render(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            float deltaTime = (System.currentTimeMillis() - started);
            int sleepTime = (int) (FRAME_PERIOD - deltaTime);
            if (sleepTime > 0) {
                try {
                    //gameThread.sleep(sleepTime);
                    Thread.sleep(sleepTime);
                }catch (InterruptedException e) {
                    // ok
                }
            }
            while (sleepTime < 0) {
                step();
                sleepTime += FRAME_PERIOD;
            }
        }
    }


    private void Initialize(){
        //System.out.println("Initializing.......\n" + "Has state? " + (mSavedState != null));
        //System.out.println("Engine started? " + mEngine.Initialized);
        System.out.println("____SurfaceView surfaceChanged() called");
        if(mSavedState != null){
            RestoreState(mSavedState);

            //this.mSavedState = null;
        }else if(!mEngine.Initialized){
            mEngine.Initialized = true;
            Bitmap ic;// = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

            ic = Bitmap.createBitmap(100, 60, Bitmap.Config.ARGB_8888);
            ic.eraseColor(Color.argb(200, 255, 0, 180));

            int NUM_BOTS = 5;
            int NUM_CHEESE = 3;

            Random rnd = new Random();

            for (int i = 0; i < NUM_BOTS; i++) {
                Bot obj = new Bot(Vec.Random(screenWidth, screenHeight), ic, 50, 0.08f);
                mEngine.AddBody(obj);
            }

            int cheeseMargin = getResources().getInteger(R.integer.cheese_margin);

            for (int i = 0; i < NUM_CHEESE; i++) {
                float radius = rnd.nextFloat() * 40 + 40;

                Vec pos = Vec.Random(screenWidth - cheeseMargin * 2, screenHeight - cheeseMargin * 2);

                pos.x += cheeseMargin;
                pos.y += cheeseMargin;


                Cheese cheese = new Cheese(pos, null, radius);
                mEngine.AddBody(cheese);
            }


            Flail flail = new Flail(Vec.Random(screenWidth, screenHeight), null, 20, 0.5f, 50);
            mEngine.AddBody(flail);
        }
    }



    public Bundle SaveState(Bundle savedInstanceState){

        savedInstanceState = mEngine.SaveState(savedInstanceState);

        return savedInstanceState;
    }

    public void RestoreState(Bundle savedInstanceState){

        mEngine.RestoreState(savedInstanceState);
    }
}
