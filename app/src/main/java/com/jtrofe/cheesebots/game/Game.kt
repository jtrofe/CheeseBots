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

/**
 * Created by MAIN on 2/10/16.
 */
open public class Game(private var mPhysicsView: PhysicsView){

    private var mEngine:Engine? = null
    private var mLandscape:Boolean = true

    public var TouchPoint:Vec = Vec(-1, -1)

    private var mInitialized:Boolean = false
    private var mLevelComplete:Boolean = false

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
        val screenWidth = Math.max(width, height)
        val screenHeight = Math.min(width, height)

        mLandscape = (width > height)
        mEngine?.WorldSize = Vec(screenWidth, screenHeight)
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

        val o1: Particle = Particle(Vec(100, 100), Vec(10, 10))

        for(i in 0..10){
            val o = Bot(Vec.Random(Vec(1000.0, 500.0)), 20.0, 100, 60, 0.1)

            mEngine?.AddBody(o)
        }
        //val o2 = Bot(Vec(200, 100), 20.0, 100, 60, 10.0)

        val c: Cheese = Cheese(Vec(200, 900), 50.0)

        mEngine?.AddBody(c)

        val f: Flail = Flail(Vec(200, 200), 50.0, 20.0, 0.5)
        mEngine?.AddBody(f)

        mEngine?.AddBody(o1)
    }
}