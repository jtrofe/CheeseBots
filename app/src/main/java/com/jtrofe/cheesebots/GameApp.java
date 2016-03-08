package com.jtrofe.cheesebots;

import android.app.Application;

import com.jtrofe.cheesebots.game.Game;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.game.UserData.User;

/**
 * Created by MAIN on 1/26/16
 */
public class GameApp extends Application {
    public static GameApp App;

    public static Game CurrentGame = null;

    public static User CurrentUser = null;


    @Override
    public void onCreate(){
        super.onCreate();

        App = this;

        CurrentUser = Storage.LoadUser();
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
