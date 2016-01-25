package com.jtrofe.cheesebots;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jtrofe.cheesebots.customviews.GameSurfaceView;


public class MainActivity extends Activity{

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

    protected GameSurfaceView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        System.out.println("MainActivity.onCreate() called");
        System.out.println("__savedInstanceState is null? " + (savedInstanceState == null));
        // Hide the system UI
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);


        gameView = new GameSurfaceView(this, savedInstanceState);

        setContentView(gameView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        System.out.println("MainActivity.onSaveInstanceState() called");
        savedInstanceState = gameView.SaveState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        System.out.println("MainActivity.onRestoreInstanceState() called");

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        System.out.println("MainActivity.onDestroy() called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameView.Resume();
        System.out.println("MainActivity.onResume() called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.Pause();

        System.out.println("MainActivity.onPause() called");
    }
}
