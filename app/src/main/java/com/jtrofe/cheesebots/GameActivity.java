package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jtrofe.cheesebots.game.SoundHandler;
import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.physics.PhysicsView;
import com.jtrofe.cheesebots.game.ScoresLoader;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

/**
 * Created by MAIN on 3/13/16
 */
public class GameActivity extends Activity{

    private PhysicsView mPhysicsView = null;

    // UI
    private TextView mScoreView;
    private TextView mMessageView;
    private TextView mScrapView;

    // Game over variables
    private View mGameOverLayout;
    private View mSubmitLayout;
    private EditText mNameInput;
    private ImageButton mSubmitButton;

    private String mFinalName;
    private int mFinalScore;
    private String mSubmitMessage;

    private GameActivity me;

    public SoundHandler SoundEffects;

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

        // Get game views
        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);

        // Create physics view and add to the game frame
        mPhysicsView = new PhysicsView(this);
        frame.addView(mPhysicsView);

        // Load the sprite sheets into the physics view on a new thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bitmap> spriteSheets = SpriteHandler.GetSpriteSheets();

                mPhysicsView.SetSpriteSheets(spriteSheets);
            }
        });
        t.start();

        // Set self-referential variables
        GameApp.CurrentGame.GameContext = this;
        me = this;

        // Get UI views
        Button exitButton = (Button) findViewById(R.id.button_exit);
        Button retryButton = (Button) findViewById(R.id.button_retry);
        mScoreView = (TextView) findViewById(R.id.destroyedCounter);
        mMessageView = (TextView) findViewById(R.id.messageText);
        mScrapView = (TextView) findViewById(R.id.game_text_scrap);

        // Get the game over views
        mGameOverLayout = findViewById(R.id.layout_game_over);
        mSubmitLayout = findViewById(R.id.layout_submit_score);
        mSubmitButton = (ImageButton) mSubmitLayout.findViewById(R.id.input_button);
        mNameInput = (EditText) mSubmitLayout.findViewById(R.id.input_name);

        //mNameInput.setText(GameApp.CurrentUser.GetName());

        // Set initial texts
        mMessageView.setText("");
        SetScrap("0");

        // If the orientation has changed or anything keep game completed if it was
        if(GameApp.CurrentGame.IsComplete()){
            GameApp.CurrentGame.OnComplete();
        }

        // Set listeners

        mMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                if(GameApp.CurrentGame != null){
                    if(!GameApp.CurrentGame.IsComplete()){
                        boolean paused = GameApp.CurrentGame.IsPaused();

                        if(!paused){
                            startAds();
                            SetCompleteMessage("PAUSED", Color.parseColor("#e78ec2"));
                        }else{
                            stopAds();
                            SetCompleteMessage("");
                        }

                        GameApp.CurrentGame.SetPaused(!paused);
                    }
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Quit();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retry();
            }
        });

        stopAds();

        SoundEffects = new SoundHandler(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        SoundEffects.Destroy();
        SoundEffects = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        mPhysicsView.Resume();

        if(SoundEffects == null) SoundEffects = new SoundHandler(this);

        if(GameApp.CurrentGame != null){
            if(GameApp.CurrentGame.IsPaused() || GameApp.CurrentGame.IsComplete()){
                startAds();
            }
        }

        removeNavigation();
    }

    @Override
    public void onPause(){
        super.onPause();
        stopAds();
        mPhysicsView.Pause();
    }

    @Override
    public void onBackPressed(){
        if(GameApp.CurrentGame != null){
            if(!GameApp.CurrentGame.IsComplete()){

                boolean paused = GameApp.CurrentGame.IsPaused();

                if(!paused){
                    startAds();
                    SetCompleteMessage("CLICK TO UNPAUSE", Color.parseColor("#e78ec2"));
                    GameApp.CurrentGame.SetPaused(true);
                }else{
                    Quit();
                }

            }else{
                Quit();
            }
        }
    }

    public void Retry(){
        stopAds();
        if(GameApp.CurrentGame != null) GameApp.CurrentGame.End();
        GameApp.CurrentGame = null;

        finish();

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void Quit(){
        stopAds();
        if(GameApp.CurrentGame != null) GameApp.CurrentGame.End();
        GameApp.CurrentGame = null;

        finish();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void SetScrap(final String scrap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO remove when upgrades are implemented
                mScrapView.setVisibility(View.GONE);
                //mScrapView.setText("Scrap: " + scrap);
            }
        });
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
                mMessageView.setTextColor(Color.WHITE);
                mMessageView.setTextSize(14);
                mMessageView.setText(msg);
            }
        });
    }

    public void SetCompleteMessage(final String msg, final int color){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.setTextColor(color);
                mMessageView.setTextSize(30);
                mMessageView.setShadowLayer(1, 2, 2, Color.parseColor("#d46a6a"));
                mMessageView.setText(msg);
            }
        });
    }

    public void MakeToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OnComplete(final int score, final long scrapAdded){
        mFinalScore = score;

        // Clear the score text view
        SetScore("");

        // Create the crazy runnable tree
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                runOnUiThread(onSubmitClick);
            }
        });

        // Start UI thread
        runOnUiThread(onGameOver);

        Storage.SaveUser();
    }

    private Runnable onSubmitClick = new Runnable(){
        @Override
        public void run(){
            // Make sure the user entered a name. Quit if they didn't
            final String name = mNameInput.getText().toString();
            if(name.trim().isEmpty()){
                Toast.makeText(me, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update user profile
            mFinalName = name;
            GameApp.CurrentUser.SetName(name);

            // Create the animation to shrink the submit layout
            Animation collapse = AnimationUtils.loadAnimation(me, R.anim.collapse);
            collapse.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    onCollapseEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mSubmitLayout.startAnimation(collapse);
            mSubmitLayout.invalidate();
        }
    };

    private void onCollapseEnd(){
        mSubmitButton.setOnClickListener(null);
        mSubmitLayout.setVisibility(View.GONE);
        mSubmitLayout.clearAnimation();

        ProgressBar progress = (ProgressBar) findViewById(R.id.score_progress);

        if(progress != null){
            progress.setVisibility(View.VISIBLE);
        }

        new Thread(submitScore).start();
    }

    private Runnable submitScore = new Runnable() {
        @Override
        public void run() {
            mSubmitMessage = GameApp.Database.SubmitScore(mFinalName, mFinalScore);

            runOnUiThread(displayMessage);
        }
    };

    private Runnable displayMessage = new Runnable(){
        @Override
        public void run(){

            Toast.makeText(me, mSubmitMessage, Toast.LENGTH_SHORT).show();

            switch (mSubmitMessage){
                case "You got the gold!":
                    GameApp.CurrentGame.SetBorderColor(Color.parseColor("#CCFFDF00"));
                    break;
                case "You got the silver!":
                    GameApp.CurrentGame.SetBorderColor(Color.parseColor("#CCD3D3D3"));
                    break;
                case "You got the bronze!":
                    GameApp.CurrentGame.SetBorderColor(Color.parseColor("#CCC9AE5D"));
                    break;
            }

            // Load highscores
            new ScoresLoader(me, findViewById(R.id.layout_scores)).LoadScores();
        }
    };

    private Runnable onGameOver = new Runnable() {
        @Override
        public void run(){
            startAds();


            // Create congrats message TODO add message about scrap once upgrading is implemented
            String r = (mFinalScore == 1) ? "robot" : "robots";
            String congratsMessage = "Congrats, you destroyed " + mFinalScore + " " + r;

            mMessageView.setText(congratsMessage);

            mNameInput.setText(GameApp.CurrentUser.GetName());

            mGameOverLayout.setVisibility(View.VISIBLE);

            Animation expand = AnimationUtils.loadAnimation(me, R.anim.expand);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mSubmitLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            mSubmitLayout.startAnimation(expand);
            mSubmitLayout.invalidate();
        }
    };


    // ADS
    private void startAds(){
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FA62693DA83DCD07CE56E8226B7FAF61").build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.VISIBLE);
    }

    private void stopAds(){
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.destroy();
        adView.setVisibility(View.INVISIBLE);
    }
}
