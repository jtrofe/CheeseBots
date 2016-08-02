package com.jtrofe.cheesebots.inventory;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.InventoryActivity;
import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.game.UserData.UserFlail;

/**
 * Created by MAIN on 4/5/16
 */
public class UpgradeFlailDialog{

    public static InventoryActivity activity;

    public static Dialog dialog;

    private static UserFlail mFlail;

    private static GaugeView massGauge;
    private static ImageButton massButton;

    private static GaugeView radiusGauge;
    private static ImageButton radiusButton;

    private static GaugeView kGauge;
    private static ImageButton kButton;

    private static TextView massLabel;
    private static TextView radiusLabel;
    private static TextView kLabel;

    private static TextView scrapLabel;

    public static void Create(Context context, UserFlail flail){
        activity = (InventoryActivity) context;

        dialog = new Dialog(context);

        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.inventory_dialog_upgrade_flail, null);

        dialog.setContentView(v);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Get views
        massGauge = (GaugeView) v.findViewById(R.id.upgrade_mass_gauge);
        radiusGauge = (GaugeView) v.findViewById(R.id.upgrade_radius_gauge);
        kGauge = (GaugeView) v.findViewById(R.id.upgrade_k_gauge);

        massButton = (ImageButton) v.findViewById(R.id.upgrade_mass_button);
        radiusButton = (ImageButton) v.findViewById(R.id.upgrade_radius_button);
        kButton = (ImageButton) v.findViewById(R.id.upgrade_k_button);

        massLabel = (TextView) v.findViewById(R.id.upgrade_mass_label);
        radiusLabel = (TextView) v.findViewById(R.id.upgrade_radius_label);
        kLabel = (TextView) v.findViewById(R.id.upgrade_k_label);

        scrapLabel = (TextView) v.findViewById(R.id.scrap_label_text);

        // Set values
        massButton.setTag(UserFlail.TYPE_MASS);
        radiusButton.setTag(UserFlail.TYPE_RADIUS);
        kButton.setTag(UserFlail.TYPE_K);

        // Set event listeners
        massButton.setOnClickListener(buttonHandler);
        radiusButton.setOnClickListener(buttonHandler);
        kButton.setOnClickListener(buttonHandler);

        // Set flail stuff
        mFlail = flail;

        massGauge.SetGauge(mFlail.GetLevel(UserFlail.TYPE_MASS));
        radiusGauge.SetGauge(mFlail.GetLevel(UserFlail.TYPE_RADIUS));
        kGauge.SetGauge(mFlail.GetLevel(UserFlail.TYPE_K));

        if(mFlail.GetLevel(UserFlail.TYPE_MASS) != 3) massButton.setVisibility(View.VISIBLE);
        if(mFlail.GetLevel(UserFlail.TYPE_RADIUS) != 3) radiusButton.setVisibility(View.VISIBLE);
        if(mFlail.GetLevel(UserFlail.TYPE_K) != 3) kButton.setVisibility(View.VISIBLE);

        setLabels();

        // Show dialog
        dialog.show();
    }

    private static void setLabels(){
        long massCost = mFlail.GetUpgradeCost(UserFlail.TYPE_MASS);
        long radiusCost = mFlail.GetUpgradeCost(UserFlail.TYPE_RADIUS);
        long kCost = mFlail.GetUpgradeCost(UserFlail.TYPE_K);

        int maxLevel = UserFlail.MAX_UPGRADE_LEVEL - 1;

        if(mFlail.GetLevel(UserFlail.TYPE_MASS) < maxLevel){
            massLabel.setText("Mass - Upgrade cost: " + massCost);
        }else {
            massLabel.setText("Mass");
        }

        if(mFlail.GetLevel(UserFlail.TYPE_RADIUS) < maxLevel){
            radiusLabel.setText("Size - Upgrade cost: " + radiusCost);
        }else {
            radiusLabel.setText("Size");
        }

        if(mFlail.GetLevel(UserFlail.TYPE_K) < maxLevel){
            kLabel.setText("Spring - Upgrade cost: " + kCost);
        }else {
            kLabel.setText("Spring");
        }

        scrapLabel.setText(GameApp.CurrentUser.GetScrap() + "");

    }

    private static void updateGauge(int type){
        switch(type){
            case UserFlail.TYPE_MASS:
                massGauge.SetGauge(mFlail.GetLevel(UserFlail.TYPE_MASS));
                break;
            case UserFlail.TYPE_RADIUS:
                radiusGauge.SetGauge(mFlail.GetLevel(UserFlail.TYPE_RADIUS));
                break;
            case UserFlail.TYPE_K:
                kGauge.SetGauge(mFlail.GetLevel(UserFlail.TYPE_K));
                break;
        }
    }

    private static View.OnClickListener buttonHandler = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            int type = (int) v.getTag();

            if(mFlail == null) return;

            long cost = mFlail.GetUpgradeCost(type);

            if(GameApp.CurrentUser.CanBuy(cost)){
                GameApp.CurrentUser.Buy(cost);
                mFlail.Upgrade(type);
                updateGauge(type);

                activity.RefreshFlails();
                activity.UpdateScrapLabel();
                setLabels();

                Storage.SaveUser();
            }else{
                Toast.makeText(activity, "You cannot afford that", Toast.LENGTH_SHORT).show();
            }

        }
    };
}
