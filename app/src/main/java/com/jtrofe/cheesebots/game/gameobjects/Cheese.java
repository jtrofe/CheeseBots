package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Basic cheese
 */
public class Cheese extends GameObject{

    protected float mRadius;
    protected float mAmountLeft;

    public float GetAmountLeft(){
        return mAmountLeft;
    }

    public float GetRadius(){
        return mRadius;
    }

    public Cheese(Vec position, Bitmap image, float radius){
        super(position, image, Float.MAX_VALUE);

        this.mType = GameObject.TYPE_CHEESE;

        this.mRadius = radius;

        mAmountLeft = 100;
    }

    @Override
    public void Draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);

        float angle = (mAmountLeft / 100) * 360;

        RectF bounds = new RectF(mPosition.x - mRadius, mPosition.y - mRadius,
                                mPosition.x + mRadius, mPosition.y + mRadius);
        canvas.drawArc(bounds, 0, angle, true, paint);
    }

    public void Eat(float amount){
        mAmountLeft -= amount;

        if(mAmountLeft <= 0){
            mAmountLeft = 0;
        }
    }
}
