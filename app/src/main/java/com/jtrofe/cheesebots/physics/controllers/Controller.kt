package com.jtrofe.cheesebots.physics.controllers

import com.jtrofe.cheesebots.physics.Engine

/**
 * Base class for controllers
 */
public abstract class Controller(protected val mEngine:Engine){

    public abstract fun Update(timeStep:Double)
}