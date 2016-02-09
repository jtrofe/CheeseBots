package com.jtrofe.cheesebots.game.physics.constraints;


import com.jtrofe.cheesebots.game.gameobjects.GameObject;

/**
 * Base class for physical constraints
 */
public abstract class Constraint{

    protected GameObject mBodyA;
    protected GameObject mBodyB;

    public Constraint(GameObject bodyA, GameObject bodyB){
        this.mBodyA = bodyA;
        this.mBodyB = bodyB;
    }


    public abstract void Solve(float timeStep);
}