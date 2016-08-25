package com.jtrofe.cheesebots.physics.controllers;

import com.jtrofe.cheesebots.physics.Engine;
import com.jtrofe.cheesebots.physics.Vec;
import com.jtrofe.cheesebots.physics.objects.Cheese;
import com.jtrofe.cheesebots.physics.objects.GameObject;

/**
 * Removes cheeses if they have been completely eaten
 */
public class CheeseController extends Controller{

    public CheeseController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(double timeStep){

        for(GameObject obj:mEngine.Bodies){
            if(obj.Type != GameObject.TYPE_CHEESE) continue;

            if(mEngine.IsMainPage()){
                if(obj.GetPosition().x > mEngine.GetWorldSize().x * 1.1){
                    Vec p = mEngine.GetWorldSize().ScalarMultiply(0.5);
                    p.x -= mEngine.GetWorldSize().x * 1.1;

                    ((Cheese) obj).SetPosition(p);
                }

                Vec v = new Vec(mEngine.GetWorldSize().x / 100, 0);

                ((Cheese) obj).SetPosition(obj.GetPosition().Add(v));
            }

            if(((Cheese) obj).GetAmountLeft() <= 0){

                mEngine.RemoveBody(obj);

                mEngine.JitterController.StartJitter(30, 60.0);
            }
        }
    }
}
