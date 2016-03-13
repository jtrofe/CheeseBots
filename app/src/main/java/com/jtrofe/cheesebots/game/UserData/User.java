package com.jtrofe.cheesebots.game.UserData;

import android.text.TextUtils;
import android.util.Log;

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

    private int mScrap = 0;

    private List<UserFlail> mFlails = new ArrayList<>();

    private int mSelectedFlail = 0;

    public int GetSelectedFlailIndex(){
        return mSelectedFlail;
    }

    public int GetScrap(){
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

            Log.i("Storage", "User loaded successfully");
        }catch(JSONException e){
            mName = "Kilroy";
            mScrap = 0;
            mSelectedFlail = 0;

            UserFlail f = new UserFlail();
            f.LoadDefault();

            mFlails.add(f);

            Log.i("Storage", "Default user loaded");
        }
    }

    public String ToJSON(){
        String json = "{ \"name\" : \"" + mName + "\", \"scrap\" : \"" + mScrap + "\", \"flails\" : ";

        String[] flailJSON = new String[mFlails.size()];

        for(int i=0;i<mFlails.size();i++){
            flailJSON[i] = mFlails.get(i).ToJSON();
        }

        json += "[ " + TextUtils.join(",", flailJSON) + "], \"selectedFlail\" : " + mSelectedFlail + " }";

        return json;
    }

    public void FromJSON(JSONObject object) throws JSONException{
        mName = object.getString("name");
        mScrap = object.getInt("scrap");

        mFlails = new ArrayList<>();
        JSONArray flails = object.getJSONArray("flails");

        for(int i=0;i<flails.length();i++){
            UserFlail f = new UserFlail();
            f.FromJSON(flails.getJSONObject(i));

            mFlails.add(f);
        }

        mSelectedFlail = object.getInt("selectedFlail");
    }

    public List<UserFlail> GetAllFlails(){
        return mFlails;
    }

    public Flail GetSelectedFlail(){
        return mFlails.get(mSelectedFlail).GetFlail();
    }
}
