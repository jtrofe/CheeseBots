package com.jtrofe.cheesebots.game.physics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.jtrofe.cheesebots.customviews.GameSurfaceView;
import com.jtrofe.cheesebots.game.controllers.BotController;
import com.jtrofe.cheesebots.game.controllers.ParticleController;
import com.jtrofe.cheesebots.game.gameobjects.Flail;
import com.jtrofe.cheesebots.game.gameobjects.GameObject;
import com.jtrofe.cheesebots.game.controllers.CheeseController;
import com.jtrofe.cheesebots.game.controllers.Controller;
import com.jtrofe.cheesebots.game.controllers.FlailController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MAIN on 1/20/16
 */
public class Engine{

    public boolean Initialized = false;
    public boolean LevelComplete = false;

    private List<GameObject> mBodies;
    private List<GameObject> mBodiesToAdd;
    private List<GameObject> mBodiesToRemove;

    private List<TouchPoint> mTouchPointsToRemove;

    private GameSurfaceView mGameSurfaceView;


    public void SetSurfaceView(GameSurfaceView gameSurfaceView){
        this.mGameSurfaceView = gameSurfaceView;
    }

    /**
     * Level Variables
     */
    public int MaxBots;
    public int MaxBotsOnScreen;
    public boolean HasTimeLimit;
    public int TimeLimit;
    public long StartTime;
    public long CurrentTime;

    private int mBotsDestroyed;

    public void AddBotDestroyed(){
        if(mBotsDestroyed < Integer.MAX_VALUE)
            mBotsDestroyed ++;
    }

    public int GetBotsDestroyed(){
        return mBotsDestroyed;
    }

    private int mWorldWidth;
    private int mWorldHeight;

    public int GetWorldWidth(){
        return mWorldWidth;
    }

    public int GetWorldHeight(){
        return mWorldHeight;
    }

    private List<Controller> mControllers;

    // For jitter effects
    public JitterControl mJitterControl;
    private Vec mOffset = new Vec();


    public void SetOffset(Vec offset){
        mOffset = offset;
    }


    /**
     * Create a basic world
     * @param worldWidth Screen width
     * @param worldHeight Screen height
     */
    public Engine(int worldWidth, int worldHeight, GameSurfaceView gameSurfaceView){
        this.mWorldWidth = worldWidth;
        this.mWorldHeight = worldHeight;
        this.mGameSurfaceView = gameSurfaceView;

        mBodies = new ArrayList<>();
        mBodiesToAdd = new ArrayList<>();
        mBodiesToRemove = new ArrayList<>();
        mControllers = new ArrayList<>();

        // Add controllers
        mControllers.add(new BotController(this));
        mControllers.add(new CheeseController(this));
        mControllers.add(new FlailController(this));
        mControllers.add(new ParticleController(this));

        mJitterControl = new JitterControl(this);

        debugBitmap = Bitmap.createBitmap(worldWidth, worldHeight,
                Bitmap.Config.ARGB_8888);
        debugCanvas = new Canvas(debugBitmap);

        mBotsDestroyed = 0;
    }

    public void AddBody(GameObject b){
        mBodiesToAdd.add(b);
    }

    public void RemoveBody(GameObject b){
        mBodiesToRemove.add(b);
    }

    private void addWaitingBodies(){
        mBodies.addAll(mBodiesToAdd);

        mBodiesToAdd = new ArrayList<>();
    }

    private void removeWaitingBodies(){
        mBodies.removeAll(mBodiesToRemove);

        mBodiesToRemove = new ArrayList<>();

        Touches.removeAll(mTouchPointsToRemove);
        mTouchPointsToRemove = new ArrayList<>();
    }

    public void SetWorldBounds(int worldWidth, int worldHeight){
        mWorldWidth = worldWidth;
        mWorldHeight = worldHeight;

        debugBitmap = Bitmap.createBitmap(worldWidth, worldHeight,
                Bitmap.Config.ARGB_8888);
        debugCanvas = new Canvas(debugBitmap);
    }

    private void resetForces(){
        for(GameObject b:mBodies){
            b.ClearForce();
        }
    }

    private void computeForces(){
        for(Controller c:mControllers){
            c.Update();
        }

        mJitterControl.Update();
    }

    private void updatePositions(float timeStep){
        for(GameObject b:mBodies){
            b.UpdateForceAndTorque(timeStep);
        }
    }

    public List<GameObject> GetType(int type){
        List<GameObject> objects = new ArrayList<>();

        for(GameObject o:mBodies){
            if(o.GetType() == type){
                objects.add(o);
            }
        }

        return objects;
    }

    /**
     * Run one iteration of the simulation
     * @param timeStep How many seconds to advance the simulation
     */
    public void Step(float timeStep){

        if(!Initialized || LevelComplete) return;

        resetForces();

        computeForces();

        updatePositions(timeStep);

        // Clean up by adding bodies waiting to be
        //    added and removing ones waiting to be
        //    removed
        addWaitingBodies();
        removeWaitingBodies();

        // Update the timer if there is one
        if(HasTimeLimit){
            CurrentTime = System.currentTimeMillis() - StartTime;
        }

        if(mBodies.size() > 0)
            LevelComplete = checkGameEndConditions();
    }

    private boolean checkGameEndConditions(){
        if(GetType(GameObject.TYPE_CHEESE).size() == 0){
            mGameSurfaceView.OnLevelEnd(false);
            return true;
        }

        if(HasTimeLimit){
            if(CurrentTime / 1000 >= TimeLimit){
                mGameSurfaceView.OnLevelEnd(true);
                return true;
            }
        }

        if(mBotsDestroyed >= MaxBots){
            mGameSurfaceView.OnLevelEnd(true);
            return true;
        }

        return false;
    }

    private Bitmap debugBitmap;
    public Canvas debugCanvas;

    public void Draw(Canvas canvas){

        if(mBodies.size() == 0) return;

        int canvasSave = canvas.save();
        if(!mGameSurfaceView.IsLandscape()){
            canvas.rotate(90, canvas.getWidth()/2, canvas.getWidth()/2);
        }

        canvas.translate(mOffset.x, mOffset.y);

        // TODO come up with a better background
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        canvas.drawRect(50, 50, mWorldWidth - 50, mWorldHeight - 50, p);


        for(GameObject b:mBodies){
            b.Draw(canvas);
        }

        // TODO debug stuff before release
        /*if(debugBitmap != null)
            canvas.drawBitmap(debugBitmap, mOffset.x, mOffset.y, null);
        debugBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
                Bitmap.Config.ARGB_8888);
        debugCanvas = new Canvas(debugBitmap);*/

        // Restore the orientation of the canvas
        canvas.restoreToCount(canvasSave);
    }


    public class JitterControl{

        private Engine mEngine;
        private int mCountdownStart = 0;
        private int mCountdown = 0;

        private float mMaxJitter = 10.0f;

        private Random rnd;

        public JitterControl(Engine engine){
            this.mEngine = engine;

            this.rnd = new Random();
        }

        public void StartJitter(int jitterTime){
            mCountdownStart = jitterTime;
            mCountdown = jitterTime;
            mMaxJitter = 10.0f;
        }


        public void StartJitter(int jitterTime, float maxJitter){
            mCountdownStart = jitterTime;
            mCountdown = jitterTime;
            mMaxJitter = maxJitter;
        }

        public void Update(){
            if(mCountdown > 0){
                float p = (mCountdown * 1.0f) / (mCountdownStart * 1.0f);

                float jitter_amount = p * mMaxJitter;

                float angle = ((float) Math.PI * 2) * rnd.nextFloat();

                float dx = (float) Math.sin(angle);
                float dy = (float) -Math.cos(angle);

                Vec offset = new Vec(dx * jitter_amount, dy * jitter_amount);

                mEngine.SetOffset(offset);

                mCountdown --;
            }else{
                mEngine.SetOffset(new Vec());
            }
        }
    }


    public List<TouchPoint> Touches = new ArrayList<>();
    public static class TouchPoint{
        public Vec Point;
        public int ID;
        public boolean InUse; // Is a flail using this point

        public TouchPoint(Vec point, int id){
            this.Point = point;
            this.ID = id;
            this.InUse = false;
        }
    }

    public TouchPoint GetTouchPointById(int id){
        for(TouchPoint t:Touches){
            if(t.ID == id){
                return t;
            }
        }
        return null;
    }

    public void RemoveTouchPoint(int id){
        for(TouchPoint t:Touches){
            if(t.ID == id){
                mTouchPointsToRemove.add(t);
                Touches.remove(t);
                return;
            }
        }
    }

    public void SetAvailableTouchPoint(Flail f){
        for(TouchPoint t:Touches){
            if(!t.InUse){
                f.TouchPointId = t.ID;
                t.InUse = true;
                return;
            }
        }
    }
}
