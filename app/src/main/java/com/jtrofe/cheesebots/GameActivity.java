package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.physics.PhysicsView;
import com.jtrofe.cheesebots.physics.ScoresLoader;

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

    public EditText mNameInput;
    public Button mSubmitButton;


    public final GameActivity me = this;
    public int FinalScore;

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
        mScrapView = (TextView) findViewById(R.id.game_text_scrap);

        mMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //mNameInput = (EditText) findViewById(R.id.inputName);
        //mSubmitButton = (Button) findViewById(R.id.buttonSubmitScore);

        //mNameInput.setText(GameApp.CurrentUser.GetName());

        mMessageView.setText("");

        SetScrap("0");

        if(GameApp.CurrentGame.IsComplete()){
            GameApp.CurrentGame.OnComplete();
        }

        /*mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSubmitClick();
            }
        });*/

        Button exitButton = (Button) findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quit();
            }
        });

        stopAds();
    }

    @Override
    public void onResume(){
        super.onResume();
        mPhysicsView.Resume();

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

    public void Quit(){
        stopAds();
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

    public void OnComplete(int score, long scrapAdded){

        FinalScore = score;
        SetScore("");

        // Final views
        final View layoutOver = findViewById(R.id.layout_game_over);
        final View layoutSubmit = findViewById(R.id.layout_submit_score);
        final EditText nameInput = (EditText) findViewById(R.id.input_name);

        // Final objects
        final Animation expand = AnimationUtils.loadAnimation(this, R.anim.expand_horizontal);
        final Animation collapse = AnimationUtils.loadAnimation(this, R.anim.collapse_horizontal);
        final ScoresLoader scoresLoader = new ScoresLoader(this, findViewById(R.id.layout_scores));

        // Create congrats message
        //TODO add message about scrap once upgrading is implemented
        String r = (score == 1) ? "robot" : "robots";
        final String congratsMessage = "Congrats, you destroyed " + score + " " + r;


        expand.setAnimationListener(new AnimListener(layoutSubmit));



        layoutSubmit.findViewById(R.id.input_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String name = nameInput.getText().toString();

                        if(name.trim().isEmpty()){
                            MakeToast("Please enter a name");
                            return;
                        }

                        GameApp.CurrentUser.SetName(name);


                        layoutSubmit.startAnimation(collapse);
                        layoutSubmit.invalidate();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String msg = GameApp.Database.SubmitScore(name, FinalScore);

                                scoresLoader.LoadScores();
                                MakeToast(msg);

                                if(msg.equals("You got the gold!")){
                                    GameApp.CurrentGame.SetBorderColor(Color.parseColor("#CCFFDF00"));
                                }else if(msg.equals("You got the silver!")){
                                    GameApp.CurrentGame.SetBorderColor(Color.parseColor("#CCD3D3D3"));

                                }else if(msg.equals("You got the bronze!")){
                                    GameApp.CurrentGame.SetBorderColor(Color.parseColor("#CCC9AE5D"));

                                }

                            }
                        }).start();

                    }
                });
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startAds();

                mMessageView.setText(congratsMessage);

                nameInput.setText(GameApp.CurrentUser.GetName());

                layoutOver.setVisibility(View.VISIBLE);

                layoutSubmit.startAnimation(expand);
                layoutSubmit.invalidate();
            }
        });

        Storage.SaveUser();
    }

    public class AnimListener implements Animation.AnimationListener{
        View view;

        public AnimListener(View v){
            super();
            view = v;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation){}

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }


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
