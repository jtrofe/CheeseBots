package com.jtrofe.cheesebots.physics.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/9/16.
 */
public class Cheese(position:Vec, val mStartRadius:Double, val mStartAmount:Double = 100.0):GameObject(position){

    private var mAmountLeft:Double = mStartAmount

    private var mRadius:Double = mStartRadius

    public fun GetAmountLeft():Double{
        return mAmountLeft
    }

    public fun GetRadius():Double{
        return mRadius
    }

    init{
        Type = GameObject.TYPE_CHEESE
    }

    public fun Eat(amount:Double){
        mAmountLeft -= amount
        if(mAmountLeft < 0) mAmountLeft = 0.0

        mRadius = (mAmountLeft / mStartAmount) * mStartRadius;
    }

    override fun Draw(canvas:Canvas){
        val paint = Paint()
        paint.setColor(Color.YELLOW)
        paint.setStyle(Paint.Style.STROKE)

        canvas.drawCircle(mPosition.x.toFloat(), mPosition.y.toFloat(), mRadius.toFloat(), paint)
    }
}