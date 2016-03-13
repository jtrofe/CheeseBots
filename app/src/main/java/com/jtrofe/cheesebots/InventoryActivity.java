package com.jtrofe.cheesebots;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jtrofe.cheesebots.game.UserData.UserFlail;
import com.jtrofe.cheesebots.inventory.FlailAdapter;
import com.jtrofe.cheesebots.inventory.UpgradeView;

/**
 * Created by MAIN on 3/7/16
 */
public class InventoryActivity extends Activity implements View.OnClickListener{

    Button upgradeButton;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_inventory);

        RecyclerView rView = (RecyclerView) findViewById(R.id.rView);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rView.setLayoutManager(manager);

        FlailAdapter adapter = new FlailAdapter(GameApp.CurrentUser.GetAllFlails());
        rView.setAdapter(adapter);

        upgradeButton = (Button) findViewById(R.id.inventory_button_upgrade);

        upgradeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){

        UpgradeView popup = new UpgradeView(this);
        int flailIndex = GameApp.CurrentUser.GetSelectedFlailIndex();
        UserFlail f = GameApp.CurrentUser.GetAllFlails().get(flailIndex);
        popup.SetFlail(f);

        PopupWindow popupWindow = new PopupWindow(popup, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.showAsDropDown(upgradeButton);
    }
}
