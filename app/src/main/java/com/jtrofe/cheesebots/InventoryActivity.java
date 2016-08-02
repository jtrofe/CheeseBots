package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jtrofe.cheesebots.game.UserData.UserFlail;
import com.jtrofe.cheesebots.inventory.FlailAdapter;
import com.jtrofe.cheesebots.inventory.UpgradeFlailDialog;

/**
 * Created by MAIN on 3/7/16
 */
public class InventoryActivity extends Activity implements View.OnClickListener{

    private static final int TAG_UPGRADE_FLAIL = 0;
    private static final int TAG_EXIT = 1;

    Button upgradeButton;

    FlailAdapter flailAdapter;
    RecyclerView flailScrollView;

    private TextView scrapLabel;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_inventory);

        // Get views
        flailScrollView = (RecyclerView) findViewById(R.id.rView);

        ImageButton exitButton = (ImageButton) findViewById(R.id.inventory_button_exit);
        upgradeButton = (Button) findViewById(R.id.inventory_button_upgrade);
        scrapLabel = (TextView) findViewById(R.id.scrap_label_text);

        // Add adapters to scroll views
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        flailScrollView.setLayoutManager(manager);
        flailAdapter = new FlailAdapter(GameApp.CurrentUser.GetAllFlails());
        flailScrollView.setAdapter(flailAdapter);

        // Set button flags
        upgradeButton.setTag(TAG_UPGRADE_FLAIL);
        exitButton.setTag(TAG_EXIT);

        // Set on click listeners
        upgradeButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        // Update labels
        UpdateScrapLabel();
    }

    public void UpdateScrapLabel(){
        scrapLabel.setText(GameApp.CurrentUser.GetScrap() + "");
    }

    public void RefreshFlails(){
        flailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(@NonNull View v){
        int tag = (int) v.getTag();

        switch (tag){
            case TAG_UPGRADE_FLAIL:
                int flailIndex = GameApp.CurrentUser.GetSelectedFlailIndex();
                UserFlail f = GameApp.CurrentUser.GetAllFlails().get(flailIndex);

                UpgradeFlailDialog.Create(this, f);
                break;
            case TAG_EXIT:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
                break;
        }
    }
}
