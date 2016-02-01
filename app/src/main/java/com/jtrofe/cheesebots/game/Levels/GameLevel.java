package com.jtrofe.cheesebots.game.Levels;

import com.jtrofe.cheesebots.game.physics.Vec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAIN on 1/24/16
 */
public abstract class GameLevel{
    public String InitialMessage = "";

    public int MaxBots = Integer.MAX_VALUE; // Don't set this to have infinite bots
    public int MaxBotsOnScreen = 10;

    public boolean HasTimeLimit = false;
    public int TimeLimit = -1; // In seconds


    public int CheeseCount = 1;

    public boolean HasRandomCheeseLocations = false;
    public Vec[] CheesePositions; // In percentage of the screen dimensions

    public boolean HasRandomCheeseSizes;
    public float[] CheeseSizes; // In percentage of the screen height

    public boolean FromJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            this.InitialMessage = jsonObject.getString("InitialMessage");

            this.MaxBots = jsonObject.getInt("MaxBots");
            this.MaxBotsOnScreen = jsonObject.getInt("MaxBotsOnScreen");

            this.HasTimeLimit = jsonObject.getBoolean("HasTimeLimit");
            this.TimeLimit = jsonObject.getInt("TimeLimit");

            this.CheeseCount = jsonObject.getInt("CheeseCount");

            this.HasRandomCheeseLocations = jsonObject.getBoolean("HasRandomCheeseLocations");
            JSONArray cheeseLocations = jsonObject.getJSONArray("CheesePositions");

            this.CheesePositions = new Vec[cheeseLocations.length()];

            for(int i=0;i<cheeseLocations.length();i++){
                JSONObject j = cheeseLocations.getJSONObject(i);
                float x = (float) j.getDouble("x");
                float y = (float) j.getDouble("y");

                this.CheesePositions[i] = new Vec(x, y);
            }

            this.HasRandomCheeseSizes = jsonObject.getBoolean("HasRandomCheeseSizes");
            JSONArray cheeseSizes = jsonObject.getJSONArray("CheeseSizes");

            this.CheeseSizes = new float[cheeseSizes.length()];

            for(int i=0;i<cheeseSizes.length();i++){
                float r = (float) cheeseSizes.getDouble(i);
                this.CheeseSizes[i] = r;
            }


            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
    }

    public String ToJSON() {
        String json = "{ 'InitialMessage':'%s', " +
                " 'MaxBots':%d, 'MaxBotsOnScreen':%d, 'HasTimeLimit':%s, 'TimeLimit':%d, " +
                " 'CheeseCount':%d, 'HasRandomCheeseLocations':%s, 'CheesePositions':[";

        for(Vec v:CheesePositions){
            json += "{ 'x':" + v.x + ", 'y':" + v.y + "},";
        }

        if(CheesePositions.length > 0)
            json = json.substring(0, json.length()-1);

        json += "], 'HasRandomCheeseSizes':%s, 'CheeseSizes':[";

        for(float f:CheeseSizes){
            json += f + ",";
        }

        if(CheeseSizes.length > 0)
            json = json.substring(0, json.length()-1);

        json += "]}";


        return String.format(json, InitialMessage, MaxBots, MaxBotsOnScreen, HasTimeLimit, TimeLimit,
                            CheeseCount, HasRandomCheeseLocations, HasRandomCheeseSizes);
    }
}
