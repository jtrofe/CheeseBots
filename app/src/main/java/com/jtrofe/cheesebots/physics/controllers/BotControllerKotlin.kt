package com.jtrofe.cheesebots.physics.controllers

import android.graphics.Color
import com.jtrofe.cheesebots.GameApp
import com.jtrofe.cheesebots.physics.EngineKotlin
import com.jtrofe.cheesebots.physics.VecKotlin
import com.jtrofe.cheesebots.physics.objects.BotKotlin
import com.jtrofe.cheesebots.physics.objects.CheeseKotlin
import com.jtrofe.cheesebots.physics.objects.GameObjectKotlin
import com.jtrofe.cheesebots.physics.objects.ParticleKotlin
import java.util.ArrayList
import java.util.Random

/**
 * Direct bots towards cheese and make them eat it
 */
public class BotControllerKotlin{//(engine: EngineKotlin): ControllerKotlin(engine){
/*
    suppress("UNCHECKED_CAST")
    override fun Update(timeStep:Double){
        val botList:List<BotKotlin> = mEngine.Bodies.filter{ it.Type == GameObjectKotlin.TYPE_BOT } as List<BotKotlin>

        if(botList.isEmpty()) return

        val cheeseList = mEngine.Bodies.filter{ it.Type == GameObjectKotlin.TYPE_CHEESE } as List<CheeseKotlin>

        if(cheeseList.isEmpty()){
            val position_sum = botList.map{ it -> it.GetPosition()}
                                    .reduce{v1, v2 -> v1 + v2 }

            val center_of_mass = position_sum / botList.size()

            botList.forEach {
                it.State = BotKotlin.STATE_WALKING

                val from_center = it.GetPosition() - center_of_mass

                //TODO remove the minus sign
                it.SteerToAlign(-from_center.Normalize())

                //TODO make 0.1 into -0.1 so they flee the center
                it.MoveTowardsPoint(center_of_mass, 0.1, it.GetMass())

                avoidObjects(it, botList)

                checkHealth(it)
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

                checkHealth(it)
            }
        }
    }

    private fun checkHealth(b: BotKotlin){
        if(b.IsAlive()) return

        mEngine.RemoveBody(b)

        val rnd = Random()

        if(mEngine.Bodies.filter{ it.Type == GameObjectKotlin.TYPE_PARTICLE }.size() < 100){

            val particles = (b.GetMass() / 3).toInt()
            for(i in 0..particles){
                var v = VecKotlin.Random(VecKotlin(40, 40))
                v = v - VecKotlin(20, 20)

                val c = if(rnd.nextBoolean()){ b.SecondaryColor }else{ b.MainColor }
                val p = ParticleKotlin(b.GetPosition(), v, c, 40)

                mEngine.AddBody(p)
            }
        }

        GameApp.CurrentGame.OnBotDestroyed()

        mEngine.JitterController.StartJitter(20)
    }

    private fun avoidObjects(b: BotKotlin, objects:List<GameObjectKotlin>){
        var push_vec = VecKotlin()

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


    private fun getNearestCheese(b: BotKotlin, cheeseList:List<CheeseKotlin>):closestCheese{
        var closest_distance = Double.MAX_VALUE
        var closest_cheese = cheeseList[0]
        var cheese_direction = VecKotlin(1, 0)
        var goal_length = 10.0

        val BACK_OFF_DISTANCE = b.GetHalfHeight()

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

    private fun attackCheese(b: BotKotlin, c:closestCheese){

        val cheese_vector = c.cheese.GetPosition() - b.GetPosition()


        if(cheese_vector.Length() < c.cheese.GetRadius() + b.GetHalfHeight() + 10){
            c.cheese.Eat(b.GetEatingSpeed())

            if(b.State != BotKotlin.STATE_EATING){
                b.State = BotKotlin.STATE_EATING
            }
        }else{
            if(b.State != BotKotlin.STATE_WALKING){
                b.State = BotKotlin.STATE_WALKING
            }
        }

        val force_vector = c.cheeseDirection * (c.goalLength * 0.3)

        val MAX_FORCE_MAGNITUDE = b.GetMass()

        b.ApplyForceToCenter(force_vector.Clamp(MAX_FORCE_MAGNITUDE))
    }

    data class closestCheese(val cheese: CheeseKotlin, val cheeseDirection: VecKotlin, val goalLength:Double)*/
}