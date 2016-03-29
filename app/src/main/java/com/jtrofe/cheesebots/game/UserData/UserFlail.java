package com.jtrofe.cheesebots.game.UserData;

import android.content.res.Resources;
import android.util.TypedValue;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.physics.objects.Flail;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAIN on 3/1/16
 */
public class UserFlail{

    private int mGraphic = 0;

    private int mRadiusLevel;
    private int mMassLevel;
    private int mKLevel;

    private Boolean mPlow;

    public int GetGraphic(){
        return mGraphic;
    }

    public int GetMassLevel(){ return mMassLevel; }
    public int GetRadiusLevel(){ return mRadiusLevel; }
    public int GetKLevel(){ return mKLevel; }

    public String ToJSON(){
        return "{ \"graphic\" : \"" + mGraphic + "\", " +
                "\"radiusLevel\" : " + mRadiusLevel + ", " +
                "\"massLevel\" : " + mMassLevel + ", " +
                "\"kLevel\" : " + mKLevel + ", " +
                "\"plow\" : " + mPlow + "}";
    }

    public void FromJSON(JSONObject object) throws JSONException{
        mGraphic = object.getInt("graphic");

        mRadiusLevel = object.getInt("radiusLevel");
        mMassLevel = object.getInt("massLevel");
        mKLevel = object.getInt("kLevel");

        mPlow = object.getBoolean("plow");
    }

    public void LoadDefault(){
        mGraphic = 0;

        mRadiusLevel = 0;
        mMassLevel = 0;
        mKLevel = 0;

        mPlow = false;
    }

    public Flail GetFlail(){
        Resources resources = GameApp.App.getResources();

        TypedValue out = new TypedValue();

        resources.getValue(R.raw.flail_mass_base, out, true);
        double massBase = (double) out.getFloat();
        resources.getValue(R.raw.flail_mass_multiplier, out, true);
        double massMultiplier = (double) out.getFloat();

        resources.getValue(R.raw.flail_radius_base, out, true);
        double radiusBase = (double) out.getFloat();
        resources.getValue(R.raw.flail_radius_multiplier, out, true);
        double radiusMultiplier = (double) out.getFloat();

        resources.getValue(R.raw.flail_k_base, out, true);
        double kBase = (double) out.getFloat();
        resources.getValue(R.raw.flail_k_multiplier, out, true);
        double kMultiplier = (double) out.getFloat();

        double mass = massBase + (massMultiplier * mMassLevel);
        double radius = radiusBase + (radiusMultiplier * mRadiusLevel);
        double k = kBase + (kMultiplier * mKLevel);

        Flail f = new Flail(mass, radius, k);
        f.IsPlow = mPlow;

        return f;
    }

    public void UpgradeMass(){
        mMassLevel ++;

        //TODO change 0 to 2
        if(mMassLevel > 2) mMassLevel = 0;
    }

    public void UpgradeRadius(){
        mRadiusLevel ++;

        //TODO change 0 to 2
        if(mRadiusLevel > 2) mRadiusLevel = 0;
    }

    public void UpgradeK(){
        mKLevel ++;

        //TODO change 0 to 2
        if(mKLevel > 2) mKLevel = 0;
    }
}
