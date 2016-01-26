package com.jtrofe.cheesebots.game.physics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

import com.jtrofe.cheesebots.MainActivity;
import com.jtrofe.cheesebots.customviews.GameSurfaceView;
import com.jtrofe.cheesebots.game.controllers.BotController;
import com.jtrofe.cheesebots.game.controllers.ParticleController;
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

    private List<GameObject> mBodies;
    private List<GameObject> mBodiesToAdd;
    private List<GameObject> mBodiesToRemove;

    private GameSurfaceView mGameSurfaceView;

    public void SetSurfaceView(GameSurfaceView gameSurfaceView){
        this.mGameSurfaceView = gameSurfaceView;
    }

    private int mBotsDestroyed;

    public void AddBotDestroyed(){
        mBotsDestroyed ++;

        mGameSurfaceView.UpdateUI();
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

    public boolean Dragging;
    public Vec TouchPoint;

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

        resetForces();

        computeForces();

        updatePositions(timeStep);

        // Clean up by adding bodies waiting to be
        //    added and removing ones waiting to be
        //    removed
        addWaitingBodies();
        removeWaitingBodies();
    }

    private Bitmap debugBitmap;
    public Canvas debugCanvas;

    public void Draw(Canvas canvas){

        Bitmap objectBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas objectCanvas = new Canvas(objectBitmap);

        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        objectCanvas.drawRect(50, 50, mWorldWidth - 50, mWorldHeight - 50, p);

        for(GameObject b:mBodies){
            b.Draw(objectCanvas);
        }

        canvas.drawBitmap(objectBitmap, mOffset.x, mOffset.y, null);

        if(debugBitmap != null)
            canvas.drawBitmap(debugBitmap, mOffset.x, mOffset.y, null);


        debugBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
                Bitmap.Config.ARGB_8888);
        debugCanvas = new Canvas(debugBitmap);
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
}
