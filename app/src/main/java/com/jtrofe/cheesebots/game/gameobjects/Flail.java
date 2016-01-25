package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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

    public float GetK(){
        return mK;
    }

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
        this.mChainPaint.setColor(Color.RED);
    }

    /**
     * If the user is dragging the flail, draw a chain
     * connecting the head and handle
     * @param canvas Drawing canvas
     */
    @Override
    public void Draw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.argb(200, 255, 255, 0));

        canvas.drawCircle(mPosition.x, mPosition.y, mRadius, p);
        //canvas.drawBitmap(mImage, mPosition.x - mHalfWidth, mPosition.y - mHalfHeight, null);
    }
}
