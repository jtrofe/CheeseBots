package com.jtrofe.cheesebots.game.controllers;

import com.jtrofe.cheesebots.game.gameobjects.GameObject;
import com.jtrofe.cheesebots.game.gameobjects.Particle;
import com.jtrofe.cheesebots.game.physics.Engine;

import java.util.List;

/**
 * Created by MAIN on 1/24/16
 */
public class ParticleController extends Controller {

    public ParticleController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(float timeStep){
        List<GameObject> particleList = mEngine.GetType(GameObject.TYPE_PARTICLE);

        for(GameObject particleObject:particleList){
            Particle particle = (Particle) particleObject;

            particle.LifeSpan --;

            if(particle.LifeSpan == 0){
                mEngine.RemoveBody(particle);
            }
        }
    }
}
