package com.jtrofe.cheesebots.physics;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by MAIN on 8/8/16
 */
public class ScoresLoader{

    private Activity mContext;
    private View mScoresLayout;

    public ScoresLoader(Activity context, View linearLayout){
        mContext = context;
        mScoresLayout = linearLayout;
    }

    public void LoadScores(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = GameApp.Database.GetScores();

                displayScores(resp);
            }
        });

        t.start();
    }

    private void displayScores(String response){
        Log.d("ANIMATION", response);
        final String[] names = new String[3];
        final long[] scores = new long[3];
        try{
            JSONArray places = new JSONArray(response);

            names[0] = places.getJSONObject(0).getString("name");
            names[1] = places.getJSONObject(1).getString("name");
            names[2] = places.getJSONObject(2).getString("name");

            scores[0] = places.getJSONObject(0).getLong("score");
            scores[1] = places.getJSONObject(1).getLong("score");
            scores[2] = places.getJSONObject(2).getLong("score");
        }catch(JSONException e){
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Error loading scores", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        final TextView name_0 = (TextView) mScoresLayout.findViewById(R.id.name_0);
        final TextView name_1 = (TextView) mScoresLayout.findViewById(R.id.name_1);
        final TextView name_2 = (TextView) mScoresLayout.findViewById(R.id.name_2);
        final TextView score_0 = (TextView) mScoresLayout.findViewById(R.id.score_0);
        final TextView score_1 = (TextView) mScoresLayout.findViewById(R.id.score_1);
        final TextView score_2 = (TextView) mScoresLayout.findViewById(R.id.score_2);

        if(name_0 == null || name_1 == null || name_2 == null || score_0 == null || score_1 == null || score_2 == null){
            return;
        }


        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                name_0.setText(names[0]);
                name_1.setText(names[1]);
                name_2.setText(names[2]);

                score_0.setText(scores[0] + "");
                score_1.setText(scores[1] + "");
                score_2.setText(scores[2] + "");

                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.expand_horizontal);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mScoresLayout.setVisibility(View.VISIBLE);
                        Log.d("ANIMATION", mScoresLayout.getVisibility() + "");
                    }

                    @Override
                    public void onAnimationEnd(Animation animation){
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mScoresLayout.startAnimation(animation);
                mScoresLayout.invalidate();
            }
        });
    }
}
