package com.jtrofe.cheesebots.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.jtrofe.cheesebots.GameActivity;
import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.physics.Engine;
import com.jtrofe.cheesebots.physics.PhysicsView;
import com.jtrofe.cheesebots.physics.Vec;
import com.jtrofe.cheesebots.physics.objects.Bot;
import com.jtrofe.cheesebots.physics.objects.Cheese;
import com.jtrofe.cheesebots.physics.objects.Flail;
import com.jtrofe.cheesebots.physics.objects.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MAIN on 3/13/16
 */
public class Game {

    private PhysicsView mPhysicsView;

    private Engine mEngine;

    public boolean SpritesLoaded = false;

    public Engine GetEngine(){
        return mEngine;
    }

    public Vec TouchPoint = new Vec(-1, -1);

    //
    // Game variables
    //
    private boolean mPaused = false;

    private boolean mInitialized = false;
    private boolean mComplete = false;

    private int mBotsDestroyed = 0;
    private long mScrapAdded = 0;

    public boolean IsComplete(){
        return mComplete;
    }

    private Vec mScreenSize = new Vec();
    private boolean mLandscape = true;

    public GameActivity GameContext = null;

    public void SetPaused(boolean paused){
        mPaused = paused;
    }

    public boolean IsPaused(){
        return mPaused;
    }

    public boolean IsInitialized(){
        return mInitialized;
    }

    public boolean IsLandscape(){
        return mLandscape;
    }

    public void SetPhysicsView(PhysicsView physicsView){
        mPhysicsView = physicsView;
    }

    public void SetWorldSize(int width, int height){
        int w = Math.max(width, height);
        int h = Math.min(width, height);

        mLandscape = (width > height);

        mScreenSize = new Vec(w, h);

        mEngine.SetWorldSize(mScreenSize);

    }

    public List<Bitmap> SpriteSheets = new ArrayList<>();

    private double[] mBotWeights = new double[]{ 20.0, 0.0, 0.0, 0.0};

    public Game(PhysicsView physicsView){
        mPhysicsView = physicsView;

        mEngine = new Engine(new Vec(150, 150), this);
    }

    public void Update(double timeStep){
        if(!mInitialized) return;

        if(!firstBotsAdded){
            long t = System.currentTimeMillis() - firstTime;

            if(t > 3000){
                createFirstBots();
            }
        }

        if(!mComplete) GameContext.SetScore(mBotsDestroyed + "");

        if(!mPaused){
            mEngine.Step(timeStep);
        }

        if(!mComplete && mEngine.CheeseAdded){
            if(mEngine.CountObjectType(GameObject.TYPE_CHEESE) == 0){
                OnComplete();
            }
        }
    }

    public void Draw(Canvas canvas){
        mEngine.Draw(canvas);
    }

    public void OnComplete(){
        mComplete = true;

        GameContext.OnComplete(mBotsDestroyed, mScrapAdded);
    }

    public void SetBorderColor(int color){
        mEngine.SetBorderColor(color);
    }

    public void Initialize(){
        mInitialized = true;
        mComplete = false;
        mBotsDestroyed = 0;

        Vec cheesePos = mEngine.GetWorldSize().ScalarMultiply(0.5);

        double radius = mEngine.GetWorldSize().y * 0.1;

        //TODO replace with CurrentUser.GetCheeses() (multiple cheese) when upgrades are to be implemented
        Cheese c = new Cheese(1, radius, 10.0);
        c.SetPosition(cheesePos);

        mEngine.AddBody(c);

        Flail f = GameApp.CurrentUser.GetSelectedFlail();

        mEngine.AddBody(f);

        firstBotsAdded = false;
        firstTime = System.currentTimeMillis();
    }

    private boolean firstBotsAdded;
    private long firstTime;

    private void createFirstBots(){
        for(int i=0;i<5;i++){
            addBot();
        }
        firstBotsAdded = true;
    }

    private void addBot(){
        Random rnd = new Random();

        double x;
        double y;

        double buffer = 200;

        if(rnd.nextBoolean()){
            x = rnd.nextDouble() * (mScreenSize.x + buffer) - buffer;
            y = (rnd.nextBoolean()) ? mScreenSize.y + buffer : -buffer;
        }else{
            y = rnd.nextDouble() * (mScreenSize.y + buffer) - buffer;
            x = (rnd.nextBoolean()) ? mScreenSize.x + buffer : -buffer;
        }

        double totalWeight = 0;
        for(double w:mBotWeights){
            totalWeight += w;
        }

        int randIndex = -1;
        double rand = rnd.nextDouble() * totalWeight;


        for(int i=0;i<mBotWeights.length;i++){
            rand -= mBotWeights[i];

            if(rand <= 0){
                randIndex = i;
                break;
            }
        }

        Vec pos = new Vec(x, y);

        Bot b;
        switch(randIndex){
            case 0:
                b = new Bot(pos, 50.0, 100, 60, 0.1, SpriteHandler.SHEET_SMALL_BOT);
                b.SetMainColor(Color.parseColor("#FF9900"));
                b.SetSecondaryColor(Color.parseColor("#006745"));
                b.SetTernaryColor(Color.parseColor("#665EC5"));
                break;
            case 1:
                b = new Bot(pos, 80.0, 85, 100, 0.1, SpriteHandler.SHEET_MEDIUM_BOT, 300.0);
                b.SetMainColor(Color.parseColor("#463DB7"));
                b.SetSecondaryColor(Color.parseColor("#27AF82"));
                b.SetTernaryColor(Color.parseColor("#4BBF99"));
                break;
            case 2:
                b = new Bot(pos, 200, 80, 220, 0.2, SpriteHandler.SHEET_LARGE_BOT, 500.0);
                b.SetImageSize(new Vec(124, 220));
                b.SetMainColor(Color.parseColor("#D46A6A"));
                b.SetTernaryColor(Color.parseColor("#801515"));
                break;
            case 3:
                b = new Bot(pos, 320, 162, 176, 0.4, SpriteHandler.SHEET_GIANT_BOT, 800.0);
                b.SetImageSize(new Vec(320, 325));
                b.SetMainColor(Color.parseColor("#F8F74E"));
                b.SetSecondaryColor(Color.parseColor("#2F187D"));
                b.SetTernaryColor(Color.parseColor("#E78EC2"));
                break;
            default:
                b = new Bot(pos, 50.0, 100, 60, 0.1, SpriteHandler.SHEET_SMALL_BOT);
                b.SetMainColor(Color.parseColor("#FF9900"));
                b.SetSecondaryColor(Color.parseColor("#006745"));
                b.SetTernaryColor(Color.parseColor("#665EC5"));
                break;
        }


        mBotWeights[1] += 0.05;
        mBotWeights[2] += 0.01;
        mBotWeights[3] += 0.001;


        mEngine.AddBody(b);
    }

    public void OnBotDestroyed(long scrap){

        addBot();

        if(mComplete) return;

        mBotsDestroyed ++;
        if(mBotsDestroyed % 50 == 0) addBot();

        mScrapAdded += scrap;
        GameContext.SetScrap(mScrapAdded + "");

        GameApp.CurrentUser.AddScrap(scrap);
    }
}
