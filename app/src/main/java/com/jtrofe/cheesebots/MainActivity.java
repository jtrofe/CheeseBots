package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by MAIN on 3/13/16
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button playButton = (Button) findViewById(R.id.button_play);
        Button inventoryButton = (Button) findViewById(R.id.button_inventory);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInventory();
            }
        });

    }

    private void openInventory(){
        Intent intent = new Intent(this, InventoryActivity.class);

        startActivity(intent);
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);

        startActivity(intent);
    }
}
