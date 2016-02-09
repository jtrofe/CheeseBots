package com.jtrofe.cheesebots;

import android.app.Application;

import com.jtrofe.cheesebots.game.physics.Engine;

/**
 * Created by MAIN on 1/26/16
 */
public class GameApplication extends Application {
    public static GameApplication App;

    public static Engine GameEngine = null;

    public static com.jtrofe.cheesebots.physics.Engine Engine = null;

    public static boolean UseRope = false;

    @Override
    public void onCreate(){
        super.onCreate();

        App = this;
    }

    public static float min(float... n){
        float val = Float.MAX_VALUE;

        for(float v:n){
            if(v < val) val = v;
        }

        return val;
    }

    public static double min(double... n){
        double val = Double.MAX_VALUE;

        for(double v:n){
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

    public static double max(double... n){
        double val = -Double.MAX_VALUE;

        for(double v:n){
            if(v > val) val = v;
        }

        return val;
    }
}
