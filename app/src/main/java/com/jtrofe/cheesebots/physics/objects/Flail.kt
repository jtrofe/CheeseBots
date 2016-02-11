package com.jtrofe.cheesebots.physics.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/9/16.
 */
public class Flail(position:Vec, mass:Double,
                   val mRadius:Double, val mK:Double):GameObject(position, mass){

    public fun GetRadius():Double{
        return mRadius
    }

    public fun GetK():Double{
        return mK
    }

    public var HandlePoint:Vec = Vec(-1, -1)

    init{
        Type = GameObject.TYPE_FLAIL

        this.calculateMoment()
    }

    /**
     * Moment of inertia calculation for a disk
     */
    override fun calculateMoment() {
        mMoment = (mMass * mRadius * mRadius) / 2

        if(mMoment.equals(0)){
            mInvMoment = 0.0
        }else{
            mMoment = 1 / mMoment
        }
    }

    override fun Draw(canvas:Canvas){
        val p = Paint()
        p.setColor(Color.RED)

        canvas.drawCircle(mPosition.xf, mPosition.yf, mRadius.toFloat(), p)
    }
}