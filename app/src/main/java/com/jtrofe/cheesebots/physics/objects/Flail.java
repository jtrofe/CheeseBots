package com.jtrofe.cheesebots.physics.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.SpriteHandler;
import com.jtrofe.cheesebots.physics.Vec;

/**
 * Created by MAIN on 3/11/16
 */
public class Flail extends GameObject {


    private double mRadius;
    private double mK;
    public boolean IsPlow = false;

    public double GetRadius(){
        return mRadius;
    }

    public double GetK(){
        return mK;
    }

    public Vec HandlePoint = new Vec();

    public Flail(double mass, double radius, double k){
        super(new Vec(), mass);

        mRadius = radius;
        mK = k;

        Type = GameObject.TYPE_FLAIL;

        this.calculateMoment();
    }

    /**
     * Moment of inertia calculation for a disk
     */
    @Override
    protected void calculateMoment(){
        mMoment = (mMass * mRadius * mRadius) / 2;

        if(mMoment == 0){
            mInvMoment = 0;
        }else{
            mInvMoment = 1 / mMoment;
        }
    }


    private Rect getFlailSrc(){
        return new Rect(0, 0, 100, 100);
    }

    @Override
    public void Draw(Canvas canvas){
        if(HandlePoint.x != -1 || HandlePoint.y != -1){
            Vec attachPoint = LocalVectorToWorldVector(new Vec(-mRadius, mRadius));

            float HANDLE_WIDTH = 4f;

            SpriteHandler.PAINT.setColor(Color.WHITE);
            SpriteHandler.PAINT.setStyle(Paint.Style.STROKE);
            SpriteHandler.PAINT.setStrokeWidth(HANDLE_WIDTH);

            canvas.drawLine(HandlePoint.xf(), HandlePoint.yf(), attachPoint.xf(), attachPoint.yf(),
                    SpriteHandler.PAINT);
        }

        int ri = (int) mRadius;
        Rect src = getFlailSrc();
        Rect dst = new Rect(mPosition.xi() - ri, mPosition.yi() - ri,
                            mPosition.xi() + ri, mPosition.yi() + ri);

        int saveCount = canvas.save();

        if(GameApp.CurrentGame == null) return;

        if(GameApp.CurrentGame.SpritesLoaded){
            canvas.rotate((float) Math.toDegrees(mAngle), mPosition.xf(), mPosition.yf());
            canvas.drawBitmap(GameApp.CurrentGame.SpriteSheets.get(SpriteHandler.SHEET_FLAIL),
                                src, dst, null);
        }else{
            SpriteHandler.PAINT.setColor(Color.WHITE);
            SpriteHandler.PAINT.setStyle(Paint.Style.STROKE);

            canvas.drawCircle(mPosition.xf(), mPosition.yf(), (float) mRadius, SpriteHandler.PAINT);
        }

        canvas.restoreToCount(saveCount);
    }
}
