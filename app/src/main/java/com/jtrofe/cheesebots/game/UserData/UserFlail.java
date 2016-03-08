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
    private String mRadius;
    private String mMass;
    private String mK;
    private Boolean mPlow;

    public String ToJSON(){
        return "{ \"graphic\" : \"" + mGraphic + "\", " +
                "\"radius\" : \"" + mRadius + "\", " +
                "\"mass\" : \"" + mMass + "\", " +
                "\"k\" : \"" + mK + "\", " +
                "\"plow\" : " + mPlow + "}";
    }

    public void FromJSON(JSONObject object) throws JSONException{
        mGraphic = object.getInt("graphic");
        mRadius = object.getString("radius");
        mMass = object.getString("mass");
        mK = object.getString("k");
        mPlow = object.getBoolean("plow");
    }

    public void LoadDefault(){
        Resources resources = GameApp.App.getResources();

        mGraphic = 0;
        mRadius = resources.getString(R.string.flail_default_radius);
        mMass = resources.getString(R.string.flail_default_mass);
        mK = resources.getString(R.string.flail_default_k);
        mPlow = false;
    }

    public Flail GetFlail(){
        Resources resources = GameApp.App.getResources();
        String packageName = GameApp.App.getPackageName();

        int id_mass = resources.getIdentifier("flail_mass_" + mMass, "raw", packageName);
        int id_radius = resources.getIdentifier("flail_radius_" + mRadius, "raw", packageName);
        int id_k = resources.getIdentifier("flail_k_" + mK, "raw", packageName);

        TypedValue out = new TypedValue();

        resources.getValue(id_mass, out, true);
        double FLAIL_MASS = (double) out.getFloat();

        resources.getValue(id_radius, out, true);
        double FLAIL_RADIUS = (double) out.getFloat();

        resources.getValue(id_k, out, true);
        double FLAIL_K = (double) out.getFloat();

        Flail f = new Flail(FLAIL_MASS, FLAIL_RADIUS, FLAIL_K);
        f.setIsPlow(mPlow);

        return f;
    }
}
