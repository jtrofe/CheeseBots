package com.jtrofe.cheesebots.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.jtrofe.cheesebots.GameApplication;
import com.jtrofe.cheesebots.MainActivity;
import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.game.Levels.GameLevel;
import com.jtrofe.cheesebots.game.Levels.Level0;
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

    private boolean mIsLandscape;

    public boolean IsLandscape(){
        return mIsLandscape;
    }

    public Matrix PortraitMatrix;

    private int screenWidth;
    private int screenHeight;

    public int GetWidth(){
        return screenWidth;
    }
    public int GetHeight(){
        return screenHeight;
    }

    private Engine mEngine;

    public UI UserInterface;

    public GameSurfaceView(Context context, UI userInterface){
        super(context);

        this.UserInterface = userInterface;

        holder = getHolder();
        holder.addCallback(surfaceCallback);

        if(GameApplication.GameEngine == null) {
            mEngine = new Engine(1000, 1500, this);
            GameApplication.GameEngine = mEngine;
        }else{
            mEngine = GameApplication.GameEngine;
            mEngine.SetSurfaceView(this);
        }

        updateUI();
    }

    private void updateUI(){
        MainActivity.RunOnUI(new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) UserInterface.GetView("destroyedCounter");

                int botsKilled = mEngine.GetBotsDestroyed();
                if(mEngine.MaxBots < Integer.MAX_VALUE){
                    botsKilled = mEngine.MaxBots - botsKilled;
                }

                String destroyed = String.valueOf(botsKilled);

                v.setText(destroyed);

                if(mEngine.HasTimeLimit){
                    int seconds = mEngine.TimeLimit - (int) Math.floor(mEngine.CurrentTime / 1000);

                    if(seconds < 0) seconds = 0;

                    int minutes = (int) Math.floor(seconds / 60);
                    seconds -= minutes * 60;

                    String s = String.valueOf(seconds);
                    if(seconds < 10) s = "0" + s;

                    String m = String.valueOf(minutes);
                    if(minutes < 10) m = "0" + m;


                    TextView timerView = (TextView) UserInterface.GetView("timerView");
                    timerView.setText(m + ":" + s);
                }
            }
        });
    }

    private Vec translateCoordinates(float x, float y){
        if(IsLandscape()){
            return new Vec(x, y);
        }
        float dx = x - (screenHeight/2);
        float dy = y - (screenHeight/2);

        float nx = screenHeight/2 + dy;
        float ny = screenHeight/2 - dx;

        return new Vec(nx, ny);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event){
        Vec touchPoint;
        int pointerIndex = event.getActionIndex();
        int pointerId;

        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                touchPoint = translateCoordinates(event.getX(pointerIndex),
                                                  event.getY(pointerIndex));

                pointerId = event.getPointerId(pointerIndex);

                mEngine.Touches.add(new Engine.TouchPoint(touchPoint, pointerId));


                return true;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

                touchPoint = translateCoordinates(event.getX(pointerIndex), event.getY(pointerIndex));

                pointerId = event.getPointerId(pointerIndex);

                mEngine.Touches.add(new Engine.TouchPoint(touchPoint, pointerId));

                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                for(pointerIndex=0;pointerIndex<event.getPointerCount();pointerIndex++){
                    pointerId = event.getPointerId(pointerIndex);

                    Engine.TouchPoint t = mEngine.GetTouchPointById(pointerId);

                    touchPoint = translateCoordinates(event.getX(pointerIndex),
                                                      event.getY(pointerIndex));

                    t.Point = touchPoint.Clone();
                }

                return true;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                pointerId = event.getPointerId(pointerIndex);

                mEngine.RemoveTouchPoint(pointerId);

                return true;
            }
            case MotionEvent.ACTION_UP:{
                pointerId = event.getPointerId(pointerIndex);
                mEngine.RemoveTouchPoint(pointerId);
                return true;
            }

            default:
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

            updateUI();

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

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {}

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            screenWidth = Math.max(width, height);
            screenHeight = Math.min(width, height);

            mIsLandscape = (width > height);

            mEngine.SetWorldBounds(screenWidth, screenHeight);

            PortraitMatrix = new Matrix();
            PortraitMatrix.setRotate(90, screenHeight/2, screenHeight/2);

            if(!mEngine.Initialized){
                InitializeLevel();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}
    };

    private void InitializeLevel(){
        mEngine.Initialized = true;
        mEngine.LevelComplete = false;

        final GameLevel level = new Level0();

        if(!level.InitialMessage.isEmpty()){
            MainActivity.RunOnUI(new Runnable() {
                @Override
                public void run() {
                    final TextView v = (TextView) UserInterface.GetView("messageText");

                    Animation out = new AlphaAnimation(1.0f, 0.0f);
                    out.setDuration(4000);

                    out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            v.setText("");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    v.setText(level.InitialMessage);
                    v.startAnimation(out);
                }
            });
        }


        mEngine.MaxBots = level.MaxBots;
        mEngine.MaxBotsOnScreen = level.MaxBotsOnScreen;

        mEngine.HasTimeLimit = level.HasTimeLimit;
        mEngine.TimeLimit = level.TimeLimit;
        if(level.HasTimeLimit){
            mEngine.StartTime = System.currentTimeMillis();
            mEngine.CurrentTime = mEngine.StartTime;
        }


        Bitmap ic;// = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        ic = Bitmap.createBitmap(100, 60, Bitmap.Config.ARGB_8888);
        ic.eraseColor(Color.argb(200, 255, 0, 180));

        for(int i=0;i<level.MaxBotsOnScreen;i++){
            Bot obj = new Bot(Vec.Random(screenWidth, screenHeight), ic, 50, 0.02f);
            mEngine.AddBody(obj);
        }


        int cheeseMargin = getResources().getInteger(R.integer.cheese_margin);

        Random rnd = new Random();
        for(int i=0;i<level.CheeseCount;i++){
            float radius;
            Vec pos;
            if(level.HasRandomCheeseSizes){
                radius = rnd.nextFloat() * 40 + 40;
            }else{
                radius = level.CheeseSizes[i] * screenHeight;
            }
            if(level.HasRandomCheeseLocations){
                pos = Vec.Random(screenWidth - cheeseMargin * 2, screenHeight - cheeseMargin * 2);

                pos.x += cheeseMargin;
                pos.y += cheeseMargin;
            }else{
                Vec percents = level.CheesePositions[i];
                pos = new Vec(screenWidth * percents.x, screenHeight * percents.y);
            }

            Cheese cheese = new Cheese(pos, null, radius);
            mEngine.AddBody(cheese);
        }

        Flail flail = new Flail(Vec.Random(screenWidth, screenHeight), null, 20, 0.5f, 50);
        //flail.PlowThrough = true;
        mEngine.AddBody(flail);

        /*flail = new Flail(Vec.Random(screenWidth, screenHeight), null, 60, 0.3f, 40);
        flail.PlowThrough = true;
        mEngine.AddBody(flail);*/
    }

    public void OnLevelEnd(final boolean won){
        MainActivity.RunOnUI(new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) UserInterface.GetView("messageText");

                v.setVisibility(VISIBLE);
                if(won){
                    v.setText("Congratulations");
                }else{
                    v.setText("I'm so sorry...");
                }
            }
        });
    }
}
