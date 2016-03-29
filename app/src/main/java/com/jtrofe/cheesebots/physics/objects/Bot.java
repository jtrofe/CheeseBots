package com.jtrofe.cheesebots.physics.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.physics.Vec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAIN on 3/12/16
 */
public class Bot extends GameObject{

    public static final int STATE_WALKING = 0;
    public static final int STATE_EATING = 1;

    private int mMainColor = Color.RED;
    private int mSecondaryColor = Color.YELLOW;
    private int mTernaryColor = Color.BLUE;
    private int fillColor;

    public int GetMainColor(){ return mMainColor; }
    public int GetSecondaryColor(){ return mSecondaryColor; }
    public int GetTernaryColor(){ return mTernaryColor; }

    public void SetMainColor(int color){
        mMainColor = color;
        setFillColor();
    }

    public void SetSecondaryColor(int color){
        mSecondaryColor = color;
        setFillColor();
    }

    public void SetTernaryColor(int color){
        mTernaryColor = color;
        setFillColor();
    }

    private void setFillColor(){
        if(System.currentTimeMillis() % 3 == 0){
            fillColor = mMainColor;
        }else if(System.currentTimeMillis() % 2 == 0){
            fillColor = mSecondaryColor;
        }else{
            fillColor = mTernaryColor;
        }
    }

    public double CurrentFrame = 0;

    public int State = STATE_WALKING;

    private double mBoundRadius = 0;

    private double mEatingSpeed;
    private double mTotalHealth;
    private double mHealthPoints;

    private int mSpriteSheetIndex;

    public double GetBoundRadius(){ return mBoundRadius; }

    public double GetEatingSpeed(){ return mEatingSpeed; }

    public void ApplyDamage(double damage){
        mHealthPoints -= damage;
        if(mHealthPoints < 0) mHealthPoints = 0;
    }

    public boolean IsAlive(){ return mHealthPoints > 0; }

    private Vec mImageSize = new Vec();

    public void SetImageSize(Vec v){
        mImageSize = v.copy();
    }

    public Bot(Vec position, double mass, int w, int h, double eatingSpeed, int spriteSheetIndex, double totalHealth){
        super(position, mass);

        Type = GameObject.TYPE_BOT;

        mHalfWidth = w / 2;
        mHalfHeight = h / 2;

        mEatingSpeed = eatingSpeed;
        mSpriteSheetIndex = spriteSheetIndex;

        mBoundRadius = Math.sqrt((mHalfWidth * mHalfWidth) + (mHalfHeight * mHalfHeight));

        this.calculateMoment();

        mAngle = Math.PI / 4;

        this.updateUnitVectors();

        mImageSize = new Vec(w, h);
        mTotalHealth = totalHealth;
        mHealthPoints = totalHealth;
    }



    public Bot(Vec position, double mass, int w, int h, double eatingSpeed, int spriteSheetIndex){
        this(position, mass, w, h, eatingSpeed, spriteSheetIndex, 100);
    }

    /**
     * This is the formula to calculate moment of
     * inertia for a rectangle
     */
    @Override
    public void calculateMoment(){
        double w = mHalfWidth / 2;
        double h = mHalfHeight / 2;

        mMoment = (mMass * (w * w + h * h)) / 12;

        if(mMoment == 0){
            mInvMoment = 0;
        }else{
            mInvMoment = 1 / mMoment;
        }
    }

    public List<Vec> GetVertices(){
        Vec vx = mUnitX.ScalarMultiply(mHalfWidth);
        Vec vy = mUnitY.ScalarMultiply(mHalfHeight);
        Vec p = mPosition.copy();

        List<Vec> l = new ArrayList<>();

        l.add(p.Add(vx).Add(vy));
        l.add(p.Add(vx).Subtract(vy));
        l.add(p.Subtract(vx).Subtract(vy));
        l.add(p.Subtract(vx).Add(vy));

        return l;
    }

    /**
     * Apply torque to the bot to align the
     * unit vector y with some other unit vector
     * @param v Vector to align with
     */
    public void SteerToAlign(Vec v){
        double dp_y = v.Dot(mUnitY);
        double amount_oriented = 2 - ((dp_y * dp_y) + 1);

        double dp_x = v.Dot(mUnitX);

        int turn_direction = (dp_x < 0)? -1 : 1;

        double MAX_TURN_TORQUE = 0.6 * turn_direction * mMoment;

        ApplyTorque(MAX_TURN_TORQUE * amount_oriented);
    }

    private Rect getFrame(){
        int[] frames = SpriteHandler.WALK_FRAMES.get(mSpriteSheetIndex).clone();

        if(State == STATE_EATING) frames = SpriteHandler.EAT_FRAMES.get(mSpriteSheetIndex).clone();

        if(CurrentFrame >= frames.length) CurrentFrame = 0.0;

        int f = (int) CurrentFrame;

        int x = frames[f];

        double healthPercent = mHealthPoints / mTotalHealth;

        int y;

        if(healthPercent > 0.66){
            y = 0;
        }else if(healthPercent > 0.33){
            y = 1;
        }else{
            y = 2;
        }

        int w = mImageSize.xi();
        int h = mImageSize.yi();

        return new Rect(x * w, y * h, (x * w) + w, (y * h) + h);
    }


    @Override
    public void Draw(Canvas canvas){
        Rect src = getFrame();
        CurrentFrame += 0.2;

        double w = mImageSize.x / 2;
        double h = mImageSize.y / 2;

        Rect dst = new Rect((int) (mPosition.x - w), (int) (mPosition.y - h),
                (int) (mPosition.x + w), (int) (mPosition.y + h));

        int saveCount = canvas.save();

        canvas.rotate((float) Math.toDegrees(mAngle), mPosition.xf(), mPosition.yf());

        if(GameApp.CurrentGame == null) return;

        if(GameApp.CurrentGame.SpritesLoaded){
            canvas.drawBitmap(GameApp.CurrentGame.SpriteSheets.get(mSpriteSheetIndex), src, dst, null);
        }else{
            SpriteHandler.PAINT.setColor(fillColor);
            SpriteHandler.PAINT.setStyle(Paint.Style.FILL);

            float wf = (float) w;
            float hf = (float) h;

            canvas.drawOval(mPosition.xf() - wf, mPosition.yf() - hf, mPosition.xf() + wf,
                    mPosition.yf() + hf, SpriteHandler.PAINT);
        }

        canvas.restoreToCount(saveCount);
    }
}
