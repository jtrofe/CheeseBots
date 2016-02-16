package com.jtrofe.cheesebots.game

import android.graphics.Bitmap
import android.graphics.Canvas
import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.PhysicsView
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.Bot
import com.jtrofe.cheesebots.physics.objects.Cheese
import com.jtrofe.cheesebots.physics.objects.Flail
import com.jtrofe.cheesebots.physics.objects.Particle
import java.util.ArrayList
import java.util.Random

/**
 * Created by MAIN on 2/10/16.
 */
open public class Game(private var mPhysicsView: PhysicsView){

    private var mEngine:Engine? = null
    private var mLandscape:Boolean = true

    public var TouchPoint:Vec = Vec(-1, -1)

    private var mInitialized:Boolean = false
    private var mLevelComplete:Boolean = false

    private var mScreenWidth:Int = 0
    private var mScreenHeight:Int = 0

    public fun IsInitialized():Boolean{
        return mInitialized
    }

    public fun IsLevelComplete():Boolean{
        return mLevelComplete
    }


    public fun IsLandscape():Boolean{
        return mLandscape
    }

    public fun SetPhysicsView(physicsView:PhysicsView){
        mPhysicsView = physicsView
    }

    public fun SetWorldSize(width:Int, height:Int){
        mScreenWidth = Math.max(width, height)
        mScreenHeight = Math.min(width, height)

        mLandscape = (width > height)
        mEngine?.WorldSize = Vec(mScreenWidth, mScreenHeight)
    }

    public var SpriteSheets:List<Bitmap> = ArrayList()


    init{
        mEngine = Engine(Vec(1500, 500), this)

    }

    open public fun Update(timeStep:Double){
        if(!mInitialized) return


        mEngine?.Step(timeStep)
    }

    public fun Draw(canvas:Canvas){
        mEngine?.Draw(canvas)
    }

    open public fun Initialize(){
        mInitialized = true
        mLevelComplete = false

        for(i in 0..10){
            val o = Bot(Vec.Random(Vec(1000.0, 500.0)), 50.0, 100, 60, 0.1)

            mEngine?.AddBody(o)
        }

        val c: Cheese = Cheese(Vec(400, 900), 50.0)

        mEngine?.AddBody(c)

        val f: Flail = Flail(Vec(200, 200), 20.0, 50.0, 0.5)
        mEngine?.AddBody(f)
    }

    open public fun OnBotDestroyed(){
        val rnd = Random()

        val x:Double
        val y:Double

        if(rnd.nextBoolean()){
            x = rnd.nextDouble() * (mScreenWidth + 200) - 100.0

            y = if(rnd.nextBoolean()){ mScreenHeight + 100.0 }else{ -100.0 }
        }else{
            y = rnd.nextDouble() * (mScreenHeight + 200) - 100.0

            x = if(rnd.nextBoolean()){ mScreenWidth + 100.0 }else{ -100.0 }
        }

        val b = Bot(Vec(x, y), 50.0, 100, 60, 0.1)

        mEngine?.AddBody(b)
    }
}