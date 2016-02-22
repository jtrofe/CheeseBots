package com.jtrofe.cheesebots.physics.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/8/16.
 */
public abstract class GameObject(protected var mPosition:Vec,
                                 protected val mMass:Double = GameObject.PARTICLE_MASS){

    companion object{
        public val QUARTER_CIRCLE:Double = Math.PI/2

        public val TYPE_PARTICLE:Int = 0
        public val TYPE_BOT:Int = 1
        public val TYPE_CHEESE:Int = 2
        public val TYPE_FLAIL:Int = 3
        public val TYPE_ROPE_NODE:Int = 4

        public val PARTICLE_RADIUS:Double = 2.0
        public val PARTICLE_MASS:Double = 4.0
    }

    public var Type:Int = GameObject.TYPE_PARTICLE

    //
    //  Physical properties
    //
    protected var mInvMass:Double = 1 / mMass
    protected var mMoment:Double = 0.0
    protected var mInvMoment:Double = 0.0
    protected var mFriction:Double = 0.9

    protected var mLinearVelocity:Vec = Vec()
    protected var mForce:Vec = Vec()

    protected var mAngle:Double = 0.0
    protected var mAngularVelocity:Double = 0.0
    protected var mTorque:Double = 0.0

    protected var mUnitX:Vec = Vec(1.0, 0.0)
    protected var mUnitY:Vec = Vec(0.0, -1.0)

    protected var mHalfWidth:Double = 0.0
    protected var mHalfHeight:Double = 0.0

    public fun GetMass():Double{
        return mMass
    }
    public fun GetPosition():Vec{
        return mPosition.copy()
    }

    public fun GetLinearVelocity():Vec{
        return mLinearVelocity.copy()
    }

    public fun GetUnitX():Vec{
        return mUnitX.copy()
    }

    public fun GetUnitY():Vec{
        return mUnitY.copy()
    }

    /**
     * Default moment of inertia calculation
     * is for a small disk
     */
    open fun calculateMoment(){
        mMoment = (mMass * PARTICLE_RADIUS * PARTICLE_RADIUS) / 2;

        if(mMoment.equals(0.0)){
            mInvMoment = 0.0
        }else{
            mInvMoment = 1.0 / mMoment
        }
    }

    /**
     * Simple, add some torque to the object
     * @param appliedTorque Amount of torque to add
     */
    public fun ApplyTorque(appliedTorque:Double){
        mTorque += appliedTorque
    }

    /**
     * Apply an impulse at a world point. Immediately
     * change linear and angular velocity
     * @param appliedImpulse Impulse to apply
     * @param appliedPoint The world point where the force will be applied
     */
    public fun ApplyImpulse(appliedImpulse:Vec, appliedPoint:Vec){
        mLinearVelocity = mLinearVelocity + (appliedImpulse * mInvMass)

        val r = appliedPoint - mPosition
        mAngularVelocity += (r.x * appliedImpulse.y - r.y * appliedImpulse.x) * mInvMoment
    }

    /**
     * Apply a force to the body at a world point
     * @param appliedForce The force vector to apply
     * @param appliedPoint The world point where the force will be applied
     */
    public fun ApplyForce(appliedForce:Vec, appliedPoint:Vec){
        mForce = mForce + appliedForce

        val r = appliedPoint - mPosition
        mTorque += r.x * appliedForce.y - r.y * appliedForce.x
    }

    /**
     * Apply a force to the center of the body's mass
     * @param appliedForce The force vector to apply
     */
    public fun ApplyForceToCenter(appliedForce:Vec){
        mForce = mForce + appliedForce
    }

    public fun ClearForce(){
        mForce = Vec()
        mTorque = 0.0
    }

    /**
     * Integrate the position/angle and velocity based on
     * currently applied forces.
     * Update unit vectors after angle is calculated
     * @param timeStep How much time to simulate
     */
    public fun Update(timeStep:Double){
        val linearAcceleration:Vec = mForce * mInvMass

        mLinearVelocity = mLinearVelocity + (linearAcceleration * timeStep)
        mPosition = mPosition + (mLinearVelocity * timeStep)

        val angularAcceleration = mTorque * mInvMoment

        mAngularVelocity += angularAcceleration * timeStep
        mAngle += mAngularVelocity * timeStep

        mLinearVelocity = mLinearVelocity * mFriction
        mAngularVelocity *= mFriction

        updateUnitVectors()
    }

    /**
     * Update the half-width unit vectors
     * based on the current angle
     */
    protected fun updateUnitVectors(){
        val dx1:Double = Math.sin(mAngle + GameObject.QUARTER_CIRCLE)
        val dy1:Double = -Math.cos(mAngle + GameObject.QUARTER_CIRCLE)
        mUnitX = Vec(dx1, dy1)

        val dx2 = Math.sin(mAngle)
        val dy2 = -Math.cos(mAngle)
        mUnitY = Vec(dx2, dy2)
    }

    /**
     * Take a vector in the object's frame of reference
     * and convert it to a point in the world
     * @param v Local vector
     * @return Corresponding point in the world
     */
    public fun LocalVectorToWorldVector(v:Vec):Vec{
        val vx = mUnitX * v.x
        val vy = mUnitY * v.y

        return mPosition + vx + vy
    }

    /**
     * Apply a force to move an object towards a point
     * @param point Point to move towards
     * @param multiplier Scale the force. Use a negative value to move away from the point
     * @param maxForce Maximum length of force vector
     */
    public fun MoveTowardsPoint(point:Vec, multiplier:Double, maxForce:Double){
        var force = (point - mPosition) * multiplier

        force = force.Clamp(maxForce)

        ApplyForce(force, mPosition)
    }

    /**
     * Default draw function draws a small
     * circle around the object's position
     * @param canvas PhysicsView's canvas
     */
    open public fun Draw(canvas:Canvas){
        val p = Paint()
        p.setColor(Color.WHITE)
        canvas.drawCircle(mPosition.x.toFloat(), mPosition.y.toFloat(), 10.0f, p)
    }
}