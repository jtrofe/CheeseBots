package com.jtrofe.cheesebots.physics.controllers

import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.Vec
import com.jtrofe.cheesebots.physics.objects.Bot
import com.jtrofe.cheesebots.physics.objects.GameObject
import java.util.ArrayList

/**
 * Created by MAIN on 2/9/16.
 */
public class BotController(engine:Engine):Controller(engine){

    suppress("UNCHECKED_CAST")
    override fun Update(timeStep:Double){
        val botList:List<Bot> = mEngine.Bodies.filter{ it.type == GameObject.TYPE_BOT } as List<Bot>

        if(botList.isEmpty()) return


        val cheeseList = mEngine.Bodies.filter{ it.type == GameObject.TYPE_CHEESE }

        if(cheeseList.isEmpty()){
            val position_sum = botList.map{ it -> it.GetPosition()}
                                    .reduce{v1, v2 -> v1 + v2 }

            val center_of_mass = position_sum / botList.size()

            botList.forEach {
                it.State = Bot.STATE_WALKING

                it.MoveTowardsPoint(center_of_mass, 0.1, 50.0)

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
}