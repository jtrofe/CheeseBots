package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jtrofe.cheesebots.physics.ScoresLoader;

/**
 * Created by MAIN on 3/13/16
 */
public class MainActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button playButton = (Button) findViewById(R.id.button_play);
        //Button inventoryButton = (Button) findViewById(R.id.button_inventory);
        final Button scoresButton = (Button) findViewById(R.id.button_highscores);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        scoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoresButton.setVisibility(View.GONE);

                loader.LoadScores();
            }
        });

        startAds();

    }

    private void openInventory(){
        Intent intent = new Intent(this, InventoryActivity.class);

        startActivity(intent);
    }

    private void startGame() {
        stopAds();

        finish();

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
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
