package com.jtrofe.cheesebots.physics.controllers;

import com.jtrofe.cheesebots.physics.Engine;
import com.jtrofe.cheesebots.physics.objects.GameObject;
import com.jtrofe.cheesebots.physics.objects.Particle;

/**
 * Track the life of particles and remove them when their life runs out
 */
public class ParticleController extends Controller{

    public ParticleController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(double timeStep){

        for(GameObject obj:mEngine.Bodies){
            if(obj.Type != GameObject.TYPE_PARTICLE) continue;

            Particle p = (Particle) obj;

            p.LifeSpan --;

            if(p.LifeSpan <= 0){
                mEngine.RemoveBody(p);
            }
        }
    }
}
