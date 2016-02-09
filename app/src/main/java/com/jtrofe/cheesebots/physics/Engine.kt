package com.jtrofe.cheesebots.physics

import android.graphics.Canvas
import com.jtrofe.cheesebots.physics.controllers.BotController
import com.jtrofe.cheesebots.physics.controllers.Controller
import com.jtrofe.cheesebots.physics.objects.GameObject
import java.util.ArrayList

/**
 * Created by MAIN on 2/8/16.
 */
public class Engine(var WorldSize:Vec = Vec(100.0, 100.0), var Surface:PhysicsView){

    private var mControllers = ArrayList<Controller>()
    var Initialized:Boolean = false
    var LevelComplete:Boolean = false

    var Bodies = ArrayList<GameObject>()
    var BodiesToAdd = ArrayList<GameObject>()
    var BodiesToRemove = ArrayList<GameObject>()

    var TouchPoint:Vec = Vec(-1, -1)

    init{
        mControllers.add(BotController(this))
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
        if(false){ //TODO replace with game surface IsLandscape
            canvas.rotate(90.0f, canvas.getWidth() / 2.0f, canvas.getWidth() / 2.0f)
        }

        Bodies.forEach { it.Draw(canvas) }

        canvas.restoreToCount(canvasCount)
    }
}