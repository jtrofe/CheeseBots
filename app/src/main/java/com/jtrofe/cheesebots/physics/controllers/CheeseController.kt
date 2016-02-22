package com.jtrofe.cheesebots.physics.controllers

import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.objects.Cheese
import com.jtrofe.cheesebots.physics.objects.GameObject

/**
 * Removes cheeses if they have been completely eaten
 */
public class CheeseController(engine:Engine):Controller(engine){

    suppress("UNCHECKED_CAST")
    override fun Update(timeStep:Double){
        val cheeseList = mEngine.Bodies.filter{ it.Type == GameObject.TYPE_CHEESE } as List<Cheese>

        cheeseList.filter{ it.GetAmountLeft().equals(0.0) }.forEach{
            mEngine.RemoveBody(it)

            mEngine.JitterController.StartJitter(30, 60.0)
        }
    }
}