package com.jtrofe.cheesebots.physics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.jtrofe.cheesebots.game.Game
import com.jtrofe.cheesebots.physics.controllers.*
import com.jtrofe.cheesebots.physics.objects.GameObject
import java.util.ArrayList
import java.util.Random

/**
 * Created by MAIN on 2/8/16.
 */
public class Engine(private var mWorldSize:Vec = Vec(100.0, 100.0), private var mGame: Game){

    public fun GetWorldSize():Vec{
        return mWorldSize
    }

    public fun SetWorldSize(worldSize:Vec){
        mWorldSize = worldSize

        TouchPoint = Vec(worldSize.x * 0.3, worldSize.y * 0.5)
    }

    private var mArrowCount = 1000.0
    public var TouchPoint:Vec = Vec()

    public fun ResetTouchPoint(){
        TouchPoint = Vec(mWorldSize.x * 0.3, mWorldSize.y * 0.5)
    }

    public fun GetGame():Game{
        return mGame
    }
    private var mControllers = ArrayList<Controller>()

    // For jitter effects
    public val JitterController:JitterControl = JitterControl(this)
    private var mOffset = Vec()

    public fun SetOffset(offset: Vec) {
        mOffset = offset
    }

    var CheeseAdded:Boolean = false

    var Bodies = ArrayList<GameObject>()
    var BodiesToAdd = ArrayList<GameObject>()
    var BodiesToRemove = ArrayList<GameObject>()



    init{
        mControllers.add(BotController(this))
        mControllers.add(CheeseController(this))
        mControllers.add(FlailController(this))
        mControllers.add(ParticleController(this))
    }


    public fun AddBody(obj:GameObject){
        BodiesToAdd.add(obj)
    }

    public fun RemoveBody(obj:GameObject){
        BodiesToRemove.add(obj)
    }

    private fun addWaiting(){
        Bodies.addAll(BodiesToAdd)

        BodiesToAdd = ArrayList<GameObject>()

        if(!CheeseAdded){
            if(Bodies.filter{ it.Type == GameObject.TYPE_CHEESE }.size() != 0){
                CheeseAdded = true
            }
        }
    }

    private fun removeWaiting(){
        Bodies.removeAll(BodiesToRemove)

        BodiesToRemove = ArrayList<GameObject>()
    }

    private fun resetForces(){
        Bodies.forEach { it.ClearForce () }
    }

    private fun computeForces(timeStep:Double){
        mControllers.forEach{ it.Update(timeStep) }
    }

    private fun updateObjects(timeStep:Double){
        Bodies.forEach{ it.Update(timeStep) }
    }

    /**
     * Run one step of the simulation
     */
    public fun Step(timeStep:Double){
        resetForces()

        computeForces(timeStep)

        updateObjects(timeStep)

        JitterController.Update()

        addWaiting()
        removeWaiting()
    }

    /**
     * Draw the current state
     */
    public fun Draw(canvas:Canvas){
        val canvasCount = canvas.save()

        //  If the surface is in landscape mode then rotate
        //  the canvas before drawing objects. Then restore
        //  its rotation after
        if(!mGame.IsLandscape()){
            canvas.rotate(90.0f, canvas.getWidth() / 2.0f, canvas.getWidth() / 2.0f)
        }
        canvas.translate(mOffset.xf, mOffset.yf)

        // TODO come up with a better background
        val p = Paint()
        p.setStyle(Paint.Style.STROKE)
        p.setColor(Color.WHITE)
        canvas.drawRect(50f, 50f, (mWorldSize.x - 50).toFloat(), (mWorldSize.y - 50).toFloat(), p)

        Bodies.forEach { it.Draw(canvas) }

        // Draw the TouchPoint
        drawTouchPoint(canvas)


        canvas.restoreToCount(canvasCount)
    }

    private fun drawTouchPoint(canvas:Canvas){
        if(mArrowCount <= 0) return

        mArrowCount --

        val alpha = (mArrowCount / 1000) * 255

        val tp = TouchPoint

        val paint = Paint()
        paint.setStyle(Paint.Style.STROKE)
        paint.setColor(Color.WHITE)
        paint.setAlpha(alpha.toInt())

        val MAX_LENGTH = 80f

        val MIN_LENGTH = MAX_LENGTH * 0.7f
        val MAX_WIDTH = 30f
        val MIN_WIDTH = 15f
        val p = Path()

        p.moveTo(tp.xf - MAX_LENGTH, tp.yf)
        p.lineTo(tp.xf - MIN_LENGTH, tp.yf + MAX_WIDTH)
        p.lineTo(tp.xf - MIN_LENGTH, tp.yf + MIN_WIDTH)
        p.lineTo(tp.xf - MIN_WIDTH, tp.yf + MIN_WIDTH)
        p.lineTo(tp.xf - MIN_WIDTH, tp.yf + MIN_LENGTH)
        p.lineTo(tp.xf - MAX_WIDTH, tp.yf + MIN_LENGTH)
        p.lineTo(tp.xf, tp.yf + MAX_LENGTH)
        p.lineTo(tp.xf + MAX_WIDTH, tp.yf + MIN_LENGTH)
        p.lineTo(tp.xf + MIN_WIDTH, tp.yf + MIN_LENGTH)
        p.lineTo(tp.xf + MIN_WIDTH, tp.yf + MIN_WIDTH)
        p.lineTo(tp.xf + MIN_LENGTH, tp.yf + MIN_WIDTH)
        p.lineTo(tp.xf + MIN_LENGTH, tp.yf + MAX_WIDTH)
        p.lineTo(tp.xf + MAX_LENGTH, tp.yf)
        p.lineTo(tp.xf + MIN_LENGTH, tp.yf - MAX_WIDTH)
        p.lineTo(tp.xf + MIN_LENGTH, tp.yf - MIN_WIDTH)
        p.lineTo(tp.xf + MIN_WIDTH, tp.yf - MIN_WIDTH)
        p.lineTo(tp.xf + MIN_WIDTH, tp.yf - MIN_LENGTH)
        p.lineTo(tp.xf + MAX_WIDTH, tp.yf - MIN_LENGTH)
        p.lineTo(tp.xf, tp.yf - MAX_LENGTH)
        p.lineTo(tp.xf - MAX_WIDTH, tp.yf - MIN_LENGTH)
        p.lineTo(tp.xf - MIN_WIDTH, tp.yf - MIN_LENGTH)
        p.lineTo(tp.xf - MIN_WIDTH, tp.yf - MIN_WIDTH)
        p.lineTo(tp.xf - MIN_LENGTH, tp.yf - MIN_WIDTH)
        p.lineTo(tp.xf - MIN_LENGTH, tp.yf - MAX_WIDTH)
        p.lineTo(tp.xf - MAX_LENGTH, tp.yf)

        canvas.drawPath(p, paint)
    }

    public inner class JitterControl(private val mEngine: Engine) {
        private var mCountdownStart = 0
        private var mCountdown = 0

        private var mMaxJitter = 10.0

        private val rnd:Random

        init{

            this.rnd = Random()
        }

        public fun StartJitter(jitterTime: Int) {
            mCountdownStart = jitterTime
            mCountdown = jitterTime
            mMaxJitter = 10.0
        }


        public fun StartJitter(jitterTime: Int, maxJitter: Double) {
            mCountdownStart = jitterTime
            mCountdown = jitterTime
            mMaxJitter = maxJitter
        }

        public fun Update() {
            if (mCountdown > 0) {
                val p = mCountdown.toDouble() / mCountdownStart.toDouble()

                val jitter_amount = p * mMaxJitter

                val angle = Math.PI * 2 * rnd.nextDouble()

                val dx = Math.sin(angle)
                val dy = -Math.cos(angle)

                val offset = Vec(dx * jitter_amount, dy * jitter_amount)

                mEngine.SetOffset(offset)

                mCountdown --
            } else {
                mEngine.SetOffset(Vec())
            }
        }
    }

}