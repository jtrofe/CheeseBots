package com.jtrofe.cheesebots.physics.controllers

import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.Bot
import com.jtrofe.cheesebots.physics.objects.Cheese
import com.jtrofe.cheesebots.physics.objects.GameObject
import java.util.ArrayList

/**
 * Created by MAIN on 2/9/16.
 */
public class BotController(engine:Engine):Controller(engine){

    suppress("UNCHECKED_CAST")
    override fun Update(timeStep:Double){
        val botList:List<Bot> = mEngine.Bodies.filter{ it.Type == GameObject.TYPE_BOT } as List<Bot>

        if(botList.isEmpty()) return


        val cheeseList = mEngine.Bodies.filter{ it.Type == GameObject.TYPE_CHEESE } as List<Cheese>

        if(cheeseList.isEmpty()){
            val position_sum = botList.map{ it -> it.GetPosition()}
                                    .reduce{v1, v2 -> v1 + v2 }

            val center_of_mass = position_sum / botList.size()

            botList.forEach {
                it.State = Bot.STATE_WALKING

                it.MoveTowardsPoint(center_of_mass, 0.1, 50.0)

                avoidObjects(it, botList)
            }
        }else{
            botList.forEach{
                // Find the cheese closest to the bot
                val cheese = getNearestCheese(it, cheeseList)
                val cheese_direction = cheese.cheeseDirection

                // Align the bot to face its target cheese
                it.SteerToAlign(cheese_direction)

                // If the bot is aligned with the cheese move towards it/eat it
                if(it.GetUnitY().Dot(cheese_direction) > 0.8){
                    attackCheese(it, cheese)
                }

                // Avoid other bots
                avoidObjects(it, botList)
            }
        }
    }

    private fun avoidObjects(b:Bot, objects:List<GameObject>){
        var push_vec = Vec()

        // Only get objects that are within 100 of the bot
        objects.forEach {
                if(it != b){
                    val d = it.GetPosition() - b.GetPosition()

                    if(d.LengthSquared() < 10000) {
                        push_vec = push_vec - d
                    }
                }
            }

        b.ApplyForceToCenter(push_vec)
    }


    private fun getNearestCheese(b:Bot, cheeseList:List<Cheese>):closestCheese{
        var closest_distance = Double.MAX_VALUE
        var closest_cheese = cheeseList[0]
        var cheese_direction = Vec(1, 0)
        var goal_length = 10.0

        val BACK_OFF_DISTANCE = b.GetBoundRadius() * 0.75

        cheeseList.forEach{
            val vector_to_center = it.GetPosition() - b.GetPosition()

            val back_off_distance = it.GetRadius() + BACK_OFF_DISTANCE

            val cheese_distance = vector_to_center.Length() - back_off_distance

            if(cheese_distance < closest_distance){
                closest_distance = cheese_distance
                closest_cheese = it

                cheese_direction = vector_to_center.Normalize()
                goal_length = cheese_distance
            }
        }

        return closestCheese(closest_cheese, cheese_direction, goal_length)
    }

    private fun attackCheese(b:Bot, c:closestCheese){
        val EATING_DISTANCE = b.GetBoundRadius() / 2

        if(c.goalLength < EATING_DISTANCE){
            c.cheese.Eat(b.GetEatingSpeed())

            if(b.State != Bot.STATE_EATING){
                b.State = Bot.STATE_EATING
            }
        }else{
            if(b.State != Bot.STATE_WALKING){
                b.State = Bot.STATE_WALKING
            }
        }

        val force_vector = c.cheeseDirection * (c.goalLength * 0.3)

        val MAX_FORCE_MAGNITUDE = 50.0

        b.ApplyForceToCenter(force_vector.Clamp(MAX_FORCE_MAGNITUDE))
    }

    data class closestCheese(val cheese: Cheese, val cheeseDirection:Vec, val goalLength:Double)
}