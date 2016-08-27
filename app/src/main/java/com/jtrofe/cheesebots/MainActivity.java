package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jtrofe.cheesebots.game.ScoresLoader;
import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.physics.PhysicsView;

import java.util.List;

/**
 * Created by MAIN on 3/13/16
 */
public class MainActivity extends Activity{

    private Context me;

    // Views
    private Button PlayButton;
    public ProgressBar ProgressSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        me = this;

        setContentView(R.layout.activity_main);

        ProgressSpinner = (ProgressBar) findViewById(R.id.score_progress);
        PlayButton = (Button) findViewById(R.id.button_play);
        //Button inventoryButton = (Button) findViewById(R.id.button_inventory);
        final Button scoresButton = (Button) findViewById(R.id.button_highscores);

        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                startGame();
            }
        });
        /*inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInventory();
            }
        });*/

        final ScoresLoader loader = new ScoresLoader(this, findViewById(R.id.layout_scores));
        final Context me = this;
        scoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {


                Animation anim = AnimationUtils.loadAnimation(me, R.anim.collapse);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        scoresButton.setVisibility(View.GONE);
                        ProgressSpinner.setVisibility(View.VISIBLE);
                        loader.LoadScores();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                scoresButton.startAnimation(anim);
                scoresButton.invalidate();
            }
        });

        startAds();

        startAnimation();

    }


    @Override
    public void onPause(){
        super.onPause();
        stopAds();
        if(mPhysicsView != null) mPhysicsView.Pause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mPhysicsView.Resume();

        startAds();
    }

    //private void openInventory(){
    //    Intent intent = new Intent(this, InventoryActivity.class);
    //
    //    startActivity(intent);
    //}

    private void startGame() {
        if(GameApp.CurrentGame != null) GameApp.CurrentGame.End();
        GameApp.CurrentGame = null;

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.collapse);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stopAds();

                finish();

                Intent intent = new Intent(me, GameActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        PlayButton.startAnimation(anim);
        PlayButton.invalidate();
    }


    private PhysicsView mPhysicsView;

    private void startAnimation(){

        // Get game views
        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);

        // Create physics view and add to the game frame
        mPhysicsView = new PhysicsView(this, true);
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
