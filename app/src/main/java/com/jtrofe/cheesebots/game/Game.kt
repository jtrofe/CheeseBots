package com.jtrofe.cheesebots.game

import android.graphics.Bitmap
import android.graphics.Canvas
import com.jtrofe.cheesebots.SpriteHandler
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


    // Probabilities that each type of bot will be added once one is destroyed
    private var mBotWeights = doubleArray(100.0, 0.0)

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
            val o = Bot(Vec.Random(Vec(1000.0, 500.0)), 50.0, 100, 60, 0.1, SpriteHandler.SHEET_SMALL_BOT)

            mEngine?.AddBody(o)
        }

        val cheesePos = mEngine!!.WorldSize * 0.5

        val r = mEngine!!.WorldSize.y * 0.1

        val c: Cheese = Cheese(cheesePos, r, 400.0)

        mEngine?.AddBody(c)

        //val f: Flail = Flail(20.0, 50.0, 0.5)

        val f = Storage.GetSelectedFlail()

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


        val totalWeight = mBotWeights.reduce{ x, y -> x + y }

        var randIndex:Int = -1
        var rand = rnd.nextDouble() * totalWeight

        for(i in 0..(mBotWeights.size()-1)){
            rand -= mBotWeights[i]

            if(rand <= 0){
                randIndex = i
                break
            }
        }

        var b:Bot
        when(randIndex){
            0 -> b = Bot(Vec(x, y), 50.0, 100, 60, 0.1, SpriteHandler.SHEET_SMALL_BOT)
            1 -> b = Bot(Vec(x, y), 80.0, 85, 100, 0.1, SpriteHandler.SHEET_LARGE_BOT, 300.0)
            else -> b = Bot(Vec(x, y), 80.0, 85, 100, 0.1, SpriteHandler.SHEET_LARGE_BOT, 300.0)
        }


        mBotWeights[1] += 0.05


        mEngine?.AddBody(b)
    }
}