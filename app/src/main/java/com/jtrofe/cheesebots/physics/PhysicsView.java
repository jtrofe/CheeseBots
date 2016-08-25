package com.jtrofe.cheesebots.physics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.game.Game;

import java.util.List;

/**
 * Created by MAIN on 3/11/16
 */
public class PhysicsView extends SurfaceView implements Runnable{

    private Boolean mMainPage;
    public Boolean IsMainPage(){ return mMainPage; }

    private static final int MAX_FPS = 40;
    private static final int FRAME_PERIOD = 1000 / MAX_FPS;

    private SurfaceHolder mHolder;
    private Boolean mIsRunning = false;
    private Thread mGameThread;

    private Boolean mIsLandscape = false;
    public Boolean IsLandscape(){
        return mIsLandscape;
    }

    private Vec mScreenSize = new Vec(100, 100);

    private Game mGame;

    public void SetSpriteSheets(List<Bitmap> spriteSheets){
        mGame.SpriteSheets = spriteSheets;
        mGame.SpritesLoaded = true;
    }

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {}
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            int screenWidth = Math.max(width, height);
            int screenHeight = Math.min(width, height);

            mScreenSize = new Vec(screenWidth, screenHeight);

            mIsLandscape = (width > height);

            mGame.SetWorldSize(width, height);

            if(!mGame.IsInitialized()){
                mGame.Initialize();
            }
        }
    };

    public PhysicsView(Context context){
        super(context);

        mMainPage = false;

        mHolder = getHolder();
        mHolder.addCallback(surfaceCallback);

        if(GameApp.CurrentGame == null){
            mGame = new Game(this);
            GameApp.CurrentGame = mGame;
        }else{
            mGame = GameApp.CurrentGame;
            mGame.SetPhysicsView(this);
        }
    }

    public PhysicsView(Context context, Boolean mainPage){
        super(context);

        mMainPage = mainPage;

        mHolder = getHolder();
        mHolder.addCallback(surfaceCallback);

        if(GameApp.CurrentGame == null){
            mGame = new Game(this);
            GameApp.CurrentGame = mGame;
        }else{
            mGame = GameApp.CurrentGame;
            mGame.SetPhysicsView(this);
        }
    }


    /**
     * When the phone orientation is landscape, points
     * on the phone screen will have to be rotated to
     * be accurate
     */
    private Vec translateCoordinates(Vec point){
        if(mIsLandscape){
            return point.copy();
        }

        double dx = point.x - (mScreenSize.y / 2);
        double dy = point.y - (mScreenSize.y / 2);

        double nx = (mScreenSize.y / 2) + dy;
        double ny = (mScreenSize.y / 2) - dx;

        return new Vec(nx, ny);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Vec touchPoint = new Vec(event.getX(), event.getY());
        touchPoint = translateCoordinates(touchPoint);

        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mGame.TouchPoint = touchPoint;
                mGame.GetEngine().TouchPoint = touchPoint;
                return true;
            case MotionEvent.ACTION_UP:
                mGame.TouchPoint = new Vec(-1, -1);
                return true;
            case MotionEvent.ACTION_MOVE:
                mGame.TouchPoint = touchPoint;
                mGame.GetEngine().TouchPoint = touchPoint;
                return true;

        }

        return super.onTouchEvent(event);
    }

    /**
     * On lifecycle resume
     */
    public void Resume(){
        mIsRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    /**
     * On lifecycle pause
     */
    public void Pause(){
        mIsRunning = false;
        boolean retry = true;

        while(retry){
            try{
                mGameThread.join();
                retry = false;
            }catch(InterruptedException e){
                retry = true;
            }
        }
    }

    /**
     * Run a step of the physics engine if it's initialized
     * and the game isn't over
     */
    private void stepEngine(){
        double TIME_STEP = 0.8;

        mGame.Update(TIME_STEP);
    }

    /**
     * Game loop. Handle frame rate and game updating
     */
    @Override
    public void run(){
        while(mIsRunning){
            if(!mHolder.getSurface().isValid()) continue;

            long started = System.currentTimeMillis();

            // Step the engine, draw the objects, and update the UI
            update();

            // Frames per second stuff
            float deltaTime = (float) (System.currentTimeMillis() - started);
            int sleepTime = (int) (FRAME_PERIOD - deltaTime);
            if(sleepTime > 0) {
                try {
                    Thread.sleep((long) sleepTime);
                }catch(InterruptedException e) {
                    //ok
                }
            }

            while(sleepTime < 0) {
                stepEngine();
                sleepTime += FRAME_PERIOD;
            }
        }
    }

    /**
     * Game frame update. Updates physics, canvas, and UI
     */
    private void update(){
        // Run physics simulation
        stepEngine();


        // Draw objects
        Canvas canvas = mHolder.lockCanvas();
        if(canvas != null){
            canvas.drawColor(Color.BLACK);

            mGame.Draw(canvas);

            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
