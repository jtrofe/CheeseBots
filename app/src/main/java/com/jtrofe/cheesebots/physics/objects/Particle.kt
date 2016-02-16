package com.jtrofe.cheesebots.physics.objects

import android.graphics.Canvas
import android.graphics.Paint
import com.jtrofe.cheesebots.physics.Vec

/**
 * A small particle for effects. Just has a color and a short lifespan
 */
public class Particle(position:Vec, velocity:Vec, color:Int, public var LifeSpan:Int):GameObject(position){

    var paint:Paint = Paint()

    init{
        Type = GameObject.TYPE_PARTICLE

        mLinearVelocity = velocity.copy()

        mFriction = 0.97

        paint.setColor(color)

        this.calculateMoment()
    }


    override fun Draw(canvas:Canvas){
        canvas.drawCircle(mPosition.xf, mPosition.yf, GameObject.PARTICLE_RADIUS.toFloat(), paint)
    }


}