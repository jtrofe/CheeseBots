package com.jtrofe.cheesebots.physics.controllers

import com.jtrofe.cheesebots.physics.Engine
import com.jtrofe.cheesebots.physics.objects.GameObject
import com.jtrofe.cheesebots.physics.objects.Particle

/**
 * Created by MAIN on 2/11/16.
 */
public class ParticleController(engine:Engine):Controller(engine){

    override fun Update(timeStep:Double){

        mEngine.Bodies.filter{ it.Type == GameObject.TYPE_PARTICLE }
            .forEach{
                val p = it as Particle

                p.LifeSpan --

                if(p.LifeSpan <= 0){
                    mEngine.RemoveBody(p)
                }
            }
    }
}