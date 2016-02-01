package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jtrofe.cheesebots.game.Levels.GameLevel;
import com.jtrofe.cheesebots.game.Levels.Level0;


public class MainActivity extends Activity{

    EditText botsOnScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button_play);

        botsOnScreen = (EditText) findViewById(R.id.text_bots_on_screen);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });


    }

    private void startGame(){
        GameLevel level = new Level0();

        try {
            level.MaxBotsOnScreen = Integer.parseInt(botsOnScreen.getText().toString());
        }catch(Exception e){
            level.MaxBotsOnScreen = 10;
        }

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", level.ToJSON());

        startActivity(intent);
    }
}
