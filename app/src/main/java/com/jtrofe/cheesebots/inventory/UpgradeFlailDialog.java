package com.jtrofe.cheesebots.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        massButton.setTag("mass");
        radiusButton.setTag("radius");
        kButton.setTag("k");

        // Set event listeners
        massButton.setOnClickListener(buttonHandler);
        radiusButton.setOnClickListener(buttonHandler);
        kButton.setOnClickListener(buttonHandler);

        // Set flail stuff

        mFlail = flail;

        massGauge.SetGauge(mFlail.GetMassLevel());
        radiusGauge.SetGauge(mFlail.GetRadiusLevel());
        kGauge.SetGauge(mFlail.GetKLevel());

        if(mFlail.GetMassLevel() != 3) massButton.setVisibility(View.VISIBLE);
        if(mFlail.GetRadiusLevel() != 3) radiusButton.setVisibility(View.VISIBLE);
        if(mFlail.GetKLevel() != 3) kButton.setVisibility(View.VISIBLE);

        setLabels();

        // Show dialog
        dialog.show();
    }

    private static void setLabels(){
        long massCost = mFlail.GetUpgradeCost(UserFlail.UPGRADE_MASS);
        long radiusCost = mFlail.GetUpgradeCost(UserFlail.UPGRADE_RADIUS);
        long kCost = mFlail.GetUpgradeCost(UserFlail.UPGRADE_K);

        int maxLevel = UserFlail.MAX_UPGRADE_LEVEL - 1;

        if(mFlail.GetMassLevel() < maxLevel){
            massLabel.setText("Mass - Upgrade cost: " + massCost);
        }else {
            massLabel.setText("Mass");
        }

        if(mFlail.GetRadiusLevel() < maxLevel){
            radiusLabel.setText("Size - Upgrade cost: " + radiusCost);
        }else {
            radiusLabel.setText("Size");
        }

        if(mFlail.GetKLevel() < maxLevel){
            kLabel.setText("Spring - Upgrade cost: " + kCost);
        }else {
            kLabel.setText("Spring");
        }

    }

    private static View.OnClickListener buttonHandler = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            String tag = v.getTag().toString();

            if(mFlail == null) return;

            switch(tag){
                case "mass":
                    mFlail.UpgradeMass();
                    massGauge.SetGauge(mFlail.GetMassLevel());
                    break;
                case "radius":
                    mFlail.UpgradeRadius();
                    radiusGauge.SetGauge(mFlail.GetRadiusLevel());
                    break;
                case "k":
                    mFlail.UpgradeK();
                    kGauge.SetGauge(mFlail.GetKLevel());
                    break;
            }

            activity.RefreshFlails();
            setLabels();

            Storage.SaveUser();
        }
    };
}
