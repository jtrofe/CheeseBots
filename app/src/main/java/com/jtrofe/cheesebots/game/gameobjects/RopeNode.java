package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.jtrofe.cheesebots.game.physics.Vec;


/**
 * One node in a longer rope
 */
public class RopeNode extends GameObject{

    public GameObject Child;
    public GameObject Parent;
    public Vec pPoint;
    public Paint paint;

    public RopeNode(Vec position){
        super(position, null, 5);
        this.mType = GameObject.TYPE_ROPE_NODE;


        this.mFriction = 0.92f;

        this.paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void SetMass(float m){
        mMass = m;
    }


    @Override
    public void Draw(Canvas canvas){
        if(Parent != null){
            paint.setColor(Color.WHITE);
            canvas.drawLine(mPosition.x, mPosition.y, Parent.GetPosition().x, Parent.GetPosition().y, paint);
        }else{
            paint.setColor(Color.RED);
            mPosition.Circle(canvas, paint);
        }

        if(Child != null){
            paint.setColor(Color.WHITE);
            canvas.drawLine(mPosition.x, mPosition.y, Child.GetPosition().x, Child.GetPosition().y, paint);
        }

        int r = 3;
        //canvas.drawCircle(mPosition.x, mPosition.y, r, paint);
    }
}