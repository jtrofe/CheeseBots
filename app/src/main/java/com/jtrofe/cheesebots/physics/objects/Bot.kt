package com.jtrofe.cheesebots.physics.objects

import android.graphics.*
import com.jtrofe.cheesebots.GameApplication
import com.jtrofe.cheesebots.physics.AABB
import com.jtrofe.cheesebots.physics.Vec
import java.util.ArrayList

/**
 * Created by MAIN on 2/9/16.
 */
public class Bot(position: Vec, mass:Double,
                 val w:Int, val h:Int,
                 private val mEatingSpeed:Double):GameObject(position, mass){

    companion object{
        public val STATE_WALKING:Int = 0
        public val STATE_EATING:Int = 1
    }

    public var CurrentFrame:Double = 0.0

    public var State:Int = STATE_WALKING
    private var mBoundRadius:Double = 0.0

    public fun GetBoundRadius():Double{
        return mBoundRadius
    }

    public fun GetEatingSpeed():Double{
        return mEatingSpeed
    }

    init{
        Type = GameObject.TYPE_BOT

        mHalfWidth = w / 2
        mHalfHeight = h / 2

        mBoundRadius = Math.sqrt((mHalfWidth * mHalfWidth + mHalfHeight * mHalfHeight).toDouble())

        this.calculateMoment()

        mAngle = Math.PI / 4

        this.updateUnitVectors()
    }

    /**
     * This is the formula to calculate moment of
     * inertia for a rectangle
     */
    override fun calculateMoment() {
        val w = mHalfWidth * 2
        val h = mHalfHeight * 2

        mMoment = (mMass * (w * w + h * h)) / 12

        if (mMoment.equals(0)){
            mInvMoment = 0.0
        }else{
            mInvMoment = 1 / mMoment
        }
    }

    public fun GetVertices(): ArrayList<Vec>{
        val vx = mUnitX * mHalfWidth.toDouble()
        val vy = mUnitY * mHalfHeight.toDouble()
        val p = mPosition

        val l = ArrayList<Vec>()

        l.add(p + vx + vy)
        l.add(p + vx - vy)
        l.add(p - vx - vy)
        l.add(p - vx + vy)

        return l
    }

    /**
     * Get the smallest AABB that contains the bot
     */
    public fun GetBounds(): AABB{
        val vertices = GetVertices()

        val minX = GameApplication.min(vertices[0].x, vertices[1].x, vertices[2].x, vertices[3].x);
        val minY = GameApplication.min(vertices[0].y, vertices[1].y, vertices[2].y, vertices[3].y);
        val maxX = GameApplication.max(vertices[0].x, vertices[1].x, vertices[2].x, vertices[3].x);
        val maxY = GameApplication.max(vertices[0].y, vertices[1].y, vertices[2].y, vertices[3].y);

        return AABB(Vec(minX, minY), Vec(maxX, maxY))
    }

    /**
     * Apply torque to the bot to align the
     * unit vector y with some other unit vector
     * @param v Vector to align with
     */
    public fun SteerToAlign(v:Vec){
        val dp_y = v.Dot(mUnitY)
        val amount_oriented = 2 - ((dp_y * dp_y) + 1)

        val dp_x = v.Dot(mUnitX)

        val turn_direction = if(dp_x < 0) -1 else 1

        val MAX_TURN_TORQUE = 0.6 * turn_direction * mMoment

        ApplyTorque(MAX_TURN_TORQUE * amount_oriented)
    }

    private fun getRect():Rect{
        val r = Rect(mPosition.x.toInt() - mHalfWidth,
                    mPosition.y.toInt() - mHalfHeight,
                    mPosition.x.toInt() + mHalfWidth,
                    mPosition.y.toInt() + mHalfHeight)

        return r
    }

    private fun getFrame():Rect{
        val f = CurrentFrame.toInt()

        return Rect(f * 100, 0, f * 100 + 100, 60)
    }

    override fun Draw(canvas:Canvas){
        var src = Rect(0, 0, mHalfWidth * 2, mHalfHeight * 2)
        when(State){
            STATE_WALKING -> {
                if(CurrentFrame >= 3) CurrentFrame = 0.0

                src = getFrame()
                CurrentFrame += 0.1
            }
            STATE_EATING -> {
                if(CurrentFrame < 3 || CurrentFrame >= 5) CurrentFrame = 3.0

                src = getFrame()
                CurrentFrame += 0.1
            }
        }

        val dst = Rect(mPosition.xi - mHalfWidth, mPosition.yi - mHalfHeight,
                    mPosition.xi + mHalfWidth, mPosition.yi + mHalfHeight)

        val saveCount = canvas.save()

        canvas.rotate(Math.toDegrees(mAngle).toFloat(), mPosition.xf, mPosition.yf)

        canvas.drawBitmap(GameApplication.CurrentGame.SpriteSheets[0], src, dst, null)


        canvas.restoreToCount(saveCount)
    }
}