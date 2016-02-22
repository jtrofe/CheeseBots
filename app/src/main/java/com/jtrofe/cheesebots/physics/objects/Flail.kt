package com.jtrofe.cheesebots.physics.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.jtrofe.cheesebots.GameApp
import com.jtrofe.cheesebots.SpriteHandler
import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/9/16.
 */
public class Flail(mass:Double,
                   val mRadius:Double,
                   val mK:Double
                  ):GameObject(Vec(), mass){

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

        if(mMoment.equals(0.0)){
            mInvMoment = 0.0
        }else{
            mInvMoment = 1 / mMoment
        }
    }

    private fun getFlailSrc():Rect{
        return Rect(0, 0, 100, 100)
    }

    override fun Draw(canvas:Canvas){

        if(!HandlePoint.x.equals(-1.0) || !HandlePoint.y.equals(-1.0)){
            val attachPoint = LocalVectorToWorldVector(Vec(-mRadius, mRadius))

            val HANDLE_WIDTH = 4f
            val p = Paint()
            p.setColor(Color.WHITE)
            p.setStyle(Paint.Style.STROKE)
            p.setStrokeWidth(HANDLE_WIDTH)

            canvas.drawLine(HandlePoint.xf, HandlePoint.yf, attachPoint.xf, attachPoint.yf, p)
        }

        val src = getFlailSrc()
        val dst = Rect(mPosition.xi - mRadius.toInt(), mPosition.yi - mRadius.toInt(),
                       mPosition.xi + mRadius.toInt(), mPosition.yi + mRadius.toInt())

        val saveCount = canvas.save()

        canvas.rotate(Math.toDegrees(mAngle).toFloat(), mPosition.xf, mPosition.yf)
        canvas.drawBitmap(GameApp.CurrentGame.SpriteSheets[SpriteHandler.SHEET_FLAIL], src, dst, null)

        canvas.restoreToCount(saveCount)
    }
}