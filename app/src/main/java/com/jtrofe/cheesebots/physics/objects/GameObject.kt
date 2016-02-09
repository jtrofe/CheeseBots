package com.jtrofe.cheesebots.physics.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/8/16.
 */
public abstract class GameObject(protected var mPosition:Vec,
                                 protected val mass:Double = GameObject.PARTICLE_MASS){

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

    public var type:Int = GameObject.TYPE_PARTICLE

    //
    //  Physical properties
    //
    protected var invMass:Double = 1 / mass
    protected var moment:Double = 0.0
    protected var invMoment:Double = 0.0
    protected var friction:Double = 0.9

    protected var linearVelocity:Vec = Vec()
    protected var force:Vec = Vec()

    protected var angle:Double = 0.0
    protected var angularVelocity:Double = 0.0
    protected var torque:Double = 0.0

    protected var unitX:Vec = Vec(1.0, 0.0)
    protected var unitY:Vec = Vec(0.0, -1.0)

    protected var halfWidth:Int = 0
    protected var halfHeight:Int = 0

    public fun GetPosition():Vec{
        return mPosition
    }


    open fun calculateMoment(){
        moment = (mass * PARTICLE_RADIUS * PARTICLE_RADIUS) / 2;

        if(moment.equals(0.0)){
            invMoment = 0.0
        }else{
            invMoment = 1.0 / moment
        }
    }

    /**
     * Simple, add some torque to the object
     * @param appliedTorque Amount of torque to add
     */
    public fun ApplyTorque(appliedTorque:Double){
        torque += appliedTorque
    }

    /**
     * Apply an impulse at a world point. Immediately
     * change linear and angular velocity
     * @param appliedImpulse Impulse to apply
     * @param appliedPoint The world point where the force will be applied
     */
    public fun ApplyImpulse(appliedImpulse:Vec, appliedPoint:Vec){
        linearVelocity = linearVelocity + (appliedImpulse * invMass)

        val r = appliedPoint - mPosition
        angularVelocity += (r.x * appliedImpulse.y - r.y * appliedImpulse.x) * invMoment
    }

    /**
     * Apply a force to the body at a world point
     * @param appliedForce The force vector to apply
     * @param appliedPoint The world point where the force will be applied
     */
    public fun ApplyForce(appliedForce:Vec, appliedPoint:Vec){
        force = force + appliedForce

        val r = appliedPoint - mPosition
        torque += r.x * appliedForce.y - r.y * appliedForce.x
    }

    public fun ApplyForceToCenter(appliedForce:Vec){
        force = force + appliedForce
    }

    public fun ClearForce(){
        force = Vec()
        torque = 0.0
    }

    public fun Update(timeStep:Double){
        val linearAcceleration:Vec = force * invMass

        linearVelocity = linearVelocity + (linearAcceleration * timeStep)
        mPosition = mPosition + (linearVelocity * timeStep)

        val angularAcceleration = torque * invMoment

        angularVelocity += angularAcceleration * timeStep
        angle += angularVelocity * timeStep

        linearVelocity = linearVelocity * friction
        angularVelocity *= friction

        updateUnitVectors()
    }

    /**
     * Update the half-width unit vectors
     * based on the current angle
     */
    protected fun updateUnitVectors(){
        val dx1:Double = Math.sin(angle + GameObject.QUARTER_CIRCLE)
        val dy1:Double = -Math.cos(angle + GameObject.QUARTER_CIRCLE)
        unitX = Vec(dx1, dy1)

        val dx2 = Math.sin(angle)
        val dy2 = -Math.cos(angle)
        unitY = Vec(dx2, dy2)
    }

    /**
     * Take a vector in the object's frame of reference
     * and convert it to a point in the world
     * @param v Local vector
     * @return Corresponding point in the world
     */
    public fun LocalVectorToWorldVector(v:Vec):Vec{
        val vx = unitX * v.x
        val vy = unitY * v.y

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

    open public fun Draw(canvas:Canvas){
        val p = Paint()
        p.setColor(Color.WHITE)
        canvas.drawCircle(mPosition.x.toFloat(), mPosition.y.toFloat(), 10.0f, p)
    }
}