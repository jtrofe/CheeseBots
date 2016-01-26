package com.jtrofe.cheesebots;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jtrofe.cheesebots.customviews.GameSurfaceView;
import com.jtrofe.cheesebots.game.UI;


public class MainActivity extends Activity{

    private static MainActivity app;

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


    /**
     * Hide the navigation buttons so the
     * game can be truly fullscreen
     */
    private void removeNavigation(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if(Build.VERSION.SDK_INT >= 19){
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        TextView scoreView = (TextView) findViewById(R.id.destroyedCounter);

        UI userInterface = new UI();
        userInterface.AddView(scoreView);

        gameView = new GameSurfaceView(this, userInterface);


        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);
        frame.addView(gameView);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameView.Resume();

        // Hide the system UI
        removeNavigation();
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.Pause();
    }

    public static void RunOnUI(Runnable r){
        app.runOnUiThread(r);
    }
}
