package com.jtrofe.cheesebots.physics.objects

import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/8/16.
 */
public class Particle(position:Vec, velocity:Vec):GameObject(position){

    init{
        Type = GameObject.TYPE_PARTICLE

        mLinearVelocity = velocity.copy()

        this.calculateMoment()
    }
}