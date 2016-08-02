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

    public static final int TYPE_MASS = 1;
    public static final int TYPE_RADIUS = 2;
    public static final int TYPE_K = 3;

    public static final int MAX_UPGRADE_LEVEL = 3;

    private int mGraphic = 0;

    private int mRadiusLevel;
    private int mMassLevel;
    private int mKLevel;

    private Boolean mPlow;

    public int GetGraphic(){
        return mGraphic;
    }

    public int GetLevel(int type){
        switch(type){
            case TYPE_MASS:
                return mMassLevel;
            case TYPE_RADIUS:
                return mRadiusLevel;
            case TYPE_K:
                return mKLevel;
            default:
                return 0;
        }
    }

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

        //TODO Remove these lines if/when you want to implement the upgrades again
        mMassLevel = 1;
        mRadiusLevel = 1;
        mKLevel = 1;
        mGraphic = 0;
        mPlow = false;

        double mass = massBase + (massMultiplier * mMassLevel);
        double radius = radiusBase + (radiusMultiplier * mRadiusLevel);
        double k = kBase + (kMultiplier * mKLevel);

        Flail f = new Flail(mass, radius, k, mGraphic);
        f.IsPlow = mPlow;

        return f;
    }

    public void Upgrade(int type){
        //TODO change 0 to 2 after if statements
        switch(type){
            case TYPE_MASS:
                mMassLevel ++;

                if(mMassLevel > 2) mMassLevel = 0;
                break;
            case TYPE_RADIUS:
                mRadiusLevel ++;

                if(mRadiusLevel > 2) mRadiusLevel = 0;
                break;
            case TYPE_K:
                mKLevel ++;

                if(mKLevel > 2) mKLevel = 0;
                break;
        }
    }

    public long GetUpgradeCost(int type){
        long GRAPHIC_MULTIPLIER = 2000;
        long LEVEL_MULTIPLIER = 1000;

        long baseCost = (mGraphic + 1) * GRAPHIC_MULTIPLIER;

        long level = (GetLevel(type) + 1);

        long levelCost = level * level * LEVEL_MULTIPLIER;

        return baseCost + levelCost;
    }
}
