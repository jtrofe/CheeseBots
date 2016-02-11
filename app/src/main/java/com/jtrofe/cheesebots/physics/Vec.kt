package com.jtrofe.cheesebots.physics

import android.graphics.PointF
import java.util.Random

/**
 * Created by MAIN on 2/8/16.
 */
public data class Vec(var x:Double = 0.0, var y:Double = 0.0){

    var xf:Float = x.toFloat()
        private set
        get() = x.toFloat()

    var yf:Float = y.toFloat()
        private set
        get() = y.toFloat()

    var xi:Int = x.toInt()
        private set
        get() = x.toInt()

    var yi:Int = y.toInt()
        private set
        get() = y.toInt()


    companion object{
        public fun Random(max:Vec):Vec{
            val r:Random = Random()

            return Vec(max.x * r.nextDouble(), max.y * r.nextDouble())
        }
    }

    constructor(point:PointF): this(point.x.toDouble(), point.y.toDouble()){}
    constructor(x:Int, y:Int): this(x.toDouble(), y.toDouble()){}
    constructor(x:Float, y:Float): this(x.toDouble(), y.toDouble()){}

    //
    //  Operator overloads
    //
    public fun plus(v:Vec):Vec{
        return Vec(x + v.x, y + v.y)
    }

    public fun minus(v:Vec):Vec{
        return Vec(x - v.x, y - v.y)
    }

    public fun times(s:Double):Vec{
        return Vec(x * s, y * s)
    }

    public fun times(s:Int):Vec{
        return Vec(x * s.toDouble(), y * s.toDouble())
    }

    public fun div(s:Double):Vec{
        return Vec(x / s, y / s)
    }

    public fun div(s:Int):Vec{
        return Vec(x / s.toDouble(), y / s.toDouble())
    }

    public fun minus():Vec{
        return Vec(-x, -y)
    }

    //
    //  Calculations
    //
    public fun LengthSquared():Double{
        return x * x + y * y
    }

    public fun Length():Double{
        return Math.sqrt(LengthSquared())
    }

    //
    //  Other operations
    //
    public fun Dot(v2:Vec):Double{
        return x * v2.x + y * v2.y
    }

    public fun Normalize():Vec{
        if(x.equals(0) && y.equals(0)) return Vec()

        val length = Length()
        return this / length
    }

    public fun Clamp(maxLength:Double):Vec{
        if(LengthSquared() > (maxLength * maxLength)){
            return Normalize() * maxLength
        }

        return Vec(x, y)
    }
}