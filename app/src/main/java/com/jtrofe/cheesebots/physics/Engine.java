package com.jtrofe.cheesebots.physics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.game.Game;
import com.jtrofe.cheesebots.physics.controllers.BotController;
import com.jtrofe.cheesebots.physics.controllers.CheeseController;
import com.jtrofe.cheesebots.physics.controllers.Controller;
import com.jtrofe.cheesebots.physics.controllers.FlailController;
import com.jtrofe.cheesebots.physics.controllers.ParticleController;
import com.jtrofe.cheesebots.physics.objects.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MAIN on 3/12/16
 */
public class Engine {

    private Vec mWorldSize = new Vec(100, 100);

    public Vec GetWorldSize(){ return mWorldSize.copy(); }
    public void SetWorldSize(Vec worldSize){
        mWorldSize = worldSize.copy();
        TouchPoint = new Vec(mWorldSize.x * 0.3, mWorldSize.y * 0.5);
    }

    private double mArrowCount = 1000.0;
    public Vec TouchPoint = new Vec();

    private Game mGame;

    public Game GetGame(){ return mGame; }

    private List<Controller> mControllers = new ArrayList<>();

    public JitterControl JitterController = new JitterControl(this);
    private Vec mOffset = new Vec();

    public void SetOffset(Vec offset){
        mOffset = offset;
    }

    public boolean CheeseAdded = false;

    public List<GameObject> Bodies = new ArrayList<>();
    public List<GameObject> BodiesToAdd = new ArrayList<>();
    public List<GameObject> BodiesToRemove = new ArrayList<>();

    public void AddBody(GameObject obj){
        BodiesToAdd.add(obj);
    }
    public void RemoveBody(GameObject obj){
        BodiesToRemove.add(obj);
    }

    public List<GameObject> GetObjectType(int type){
        List<GameObject> returnList = new ArrayList<>();

        for(GameObject obj:Bodies){
            if(obj.Type == type) returnList.add(obj);
        }

        return returnList;
    }

    public int CountObjectType(int type){
        int cnt = 0;

        for(GameObject obj:Bodies){
            if(obj.Type == type) cnt ++;
        }

        return cnt;
    }

    private void addWaiting(){
        Bodies.addAll(BodiesToAdd);

        BodiesToAdd.clear();

        if(!CheeseAdded){
            if(GetObjectType(GameObject.TYPE_CHEESE).size() != 0){
                CheeseAdded = true;
            }
        }
    }

    private void removeWaiting(){
        Bodies.removeAll(BodiesToRemove);

        BodiesToRemove.clear();
    }

    private void resetForces(){
        for(GameObject obj:Bodies){
            obj.ClearForce();
        }
    }

    private void computeForces(double timeStep){
        for(Controller c:mControllers){
            c.Update(timeStep);
        }
    }

    private void updateObjects(double timeStep){
        for(GameObject obj:Bodies){
            obj.Update(timeStep);
        }
    }

    public Engine(Vec worldSize, Game game){
        mWorldSize = worldSize.copy();
        mGame = game;

        mControllers.add(new BotController(this));
        mControllers.add(new CheeseController(this));
        mControllers.add(new FlailController(this));
        mControllers.add(new ParticleController(this));
    }


    /**
     * Run one step of the simulation
     */
    public void Step(double timeStep){
        resetForces();

        computeForces(timeStep);

        updateObjects(timeStep);

        JitterController.Update();

        addWaiting();
        removeWaiting();
    }

    private int mBorderColor = Color.WHITE;
    private int mBorderCount = 0;

    public void SetBorderColor(int color){
        mBorderColor = color;
    }

    /**
     * Draw the current state
     */
    public void Draw(Canvas canvas){
        int saveCount = canvas.save();

        //  If the surface is in landscape mode then rotate
        //  the canvas before drawing objects. Then restore
        //  its rotation after
        if(!mGame.IsLandscape()){
            canvas.rotate(90.0f, canvas.getWidth() / 2.0f, canvas.getWidth() / 2.0f);
        }
        canvas.translate(mOffset.xf(), mOffset.yf());

        // TODO come up with a better background
        SpriteHandler.PAINT.setColor(mBorderColor);
        SpriteHandler.PAINT.setStyle(Paint.Style.STROKE);

        if(mBorderColor != Color.WHITE){
            mBorderCount ++;
            if(mBorderCount == 10) mBorderCount = 0;

            Float b = 50f - mBorderCount;

            canvas.drawRect(b, b, mWorldSize.xf() - b, mWorldSize.yf() - b, SpriteHandler.PAINT);

        }else{
            canvas.drawRect(50f, 50f, mWorldSize.xf() - 50f, mWorldSize.yf() - 50f, SpriteHandler.PAINT);
        }

        for(GameObject obj:Bodies){
            obj.Draw(canvas);
        }

        // Draw touch point
        drawTouchPoint(canvas);

        canvas.restoreToCount(saveCount);
    }

    private void drawTouchPoint(Canvas canvas){
        if(mArrowCount <= 0) return;

        mArrowCount--;

        double alpha = (mArrowCount / 1000) * 255;

        Vec tp = TouchPoint.copy();

        SpriteHandler.PAINT.setColor(Color.WHITE);
        SpriteHandler.PAINT.setStyle(Paint.Style.STROKE);
        SpriteHandler.PAINT.setAlpha((int) alpha);

        float MAX_LENGTH = 80f;
        float MIN_LENGTH = MAX_LENGTH * 0.7f;
        float MAX_WIDTH = 30f;
        float MIN_WIDTH = 15f;
        Path p = new Path();

        p.moveTo(tp.xf() - MAX_LENGTH, tp.yf());
        p.lineTo(tp.xf() - MIN_LENGTH, tp.yf() + MAX_WIDTH);
        p.lineTo(tp.xf() - MIN_LENGTH, tp.yf() + MIN_WIDTH);
        p.lineTo(tp.xf() - MIN_WIDTH, tp.yf() + MIN_WIDTH);
        p.lineTo(tp.xf() - MIN_WIDTH, tp.yf() + MIN_LENGTH);
        p.lineTo(tp.xf() - MAX_WIDTH, tp.yf() + MIN_LENGTH);
        p.lineTo(tp.xf(), tp.yf() + MAX_LENGTH);
        p.lineTo(tp.xf() + MAX_WIDTH, tp.yf() + MIN_LENGTH);
        p.lineTo(tp.xf() + MIN_WIDTH, tp.yf() + MIN_LENGTH);
        p.lineTo(tp.xf() + MIN_WIDTH, tp.yf() + MIN_WIDTH);
        p.lineTo(tp.xf() + MIN_LENGTH, tp.yf() + MIN_WIDTH);
        p.lineTo(tp.xf() + MIN_LENGTH, tp.yf() + MAX_WIDTH);
        p.lineTo(tp.xf() + MAX_LENGTH, tp.yf());
        p.lineTo(tp.xf() + MIN_LENGTH, tp.yf() - MAX_WIDTH);
        p.lineTo(tp.xf() + MIN_LENGTH, tp.yf() - MIN_WIDTH);
        p.lineTo(tp.xf() + MIN_WIDTH, tp.yf() - MIN_WIDTH);
        p.lineTo(tp.xf() + MIN_WIDTH, tp.yf() - MIN_LENGTH);
        p.lineTo(tp.xf() + MAX_WIDTH, tp.yf() - MIN_LENGTH);
        p.lineTo(tp.xf(), tp.yf() - MAX_LENGTH);
        p.lineTo(tp.xf() - MAX_WIDTH, tp.yf() - MIN_LENGTH);
        p.lineTo(tp.xf() - MIN_WIDTH, tp.yf() - MIN_LENGTH);
        p.lineTo(tp.xf() - MIN_WIDTH, tp.yf() - MIN_WIDTH);
        p.lineTo(tp.xf() - MIN_LENGTH, tp.yf() - MIN_WIDTH);
        p.lineTo(tp.xf() - MIN_LENGTH, tp.yf() - MAX_WIDTH);
        p.lineTo(tp.xf() - MAX_LENGTH, tp.yf());

        canvas.drawPath(p, SpriteHandler.PAINT);

        SpriteHandler.PAINT.setAlpha(255);
    }

    public class JitterControl{

        private Engine mEngine;

        private int mCountdownStart = 0;
        private int mCountDown = 0;

        private double mMaxJitter = 10.0;

        private Random rnd;

        public JitterControl(Engine engine) {
            mEngine = engine;

            this.rnd = new Random();
        }

        public void StartJitter(int jitterTime) {
            mCountdownStart = jitterTime;
            mCountDown = jitterTime;
            mMaxJitter = 10.0;
        }

        public void StartJitter(int jitterTime, double maxJitter) {
            mCountdownStart = jitterTime;
            mCountDown = jitterTime;
            mMaxJitter = maxJitter;
        }

        public void Update(){
            if(mCountDown > 0) {
                double p = ((double) mCountDown) / ((double) mCountdownStart);

                double jitter_amount = p * mMaxJitter;

                double angle = Math.PI * 2 * rnd.nextDouble();

                double dx = Math.sin(angle);
                double dy = -Math.cos(angle);

                Vec offset = new Vec(dx * jitter_amount, dy * jitter_amount);

                mEngine.SetOffset(offset);
                mCountDown--;
            }else{
                mEngine.SetOffset(new Vec());
            }
        }
    }
}
