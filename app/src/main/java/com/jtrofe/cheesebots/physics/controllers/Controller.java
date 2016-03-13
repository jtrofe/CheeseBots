package com.jtrofe.cheesebots.physics.controllers;

import com.jtrofe.cheesebots.physics.Engine;

/**
 * Base class for controllers
 */
public abstract class Controller{

    protected Engine mEngine;

    public Controller(Engine engine){
        mEngine = engine;
    }

    public abstract void Update(double timeStep);
}
