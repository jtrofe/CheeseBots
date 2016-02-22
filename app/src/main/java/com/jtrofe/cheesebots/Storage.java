package com.jtrofe.cheesebots;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

import com.jtrofe.cheesebots.game.gameobjects.Flail;
import com.jtrofe.cheesebots.game.physics.Engine;
import com.jtrofe.cheesebots.game.physics.Vec;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAIN on 2/1/16
 */
public class Storage{

    private static final String PREFERENCES_NAME = "CheeseBots";

    /**
     * Flail option resource names
     */
    private static final String FLAIL_SIZE_SMALL = "flail_small_radius";
    private static final String FLAIL_SIZE_MEDIUM = "flail_medium_radius";
    private static final String FLAIL_SIZE_LARGE = "flail_large_radius";

    private static final String FLAIL_K_SMALL = "flail_small_k";
    private static final String FLAIL_K_MEDIUM = "flail_medium_k";
    private static final String FLAIL_K_LARGE = "flail_large_k";

    private static final String FLAIL_MASS_SMALL = "flail_small_mass";
    private static final String FLAIL_MASS_MEDIUM = "flail_medium_mass";
    private static final String FLAIL_MASS_LARGE = "flail_large_mass";

    public static void LoadFlail(Engine engine){
        SharedPreferences sharedPreferences = GameApp.App.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        Resources resources = GameApp.App.getResources();

        String userJSON = sharedPreferences.getString("user",
                resources.getString(R.string.user_default_json));

        try {
            JSONObject json = new JSONObject(userJSON);

            JSONObject flail1 = json.getJSONObject("flail1");

            if(flail1.toString().equals("{}")){
                flail1 = new JSONObject(resources.getString(R.string.flail_default_json));
            }


            Flail flail = loadFlail(flail1, sharedPreferences, engine);
            //System.out.println(flail1.toString());


            engine.AddBody(flail);

        }catch(JSONException e){
            e.printStackTrace();
        }


       // Flail flail = new Flail(new Vec(100, 100), null, 20, 0.6f, 50);
        //Flail flail = loadFlailNumber(0, sharedPreferences);
       // engine.AddBody(flail);
    }

    /**
     * User data:
     * user: {
     *     scrap: int
     *
     *     flails [ flail, flail, flail... ]
     *     selectedFlail: int
     * }
     */
    /**
     * How flail data is stored:
     * flail: {
     *     frame: 0-3
     *     size: large/medium/small
     *     mass: heavy/medium/light
     *     damage upgrade level: 0-5
     *     plow: true/false
     *     hasRope: true/false
     * }
     */
    private static Flail loadFlail(JSONObject json,
                                   SharedPreferences sharedPreferences, Engine engine) throws JSONException{
        Resources resources = GameApp.App.getResources();
        String packageName = GameApp.App.getPackageName();
        String size = json.getString("size");
        String weight = json.getString("mass");
        boolean plow = json.getBoolean("plow");
        boolean hasRope = json.getBoolean("hasRope");
        int damage = json.getInt("damage");
        int frame = json.getInt("frame");

        int size_id = resources.getIdentifier("flail_size_" + size, "raw", packageName);
        int mass_id = resources.getIdentifier("flail_mass_" + weight, "raw", packageName);

        TypedValue out = new TypedValue();

        resources.getValue(size_id, out, true);
        float radius = out.getFloat();

        resources.getValue(mass_id, out, true);
        float mass = out.getFloat();

        Flail flail = new Flail(new Vec(100, 100), null, mass, 0.6f, radius);
        flail.PlowThrough = plow;

        // TODO make this be based on hasRope instead of Application's UseRope
        if(GameApp.UseRope){//hasRope){
            flail.CreateRope(engine);
        }

        return flail;
    }

    private static Flail loadFlailNumber(int n, SharedPreferences sharedPreferences){
        Resources resources = GameApp.App.getResources();
        String packageName = GameApp.App.getPackageName();

        String flail_size = sharedPreferences.getString("flail_" + n + "_size", FLAIL_SIZE_LARGE);
        String flail_k = sharedPreferences.getString("flail_" + n + "_k", FLAIL_K_LARGE);
        String flail_mass = sharedPreferences.getString("flail_" + n + "_mass", FLAIL_MASS_SMALL);
        boolean flail_is_plow = sharedPreferences.getBoolean("flail_" + n + "_is_plow", true);

        int size_id = resources.getIdentifier(flail_size, "raw", packageName);
        int k_id = resources.getIdentifier(flail_k, "raw", packageName);
        int mass_id = resources.getIdentifier(flail_mass, "raw", packageName);


        TypedValue out = new TypedValue();

        resources.getValue(size_id, out, true);
        float radius = out.getFloat();

        resources.getValue(k_id, out, true);
        float k = out.getFloat();

        resources.getValue(mass_id, out, true);
        float mass = out.getFloat();


        Flail flail = new Flail(new Vec(100, 100), null, mass, k, radius);
        flail.PlowThrough = flail_is_plow;

        return flail;
    }
}
