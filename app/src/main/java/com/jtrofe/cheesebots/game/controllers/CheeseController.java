package com.jtrofe.cheesebots.game.controllers;

import com.jtrofe.cheesebots.game.gameobjects.Cheese;
import com.jtrofe.cheesebots.game.gameobjects.GameObject;
import com.jtrofe.cheesebots.game.physics.Engine;

import java.util.List;

/**
 * Created by MAIN on 1/23/16
 */
public class CheeseController extends Controller{

    public CheeseController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(float timeStep){
        List<GameObject> cheese = mEngine.GetType(GameObject.TYPE_CHEESE);

        for(GameObject cheese_object:cheese){
            Cheese c = (Cheese) cheese_object;
            if(c.GetAmountLeft() == 0){
                mEngine.RemoveBody(c);

                mEngine.mJitterControl.StartJitter(30, 60);
            }
        }


    }
}
