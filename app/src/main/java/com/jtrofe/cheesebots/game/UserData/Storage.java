package com.jtrofe.cheesebots.game.UserData;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jtrofe.cheesebots.GameApp;

/**
 * Created by MAIN on 3/7/16
 */
public class Storage{

    public static String USER_NOT_SET = "";
    public static String PREFERENCES_NAME = "CHEESE_BOTS";

    public static User LoadUser(){
        SharedPreferences preferences = GameApp.App.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        String userJSON = preferences.getString("user", USER_NOT_SET);


        return new User(userJSON);
    }

    public static void SaveUser(){
        SharedPreferences preferences = GameApp.App.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("user", GameApp.CurrentUser.ToJSON());

        editor.commit();

        Log.i("Storage", "User saved");
    }
}
