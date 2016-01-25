package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Basic cheese
 */
public class Cheese extends GameObject implements Parcelable{

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

    //------------------------------------//
    //----THINGS FOR SAVING GAME STATE----//
    //------------------------------------//
    public static final Parcelable.Creator<Cheese> CREATOR = new Parcelable.Creator<Cheese>() {
        public Cheese createFromParcel(Parcel in) {
            return new Cheese(in) {
            };
        }

        public Cheese[] newArray(int size) {
            return new Cheese[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Write the game object to parcel, and additionally:
    // mRadius
    // mAmountLeft
    //
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);

        dest.writeFloat(mRadius);
        dest.writeFloat(mAmountLeft);
    }

    private Cheese(Parcel in){
        super(in);

        mRadius = in.readFloat();
        mAmountLeft = in.readFloat();
    }
}
