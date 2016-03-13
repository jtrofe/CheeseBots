package com.jtrofe.cheesebots.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.game.UserData.UserFlail;

import org.jetbrains.annotations.NotNull;

/**
 * Created by MAIN on 3/10/16
 */
public class UpgradeView extends FrameLayout implements View.OnClickListener{

    private UserFlail mFlail;

    private GaugeView massGauge;
    private Button massButton;

    private GaugeView radiusGauge;
    private Button radiusButton;

    private GaugeView kGauge;
    private Button kButton;

    public UpgradeView(Context context){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.inventory_popup_upgrade, this, true);

        massGauge = (GaugeView) findViewById(R.id.upgrade_mass_gauge);
        radiusGauge = (GaugeView) findViewById(R.id.upgrade_radius_gauge);
        kGauge = (GaugeView) findViewById(R.id.upgrade_k_gauge);

        massButton = (Button) findViewById(R.id.upgrade_mass_button);
        radiusButton = (Button) findViewById(R.id.upgrade_radius_button);
        kButton = (Button) findViewById(R.id.upgrade_k_button);

        massButton.setTag("mass");
        radiusButton.setTag("radius");
        kButton.setTag("k");

        massButton.setOnClickListener(this);
        radiusButton.setOnClickListener(this);
        kButton.setOnClickListener(this);
    }

    public void SetFlail(UserFlail flail){
        mFlail = flail;

        massGauge.SetGauge(mFlail.GetMassLevel());
        radiusGauge.SetGauge(mFlail.GetRadiusLevel());
        kGauge.SetGauge(mFlail.GetKLevel());

        if(mFlail.GetMassLevel() != 3) massButton.setVisibility(VISIBLE);
        if(mFlail.GetRadiusLevel() != 3) radiusButton.setVisibility(VISIBLE);
        if(mFlail.GetKLevel() != 3) kButton.setVisibility(VISIBLE);
    }

    @Override
    public void onClick(@NotNull View v){
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

        Storage.SaveUser();
    }
}
