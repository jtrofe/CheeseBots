package com.jtrofe.cheesebots;

import android.app.Application;

import com.jtrofe.cheesebots.game.physics.Engine;

/**
 * Created by MAIN on 1/26/16
 */
public class GameApplication extends Application {

    public static Engine GameEngine = null;

    public static float min(float... n){
        float val = Float.MAX_VALUE;

        for(float v:n){
            if(v < val) val = v;
        }

        return val;
    }

    public static float max(float... n){
        float val = -Float.MAX_VALUE;

        for(float v:n){
            if(v > val) val = v;
        }

        return val;
    }

}
