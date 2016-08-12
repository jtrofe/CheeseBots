package com.jtrofe.cheesebots.game.UserData;

import android.text.TextUtils;
import android.util.Log;

import com.jtrofe.cheesebots.physics.objects.Cheese;
import com.jtrofe.cheesebots.physics.objects.Flail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAIN on 3/1/16
 */
public class User{

    private String mName = "Kilroy";

    private long mScrap = 0;

    private List<UserFlail> mFlails = new ArrayList<>();
    private List<UserCheese> mCheeses = new ArrayList<>();

    private int mSelectedFlail = 0;
    private int[] mSelectedCheeses = new int[]{0, -1, -1};

    public String GetName(){
        return mName;
    }

    public void SetName(String name){
        mName = name;

        Storage.SaveUser();
    }

    public int GetSelectedFlailIndex(){
        return mSelectedFlail;
    }

    public long GetScrap(){
        return mScrap;
    }

    public void SetSelectedFlailIndex(int index){
        if(index < 0) index = 0;
        if(index > mFlails.size() - 1) index = mFlails.size() - 1;
        mSelectedFlail = index;
    }

    public User(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);

            FromJSON(jsonObject);

            // TODO REMOVE THIS
            mScrap += 10000;

            Log.i("Storage", "User loaded successfully");
        }catch(JSONException e){
            mName = "Kilroy";
            mScrap = 0;
            mSelectedFlail = 0;

            UserFlail f = new UserFlail();
            f.LoadDefault();
            mFlails.add(f);

            UserCheese c = new UserCheese();
            c.LoadDefault();
            mCheeses.add(c);

            Log.i("Storage", "Default user loaded");
        }
    }

    public String ToJSON(){
        String json = "{ \"name\" : \"" + mName + "\", \"scrap\" : \"" + mScrap + "\", \"flails\" : ";

        String[] flailJSON = new String[mFlails.size()];

        for(int i=0;i<mFlails.size();i++){
            flailJSON[i] = mFlails.get(i).ToJSON();
        }

        json += "[ " + TextUtils.join(",", flailJSON) + "], \"selectedFlail\" : " + mSelectedFlail + ", ";

        String[] cheeseJSON = new String[mCheeses.size()];

        for(int i=0;i<mCheeses.size();i++){
            cheeseJSON[i] = mCheeses.get(i).ToJSON();
        }

        json += "\"cheeses\" : [ " + TextUtils.join(",", cheeseJSON) + "], ";

        json += "\"selectedCheeses\" : [ " + mSelectedCheeses[0] + ", " +
                    mSelectedCheeses[1] + ", " + mSelectedCheeses[2] + " ] } ";
        return json;
    }

    public void FromJSON(JSONObject object) throws JSONException{
        mName = object.getString("name");
        mScrap = object.getLong("scrap");

        mFlails = new ArrayList<>();
        JSONArray flails = object.getJSONArray("flails");
        for(int i=0;i<flails.length();i++){
            UserFlail f = new UserFlail();
            f.FromJSON(flails.getJSONObject(i));

            mFlails.add(f);
        }

        mCheeses = new ArrayList<>();
        JSONArray cheeses = object.getJSONArray("cheeses");
        for(int i=0;i<cheeses.length();i++){
            UserCheese c = new UserCheese();
            c.FromJSON(cheeses.getJSONObject(i));

            mCheeses.add(c);
        }

        mSelectedFlail = object.getInt("selectedFlail");

        mSelectedCheeses = new int[3];
        JSONArray selectedCheeses = object.getJSONArray("selectedCheeses");

        for(int i=0;i<selectedCheeses.length();i++){
            mSelectedCheeses[i] = selectedCheeses.getInt(i);
        }
    }

    public List<UserCheese> GetAllCheeses(){
        return mCheeses;
    }

    public List<Cheese> GetSelectedCheeses(){
        List<Cheese> cheeses = new ArrayList<>();

        for(int i=0;i<mSelectedCheeses.length;i++){
            int cheeseIndex = mSelectedCheeses[i];
            if(cheeseIndex != -1){
                UserCheese us = mCheeses.get(cheeseIndex);

                cheeses.add(us.GetCheese());
            }
        }

        if(cheeses.size() == 0){
            UserCheese us = new UserCheese();
            us.LoadDefault();
            cheeses.add(us.GetCheese());
        }

        return cheeses;
    }

    public List<UserFlail> GetAllFlails(){
        return mFlails;
    }

    public Flail GetSelectedFlail(){
        return mFlails.get(mSelectedFlail).GetFlail();
    }

    public void AddScrap(long scrap){
        mScrap += scrap;
    }

    public boolean CanBuy(long scrapCost){
        return scrapCost <= mScrap;
    }

    public void Buy(long scrapCost){
        mScrap -= scrapCost;

        Storage.SaveUser();
    }
}
