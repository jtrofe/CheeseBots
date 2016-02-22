package com.jtrofe.cheesebots.game

import android.content.Context
import android.util.TypedValue
import com.jtrofe.cheesebots.GameApp
import com.jtrofe.cheesebots.R
import com.jtrofe.cheesebots.physics.objects.Flail
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by MAIN on 2/21/16.
 */
public class Storage(){

    companion object {
        public val PREFERENCES_NAME: String = "CHEESE_BOTS"

        public fun GetSelectedFlail(): Flail {
            val preferences = GameApp.App.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            val resources = GameApp.App.getResources()
            val packageName = GameApp.App.getPackageName()

            val userJSON = preferences.getString("user", resources.getString(R.string.user_default_json))

            var flail: Flail

            var FLAIL_MASS: Double
            var FLAIL_RADIUS: Double
            var FLAIL_K: Double


            try {
                val userObject = JSONObject(userJSON)

                val selectedFlail = userObject.getInt("selectedFlail")

                val flailList = userObject.getJSONArray("flails")

                val flailObject = flailList.getJSONObject(selectedFlail)

                FLAIL_MASS = flailObject.getDouble("mass")
                FLAIL_RADIUS = flailObject.getDouble("radius")
                FLAIL_K = flailObject.getDouble("k")

            } catch(e: Exception) {
                e.printStackTrace()

                val default_mass = resources.getString(R.string.flail_default_mass)
                val default_radius = resources.getString(R.string.flail_default_radius)
                val default_k = resources.getString(R.string.flail_default_k)

                val id_mass = resources.getIdentifier("flail_mass_" + default_mass, "raw", packageName)
                val id_radius = resources.getIdentifier("flail_radius_" + default_radius, "raw", packageName)
                val id_k = resources.getIdentifier("flail_k_" + default_k, "raw", packageName)

                var out = TypedValue()

                resources.getValue(id_mass, out, true)
                FLAIL_MASS = out.getFloat().toDouble()

                resources.getValue(id_radius, out, true)
                FLAIL_RADIUS = out.getFloat().toDouble()

                resources.getValue(id_k, out, true)
                FLAIL_K = out.getFloat().toDouble()
            }
            flail = Flail(FLAIL_MASS, FLAIL_RADIUS, FLAIL_K)

            return flail
        }
    }
}