package com.jtrofe.cheesebots.game.UserData;

import android.content.res.Resources;
import android.util.TypedValue;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.physics.Vec;
import com.jtrofe.cheesebots.physics.objects.Cheese;
import com.jtrofe.cheesebots.physics.objects.Flail;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAIN on 3/13/16
 */
public class UserCheese{

    private int mGraphicIndex = 0;
    private int mRadiusLevel = 0;
    private int mDensityLevel = 0;

    public int GetGraphicIndex(){ return mGraphicIndex; }
    public int GetRadiusLevel(){ return mRadiusLevel; }
    public int GetDensityLevel(){ return mDensityLevel; }

    public String ToJSON(){
        return "{ \"graphicIndex\" : \"" + mGraphicIndex + "\", " +
                "\"radiusLevel\" : " + mRadiusLevel + ", " +
                "\"densityLevel\" : " + mDensityLevel + "}";
    }

    public void FromJSON(JSONObject object) throws JSONException {
        mGraphicIndex = object.getInt("graphicIndex");
        mRadiusLevel = object.getInt("radiusLevel");
        mDensityLevel = object.getInt("densityLevel");
    }

    public void LoadDefault(){
        mGraphicIndex = 0;
        mRadiusLevel = 0;
        mDensityLevel = 0;
    }

    public Cheese GetCheese(){
        Resources resources = GameApp.App.getResources();

        TypedValue out = new TypedValue();

        resources.getValue(R.raw.cheese_radius_base, out, true);
        double radiusBase = (double) out.getFloat();
        resources.getValue(R.raw.cheese_radius_multiplier, out, true);
        double radiusMultiplier = (double) out.getFloat();

        resources.getValue(R.raw.cheese_density_base, out, true);
        double densityBase = (double) out.getFloat();
        resources.getValue(R.raw.cheese_density_multiplier, out, true);
        double densityMultiplier = (double) out.getFloat();

        double radius = radiusBase + (mRadiusLevel * radiusMultiplier);
        double density = densityBase + (mDensityLevel * densityMultiplier);

        // Calculate the amount (mass) in the cheese based on area and density
        double area = Math.PI * radius * radius;
        double mass = density * area;

        return new Cheese(mGraphicIndex, radius, mass);
    }

    public void UpgradeRadius(){
        mRadiusLevel ++;

        //TODO change 0 to 2
        if(mRadiusLevel > 2) mRadiusLevel = 0;
    }

    public void UpgradeDensity(){
        mDensityLevel ++;

        //TODO change 0 to 2
        if(mDensityLevel > 2) mDensityLevel = 0;
    }
}
