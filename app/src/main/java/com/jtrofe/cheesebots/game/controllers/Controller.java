package com.jtrofe.cheesebots.game.controllers;

import com.jtrofe.cheesebots.game.physics.Engine;

/**
 * Created by MAIN on 1/23/16
 */
public abstract class Controller{

    protected Engine mEngine;

    public Controller(Engine engine){
        this.mEngine = engine;
    }

    public abstract void Update();
}
