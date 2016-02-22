package com.jtrofe.cheesebots.physics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView

import java.util.Random

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import com.jtrofe.cheesebots
import com.jtrofe.cheesebots.GameApp
import com.jtrofe.cheesebots.game.Game

import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.*
import java.util.ArrayList

import java.util.Random

/**

 */
public class PhysicsView(context: Context) : SurfaceView(context), Runnable {

    private var mHolder: SurfaceHolder? = null
    private var mIsRunning = false
    private var mGameThread: Thread? = null

    private var mIsLandscape: Boolean = false

    private var mPauseTime:Double = -1.0


    public fun IsLandscape(): Boolean {
        return mIsLandscape
    }

    private var mScreenSize:Vec = Vec(100.0, 100.0)


    //private var mEngine: Engine? = null
    private var mGame: Game? = null

    public fun SetSpriteSheet(spriteSheets:ArrayList<Bitmap>){
        mGame?.SpriteSheets = spriteSheets
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {}

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            val screenWidth = Math.max(width, height)
            val screenHeight = Math.min(width, height)
            mScreenSize = Vec(screenWidth, screenHeight)

            mIsLandscape = (width > height)

            mGame?.SetWorldSize(width, height)
            if(!mGame!!.IsInitialized()){
                mGame?.Initialize()
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }

    init{
        mHolder = getHolder()
        mHolder?.addCallback(surfaceCallback)

        if(GameApp.CurrentGame == null){
            mGame = Game(this)
            GameApp.CurrentGame = mGame
        }else{
            mGame = GameApp.CurrentGame
            mGame?.SetPhysicsView(this)
        }
        /*if (GameApplication.GameEngine == null) {
            mEngine = Engine(Vec(1000, 1500), this)
            GameApplication.Engine = mEngine
        }else{
            mEngine = GameApplication.Engine
            mEngine?.Surface = this
        }*/
    }


    /**
     * When the phone orientation is landscape, points
     * on the phone screen will have to be rotated to
     * be accurate
     */
    private fun translateCoordinates(point:Vec): Vec {
        if (IsLandscape()) {
            return point.copy()
        }
        val dx = point.x - (mScreenSize.y / 2)
        val dy = point.y - (mScreenSize.y / 2)

        val nx = (mScreenSize.y / 2) + dy
        val ny = (mScreenSize.y / 2) - dx

        return Vec(nx, ny)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchPoint = Vec(event.getX(), event.getY())
        val action = event.getAction()

        when (action){
            MotionEvent.ACTION_DOWN -> {
                mGame?.TouchPoint = translateCoordinates(touchPoint)
                //mEngine?.TouchPoint = translateCoordinates(touchPoint)

                return true
            }
            MotionEvent.ACTION_UP -> {
                mGame?.TouchPoint = Vec(-1, -1)
                //mEngine?.TouchPoint = Vec(-1, -1)

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mGame?.TouchPoint = translateCoordinates(touchPoint)
                //mEngine?.TouchPoint = translateCoordinates(touchPoint)

                return true
            }
        }

        return super<SurfaceView>.onTouchEvent(event)
    }

    /**
     * On lifecycle resume
     */
    public fun Resume() {
        mIsRunning = true
        mGameThread = Thread(this)
        mGameThread!!.start()

        /*if (!mPauseTime.equals(-1) && mEngine!!.HasTimeLimit) {
            val pauseLength = System.currentTimeMillis() - mPauseTime

            mEngine!!.StartTime += pauseLength
        }*/
    }

    /**
     * On lifecycle pause
     */
    public fun Pause() {
        mIsRunning = false
        mPauseTime = System.currentTimeMillis().toDouble()
        var retry = true
        while (retry) {
            try {
                mGameThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
                retry = true
            }

        }
    }

    /**
     * Run a step of the physics engine if it's initialized
     * and the game isn't over
     */
    private fun stepEngine() {
        val TIME_STEP = 0.8

        mGame?.Update(TIME_STEP)

        //if (mEngine!!.Initialized) {
        //    mEngine?.Step(TIME_STEP)
        //}
    }

    /**
     * Game loop. Handle frame rate and game updating
     */
    override fun run() {
        while (mIsRunning) {
            if (!mHolder!!.getSurface().isValid()) continue

            val started = System.currentTimeMillis()

            // Step the engine, draw the objects, and update the UI
            update()

            // Frames per second stuff
            val deltaTime = (System.currentTimeMillis() - started).toFloat()
            var sleepTime = (FRAME_PERIOD.toFloat() - deltaTime).toInt()
            if (sleepTime > 0) {
                try {
                    //gameThread.sleep(sleepTime);
                    Thread.sleep(sleepTime.toLong())
                } catch (e: InterruptedException) {
                    // ok
                }

            }
            while (sleepTime < 0) {
                stepEngine()
                sleepTime += FRAME_PERIOD
            }
        }
    }

    /**
     * Game frame update. Updates physics, canvas, and UI
     */
    private fun update() {
        // Run physics simulation
        stepEngine()

        // Draw objects
        val canvas = mHolder?.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(Color.BLACK)

            mGame?.Draw(canvas)
            //mEngine!!.Draw(canvas)

            mHolder?.unlockCanvasAndPost(canvas)
        }

        // Update user interface
        //updateUI()
    }



    private fun InitializeLevel() {
       /* mEngine!!.Initialized = true
        mEngine!!.LevelComplete = false

        val o1:Particle = Particle(Vec(100, 100), Vec(10, 10))

        for(i in 0..10){
            val o = Bot(Vec.Random(Vec(1000.0, 500.0)), 20.0, 100, 60, 0.1)

            mEngine?.AddBody(o)
        }
        //val o2 = Bot(Vec(200, 100), 20.0, 100, 60, 10.0)

        val c:Cheese = Cheese(Vec(200, 900), 50.0)

        mEngine?.AddBody(c)

        val f:Flail = Flail(Vec(200, 200), 50.0, 20.0, 0.5)
        mEngine?.AddBody(f)

        mEngine?.AddBody(o1)
        //mEngine?.AddBody(o2)*/
    }



    companion object {

        private val MAX_FPS = 40 //desired fps
        private val FRAME_PERIOD = 1000 / MAX_FPS
    }

}
