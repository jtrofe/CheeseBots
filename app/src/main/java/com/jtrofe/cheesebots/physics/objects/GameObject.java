package com.jtrofe.cheesebots.physics.objects;

import android.graphics.Canvas;
import android.graphics.Color;

import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.physics.Vec;

/**
 * Created by MAIN on 3/11/16
 */
public class GameObject {

    public static final double QUARTER_CIRCLE = Math.PI/2;

    public static final int TYPE_PARTICLE = 0;
    public static final int TYPE_BOT = 1;
    public static final int TYPE_CHEESE = 2;
    public static final int TYPE_FLAIL = 3;

    public static final double PARTICLE_RADIUS = 4;
    public static final double PARTICLE_MASS = 4;

    public int Type = TYPE_PARTICLE;

    //
    //  Physical properties
    //
    protected double mMass = PARTICLE_MASS;
    protected double mInvMass = 1 / mMass;
    protected double mMoment = 0.0;
    protected double mInvMoment = 0.0;
    protected double mFriction = 0.9;

    protected Vec mPosition = new Vec();
    protected Vec mLinearVelocity = new Vec();
    protected Vec mForce = new Vec();

    protected double mAngle = 0.0;
    protected double mAngularVelocity = 0.0;
    protected double mTorque = 0.0;

    protected Vec mUnitX = new Vec(1.0, 0.0);
    protected Vec mUnitY = new Vec(0.0, -1.0);

    protected double mHalfWidth = 0.0;
    protected double mHalfHeight = 0.0;

    //
    //  Getters
    //
    public double GetHalfWidth(){
        return mHalfWidth;
    }
    public double GetHalfHeight(){
        return mHalfHeight;
    }

    public Vec GetUnitX(){
        return mUnitX.copy();
    }
    public Vec GetUnitY(){
        return mUnitY.copy();
    }

    public double GetMass(){
        return mMass;
    }

    public Vec GetPosition(){
        return mPosition.copy();
    }
    public Vec GetLinearVelocity(){
        return mLinearVelocity.copy();
    }

    //
    //  Constructor
    //
    public GameObject(Vec position){
        mPosition = position.copy();
    }

    public GameObject(Vec position, double mass){
        mPosition = position.copy();
        mMass = mass;
        mInvMass = 1 / mass;
    }

    /**
     * Default moment of inertia calculation
     * is for a small disk
     */
    protected void calculateMoment(){
        mMoment = (mMass * PARTICLE_RADIUS * PARTICLE_RADIUS) / 2;

        if(mMoment == 0){
            mInvMoment = 0;
        }else{
            mInvMoment = 1 / mMoment;
        }
    }


    /**
     * Simple, add some torque to the object
     * @param appliedTorque Amount of torque to add
     */
    public void ApplyTorque(double appliedTorque){
        mTorque += appliedTorque;
    }

    /**
     * Apply an impulse at a world point. Immediately
     * change linear and angular velocity
     * @param appliedImpulse Impulse to apply
     * @param appliedPoint The world point where the force will be applied
     */
    public void ApplyImpulse(Vec appliedImpulse, Vec appliedPoint){
        mLinearVelocity = mLinearVelocity.Add(appliedImpulse.ScalarMultiply(mInvMass));

        Vec r = appliedPoint.Subtract(mPosition);
        mAngularVelocity += (r.x * appliedImpulse.y - r.y * appliedImpulse.x) * mInvMoment;
    }

    /**
     * Apply a force to the body at a world point
     * @param appliedForce The force vector to apply
     * @param appliedPoint The world point where the force will be applied
     */
    public void ApplyForce(Vec appliedForce, Vec appliedPoint){
        mForce = mForce.Add(appliedForce);

        Vec r = appliedPoint.Subtract(mPosition);
        mTorque += r.x * appliedForce.y - r.y * appliedForce.x;
    }

    /**
     * Apply a force to the center of the body's mass
     * @param appliedForce The force vector to apply
     */
    public void ApplyForceToCenter(Vec appliedForce){
        mForce = mForce.Add(appliedForce);
    }

    public void ClearForce(){
        mForce = new Vec();
        mTorque = 0.0;
    }

    /**
     * Integrate the position/angle and velocity based on
     * currently applied forces.
     * Update unit vectors after angle is calculated
     * @param timeStep How much time to simulate
     */
    public void Update(double timeStep){
        Vec linearAcceleration = mForce.ScalarMultiply(mInvMass);

        mLinearVelocity = mLinearVelocity.Add(linearAcceleration.ScalarMultiply(timeStep));
        mPosition = mPosition.Add(mLinearVelocity.ScalarMultiply(timeStep));

        double angularAcceleration = mTorque * mInvMoment;

        mAngularVelocity += angularAcceleration * timeStep;
        mAngle += mAngularVelocity * timeStep;

        mLinearVelocity = mLinearVelocity.ScalarMultiply(mFriction);
        mAngularVelocity *= mFriction;

        updateUnitVectors();
    }

    /**
     * Update the half-width unit vectors
     * based on the current angle
     */
    protected void updateUnitVectors(){
        double dx1 = Math.sin(mAngle + QUARTER_CIRCLE);
        double dy1 = -Math.cos(mAngle + QUARTER_CIRCLE);
        mUnitX = new Vec(dx1, dy1);

        double dx2 = Math.sin(mAngle);
        double dy2 = -Math.cos(mAngle);
        mUnitY = new Vec(dx2, dy2);
    }

    /**
     * Take a vector in the object's frame of reference
     * and convert it to a point in the world
     * @param v Local vector
     * @return Corresponding point in the world
     */
    public Vec LocalVectorToWorldVector(Vec v){
        Vec vx = mUnitX.ScalarMultiply(v.x);
        Vec vy = mUnitY.ScalarMultiply(v.y);

        return mPosition.Add(vx).Add(vy);
    }

    /**
     * Apply a force to move an object towards a point
     * @param point Point to move towards
     * @param multiplier Scale the force. Use a negative value to move away from the point
     * @param maxForce Maximum length of force vector
     */
    public void MoveTowardsPoint(Vec point, double multiplier, double maxForce){
        Vec force = point.Subtract(mPosition).ScalarMultiply(multiplier);

        force = force.Clamp(maxForce);

        ApplyForceToCenter(force);
    }

    /**
     * Default draw function draws a small
     * circle around the object's position
     * @param canvas PhysicsView's canvas
     */
    public void Draw(Canvas canvas){
        SpriteHandler.PAINT.setColor(Color.WHITE);

        canvas.drawCircle(mPosition.xf(), mPosition.yf(), 10.0f, SpriteHandler.PAINT);
    }
}
