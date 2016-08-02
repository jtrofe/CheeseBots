package com.jtrofe.cheesebots;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.jtrofe.cheesebots.game.DatabaseHandler;
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

    public static DatabaseHandler Database = new DatabaseHandler();

    @Override
    public void onCreate(){
        super.onCreate();

        App = this;

        CurrentUser = Storage.LoadUser();

        // Start database stuff
        String device_id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Database.SetDeviceId(device_id);
        Database.SetApp(this);
        Database.GetPlace();
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
