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
                 val eatingSpeed:Double):GameObject(position, mass){

    companion object{
        public val STATE_WALKING:Int = 0
        public val STATE_EATING:Int = 1
    }

    public var State:Int = STATE_WALKING
    private var boundRadius:Double = 0.0

    init{
        type = GameObject.TYPE_BOT

        halfWidth = w / 2
        halfHeight = h / 2

        boundRadius = Math.sqrt((halfWidth * halfWidth + halfHeight * halfHeight).toDouble())

        this.calculateMoment()

        angle = Math.PI / 4

        this.updateUnitVectors()
    }

    /**
     * This is the formula to calculate moment of
     * inertia for a rectangle
     */
    override fun calculateMoment() {
        val w = halfWidth * 2
        val h = halfHeight * 2

        moment = (mass * (w * w + h * h)) / 12

        if (moment.equals(0)){
            invMoment = 0.0
        }else{
            invMoment = 1 / moment
        }
    }

    public fun GetVertices(): ArrayList<Vec>{
        val vx = unitX * halfWidth.toDouble()
        val vy = unitY * halfHeight.toDouble()
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
        val dp_y = v.Dot(unitY)
        val amount_oriented = 2 - ((dp_y * dp_y) + 1)

        val dp_x = v.Dot(unitX)

        val turn_direction = if(dp_x < 0) -1 else 1

        val MAX_TURN_TORQUE = 0.6 * turn_direction * moment

        ApplyTorque(MAX_TURN_TORQUE * amount_oriented)
    }

    private fun getRect():Rect{
        val r = Rect(mPosition.x.toInt() - halfWidth,
                    mPosition.y.toInt() - halfHeight,
                    mPosition.x.toInt() + halfWidth,
                    mPosition.y.toInt() + halfHeight)

        return r
    }

    override fun Draw(canvas:Canvas){

        val saveCount = canvas.save()

        canvas.rotate(Math.toDegrees(angle).toFloat(), mPosition.x.toFloat(), mPosition.y.toFloat())

        val p = Paint()
        p.setColor(Color.argb(80, 255, 255, 0))

        canvas.drawRect(getRect(), p)

        canvas.restoreToCount(saveCount)
    }
}