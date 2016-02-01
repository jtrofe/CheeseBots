package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jtrofe.cheesebots.customviews.GameSurfaceView;
import com.jtrofe.cheesebots.game.Levels.GameLevel;
import com.jtrofe.cheesebots.game.Levels.Level0;
import com.jtrofe.cheesebots.game.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAIN on 2/1/16
 */
public class GameActivity extends Activity{
    private static GameActivity app;


    protected GameSurfaceView gameView;

    public static List<Bitmap> SpriteSheets;


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


        setContentView(R.layout.activity_game);

        SpriteSheets = new ArrayList<>();

        // Load sprite sheets and resize them based on screen dimensions
        Bitmap sheet = BitmapFactory.decodeResource(getResources(), R.drawable.robot_frames);
        SpriteSheets.add(Bitmap.createScaledBitmap(sheet, 500, 120, false));

        sheet = BitmapFactory.decodeResource(getResources(), R.drawable.flail_frames);
        SpriteSheets.add(Bitmap.createScaledBitmap(sheet, 300, 100, false));


        TextView scoreView = (TextView) findViewById(R.id.destroyedCounter);
        TextView timerView = (TextView) findViewById(R.id.timerView);
        TextView messageView = (TextView) findViewById(R.id.messageText);

        UI userInterface = new UI();
        userInterface.AddView(scoreView);
        userInterface.AddView(timerView);
        userInterface.AddView(messageView);

        Intent intent = getIntent();
        GameLevel level = new Level0();


        String json = intent.getExtras().getString("level", "");
        if(!json.isEmpty()){
            level.FromJSON(json);
        }

        gameView = new GameSurfaceView(this, userInterface, level);


        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);
        frame.addView(gameView);
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

    @Override
    public void onBackPressed(){
        if(GameApplication.GameEngine.LevelComplete){
            GameApplication.GameEngine = null;
            finish();
        }
    }

    public static void RunOnUI(Runnable r){
        app.runOnUiThread(r);
    }

    public static void OnGameEnd(boolean userWon){

    }
}
