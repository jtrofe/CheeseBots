package com.jtrofe.cheesebots.physics.controllers

import android.graphics.Color
import com.jtrofe.cheesebots.GameApplication
import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.Bot
import com.jtrofe.cheesebots.physics.objects.Flail
import com.jtrofe.cheesebots.physics.objects.GameObject
import com.jtrofe.cheesebots.physics.objects.Particle
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
        manifolds.forEach @mainLoop{
            val flail = it.flail
            val flail_position = flail.GetPosition()
            val bot_vertices = it.bot.GetVertices()

            // Get closest vertex to flail center
            val closest_vertex = Vec.GetClosestToPoint(bot_vertices, flail_position)

            // Get axes
            val separating_axes = arrayListOf(
                    -it.bot.GetUnitX(),
                    -it.bot.GetUnitY(),
                    -(flail_position - closest_vertex).Normalize()
                )

            // Get vectors from the center of the flail to each vertex on the bot
            val vectors_to_corners = bot_vertices.map{ it -> it - flail_position}

            var result = satResult(false)

            // For each axis, test for overlap. If there is overlap,
            // see if this is the axis of least penetration
            separating_axes.forEach {
                val r = SAT(flail, vectors_to_corners, it)
                if(!r.penetrating) return@mainLoop

                if(Math.abs(r.overlap) < Math.abs(result.overlap)) result = r.copy()
            }

            // This is the axis of least penetration
            var axis = result.axis

            // If the axis is pointing away from the center of the bot, negate it
            val to_bot_center = it.bot.GetPosition() - flail_position
            if(to_bot_center.Dot(axis) < 0) axis = -axis

            val point_of_collision = flail_position + (axis * flail.GetRadius())
            axis = axis * -result.overlap

            val RESOLVE_FORCE = 40.0
            // TODO If flail is not a plow, apply a force to it
            flail.ApplyForce(axis * RESOLVE_FORCE, point_of_collision)
            it.bot.ApplyForce(axis * -RESOLVE_FORCE, point_of_collision)

            // Damage the bot
            val DAMAGE_MULTIPLIER = 1.0 / 30.0

            val speed = flail.GetLinearVelocity().Length()
            val momentum = flail.GetMass() * speed
            val damage = momentum * DAMAGE_MULTIPLIER

            val dp = axis.Normalize().Dot(Vec(1, 0))
            flail.ApplyTorque(dp * speed * 0.5)

            if(damage > 0.5){
                it.bot.ApplyDamage(damage)
            }

            if(speed > 10){
                var v = Vec.Random(Vec(40, 40)) - Vec(20, 20)
                var p = Particle(point_of_collision, v, Color.YELLOW, 50)
                mEngine.AddBody(p)

                v = Vec.Random(Vec(40, 40)) - Vec(20, 20)
                p = Particle(point_of_collision, v, Color.YELLOW, 50)
                mEngine.AddBody(p)

                v = Vec.Random(Vec(40, 40)) - Vec(20, 20)
                p = Particle(point_of_collision, v, Color.RED, 50)
                mEngine.AddBody(p)
            }
        }
    }


    /**
     * Project a flail and bot onto an axis and determine if
     * their shadows overlap
     * @param flail The flail
     * @param vectors_to_corners Vectors pointing from the center of the flail to each vertex of the bot
     * @param axis The axis to project onto
     * @return 4 floats. The first float is 0 if they are not overlapping and 1 if they are. The
     *      second is the amount of overlap, if any. The last two are the axis coordinates
     */
    private fun SAT(flail:Flail, vectors_to_corners:List<Vec>, axis:Vec):satResult{
        val flail_min = -flail.GetRadius()
        val flail_max = -flail_min

        val p0 = vectors_to_corners[0].Dot(axis)
        val p1 = vectors_to_corners[1].Dot(axis)
        val p2 = vectors_to_corners[2].Dot(axis)
        val p3 = vectors_to_corners[3].Dot(axis)

        val bot_min = GameApplication.min(p0, p1, p2, p3)
        val bot_max = GameApplication.max(p0, p1, p2, p3)

        val overlapping = Math.max(flail_min, bot_min) <= Math.min(flail_max, bot_max)

        if(!overlapping){
            return satResult(false)
        }else{
            val penetration_depth = Math.min(bot_max, flail_max) - Math.max(bot_min, flail_min)

            return satResult(true, penetration_depth, axis.copy())
        }

    }

    private data class satResult(var penetrating:Boolean, var overlap:Double = Double.MAX_VALUE, var axis:Vec = Vec())

    private data class manifold(val flail:Flail, val bot:Bot)
}