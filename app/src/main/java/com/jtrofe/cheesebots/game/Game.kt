package com.jtrofe.cheesebots.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.jtrofe.cheesebots.GameActivity
import com.jtrofe.cheesebots.SpriteHandler
import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.PhysicsView
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.*
import java.util.ArrayList
import java.util.Random

/**
 * Created by MAIN on 2/10/16.
 */
open public class Game(private var mPhysicsView: PhysicsView){

    private var mEngine:Engine? = null

    public var TouchPoint:Vec = Vec(-1, -1)

    //
    // Game variables
    //
    private var mInitialized:Boolean = false
    private var mComplete:Boolean = false
    private var mBotsDestroyed:Int = 0

    public fun IsComplete():Boolean{
        return mComplete
    }


    private var mScreenWidth:Int = 0
    private var mScreenHeight:Int = 0
    private var mLandscape:Boolean = true

    public var GameContext:GameActivity? = null



    public fun IsInitialized():Boolean{
        return mInitialized
    }

    public fun IsLevelComplete():Boolean{
        return mComplete
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
    private var mBotWeights = doubleArray(50.0, 0.0, 0.0)

    init{
        mEngine = Engine(Vec(1500, 500), this)
    }

    open public fun Update(timeStep:Double){
        if(!mInitialized) return

        if(!mComplete) GameContext?.SetScore(mBotsDestroyed.toString())

        mEngine?.Step(timeStep)

        if(mEngine?.Bodies?.filter{ it.Type == GameObject.TYPE_CHEESE }?.size() == 0 && !mComplete){
            OnComplete()
        }
    }

    public fun Draw(canvas:Canvas){
        mEngine?.Draw(canvas)
    }

    public fun OnComplete(){
        mComplete = true

        GameContext?.SetScore("")
        GameContext?.SetCompleteMessage("Congrats, you destroyed ${mBotsDestroyed} robots")
    }

    open public fun Initialize(){
        mInitialized = true
        mComplete = false
        mBotsDestroyed = 0

        for(i in 0..10){
            //val o = Bot(Vec.Random(Vec(1000.0, 500.0)), 50.0, 100, 60, 0.1, SpriteHandler.SHEET_SMALL_BOT)

            //mEngine?.AddBody(o)
            addBot()
        }

        val cheesePos = mEngine!!.WorldSize * 0.5

        val r = mEngine!!.WorldSize.y * 0.1

        val c: Cheese = Cheese(cheesePos, r, 400.0)

        mEngine?.AddBody(c)

        val f = Storage.GetSelectedFlail()

        mEngine?.AddBody(f)
    }

    private fun addBot(){
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
            2 -> {
                b = Bot(Vec(x, y), 200.0, 60, 220, 0.2, SpriteHandler.SHEET_GIANT_BOT, 500.0)
                b.SetImageSize(Vec(124, 220))
            }
            else -> b = Bot(Vec(x, y), 80.0, 85, 100, 0.1, SpriteHandler.SHEET_LARGE_BOT, 300.0)
        }


        mBotWeights[1] += 0.05
        mBotWeights[2] += 0.01


        mEngine?.AddBody(b)
    }

    open public fun OnBotDestroyed(){
        mBotsDestroyed ++

        addBot()

        if(mBotsDestroyed % 50 == 0){
            addBot()
        }
    }
}