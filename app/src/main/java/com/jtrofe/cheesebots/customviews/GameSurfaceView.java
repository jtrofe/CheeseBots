package com.jtrofe.cheesebots.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

        UpdateUI();
    }

    public void UpdateUI(){
        MainActivity.RunOnUI(new Runnable() {
            @Override
            public void run() {
                TextView v = (TextView) UserInterface.GetView("destroyedCounter");
                v.setText(String.valueOf(mEngine.GetBotsDestroyed()));
            }
        });
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event){

        int index = event.getActionIndex();

        Paint p = new Paint();
        p.setColor(Color.RED);

        float x, y;
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN: {
                x = event.getX(index);
                y = event.getY(index);

                int id = event.getPointerId(index);

                mEngine.Touches.add(new Engine.TouchPoint(new Vec(x, y), id));


                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                int id = event.getPointerId(pointerIndex);

                mEngine.Touches.add(new Engine.TouchPoint(new Vec(x, y), id));


                break;
            }
            case MotionEvent.ACTION_MOVE:
                for(int i=0;i<event.getPointerCount();i++){
                    int pointerId = event.getPointerId(i);

                    Engine.TouchPoint t = mEngine.GetTouchPointById(pointerId);

                    t.Point.x = event.getX(i);
                    t.Point.y = event.getY(i);

                }

                break;
            case MotionEvent.ACTION_POINTER_UP:{
                int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndex);

                mEngine.RemoveTouchPoint(pointerId);


                break;}
            case MotionEvent.ACTION_UP:
                int pointerId = event.getPointerId(index);
                mEngine.RemoveTouchPoint(pointerId);
                //mEngine.Touches = new ArrayList<>();
                break;

            default:
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mEngine.Dragging = true;
            mEngine.DragPoint = new Vec(event.getX(), event.getY());
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            mEngine.Dragging = false;
            mEngine.DragPoint = null;
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            mEngine.DragPoint = new Vec(event.getX(), event.getY());
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

        GameLevel level = new Level0();

        mEngine.MaxBots = level.MaxBots;
        mEngine.MaxBotsOnScreen = level.MaxBotsOnScreen;

        mEngine.HasTimeLimit = level.HasTimeLimit;
        mEngine.TimeLimit = level.TimeLimit;


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
        flail.PlowThrough = true;
        mEngine.AddBody(flail);

        /*flail = new Flail(Vec.Random(screenWidth, screenHeight), null, 60, 0.3f, 40);
        flail.PlowThrough = true;
        mEngine.AddBody(flail);*/
    }
}
