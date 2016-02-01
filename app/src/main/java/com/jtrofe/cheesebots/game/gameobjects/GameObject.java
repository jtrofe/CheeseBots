package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * The basic object everything will extend
 */
public abstract class GameObject{

    public static float QUARTER_CIRCLE = (float) Math.PI/2;

    public static int TYPE_PARTICLE = 0;
    public static int TYPE_BOT = 1;
    public static int TYPE_CHEESE = 2;
    public static int TYPE_FLAIL = 3;


    /**
     * Default values for particles
     */
    public static int PARTICLE_RADIUS = 2;
    public static float PARTICLE_MASS = 4;



    public int GetType(){
        return mType;
    }

    /**
     * Physical properties
     */
    protected int mType = TYPE_PARTICLE;

    protected float mMass;
    protected float mInvMass;
    protected float mMomentOfInertia;
    protected float mInvMoment;
    protected int mHalfWidth;
    protected int mHalfHeight;

    public int GetHalfWidth(){
        return mHalfWidth;
    }
    public int GetHalfHeight(){
        return mHalfHeight;
    }

    public float GetMass(){
        return mMass;
    }

    /**
     * Physics variables
     */
    protected float mFriction = 0.9f;

    protected Vec mPosition = new Vec();
    protected Vec mLinearVelocity = new Vec();
    protected Vec mForce = new Vec();

    protected float mAngle = 0;
    protected float mAngularVelocity = 0;
    protected float mTorque = 0;



    public Vec GetPosition(){
        return mPosition;
    }

    public void SetPosition(Vec p){
        mPosition = p.Clone();
    }

    public Vec GetLinearVelocity(){
        return mLinearVelocity.Clone();
    }

    public void SetLinearVelocity(Vec v){
        mLinearVelocity = v.Clone();
    }

    /**
     * Visual variables
     */
    protected Bitmap mImage;
    public float CurrentFrame = 0;

    public GameObject(Vec position, Bitmap image, float mass){
        this.mPosition = position;
        this.mImage = image;

        if(image != null) {
            this.mHalfWidth = image.getWidth() / 2;
            this.mHalfHeight = image.getHeight() / 2;
        }else{
            this.mHalfWidth = 0;
            this.mHalfHeight = 0;
        }

        this.mMass = mass;
        this.mInvMass = 1 / mass;

        this.mLinearVelocity = new Vec();
        this.mAngle = 0;
        this.mAngularVelocity = 0;

        calculateMomentOfInertia();
    }

    /**
     * Default moment is that of a small particle
     */
    protected void calculateMomentOfInertia(){
        mMomentOfInertia = (mMass * PARTICLE_RADIUS * PARTICLE_RADIUS)/2;
        mInvMoment = 1 / mMomentOfInertia;
    }


    /**
     * Reset force and torque at the
     * beginning of each simulation step
     */
    public void ClearForce(){
        mForce = new Vec();

        mTorque = 0;
    }

    /**
     * Simple, add some torque to the object
     * @param torque Amount of torque to add
     */
    public void ApplyTorque(float torque){
        mTorque += torque;
    }

    /**
     * Apply an impulse at a world point. Immediately
     * change linear and angular velocity
     * @param impulse Impulse to apply
     * @param point The world point where the force will be applied
     */
    public void ApplyImpulse(Vec impulse, Vec point){
        Vec f = impulse.ScalarMultiply(mInvMass);

        mLinearVelocity.Add(f);

        Vec r = point.Subtract(mPosition);
        mAngularVelocity += mInvMoment * (r.x * impulse.y -
                r.y * impulse.x);
    }

    /**
     * Apply a force to the body at a world point
     * @param force The force vector to apply
     * @param point The world point where the force will be applied
     */
    public void ApplyForce(Vec force, Vec point){
        mForce = mForce.Add(force);

        Vec r = point.Subtract(mPosition);
        mTorque += r.x * force.y - r.y * force.x;
    }

    /**
     * Update position based on applied forces
     * @param timeStep How many seconds to simulate
     */
    public void UpdateForceAndTorque(float timeStep){
        Vec linearAcceleration = mForce.ScalarDivide(mMass);
        mLinearVelocity = mLinearVelocity.Add(linearAcceleration.ScalarMultiply(timeStep));

        mPosition = mPosition.Add(mLinearVelocity.ScalarMultiply(timeStep));

        float angularAcceleration = mTorque / mMomentOfInertia;

        mAngularVelocity += angularAcceleration * timeStep;
        mAngle += mAngularVelocity * timeStep;

        // Apply friction
        mLinearVelocity = mLinearVelocity.ScalarMultiply(mFriction);
        mAngularVelocity *= mFriction;
    }

    /**
     * Apply a force to move an object towards a point
     * @param point Point to move towards
     * @param forceMultiplier Scale the force. Use a negative value to move away from the point
     */
    public void MoveTowardsPoint(Vec point, float forceMultiplier, float maxForceMagnitude){
        Vec distance = point.Subtract(mPosition);

        Vec force = distance.ScalarMultiply(forceMultiplier);

        force = force.Clamp(maxForceMagnitude);

        this.ApplyForce(force, mPosition);
    }

    /**
     * Draw the object's bitmap to the canvas
     * @param canvas GameSurfaceView's canvas
     */
    public void Draw(Canvas canvas){
        int saveCount = canvas.save();

        canvas.rotate((float) Math.toDegrees(mAngle), mPosition.x, mPosition.y);
        canvas.drawBitmap(mImage, mPosition.x - mHalfWidth, mPosition.y - mHalfHeight, null);

        canvas.restoreToCount(saveCount);
    }
}
