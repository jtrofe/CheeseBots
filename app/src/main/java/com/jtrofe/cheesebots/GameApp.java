package com.jtrofe.cheesebots;

import android.app.Application;

import com.jtrofe.cheesebots.game.Game;

/**
 * Created by MAIN on 1/26/16
 */
public class GameApp extends Application {
    public static GameApp App;

    public static Game CurrentGame = null;


    @Override
    public void onCreate(){
        super.onCreate();

        App = this;
    }

    public static double min(double... n){
        double val = Double.MAX_VALUE;

        for(double v:n){
            if(v < val) val = v;
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
