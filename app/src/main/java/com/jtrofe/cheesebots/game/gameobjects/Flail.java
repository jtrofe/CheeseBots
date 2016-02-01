package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.jtrofe.cheesebots.GameActivity;
import com.jtrofe.cheesebots.game.physics.Vec;


/**
 * Basic flail
 */
public class Flail extends GameObject{

    /**
     * Physics properties
     */
    protected float mRadius;

    public float GetRadius(){
        return mRadius;
    }

    /**
     * Physics variables
     */
    protected float mK;
    public boolean PlowThrough = false; // If true, flail will not bounce off robots

    public float GetK(){
        return mK;
    }

    /**
     * Control variables
     */
    public int TouchPointId = -1;
    public Vec HandlePoint;

    /**
     * Drawing variables
     */
    protected Paint mChainPaint;

    public Flail(Vec position, Bitmap image, float mass, float k, float radius){
        super(position, image, mass);

        this.mType = GameObject.TYPE_FLAIL;

        this.mMass = mass;
        this.mK = k;

        this.mFriction = 0.92f;

        this.mRadius = radius;


        // Set up paint to draw the chain
        this.mChainPaint = new Paint();
        this.mChainPaint.setStyle(Paint.Style.STROKE);
        this.mChainPaint.setStrokeWidth(2);
        this.mChainPaint.setColor(Color.WHITE);
    }

    private Rect getFlail(){
        return new Rect(0, 0, 100, 100);
    }

    /**
     * If the user is dragging the flail, draw a chain
     * connecting the head and handle
     * @param canvas Drawing canvas
     */
    @Override
    public void Draw(Canvas canvas){
        Rect src = getFlail();

        Rect dst = new Rect((int) (mPosition.x - mRadius), (int) (mPosition.y - mRadius),
                (int) (mPosition.x + mRadius), (int) (mPosition.y + mRadius));

        int saveCount = canvas.save();

        canvas.rotate((float) Math.toDegrees(mAngle), mPosition.x, mPosition.y);
        canvas.drawBitmap(GameActivity.SpriteSheets.get(1), src, dst, null);

        canvas.restoreToCount(saveCount);
    }
}
