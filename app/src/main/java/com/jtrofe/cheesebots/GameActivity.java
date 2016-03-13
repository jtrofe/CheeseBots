package com.jtrofe.cheesebots;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jtrofe.cheesebots.physics.PhysicsView;

import java.util.List;

/**
 * Created by MAIN on 3/13/16.
 */
public class GameActivity extends Activity{

    private PhysicsView mPhysicsView = null;

    // UI
    private TextView mScoreView;
    private TextView mMessageView;

    /**
     * Hide the navigation buttons so the
     * game can be truly fullscreen
     */
    private void removeNavigation(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if(Build.VERSION.SDK_INT >= 19){
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        mPhysicsView = new PhysicsView(this);

        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);

        frame.addView(mPhysicsView);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bitmap> spriteSheets = SpriteHandler.GetSpriteSheets();

                mPhysicsView.SetSpriteSheets(spriteSheets);
            }
        });
        t.start();

        GameApp.CurrentGame.GameContext = this;

        mScoreView = (TextView) findViewById(R.id.destroyedCounter);

        mMessageView = (TextView) findViewById(R.id.messageText);

        mMessageView.setText("");

        if(GameApp.CurrentGame.IsComplete()){
            GameApp.CurrentGame.OnComplete();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mPhysicsView.Resume();

        removeNavigation();
    }

    @Override
    public void onPause(){
        super.onPause();
        mPhysicsView.Pause();
    }

    @Override
    public void onBackPressed(){
        if(GameApp.CurrentGame != null){
            if(GameApp.CurrentGame.IsComplete()){
                GameApp.CurrentGame = null;
                finish();
            }
        }
    }

    public void SetScore(final String score){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScoreView.setText(score);
            }
        });
    }

    public void SetCompleteMessage(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.setText(msg);
            }
        });
    }
}
