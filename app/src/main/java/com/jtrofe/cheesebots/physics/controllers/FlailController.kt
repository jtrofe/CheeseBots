package com.jtrofe.cheesebots.physics.controllers

import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.Bot
import com.jtrofe.cheesebots.physics.objects.Flail
import com.jtrofe.cheesebots.physics.objects.GameObject
import java.util.ArrayList

/**
 * Created by MAIN on 2/9/16.
 */
public class FlailController(engine:Engine):Controller(engine){

    suppress("UNCHECKED_CAST")
    override fun Update(timeStep:Double){
        if(mEngine.GetGame().IsLevelComplete()) return

        val flailList = mEngine.Bodies.filter { it.Type == GameObject.TYPE_FLAIL } as List<Flail>
        if(flailList.isEmpty()) return

        val flail = flailList[0]
        flail.HandlePoint = mEngine.GetGame().TouchPoint.copy()

        val handle_point = flail.HandlePoint.copy()
        if(handle_point.x != -1.0 || handle_point.y != -1.0){
            val radius = flail.GetRadius()

            val attach_point = flail.LocalVectorToWorldVector(Vec(-radius, radius))

            val X = handle_point - attach_point

            val MAX_FORCE = 300.0

            val force = (X * flail.GetK()).Clamp(MAX_FORCE)

            flail.ApplyForce(force, attach_point)
        }


        val botList = mEngine.Bodies.filter{ it.Type == GameObject.TYPE_BOT } as List<Bot>


        val possibleCollisions = broadPhase(flail, botList)

        narrowPhase(possibleCollisions)
    }

    /**
     * Perform broad-phase collision detection between
     * the flail and the bots by comparing the overlap
     * between the flail and the bots' bounding circles
     */
    private fun broadPhase(flail:Flail, botList:List<Bot>):List<manifold>{
        var possible_collisions = ArrayList<manifold>()

        botList.forEach {
            val bot_radius = it.GetBoundRadius()
            val min_distance = bot_radius + flail.GetRadius()

            val vector_to_bot_center = it.GetPosition() - flail.GetPosition()

            if(vector_to_bot_center.LengthSquared() < min_distance * min_distance){
                possible_collisions.add(manifold(flail, it))
            }
        }

        return possible_collisions
    }


    private fun narrowPhase(manifolds:List<manifold>){

    }

    private data class manifold(val flail:Flail, val bot:Bot)
}