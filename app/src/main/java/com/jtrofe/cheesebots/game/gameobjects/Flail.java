package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.jtrofe.cheesebots.GameActivity;
import com.jtrofe.cheesebots.game.physics.Engine;
import com.jtrofe.cheesebots.game.physics.Vec;
import com.jtrofe.cheesebots.game.physics.constraints.DistanceConstraint;

import java.util.ArrayList;
import java.util.List;


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
    public boolean HasRope = false;

    public GameObject HandleNode = null;

    /**
     * Drawing variables
     */
    protected Paint mChainPaint;

    public Flail(Vec position, Bitmap image, float mass, float k, float radius){
        super(position, image, mass);

        this.mType = GameObject.TYPE_FLAIL;

        this.mMass = mass;
        this.mK = k;

        this.mRadius = radius;


        // Set up paint to draw the chain
        this.mChainPaint = new Paint();
        this.mChainPaint.setStyle(Paint.Style.STROKE);
        this.mChainPaint.setStrokeWidth(2);
        this.mChainPaint.setColor(Color.WHITE);


        calculateMomentOfInertia();
    }

    public void CreateRope(Engine engine){
        int NODE_COUNT = 10;
        float DISTANCE = 20;

        RopeNode lastNode = null;
        RopeNode firstNode = null;
        RopeNode prevNode = null;

        RopeNode[] nodes = new RopeNode[NODE_COUNT];

        for(int i=0;i<NODE_COUNT;i++){
            RopeNode n = new RopeNode(mPosition.Add(new Vec(0, -1 * DISTANCE)));
            nodes[i] = n;

            engine.AddBody(n);
        }

        HandleNode = nodes[NODE_COUNT-1];
        nodes[NODE_COUNT-1].SetMass(Float.MAX_VALUE);

        for(int i=NODE_COUNT-2;i>=0;i--){
            nodes[i].Parent = nodes[i+1];

            DistanceConstraint d = new DistanceConstraint(nodes[i], nodes[i+1], DISTANCE);
            engine.AddConstraint(d);
        }

        nodes[0].Child = this;
        DistanceConstraint d = new DistanceConstraint(this, nodes[0], DISTANCE);
        d.SetLocalPointA(new Vec(-mRadius, mRadius));
        engine.AddConstraint(d);

        /*
        for(int i=0;i<NODE_COUNT;i++){
            float percent
            RopeNode n = new RopeNode(mPosition.Add(new Vec(0, -i * DISTANCE)));
            if(firstNode == null){
                firstNode = n;
                n.Parent = this;

                DistanceConstraint d = new DistanceConstraint(this, n, DISTANCE);
                d.SetLocalPointA(new Vec(-mRadius, mRadius));
                engine.AddConstraint(d);
            }else{
                n.Parent = prevNode;

                DistanceConstraint d = new DistanceConstraint(n, prevNode, DISTANCE);
                engine.AddConstraint(d);

            }

            engine.AddBody(n);


            prevNode = n;
        }

        prevNode.SetMass(Float.MAX_VALUE);
        HandleNode = prevNode;



        /*
        for(int i=0;i<NODE_COUNT;i++){
            RopeNode n = new RopeNode(mPosition.Add(new Vec(0, i * DISTANCE)));
            n.Parent = lastNode;

            if(firstNode == null) firstNode = n;

            engine.AddBody(n);

            if(lastNode != null){
                DistanceConstraint d = new DistanceConstraint(n, n.Parent, DISTANCE);

                engine.AddConstraint(d);
            }


            lastNode = n;
        }

        HandleNode = lastNode;
        lastNode.SetMass(Float.MAX_VALUE);

        firstNode.Parent = this;

        DistanceConstraint d = new DistanceConstraint(this, firstNode, DISTANCE);
        d.SetLocalPointA(new Vec(-mRadius, mRadius));
        engine.AddConstraint(d);*/
    }


    /**
     * Default moment is that of a small particle
     */
    @Override
    protected void calculateMomentOfInertia(){
        mMomentOfInertia = (mMass * mRadius * mRadius)/2;
        mInvMoment = 1 / mMomentOfInertia;
    }

    /**
     * Returns a rectangle representing where in the flail sprite
     * sheet to take a picture from
     * @return Src rectangle
     */
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

        if(HandlePoint != null){
            Vec attachPoint = LocalVectorToWorldVector(new Vec(-mRadius, mRadius));
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(4);
            canvas.drawLine(HandlePoint.x, HandlePoint.y, attachPoint.x, attachPoint.y, p);
        }

        Rect src = getFlail();

        Rect dst = new Rect((int) (mPosition.x - mRadius), (int) (mPosition.y - mRadius),
                (int) (mPosition.x + mRadius), (int) (mPosition.y + mRadius));

        int saveCount = canvas.save();

        canvas.rotate((float) Math.toDegrees(mAngle), mPosition.x, mPosition.y);
        canvas.drawBitmap(GameActivity.SpriteSheets.get(1), src, dst, null);

        canvas.restoreToCount(saveCount);

    }
}
