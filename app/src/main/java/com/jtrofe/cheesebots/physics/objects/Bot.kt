package com.jtrofe.cheesebots.physics.objects

import android.graphics.*
import com.jtrofe.cheesebots.GameApp
import com.jtrofe.cheesebots.SpriteHandler
import com.jtrofe.cheesebots.physics.AABB
import com.jtrofe.cheesebots.physics.Vec
import java.util.ArrayList

/**
 * Created by MAIN on 2/9/16.
 */
public class Bot(position: Vec, mass:Double,
                 val w:Int, val h:Int,
                 private val mEatingSpeed:Double,
                 private val mSpriteSheetIndex:Int,
                 private val mTotalHealth:Double=100.0):GameObject(position, mass){

    companion object{
        public val STATE_WALKING:Int = 0
        public val STATE_EATING:Int = 1
    }

    public var CurrentFrame:Double = 0.0

    public var State:Int = STATE_WALKING
    private var mBoundRadius:Double = 0.0

    private var mHealthPoints = mTotalHealth

    public fun GetBoundRadius():Double{
        return mBoundRadius
    }

    public fun GetEatingSpeed():Double{
        return mEatingSpeed
    }

    public fun ApplyDamage(damage:Double){
        mHealthPoints -= damage;
    }

    public fun IsAlive():Boolean{
        return mHealthPoints > 0
    }

    init{
        Type = GameObject.TYPE_BOT

        mHalfWidth = w.toDouble ()/ 2
        mHalfHeight = h.toDouble ()/ 2

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

        if (mMoment.equals(0.0)){
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

        val minX = GameApp.min(vertices[0].x, vertices[1].x, vertices[2].x, vertices[3].x);
        val minY = GameApp.min(vertices[0].y, vertices[1].y, vertices[2].y, vertices[3].y);
        val maxX = GameApp.max(vertices[0].x, vertices[1].x, vertices[2].x, vertices[3].x);
        val maxY = GameApp.max(vertices[0].y, vertices[1].y, vertices[2].y, vertices[3].y);

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
        val r = Rect((mPosition.x - mHalfWidth).toInt(),
                    (mPosition.y.toInt() - mHalfHeight).toInt(),
                    (mPosition.x.toInt() + mHalfWidth).toInt(),
                    (mPosition.y.toInt() + mHalfHeight).toInt())

        return r
    }

    private fun getFrame():Rect{
        var frames:IntArray = SpriteHandler.WALK_FRAMES[mSpriteSheetIndex].clone()

        if(State == STATE_EATING) frames = SpriteHandler.EAT_FRAMES[mSpriteSheetIndex].clone()

        if(CurrentFrame >= frames.size()) CurrentFrame = 0.0;


        val f = CurrentFrame.toInt();

        val x = frames[f];

        val healthPercent = mHealthPoints / mTotalHealth

        var y:Int

        if(healthPercent > 0.66){
            y = 0
        }else if(healthPercent > 0.33){
            y = 1
        }else{
            y = 2
        }

        val w = (mHalfWidth * 2).toInt()
        val h = (mHalfHeight * 2).toInt()

        return Rect(x * w, y * h, (x * w) + w, (y * h) + h)
    }

    override fun Draw(canvas:Canvas){
        val src = getFrame();
        CurrentFrame += 0.2

        val dst = Rect((mPosition.x - mHalfWidth).toInt(), (mPosition.y - mHalfHeight).toInt(),
                (mPosition.x + mHalfWidth).toInt(), (mPosition.y + mHalfHeight).toInt())

        val saveCount = canvas.save()

        canvas.rotate(Math.toDegrees(mAngle).toFloat(), mPosition.xf, mPosition.yf)

        canvas.drawBitmap(GameApp.CurrentGame.SpriteSheets[mSpriteSheetIndex], src, dst, null)


        canvas.restoreToCount(saveCount)
    }
}